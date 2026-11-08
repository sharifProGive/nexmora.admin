package com.zentora.nexmora.admin

import android.util.Base64
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import java.security.SecureRandom
import kotlin.random.Random

enum class Kind { NETWORK, AUTH_NEEDED, KEY_INVALID, SERVER, BAD_URL }

class ApiException(
    val kind: Kind,
    val error: String,
    val status: Int = 0,
    val retryAfter: Int = 0
) : Exception("$kind/$error/$status")

object Net {
    // Accepts "host" or "https://host", returns "https://host" without a trailing slash, or null when invalid.
    fun normalizeUrl(raw: String): String? {
        var s = raw.trim().trimEnd('/')
        if (s.isEmpty()) return null
        if (!s.contains("://")) s = "https://$s"
        if (!s.startsWith("https://", ignoreCase = true)) return null
        val host = try {
            URL(s).host
        } catch (e: Exception) {
            return null
        }
        if (host.isNullOrEmpty() || !host.contains('.')) return null
        return s
    }
}

// Talks to the admin endpoints of the server. Every request carries the Bearer Admin Token; every request
// except state, setup and rebind is also signed by the phone's fingerprint-protected key (METHOD, URL, time,
// nonce, SHA-256 of the body). A sleeping Render server (it answers 502, 503 or 504 while waking, which can
// take about a minute) and network drops are retried with exponential backoff and jitter, capped at 8 seconds.
class AdminApi(private val baseUrl: String, private val token: String, private val keyAlias: String) {
    private val random = SecureRandom()

    suspend fun state(): JSONObject = call("GET", "/api/v1/admin/state", null, false)

    suspend fun setup(devicePublicKey: String, deletePassword: String): JSONObject = call(
        "POST", "/api/v1/admin/setup",
        JSONObject().put("devicePublicKey", devicePublicKey).put("deletePassword", deletePassword).toString(),
        false
    )

    suspend fun rebind(devicePublicKey: String, recoveryCode: String): JSONObject = call(
        "POST", "/api/v1/admin/rebind",
        JSONObject().put("devicePublicKey", devicePublicKey).put("recoveryCode", recoveryCode).toString(),
        false
    )

    suspend fun overview(): JSONObject = call("GET", "/api/v1/admin/overview", null, true)

    suspend fun get(pathWithQuery: String): JSONObject = call("GET", pathWithQuery, null, true)

    suspend fun post(path: String, body: JSONObject): JSONObject = call("POST", path, body.toString(), true)

    private suspend fun call(method: String, path: String, body: String?, signed: Boolean): JSONObject =
        withContext(Dispatchers.IO) {
            var last = ApiException(Kind.NETWORK, "network")
            for (attempt in 0..MAX_RETRIES) {
                try {
                    return@withContext once(method, path, body, signed)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: ApiException) {
                    val transient = e.kind == Kind.SERVER &&
                        (e.status == 502 || e.status == 503 || e.status == 504 || e.status in 520..524)
                    if (!transient) throw e
                    last = e
                } catch (e: IOException) {
                    last = ApiException(Kind.NETWORK, "network")
                }
                if (attempt < MAX_RETRIES) delay(minOf(1000L shl attempt, 8000L) + Random.nextLong(0, 600))
            }
            throw last
        }

    private fun once(method: String, path: String, body: String?, signed: Boolean): JSONObject {
        val connection = URL(baseUrl + path).openConnection() as HttpURLConnection
        try {
            connection.requestMethod = method
            connection.connectTimeout = 20000
            connection.readTimeout = 75000
            connection.instanceFollowRedirects = false
            connection.useCaches = false
            connection.setRequestProperty("Accept", "application/json")
            connection.setRequestProperty("Authorization", "Bearer $token")
            val bytes = body?.toByteArray(Charsets.UTF_8) ?: ByteArray(0)
            if (signed) {
                val time = System.currentTimeMillis().toString()
                val nonce = newNonce()
                val text = listOf(method, path, time, nonce, sha256Hex(bytes)).joinToString("\n")
                val signature = DeviceKey.sign(keyAlias, text.toByteArray(Charsets.UTF_8))
                connection.setRequestProperty("X-Admin-Time", time)
                connection.setRequestProperty("X-Admin-Nonce", nonce)
                connection.setRequestProperty("X-Admin-Signature", Base64.encodeToString(signature, Base64.NO_WRAP))
            }
            if (body != null) {
                connection.doOutput = true
                connection.setRequestProperty("Content-Type", "application/json; charset=utf-8")
                connection.setFixedLengthStreamingMode(bytes.size)
                connection.outputStream.use { it.write(bytes) }
            }
            val status = connection.responseCode
            val stream = if (status >= 400) connection.errorStream else connection.inputStream
            val text = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() } ?: ""
            val json = try {
                JSONObject(text)
            } catch (e: Exception) {
                null
            }
            if (status in 200..299 && json != null) return json
            val retryAfter = connection.getHeaderField("Retry-After")?.toIntOrNull() ?: 0
            throw ApiException(Kind.SERVER, json?.optString("error", "") ?: "", status, retryAfter)
        } finally {
            connection.disconnect()
        }
    }

    private fun newNonce(): String {
        val raw = ByteArray(16)
        random.nextBytes(raw)
        return Base64.encodeToString(raw, Base64.NO_WRAP or Base64.URL_SAFE or Base64.NO_PADDING)
    }

    private fun sha256Hex(data: ByteArray): String =
        MessageDigest.getInstance("SHA-256").digest(data).joinToString("") { "%02x".format(it) }

    private companion object {
        const val MAX_RETRIES = 8
    }
}
