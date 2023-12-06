package com.example.englishlearners.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.englishlearners.AppConst
import com.example.englishlearners.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class SignUpActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private val database = Firebase.database

    private lateinit var displayNameEditText : EditText
    private lateinit var emailEditText : EditText
    private lateinit var passwordEditText : EditText
    private lateinit var signUpButton : Button
    private lateinit var loginScreenLink : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        mAuth = FirebaseAuth.getInstance()
        displayNameEditText = findViewById(R.id.edit_text_display_name)
        emailEditText = findViewById(R.id.edit_text_email_sign_up)
        passwordEditText = findViewById(R.id.edit_text_password_sign_up)
        signUpButton = findViewById(R.id.button_sign_up)
        loginScreenLink = findViewById(R.id.nav_to_login_screen)

        signUpButton.setOnClickListener{
            // validate
            handleSignUp()
        }

        loginScreenLink.setOnClickListener{
            Toast.makeText(this@SignUpActivity, "to login clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun handleSignUp() {

        mAuth.createUserWithEmailAndPassword(
            emailEditText.text.toString(),
            passwordEditText.text.toString())

            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    val uid = mAuth.currentUser!!.uid
                    val myRef = database.getReference(AppConst.KEY_USER)
                    val newRef = myRef.child(uid)
                    // get data from ui
                    val data = mapOf(
                        "displayName" to displayNameEditText.text.toString(),
                        "created" to System.currentTimeMillis(),
                        "updated" to System.currentTimeMillis(),
                    )
                    newRef.setValue(data)
                    { databaseError, _ ->
                        if (databaseError != null) {
                            Toast.makeText(this, "Có lỗi xảy ra. $databaseError.message", Toast.LENGTH_LONG)
                                .show()
                        } else {
                            Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                } else {
                    Toast.makeText(this, "Đăng ký thất bại: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}