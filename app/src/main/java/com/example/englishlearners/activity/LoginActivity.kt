package com.example.englishlearners.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.englishlearners.R
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private val mAuth = FirebaseAuth.getInstance()

    private lateinit var btnLogin: Button
    private lateinit var textViewToSignUpScreen: TextView
    private lateinit var userNameRditText: EditText
    private lateinit var passwordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        // map view
        btnLogin = findViewById(R.id.btn_login)
        userNameRditText = findViewById(R.id.edit_text_email)
        passwordEditText = findViewById(R.id.edit_text_password)
        textViewToSignUpScreen = findViewById(R.id.to_sign_up_screen)
        // set event
        btnLogin.setOnClickListener {
            Toast.makeText(this, "Login button clicked", Toast.LENGTH_LONG).show()
            signIn(userNameRditText.text.toString(), passwordEditText.text.toString())
        }

        textViewToSignUpScreen.setOnClickListener {
            Toast.makeText(this, "to sign up clicked", Toast.LENGTH_LONG).show()
            val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun signIn(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Đăng nhập thất bại: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}