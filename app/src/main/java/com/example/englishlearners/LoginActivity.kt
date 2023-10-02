package com.example.englishlearners

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    /*
    * lim's note
    * kotlin doesn't have static key word
    * use Companion instead
    * link: https://www.youtube.com/watch?v=tlrU5_dHLZo
    * */

    /*
    * lim's note
    * what different betweent 'var' and 'val'
    * val is const, can be reassign
    * link: https://www.youtube.com/watch?v=4fAkUzGDSg4s
    * */


    lateinit var preferences : SharedPreferences
    private lateinit var btnLogin : Button
    private lateinit var editTextusername : EditText
    private lateinit var editTextpassword : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnLogin = findViewById(R.id.btn_login)
        editTextusername = findViewById(R.id.edit_text_email)
        editTextpassword = findViewById(R.id.edit_text_password)

        btnLogin.setOnClickListener {
            Toast.makeText(this, "Clicked", Toast.LENGTH_LONG).show()
            postLogin()
        }

    }

    private fun postLogin() {
        val apiService = RetrofitService.api
        // get form data
        val username = editTextusername.text.toString()
        val password = editTextpassword.text.toString()

        val loginData = LoginForm(username, password)

        apiService.login(loginData).enqueue( object : Callback<Token?> {
            override fun onResponse(call: Call<Token?>, response: Response<Token?>) {

                if (response.isSuccessful) {
                    Toast.makeText(this@LoginActivity , "Login ok", Toast.LENGTH_LONG).show()
                    preferences = this@LoginActivity.getSharedPreferences("auth_save", Context.MODE_PRIVATE)
                    val editor = preferences.edit()
                    editor.putString("access_token", response.body()!!.access)
                    editor.apply()

                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()

                } else {
                    Toast.makeText(this@LoginActivity , "HTTP error code" + response.code(), Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Token?>, t: Throwable) {
                Toast.makeText(this@LoginActivity , "Can't call api", Toast.LENGTH_LONG).show()
                t.printStackTrace()
            }
        })
    }
}