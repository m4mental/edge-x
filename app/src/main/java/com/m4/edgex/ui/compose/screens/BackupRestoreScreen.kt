package com.m4.edgex.ui.compose.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.m4.edgex.R
import com.m4.edgex.config.configPrefs
import com.m4.edgex.config.putConfigsSync
import com.m4.edgex.ui.compose.components.EdgeXDivider
import com.m4.edgex.ui.compose.components.EdgeXIcon
import com.m4.edgex.ui.compose.components.EdgeXIcons
import com.m4.edgex.ui.compose.components.EdgeXListGroup
import com.m4.edgex.ui.compose.components.EdgeXRow
import com.m4.edgex.ui.compose.components.EdgeXTopBar
import com.m4.edgex.ui.compose.theme.LocalEdgeXColors
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BackupRestoreScreen(
    onBack: () -> Unit,
    showToast: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val colors = LocalEdgeXColors.current

    val backupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json"),
        onResult = { uri ->
            uri?.let { performBackup(context, it, showToast) }
        }
    )

    val restoreLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let { performRestore(context, it, showToast) }
        }
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        EdgeXTopBar(title = stringResource(R.string.menu_backup_restore), onBack = onBack)
        
        EdgeXListGroup(modifier = Modifier.padding(16.dp)) {
            EdgeXRow(
                title = stringResource(R.string.btn_backup),
                subtitle = stringResource(R.string.backup_restore_desc),
                icon = EdgeXIcons.BackupRestore,
                onClick = {
                    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                    backupLauncher.launch("EdgeX_Backup_$timestamp.json")
                }
            ) {
                EdgeXIcon(EdgeXIcons.ChevronRight, contentDescription = null, tint = colors.onSurface)
            }
            EdgeXDivider()
            EdgeXRow(
                title = stringResource(R.string.btn_restore),
                subtitle = stringResource(R.string.backup_restore_desc),
                icon = EdgeXIcons.Restart,
                onClick = {
                    restoreLauncher.launch("application/json")
                }
            ) {
                EdgeXIcon(EdgeXIcons.ChevronRight, contentDescription = null, tint = colors.onSurface)
            }
        }
    }
}

private fun performBackup(context: Context, uri: Uri, showToast: (String) -> Unit) {
    runCatching {
        val prefs = context.configPrefs()
        val allEntries = prefs.all
        val json = JSONObject()
        allEntries.forEach { (key, value) ->
            json.put(key, value)
        }
        
        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            OutputStreamWriter(outputStream).use { writer ->
                writer.write(json.toString(4))
            }
        }
        showToast(context.getString(R.string.toast_backup_success))
    }.onFailure {
        showToast(context.getString(R.string.toast_backup_failed, it.message ?: "Unknown error"))
    }
}

private fun performRestore(context: Context, uri: Uri, showToast: (String) -> Unit) {
    runCatching {
        val jsonString = context.contentResolver.openInputStream(uri)?.use { inputStream ->
            inputStream.bufferedReader().use { it.readText() }
        } ?: throw Exception("Could not read file")
        
        val json = JSONObject(jsonString)
        val entries = mutableListOf<Pair<String, String>>()
        val keys = json.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val value = json.get(key).toString()
            entries.add(key to value)
        }
        
        if (context.putConfigsSync(*entries.toTypedArray())) {
            showToast(context.getString(R.string.toast_restore_success))
        } else {
            throw Exception("Failed to commit settings")
        }
    }.onFailure {
        showToast(context.getString(R.string.toast_restore_failed, it.message ?: "Unknown error"))
    }
}
