package com.alphaflash.select.auth

import kotlinx.serialization.Serializable

//"access_token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzUxMiJ9...jLjaQ8PjzlcbCOBQZRkMDgnCAF-9w",
//"expires_in": 600,
//"refresh_token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzUxMiJ9...KOSee-cpZeUyEMcVZNZYr3eypnMm6",
//"refresh_expires_in": 1800
@Serializable
data class AuthResponse(
    val access_token: String,
    val expires_in: Int,
    val refresh_token: String,
    val refresh_expires_in: Int,
)