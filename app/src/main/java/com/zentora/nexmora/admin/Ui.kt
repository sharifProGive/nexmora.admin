package com.zentora.nexmora.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

val NxBg = Color(0xFF0B1220)
val NxPanel = Color(0xFF131E33)
val NxPanelHi = Color(0xFF1B2A47)
val NxAccent = Color(0xFF4F8CFF)
val NxGood = Color(0xFF36D399)
val NxBad = Color(0xFFFF6B7A)
val NxWarn = Color(0xFFF5B642)
val NxText = Color(0xFFF2F5FA)
val NxMuted = Color(0xFF9AA8C2)

@Composable
fun NxTheme(lang: Lang, content: @Composable () -> Unit) {
    val scheme = darkColorScheme(
        primary = NxAccent,
        onPrimary = Color.White,
        background = NxBg,
        onBackground = NxText,
        surface = NxPanel,
        onSurface = NxText,
        surfaceVariant = NxPanelHi,
        onSurfaceVariant = NxMuted,
        error = NxBad,
        outline = Color(0xFF3A4A6B)
    )
    CompositionLocalProvider(LocalLang provides lang) {
        MaterialTheme(colorScheme = scheme, content = content)
    }
}

@Composable
fun Page(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NxBg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        content = content
    )
}

@Composable
fun Heading(text: String) {
    Text(text, color = NxText, fontSize = 24.sp, fontWeight = FontWeight.Bold, lineHeight = 30.sp)
}

@Composable
fun Body(text: String, color: Color = NxMuted) {
    Text(text, color = color, fontSize = 15.sp, lineHeight = 22.sp)
}

@Composable
fun ErrorLine(error: Tx?) {
    if (error != null) Text(error.t(), color = NxBad, fontSize = 15.sp, lineHeight = 21.sp)
}

@Composable
fun Panel(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = modifier
            .background(NxPanel, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        content = content
    )
}

@Composable
fun StatTile(label: String, value: String, modifier: Modifier = Modifier) {
    Panel(modifier) {
        Text(value, color = NxText, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text(label, color = NxMuted, fontSize = 13.sp, lineHeight = 17.sp)
    }
}

@Composable
fun KeyValue(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = NxMuted, fontSize = 14.sp)
        Text(value, color = NxText, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun NxField(
    value: String,
    onChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    password: Boolean = false,
    numeric: Boolean = false,
    placeholder: String = "",
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        placeholder = if (placeholder.isEmpty()) null else ({ Text(placeholder) }),
        singleLine = true,
        enabled = enabled,
        visualTransformation = if (password) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(
            keyboardType = when {
                numeric -> KeyboardType.Number
                password -> KeyboardType.Password
                else -> KeyboardType.Uri
            }
        ),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun PrimaryButton(text: String, onClick: () -> Unit, enabled: Boolean = true, busy: Boolean = false) {
    Button(
        onClick = onClick,
        enabled = enabled && !busy,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
    ) {
        if (busy) {
            CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp, color = Color.White)
        } else {
            Text(text, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun LanguagePicker(current: Lang, onPick: (Lang) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Lang.entries.forEach { item ->
            FilterChip(selected = item == current, onClick = { onPick(item) }, label = { Text(item.label) })
        }
    }
}

fun fmtNum(value: Long): String = String.format(Locale.US, "%,d", value)

fun fmtBytes(bytes: Long): String {
    if (bytes < 1024) return "$bytes B"
    val units = arrayOf("KB", "MB", "GB", "TB")
    var size = bytes.toDouble()
    var index = -1
    while (size >= 1024 && index < units.size - 1) {
        size /= 1024
        index += 1
    }
    return String.format(Locale.US, "%.1f %s", size, units[index])
}

fun fmtDuration(seconds: Long): String {
    val d = seconds / 86400
    val h = (seconds % 86400) / 3600
    val m = (seconds % 3600) / 60
    return when {
        d > 0 -> "${d}d ${h}h"
        h > 0 -> "${h}h ${m}m"
        else -> "${m}m"
    }
}
