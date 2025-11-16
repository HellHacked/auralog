package com.example.auralog.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first

val Context.userStatsDataStore by preferencesDataStore("user_stats")

object UserStatsDataStore {

    private val SLEEP_HOURS = intPreferencesKey("sleep_hours")
    private val STEPS = intPreferencesKey("steps")
    private val HEART_RATE = intPreferencesKey("heart_rate")
    private val BLOOD_OXYGEN = intPreferencesKey("blood_oxygen")
    private val SYSTOLIC_BP = intPreferencesKey("systolic_bp")
    private val DIASTOLIC_BP = intPreferencesKey("diastolic_bp")
    private val STRESS = intPreferencesKey("stress_level")
    private val PREDICTION = intPreferencesKey("prediction_result")

    private val BIRTH_YEAR = intPreferencesKey("birth_year")
    private val GENDER = stringPreferencesKey("gender")
    private val DIAGNOSED = booleanPreferencesKey("diagnosed_migraine")
    private val EPISODE_FREQ = intPreferencesKey("episode_frequency")
    private val EPISODE_DURATION = intPreferencesKey("avg_duration_days")
    private val SMOKES = intPreferencesKey("cigarettes_per_day")
    private val CAFFEINE = intPreferencesKey("caffeine_per_day")


    suspend fun load(context: Context) {
        val prefs = context.userStatsDataStore.data.first()

        UserStats.sleepHours = prefs[SLEEP_HOURS] ?: 8
        UserStats.steps = prefs[STEPS] ?: 4000
        UserStats.heartRate = prefs[HEART_RATE] ?: 70
        UserStats.bloodOxygen = prefs[BLOOD_OXYGEN] ?: 98
        UserStats.systolicBP = prefs[SYSTOLIC_BP] ?: 120
        UserStats.diastolicBP = prefs[DIASTOLIC_BP] ?: 80
        UserStats.stressLevel = prefs[STRESS] ?: 3
        UserStats.predictionResult = prefs[PREDICTION] ?: 0

        UserStats.birthYear = prefs[BIRTH_YEAR]?.takeIf { it != -1 }
        UserStats.gender = prefs[GENDER]?.takeIf { it.isNotEmpty() }
        UserStats.isDiagnosedMigraine = prefs[DIAGNOSED] ?: false // Boolean doesn't need null sentinel
        UserStats.episodeFrequency = prefs[EPISODE_FREQ]?.takeIf { it != -1 }
        UserStats.avgDurationDays = prefs[EPISODE_DURATION]?.takeIf { it != -1 }
        UserStats.cigarettesPerDay = prefs[SMOKES]?.takeIf { it != -1 }
        UserStats.caffeinePerDay = prefs[CAFFEINE]?.takeIf { it != -1 }


    }

    suspend fun save(context: Context) {
        context.userStatsDataStore.edit { prefs ->

            // Vital stats
            prefs[SLEEP_HOURS] = UserStats.sleepHours
            prefs[STEPS] = UserStats.steps
            prefs[HEART_RATE] = UserStats.heartRate
            prefs[BLOOD_OXYGEN] = UserStats.bloodOxygen
            prefs[SYSTOLIC_BP] = UserStats.systolicBP
            prefs[DIASTOLIC_BP] = UserStats.diastolicBP
            prefs[STRESS] = UserStats.stressLevel
            prefs[PREDICTION] = UserStats.predictionResult

            // Nullable profile fields
            prefs[BIRTH_YEAR] = UserStats.birthYear ?: -1
            prefs[GENDER] = UserStats.gender ?: ""
            prefs[DIAGNOSED] = UserStats.isDiagnosedMigraine // Boolean always stored
            prefs[EPISODE_FREQ] = UserStats.episodeFrequency ?: -1
            prefs[EPISODE_DURATION] = UserStats.avgDurationDays ?: -1
            prefs[SMOKES] = UserStats.cigarettesPerDay ?: -1
            prefs[CAFFEINE] = UserStats.caffeinePerDay ?: -1
        }
    }

}
