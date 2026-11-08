package com.zentora.nexmora.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private class Stat(val label: Tx, val key: String, val bytes: Boolean = false)

private val stats = listOf(
    Stat(S.stUsers, "users"),
    Stat(S.stNew, "newUsers24h"),
    Stat(S.stVideos, "videos"),
    Stat(S.stComments, "comments"),
    Stat(S.stMedia, "mediaFiles"),
    Stat(S.stStorage, "mediaBytes", true),
    Stat(S.stAds, "ads"),
    Stat(S.stFriends, "friendLinks"),
    Stat(S.stRequests, "friendRequests"),
    Stat(S.stChats, "chatMessages"),
    Stat(S.stCalls, "calls"),
    Stat(S.stLedger, "ledgerBlocks"),
    Stat(S.stBanned, "banned"),
    Stat(S.stDelAdmin, "deletedByAdmin"),
    Stat(S.stDelUser, "deletedByUser"),
    Stat(S.stPurged, "purgedTotal"),
    Stat(S.stDb, "dbBytes", true)
)

@Composable
fun HomeScreen(vm: AppVm) {
    var tab by rememberSaveable { mutableIntStateOf(0) }
    Scaffold(
        containerColor = NxBg,
        bottomBar = {
            NavigationBar(containerColor = NxPanel) {
                NavigationBarItem(
                    selected = tab == 0,
                    onClick = { tab = 0 },
                    icon = { Text("📊", fontSize = 20.sp) },
                    label = { Text(S.tabOverview.t()) }
                )
                NavigationBarItem(
                    selected = tab == 1,
                    onClick = { tab = 1 },
                    icon = { Text("⚙️", fontSize = 20.sp) },
                    label = { Text(S.tabSettings.t()) }
                )
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            if (tab == 0) OverviewTab(vm) else SettingsTab(vm)
        }
    }
}

@Composable
private fun OverviewTab(vm: AppVm) {
    LaunchedEffect(Unit) {
        if (vm.overview == null) vm.refresh()
    }
    val data = vm.overview
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Heading(S.tabOverview.t())
                if (data != null) {
                    val stamp = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(data.loadedAt))
                    Text(S.updatedAt.t() + ": " + stamp, color = NxMuted, fontSize = 12.sp)
                }
            }
            OutlinedButton(onClick = { vm.refresh() }, enabled = !vm.loading) { Text(S.refresh.t()) }
        }
        if (vm.loading) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
            Body(S.waking.t())
        }
        ErrorLine(vm.loadError)
        if (data != null) {
            stats.chunked(2).forEach { row ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    row.forEach { item ->
                        val raw = data.long(item.key)
                        StatTile(item.label.t(), if (item.bytes) fmtBytes(raw) else fmtNum(raw), Modifier.weight(1f))
                    }
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
            }
            Panel(Modifier.fillMaxWidth()) {
                Text(S.serverSection.t(), color = NxText, fontSize = 16.sp)
                KeyValue(S.uptime.t(), fmtDuration(data.server("uptimeSeconds")))
                KeyValue(S.memory.t(), fmtBytes(data.server("memoryBytes")))
                KeyValue(S.nodeVersion.t(), data.serverText("node"))
            }
        }
    }
}

@Composable
private fun SettingsTab(vm: AppVm) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Heading(S.tabSettings.t())
        Panel(Modifier.fillMaxWidth()) {
            Text(S.language.t(), color = NxText, fontSize = 16.sp)
            LanguagePicker(vm.lang) { vm.setLang(it) }
        }
        Panel(Modifier.fillMaxWidth()) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(S.screenProtect.t(), color = NxText, fontSize = 16.sp, modifier = Modifier.weight(1f))
                Switch(checked = vm.screenProtect, onCheckedChange = { vm.setScreenProtect(it) })
            }
            Text(S.screenProtectHint.t(), color = NxMuted, fontSize = 13.sp, lineHeight = 18.sp)
        }
        Panel(Modifier.fillMaxWidth()) {
            KeyValue(S.connectedTo.t(), vm.serverHost())
            KeyValue(S.version.t(), "1.0.0")
        }
        Body(S.codeNote.t())
        PrimaryButton(S.lockNow.t(), onClick = { vm.lock() })
    }
}
