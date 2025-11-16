package com.example.auralog

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.lifecycle.lifecycleScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.auralog.data.UserStats
import com.example.auralog.data.UserStatsDataStore
import com.example.auralog.manager.MigrainePredictionManager
import com.example.auralog.network.AiRepository
import com.example.auralog.ui.dev.DevScreen
import com.example.auralog.ui.home.HomeScreen
import com.example.auralog.ui.theme.AuralogTheme
import com.example.auralog.viewmodel.VitalsViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import androidx.compose.material.icons.filled.Person
import com.example.auralog.ui.profile.ProfileScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycleScope.launch {
            UserStatsDataStore.load(this@MainActivity)
        }

        setContent {
            AuralogTheme {
                NotificationPermissionWrapper {
                    ActivityRecognitionPermissionWrapper {
                        AuralogApp()
                    }
                }
            }
        }
    }
}

/**
 * Wraps your app and requests ACTIVITY_RECOGNITION at runtime if needed.
 */
@Composable
fun ActivityRecognitionPermissionWrapper(content: @Composable () -> Unit) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { /* Permission result handled if needed */ }
    )

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            launcher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
        }
    }

    content()
}

@Composable
fun NotificationPermissionWrapper(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val activity = context as ComponentActivity
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (!granted) {
                Toast.makeText(
                    context,
                    "Notifications disabled — migraine alerts will not appear.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    )

    // Only request if needed
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Check first if permission is already granted
            val granted = activity.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED
            if (!granted) {
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    content()
}



@PreviewScreenSizes
@Composable
fun AuralogApp(viewModel: VitalsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val context = LocalContext.current
    // Update current time every second
    var currentTime by remember { mutableStateOf(getCurrentTime()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = getCurrentTime()
            UserStats.currentTime = currentTime
            kotlinx.coroutines.delay(1000L)
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            MigrainePredictionManager.runPrediction(context, showNotification = false)
            kotlinx.coroutines.delay(60_000L) // 1 minute when app is active
        }
    }

    LaunchedEffect(
        UserStats.sleepHours,
        UserStats.steps,
        UserStats.heartRate,
        UserStats.bloodOxygen,
        UserStats.systolicBP,
        UserStats.diastolicBP,
        UserStats.stressLevel,
        UserStats.predictionResult
    ) {
        UserStatsDataStore.save(context)
    }


    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = { Icon(it.icon, contentDescription = it.label) },
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it }
                )
            }
        }
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            when (currentDestination) {
                AppDestinations.HOME -> HomeScreen(
                    modifier = Modifier.padding(innerPadding)
                )
                AppDestinations.DEV -> DevScreen(
                    modifier = Modifier.padding(innerPadding),
                    onCheck = { viewModel.checkMigrainePrediction() }
                )
                AppDestinations.PROFILE -> ProfileScreen(
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}




enum class AppDestinations(val label: String, val icon: ImageVector) {
    HOME("Home", Icons.Default.Home),
    PROFILE("Profile", Icons.Default.Person),
    DEV("Dev", Icons.Default.Favorite),
}



@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AuralogTheme {
        Greeting("Android")
    }
}

// Helper function to get current time as HH:mm
private fun getCurrentTime(): String {
    val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    return formatter.format(Date())
}