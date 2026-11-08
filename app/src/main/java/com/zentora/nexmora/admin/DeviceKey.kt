package com.zentora.nexmora.admin

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import android.security.keystore.UserNotAuthenticatedException
import android.util.Base64
import java.security.GeneralSecurityException
import java.security.InvalidKeyException
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.Signature
import java.security.spec.ECGenParameterSpec

/**
 * The phone's identity key: an ECDSA P-256 key that never leaves the secure hardware and can sign only within
 * 300 seconds after a successful fingerprint. If a new fingerprint is enrolled the key is destroyed by Android.
 * Two alias slots (A and B) let a new key be created and proven before the old one is removed.
 */
object DeviceKey {
    const val ALIAS_A = "nx_admin_device_a"
    const val ALIAS_B = "nx_admin_device_b"
    private const val AUTH_SECONDS = 300

    fun other(alias: String): String = if (alias == ALIAS_A) ALIAS_B else ALIAS_A

    private fun keyStore(): KeyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }

    fun exists(alias: String): Boolean = try {
        keyStore().containsAlias(alias)
    } catch (e: Exception) {
        false
    }

    fun delete(alias: String) {
        try {
            keyStore().deleteEntry(alias)
        } catch (e: Exception) {
            // nothing to delete
        }
    }

    /** Creates a fresh key under [alias] (replacing any old one) and returns its public key, Base64 X.509. */
    @Suppress("DEPRECATION")
    fun create(alias: String): String {
        delete(alias)
        val builder = KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_SIGN)
            .setAlgorithmParameterSpec(ECGenParameterSpec("secp256r1"))
            .setDigests(KeyProperties.DIGEST_SHA256)
            .setUserAuthenticationRequired(true)
            .setInvalidatedByBiometricEnrollment(true)
        if (Build.VERSION.SDK_INT >= 30) {
            builder.setUserAuthenticationParameters(AUTH_SECONDS, KeyProperties.AUTH_BIOMETRIC_STRONG)
        } else {
            builder.setUserAuthenticationValidityDurationSeconds(AUTH_SECONDS)
        }
        val generator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore")
        generator.initialize(builder.build())
        val pair = generator.generateKeyPair()
        return Base64.encodeToString(pair.public.encoded, Base64.NO_WRAP)
    }

    /** Signs [data]. Throws ApiException(AUTH_NEEDED) when the fingerprint window has expired. */
    fun sign(alias: String, data: ByteArray): ByteArray {
        try {
            val key = keyStore().getKey(alias, null) as? PrivateKey
                ?: throw ApiException(Kind.KEY_INVALID, "key_missing")
            val signature = Signature.getInstance("SHA256withECDSA")
            signature.initSign(key)
            signature.update(data)
            return signature.sign()
        } catch (e: ApiException) {
            throw e
        } catch (e: UserNotAuthenticatedException) {
            throw ApiException(Kind.AUTH_NEEDED, "auth_needed")
        } catch (e: KeyPermanentlyInvalidatedException) {
            throw ApiException(Kind.KEY_INVALID, "key_invalidated")
        } catch (e: InvalidKeyException) {
            val needsAuth = e.cause is UserNotAuthenticatedException ||
                (e.message ?: "").contains("authenticat", ignoreCase = true)
            throw if (needsAuth) ApiException(Kind.AUTH_NEEDED, "auth_needed") else ApiException(Kind.KEY_INVALID, "key_error")
        } catch (e: GeneralSecurityException) {
            throw ApiException(Kind.KEY_INVALID, "key_error")
        }
    }
}
