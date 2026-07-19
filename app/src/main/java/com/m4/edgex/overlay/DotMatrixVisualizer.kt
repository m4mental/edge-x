package com.m4.edgex.overlay

import android.graphics.Canvas
import android.graphics.Paint
import kotlin.math.sin

object DotMatrixVisualizer {

    private val charMap = mapOf(
        'C' to arrayOf("01110", "10001", "10000", "10000", "10000", "10001", "01110"),
        'H' to arrayOf("10001", "10001", "10001", "11111", "10001", "10001", "10001"),
        'A' to arrayOf("01110", "10001", "10001", "11111", "10001", "10001", "10001"),
        'R' to arrayOf("11110", "10001", "10001", "11110", "10100", "10010", "10001"),
        'G' to arrayOf("01110", "10001", "10000", "10111", "10001", "10001", "01110"),
        'I' to arrayOf("01110", "00100", "00100", "00100", "00100", "00100", "01110"),
        'N' to arrayOf("10001", "11001", "10101", "10011", "10001", "10001", "10001"),
        'D' to arrayOf("11110", "10001", "10001", "10001", "10001", "10001", "11110"),
        'S' to arrayOf("01110", "10001", "01000", "00110", "00001", "10001", "01110"),
        'E' to arrayOf("11111", "10000", "10000", "11110", "10000", "10000", "11111"),
        'O' to arrayOf("01110", "10001", "10001", "10001", "10001", "10001", "01110"),
        'T' to arrayOf("11111", "00100", "00100", "00100", "00100", "00100", "00100"),
        'M' to arrayOf("10001", "11011", "10101", "10001", "10001", "10001", "10001"),
        ' ' to arrayOf("00000", "00000", "00000", "00000", "00000", "00000", "00000"),
        'Z' to arrayOf("00100", "01100", "11111", "01110", "00110", "00100", "00000"), // Bolt style
        '%' to arrayOf("11001", "11010", "00100", "01011", "10011", "00000", "00000"),
        '0' to arrayOf("01110", "10001", "10011", "10101", "11001", "10001", "01110"),
        '1' to arrayOf("00100", "01100", "00100", "00100", "00100", "00100", "01110"),
        '2' to arrayOf("01110", "10001", "00001", "00110", "01000", "10000", "11111"),
        '3' to arrayOf("11111", "00010", "00100", "00010", "00001", "10001", "01110"),
        '4' to arrayOf("00010", "00110", "01010", "10010", "11111", "00010", "00010"),
        '5' to arrayOf("11111", "10000", "11110", "00001", "00001", "10001", "01110"),
        '6' to arrayOf("00110", "01000", "10000", "11110", "10001", "10001", "01110"),
        '7' to arrayOf("11111", "00001", "00010", "00100", "01000", "01000", "01000"),
        '8' to arrayOf("01110", "10001", "10001", "01110", "10001", "10001", "01110"),
        '9' to arrayOf("01110", "10001", "10001", "01111", "00001", "00010", "00110")
    )

    fun drawText(canvas: Canvas, text: String, x: Float, y: Float, dotSize: Float, spacing: Float, paint: Paint, timeMs: Long, animateBreathe: Boolean = false) {
        var currentX = x
        val breatheAlpha = if (animateBreathe) (0.8f + 0.2f * sin(timeMs / 180.0).toFloat()) else 1.0f
        val boltFlicker = (0.7f + 0.3f * sin(timeMs / 80.0).toFloat())
        val originalAlpha = paint.alpha

        text.forEach { char ->
            val pattern = charMap[char] ?: charMap[' ']!!
            pattern.forEachIndexed { rowIndex, row ->
                row.forEachIndexed { colIndex, dot ->
                    if (dot == '1') {
                        val dx = currentX + colIndex * (dotSize + spacing)
                        val dy = y + rowIndex * (dotSize + spacing)
                        
                        val charAlphaFactor = if (char == 'Z') boltFlicker else 1.0f
                        paint.alpha = (originalAlpha * breatheAlpha * charAlphaFactor).toInt()
                        
                        canvas.drawCircle(dx, dy, dotSize / 2, paint)
                    }
                }
            }
            currentX += 5 * (dotSize + spacing) + spacing * 2
        }
        paint.alpha = originalAlpha
    }
}
