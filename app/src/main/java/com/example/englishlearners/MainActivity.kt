package com.example.englishlearners

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    lateinit var preferences : SharedPreferences
    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.textView)

        preferences = this.getSharedPreferences("auth_save", Context.MODE_PRIVATE)
        textView.text = preferences.getString("access_token", "empty")

        val accessToken = preferences.getString("access_token", "empty") ?: return

        val topicService = RetrofitService.getTopicApi(accessToken)
        topicService.getAll().enqueue(object : Callback<List<Topic>> {
            override fun onResponse(call: Call<List<Topic>>, response: Response<List<Topic>>) {

                if (response.isSuccessful) {
                    Toast.makeText(this@MainActivity , "Get topic ok", Toast.LENGTH_LONG).show()
                    val topics = response.body()
                    if (topics != null) {
                        for (topic in topics) {
                            Log.d("check get topic", "title ${topic.title}")
                        }
                    }
                } else {
                    Toast.makeText(this@MainActivity , "HTTP error code" + response.code(), Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<Topic>>, t: Throwable) {
                Toast.makeText(this@MainActivity , "Can't get topic", Toast.LENGTH_LONG).show()
                t.printStackTrace()
            }
        })
    }
}