package com.example.englishlearners.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import com.example.englishlearners.R
import com.example.englishlearners.dialog.FolderDialog
import com.example.englishlearners.fragment.TopicFragment
import com.example.englishlearners.fragment.LibraryFragment
import com.example.englishlearners.fragment.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class MainActivity : AppCompatActivity() {


    companion object {
         fun getFireBaseUser(context : Context) : FirebaseUser {
             val mAuth = FirebaseAuth.getInstance()
             var firebaseUser: FirebaseUser? = null

             firebaseUser = mAuth.currentUser
             if (firebaseUser == null) {
                 // User is not logged in
                 Toast.makeText(context, "Vui lòng đăng nhập.", Toast.LENGTH_LONG).show()
                 val intent = Intent(context, LoginActivity::class.java)
                 context.startActivity(intent)
             }
             return firebaseUser!!
        }

    }

    override fun onStart() {
        super.onStart()
        getFireBaseUser(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val bottomNav: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        replaceFragment(TopicFragment())

        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_item_home -> replaceFragment(TopicFragment())
                R.id.menu_item_profile -> replaceFragment(ProfileFragment())
                R.id.menu_item_library -> replaceFragment(LibraryFragment())
                R.id.menu_item_add -> {
                    openBottomDialog()
                    return@setOnItemSelectedListener false
                }
                else -> {}
            }
            true
        }
    }

    @SuppressLint("InflateParams")
    private fun openBottomDialog() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view: View = layoutInflater.inflate(R.layout.bottom_dialog_layout, null)
        bottomSheetDialog.setContentView(view)

        val addFolder: TextView = view.findViewById(R.id.add_folder)
        val addTopic: TextView = view.findViewById(R.id.add_topic)

        // add folder
        addFolder.setOnClickListener() {
            // close bottomSheetDialog
            bottomSheetDialog.dismiss()

            val folderDialog = FolderDialog(this)
            folderDialog.show()
        }

        // add topic
        addTopic.setOnClickListener() {
            bottomSheetDialog.dismiss()
            val intent = Intent(this@MainActivity, ChangeTopicActivity::class.java)
            startActivity(intent)
        }
        bottomSheetDialog.show()
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}