package com.alphaflash.select

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "af-select-sample-kotlin",
    ) {
        App()
    }
}