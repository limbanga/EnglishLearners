package com.example.englishlearners.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.englishlearners.R

class LoginActivity : AppCompatActivity() {

    private lateinit var btnLogin : Button
    private lateinit var textViewToSignUpScreen : TextView
    private lateinit var editTextusername : EditText
    private lateinit var editTextpassword : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnLogin = findViewById(R.id.btn_login)
        editTextusername = findViewById(R.id.edit_text_email)
        editTextpassword = findViewById(R.id.edit_text_password)
        textViewToSignUpScreen = findViewById(R.id.to_sign_up_screen)

        btnLogin.setOnClickListener {
            Toast.makeText(this, "Login button clicked", Toast.LENGTH_LONG).show()
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
        }

        textViewToSignUpScreen.setOnClickListener {
            Toast.makeText(this, "to sign up clicked", Toast.LENGTH_LONG).show()
            val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}