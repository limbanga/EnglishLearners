package com.example.englishlearners.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.englishlearners.FirebaseService
import com.example.englishlearners.R
import com.example.englishlearners.Utils
import com.example.englishlearners.model.Topic
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.coroutines.launch

class AddTopicToFolderActivity : AppCompatActivity() {

    companion object {
        const val KEY_FOLDER_ID = "KEY_FOLDER_ID"
    }

    private var firebaseUser: FirebaseUser? = null
    private var folderId: String = ""
    private var list: ArrayList<Topic> = ArrayList<Topic>()
    private var selectedList: ArrayList<Topic> = ArrayList<Topic>()

    private lateinit var linearLayout: LinearLayout
    private lateinit var backButton: ImageView
    private lateinit var saveButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_topic_to_folder)
        // check data passed
        firebaseUser = MainActivity.getFireBaseUser(this)
        val receivedIntent = intent
        if (receivedIntent == null || !receivedIntent.hasExtra(KEY_FOLDER_ID)) {
            Toast.makeText(this, "Folder không tồn tại.", Toast.LENGTH_LONG).show()
            finish()
        }
        folderId = receivedIntent.getStringExtra(KEY_FOLDER_ID) as String
        // map view
        linearLayout = findViewById(R.id.list)
        backButton = findViewById(R.id.back_btn)
        saveButton = findViewById(R.id.save_change_btn)
        // set event
        backButton.setOnClickListener {
            finish()
        }

        saveButton.setOnClickListener {
            lifecycleScope.launch {
                val clearResult = FirebaseService.removeTopicFromFolder(folderId)
                if (!clearResult) {
                    Toast.makeText(
                        this@AddTopicToFolderActivity,
                        "Đã xảy ra lỗi2.",
                        Toast.LENGTH_SHORT
                    ).show()
                    Toast.makeText(
                        this@AddTopicToFolderActivity,
                        "Xin hãy kiểm tra lại.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }

                val addResult = FirebaseService.addTopicsToFolder(selectedList, folderId)

                if (addResult) {
                    Toast.makeText(
                        this@AddTopicToFolderActivity,
                        "Lưu thay đổi thành công.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@AddTopicToFolderActivity,
                        "Đã xảy ra lỗi.",
                        Toast.LENGTH_SHORT
                    ).show()
                    Toast.makeText(
                        this@AddTopicToFolderActivity,
                        "Xin hãy kiểm tra lại.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                finish()
            }
        }

        // load data
        loadData()
    }


    private fun loadData() {
        lifecycleScope.launch {
            try {
                selectedList = FirebaseService.getTopicsByFolderId(folderId)
                val topics = FirebaseService.getTopicsByOwnerId(firebaseUser!!.uid)

                if (topics.isEmpty()) {
                    Log.d("Topic", "Không có topic nào")
                    return@launch
                }

                val owner = FirebaseService.getUser(firebaseUser!!.uid)

                linearLayout.removeAllViews()
                topics.forEach {
                    if (owner != null) {
                        it.owner = owner
                    }
                    setCardView(it)
                }

            } catch (e: Exception) {
                Log.e("Firebase", "Error getting topics: ${e.message}", e)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setCardView(topic: Topic) {
        // sửa lại màu sau
        val lightGrey = ContextCompat.getColor(this, R.color.light_grey)
        val white = ContextCompat.getColor(this, R.color.white)

        val view: View = layoutInflater
            .inflate(R.layout.item_topic, linearLayout, false)
        // map view
        val layout: LinearLayout = view.findViewById(R.id.layout_item_topic)
        val titleTextView: TextView = view.findViewById(R.id.title)
        val timeSinceTextView: TextView = view.findViewById(R.id.time_since_text_view)
        val displayNameTextView: TextView = view.findViewById(R.id.display_name_text_view)
        val vocabularyTextView: TextView = view.findViewById(R.id.vocabulary_count_text_view)

        titleTextView.text = topic.title
        timeSinceTextView.text = Utils.getTimeSince(topic.updated)
        displayNameTextView.text = topic.owner?.displayName
        vocabularyTextView.text = "${topic.vocabularyCount} thuật ngữ"

        // set back groud if selected
        if (selectedList.any { it.id == topic.id }) {
            layout.setBackgroundColor(lightGrey)
        }

        view.setOnClickListener {

            if (!selectedList.any { it.id == topic.id }) {
                selectedList.add(topic)
                layout.setBackgroundColor(lightGrey)
            } else {
                selectedList.removeAll { it.id == topic.id }
                layout.setBackgroundColor(white)
            }
        }

        linearLayout.addView(view)
    }
}