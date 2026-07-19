package com.m4.edgex.overlay

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.view.View
import kotlin.math.PI
import kotlin.math.log10

class FluidEffectView(context: Context) : View(context) {

    var edge: String = "left"
    var touchX: Float = 0f
    var touchY: Float = 0f
    var screenWidth: Float = 0f
    var screenHeight: Float = 0f
    var color: Int = Color.WHITE
    var sizeProgress: Int = 50
    var alphaVal: Float = 0.8f
    
    // For visual feedback when finger moves
    var currentDist: Float = 0f

    private val path = Path()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (width <= 0 || height <= 0) return

        val density = resources.displayMetrics.density
        val maxDist = 80f * density
        val dist = currentDist.coerceAtLeast(10f * density)
        
        val elasticity = 0.3f + sizeProgress.coerceIn(0, 100) * 0.025f
        val protrusion = log10((dist / maxDist + 1.0)).toFloat() * maxDist * elasticity
        
        val spreadDim = if (edge == "left" || edge == "right") height.toFloat() else width.toFloat()
        val fingerAlong = if (edge == "left" || edge == "right") touchY else touchX
        
        val halfSpread = spreadDim * 0.15f
        val cp = halfSpread * 0.6f

        path.reset()
        when (edge) {
            "left" -> {
                path.moveTo(0f, fingerAlong - halfSpread)
                path.cubicTo(0f, fingerAlong - cp, protrusion, fingerAlong - cp, protrusion, fingerAlong)
                path.cubicTo(protrusion, fingerAlong + cp, 0f, fingerAlong + cp, 0f, fingerAlong + halfSpread)
            }
            "right" -> {
                val tx = width - protrusion
                path.moveTo(width.toFloat(), fingerAlong - halfSpread)
                path.cubicTo(width.toFloat(), fingerAlong - cp, tx, fingerAlong - cp, tx, fingerAlong)
                path.cubicTo(tx, fingerAlong + cp, width.toFloat(), fingerAlong + cp, width.toFloat(), fingerAlong + halfSpread)
            }
            "top" -> {
                path.moveTo(fingerAlong - halfSpread, 0f)
                path.cubicTo(fingerAlong - cp, 0f, fingerAlong - cp, protrusion, fingerAlong, protrusion)
                path.cubicTo(fingerAlong + cp, protrusion, fingerAlong + cp, 0f, fingerAlong + halfSpread, 0f)
            }
            "bottom" -> {
                val ty = height - protrusion
                path.moveTo(fingerAlong - halfSpread, height.toFloat())
                path.cubicTo(fingerAlong - cp, height.toFloat(), fingerAlong - cp, ty, fingerAlong, ty)
                path.cubicTo(fingerAlong + cp, ty, fingerAlong + cp, height.toFloat(), fingerAlong + halfSpread, height.toFloat())
            }
        }
        path.close()

        paint.color = Color.argb(
            (Color.alpha(color) * alphaVal).toInt().coerceIn(0, 255),
            Color.red(color),
            Color.green(color),
            Color.blue(color),
        )
        canvas.drawPath(path, paint)
    }
}
