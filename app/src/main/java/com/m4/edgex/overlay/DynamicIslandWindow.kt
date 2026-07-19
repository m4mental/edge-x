package com.m4.edgex.overlay

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.WindowManager
import android.view.animation.OvershootInterpolator
import de.robv.android.xposed.XposedBridge

class DynamicIslandWindow(private val context: Context) {
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val islandView = DynamicIslandView(context)
    private var added = false
    private val handler = Handler(Looper.getMainLooper())
    private var animator: ValueAnimator? = null

    fun show(text: String, color: Int, baseSizeDp: Int, yOffsetDp: Int, slideDistDp: Int, scale: Float = 1.0f, isPreview: Boolean = false) {
        val density = context.resources.displayMetrics.density
        islandView.baseWidth = baseSizeDp * density
        islandView.baseHeight = baseSizeDp * density
        islandView.yOffset = yOffsetDp * density
        islandView.slideDistance = slideDistDp.toFloat()
        islandView.islandScale = scale
        islandView.contentText = text
        islandView.contentColor = color

        if (!added) {
            // Calculate a tighter window area to avoid obscuring the whole screen
            val windowWidth = (300f * density * scale).toInt()
            val windowHeight = ((slideDistDp + 60f) * density * scale).toInt()
            
            val params = WindowManager.LayoutParams(
                windowWidth,
                windowHeight,
                2010, // TYPE_SYSTEM_ERROR
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                y = (yOffsetDp * density).toInt()
                layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
            }
            
            // Adjust islandView internal offsets because window is now positioned at y
            islandView.yOffset = 0f 

            try {
                windowManager.addView(islandView, params)
                added = true
                XposedBridge.log("EdgeX: Dynamic Island window added")
            } catch (t: Throwable) {
                XposedBridge.log("EdgeX: Dynamic Island addView failed: ${t.message}")
                return
            }
        }

        if (isPreview) {
            // Instant show for preview
            animator?.cancel()
            islandView.expansionProgress = 1f
            islandView.islandAlpha = 1f
            
            handler.removeCallbacksAndMessages(null)
            handler.postDelayed({
                animateDismiss()
            }, 3000L)
        } else {
            animateEmergence()
        }
    }

    private fun animateEmergence() {
        animator?.cancel()
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 800L
            interpolator = OvershootInterpolator(1.2f)
            addUpdateListener {
                val p = it.animatedValue as Float
                islandView.expansionProgress = p
                islandView.islandAlpha = 0.01f + (0.99f * p)
            }
            start()
        }

        handler.removeCallbacksAndMessages(null)
        handler.postDelayed({
            animateDismiss()
        }, 4000L) // Auto hide after 4 seconds
    }

    private fun animateDismiss() {
        animator?.cancel()
        animator = ValueAnimator.ofFloat(islandView.expansionProgress, 0f).apply {
            duration = 500L
            addUpdateListener {
                val p = it.animatedValue as Float
                islandView.expansionProgress = p
                islandView.islandAlpha = 0.01f + (0.99f * p)
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    dismiss()
                }
            })
            start()
        }
    }

    fun dismiss() {
        if (!added) return
        added = false
        animator?.cancel()
        try {
            windowManager.removeView(islandView)
        } catch (t: Throwable) {}
    }
}
