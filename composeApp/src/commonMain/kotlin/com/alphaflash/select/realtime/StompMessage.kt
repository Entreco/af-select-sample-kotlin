package com.alphaflash.select.realtime

class StompMessage(
    val messageType: String,
    val body: String = "",
) {

    companion object {
        const val DELIMITER: String = "\n"
        const val NULL: String = "\u0000"
    }

    private val headers: MutableMap<String, MutableList<String>> = HashMap()

    fun headers(): Map<String, List<String>> = headers

    fun addHeader(header: Pair<String, String>) {
        headers.getOrPut(header.first) { mutableListOf() }.add(header.second)
    }

    fun firstHeader(key: String): String? = headers[key]?.getOrNull(0)

    override fun toString() = listOf(messageType, headers.short(), body).filter { it.isNotBlank() }.joinToString("|")

    private fun Map<String, List<String>>.short(): String = headers.entries.joinToString { e ->
        "${e.key}:${e.value.take(10)}"
    }
}