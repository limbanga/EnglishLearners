package com.example.englishlearners

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.englishlearners.Activity.LoginActivity
import com.example.englishlearners.Adapter.TopicAdapter
import com.example.englishlearners.Api.RetrofitService
import com.example.englishlearners.Model.Topic
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class Home : Fragment() {

    lateinit var preferences : SharedPreferences
    lateinit var listViewTopic : ListView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
            return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rootView = getView()

        listViewTopic = rootView!!.findViewById(R.id.list_item_topic)
        preferences = requireActivity().getSharedPreferences("auth_save", Context.MODE_PRIVATE)

        // get stored token
        val accessToken = preferences.getString("access_token", null)

        if (accessToken == null) {
            // redirect to login activity
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
            return
        }

        val topicService = RetrofitService.getTopicApi(accessToken)

        topicService.getAll().enqueue(object : Callback<List<Topic>> {
            override fun onResponse(call: Call<List<Topic>>, response: Response<List<Topic>>) {

                if (response.isSuccessful) {
                    Toast.makeText(context , "Get topic ok", Toast.LENGTH_LONG).show()
                    val topics = response.body()
                    if (topics != null) {
                        for (topic in topics) {
                            Log.d("check get topic", "title ${topic.title}")
                        }

                        var temp : ArrayList<Topic> = topics as ArrayList<Topic>

                        listViewTopic.adapter = TopicAdapter(requireContext(), temp)
                    }


                } else {
                    Toast.makeText(context , "HTTP error code" + response.code(), Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<Topic>>, t: Throwable) {
                Toast.makeText(context , "Can't get topic", Toast.LENGTH_LONG).show()
                t.printStackTrace()
            }
        })

    }
}