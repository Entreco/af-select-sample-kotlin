package com.alphaflash.select.auth

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

interface AuthService {
    suspend fun authenticate(user: String, pass: String): AuthResponse
}

const val AUTH_URL: String = "https://api.alphaflash.com/api/auth/alphaflash-client/token"

internal class AuthServiceImpl(
    private val client: HttpClient
) : AuthService {

    override suspend fun authenticate(user: String, pass: String): AuthResponse = client.post(AUTH_URL) {
        contentType(ContentType.Application.Json)
        setBody(AuthRequest(user, pass))
    }.body<AuthResponse>()
}