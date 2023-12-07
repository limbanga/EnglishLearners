package com.example.englishlearners.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
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
import com.example.englishlearners.R
import com.example.englishlearners.fragment.TopicFragment
import com.example.englishlearners.fragment.LibraryFragment
import com.example.englishlearners.fragment.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    companion object {
        fun getFireBaseUser(context: Context): FirebaseUser {
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

    private var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val bottomNav: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        firebaseUser = getFireBaseUser(this)
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
                val database = Firebase.database
                //
                val myRef = database.getReference(AppConst.KEY_FOLDER)
                val newRef = myRef.push()
                val data = mapOf(
                    "name" to nameEditText.text.toString(),
                    "desc" to descEditText.text.toString(),
                    "ownerId" to firebaseUser!!.uid,
                    "created" to System.currentTimeMillis(),
                    "updated" to System.currentTimeMillis(),
                )
                newRef.setValue(data)
                { databaseError, _ ->
                    if (databaseError != null) {
                        Toast.makeText(this, "Có lỗi xảy ra.", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "Tạo thư mục thành công!", Toast.LENGTH_LONG).show()
                        folderDialog.dismiss()
                    }
                }
            }

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