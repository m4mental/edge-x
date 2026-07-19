package com.m4.edgex.overlay

import android.content.Context
import android.graphics.PixelFormat
import android.view.WindowManager
import de.robv.android.xposed.XposedBridge

class PieWindow(
    private val context: Context,
    private val onDismiss: () -> Unit,
) {
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val pieView = PieView(context)
    private var added = false

    var onStandaloneAction: ((String) -> Unit)? = null

    fun show(anchorX: Float, anchorY: Float, edge: String, rings: List<PieView.Ring>, accentColor: Int, sizeScale: Float, touchable: Boolean = false) {
        if (added) return
        pieView.anchorX = anchorX
        pieView.anchorY = anchorY
        pieView.edge = edge
        pieView.rings = rings
        pieView.accentColor = accentColor
        pieView.sizeScale = sizeScale

        if (touchable) {
            pieView.onActionSelected = { action ->
                if (action != null) {
                    // Logic to execute action will be handled by the caller or a listener
                    onStandaloneAction?.invoke(action)
                }
                dismiss()
            }
        } else {
            pieView.onActionSelected = null
        }

        @Suppress("DEPRECATION")
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            2024, // TYPE_NAVIGATION_BAR_PANEL - guaranteed above almost everything
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                (if (touchable) 0 else WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE) or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            PixelFormat.TRANSLUCENT,
        ).apply {
            gravity = android.view.Gravity.TOP or android.view.Gravity.START
            layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
            // Use private flags to ensure visibility above status bar
            try {
                val field = WindowManager.LayoutParams::class.java.getField("privateFlags")
                var privateFlags = field.get(this) as Int
                privateFlags = privateFlags or 0x00000040 // PRIVATE_FLAG_SYSTEM_ERROR
                field.set(this, privateFlags)
            } catch (_: Throwable) {}
        }

        try {
            windowManager.addView(pieView, params)
            added = true
            pieView.animateIn()
        } catch (t: Throwable) {
            XposedBridge.log("EdgeX: PieWindow.show failed: ${t.message}")
            onDismiss()
        }
    }

    fun update(x: Float, y: Float) {
        if (!added) return
        val hit = pieView.hitTest(x, y)
        pieView.highlightedRing = hit?.first ?: -1
        pieView.highlightedSlot = hit?.second ?: -1
    }

    fun commit(): String? {
        val r = pieView.highlightedRing
        val s = pieView.highlightedSlot
        val selected = if (pieView.isAnimationComplete() && r >= 0 && s >= 0)
            pieView.rings.getOrNull(r)?.slots?.getOrNull(s)?.action
        else null
        dismiss()
        return selected
    }

    fun dismiss() {
        if (!added) return
        added = false
        try {
            windowManager.removeView(pieView)
        } catch (t: Throwable) {
            XposedBridge.log("EdgeX: PieWindow.dismiss failed: ${t.message}")
        }
        onDismiss()
    }

    fun isShowing() = added
}
