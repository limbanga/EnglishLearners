package com.example.englishlearners.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.englishlearners.AppConst
import com.example.englishlearners.R
import com.example.englishlearners.fragment.TopicFragment
import com.example.englishlearners.model.AppUser
import com.example.englishlearners.model.Folder
import com.example.englishlearners.model.Topic
import com.example.englishlearners.model.Vocabulary
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FolderDetailActivity : AppCompatActivity() {

    companion object {
        const val KEY_FOLDER_ID = "KEY_FOLDER_ID"
    }

    private val database = Firebase.database
    private lateinit var folderId: String
    private lateinit var folder: Folder

    private lateinit var linearLayout: LinearLayout
    private lateinit var folderNameTextView: TextView
    private lateinit var displayNameTextView: TextView
    private lateinit var openActionMenuImageView: ImageView

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
        openActionMenuImageView = findViewById(R.id.open_menu_btn)
        // load data
        loadData()
        // add topic fragment
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        val fragment = TopicFragment(folderId)
        transaction.replace(R.id.list, fragment)
        transaction.commit()

        openActionMenuImageView.setOnClickListener {
            openBottomDialog()
        }

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
            val userRef = database.getReference(AppConst.KEY_USER).child(folder.ownerId)
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

    @SuppressLint("InflateParams")
    private fun openBottomDialog() {
        val sheetDialog = BottomSheetDialog(this)
        val view: View = layoutInflater.inflate(R.layout.bottom_sheet_folder_action, null)
        sheetDialog.setContentView(view)

        // map view
        val deleteTopicButton: LinearLayout = view.findViewById(R.id.delete_topic_button)
        val openUpdateButton: LinearLayout = view.findViewById(R.id.open_update_button)
        val openAddButton: LinearLayout = view.findViewById(R.id.open_add_button)
        val backButton: TextView = view.findViewById(R.id.back_btn)

        // set event
        deleteTopicButton.setOnClickListener {
            sheetDialog.dismiss()

            val dialog = Dialog(this)
            val view1: View = layoutInflater.inflate(R.layout.dialog_confirm, null)
            dialog.setContentView(view1)
            // set width and height
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            // map view
//            val okButton: Button = view1.findViewById(R.id.ok_btn)
//            val cancelButton: Button = view1.findViewById(R.id.cancel_button)
//
//            okButton.setOnClickListener {
//                //handleDelete()
//                val intent = Intent(this, MainActivity::class.java)
//                startActivity(intent)
//            }
//
//            cancelButton.setOnClickListener {
//                dialog.dismiss()
//            }
//            dialog.show()
//        }
//
//        openUpdateButton.setOnClickListener {
//            val intent = Intent(this, ChangeTopicActivity::class.java)
//            //intent.putExtra(ChangeTopicActivity.KEY_TOPIC_ID, topicId)
//            startActivity(intent)
//            sheetDialog.dismiss()
//        }
//
//        backButton.setOnClickListener {
//            sheetDialog.dismiss()
        }

        openAddButton.setOnClickListener{
            sheetDialog.dismiss()
            val intent = Intent(this, AddTopicToFolderActivity::class.java)
            //intent.putExtra(ChangeTopicActivity.KEY_TOPIC_ID, topicId)
            startActivity(intent)
        }

        sheetDialog.show()
    }
}