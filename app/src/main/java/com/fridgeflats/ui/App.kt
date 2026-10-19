package com.fridgeflats.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.fridgeflats.data.GroceryStore
import com.fridgeflats.data.Lines
import com.fridgeflats.notify.Notifier

private sealed interface Screen {
    data object Building : Screen
    data object Add : Screen
    data class Detail(val id: Long) : Screen
}

@Composable
fun FridgeFlatsApp() {
    val context = LocalContext.current
    var residents by remember { mutableStateOf(GroceryStore.load(context)) }
    var screen by remember { mutableStateOf<Screen>(Screen.Building) }
    var farewell by remember { mutableStateOf<String?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !Notifier.canNotify(context)) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    BackHandler(enabled = screen != Screen.Building) { screen = Screen.Building }

    when (val s = screen) {
        Screen.Building -> BuildingScreen(
            residents = residents,
            onOpen = { screen = Screen.Detail(it) },
            onAdd = { screen = Screen.Add },
            onTestNag = {
                if (!Notifier.canNotify(context) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    val worst = residents.minByOrNull { it.expiresEpochDay }
                    if (worst == null) {
                        Notifier.send(context, 1, "Fridge Flats", "Nobody lives here yet. The silence is deafening.")
                    } else {
                        val stage = worst.daysLeft().coerceIn(-1L, 2L).toInt()
                        val (title, body) = Lines.nag(worst.personality, stage, worst.name)
                        Notifier.send(context, 1, title, body)
                    }
                }
            },
        )

        Screen.Add -> AddScreen(
            onCancel = { screen = Screen.Building },
            onSave = { g ->
                residents = GroceryStore.add(context, g)
                screen = Screen.Building
            },
        )

        is Screen.Detail -> {
            val g = residents.find { it.id == s.id }
            if (g == null) {
                LaunchedEffect(s) { screen = Screen.Building }
            } else {
                DetailScreen(
                    g = g,
                    onBack = { screen = Screen.Building },
                    onEaten = {
                        farewell = Lines.farewell(eaten = true, name = g.name)
                        residents = GroceryStore.remove(context, g.id)
                        screen = Screen.Building
                    },
                    onTossed = {
                        farewell = Lines.farewell(eaten = false, name = g.name)
                        residents = GroceryStore.remove(context, g.id)
                        screen = Screen.Building
                    },
                )
            }
        }
    }

    farewell?.let { message ->
        AlertDialog(
            onDismissRequest = { farewell = null },
            confirmButton = { TextButton(onClick = { farewell = null }) { Text("Okay") } },
            title = { Text("Moving out") },
            text = { Text(message) },
        )
    }
}
