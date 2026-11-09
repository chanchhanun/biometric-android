package com.chanchhanun.biometricandroid.features.auth.domain.state

import androidx.biometric.BiometricPrompt

sealed class BiometricState {
    data object Idle : BiometricState()
    data object Authenticating : BiometricState()
    data class Success(val encryptedPayload: ByteArray) : BiometricState()
    data class Error(val message: String) : BiometricState()
}

sealed class BiometricIntent {
    data object StartAuthentication : BiometricIntent()
    data class OnAuthenticationSucceeded(val cryptoObject: BiometricPrompt.CryptoObject?) : BiometricIntent()
    data class OnAuthenticationFailed(val errorMsg: String) : BiometricIntent()
}