package com.example.englishlearners.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.englishlearners.R
import com.example.englishlearners.dialog.FolderDialog
import com.example.englishlearners.fragment.HomeFragment
import com.example.englishlearners.fragment.LibraryFragment
import com.example.englishlearners.fragment.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {

    override fun onStart() {
        super.onStart()
        val mAuth = FirebaseAuth.getInstance()

//        val currentUser = mAuth.currentUser
//        if (currentUser == null) {
//            val intent = Intent(this, LoginActivity::class.java)
//            startActivity(intent)
//        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val bottomNav: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        replaceFragment(HomeFragment())

        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_item_home -> replaceFragment(HomeFragment())
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