package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.OceanTealDark
import com.example.ui.theme.OutlineBorder
import kotlin.math.abs

/**
 * Custom Jetpack Compose 2D Matrix Barcode Canvas.
 * Generates an authentic matrix pattern based on the seed string and step,
 * including 3 corner position detection patterns (finder patterns) and timing marks.
 */
@Composable
fun QrCodeCanvas(
    payload: String,
    modifier: Modifier = Modifier,
    size: Dp = 220.dp,
    moduleColor: Color = OceanTealDark,
    backgroundColor: Color = Color.White
) {
    val matrixSize = 25 // 25x25 QR Matrix
    val hash = remember(payload) { abs(payload.hashCode()) }

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(1.dp, OutlineBorder, RoundedCornerShape(12.dp))
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size - 24.dp)) {
            val moduleSize = this.size.width / matrixSize

            // Helper to draw a square module
            fun drawModule(x: Int, y: Int, color: Color) {
                drawRoundRect(
                    color = color,
                    topLeft = Offset(x * moduleSize, y * moduleSize),
                    size = Size(moduleSize * 0.95f, moduleSize * 0.95f),
                    cornerRadius = CornerRadius(moduleSize * 0.2f, moduleSize * 0.2f)
                )
            }

            // Draw Finder Pattern (7x7 nested square) at (startX, startY)
            fun drawFinderPattern(startX: Int, startY: Int) {
                for (r in 0 until 7) {
                    for (c in 0 until 7) {
                        val isOuter = (r == 0 || r == 6 || c == 0 || c == 6)
                        val isInner = (r in 2..4 && c in 2..4)
                        if (isOuter || isInner) {
                            drawModule(startX + c, startY + r, moduleColor)
                        }
                    }
                }
            }

            // Draw Top-Left, Top-Right, Bottom-Left Finder Patterns
            drawFinderPattern(0, 0)
            drawFinderPattern(matrixSize - 7, 0)
            drawFinderPattern(0, matrixSize - 7)

            // Draw Timing Patterns
            for (i in 7 until matrixSize - 7) {
                if (i % 2 == 0) {
                    drawModule(i, 6, moduleColor)
                    drawModule(6, i, moduleColor)
                }
            }

            // Pseudo-random data modules derived deterministically from payload hash
            for (r in 0 until matrixSize) {
                for (c in 0 until matrixSize) {
                    // Skip finder pattern zones
                    val inTL = r < 8 && c < 8
                    val inTR = r < 8 && c >= matrixSize - 8
                    val inBL = r >= matrixSize - 8 && c < 8
                    val isTiming = (r == 6 || c == 6)

                    if (!inTL && !inTR && !inBL && !isTiming) {
                        // Deterministic bit generation
                        val cellSeed = (r * matrixSize + c) xor hash
                        val bit = ((cellSeed * 1103515245 + 12345) ushr 16) and 1
                        if (bit == 1) {
                            drawModule(c, r, moduleColor)
                        }
                    }
                }
            }
        }
    }
}

