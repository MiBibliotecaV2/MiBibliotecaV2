package com.example.mibibliotecav2

import android.content.Intent
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.example.mibibliotecav2.login.LoginActivity
import java.util.*
import kotlin.concurrent.timerTask


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_splash)

        val timer = Timer()
        timer.schedule(timerTask { goTologinActivity() }, 2000)


    }

    private fun goTologinActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}