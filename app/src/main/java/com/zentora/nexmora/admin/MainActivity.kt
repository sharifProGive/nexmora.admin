package com.zentora.nexmora.admin

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.LaunchedEffect

class MainActivity : AppCompatActivity() {
    private val vm: AppVm by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
        vm.prompter = { title, subtitle, cancel -> KeyGate.authenticate(this, title, subtitle, cancel) }
        setContent {
            NxTheme(vm.lang) {
                val secure = vm.screenProtect || vm.screen is Screen.Recovery || vm.screen is Screen.Secure
                LaunchedEffect(secure) {
                    if (secure) window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
                    else window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                }
                when (val current = vm.screen) {
                    Screen.Connect -> ConnectScreen(vm)
                    Screen.Secure -> SecureScreen(vm)
                    is Screen.Recovery -> RecoveryScreen(vm, current.code)
                    Screen.Locked -> LockScreen(vm)
                    Screen.Rebind -> RebindScreen(vm)
                    Screen.Home -> HomeScreen(vm)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        vm.autoUnlock()
    }

    override fun onStop() {
        super.onStop()
        vm.lock()
    }

    override fun onDestroy() {
        vm.prompter = null
        super.onDestroy()
    }
}
