package com.example.englishlearners.Api

import okhttp3.OkHttpClient
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
    private fun retrofitService(accessToken: String? = null): Retrofit {

        val client = OkHttpClient.Builder().addInterceptor { chain ->
            val originalRequest = chain.request()
            val newRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .build()
            chain.proceed(newRequest)
        }.build()

        return Retrofit.Builder()
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
    }

    val api: AuthApi by lazy {
        retrofitService().create(AuthApi::class.java)
    }

    private var topicApi : TopicApi? = null
    fun getTopicApi(accessToken: String) : TopicApi {
        if (topicApi == null) {
            topicApi = retrofitService(accessToken).create(TopicApi::class.java)
        }
        return topicApi as TopicApi
    }
}
