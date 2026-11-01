package com.example.data.services

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.abs

class CompassSensorManager(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val rotationSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    private val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometer = sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    private val _azimuth = MutableStateFlow(0f)
    val azimuth: StateFlow<Float> = _azimuth.asStateFlow()

    private val _isSensorAvailable = MutableStateFlow(true)
    val isSensorAvailable: StateFlow<Boolean> = _isSensorAvailable.asStateFlow()

    private var gravity = FloatArray(3)
    private var geomagnetic = FloatArray(3)
    private var hasGravity = false
    private var hasGeomagnetic = false

    private val rotationMatrix = FloatArray(9)
    private val orientation = FloatArray(3)

    private var currentFilteredAzimuth = 0f

    fun startListening() {
        if (sensorManager == null) {
            _isSensorAvailable.value = false
            return
        }

        if (rotationSensor != null) {
            sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_UI)
            _isSensorAvailable.value = true
        } else if (accelerometer != null && magnetometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI)
            _isSensorAvailable.value = true
        } else {
            _isSensorAvailable.value = false
        }
    }

    fun stopListening() {
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
            SensorManager.getOrientation(rotationMatrix, orientation)
            val azimuthRad = orientation[0]
            var azimuthDeg = Math.toDegrees(azimuthRad.toDouble()).toFloat()
            azimuthDeg = (azimuthDeg + 360f) % 360f
            updateFilteredAzimuth(azimuthDeg)
        } else {
            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                System.arraycopy(event.values, 0, gravity, 0, 3)
                hasGravity = true
            }
            if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                System.arraycopy(event.values, 0, geomagnetic, 0, 3)
                hasGeomagnetic = true
            }
            if (hasGravity && hasGeomagnetic) {
                val success = SensorManager.getRotationMatrix(rotationMatrix, null, gravity, geomagnetic)
                if (success) {
                    SensorManager.getOrientation(rotationMatrix, orientation)
                    val azimuthRad = orientation[0]
                    var azimuthDeg = Math.toDegrees(azimuthRad.toDouble()).toFloat()
                    azimuthDeg = (azimuthDeg + 360f) % 360f
                    updateFilteredAzimuth(azimuthDeg)
                }
            }
        }
    }

    private fun updateFilteredAzimuth(newAzimuth: Float) {
        // Handle wrap-around at 360/0
        var diff = newAzimuth - currentFilteredAzimuth
        while (diff < -180f) diff += 360f
        while (diff > 180f) diff -= 360f

        val alpha = 0.2f
        currentFilteredAzimuth += diff * alpha
        currentFilteredAzimuth = (currentFilteredAzimuth + 360f) % 360f
        _azimuth.value = currentFilteredAzimuth
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
