package com.example.englishlearners

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.fragment.app.Fragment
import com.auth0.android.jwt.JWT
import com.example.englishlearners.Activity.LoginActivity

object RequiredAuthenticated {
    fun checkToken(fragment: Fragment) : String {
        // get token
        val preferences: SharedPreferences =
            fragment
                .requireActivity()
                .getSharedPreferences(APP_PREFERENCES_NAME, Context.MODE_PRIVATE)

        val accessToken = preferences.getString(ACCESS_TOKEN, null)
        // redirect to login page if token is not valid
        if (accessToken == null || JWT(accessToken).isExpired(10)) {
            val intent = Intent(fragment.requireContext(), LoginActivity::class.java)
            fragment.startActivity(intent)
            fragment.requireActivity().finish()
        }

        return accessToken as String
    }
}