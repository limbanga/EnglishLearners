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
import androidx.lifecycle.lifecycleScope
import com.example.englishlearners.FirebaseService
import com.example.englishlearners.R
import com.example.englishlearners.fragment.TopicFragment
import com.example.englishlearners.model.Folder
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class FolderDetailActivity : AppCompatActivity() {

    companion object {
        const val KEY_FOLDER_ID = "KEY_FOLDER_ID"
    }

    private val database = Firebase.database
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var folderId: String
    private lateinit var folder: Folder

    private lateinit var linearLayout: LinearLayout
    private lateinit var folderNameTextView: TextView
    private lateinit var displayNameTextView: TextView
    private lateinit var openActionMenuImageView: ImageView
    private lateinit var backButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folder_detail)

        val receivedIntent = intent
        if (receivedIntent == null || !receivedIntent.hasExtra(KEY_FOLDER_ID)) {
            Toast.makeText(this, "No folder id pass.", Toast.LENGTH_LONG).show()
            finish()
        }
        folderId = receivedIntent.getStringExtra(KEY_FOLDER_ID) as String
        // kiểm tra đăng nhập
        val tempUser = MainActivity.getFireBaseUser(this)
        if (tempUser == null) {
            Toast.makeText(this, "Vui lòng đăng nhập.", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        firebaseUser = tempUser

        // map view
        linearLayout = findViewById(R.id.list)
        folderNameTextView = findViewById(R.id.folder_name_text_view)
        displayNameTextView = findViewById(R.id.display_name_text_view)
        openActionMenuImageView = findViewById(R.id.open_menu_btn)
        backButton = findViewById(R.id.back_btn)

        // set event
        backButton.setOnClickListener {
            finish()
        }
        openActionMenuImageView.setOnClickListener {
            openBottomDialog()
        }

    }

    override fun onStart() {
        super.onStart()
        // load data
        loadData()
    }

    override fun onResume() {
        super.onResume()
        Toast.makeText(this, "resume", Toast.LENGTH_SHORT).show()
        loadData()
    }

    private fun loadData() {
        lifecycleScope.launch {
            val result = FirebaseService.getFolder(folderId)

            if (result == null) {
                // Xử lý khi folder không tồn tại
                Toast.makeText(
                    this@FolderDetailActivity,
                    "Folder không tồn tại.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
                return@launch
            }
            // get owner
            val owner = FirebaseService.getUser(result.ownerId)
            if (owner != null) {
                result.owner = owner
            }
            bindingFolder(result)
            addTopicFragment()
        }
    }

    private fun addTopicFragment() {
        // add topic fragment
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        val fragment = TopicFragment(folderId)
        transaction.replace(R.id.list, fragment)
        transaction.commit()
    }

    private fun bindingFolder(folder: Folder) {
        this.folder = folder
        folderNameTextView.text = folder.name
        displayNameTextView.text = folder.owner?.displayName
    }

    @SuppressLint("InflateParams")
    private fun openBottomDialog() {
        val sheetDialog = BottomSheetDialog(this)
        val view: View = layoutInflater.inflate(R.layout.bottom_sheet_folder_action, null)
        sheetDialog.setContentView(view)

        // map view
        val deleteFolderButton: LinearLayout = view.findViewById(R.id.delete_folder_button)
        val openUpdateButton: LinearLayout = view.findViewById(R.id.open_update_button)
        val openAddTopicToFolderButton: LinearLayout = view.findViewById(R.id.open_add_button)
        val backButton: TextView = view.findViewById(R.id.back_btn)

        // set event
        backButton.setOnClickListener {
            sheetDialog.dismiss()
        }

        openUpdateButton.setOnClickListener {
            sheetDialog.dismiss()
            openFolderDialog()
        }

        deleteFolderButton.setOnClickListener {
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
            val cancelButton: Button = view1.findViewById(R.id.cancel_button)
            val okButton: Button = view1.findViewById(R.id.ok_btn)
            // set event
            cancelButton.setOnClickListener {
                dialog.dismiss()
            }

            okButton.setOnClickListener {
                lifecycleScope.launch {

                    val deletionResult = FirebaseService.deleteFolder(folderId)
                    FirebaseService.removeTopicFromFolder(folderId)
                    if (deletionResult) {
                        Toast.makeText(
                            this@FolderDetailActivity,
                            "Xóa folder thành công",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(this@FolderDetailActivity, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            this@FolderDetailActivity,
                            "Xóa folder thất bại",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }

            dialog.show()
        }

        openAddTopicToFolderButton.setOnClickListener {
            sheetDialog.dismiss()
            val intent = Intent(this, AddTopicToFolderActivity::class.java)
            intent.putExtra(AddTopicToFolderActivity.KEY_FOLDER_ID, folderId)
            startActivity(intent)
        }

        sheetDialog.show()
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
        // set data
        nameEditText.setText(folder.name)
        descEditText.setText(folder.desc)
        //set event
        cancelButton.setOnClickListener {
            folderDialog.dismiss()
        }
        okButton.setOnClickListener {
            // nested fun
            fun reverseBindingFolder(folder: Folder? = null): Folder {
                val result = folder ?: Folder()
                result.name = nameEditText.text.toString()
                result.desc = descEditText.text.toString()
                result.ownerId = firebaseUser.uid
                return result
            }

            lifecycleScope.launch {
                val folderToAdd = reverseBindingFolder()

                val addedFolder = FirebaseService.addFolder(folderToAdd)

                if (addedFolder != null) {
                    Toast.makeText(
                        this@FolderDetailActivity,
                        "Cập nhật thành công!",
                        Toast.LENGTH_SHORT
                    ).show()
                    folderDialog.dismiss()
                } else {
                    Toast.makeText(
                        this@FolderDetailActivity,
                        "Cập nhật không thành công.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        folderDialog.show()
    }
}