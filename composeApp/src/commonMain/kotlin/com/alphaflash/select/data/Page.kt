package com.alphaflash.select.data

import kotlinx.serialization.Serializable

@Serializable
data class Page<T>(
    val number: Long,
    val totalElements: Long,
    val totalPages: Long,
    val content: List<T>
)