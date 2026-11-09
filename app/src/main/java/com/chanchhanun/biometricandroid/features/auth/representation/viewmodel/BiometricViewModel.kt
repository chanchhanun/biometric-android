package com.chanchhanun.biometricandroid.features.auth.representation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chanchhanun.biometricandroid.features.auth.domain.repository.IAuthRepository
import com.chanchhanun.biometricandroid.features.auth.domain.state.BiometricIntent
import com.chanchhanun.biometricandroid.features.auth.domain.state.BiometricState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BiometricViewModel(
    private val repository: IAuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<BiometricState>(BiometricState.Idle)
    val uiState: StateFlow<BiometricState> = _uiState.asStateFlow()

    fun processIntent(intent: BiometricIntent) {
        when (intent) {
            is BiometricIntent.StartAuthentication -> {
                _uiState.value = BiometricState.Authenticating
            }
            is BiometricIntent.OnAuthenticationSucceeded -> handleSuccess(intent.cryptoObject)
            is BiometricIntent.OnAuthenticationFailed -> {
                _uiState.value = BiometricState.Error(intent.errorMsg)
            }
        }
    }

    private fun handleSuccess(cryptoObject: androidx.biometric.BiometricPrompt.CryptoObject?) {
        viewModelScope.launch {
            try {
                val cipher = cryptoObject?.cipher
                val dummyData = "SecureAPI_Token_12345".toByteArray()
                val encrypted = cipher?.doFinal(dummyData) ?: byteArrayOf()
                
                repository.saveEncryptedToken(encrypted)
                _uiState.value = BiometricState.Success(encrypted)
            } catch (e: Exception) {
                _uiState.value = BiometricState.Error("Encryption failed: ${e.message}")
            }
        }
    }
}