package com.example.englishlearners.activity

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.englishlearners.AppConst
import com.example.englishlearners.ChangeTopicActivity
import com.example.englishlearners.R
import com.example.englishlearners.fragment.HomeFragment
import com.example.englishlearners.fragment.LibraryFragment
import com.example.englishlearners.fragment.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


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

        // add folder
        addFolder.setOnClickListener() {
            // close bottomsheetdialog
            bottomSheetDialog.dismiss()

            val customDialog = Dialog(this)
            val view: View = layoutInflater.inflate(R.layout.add_folder_dialog_layout, null)
            customDialog.setContentView(view)
            // set width and height
            customDialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            // get dialog view
            val cancelButton: Button = view.findViewById(R.id.cancel_button)
            val okButton: Button = view.findViewById(R.id.ok_btn)
            val nameEditText: EditText = view.findViewById(R.id.folder_name_edit_text)
            val descEditText: EditText = view.findViewById(R.id.folder_desc_edit_text)

            cancelButton.setOnClickListener {
                customDialog.dismiss()
            }

            okButton.setOnClickListener{
                val database = Firebase.database
                //
                val myRef = database.getReference(AppConst.KEY_FOLDER)
                val newRef = myRef.push()
                val data = mapOf(
                    "name" to nameEditText.text.toString(),
                    "desc" to descEditText.text.toString(),
                )
                newRef.setValue(data)
                { databaseError, _ ->
                    if (databaseError != null) {
                        Toast.makeText(this, "Some thing went wrong", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "Folder created successfully!", Toast.LENGTH_LONG).show()
                        customDialog.dismiss()
                    }
                }
            }

            customDialog.show()
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