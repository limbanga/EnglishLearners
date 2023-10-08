package com.example.englishlearners

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.englishlearners.Api.RetrofitService
import com.example.englishlearners.Form.SignUpForm
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {

    lateinit var editTextDisplayName : EditText
    lateinit var editTextEmail : EditText
    lateinit var editTextPassword : EditText
    lateinit var buttonSignUp : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        editTextDisplayName = findViewById(R.id.edit_text_display_name)
        editTextEmail = findViewById(R.id.edit_text_email_sign_up)
        editTextPassword = findViewById(R.id.edit_text_password_sign_up)
        buttonSignUp = findViewById(R.id.button_sign_up)


        buttonSignUp.setOnClickListener{
            postRegister()
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