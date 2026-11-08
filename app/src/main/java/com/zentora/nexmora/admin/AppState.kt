package com.zentora.nexmora.admin

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import org.json.JSONObject

sealed interface Screen {
    data object Connect : Screen
    data object Secure : Screen
    data class Recovery(val code: String) : Screen
    data object Locked : Screen
    data object Rebind : Screen
    data object Home : Screen
}

class Overview(val json: JSONObject, val loadedAt: Long) {
    fun long(name: String): Long = json.optLong(name, 0L)
    fun server(name: String): Long = json.optJSONObject("server")?.optLong(name, 0L) ?: 0L
    fun serverText(name: String): String = json.optJSONObject("server")?.optString(name, "") ?: ""
}

fun errorText(e: Throwable): Tx = when (e) {
    is ApiException -> when (e.kind) {
        Kind.NETWORK -> S.errNetwork
        Kind.AUTH_NEEDED -> S.errAuth
        Kind.KEY_INVALID -> S.errKeyInvalid
        Kind.BAD_URL -> S.errHttps
        Kind.SERVER -> when (e.error) {
            "unauthorized" -> S.errToken
            "locked", "password_locked", "recovery_locked" -> S.errLocked
            "wrong_recovery_code" -> S.errWrongCode
            "already_registered" -> S.errAlready
            "not_registered" -> S.errNotRegistered
            "stale_request", "bad_signature", "replayed_request" -> S.errSignature
            else -> S.errServer
        }
    }
    else -> S.errUnknown
}

class AppVm(app: Application) : AndroidViewModel(app) {
    private val store = SecureStore(app)

    var screen by mutableStateOf<Screen>(Screen.Connect)
        private set
    var lang by mutableStateOf(store.lang())
        private set
    var screenProtect by mutableStateOf(store.screenProtect())
        private set
    var busy by mutableStateOf(false)
        private set
    var error by mutableStateOf<Tx?>(null)
        private set
    var overview by mutableStateOf<Overview?>(null)
        private set
    var loading by mutableStateOf(false)
        private set
    var loadError by mutableStateOf<Tx?>(null)
        private set

    /** Set by MainActivity: shows the fingerprint dialog. */
    var prompter: (suspend (String, String, String) -> AuthResult)? = null

    private var api: AdminApi? = null
    private var pendingUrl = ""
    private var pendingToken = ""

    init {
        val url = store.getSecret("url")
        val token = store.getSecret("token")
        val alias = store.keyAlias()
        if (store.registered() && url != null && token != null) {
            pendingUrl = url
            pendingToken = token
            if (DeviceKey.exists(alias)) {
                api = AdminApi(url, token, alias)
                screen = Screen.Locked
            } else {
                error = S.errKeyInvalid
                screen = Screen.Rebind
            }
        } else {
            screen = Screen.Connect
        }
    }

    fun clearError() {
        error = null
    }

    @JvmName("changeLanguage")
    fun setLang(value: Lang) {
        lang = value
        store.setLang(value)
    }

    @JvmName("changeScreenProtect")
    fun setScreenProtect(on: Boolean) {
        screenProtect = on
        store.setScreenProtect(on)
    }

    fun serverHost(): String = try {
        java.net.URL(store.getSecret("url") ?: "").host ?: ""
    } catch (e: Exception) {
        ""
    }

    private fun launchBusy(block: suspend () -> Unit) {
        if (busy) return
        busy = true
        error = null
        viewModelScope.launch {
            try {
                block()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                error = errorText(e)
            } finally {
                busy = false
            }
        }
    }

    private suspend fun askFinger() {
        val prompt = prompter ?: throw ApiException(Kind.AUTH_NEEDED, "no_prompter")
        when (prompt(S.promptTitle.get(lang), S.promptSub.get(lang), S.promptCancel.get(lang))) {
            AuthResult.Ok -> Unit
            AuthResult.Cancelled -> throw ApiException(Kind.AUTH_NEEDED, "cancelled")
            is AuthResult.Failed -> throw ApiException(Kind.AUTH_NEEDED, "failed")
        }
    }

    private suspend fun <T> secured(block: suspend (AdminApi) -> T): T {
        val current = api ?: throw ApiException(Kind.KEY_INVALID, "no_api")
        try {
            return block(current)
        } catch (e: ApiException) {
            if (e.kind != Kind.AUTH_NEEDED) throw e
        }
        askFinger()
        return block(current)
    }

