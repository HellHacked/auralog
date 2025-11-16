package com.example.auralog.ui.home

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.auralog.data.UserStats
import com.example.auralog.step.StepCountManager

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val predictionResult by remember { UserStats::predictionResult }
    val context = LocalContext.current
    val activity = context as Activity
    val stepManager = remember { StepCountManager(activity) }
    val steps by stepManager.steps.collectAsState()

    // Update UserStats.steps live
    LaunchedEffect(steps) {
        UserStats.steps = steps
    }

    DisposableEffect(Unit) {
        stepManager.start()
        onDispose { stepManager.stop() }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard("Sleep", "${UserStats.sleepHours} h", Modifier.weight(1f))
            StatCard("Steps", "$steps", Modifier.weight(1f))
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard("Heart Rate", "${UserStats.heartRate} bpm", Modifier.weight(1f))
            StatCard("Blood Oxygen", "${UserStats.bloodOxygen}%", Modifier.weight(1f))
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard("Blood Pressure", "${UserStats.systolicBP}/${UserStats.diastolicBP}", Modifier.weight(1f))
            StatCard("Stress Level", "${UserStats.stressLevel}/10", Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))
        StatCard("Migraine Probability", "${predictionResult}%", Modifier.fillMaxWidth())

    }
}

@Composable
fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .height(100.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Text(label, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Text(value, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
