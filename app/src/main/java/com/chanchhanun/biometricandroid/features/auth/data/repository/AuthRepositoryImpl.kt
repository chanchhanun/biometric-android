package com.chanchhanun.biometricandroid.features.auth.data.repository

import com.chanchhanun.biometricandroid.features.auth.domain.repository.IAuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepositoryImpl : IAuthRepository {
    override suspend fun saveEncryptedToken(token: ByteArray) {
        withContext(Dispatchers.IO) {
            // Write encrypted byte array to secure storage here
        }
    }
}