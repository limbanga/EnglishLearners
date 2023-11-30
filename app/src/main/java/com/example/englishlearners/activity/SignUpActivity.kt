package com.example.englishlearners.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.englishlearners.R


class SignUpActivity : AppCompatActivity() {

    private lateinit var editTextDisplayName : EditText
    private lateinit var editTextEmail : EditText
    private lateinit var editTextPassword : EditText
    private lateinit var buttonSignUp : Button
    private lateinit var navToLoginScreen : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        editTextDisplayName = findViewById(R.id.edit_text_display_name)
        editTextEmail = findViewById(R.id.edit_text_email_sign_up)
        editTextPassword = findViewById(R.id.edit_text_password_sign_up)
        buttonSignUp = findViewById(R.id.button_sign_up)
        navToLoginScreen = findViewById(R.id.nav_to_login_screen)

        buttonSignUp.setOnClickListener{
            Toast.makeText(this@SignUpActivity, "buttonSignUp clicked", Toast.LENGTH_SHORT).show()
        }

        navToLoginScreen.setOnClickListener{
            Toast.makeText(this@SignUpActivity, "to login clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}