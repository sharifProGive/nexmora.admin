package com.zentora.nexmora.admin

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ConnectScreen(vm: AppVm) {
    var url by remember { mutableStateOf("") }
    var token by remember { mutableStateOf("") }
    Page {
        Heading(S.connectTitle.t())
        Body(S.connectHint.t())
        NxField(url, { url = it }, S.serverAddress.t(), placeholder = "nexora-backend-fq54.onrender.com", enabled = !vm.busy)
        NxField(token, { token = it }, S.adminToken.t(), password = true, enabled = !vm.busy)
        ErrorLine(vm.error)
        if (vm.busy) Body(S.waking.t())
        PrimaryButton(S.checkConnect.t(), onClick = { vm.connect(url, token) }, busy = vm.busy)
        LanguagePicker(vm.lang) { vm.setLang(it) }
    }
}

@Composable
fun SecureScreen(vm: AppVm) {
    var password by remember { mutableStateOf("") }
    var repeat by remember { mutableStateOf("") }
    Page {
        Heading(S.secureTitle.t())
        Body(S.secureHint.t())
        NxField(password, { password = it }, S.deletePassword.t(), password = true, enabled = !vm.busy)
        NxField(repeat, { repeat = it }, S.repeatPassword.t(), password = true, enabled = !vm.busy)
        ErrorLine(vm.error)
        PrimaryButton(S.setFinger.t(), onClick = { vm.finishSetup(password, repeat) }, busy = vm.busy)
        TextButton(onClick = { vm.backToConnect() }, enabled = !vm.busy) { Text(S.back.t()) }
    }
}

@Composable
fun RecoveryScreen(vm: AppVm, code: String) {
    var written by remember { mutableStateOf(false) }
    Page {
        Heading(S.recoveryTitle.t())
        Body(S.recoveryHint.t())
        Panel(Modifier.fillMaxWidth()) {
            Text(
                code.chunked(5).joinToString("   "),
                color = NxWarn,
                fontSize = 22.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                lineHeight = 32.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = written, onCheckedChange = { written = it })
            Text(S.recoveryWritten.t(), color = NxText, fontSize = 16.sp)
        }
        PrimaryButton(S.continueBtn.t(), onClick = { vm.recoveryDone() }, enabled = written)
    }
}

@Composable
fun RebindScreen(vm: AppVm) {
    var code by remember { mutableStateOf("") }
    Page {
        Heading(S.rebindTitle.t())
        Body(S.rebindHint.t())
        NxField(code, { code = it.filter { c -> c.isDigit() }.take(25) }, S.codeField.t(), numeric = true, enabled = !vm.busy)
        ErrorLine(vm.error)
        PrimaryButton(S.rebindBtn.t(), onClick = { vm.rebind(code) }, busy = vm.busy)
        TextButton(onClick = { vm.backFromRebind() }, enabled = !vm.busy) { Text(S.back.t()) }
    }
}
