package com.example.aston_intensiv_1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    private lateinit var musicServiceIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        musicServiceIntent = Intent(this, MusicService::class.java)

        val playButton = findViewById<Button>(R.id.playButton)
        val pauseButton = findViewById<Button>(R.id.pauseButton)
        val nextButton = findViewById<Button>(R.id.nextButton)
        val prevButton = findViewById<Button>(R.id.prevButton)

        playButton.setOnClickListener {
            startService(musicServiceIntent.setAction("PLAY"))
        }

        pauseButton.setOnClickListener {
            startService(musicServiceIntent.setAction("PAUSE"))
        }

        nextButton.setOnClickListener {
            startService(musicServiceIntent.setAction("NEXT"))
        }

        prevButton.setOnClickListener {
            startService(musicServiceIntent.setAction("PREVIOUS"))
        }

        startService(musicServiceIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(musicServiceIntent)
    }
}