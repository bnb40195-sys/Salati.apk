package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.data.models.WallpaperModel
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun IslamicPatternBackground(
    modifier: Modifier = Modifier,
    wallpaper: WallpaperModel? = null,
    content: @Composable () -> Unit
) {
    val baseColor = if (wallpaper != null) {
        Color(wallpaper.colorHex)
    } else {
        MaterialTheme.colorScheme.background
    }

    val surfaceColor = MaterialTheme.colorScheme.surface
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            baseColor.copy(alpha = 0.95f),
            baseColor.copy(alpha = 0.85f),
            surfaceColor.copy(alpha = 0.92f)
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(gradientBrush)
    ) {
        // Islamic geometric watermark pattern
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 1.2f
            val patternColor = Color(0xFFD4AF37).copy(alpha = 0.06f) // Subtle gold arabesque
            val spacing = 120f
            val starRadius = 24f

            val cols = (size.width / spacing).toInt() + 2
            val rows = (size.height / spacing).toInt() + 2

            for (i in 0 until cols) {
                for (j in 0 until rows) {
                    val cx = i * spacing
                    val cy = j * spacing

                    // Draw 8-pointed Islamic Star
                    val path = Path()
                    val points = 16
                    for (k in 0 until points) {
                        val angle = (k * Math.PI / 8).toFloat()
                        val r = if (k % 2 == 0) starRadius else starRadius * 0.5f
                        val x = cx + r * cos(angle)
                        val y = cy + r * sin(angle)
                        if (k == 0) path.moveTo(x, y) else path.lineTo(x, y)
                    }
                    path.close()
                    drawPath(path, color = patternColor, style = Stroke(width = strokeWidth))

                    // Interconnecting geometric lines
                    drawLine(
                        color = patternColor.copy(alpha = 0.03f),
                        start = Offset(cx - starRadius, cy),
                        end = Offset(cx + starRadius, cy),
                        strokeWidth = strokeWidth
                    )
                    drawLine(
                        color = patternColor.copy(alpha = 0.03f),
                        start = Offset(cx, cy - starRadius),
                        end = Offset(cx, cy + starRadius),
                        strokeWidth = strokeWidth
                    )
                }
            }
        }

        content()
    }
}
