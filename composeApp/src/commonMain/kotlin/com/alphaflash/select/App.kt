package com.alphaflash.select

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(viewModel: AppViewModel = viewModel()) {
    MaterialTheme {

        val state by viewModel.authState.collectAsStateWithLifecycle(null)
        val beats by viewModel.beats.collectAsStateWithLifecycle(null)
        val logs by viewModel.logs.collectAsStateWithLifecycle(emptyList<Log>())

        Column(Modifier.fillMaxWidth().padding(4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            LoginView(if (state == null) "Authenticate" else " Disconnect", beats) { viewModel.toggle(state) }
            Separator("Console")
            LazyColumn(Modifier.fillMaxWidth()) {
                items(logs) { log ->
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start)) {
                        Text(log.stamp.toString(), color = Color.LightGray, fontSize = 10.sp)
                        Text(log.message, color = Color.DarkGray, fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ColumnScope.Separator(title: String) {
    Box(Modifier.fillMaxWidth().wrapContentHeight(), contentAlignment = Alignment.Center) {
        Divider()
        Text(title, modifier = Modifier.background(Color.White).padding(8.dp), color = Color.LightGray)
    }
}

