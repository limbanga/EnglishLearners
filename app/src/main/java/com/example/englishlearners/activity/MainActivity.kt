package com.example.englishlearners.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.englishlearners.FirebaseService
import com.example.englishlearners.fragment.HomeFragment
import com.example.englishlearners.R
import com.example.englishlearners.fragment.LibraryFragment
import com.example.englishlearners.fragment.ProfileFragment
import com.example.englishlearners.model.Folder
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    companion object {
        fun getFireBaseUser(context: Context): FirebaseUser? {
            val mAuth = FirebaseAuth.getInstance()
            var firebaseUser: FirebaseUser? = null

            firebaseUser = mAuth.currentUser
            return if (firebaseUser == null) {
                // User is not logged in
                Toast.makeText(context, "Vui lòng đăng nhập.", Toast.LENGTH_LONG).show()
                val intent = Intent(context, LoginActivity::class.java)
                context.startActivity(intent)
                null
            } else {
                firebaseUser
            }
        }

    }

    private var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val bottomNav: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        firebaseUser = getFireBaseUser(this)
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

    @SuppressLint("InflateParams")
    private fun openBottomDialog() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view: View = layoutInflater.inflate(R.layout.bottom_dialog_layout, null)
        bottomSheetDialog.setContentView(view)
        // map view
        val addFolder: TextView = view.findViewById(R.id.add_folder)
        val addTopic: TextView = view.findViewById(R.id.add_topic)
        // set event
        // add folder
        addFolder.setOnClickListener() {
            // close bottomSheetDialog
            bottomSheetDialog.dismiss()
            // open
            openFolderDialog()
        }

        // add topic
        addTopic.setOnClickListener() {
            bottomSheetDialog.dismiss()
            val intent = Intent(this, ChangeTopicActivity::class.java)
            startActivity(intent)
        }
        bottomSheetDialog.show()
    }

    private fun openFolderDialog() {

        val folderDialog = Dialog(this)

        val view1: View = layoutInflater.inflate(R.layout.add_folder_dialog_layout, null)
        folderDialog.setContentView(view1)
        // set width and height
        folderDialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // get dialog view
        val cancelButton: Button = view1.findViewById(R.id.cancel_button)
        val okButton: Button = view1.findViewById(R.id.ok_btn)
        val nameEditText: EditText = view1.findViewById(R.id.folder_name_edit_text)
        val descEditText: EditText = view1.findViewById(R.id.folder_desc_edit_text)

        cancelButton.setOnClickListener {
            folderDialog.dismiss()
        }

        okButton.setOnClickListener {
            // nested fun
            fun reverseBindingFolder(folder: Folder? = null) : Folder {
                val result = folder ?: Folder()
                result.name = nameEditText.text.toString()
                result.desc = descEditText.text.toString()
                result.ownerId = firebaseUser!!.uid
                return result
            }

            lifecycleScope.launch {
                val folderToAdd = reverseBindingFolder()

                val addedFolder = FirebaseService.addFolder(folderToAdd)

                if (addedFolder != null) {
                    Log.d("Added Folder", "Added folder with ID: ${addedFolder.id}")
                    Toast.makeText(this@MainActivity, "Tạo thư mục thành công!", Toast.LENGTH_LONG).show()
                    folderDialog.dismiss()
                } else {
                    Log.d("Added Folder", "Failed to add folder")
                    Toast.makeText(this@MainActivity, "Tạo thư mục không thành công.", Toast.LENGTH_LONG).show()
                }
            }

        }

        folderDialog.show()
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}