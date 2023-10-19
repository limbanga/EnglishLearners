package com.example.englishlearners

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.englishlearners.Activity.LoginActivity
import com.example.englishlearners.Api.RetrofitService
import com.example.englishlearners.Form.SignUpForm
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {

    private lateinit var editTextDisplayName : EditText
    private lateinit var editTextEmail : EditText
    private lateinit var editTextPassword : EditText
    private lateinit var buttonSignUp : Button
    private lateinit var navToLoginScreen : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        findView();
        setListener();
    }

    private fun findView() {
        editTextDisplayName = findViewById(R.id.edit_text_display_name)
        editTextEmail = findViewById(R.id.edit_text_email_sign_up)
        editTextPassword = findViewById(R.id.edit_text_password_sign_up)
        buttonSignUp = findViewById(R.id.button_sign_up)
        navToLoginScreen = findViewById(R.id.nav_to_login_screen)
    }

    private fun setListener() {
        buttonSignUp.setOnClickListener{
            postRegister()
        }

        navToLoginScreen.setOnClickListener{
            val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    //----------------------------------------------------------------------------------------------
    // HELPER FUNCTIONS
    //----------------------------------------------------------------------------------------------
    private fun postRegister() {
        // get user input
        val displayName = editTextDisplayName.text.toString()
        val email = editTextEmail.text.toString()
        val password = editTextPassword.text.toString()

        val signUpForm = SignUpForm(displayName, email, password)

        val authApi = RetrofitService.api

        authApi.register(signUpForm).enqueue(object : Callback<SignUpForm?> {
            override fun onResponse(call: Call<SignUpForm?>, response: Response<SignUpForm?>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@SignUpActivity,
                        "Đăng ký thành công!",
                        Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@SignUpActivity,
                        "Đăng ký thất bại! http code: ${response.code()}",
                        Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<SignUpForm?>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }
}