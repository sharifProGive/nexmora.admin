package com.zentora.nexmora.admin

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LockScreen(vm: AppVm) {
    Page {
        Spacer(Modifier.height(48.dp))
        Text("🔐", fontSize = 64.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        Text(
            S.lockTitle.t(),
            color = NxText,
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        ErrorLine(vm.error)
        PrimaryButton(S.unlock.t(), onClick = { vm.unlock() }, busy = vm.busy)
        TextButton(onClick = { vm.goRebind() }, enabled = !vm.busy, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text(S.lostFinger.t(), textAlign = TextAlign.Center)
        }
    }
}
