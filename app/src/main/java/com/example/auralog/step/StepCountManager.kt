package com.example.auralog.step

import android.app.Activity
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*

class StepCountManager(activity: Activity) : SensorEventListener {

    private val sensorManager = activity.getSystemService(SensorManager::class.java)
            as SensorManager
    private val stepSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    private val _steps = MutableStateFlow(0)
    val steps: StateFlow<Int> = _steps

    private var initialSteps = -1
    private var midnightBaseline = 0
    private var lastResetDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)

    fun start() {
        stepSensor?.let { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return
        val totalSteps = event.values[0].toInt()

        if (initialSteps < 0) {
            initialSteps = totalSteps
        }

        // Check if we passed midnight
        val now = Calendar.getInstance()
        val dayOfYear = now.get(Calendar.DAY_OF_YEAR)
        if (dayOfYear != lastResetDay) {
            midnightBaseline = totalSteps - initialSteps
            lastResetDay = dayOfYear
        }

        val stepsSinceMidnight = totalSteps - initialSteps - midnightBaseline
        _steps.value = if (stepsSinceMidnight >= 0) stepsSinceMidnight else 0
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
