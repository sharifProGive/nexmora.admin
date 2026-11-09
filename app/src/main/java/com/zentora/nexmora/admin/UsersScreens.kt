package com.zentora.nexmora.admin

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal fun fmtTime(ms: Long): String =
    if (ms <= 0L) "—" else SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(ms))

private class StatRow(val label: Tx, val key: String, val bytes: Boolean = false)

private val statRows = listOf(
    StatRow(U.stVideos, "videos"),
    StatRow(U.stViews, "views"),
    StatRow(U.stLikesRecv, "likesReceived"),
    StatRow(U.stComments, "commentsWritten"),
    StatRow(U.stLikesGiven, "likesGiven"),
    StatRow(U.stDislikesGiven, "dislikesGiven"),
    StatRow(U.stFriends, "friends"),
    StatRow(U.stReqRecv, "requestsReceived"),
    StatRow(U.stReqSent, "requestsSent"),
    StatRow(U.stChatSent, "chatMessagesSent"),
    StatRow(U.stChatRecv, "chatMessagesReceived"),
    StatRow(U.stCallsMade, "callsMade"),
    StatRow(U.stCallsRecv, "callsReceived"),
    StatRow(U.stMediaFiles, "mediaFiles"),
    StatRow(U.stMediaBytes, "mediaBytes", true)
)

@Composable
fun UsersTab(vm: AppVm) {
    val users = vm.users
    BackHandler(enabled = users.selected != null) { users.closeProfile() }
    if (users.selected != null) ProfileScreen(vm) else UserList(vm)
}

