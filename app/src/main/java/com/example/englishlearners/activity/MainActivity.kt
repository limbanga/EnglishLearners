package com.example.englishlearners.activity

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.englishlearners.R
import com.example.englishlearners.fragment.HomeFragment
import com.example.englishlearners.fragment.LibraryFragment
import com.example.englishlearners.fragment.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog


class MainActivity : AppCompatActivity() {

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
                    openDialog()
                    return@setOnItemSelectedListener false
                }
                else -> {}
            }
            true
        }
    }

    private fun openDialog() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val bottomSheetView: View = layoutInflater.inflate(R.layout.bottom_dialog_layout, null)
        bottomSheetDialog.setContentView(bottomSheetView)

        val addFolder: TextView = bottomSheetView.findViewById(R.id.add_folder)
        val addTopic: TextView = bottomSheetView.findViewById(R.id.add_topic)

        addFolder.setOnClickListener() {
            Toast.makeText(this, "add folder clicked", Toast.LENGTH_SHORT).show()

            val customDialog = Dialog(this)
            val addFolderDialogView: View =
                layoutInflater.inflate(R.layout.add_folder_dialog_layout, null)
            customDialog.setContentView(addFolderDialogView)
            // set width and height
            customDialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)

            // get dialog view
            val cancelButton: Button = addFolderDialogView.findViewById(R.id.cancel_button)

            cancelButton.setOnClickListener {
                customDialog.dismiss()
            }
            customDialog.show()
        }

        addTopic.setOnClickListener() {
            Toast.makeText(this, "add topic clicked", Toast.LENGTH_SHORT).show()
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