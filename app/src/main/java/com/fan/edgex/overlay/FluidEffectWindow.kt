package com.fan.edgex.overlay

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import de.robv.android.xposed.XposedBridge

class FluidEffectWindow(private val context: Context) {
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val fluidView = FluidEffectView(context)
    private var added = false
    private var dismissAnimator: ValueAnimator? = null

    fun show(
        edge: String,
        touchX: Float,
        touchY: Float,
        screenWidth: Float,
        screenHeight: Float,
        color: Int,
        sizeProgress: Int,
        alpha: Float,
    ) {
        if (!added) {
            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                2027, // TYPE_ACCESSIBILITY_MAGNIFICATION_OVERLAY
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.TOP or Gravity.START
            }
            try {
                windowManager.addView(fluidView, params)
                added = true
            } catch (t: Throwable) {
                XposedBridge.log("EdgeX: FluidEffectWindow.show failed: ${t.message}")
                return
            }
        }

        dismissAnimator?.cancel()
        dismissAnimator = null

        fluidView.edge = edge
        fluidView.touchX = touchX
        fluidView.touchY = touchY
        fluidView.screenWidth = screenWidth
        fluidView.screenHeight = screenHeight
        fluidView.color = color
        fluidView.sizeProgress = sizeProgress
        fluidView.alphaVal = alpha
        
        // Initial distance for the "down" event
        fluidView.currentDist = 40f * context.resources.displayMetrics.density
        fluidView.invalidate()
    }

    fun update(x: Float, y: Float) {
        if (!added) return
        fluidView.touchX = x
        fluidView.touchY = y
        
        val density = context.resources.displayMetrics.density
        val dist = when (fluidView.edge) {
            "left" -> x
            "right" -> fluidView.width - x
            "top" -> y
            "bottom" -> fluidView.height - y
            else -> 0f
        }
        fluidView.currentDist = dist.coerceAtLeast(10f * density)
        fluidView.invalidate()
    }

    fun dismiss(onComplete: Runnable?) {
        if (!added) {
            onComplete?.run()
            return
        }

        val startDist = fluidView.currentDist
        dismissAnimator = ValueAnimator.ofFloat(startDist, 0f).apply {
            duration = 250L
            interpolator = DecelerateInterpolator()
            addUpdateListener { animation ->
                fluidView.currentDist = animation.animatedValue as Float
                fluidView.alphaVal = (animation.animatedValue as Float / startDist).coerceIn(0f, 1f) * 0.8f
                fluidView.invalidate()
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    realDismiss()
                    onComplete?.run()
                }
            })
            start()
        }
    }

    private fun realDismiss() {
        if (!added) return
        added = false
        try {
            windowManager.removeView(fluidView)
        } catch (t: Throwable) {
            XposedBridge.log("EdgeX: FluidEffectWindow.dismiss failed: ${t.message}")
        }
    }
}
