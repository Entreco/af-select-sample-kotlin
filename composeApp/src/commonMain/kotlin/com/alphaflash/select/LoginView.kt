package com.alphaflash.select

import af_select_sample_kotlin.composeApp.BuildConfig
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity

import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class Pulse(val time: Instant, var offset: Float = 0F)

@Composable
fun ColumnScope.LoginView(title: String, beats: Instant?, auth: () -> Unit) {
    Row(Modifier.fillMaxWidth().height(60.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End )) {
        Column {
            Text("user | ${BuildConfig.USER}")
            Text("pass | ${BuildConfig.PWD.take(3)}***")
        }
        Button(onClick = auth) {
            Text(title)
        }
//        BeatsView(beats)
    }
}

@Composable
fun RowScope.BeatsView(pulse: Instant?) {
    val density = LocalDensity.current
    val strokeWidth = with(density) { 2.dp.toPx() }
    val color = Color.Green
    val baseLineColor = Color.LightGray
    val baseLineWidth = with(density) { 1.dp.toPx() }
    val pulses = remember { mutableStateListOf<Pulse>() }

    LaunchedEffect(pulse) {
        if (pulse != null) {
            pulses.add(Pulse(pulse))
        }
    }

    // Update pulse positions
    var lastUpdate by remember { mutableStateOf(Clock.System.now()) }
    LaunchedEffect(pulses.size) {
        while (true) {
            val currentTime = Clock.System.now()
            val elapsedTime = currentTime - lastUpdate
            lastUpdate = currentTime

            if (pulses.isNotEmpty()) {
                val toRemove = mutableListOf<Pulse>()
                for (p in pulses) {
                    val offsetChange = elapsedTime.inWholeMilliseconds / 5000f
                    p.offset -= offsetChange
                    if (p.offset <= -1.0f) {
                        toRemove.add(p)
                    }
                }
                pulses.removeAll(toRemove)
            }
            delay(16) // ~60 FPS update
        }
    }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(horizontal = 4.dp)
    ) {
        val canvasHeight = size.height
        val canvasWidth = size.width
        val centerY = canvasHeight / 2
        val startHorizontal = Offset(x = 0f, y = centerY)
        val endHorizontal = Offset(x = canvasWidth, y = centerY)

        // Draw the horizontal base line
        drawLine(
            color = baseLineColor,
            start = startHorizontal,
            end = endHorizontal,
            strokeWidth = baseLineWidth,
            cap = StrokeCap.Round
        )

        // Draw the vertical pulses
        pulses.forEach { pulse ->
            val pulseX = canvasWidth + (pulse.offset * canvasWidth) // calculate the offset to the right
            val pulseWidth = canvasWidth / 30 // Adjust as needed for curve width
            val pulseHeight = canvasHeight / 4 // Adjust as needed for curve height

            // Define the path for the curve
            val path = Path().apply {
                moveTo(pulseX - pulseWidth / 2, centerY) // Starting point on the left
                cubicTo(
                    x1 = pulseX - pulseWidth / 4,
                    y1 = centerY - pulseHeight,
                    x2 = pulseX + pulseWidth / 4,
                    y2 = centerY - pulseHeight,
                    x3 = pulseX + pulseWidth / 2,
                    y3 = centerY
                ) // Ending point on the right
            }

            drawPath(
                path = path,
                color = color,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
    }
}