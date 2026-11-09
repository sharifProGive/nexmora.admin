package com.zentora.nexmora.admin

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONObject

@Composable
fun ControlTab(vm: AppVm) {
    var page by rememberSaveable { mutableIntStateOf(-1) }
    BackHandler(enabled = page >= 0) { page = -1 }
    when (page) {
        0 -> ArchivePage(vm) { page = -1 }
        1 -> BansPage(vm) { page = -1 }
        2 -> ActionsPage(vm) { page = -1 }
        3 -> LedgerPage(vm) { page = -1 }
        4 -> VaultsPage(vm) { page = -1 }
        else -> ControlMenu { page = it }
    }
}

@Composable
private fun MenuCard(icon: String, title: Tx, desc: Tx, onClick: () -> Unit) {
    Panel(Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(icon, fontSize = 26.sp)
            Column {
                Text(title.t(), color = NxText, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                Text(desc.t(), color = NxMuted, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun ControlMenu(onOpen: (Int) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Heading(C.tabControl.t())
        MenuCard("🗂", C.menuArchive, C.menuArchiveDesc) { onOpen(0) }
        MenuCard("⛔", C.menuBans, C.menuBansDesc) { onOpen(1) }
        MenuCard("📜", C.menuActions, C.menuActionsDesc) { onOpen(2) }
        MenuCard("🔗", C.menuLedger, C.menuLedgerDesc) { onOpen(3) }
        MenuCard("🏛", C.menuVaults, C.menuVaultsDesc) { onOpen(4) }
    }
}

@Composable
private fun PageFrame(
    title: String,
    onBack: () -> Unit,
    pager: Pager<JSONObject>,
    header: @Composable () -> Unit = {},
    row: @Composable (JSONObject) -> Unit
) {
    LaunchedEffect(Unit) {
        if (!pager.loaded) pager.reload()
    }
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack) { Text("← " + S.back.t()) }
            Spacer(Modifier.weight(1f))
            OutlinedButton(onClick = { pager.reload() }, enabled = !pager.loading) { Text(S.refresh.t()) }
        }
        Heading(title)
        header()
        ErrorLine(pager.error)
        if (pager.loading && pager.items.isEmpty()) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
            Body(S.waking.t())
        }
        LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(pager.items) { entry -> row(entry) }
            item {
                if (pager.loaded && !pager.loading && pager.items.isEmpty()) Body(C.empty.t())
                if (pager.next != null) {
                    OutlinedButton(
                        onClick = { pager.more() },
                        enabled = !pager.loading,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text(U.loadMore.t()) }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

private fun nameOf(e: JSONObject): String =
    e.str("username").ifBlank { e.str("handle").ifBlank { e.str("nexoraId") } }

@Composable
private fun ArchivePage(vm: AppVm, onBack: () -> Unit) {
    val control = vm.users.control
    var restoreId by remember { mutableStateOf<Long?>(null) }
    var restoreName by remember { mutableStateOf("") }
    PageFrame(C.menuArchive.t(), onBack, control.archive) { e ->
        Panel(Modifier.fillMaxWidth()) {
            Text(nameOf(e), color = NxText, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Text(e.str("handle"), color = NxMuted, fontSize = 13.sp)
            Text(
                (if (e.str("kind") == "self") C.kindSelf.t() else C.kindAdmin.t()) + " · " +
                    C.deletedOn.t() + ": " + fmtTime(e.lng("deletedAt")),
                color = NxMuted, fontSize = 12.sp
            )
            if (e.lng("purgeAt") > 0L) {
                Text(C.autoPurge.t() + ": " + fmtTime(e.lng("purgeAt")), color = NxWarn, fontSize = 12.sp)
            }
            if (e.str("reason").isNotBlank()) {
                Text(C.reason.t() + ": " + e.str("reason"), color = NxText, fontSize = 13.sp)
            }
            if (e.lng("restoredAt") > 0L) {
                Text(C.restoredOn.t() + ": " + fmtTime(e.lng("restoredAt")), color = NxGood, fontSize = 13.sp)
            } else {
                OutlinedButton(onClick = {
                    control.clearActionError()
                    restoreName = nameOf(e)
                    restoreId = e.lng("id")
                }) { Text("♻️ " + C.restoreBtn.t()) }
            }
        }
    }
    val rid = restoreId
    if (rid != null) {
        DangerDialog(
            kind = Danger.RESTORE,
            name = restoreName,
            withReason = false,
            busy = control.actionBusy,
            error = control.actionError,
            onConfirm = { password, _ ->
                control.restore(rid, password) {
                    restoreId = null
                    control.archive.reload()
                    vm.refresh()
                }
            },
            onDismiss = { restoreId = null }
        )
    }
}

@Composable
private fun BansPage(vm: AppVm, onBack: () -> Unit) {
    val control = vm.users.control
    var unbanId by remember { mutableStateOf<String?>(null) }
    var unbanName by remember { mutableStateOf("") }
    PageFrame(C.menuBans.t(), onBack, control.bans) { e ->
        Panel(Modifier.fillMaxWidth()) {
            Text(nameOf(e), color = NxText, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Text(e.str("handle"), color = NxMuted, fontSize = 13.sp)
            Text(C.bannedOn.t() + ": " + fmtTime(e.lng("bannedAt")), color = NxMuted, fontSize = 12.sp)
            if (e.str("reason").isNotBlank()) {
                Text(C.reason.t() + ": " + e.str("reason"), color = NxText, fontSize = 13.sp)
            }
            OutlinedButton(onClick = {
                control.clearActionError()
                unbanName = nameOf(e)
                unbanId = e.str("nexoraId")
            }) { Text("✅ " + C.unbanBtn.t()) }
        }
    }
    val uid = unbanId
    if (uid != null) {
        DangerDialog(
            kind = Danger.UNBAN,
            name = unbanName,
            withReason = false,
            busy = control.actionBusy,
            error = control.actionError,
            onConfirm = { password, _ ->
                control.unban(uid, password) {
                    unbanId = null
                    control.bans.reload()
                    vm.refresh()
                }
            },
            onDismiss = { unbanId = null }
        )
    }
}

@Composable
private fun ActionsPage(vm: AppVm, onBack: () -> Unit) {
    PageFrame(C.menuActions.t(), onBack, vm.users.control.actions) { e ->
        Panel(Modifier.fillMaxWidth()) {
            Text(e.str("action"), color = NxText, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Text(e.str("target"), color = NxMuted, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (e.str("detail").isNotBlank()) {
                Text(e.str("detail"), color = NxMuted, fontSize = 12.sp, maxLines = 3, overflow = TextOverflow.Ellipsis)
            }
            Text(fmtTime(e.lng("at")), color = NxMuted, fontSize = 12.sp)
        }
    }
}

@Composable
private fun LedgerPage(vm: AppVm, onBack: () -> Unit) {
    val control = vm.users.control
    PageFrame(
        C.menuLedger.t(), onBack, control.ledger,
        header = {
            Panel(Modifier.fillMaxWidth()) {
                Button(
                    onClick = { control.verifyLedger() },
                    enabled = control.verifyState != 1,
                    modifier = Modifier.fillMaxWidth()
                ) { Text(C.verifyBtn.t()) }
                when (control.verifyState) {
                    1 -> Body(C.verifying.t())
                    2 -> Text(C.chainValid.t(), color = NxGood, fontSize = 14.sp)
                    3 -> Text(C.chainBroken.t(), color = NxBad, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    else -> Unit
                }
                ErrorLine(control.verifyError)
            }
        }
    ) { e ->
        Panel(Modifier.fillMaxWidth()) {
            Text(
                "#" + e.lng("seq") + " · " + e.str("vault") + " · " + e.str("event"),
                color = NxText, fontSize = 14.sp, fontWeight = FontWeight.SemiBold
            )
            Text(fmtTime(e.lng("createdAt")), color = NxMuted, fontSize = 12.sp)
            Text(e.str("hash").take(24), color = NxMuted, fontSize = 11.sp)
        }
    }
}

@Composable
private fun VaultsPage(vm: AppVm, onBack: () -> Unit) {
    PageFrame(C.menuVaults.t(), onBack, vm.users.control.vaults) { e ->
        Panel(Modifier.fillMaxWidth()) {
            Text(
                "Vault " + e.lng("no").toString().padStart(2, '0') + " — " + e.str("name"),
                color = NxText, fontSize = 14.sp, fontWeight = FontWeight.SemiBold
            )
            Text(
                fmtNum(e.lng("records")) + " " + C.vaultRecords.t() + " · " +
                    fmtNum(e.lng("ops")) + " " + C.vaultOps.t() + " · " +
                    fmtNum(e.lng("errors")) + " " + C.vaultErrors.t(),
                color = if (e.lng("errors") > 0L) NxWarn else NxMuted, fontSize = 12.sp
            )
            Text(C.lastOp.t() + ": " + fmtTime(e.lng("lastOpAt")), color = NxMuted, fontSize = 12.sp)
        }
    }
}
