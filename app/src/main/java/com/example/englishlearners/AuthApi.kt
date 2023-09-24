package com.example.englishlearners
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/token/")
    fun login(@Body body: LoginForm): Call<Token>
}