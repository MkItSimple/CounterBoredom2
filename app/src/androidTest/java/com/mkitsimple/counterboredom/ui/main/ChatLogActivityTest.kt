package com.mkitsimple.counterboredom.ui.main

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.mkitsimple.counterboredom.R
import com.mkitsimple.counterboredom.ui.auth.LoginActivity
import org.junit.Rule
import org.junit.Test

class ChatLogActivityTest {

    @Rule
    val activityScenario = ActivityScenario.launch(LoginActivity::class.java)

    @Test
    fun emailIsEmpty(){
        onView(withId(R.id.editTextEmail)).perform(clearText())
        onView(withId(R.id.buttonLogin)).perform(click())
    }
}