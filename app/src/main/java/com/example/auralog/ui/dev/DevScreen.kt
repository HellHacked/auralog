package com.example.auralog.ui.dev

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.auralog.data.UserStats
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

@Composable
fun DevScreen(modifier: Modifier = Modifier, onCheck: () -> Unit) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Current Time display
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Current Time: ${UserStats.currentTime}",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Sleep Hours
        IntSliderCard("Sleep Hours (h)", 0, 12, UserStats.sleepHours) { UserStats.sleepHours = it }

        // Heart Rate
        IntSliderCard("Heart Rate (bpm)", 40, 180, UserStats.heartRate) { UserStats.heartRate = it }

        // Blood Oxygen
        IntSliderCard("Blood Oxygen (%)", 70, 100, UserStats.bloodOxygen) { UserStats.bloodOxygen = it }

        // Systolic BP - slider for simplicity, range 80-200
        IntSliderCard("Systolic BP", 80, 200, UserStats.systolicBP) { UserStats.systolicBP = it }

        // Diastolic BP - slider for simplicity, range 80-200
        IntSliderCard("Diastolic BP", 50, 130, UserStats.diastolicBP) { UserStats.diastolicBP = it }

        // Stress Level
        IntSliderCard("Stress Level (1-10)", 1, 10, UserStats.stressLevel) { UserStats.stressLevel = it }

        Spacer(modifier = Modifier.height(16.dp))

        // Check Button
        Button(
            onClick = onCheck,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Text("Check", color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun IntSliderCard(
    label: String,
    min: Int,
    max: Int,
    value: Int,
    onValueChange: (Int) -> Unit
) {
    var sliderValue by remember { mutableStateOf(value.toFloat()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Text("$label: ${sliderValue.toInt()}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Slider(
                value = sliderValue,
                onValueChange = {
                    // Round to nearest integer
                    sliderValue = it.roundToInt().toFloat()
                    onValueChange(sliderValue.toInt())
                },
                valueRange = min.toFloat()..max.toFloat(),
                steps = max - min - 1, // divide range into equal steps
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }
}


