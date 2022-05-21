package com.mehdi.memo

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.ActivityAction
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
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

    @Test
    fun note_isSuccessfullySaved(){
        val activity = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.fab))
            .perform(ViewActions.click())

        onView(withId(R.id.text_memo_author))
            .perform(ViewActions.typeText("Test Note"))
        onView(withId(R.id.text_memo_note))
            .perform(ViewActions.typeText("Test Note"))
        onView(withId(R.id.text_memo_author))
            .perform(ViewActions.typeText("Test Author"))

        onView(withId(R.id.action_save))
            .perform(ViewActions.click())

        //Check that we are in MainActivity
        onView(withId(R.id.fab))
            .check { view, noViewFoundException ->  view.isShown}

        activity.close()
    }


}