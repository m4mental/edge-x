package com.fan.edgex.overlay

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.View
import com.fan.edgex.R

class DynamicIslandView(context: Context) : View(context) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val contentPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rect = RectF()
    
    var islandAlpha: Float = 0.01f
        set(value) {
            field = value
            invalidate()
        }
        
    var expansionProgress: Float = 0f // 0 to 1
        set(value) {
            field = value
            invalidate()
        }
        
    var contentText: String = ""
        set(value) {
            field = value
            invalidate()
        }
        
    var contentColor: Int = Color.GREEN
        set(value) {
            field = value
            invalidate()
        }

    var baseWidth: Float = 100f
        set(value) {
            field = value
            invalidate()
        }
    var baseHeight: Float = 100f
        set(value) {
            field = value
            invalidate()
        }
    var yOffset: Float = 0f
        set(value) {
            field = value
            invalidate()
        }
    var slideDistance: Float = 45f // dp
        set(value) {
            field = value
            invalidate()
        }
    var islandScale: Float = 1.0f
        set(value) {
            field = value.coerceIn(0.5f, 2.0f)
            invalidate()
        }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (width <= 0 || height <= 0) return
        
        val density = resources.displayMetrics.density
        val dotSize = 3.0f * density * islandScale
        val spacing = 1.0f * density * islandScale
        val charWidth = 5 * (dotSize + spacing) + spacing * 2
        
        // Calculate required width based on text length
        val requiredTextWidth = (contentText.length * charWidth)
        val horizontalPadding = 12f * density * islandScale
        val maxWidth = (requiredTextWidth + horizontalPadding * 2).coerceAtLeast(baseWidth * islandScale)
        val maxHeight = 44f * density * islandScale
        
        val currentWidth = (baseWidth * islandScale) + (maxWidth - (baseWidth * islandScale)) * expansionProgress
        val currentHeight = (baseHeight * islandScale) + (maxHeight - (baseHeight * islandScale)) * expansionProgress
        val currentY = (slideDistance * density) * expansionProgress
        
        val centerX = width / 2f
        rect.set(centerX - currentWidth / 2f, currentY, centerX + currentWidth / 2f, currentY + currentHeight)
        
        paint.color = Color.BLACK
        paint.alpha = (255 * islandAlpha).toInt()
        
        val cornerRadius = currentHeight / 2f
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)
        
        if (expansionProgress > 0.8f && contentText.isNotEmpty()) {
            contentPaint.color = contentColor
            contentPaint.alpha = ((expansionProgress - 0.8f) / 0.2f * 255).toInt()
            
            DotMatrixVisualizer.drawText(
                canvas,
                contentText,
                centerX - requiredTextWidth / 2f,
                currentY + (currentHeight - 7 * (dotSize + spacing)) / 2f,
                dotSize,
                spacing,
                contentPaint,
                System.currentTimeMillis(),
                animateBreathe = true
            )
            invalidate() // Keep animating if needed
        }
    }
}
