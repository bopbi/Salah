package com.arjunalabs.serpong.android.salah.schedule

/**
 * Created by bobbyadiprabowo on 27/05/17.
 */

interface ScheduleContract {

    interface Presenter {
        fun getSchedules()
    }

    interface View {
        fun openQiblaActivity()
        fun displaySchedule(prayTimeNames: ArrayList<String>, prayTimes: ArrayList<String>)
    }

}