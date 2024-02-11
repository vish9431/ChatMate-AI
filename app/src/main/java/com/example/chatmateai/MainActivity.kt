package com.example.chatmateai

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btntext = findViewById<Button>(R.id.butt1)
        val btnimg = findViewById<Button>(R.id.butt2)

        btntext.setOnClickListener{
            val intent = Intent(this, TextGen::class.java)
            startActivity(intent)
        }
        btnimg.setOnClickListener {
            val intent = Intent(this, ImageGen::class.java)
            startActivity(intent)
        }


    }
}