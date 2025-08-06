package com.alphaflash.select.realtime

import kotlinx.serialization.Serializable

@Serializable
data class Observation(
    val dataSeriesId: Long = 0L,
    val value: String = "",
    val eventDate: String = "",
    val source: String = "",
)