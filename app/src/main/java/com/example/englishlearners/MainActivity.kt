package com.example.englishlearners

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


const val BASE_URL = ""

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnLogin = findViewById<Button>(R.id.btn_login)


        btnLogin.setOnClickListener {
            Toast.makeText(this, "Clicked", Toast.LENGTH_LONG).show()
            postLogin()
        }


    }
    private fun postLogin() {
        val apiService = RetrofitService.api
        val EditTextusername = findViewById<EditText>(R.id.edit_text_email)
        val EditTextpassword = findViewById<EditText>(R.id.edit_text_password)

        val username = EditTextusername.text.toString()
        val password = EditTextpassword.text.toString()

        val loginData = LoginForm(username, password)

        apiService.login(loginData).enqueue(object : Callback<Token?> {
            override fun onResponse(call: Call<Token?>, response: Response<Token?>) {
                Log.d(TAG, "my Message" + response.body()?.access)
            }

            override fun onFailure(call: Call<Token?>, t: Throwable) {
                Log.e(TAG, "Error" + t.message)
                t.printStackTrace()
            }
        })
    }
}