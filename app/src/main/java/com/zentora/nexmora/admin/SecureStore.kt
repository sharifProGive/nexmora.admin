package com.zentora.nexmora.admin

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Small settings store. Server address and Admin Token are encrypted with an AES-GCM key that lives in the
 * Android Keystore, so the raw values are never written to disk.
 */
class SecureStore(context: Context) {
    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences("nx_admin_prefs", Context.MODE_PRIVATE)

    fun lang(): Lang = Lang.from(prefs.getString("lang", null))
    fun setLang(lang: Lang) {
        prefs.edit().putString("lang", lang.code).apply()
    }

    fun screenProtect(): Boolean = prefs.getBoolean("protect", true)
    fun setScreenProtect(on: Boolean) {
        prefs.edit().putBoolean("protect", on).apply()
    }

    fun registered(): Boolean = prefs.getBoolean("registered", false)
    fun setRegistered(on: Boolean) {
        prefs.edit().putBoolean("registered", on).commit()
    }

    fun keyAlias(): String = prefs.getString("key_alias", DeviceKey.ALIAS_A) ?: DeviceKey.ALIAS_A
    fun setKeyAlias(alias: String) {
        prefs.edit().putString("key_alias", alias).commit()
    }

    fun putSecret(name: String, value: String) {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, boxKey())
        val encrypted = cipher.doFinal(value.toByteArray(Charsets.UTF_8))
        val packed = cipher.iv + encrypted
        prefs.edit().putString("s_$name", Base64.encodeToString(packed, Base64.NO_WRAP)).commit()
    }

    fun getSecret(name: String): String? {
        val raw = prefs.getString("s_$name", null) ?: return null
        return try {
            val packed = Base64.decode(raw, Base64.NO_WRAP)
            val iv = packed.copyOfRange(0, 12)
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.DECRYPT_MODE, boxKey(), GCMParameterSpec(128, iv))
            String(cipher.doFinal(packed, 12, packed.size - 12), Charsets.UTF_8)
        } catch (e: Exception) {
            null
        }
    }

    private fun boxKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        val existing = keyStore.getKey(BOX_ALIAS, null) as? SecretKey
        if (existing != null) return existing
        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        generator.init(
            KeyGenParameterSpec.Builder(BOX_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()
        )
        return generator.generateKey()
    }

    private companion object {
        const val BOX_ALIAS = "nx_admin_box"
    }
}
