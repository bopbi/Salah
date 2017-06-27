package com.arjunalabs.serpong.android.salah.qibla

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.OnLifecycleEvent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener2
import android.hardware.SensorManager
import java.util.*


/**
 * Created by bobbyadiprabowo on 6/1/17.
 */

class QiblaSensorManager(val currentLat: Double, val currentLng: Double, lifecycleRegistry: LifecycleRegistry, val sensorManager: SensorManager, val qiblaCompassView: QiblaCompassView) : SensorEventListener2, LifecycleObserver {

    private var mAccelerometerReading = FloatArray(3)
    private var mMagnetometerReading = FloatArray(3)

    private val mRotationMatrix = FloatArray(9)
    private val mOrientationAngles = FloatArray(3)

    private val DEGREE_THRESHOLD = 5.0F
    private var realDegree = 0F
    var magnetometerSet = false
    var accelerometerSet = false
    var date1 : Date? = null
    var date2 : Date? = null

    private val KABA_LAT = 21.422487
    private val KABA_LNG = 39.826206

    val ALPHA = 0.25F

    init {
        lifecycleRegistry.addObserver(this)
        date1 = Date()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onFlushCompleted(sensor: Sensor?) {

    }

    override fun onSensorChanged(event: SensorEvent?) {

        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
           // System.arraycopy(event.values, 0, mAccelerometerReading,
            //        0, mAccelerometerReading.size)
            mAccelerometerReading = lowPass(event.values, mAccelerometerReading)
            accelerometerSet = true
        } else if (event?.sensor?.type == Sensor.TYPE_MAGNETIC_FIELD) {
            //System.arraycopy(event.values, 0, mMagnetometerReading,
            //        0, mMagnetometerReading.size)
            mMagnetometerReading = lowPass(event.values, mMagnetometerReading)
            magnetometerSet = true
        }

        if (magnetometerSet && accelerometerSet) {
            updateOrientationAngles()
        }
    }

    // Compute the three orientation angles based on the most recent readings from
    // the device's accelerometer and magnetometer.
    fun updateOrientationAngles() {
        // Update rotation matrix, which is needed to update orientation angles.
        SensorManager.getRotationMatrix(mRotationMatrix, null,
                mAccelerometerReading, mMagnetometerReading)

        // "mRotationMatrix" now has up-to-date information.
        SensorManager.getOrientation(mRotationMatrix, mOrientationAngles)

        // "mOrientationAngles" now has up-to-date information.

        // Log.d(TAG, "azimuth (rad): " + azimuth);
        var azimuth = Math.toDegrees(mOrientationAngles[0].toDouble()).toFloat() // orientation
        val degreeAzimuth = (azimuth + 360) % 360
        // Log.d(TAG, "azimuth (deg): " + azimuth);

        val kabaaAzimuth = degreeAzimuth - bearing()
        // Log.d(TAG, "azimuth (deg): " + azimuth);

        var letsRotate = false
        if (realDegree > degreeAzimuth) {
            if ((realDegree - degreeAzimuth) > DEGREE_THRESHOLD) {
                letsRotate = true
            }
        } else {
            if ((degreeAzimuth - realDegree) > DEGREE_THRESHOLD) {
                letsRotate = true
            }
        }

        if (letsRotate) {
            date2 = Date()
            val secondsBetween = (date2?.time as Long - date1?.time as Long) // keep use miliscond
            if (secondsBetween > 600) {
                qiblaCompassView.rotateCompass(realDegree, -kabaaAzimuth.toFloat())
                realDegree = -kabaaAzimuth.toFloat()
                date1 = date2
            }
        }
    }

    fun bearing(): Double {
        val latitude1 = Math.toRadians(currentLat)
        val latitude2 = Math.toRadians(KABA_LAT)
        val longDiff = Math.toRadians(KABA_LNG - currentLng)
        val y = Math.sin(longDiff) * Math.cos(latitude2)
        val x = Math.cos(latitude1) * Math.sin(latitude2) - Math.sin(latitude1) * Math.cos(latitude2) * Math.cos(longDiff)

        return (Math.toDegrees(Math.atan2(y, x)) + 360) % 360
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun resume() {
        // connect
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI)

    }


    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun pause() {
        // disconnect if connected
        sensorManager.unregisterListener(this)
    }

    fun lowPass(input: FloatArray, output: FloatArray?): FloatArray {
        if (output == null) return input
        for (i in input.indices) {
            output[i] = output[i] + ALPHA * (input[i] - output[i])
        }
        return output
    }
}