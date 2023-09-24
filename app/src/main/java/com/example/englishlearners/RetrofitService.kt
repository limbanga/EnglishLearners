package com.example.englishlearners

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitService {
    private const val BASE_URL = "http://10.0.2.2:8000"

    private fun retrofitService(): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
    }

    val api: AuthApi by lazy {
        retrofitService().create(AuthApi::class.java)
    }
}
