package com.fridgeflats

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.fridgeflats.notify.ExpiryWorker
import com.fridgeflats.notify.Notifier
import com.fridgeflats.ui.FridgeFlatsApp
import com.fridgeflats.ui.FridgeFlatsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Notifier.ensureChannel(this)
        ExpiryWorker.schedule(this)
        setContent {
            FridgeFlatsTheme {
                FridgeFlatsApp()
            }
        }
    }
}
