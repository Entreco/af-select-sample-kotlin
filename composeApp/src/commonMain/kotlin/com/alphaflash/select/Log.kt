package com.alphaflash.select

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class Log(
    val type: Int,
    val message: String,
    val stamp: Instant = Clock.System.now(),
)