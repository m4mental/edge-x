package com.fan.edgex.hook

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import com.fan.edgex.config.AppConfig
import com.fan.edgex.overlay.DynamicIslandWindow
import de.robv.android.xposed.XposedBridge

object DynamicIslandManager {
    private var islandWindow: DynamicIslandWindow? = null
    private val handler = Handler(Looper.getMainLooper())
    private var configRepository: HookConfigRepository? = null

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action ?: return
            XposedBridge.log("EdgeX: Dynamic Island received: $action")
            
            val isEnabled = configRepository?.get(AppConfig.DYNAMIC_ISLAND_ENABLED) == "true"
            val isPreview = action == "com.fan.edgex.action.DYNAMIC_ISLAND_PREVIEW"
            
            XposedBridge.log("EdgeX: Dynamic Island isEnabled=$isEnabled, isPreview=$isPreview")
            
            if (!isEnabled && !isPreview) return
            
            val yOffset = configRepository?.get(AppConfig.DYNAMIC_ISLAND_Y_OFFSET_DP)?.toIntOrNull() ?: 10
            val baseSize = configRepository?.get(AppConfig.DYNAMIC_ISLAND_SIZE_DP)?.toIntOrNull() ?: 30
            val slideDist = configRepository?.get(AppConfig.DYNAMIC_ISLAND_SLIDE_DISTANCE_DP)?.toIntOrNull() ?: 45
            val scale = configRepository?.get(AppConfig.DYNAMIC_ISLAND_SCALE)?.toFloatOrNull() ?: 1.0f
            
            // Fix: Default to true if not set. configRepository.get returns "" for missing keys.
            val rawShowPct = configRepository?.get(AppConfig.DYNAMIC_ISLAND_SHOW_PERCENTAGE)
            val showPercentage = rawShowPct == "true" || rawShowPct.isNullOrEmpty()

            XposedBridge.log("EdgeX: Dynamic Island show request: showPercentage=$showPercentage (raw='$rawShowPct')")

            when (action) {
                Intent.ACTION_POWER_CONNECTED -> {
                    val level = if (showPercentage) getBatteryLevel(context) else -1
                    XposedBridge.log("EdgeX: Dynamic Island charging level=$level")
                    val text = if (level >= 0) "Z CHARGING $level%" else "Z CHARGING"
                    showIsland(context, text, Color.GREEN, baseSize, yOffset, slideDist, scale)
                }
                Intent.ACTION_POWER_DISCONNECTED -> {
                    val level = if (showPercentage) getBatteryLevel(context) else -1
                    XposedBridge.log("EdgeX: Dynamic Island discharge level=$level")
                    val text = if (level >= 0) "Z DISCHARGE $level%" else "Z DISCHARGE"
                    showIsland(context, text, Color.RED, baseSize, yOffset, slideDist, scale)
                }
                "com.fan.edgex.action.DYNAMIC_ISLAND_PREVIEW" -> {
                    val pText = intent.getStringExtra("text") ?: "Z PREVIEW 100%"
                    val pColor = intent.getIntExtra("color", Color.GREEN)
                    val pY = intent.getIntExtra("y", yOffset)
                    val pSize = intent.getIntExtra("size", baseSize)
                    val pSlide = intent.getIntExtra("slide", slideDist)
                    val pScale = intent.getFloatExtra("scale", scale)
                    showIsland(context, pText, pColor, pSize, pY, pSlide, pScale, isPreview = true)
                }
            }
        }
    }

    private fun getBatteryLevel(context: Context): Int {
        // Attempt 1: BatteryManager service (Standard property)
        try {
            val bm = context.getSystemService(Context.BATTERY_SERVICE) as? android.os.BatteryManager
            if (bm != null) {
                val pct = bm.getIntProperty(android.os.BatteryManager.BATTERY_PROPERTY_CAPACITY)
                if (pct in 0..100) return pct
            }
        } catch (_: Exception) {}

        // Attempt 2: Intent broadcast (Sticky intent)
        try {
            val batteryStatus: Intent? = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            if (batteryStatus != null) {
                val level = batteryStatus.getIntExtra(android.os.BatteryManager.EXTRA_LEVEL, -1)
                val scale = batteryStatus.getIntExtra(android.os.BatteryManager.EXTRA_SCALE, -1)
                if (level >= 0 && scale > 0) return (level * 100 / scale)
            }
        } catch (_: Exception) {}
        
        return -1
    }

    internal fun init(context: Context, repo: HookConfigRepository) {
        configRepository = repo
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
            addAction("com.fan.edgex.action.DYNAMIC_ISLAND_PREVIEW")
        }
        
        try {
            // Must be exported to receive PREVIEW broadcast from app process
            context.registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED)
        } catch (t: Throwable) {
            XposedBridge.log("EdgeX: registerReceiver failed: ${t.message}")
            try {
                context.registerReceiver(receiver, filter)
            } catch (_: Throwable) {}
        }
    }

    private fun showIsland(context: Context, text: String, color: Int, baseSize: Int, yOffset: Int, slideDist: Int, scale: Float = 1.0f, isPreview: Boolean = false) {
        handler.post {
            try {
                if (islandWindow == null) {
                    islandWindow = DynamicIslandWindow(context)
                }
                islandWindow?.show(text, color, baseSize, yOffset, slideDist, scale, isPreview)
            } catch (t: Throwable) {
                XposedBridge.log("EdgeX: Failed to show Dynamic Island: ${t.message}")
            }
        }
    }
}
