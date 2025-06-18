package com.example.gametester2

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)



    val btnStart: Button = findViewById(R.id.btnStart)
        val btnExit: Button = findViewById(R.id.btnExit)

        // Set up click listener for the Start button
        btnStart.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            // finish() // Optional: uncomment if you don't want to return to splash screen
        }

        // Set up click listener for the Exit button
        btnExit.setOnClickListener {
            finishAffinity() // Closes all activities in the task associated with this activity
        }
        val homeIntent = Intent(this@splash, MainActivity::class.java)
        Handler(Looper.getMainLooper()).postDelayed({
            //Do some stuff here, like implement deep linking
            startActivity(homeIntent)
            finish()
        }, SPLASH_TIME_OUT.toLong())
    }
    companion object {
        const val SPLASH_TIME_OUT = 10000
    }
    }

