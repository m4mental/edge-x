package com.fan.edgex.ui.compose.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fan.edgex.R
import com.fan.edgex.config.AppConfig
import com.fan.edgex.config.getConfigBool
import com.fan.edgex.config.getConfigString
import com.fan.edgex.config.putConfig
import com.fan.edgex.ui.compose.components.EdgeXDivider
import com.fan.edgex.ui.compose.components.EdgeXListGroup
import com.fan.edgex.ui.compose.components.EdgeXRow
import com.fan.edgex.ui.compose.components.EdgeXSwitch
import com.fan.edgex.ui.compose.components.EdgeXTopBar
import com.fan.edgex.ui.compose.theme.LocalEdgeXColors

@Composable
fun DynamicIslandScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var enabled by remember { mutableStateOf(context.getConfigBool(AppConfig.DYNAMIC_ISLAND_ENABLED, false)) }
    var yOffset by remember { mutableStateOf(context.getConfigString(AppConfig.DYNAMIC_ISLAND_Y_OFFSET_DP, "10").toIntOrNull() ?: 10) }
    var size by remember { mutableStateOf(context.getConfigString(AppConfig.DYNAMIC_ISLAND_SIZE_DP, "30").toIntOrNull() ?: 30) }
    var slideDist by remember { mutableStateOf(context.getConfigString(AppConfig.DYNAMIC_ISLAND_SLIDE_DISTANCE_DP, "45").toIntOrNull() ?: 45) }
    var scale by remember { mutableStateOf(context.getConfigString(AppConfig.DYNAMIC_ISLAND_SCALE, "1.0").toFloatOrNull() ?: 1.0f) }
    var showPercentage by remember { mutableStateOf(context.getConfigBool(AppConfig.DYNAMIC_ISLAND_SHOW_PERCENTAGE, true)) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        EdgeXTopBar(title = stringResource(R.string.header_dynamic_island), onBack = onBack)
        
        EdgeXListGroup(modifier = Modifier.padding(16.dp)) {
            EdgeXRow(
                title = stringResource(R.string.dynamic_island_enabled),
                subtitle = stringResource(if (enabled) R.string.compose_enabled else R.string.compose_disabled),
            ) {
                EdgeXSwitch(
                    checked = enabled,
                    onCheckedChange = {
                        enabled = it
                        context.putConfig(AppConfig.DYNAMIC_ISLAND_ENABLED, it)
                        if (it) {
                            sendPreview(context, yOffset, size, slideDist, scale, showPercentage)
                        }
                    }
                )
            }
            EdgeXDivider()
            EdgeXRow(
                title = stringResource(R.string.dynamic_island_show_percentage),
                subtitle = stringResource(if (showPercentage) R.string.compose_enabled else R.string.compose_disabled),
            ) {
                EdgeXSwitch(
                    checked = showPercentage,
                    onCheckedChange = {
                        showPercentage = it
                        context.putConfig(AppConfig.DYNAMIC_ISLAND_SHOW_PERCENTAGE, it)
                        sendPreview(context, yOffset, size, slideDist, scale, it)
                    }
                )
            }
        }

        PremiumSectionLabel(stringResource(R.string.dynamic_island_section_appearance))
        EdgeXListGroup(modifier = Modifier.padding(16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.dynamic_island_pos_y) + ": $yOffset dp",
                    color = LocalEdgeXColors.current.onSurface,
                    fontSize = 14.sp
                )
                Slider(
                    value = yOffset.toFloat(),
                    onValueChange = { 
                        yOffset = it.toInt()
                        context.putConfig(AppConfig.DYNAMIC_ISLAND_Y_OFFSET_DP, yOffset.toString())
                        sendPreview(context, yOffset, size, slideDist, scale, showPercentage)
                    },
                    valueRange = -50f..150f
                )
            }
            EdgeXDivider()
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.dynamic_island_size) + ": $size dp",
                    color = LocalEdgeXColors.current.onSurface,
                    fontSize = 14.sp
                )
                Slider(
                    value = size.toFloat(),
                    onValueChange = { 
                        size = it.toInt()
                        context.putConfig(AppConfig.DYNAMIC_ISLAND_SIZE_DP, size.toString())
                        sendPreview(context, yOffset, size, slideDist, scale, showPercentage)
                    },
                    valueRange = 10f..80f
                )
            }
            EdgeXDivider()
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.dynamic_island_scale) + ": ${"%.1f".format(scale)}x",
                    color = LocalEdgeXColors.current.onSurface,
                    fontSize = 14.sp
                )
                Slider(
                    value = scale,
                    onValueChange = { 
                        scale = it
                        context.putConfig(AppConfig.DYNAMIC_ISLAND_SCALE, "%.2f".format(java.util.Locale.US, scale))
                        sendPreview(context, yOffset, size, slideDist, scale, showPercentage)
                    },
                    valueRange = 0.5f..2.0f
                )
            }
            EdgeXDivider()
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.dynamic_island_slide_dist) + ": $slideDist dp",
                    color = LocalEdgeXColors.current.onSurface,
                    fontSize = 14.sp
                )
                Slider(
                    value = slideDist.toFloat(),
                    onValueChange = { 
                        slideDist = it.toInt()
                        context.putConfig(AppConfig.DYNAMIC_ISLAND_SLIDE_DISTANCE_DP, slideDist.toString())
                        sendPreview(context, yOffset, size, slideDist, scale, showPercentage)
                    },
                    valueRange = 0f..100f
                )
            }
        }
    }
}

private fun sendPreview(context: android.content.Context, y: Int, size: Int, slide: Int, scale: Float, showPct: Boolean) {
    val intent = android.content.Intent("com.fan.edgex.action.DYNAMIC_ISLAND_PREVIEW").apply {
        putExtra("y", y)
        putExtra("size", size)
        putExtra("slide", slide)
        putExtra("scale", scale)
        putExtra("text", if (showPct) "Z PREVIEW 100%" else "Z PREVIEW")
        putExtra("color", android.graphics.Color.GREEN)
    }
    context.sendBroadcast(intent)
}

@Composable
private fun PremiumSectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        modifier = Modifier.padding(start = 20.dp, top = 8.dp, bottom = 4.dp),
        color = LocalEdgeXColors.current.onSurfaceDim,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        letterSpacing = 1.sp
    )
}
