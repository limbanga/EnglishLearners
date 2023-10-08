package com.example.englishlearners.Api

import com.example.englishlearners.Model.Topic
import retrofit2.Call
import retrofit2.http.GET

interface TopicApi {
    @GET("api/topic")
    fun getAll(): Call<ArrayList<Topic>>
//    @GET("api/topic/string")
//    fun get(id : String): Call<Topic>
}