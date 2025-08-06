package com.alphaflash.select.data

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.datetime.Instant

interface SelectDataService {
    fun auth(token: String?): Boolean
    suspend fun getAllEventsBetween(start: Instant, end: Instant): Collection<Event>
    suspend fun getAllDataSeries(): Collection<DataSeries>
}

const val EVENTS_URL = "https://api.alphaflash.com/api/select/calendar/events"
const val DATA_SERIES_URL = "https://api.alphaflash.com/api/select/series"
const val PAGE_SIZE: Int = 100

internal class SelectDataServiceImpl(
    private val client: HttpClient,
) : SelectDataService {

    private var accessToken: String? = null

    override fun auth(token: String?) : Boolean {
        accessToken = token
        return accessToken?.isNotBlank() == true
    }

    override suspend fun getAllEventsBetween(start: Instant, end: Instant): Collection<Event> =
        depaginate { pageNumber: Int ->
            getEventPageBetween(start, end, pageNumber, PAGE_SIZE)
        }

    override suspend fun getAllDataSeries(): Collection<DataSeries> =
        depaginate { pageNumber: Int ->
            getDataSeriesPage(pageNumber, PAGE_SIZE)
        }

    private suspend fun getDataSeriesPage(page: Int, size: Int): Page<DataSeries> {
        val request = httpGet("$DATA_SERIES_URL?page=$page&size=$size")
        return try {
            request.body()
        } catch (oops: Exception) {
            println("Error parsing results: $oops")
            Page(0, 0, 0, emptyList())
        }
    }

    private suspend fun getEventPageBetween(start: Instant, end: Instant, page: Int, size: Int): Page<Event> {
        val request =
            httpGet("$EVENTS_URL?start=${start.toEpochMilliseconds()}&end=${end.toEpochMilliseconds()}&page=$page&size=$size")
        return try {
            request.body()
        } catch (oops: Exception) {
            println("Error parsing results: $oops")
            Page(0, 0, 0, emptyList())
        }
    }

    private suspend fun httpGet(
        url: String
    ) = client.get(url) {
        header("Authorization", "Bearer $accessToken")
    }

    private suspend fun <T> depaginate(pageSupplier: PageSupplier<T>): Collection<T> = buildList {
        var pageCount: Long = 1
        var i = 0
        while (i < pageCount) {
            val currentPage = pageSupplier.getPage(i)
            println("getting page: $i")
            pageCount = currentPage.totalPages
            addAll(currentPage.content)
            i++
        }
    }
}

fun interface PageSupplier<T> {
    suspend fun getPage(pageNumber: Int): Page<T>
}