@Composable
private fun Avatar(name: String, size: Int = 44) {
    val initial = name.trim().firstOrNull()?.uppercase() ?: "?"
    Box(
        modifier = Modifier.size(size.dp).background(NxPanelHi, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(initial, color = NxAccent, fontSize = (size / 2.2).sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun UserList(vm: AppVm) {
    val users = vm.users
    LaunchedEffect(Unit) {
        if (!users.loaded) users.loadFirst()
    }
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Spacer(Modifier.height(8.dp))
        Heading(U.tabUsers.t())
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = users.query,
                onValueChange = { users.query = it },
                placeholder = { Text(U.searchHint.t()) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { users.loadFirst() }),
                modifier = Modifier.weight(1f)
            )
            Button(onClick = { users.loadFirst() }, enabled = !users.loading) { Text(U.searchBtn.t()) }
        }
        ErrorLine(users.error)
        if (users.loading && users.rows.isEmpty()) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
            Body(S.waking.t())
        }
        LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(users.rows, key = { it.nexoraId }) { row ->
                UserCard(row) { users.openProfile(row.nexoraId) }
            }
            item {
                if (users.loaded && !users.loading && users.rows.isEmpty()) Body(U.noUsers.t())
                if (users.next != null) {
                    OutlinedButton(
                        onClick = { users.loadMore() },
                        enabled = !users.loading,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text(U.loadMore.t()) }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun UserCard(row: UserRow, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(NxPanel, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Avatar(row.title)
        Column(Modifier.weight(1f)) {
            Text(
                row.title, color = NxText, fontSize = 16.sp, fontWeight = FontWeight.SemiBold,
                maxLines = 1, overflow = TextOverflow.Ellipsis
            )
            Text(
                row.handle.ifBlank { row.email }, color = NxMuted, fontSize = 13.sp,
                maxLines = 1, overflow = TextOverflow.Ellipsis
            )
            Text(U.joined.t() + ": " + fmtTime(row.createdAt), color = NxMuted, fontSize = 12.sp)
        }
        if (row.banned) Text(U.bannedTag.t(), color = NxBad, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SectionPanel(title: String, content: @Composable () -> Unit) {
    Panel(Modifier.fillMaxWidth()) {
        Text(title, color = NxText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        content()
    }
}

@Composable
private fun FieldLine(label: String, value: String) {
    Column(Modifier.fillMaxWidth()) {
        Text(label, color = NxMuted, fontSize = 12.sp)
        Text(value, color = NxText, fontSize = 15.sp, lineHeight = 21.sp)
    }
}

@Composable
private fun ProfileScreen(vm: AppVm) {
    val users = vm.users
    val control = users.control
    val data = users.profile
    var ask by remember { mutableStateOf(false) }
    var danger by remember { mutableStateOf<Danger?>(null) }
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TextButton(onClick = { users.closeProfile() }) { Text("← " + S.back.t()) }
        if (users.profileLoading) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
            Body(S.waking.t())
        }
        ErrorLine(users.profileError)
        if (data != null) {
            val user = data.user
            val name = user.str("username").ifBlank { user.str("handle").ifBlank { user.str("nexoraId") } }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Avatar(name, 64)
                Column {
                    Text(name, color = NxText, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(user.str("handle"), color = NxMuted, fontSize = 14.sp)
                }
            }

            SectionPanel(U.secAccount.t()) {
                FieldLine(U.emailLabel.t(), user.str("email").ifBlank { "—" })
                FieldLine(U.idLabel.t(), user.str("nexoraId"))
                FieldLine(U.joined.t(), fmtTime(user.lng("createdAt")))
                FieldLine(U.lastSeen.t(), fmtTime(user.lng("lastSeenAt")))
                if (data.banned) {
                    FieldLine(U.statusLabel.t(), U.statusBanned.t())
                    FieldLine(U.bannedSince.t(), fmtTime(data.status.lng("since")))
                    FieldLine(U.reason.t(), data.status.str("reason").ifBlank { "—" })
                } else {
                    FieldLine(U.statusLabel.t(), U.statusActive.t())
                }
            }

            SectionPanel(C.secManage.t()) {
                if (data.banned) {
                    OutlinedButton(
                        onClick = {
                            control.clearActionError()
                            danger = Danger.UNBAN
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("✅ " + C.unbanBtn.t()) }
                } else {
                    OutlinedButton(
                        onClick = {
                            control.clearActionError()
                            danger = Danger.BAN
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NxWarn),
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("⛔ " + C.banBtn.t()) }
                }
                OutlinedButton(
                    onClick = {
                        control.clearActionError()
                        danger = Danger.DELETE
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NxBad),
                    modifier = Modifier.fillMaxWidth()
                ) { Text("🗑 " + C.deleteBtn.t()) }
            }

            SectionPanel(U.secStats.t()) {
                statRows.forEach { item ->
                    val raw = data.stats.lng(item.key)
                    KeyValue(item.label.t(), if (item.bytes) fmtBytes(raw) else fmtNum(raw))
                }
            }

            SectionPanel(U.secPrivate.t()) {
                Body(U.privateWarn.t())
                if (users.privateShown) {
                    FieldLine(U.phone.t(), users.phone.ifBlank { "—" })
                    FieldLine(U.billing.t(), users.billing.ifBlank { "—" })
                    OutlinedButton(onClick = { users.hidePrivate() }) { Text(U.hide.t()) }
                } else {
                    ErrorLine(users.privateError)
                    PrimaryButton(U.showPrivate.t(), onClick = { ask = true }, busy = users.privateLoading)
                }
            }

            SectionPanel(U.secVideos.t()) {
                if (data.videos.isEmpty()) Body("—")
                data.videos.forEach { v ->
                    Column(Modifier.fillMaxWidth()) {
                        Text(v.str("title").ifBlank { "—" }, color = NxText, fontSize = 15.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        Text(
                            v.str("visibility") + " · " + fmtNum(v.lng("views")) + " " + U.views.t() +
                                " · " + fmtNum(v.lng("likes")) + " " + U.likes.t() + " · " + fmtTime(v.lng("createdAt")),
                            color = NxMuted, fontSize = 12.sp
                        )
                    }
                }
            }

            SectionPanel(U.secComments.t()) {
                if (data.comments.isEmpty()) Body("—")
                data.comments.forEach { c ->
                    Column(Modifier.fillMaxWidth()) {
                        Text(c.str("body").ifBlank { "—" }, color = NxText, fontSize = 15.sp, maxLines = 3, overflow = TextOverflow.Ellipsis)
                        Text(fmtNum(c.lng("likes")) + " " + U.likes.t() + " · " + fmtTime(c.lng("createdAt")), color = NxMuted, fontSize = 12.sp)
                    }
                }
            }

            SectionPanel(U.secFriends.t()) {
                if (data.friends.isEmpty()) Body("—")
                data.friends.forEach { f ->
                    val friendName = f.str("username").ifBlank { f.str("handle").ifBlank { f.str("nexoraId") } }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { users.openProfile(f.str("nexoraId")) }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Avatar(friendName, 36)
                        Column {
                            Text(friendName, color = NxText, fontSize = 15.sp)
                            Text(f.str("handle"), color = NxMuted, fontSize = 12.sp)
                        }
                    }
                }
            }

            SectionPanel(U.secCalls.t()) {
                if (data.calls.isEmpty()) Body("—")
                data.calls.forEach { c ->
                    val direction = if (c.str("direction") == "out") U.callOut.t() else U.callIn.t()
                    val kind = if (c.optBoolean("video", false)) U.videoCall.t() else U.voiceCall.t()
                    Column(Modifier.fillMaxWidth()) {
                        Text("$kind · $direction · ${c.str("outcome")}", color = NxText, fontSize = 15.sp)
                        Text(
                            U.withUser.t() + " " + c.str("with") + " · " + fmtTime(c.lng("createdAt")),
                            color = NxMuted, fontSize = 12.sp
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))

            val kind = danger
            if (kind != null) {
                val targetId = user.str("nexoraId")
                DangerDialog(
                    kind = kind,
                    name = name,
                    withReason = kind == Danger.BAN || kind == Danger.DELETE,
                    busy = control.actionBusy,
                    error = control.actionError,
                    onConfirm = { password, reason ->
                        when (kind) {
                            Danger.BAN -> control.ban(targetId, reason, password) {
                                danger = null
                                users.reloadProfile()
                                users.loadFirst()
                                vm.refresh()
                            }
                            Danger.UNBAN -> control.unban(targetId, password) {
                                danger = null
                                users.reloadProfile()
                                users.loadFirst()
                                vm.refresh()
                            }
                            Danger.DELETE -> control.delete(targetId, reason, password) {
                                danger = null
                                users.closeProfile()
                                users.loadFirst()
                                vm.refresh()
                            }
                            Danger.RESTORE -> Unit
                        }
                    },
                    onDismiss = { danger = null }
                )
            }
        }
    }
    if (ask) {
        AlertDialog(
            onDismissRequest = { ask = false },
            title = { Text(U.confirmTitle.t()) },
            text = { Text(U.confirmText.t()) },
            confirmButton = {
                TextButton(onClick = {
                    ask = false
                    users.revealPrivate()
                }) { Text(U.yes.t()) }
            },
            dismissButton = { TextButton(onClick = { ask = false }) { Text(S.promptCancel.t()) } }
        )
    }
}
