package com.m4.edgex.ui.compose.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.m4.edgex.R
import com.m4.edgex.license.PremiumActivator
import com.m4.edgex.ui.compose.components.EdgeXDivider
import com.m4.edgex.ui.compose.components.EdgeXIcon
import com.m4.edgex.ui.compose.components.EdgeXIcons
import com.m4.edgex.ui.compose.components.EdgeXListGroup
import com.m4.edgex.ui.compose.components.EdgeXRow
import com.m4.edgex.ui.compose.components.EdgeXTopBar
import com.m4.edgex.ui.compose.theme.EdgeXRadius
import com.m4.edgex.ui.compose.theme.LocalEdgeXColors

@Composable
fun PremiumScreen(
    onBack: () -> Unit,
    onOpenFluidEffect: () -> Unit,
    onOpenDynamicIsland: () -> Unit,
    showToast: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        EdgeXTopBar(title = stringResource(R.string.compose_premium_title), onBack = onBack)
        SpecialFeatureHero()
        PremiumSectionLabel(stringResource(R.string.compose_premium_features))
        EdgeXListGroup(modifier = Modifier.padding(16.dp)) {
            val rows = premiumRows(
                onOpenFluidEffect = onOpenFluidEffect,
                onOpenDynamicIsland = onOpenDynamicIsland,
            )
            rows.forEachIndexed { index, row ->
                EdgeXRow(title = row.title, subtitle = row.subtitle, icon = row.icon, onClick = row.onClick) {
                    EdgeXIcon(EdgeXIcons.ChevronRight, contentDescription = null, tint = LocalEdgeXColors.current.onSurface)
                }
                if (index != rows.lastIndex) EdgeXDivider()
            }
        }
        PremiumSectionLabel(stringResource(R.string.compose_support_methods))
        SupportGrid(showToast = showToast)
        Spacer(modifier = Modifier.height(28.dp))
    }
}

@Composable
private fun SpecialFeatureHero() {
    val colors = LocalEdgeXColors.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 4.dp, end = 16.dp, bottom = 20.dp),
        shape = RoundedCornerShape(EdgeXRadius.xl),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1E14)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.radialGradient(
                        listOf(colors.accent.copy(alpha = 0.32f), Color.Transparent),
                        center = androidx.compose.ui.geometry.Offset(520f, 20f),
                        radius = 280f,
                    ),
                )
                .fillMaxWidth()
                .padding(24.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(colors.onAccentSoft)
                        .padding(horizontal = 12.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF4CAF50)),
                    )
                    Text(stringResource(R.string.compose_premium_status_installed), color = colors.accentSoft, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Text(
                    stringResource(R.string.compose_premium_hero),
                    color = Color(0xFFF4F0E8),
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    lineHeight = 35.sp,
                )
                Text(
                    stringResource(R.string.compose_premium_hero_subtitle),
                    color = Color(0xFFF4F0E8).copy(alpha = 0.78f),
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }
        }
    }
}

@Composable
private fun SupportGrid(showToast: (String) -> Unit) {
    val context = LocalContext.current
    val colors = LocalEdgeXColors.current
    val methods = listOf(
        MethodItem(stringResource(R.string.donate_alipay), stringResource(R.string.compose_scan_donate), EdgeXIcons.Alipay, 28.dp),
        MethodItem(stringResource(R.string.donate_wechat), stringResource(R.string.compose_scan_donate), EdgeXIcons.WechatPay, 28.dp),
        MethodItem(stringResource(R.string.donate_kofi), stringResource(R.string.compose_donate_click_jump), EdgeXIcons.KoFi, 26.dp),
        MethodItem(stringResource(R.string.donate_crypto), stringResource(R.string.compose_donate_view_address), EdgeXIcons.Eth, 25.dp),
    )

    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MethodCard(methods[0], modifier = Modifier.weight(1f)) {
                showToast("Alipay QR coming soon")
            }
            MethodCard(methods[1], modifier = Modifier.weight(1f)) {
                showToast("WeChat QR coming soon")
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MethodCard(methods[2], modifier = Modifier.weight(1f)) {
                context.openUrl("https://ko-fi.com/fantasy1999")
            }
            MethodCard(methods[3], modifier = Modifier.weight(1f)) {
                showToast("ETH: 0x... SOL: ...")
            }
        }
    }
}

