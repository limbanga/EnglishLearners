package com.example.englishlearners.activity

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.englishlearners.R

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var backButton: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        // get view
        backButton = findViewById(R.id.back_btn)
        // set event
        backButton.setOnClickListener {
            finish()
        }
    }
}