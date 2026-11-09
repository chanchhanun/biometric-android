package com.chanchhanun.biometricandroid.di

import com.chanchhanun.biometricandroid.core.security.BiometricCryptoManager
import com.chanchhanun.biometricandroid.features.auth.data.repository.AuthRepositoryImpl
import com.chanchhanun.biometricandroid.features.auth.domain.repository.IAuthRepository
import com.chanchhanun.biometricandroid.features.auth.representation.viewmodel.BiometricViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Core
    single { BiometricCryptoManager() }

    // Data Layer
    single<IAuthRepository> { AuthRepositoryImpl() }

    // Presentation Layer
    viewModel { BiometricViewModel(repository = get()) }
}