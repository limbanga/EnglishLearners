package com.example.englishlearners.Api
import com.example.englishlearners.Form.LoginForm
import com.example.englishlearners.Form.SignUpForm
import com.example.englishlearners.Model.Token
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/token/")
    fun login(@Body body: LoginForm): Call<Token>

    @POST("api/register/")
    fun register(@Body body: SignUpForm): Call<SignUpForm>
}