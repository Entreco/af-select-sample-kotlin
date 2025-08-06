package com.alphaflash.select.data

import kotlinx.serialization.Serializable

@Serializable
data class Event(
    val id: String,
    val dataReleaseId: Long,
    val date: String,
    val country: String,
    val title: String,
    val rating: Int,
    val reportingPeriod: String?,
    val dataSeriesEntries: Collection<DataSeriesEntry>? = emptyList()
)

@Serializable
data class DataSeriesEntry(
    val dataSeriesId: Int,
    val display: String,
    val previous: String?,
    val forecasts: Collection<Forecast>,
    val actual: String? = null,
    val type: String? = null,
    val scale: String? = null,
)

@Serializable
data class Forecast(
    val source: String,
    val value: String?
)