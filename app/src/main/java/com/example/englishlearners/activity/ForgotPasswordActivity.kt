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

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var confirmButton: Button
    private lateinit var emailEditText: EditText
    private lateinit var loginLink: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        // get view
        confirmButton = findViewById(R.id.btn_confirm)
        emailEditText = findViewById(R.id.email_edt)
        loginLink = findViewById(R.id.login_link)
        // set event
        confirmButton.setOnClickListener {
            forgotPassword()
        }
        loginLink.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun forgotPassword() {
        val auth = FirebaseAuth.getInstance()

        val emailAddress = emailEditText.text.toString()

        auth.sendPasswordResetEmail(emailAddress)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Đặt lại mật khẩu thành công.", Toast.LENGTH_SHORT).show()
                    Toast.makeText(this, "Hãy kiểm tra email của bạn", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Email không tồn tại.", Toast.LENGTH_SHORT).show()
                }
            }

    }
}