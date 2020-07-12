package com.mkitsimple.counterboredom.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mkitsimple.counterboredom.R
import java.util.*

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Timer().schedule(object : TimerTask() {
            override fun run() {
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
            }
        }, 3000)
    }
}
