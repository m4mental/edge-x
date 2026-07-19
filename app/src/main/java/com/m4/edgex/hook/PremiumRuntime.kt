package com.m4.edgex.hook

import android.content.Context

object PremiumRuntime {
    fun isActive(): Boolean = true

    fun onFluidEffectDown(
        context: Context,
        edge: String,
        touchX: Float,
        touchY: Float,
        screenWidth: Float,
        screenHeight: Float,
        color: Int,
        sizeProgress: Int,
        alpha: Float,
    ): Boolean {
        FluidEffectManager.onDown(context, edge, touchX, touchY, screenWidth, screenHeight, color, sizeProgress, alpha)
        return true
    }

    fun onFluidEffectMove(touchX: Float, touchY: Float): Boolean {
        FluidEffectManager.onMove(touchX, touchY)
        return true
    }

    fun onFluidEffectUp(onComplete: Runnable? = null): Boolean {
        FluidEffectManager.onUp(onComplete)
        return true
    }

    fun onScreenOff() {
    }

    fun dismissEdgeLighting() {
    }
}
