package com.example.englishlearners.Api

import com.example.englishlearners.Model.Topic
import com.example.englishlearners.Model.Vocabulary
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface VocabularyApi {
    @GET("/api/vocabulary")
    fun getByTopicId(@Query("topic") topicId: Int): Call<ArrayList<Vocabulary>>
}