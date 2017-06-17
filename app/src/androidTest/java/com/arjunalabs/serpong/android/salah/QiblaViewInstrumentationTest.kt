package com.arjunalabs.serpong.android.salah

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.arjunalabs.serpong.android.salah.qibla.QiblaActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import android.content.Intent



/**
 * Created by bobbyadiprabowo on 5/27/17.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class QiblaViewInstrumentationTest {

    @Rule @JvmField
    val qiblaActivityRule = ActivityTestRule(QiblaActivity::class.java)

    @Before
    fun setup() {
        qiblaActivityRule.launchActivity(Intent())
    }

    @Test
    fun checkUI() {
        onView(withId(R.id.qiblacompass)).check(matches(isDisplayed()))
    }
}