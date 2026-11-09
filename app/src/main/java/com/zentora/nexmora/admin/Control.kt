package com.zentora.nexmora.admin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import org.json.JSONObject

/** One paged list from the server (30 rows at a time, newest first). */
class Pager<T>(
    private val vm: AppVm,
    private val path: String,
    private val listKey: String,
    private val parse: (JSONObject) -> T
) {
    var items by mutableStateOf<List<T>>(emptyList())
        private set
    var next by mutableStateOf<Long?>(null)
        private set
    var loading by mutableStateOf(false)
        private set
    var loaded by mutableStateOf(false)
        private set
    var error by mutableStateOf<Tx?>(null)
        private set

    fun reload() {
        fetch(true)
    }

    fun more() {
        if (next != null) fetch(false)
    }

    private fun fetch(reset: Boolean) {
        if (loading) return
        loading = true
        error = null
        val before = if (reset) null else next
        vm.viewModelScope.launch {
            try {
                val separator = if (path.contains('?')) "&" else "?"
                val full = path + separator + "limit=30" + (if (before != null) "&before=$before" else "")
                val json = vm.secured { it.get(full) }
                val list = json.optJSONArray(listKey).objects().map(parse)
                items = if (reset) list else items + list
                next = if (json.isNull("next")) null else json.optLong("next")
                loaded = true
            } catch (e: CancellationException) {
                throw e
            } catch (e: ApiException) {
                error = vm.failure(e)
            } catch (e: Throwable) {
                error = S.errUnknown
            } finally {
                loading = false
            }
        }
    }

    fun clear() {
        items = emptyList()
        next = null
        loading = false
        loaded = false
        error = null
    }
}

/** Archive, bans, history, ledger, vaults, and the dangerous actions. Wiped when the app locks. */
class ControlModel(private val vm: AppVm) {
    val archive = Pager<JSONObject>(vm, "/api/v1/admin/archive", "entries") { it }
    val bans = Pager<JSONObject>(vm, "/api/v1/admin/bans", "bans") { it }
    val actions = Pager<JSONObject>(vm, "/api/v1/admin/actions", "actions") { it }
    val ledger = Pager<JSONObject>(vm, "/api/v1/admin/ledger", "blocks") { it }
    val vaults = Pager<JSONObject>(vm, "/api/v1/admin/vaults", "vaults") { it }

    var actionBusy by mutableStateOf(false)
        private set
    var actionError by mutableStateOf<Tx?>(null)
        private set

    /** 0 not checked, 1 running, 2 chain intact, 3 chain broken */
    var verifyState by mutableStateOf(0)
        private set
    var verifyError by mutableStateOf<Tx?>(null)
        private set

    fun clearActionError() {
        actionError = null
    }

    fun ban(id: String, reason: String, password: String, done: () -> Unit) {
        act(
            "/api/v1/admin/users/$id/ban",
            JSONObject().put("reason", reason).put("deletePassword", password),
            done
        )
    }

    fun unban(id: String, password: String, done: () -> Unit) {
        act("/api/v1/admin/users/$id/unban", JSONObject().put("deletePassword", password), done)
    }

    fun delete(id: String, reason: String, password: String, done: () -> Unit) {
        act(
            "/api/v1/admin/users/$id/delete",
            JSONObject().put("reason", reason).put("deletePassword", password),
            done
        )
    }

    fun restore(archiveId: Long, password: String, done: () -> Unit) {
        act("/api/v1/admin/archive/$archiveId/restore", JSONObject().put("deletePassword", password), done)
    }

    private fun actionText(e: ApiException): Tx? {
        if (e.kind == Kind.SERVER) {
            when (e.error) {
                "wrong_password" -> return C.wrongPassword
                "already_banned" -> return C.alreadyBanned
                "not_banned" -> return C.notBanned
                "not_found" -> return C.notFound
                "too_large" -> return C.tooLarge
                "already_restored" -> return C.alreadyRestored
                "exists" -> return C.exists
            }
        }
        return vm.failure(e)
    }

    private fun act(path: String, body: JSONObject, done: () -> Unit) {
        if (actionBusy) return
        actionBusy = true
        actionError = null
        vm.viewModelScope.launch {
            try {
                vm.secured { it.post(path, body) }
                done()
            } catch (e: CancellationException) {
                throw e
            } catch (e: ApiException) {
                actionError = actionText(e)
            } catch (e: Throwable) {
                actionError = S.errUnknown
            } finally {
                actionBusy = false
            }
        }
    }

    fun verifyLedger() {
        if (verifyState == 1) return
        verifyState = 1
        verifyError = null
        vm.viewModelScope.launch {
            try {
                val json = vm.secured { it.get("/api/v1/admin/ledger/verify") }
                verifyState = if (json.optBoolean("chainValid", false)) 2 else 3
            } catch (e: CancellationException) {
                throw e
            } catch (e: ApiException) {
                verifyState = 0
                verifyError = vm.failure(e)
            } catch (e: Throwable) {
                verifyState = 0
                verifyError = S.errUnknown
            }
        }
    }

    fun clear() {
        archive.clear()
        bans.clear()
        actions.clear()
        ledger.clear()
        vaults.clear()
        actionBusy = false
        actionError = null
        verifyState = 0
        verifyError = null
    }
}
