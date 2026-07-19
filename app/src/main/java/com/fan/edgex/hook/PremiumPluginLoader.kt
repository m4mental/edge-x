package com.fan.edgex.hook

import android.content.Context
import com.fan.edgex.premium.IPremiumPlugin

/**
 * Placeholder for the former premium plugin loader.
 * Premium features are now built-in.
 */
object PremiumPluginLoader {
    @Volatile
    var plugin: IPremiumPlugin? = null
        private set

    fun tryLoad() {
        // No-op: premium features are now built-in.
    }

    fun verifyDeviceBinding(context: Context) {
        // No-op: activation is no longer required.
    }

    fun retryChallengeIfNeeded(context: Context) {
        // No-op.
    }
}
