package com.m4.edgex.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import com.m4.edgex.config.FreezerBootstrap
import com.m4.edgex.config.ModuleActivationState
import com.m4.edgex.config.broadcastFullConfigSnapshot
import com.m4.edgex.config.syncRuntimeEnableFlagsFromConfiguredActions
import com.m4.edgex.ui.compose.EdgeXApp

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }
        syncRuntimeEnableFlagsFromConfiguredActions()
        ModuleActivationState.requestRefresh(this)
        broadcastFullConfigSnapshot()
        FreezerBootstrap.ensureMigrated(this)
        setContent {
            EdgeXApp()
        }
    }
}
