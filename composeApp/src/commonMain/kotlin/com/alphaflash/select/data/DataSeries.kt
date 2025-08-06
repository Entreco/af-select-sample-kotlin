package com.alphaflash.select.data

import kotlinx.serialization.Serializable

@Serializable
data class DataSeries(
    val id: Long = 0,
    val display: String? = null,
    val type: Type? = null,
    val scale: Scale? = null,
    val interval: String? = null,
    val topics: Collection<Topic>? = null,
    val providers: Collection<AlphaFlashProvider>? = null,
)

@Serializable
data class Type(
    val name: String? = null,
    val display: String? = null,
    val description: String? = null,
)

@Serializable
data class Scale(
    val name: String? = null,
    val display: String? = null,
    val description: String? = null,
)

@Serializable
data class Topic(
    val qcode: String,
    val name: String? = null,
    val broader: String? = null,
)

@Serializable
data class AlphaFlashProvider(
    val providerName: String? = null,
    val categoryId : Int = 0,
    val datumId: Int = 0,
    val active : Boolean = false,
)