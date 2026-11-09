package com.chanchhanun.biometricandroid.core.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class BiometricCryptoManager {

    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
    private val keyName = "biometric_secure_key"

    fun getInitializedCipherForEncryption(): Cipher? {
        val cipher = getCipher()
        val secretKey = getOrCreateSecretKey()
        return try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            cipher
        } catch (e: Exception) {
            null // Key might be invalidated
        }
    }

    private fun getOrCreateSecretKey(): SecretKey {
        keyStore.getKey(keyName, null)?.let { return it as SecretKey }

        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            "AndroidKeyStore"
        )

        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            keyName,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
            .setUserAuthenticationRequired(true)
            // Critical: Invalidates key if new biometrics are enrolled
            .setInvalidatedByBiometricEnrollment(true)
            .build()

        keyGenerator.init(keyGenParameterSpec)
        return keyGenerator.generateKey()
    }

    private fun getCipher(): Cipher {
        return Cipher.getInstance(
            KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7
        )
    }
}