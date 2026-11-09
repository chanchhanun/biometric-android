package com.chanchhanun.biometricandroid.features.auth.representation.ui

import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.core.content.ContextCompat
import com.chanchhanun.biometricandroid.core.security.BiometricCryptoManager
import com.chanchhanun.biometricandroid.features.auth.domain.state.BiometricIntent
import com.chanchhanun.biometricandroid.features.auth.domain.state.BiometricState
import com.chanchhanun.biometricandroid.features.auth.representation.viewmodel.BiometricViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun BiometricAuthScreen(
    viewModel: BiometricViewModel = koinViewModel(),
    cryptoManager: BiometricCryptoManager = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current as FragmentActivity

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (uiState) {
            is BiometricState.Idle -> {
                Button(onClick = {
                    val cipher = cryptoManager.getInitializedCipherForEncryption()
                    if (cipher != null) {
                        viewModel.processIntent(BiometricIntent.StartAuthentication)
                        showBiometricPrompt(context, viewModel, cipher)
                    } else {
                        viewModel.processIntent(BiometricIntent.OnAuthenticationFailed("Biometrics changed. Login with password."))
                    }
                }) {
                    Text("Authenticate")
                }
            }
            is BiometricState.Authenticating -> CircularProgressIndicator()
            is BiometricState.Success -> Text("Authentication & Encryption Successful")
            is BiometricState.Error -> {
                val errorMsg = (uiState as BiometricState.Error).message
                Text("Error: $errorMsg", color = MaterialTheme.colorScheme.error)
                Button(onClick = { viewModel.processIntent(BiometricIntent.StartAuthentication) }) {
                    Text("Retry")
                }
            }
        }
    }
}

private fun showBiometricPrompt(
    activity: FragmentActivity,
    viewModel: BiometricViewModel,
    cipher: javax.crypto.Cipher
) {
    val executor = ContextCompat.getMainExecutor(activity)
    val biometricPrompt = BiometricPrompt(activity, executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                viewModel.processIntent(BiometricIntent.OnAuthenticationFailed(errString.toString()))
            }
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                viewModel.processIntent(BiometricIntent.OnAuthenticationSucceeded(result.cryptoObject))
            }
            override fun onAuthenticationFailed() {
                viewModel.processIntent(BiometricIntent.OnAuthenticationFailed("Not recognized."))
            }
        })

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Sign In")
        .setSubtitle("Use your biometric credential")
        .setNegativeButtonText("Cancel")
        .setAllowedAuthenticators(BIOMETRIC_STRONG)
        .build()

    biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
}