private data class MethodItem(val title: String, val subtitle: String, val icon: Int, val iconSize: Dp)

@Composable
private fun MethodCard(item: MethodItem, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .aspectRatio(1.6f)
            .clickable { onClick() },
        shape = RoundedCornerShape(EdgeXRadius.lg),
        colors = CardDefaults.cardColors(containerColor = LocalEdgeXColors.current.surface),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.04f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(item.icon),
                contentDescription = null,
                modifier = Modifier.size(item.iconSize)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(item.title, color = LocalEdgeXColors.current.onSurface, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(item.subtitle, color = LocalEdgeXColors.current.onSurfaceDim, fontSize = 10.sp)
        }
    }
}

@Composable
fun AboutScreen(
    onBack: () -> Unit,
    showToast: (String) -> Unit,
    onOpenTheme: () -> Unit,
    onCheckForUpdates: () -> Unit,
    onOpenBackupRestore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        EdgeXTopBar(title = stringResource(R.string.menu_about), onBack = onBack)
        AboutHeader()
        PremiumSectionLabel(stringResource(R.string.compose_section_about))
        EdgeXListGroup(modifier = Modifier.padding(16.dp)) {
            EdgeXRow(
                title = stringResource(R.string.label_project_url),
                subtitle = stringResource(R.string.value_project_url),
                icon = EdgeXIcons.Link,
                onClick = { context.openUrl("https://" + context.getString(R.string.value_project_url)) }
            )
            EdgeXDivider()
            EdgeXRow(
                title = stringResource(R.string.compose_developer),
                subtitle = stringResource(R.string.value_author),
                icon = EdgeXIcons.Person,
                onClick = { }
            )
            EdgeXDivider()
            EdgeXRow(
                title = stringResource(R.string.menu_theme),
                subtitle = stringResource(R.string.compose_home_theme_meta),
                icon = EdgeXIcons.Theme,
                onClick = onOpenTheme
            )
            EdgeXDivider()
            EdgeXRow(
                title = stringResource(R.string.menu_backup_restore),
                subtitle = stringResource(R.string.backup_restore_desc),
                icon = EdgeXIcons.BackupRestore,
                onClick = onOpenBackupRestore
            )
            EdgeXDivider()
            EdgeXRow(
                title = stringResource(R.string.update_checking),
                subtitle = stringResource(R.string.compose_version_info, stringResource(R.string.value_version)),
                icon = EdgeXIcons.Restart,
                onClick = onCheckForUpdates
            )
        }
        Spacer(modifier = Modifier.height(28.dp))
    }
}

@Composable
private fun AboutHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(EdgeXRadius.xl))
                .background(LocalEdgeXColors.current.surface),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = null,
                modifier = Modifier.size(64.dp)
            )
        }
        Text(
            stringResource(R.string.app_name),
            color = LocalEdgeXColors.current.onSurface,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )
        Text(
            stringResource(R.string.compose_version_info, stringResource(R.string.value_version)),
            color = LocalEdgeXColors.current.onSurfaceDim,
            fontSize = 14.sp
        )
    }
}

private data class PremiumRow(val title: String, val subtitle: String, val icon: Int, val onClick: () -> Unit)

@Composable
private fun premiumRows(
    onOpenFluidEffect: () -> Unit,
    onOpenDynamicIsland: () -> Unit,
): List<PremiumRow> =
    listOf(
        PremiumRow(
            title = stringResource(R.string.header_fluid_effect),
            subtitle = stringResource(R.string.menu_fluid_effect_desc),
            icon = EdgeXIcons.FluidEffect,
            onClick = onOpenFluidEffect,
        ),
        PremiumRow(
            title = stringResource(R.string.header_dynamic_island),
            subtitle = stringResource(R.string.menu_dynamic_island_desc),
            icon = EdgeXIcons.Sparkle,
            onClick = onOpenDynamicIsland,
        ),
    )

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

private fun Context.openUrl(url: String) {
    runCatching {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}
