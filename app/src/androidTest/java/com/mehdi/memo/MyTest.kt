package com.mehdi.memo

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MyTest {
    @Test
    fun useAppContext(){
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.mehdi.memo", appContext.packageName)
    }


    @Test
    fun fab_isShown(){
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.fab))
            .check { view, noViewFoundException -> view.isShown }

        activityScenario.close()
    }
}