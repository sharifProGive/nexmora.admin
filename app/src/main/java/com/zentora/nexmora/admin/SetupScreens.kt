package com.zentora.nexmora.admin

import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.PersistableBundle
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val copyBtn = Tx("कोड कॉपी करें", "কোড কপি করুন", "Copy code")
private val copiedNote = Tx(
    "कॉपी हो गया। इसे अभी किसी लॉक वाले नोट में रखें या कागज़ पर लिखें। 60 सेकंड बाद क्लिपबोर्ड से हटा दिया जाएगा।",
    "কপি হয়েছে। এখনই কোনো লক করা নোটে রাখুন বা কাগজে লিখুন। ৬০ সেকেন্ড পরে ক্লিপবোর্ড থেকে সরানো হবে।",
    "Copied. Put it in a locked note or write it on paper now. It is removed from the clipboard after 60 seconds."
)

private fun copySecret(context: Context, text: String) {
    val manager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("nexmora", text)
    if (Build.VERSION.SDK_INT >= 33) {
        val extras = PersistableBundle()
        extras.putBoolean(ClipDescription.EXTRA_IS_SENSITIVE, true)
        clip.description.extras = extras
    }
    manager.setPrimaryClip(clip)
    Handler(Looper.getMainLooper()).postDelayed({
        try {
            if (Build.VERSION.SDK_INT >= 28) {
                manager.clearPrimaryClip()
            } else {
                manager.setPrimaryClip(ClipData.newPlainText("", ""))
            }
        } catch (e: Exception) {
            // the clipboard could not be cleared; nothing more to do
        }
    }, 60000L)
}

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
    val context = LocalContext.current
    var written by remember { mutableStateOf(false) }
    var copied by remember { mutableStateOf(false) }
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
        OutlinedButton(
            onClick = {
                copySecret(context, code)
                copied = true
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("📋 " + copyBtn.t()) }
        if (copied) Body(copiedNote.t(), NxGood)
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
