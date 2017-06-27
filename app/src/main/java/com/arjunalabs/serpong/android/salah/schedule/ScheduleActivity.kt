package com.arjunalabs.serpong.android.salah.schedule

import android.Manifest
import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.arjunalabs.serpong.android.salah.R
import android.support.v4.app.ActivityCompat
import android.content.pm.PackageManager
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.*
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability


class ScheduleActivity : AppCompatActivity() {

    val PLAY_SERVICES_RESOLUTION_REQUEST = 9000
    var fusedLocationClient : FusedLocationProviderClient? = null
    var presenter : ScheduleContract.Presenter? = null
    val REQUEST_LOCATION_PERMISSION_CODE = 1
    var viewModel : ScheduleViewModel? = null
    var scheduleView : ScheduleView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        setTitle(R.string.activity_schedule)

        viewModel = ViewModelProviders.of(this).get(ScheduleViewModel::class.java)
        scheduleView = findViewById(R.id.view_schedule) as ScheduleView
        scheduleView?.viewModel = viewModel

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    REQUEST_LOCATION_PERMISSION_CODE);
        } else {

            if (!checkPlayServices()) {

            } else {

                if (viewModel?.lat == null || viewModel?.lng == null) {
                    getLastLocation()
                } else {
                    calculateSchedule()
                }
            }
        }
        presenter = SchedulePresenter()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_schedule, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        fusedLocationClient?.lastLocation?.addOnCompleteListener(this, {
            task -> if (task.isSuccessful && task.result != null) {
                val location = task.result
                viewModel?.lat = location.latitude
                viewModel?.lng = location.longitude

                calculateSchedule()
            } else {
                Log.w("", "getLastLocation:exception", task.exception)
            }
        })

    }

    private fun calculateSchedule() {

        val latitude = viewModel?.lat
        val longitude = viewModel?.lng
        val tz = TimeZone.getDefault()
        val nowDate = Date()
        val timezone = tz.getOffset(nowDate.time) / 3600000
        // Test Prayer times here
        val prayers = PrayTime()

        prayers.timeFormat = prayers.time12
        prayers.calcMethod = prayers.jafari
        prayers.asrJuristic = prayers.shafii
        prayers.adjustHighLats = prayers.angleBased
        val offsets = intArrayOf(0, 0, 0, 0, 0, 0, 0) // {Fajr,Sunrise,Dhuhr,Asr,Sunset,Maghrib,Isha}
        prayers.tune(offsets)

        val now = Date()
        val cal = Calendar.getInstance()
        cal.time = now

        val prayerTimes = prayers.getPrayerTimes(cal,
                latitude!!, longitude!!, timezone.toDouble())
        val prayerNames = prayers.timeNames

        scheduleView?.displaySchedule(prayerNames, prayerTimes)
    }

    fun checkPlayServices(): Boolean {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = apiAvailability.isGooglePlayServicesAvailable(this)
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show()
            } else {
                Log.i("Salaah", "This device is not supported.")
                finish()
            }
            return false
        }
        return true
    }
}
