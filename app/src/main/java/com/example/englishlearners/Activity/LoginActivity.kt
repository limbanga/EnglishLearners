package com.example.englishlearners.Activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.auth0.android.jwt.JWT
import com.example.englishlearners.ACCESS_TOKEN
import com.example.englishlearners.APP_PREFERENCES_NAME
import com.example.englishlearners.Api.RetrofitService
import com.example.englishlearners.Form.LoginForm
import com.example.englishlearners.Model.Token
import com.example.englishlearners.R
import com.example.englishlearners.SignUpActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var preferences : SharedPreferences
    private lateinit var btnLogin : Button
    private lateinit var textViewToSignUpScreen : TextView
    private lateinit var editTextusername : EditText
    private lateinit var editTextpassword : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        preferences = this@LoginActivity.getSharedPreferences(APP_PREFERENCES_NAME, Context.MODE_PRIVATE)
        // if has login recently
        val accessToken = preferences.getString(ACCESS_TOKEN, null)
        // re-use token
        if (accessToken != null) {
            val jwt = JWT(accessToken)
            val isExpired: Boolean = jwt.isExpired(10)
            if (!isExpired) {
                // redirect to main
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        btnLogin = findViewById(R.id.btn_login)
        editTextusername = findViewById(R.id.edit_text_email)
        editTextpassword = findViewById(R.id.edit_text_password)
        textViewToSignUpScreen = findViewById(R.id.to_sign_up_screen)

        btnLogin.setOnClickListener {
            Toast.makeText(this, "Login button clicked", Toast.LENGTH_LONG).show()
            postLogin()
        }

        textViewToSignUpScreen.setOnClickListener {
            val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    //----------------------------------------------------------------------------------------------
    // HELPER FUNCTIONS
    //----------------------------------------------------------------------------------------------

    // REPAIR LATER---------------------------------------------------------------------------------
    private fun postLogin() {
        val apiService = RetrofitService.api
        // get form data
        val username = editTextusername.text.toString()
        val password = editTextpassword.text.toString()

        val loginData = LoginForm(username, password)

        apiService.login(loginData).enqueue( object : Callback<Token?> {
            override fun onResponse(call: Call<Token?>, response: Response<Token?>) {
                // http code 200
                if (response.isSuccessful) {
                    Toast.makeText(this@LoginActivity , "Call login success", Toast.LENGTH_LONG).show()
                    // save token
                    val editor = preferences.edit()
                    editor.putString("access_token", response.body()!!.access)
                    editor.apply()
                    // redirect to main activity
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else if (response.code() == 401) {
                    Toast.makeText(this@LoginActivity , "Login failed", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@LoginActivity ,
                        "Unknown error http code: ${response.code()}",
                        Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Token?>, t: Throwable) {
                Toast.makeText(this@LoginActivity , "App error: can call api", Toast.LENGTH_LONG).show()
                t.printStackTrace()
            }
        })
    }

}