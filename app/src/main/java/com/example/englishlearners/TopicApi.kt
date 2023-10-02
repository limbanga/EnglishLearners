package com.example.englishlearners

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET

interface TopicApi {
    @GET("api/topics/")
    fun getAll(): Call<List<Topic>>
}