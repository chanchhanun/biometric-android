package com.chanchhanun.biometricandroid.features.auth.domain.repository

interface IAuthRepository {
    suspend fun saveEncryptedToken(token: ByteArray)
}