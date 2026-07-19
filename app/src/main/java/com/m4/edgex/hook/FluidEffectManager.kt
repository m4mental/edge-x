package com.m4.edgex.hook

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.m4.edgex.overlay.FluidEffectWindow

object FluidEffectManager {
    private var activeWindow: FluidEffectWindow? = null
    private val handler = Handler(Looper.getMainLooper())

    fun onDown(
        context: Context,
        edge: String,
        touchX: Float,
        touchY: Float,
        screenWidth: Float,
        screenHeight: Float,
        color: Int,
        sizeProgress: Int,
        alpha: Float,
    ) {
        handler.post {
            if (activeWindow == null) {
                activeWindow = FluidEffectWindow(context)
            }
            activeWindow?.show(edge, touchX, touchY, screenWidth, screenHeight, color, sizeProgress, alpha)
        }
    }

    fun onMove(x: Float, y: Float) {
        handler.post {
            activeWindow?.update(x, y)
        }
    }

    fun onUp(onComplete: Runnable?) {
        handler.post {
            activeWindow?.dismiss(onComplete)
        }
    }
}
