package com.alphaflash.select

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform