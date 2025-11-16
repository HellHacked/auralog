package com.example.auralog.data

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object UserStats {
    var sleepHours by mutableStateOf(8)
    var steps by mutableStateOf(4000)
    var currentTime by mutableStateOf("08:30")
    var heartRate by mutableStateOf(70)
    var bloodOxygen by mutableStateOf(98)
    var systolicBP by mutableStateOf(120)     // NEW
    var diastolicBP by mutableStateOf(80)     // NEW
    var stressLevel by mutableStateOf(3)
    var predictionResult by mutableStateOf(0) // make it a Compose state

    var birthYear by mutableStateOf<Int?>(null)
    var gender by mutableStateOf<String?>(null)
    var isDiagnosedMigraine by mutableStateOf(false)
    var episodeFrequency by mutableStateOf<Int?>(null)
    var avgDurationDays by mutableStateOf<Int?>(null)
    var cigarettesPerDay by mutableStateOf<Int?>(null)
    var caffeinePerDay by mutableStateOf<Int?>(null)

    fun buildPrompt(): String {
        val sb = StringBuilder()

        // Header depending on diagnosis
        if (UserStats.isDiagnosedMigraine) {
            sb.append("A person is diagnosed to have migraine. His vital measurements now are: \n")
        } else {
            sb.append("A person whose vital measurements now are: \n")
        }

        // Vital measurements
        sb.append(
            "{\n" +
                    "'Heart Rate': ${UserStats.heartRate}\n" +
                    "'Stress Level': ${UserStats.stressLevel}\n" +
                    "'Blood Pressure': ${UserStats.systolicBP}/${UserStats.diastolicBP}\n" +
                    "'Last Continual Sleeping Hours': ${UserStats.sleepHours}\n" +
                    "'Oxygen Level': ${UserStats.bloodOxygen}%\n" +
                    "'Number of Steps Today': ${UserStats.steps}\n" +
                    "'Clock Time': ${UserStats.currentTime}\n" +
                    "}"
        )

        // Personalized disease severity factors
        if (UserStats.episodeFrequency != null || UserStats.avgDurationDays != null) {
            sb.append("\nHis personalized disease severity factors are: \n{")
            UserStats.episodeFrequency?.let { sb.append("'Monthly episodes frequency': $it\n") }
            UserStats.avgDurationDays?.let { sb.append("'Average episode days': $it\n") }
            sb.append("}")
        }

        // Demographics
        if (UserStats.birthYear != null || !UserStats.gender.isNullOrBlank()) {
            sb.append("\nHis demographic information is: \n{")
            UserStats.birthYear?.let { age ->
                sb.append("'Age': ${2025 - age}\n")
            }
            UserStats.gender?.let { g ->
                sb.append("'Gender': $g\n")
            }
            sb.append("}")
        }

        // Lifestyle
        if (UserStats.caffeinePerDay != null || UserStats.cigarettesPerDay != null) {
            sb.append("\nHis lifestyle traits are: \n{")
            UserStats.caffeinePerDay?.let { sb.append("'Average Caffeine Drinks per Day': $it\n") }
            UserStats.cigarettesPerDay?.let { sb.append("'Average Cigarettes per Day': $it\n") }
            sb.append("}")
        }

        // Ending prompt
        sb.append(
            "\nEstimate the likelihood (in percentage) that the person will experience a migraine episode within the next 24 hours.\n" +
                    "Answer explicitly with a single number between 0 and 100 only, representing the predicted probability without further explanation."
        )

        Log.d("Prompt", sb.toString())

        return sb.toString()

    }

}


