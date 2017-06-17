package com.arjunalabs.serpong.android.salah

import android.content.Intent
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.arjunalabs.serpong.android.salah.schedule.ScheduleActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by bobbyadiprabowo on 5/27/17.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class ScheduleViewInstrumentationTest {

    @Rule @JvmField
    val scheduleActivityRule = ActivityTestRule(ScheduleActivity::class.java)

    @Before
    fun setup() {
        scheduleActivityRule.launchActivity(Intent())
    }

    @Test
    fun testFabNavigation() {
        onView(withId(R.id.fab)).check(matches(isDisplayed()))
    }

    @Test
    fun testLocation() {
        onView(withId(R.id.recycler_schedule)).check(matches(isDisplayed()))
    }
}