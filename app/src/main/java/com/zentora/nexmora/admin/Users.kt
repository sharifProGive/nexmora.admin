package com.zentora.nexmora.admin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder

fun JSONObject.str(key: String): String = optString(key, "")

fun JSONObject.lng(key: String): Long = optLong(key, 0L)

fun JSONArray?.objects(): List<JSONObject> {
    if (this == null) return emptyList()
    val out = ArrayList<JSONObject>(length())
    for (i in 0 until length()) {
        val item = optJSONObject(i)
        if (item != null) out.add(item)
    }
    return out
}

class UserRow(
    val nexoraId: String,
    val username: String,
    val handle: String,
    val email: String,
    val createdAt: Long,
    val lastSeenAt: Long,
    val banned: Boolean
) {
    val title: String
        get() = username.ifBlank { handle.ifBlank { nexoraId } }
}

private fun parseRow(j: JSONObject): UserRow = UserRow(
    nexoraId = j.str("nexoraId"),
    username = j.str("username"),
    handle = j.str("handle"),
    email = j.str("email"),
    createdAt = j.lng("createdAt"),
    lastSeenAt = j.lng("lastSeenAt"),
    banned = j.str("status") == "banned"
)

class Profile(val root: JSONObject) {
    val user: JSONObject = root.optJSONObject("user") ?: JSONObject()
    val status: JSONObject = root.optJSONObject("status") ?: JSONObject()
    val stats: JSONObject = root.optJSONObject("stats") ?: JSONObject()
    val videos: List<JSONObject> = root.optJSONArray("videos").objects()
    val comments: List<JSONObject> = root.optJSONArray("comments").objects()
    val friends: List<JSONObject> = root.optJSONArray("friends").objects()
    val calls: List<JSONObject> = root.optJSONArray("calls").objects()
    val banned: Boolean get() = status.str("state") == "banned"
}

/** Everything the Users and Control tabs show. It lives in the view model, so it survives tab changes, and is wiped on lock. */
class UsersModel(private val vm: AppVm) {
    /** Archive, bans, history, ledger, vaults and the dangerous actions. */
    val control = ControlModel(vm)

    var query by mutableStateOf("")

    var rows by mutableStateOf<List<UserRow>>(emptyList())
        private set
    var next by mutableStateOf<Long?>(null)
        private set
    var loading by mutableStateOf(false)
        private set
    var loaded by mutableStateOf(false)
        private set
    var error by mutableStateOf<Tx?>(null)
        private set

    var selected by mutableStateOf<String?>(null)
        private set
    var profile by mutableStateOf<Profile?>(null)
        private set
    var profileLoading by mutableStateOf(false)
        private set
    var profileError by mutableStateOf<Tx?>(null)
        private set

    var privateShown by mutableStateOf(false)
        private set
    var privateLoading by mutableStateOf(false)
        private set
    var privateError by mutableStateOf<Tx?>(null)
        private set
    var phone by mutableStateOf("")
        private set
    var billing by mutableStateOf("")
        private set

    private var activeQuery = ""

    fun loadFirst() {
        fetch(true)
    }

    fun loadMore() {
        if (next != null) fetch(false)
    }

    private fun fetch(reset: Boolean) {
        if (loading) return
        loading = true
        error = null
        val q = if (reset) query.trim() else activeQuery
        val before = if (reset) null else next
        vm.viewModelScope.launch {
            try {
                val path = buildString {
                    append("/api/v1/admin/users?limit=30")
                    if (q.isNotEmpty()) append("&q=").append(URLEncoder.encode(q, "UTF-8"))
                    if (before != null) append("&before=").append(before)
                }
                val json = vm.secured { it.get(path) }
                val list = json.optJSONArray("users").objects().map { parseRow(it) }
                if (reset) {
                    rows = list
                } else {
                    val seen = rows.map { it.nexoraId }.toHashSet()
                    rows = rows + list.filter { seen.add(it.nexoraId) }
                }
                next = if (json.isNull("next")) null else json.optLong("next")
                activeQuery = q
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

    fun openProfile(id: String) {
        hidePrivate()
        selected = id
        profile = null
        profileError = null
        profileLoading = true
        vm.viewModelScope.launch {
            try {
                val json = vm.secured { it.get("/api/v1/admin/users/$id") }
                if (selected == id) profile = Profile(json)
            } catch (e: CancellationException) {
                throw e
            } catch (e: ApiException) {
                if (selected == id) profileError = vm.failure(e)
            } catch (e: Throwable) {
                if (selected == id) profileError = S.errUnknown
            } finally {
                if (selected == id) profileLoading = false
            }
        }
    }

    fun reloadProfile() {
        val id = selected ?: return
        openProfile(id)
    }

    fun closeProfile() {
        hidePrivate()
        selected = null
        profile = null
        profileError = null
        profileLoading = false
    }

    fun revealPrivate() {
        val id = selected ?: return
        if (privateLoading) return
        privateLoading = true
        privateError = null
        vm.viewModelScope.launch {
            try {
                val json = vm.secured { it.post("/api/v1/admin/users/$id/private", JSONObject()) }
                if (selected == id) {
                    phone = json.str("phoneNumber")
                    billing = json.str("billingInfo")
                    privateShown = true
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: ApiException) {
                if (selected == id) privateError = vm.failure(e)
            } catch (e: Throwable) {
                if (selected == id) privateError = S.errUnknown
            } finally {
                privateLoading = false
            }
        }
    }

    fun hidePrivate() {
        privateShown = false
        privateLoading = false
        privateError = null
        phone = ""
        billing = ""
    }

    fun clear() {
        query = ""
        rows = emptyList()
        next = null
        loading = false
        loaded = false
        error = null
        activeQuery = ""
        closeProfile()
        control.clear()
    }
}
