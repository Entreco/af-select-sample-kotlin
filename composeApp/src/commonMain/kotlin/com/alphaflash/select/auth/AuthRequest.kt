package com.alphaflash.select.auth

import kotlinx.serialization.Serializable

//    "username":"test",
//    "password":"secret"
@Serializable
data class AuthRequest(
    val username: String,
    val password: String
)