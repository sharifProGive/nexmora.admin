package com.zentora.nexmora.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

/**
 * The three-step safety gate for every dangerous action: ask, ask again, then the delete password.
 * The password is kept only inside this dialog and is never stored.
 */
@Composable
fun DangerDialog(
    kind: Danger,
    name: String,
    withReason: Boolean,
    busy: Boolean,
    error: Tx?,
    onConfirm: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var step by remember { mutableIntStateOf(1) }
    var password by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = { if (!busy) onDismiss() },
        title = { Text(C.dangerTitle(kind).t()) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                when (step) {
                    1 -> Text(C.step1(kind, name).t())
                    2 -> Text(C.step2(kind).t(), color = NxWarn)
                    else -> {
                        Text(C.step3.t())
                        if (withReason) {
                            OutlinedTextField(
                                value = reason,
                                onValueChange = { reason = it.take(300) },
                                label = { Text(C.reasonLabel.t()) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text(S.deletePassword.t()) },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier.fillMaxWidth()
                        )
                        ErrorLine(error)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = !busy && (step < 3 || password.isNotEmpty()),
                onClick = {
                    if (step < 3) step += 1 else onConfirm(password, reason.trim())
                }
            ) { Text(if (step < 3) C.yesContinue.t() else C.confirm.t()) }
        },
        dismissButton = {
            TextButton(enabled = !busy, onClick = onDismiss) { Text(S.promptCancel.t()) }
        }
    )
}
