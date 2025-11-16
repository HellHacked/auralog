package com.example.auralog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.auralog.data.UserStats
import com.example.auralog.network.AiRepository
import kotlinx.coroutines.launch

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VitalsViewModel : ViewModel() {
    private val repo = AiRepository()

    fun checkMigrainePrediction() {
        viewModelScope.launch {
            val prompt = UserStats.buildPrompt()

            try {
                // Perform network request on IO dispatcher
                val result = withContext(Dispatchers.IO) {
                    repo.predictMigraine(prompt)
                }

                UserStats.predictionResult = result

                Log.d("VitalsViewModel", "PredictionResult: ${UserStats.predictionResult}")
            } catch (e: Exception) {
                Log.e("VitalsViewModel", "Prediction error", e)
            }
        }
    }

}
