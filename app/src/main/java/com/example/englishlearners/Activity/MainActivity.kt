package com.example.englishlearners.Activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.englishlearners.Adapter.TopicAdapter
import com.example.englishlearners.Api.RetrofitService
import com.example.englishlearners.Home
import com.example.englishlearners.Model.Topic
import com.example.englishlearners.Profile
import com.example.englishlearners.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        replaceFragment(Home())
        val bottomNav : BottomNavigationView = findViewById(R.id.bottomNavigationView)

        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_item_home -> replaceFragment(Home())
                R.id.menu_item_profile -> replaceFragment(Profile())
                else -> {

                }
            }
            true
        }

//        listViewTopic = findViewById(R.id.list_view_topic)
//        preferences = this.getSharedPreferences("auth_save", Context.MODE_PRIVATE)
//
//        // get stored token
//        val accessToken = preferences.getString("access_token", null)
//
//        if (accessToken == null) {
//            // redirect to login activity
//            val intent = Intent(this, LoginActivity::class.java)
//            startActivity(intent)
//            finish()
//            return
//        }
//
//        val topicService = RetrofitService.getTopicApi(accessToken)
//
//        topicService.getAll().enqueue(object : Callback<List<Topic>> {
//            override fun onResponse(call: Call<List<Topic>>, response: Response<List<Topic>>) {
//
//                if (response.isSuccessful) {
//                    Toast.makeText(this@MainActivity , "Get topic ok", Toast.LENGTH_LONG).show()
//                    val topics = response.body()
//                    if (topics != null) {
//                        for (topic in topics) {
//                            Log.d("check get topic", "title ${topic.title}")
//                        }
//
//                        var temp : ArrayList<Topic> = topics as ArrayList<Topic>
//
//                        listViewTopic.adapter = TopicAdapter(this@MainActivity, temp)
//                    }
//
//
//                } else {
//                    Toast.makeText(this@MainActivity , "HTTP error code" + response.code(), Toast.LENGTH_LONG).show()
//                }
//            }
//
//            override fun onFailure(call: Call<List<Topic>>, t: Throwable) {
//                Toast.makeText(this@MainActivity , "Can't get topic", Toast.LENGTH_LONG).show()
//                t.printStackTrace()
//            }
//        })

    }

    private fun replaceFragment(fragment : Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}