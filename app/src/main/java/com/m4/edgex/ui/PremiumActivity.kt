package com.m4.edgex.ui

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.m4.edgex.R
import com.m4.edgex.license.PremiumActivator

class PremiumActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_premium)
        ThemeManager.applyToActivity(this)

        findViewById<View>(R.id.header_container).setOnApplyWindowInsetsListener { view, insets ->
            view.setPadding(
                view.paddingLeft,
                insets.getInsets(android.view.WindowInsets.Type.statusBars()).top,
                view.paddingRight,
                view.paddingBottom,
            )
            insets
        }
        findViewById<View>(R.id.btn_back).setOnClickListener { finish() }

        findViewById<View>(R.id.item_fluid_effect).setOnClickListener {
            startActivity(Intent(this, FluidEffectSettingsActivity::class.java))
        }

        refreshStatus()
    }

    override fun onResume() {
        super.onResume()
        ThemeManager.applyToActivity(this)
        refreshStatus()
    }

    private fun refreshStatus() {
        val accentColor = ThemeManager.currentAccent(this)
        
        val iconBg = findViewById<FrameLayout>(R.id.icon_status_bg)
        iconBg.background = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(accentColor)
        }
        findViewById<ImageView>(R.id.icon_status).apply {
            setImageResource(R.drawable.ic_supporter_extra)
            setColorFilter(ThemeManager.onAccentColor(accentColor))
        }
        findViewById<TextView>(R.id.text_status).setText(R.string.premium_status_active)

        val codeView = findViewById<TextView>(R.id.text_activation_code)
        codeView.visibility = View.VISIBLE
        codeView.text = getString(R.string.premium_desc_not_activated)

        findViewById<Button>(R.id.btn_activate).visibility = View.GONE
        findViewById<Button>(R.id.btn_deactivate).visibility = View.GONE
        findViewById<TextView>(R.id.text_dex_info).visibility = View.GONE
        findViewById<Button>(R.id.btn_update).visibility = View.GONE
    }
}
