package com.example.englishlearners.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.englishlearners.fragment.LibraryFragment
import com.example.englishlearners.fragment.HomeFragment
import com.example.englishlearners.fragment.ProfileFragment
import com.example.englishlearners.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        replaceFragment(HomeFragment())

        val bottomNav : BottomNavigationView = findViewById(R.id.bottomNavigationView)

        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_item_home -> replaceFragment(HomeFragment())
                R.id.menu_item_profile -> replaceFragment(ProfileFragment())
                R.id.menu_item_library -> replaceFragment(LibraryFragment())
                else -> {

                }
            }
            true
        }
    }

    private fun replaceFragment(fragment : Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}