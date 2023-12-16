package com.example.englishlearners.activity

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.englishlearners.R
import com.google.firebase.auth.FirebaseAuth
import org.w3c.dom.Text

class AccountSettingActivity : AppCompatActivity() {
    private val mAuth = FirebaseAuth.getInstance()

    private lateinit var backButton: ImageView
    private lateinit var logoutButton: TextView
    private lateinit var openChangePass: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_setting)
        // map view
        backButton = findViewById(R.id.back_btn)
        logoutButton = findViewById(R.id.logout_btn)
        openChangePass = findViewById(R.id.open_change_pass)
        // set event
        backButton.setOnClickListener {
            finish()
        }
        openChangePass.setOnClickListener{
            val intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
        }
        logoutButton.setOnClickListener{
            signOut()
        }
    }

    private fun signOut() {
        mAuth.signOut()
        Toast.makeText(this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}