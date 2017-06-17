package com.arjunalabs.serpong.android.salah.qibla

import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LifecycleRegistryOwner
import android.content.Context
import android.hardware.SensorManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.arjunalabs.serpong.android.salah.R

/**
 * Created by bobbyadiprabowo on 27/05/17.
 */

class QiblaActivity : AppCompatActivity(), LifecycleRegistryOwner {

    val lifeCycleRegistry = LifecycleRegistry(this)

    override fun getLifecycle(): LifecycleRegistry {
        return lifeCycleRegistry
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qibla)

        setTitle(R.string.activity_qibla)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val sensorManager = getSystemService(Context.SENSOR_SERVICE)
        val qiblaView = findViewById(R.id.qiblacompass)
        QiblaSensorManager(
                intent.getDoubleExtra("LAT", 0.0),
                intent.getDoubleExtra("LNG", 0.0),
                lifeCycleRegistry, sensorManager as SensorManager,
                qiblaView as QiblaCompassView
        )
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}