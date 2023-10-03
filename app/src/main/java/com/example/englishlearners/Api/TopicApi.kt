package com.example.englishlearners.Api

import com.example.englishlearners.Model.Topic
import retrofit2.Call
import retrofit2.http.GET

interface TopicApi {
    @GET("api/topics/")
    fun getAll(): Call<List<Topic>>
}