    // ---------------------------------------------------------------- first run

    fun connect(urlRaw: String, tokenRaw: String) {
        val url = Net.normalizeUrl(urlRaw)
        val token = tokenRaw.trim()
        if (url == null) {
            error = S.errHttps
            return
        }
        if (token.length < 32) {
            error = S.errToken
            return
        }
        launchBusy {
            val state = AdminApi(url, token, "").state()
            pendingUrl = url
            pendingToken = token
            val registered = state.optBoolean("registered", false)
            val allowed = state.optBoolean("setupAllowed", false)
            screen = if (!registered || allowed) Screen.Secure else Screen.Rebind
        }
    }

    fun backToConnect() {
        error = null
        screen = Screen.Connect
    }

    fun finishSetup(password: String, repeat: String) {
        if (password.length < 8) {
            error = S.passwordShort
            return
        }
        if (password != repeat) {
            error = S.passwordMismatch
            return
        }
        if (!KeyGate.available(getApplication<Application>())) {
            error = S.noBiometric
            return
        }
        launchBusy {
            val alias = DeviceKey.ALIAS_A
            val publicKey = DeviceKey.create(alias)
            try {
                askFinger()
                val result = AdminApi(pendingUrl, pendingToken, alias).setup(publicKey, password)
                val code = result.optString("recoveryCode", "")
                if (code.length != 25) throw ApiException(Kind.SERVER, "bad_response", 200)
                DeviceKey.delete(DeviceKey.ALIAS_B)
                store.putSecret("url", pendingUrl)
                store.putSecret("token", pendingToken)
                store.setKeyAlias(alias)
                store.setRegistered(true)
                api = AdminApi(pendingUrl, pendingToken, alias)
                screen = Screen.Recovery(code)
            } catch (e: Throwable) {
                DeviceKey.delete(alias)
                throw e
            }
        }
    }

    fun recoveryDone() {
        screen = Screen.Home
        refresh()
    }

    // ---------------------------------------------------------------- lock

    fun unlock() {
        launchBusy {
            askFinger()
            screen = Screen.Home
            refresh()
        }
    }

    fun autoUnlock() {
        if (screen == Screen.Locked && !busy) unlock()
    }

    fun lock() {
        if (screen == Screen.Home) {
            overview = null
            loadError = null
            screen = Screen.Locked
        }
    }

    fun goRebind() {
        pendingUrl = store.getSecret("url") ?: pendingUrl
        pendingToken = store.getSecret("token") ?: pendingToken
        error = null
        screen = Screen.Rebind
    }

    fun backFromRebind() {
        error = null
        screen = if (store.registered() && DeviceKey.exists(store.keyAlias())) Screen.Locked else Screen.Connect
    }

    fun rebind(codeRaw: String) {
        val code = codeRaw.trim()
        if (!Regex("^\\d{25}$").matches(code)) {
            error = S.codeInvalid
            return
        }
        if (!KeyGate.available(getApplication<Application>())) {
            error = S.noBiometric
            return
        }
        launchBusy {
            val old = store.keyAlias()
            val alias = DeviceKey.other(old)
            val publicKey = DeviceKey.create(alias)
            try {
                askFinger()
                val result = AdminApi(pendingUrl, pendingToken, alias).rebind(publicKey, code)
                val fresh = result.optString("recoveryCode", "")
                if (fresh.length != 25) throw ApiException(Kind.SERVER, "bad_response", 200)
                DeviceKey.delete(old)
                store.putSecret("url", pendingUrl)
                store.putSecret("token", pendingToken)
                store.setKeyAlias(alias)
                store.setRegistered(true)
                api = AdminApi(pendingUrl, pendingToken, alias)
                screen = Screen.Recovery(fresh)
            } catch (e: Throwable) {
                DeviceKey.delete(alias)
                throw e
            }
        }
    }

    // ---------------------------------------------------------------- data

    fun refresh() {
        if (loading) return
        loading = true
        loadError = null
        viewModelScope.launch {
            try {
                val json = secured { it.overview() }
                overview = Overview(json, System.currentTimeMillis())
            } catch (e: CancellationException) {
                throw e
            } catch (e: ApiException) {
                if (e.kind == Kind.KEY_INVALID) {
                    goRebind()
                    error = S.errKeyInvalid
                } else {
                    loadError = errorText(e)
                }
            } catch (e: Throwable) {
                loadError = S.errUnknown
            } finally {
                loading = false
            }
        }
    }
}
