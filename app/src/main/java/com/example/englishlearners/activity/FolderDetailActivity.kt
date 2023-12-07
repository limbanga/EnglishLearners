package com.example.englishlearners.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.englishlearners.AppConst
import com.example.englishlearners.R
import com.example.englishlearners.fragment.TopicFragment
import com.example.englishlearners.model.AppUser
import com.example.englishlearners.model.Folder
import com.example.englishlearners.model.Topic
import com.example.englishlearners.model.Vocabulary
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FolderDetailActivity : AppCompatActivity() {

    companion object{
        const val KEY_FOLDER_ID = "KEY_FOLDER_ID"
    }

    private val database = Firebase.database
    private lateinit var folderId: String
    private lateinit var folder: Folder

    private lateinit var linearLayout: LinearLayout
    private lateinit var folderNameTextView: TextView
    private lateinit var displayNameTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folder_detail)

        val receivedIntent = intent
        if (receivedIntent == null || !receivedIntent.hasExtra(KEY_FOLDER_ID)) {
            Toast.makeText(this, "No folder id pass.", Toast.LENGTH_LONG).show()
            finish()
        }
        folderId = receivedIntent.getStringExtra(KEY_FOLDER_ID) as String

        // map view
        linearLayout = findViewById(R.id.list)
        folderNameTextView = findViewById(R.id.folder_name_text_view)
        displayNameTextView = findViewById(R.id.display_name_text_view)
        // load data
        loadData()
        // add topic fragment
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        val fragment = TopicFragment()
        transaction.replace(R.id.list, fragment)
        transaction.commit()

    }

    private fun loadData() {

        val myRef = database.getReference(AppConst.KEY_FOLDER).child(folderId)

        myRef.get().addOnSuccessListener { documentSnapshot ->
            if (!documentSnapshot.exists()) {
                Toast.makeText(this, "Folder không tồn tại.", Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
            folder = documentSnapshot.getValue(Folder::class.java) as Folder
            val userRef =  database.getReference(AppConst.KEY_USER).child(folder.ownerId)
            userRef.get().addOnSuccessListener { userSnapshot ->
                if (!userSnapshot.exists()) {
                    Toast.makeText(this, "Owner không tồn tại.", Toast.LENGTH_SHORT)
                        .show()
                    finish()
                }
                folder.owner = userSnapshot.getValue(AppUser::class.java)
                bindingData()
            }
        }
    }

    private fun bindingData() {
        folderNameTextView.text = folder.name
        displayNameTextView.text = folder.owner?.displayName
    }
}