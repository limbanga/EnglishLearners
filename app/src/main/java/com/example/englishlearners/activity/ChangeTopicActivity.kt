package com.example.englishlearners.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.englishlearners.FirebaseService
import com.example.englishlearners.R
import com.example.englishlearners.model.Topic
import com.example.englishlearners.model.Vocabulary
import com.google.firebase.auth.FirebaseUser
import com.opencsv.CSVWriter
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter
import java.io.IOException


class ChangeTopicActivity : AppCompatActivity() {

    companion object {
        const val KEY_TOPIC_ID = "KEY_TOPIC_ID"
    }

    private var firebaseUser: FirebaseUser? = null

    private var topicId: String = ""
    private var topic: Topic? = null
    private var list: ArrayList<Vocabulary> = ArrayList()
    private var deleteList: ArrayList<Vocabulary> = ArrayList()

    private lateinit var linearLayout: LinearLayout
    private lateinit var titleEditText: EditText
    private lateinit var saveChangeButton: ImageView
    private lateinit var backButton: ImageView
    private lateinit var addMoreButton: Button
    private lateinit var appTitleTextView: TextView
    private lateinit var addDescButton: TextView
    private lateinit var descEditText: EditText
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var isPublicSwitch: Switch
    private lateinit var Btn_export: Button
    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_topic)
        // map view
        linearLayout = findViewById(R.id.list)
        titleEditText = findViewById(R.id.title_edit_text)
        saveChangeButton = findViewById(R.id.save_change_btn)
        backButton = findViewById(R.id.back_btn)
        addMoreButton = findViewById(R.id.add_more_btn)
        appTitleTextView = findViewById(R.id.app_title_text_view)
        addDescButton = findViewById(R.id.add_desc_text_view)
        descEditText = findViewById(R.id.topic_desc_edit_text)
        isPublicSwitch = findViewById(R.id.is_public_switch)

        // if update
        val receivedIntent = intent
        if (receivedIntent != null && receivedIntent.hasExtra(KEY_TOPIC_ID)) {
            topicId = receivedIntent.getStringExtra(KEY_TOPIC_ID) as String
            Toast.makeText(this, "Update topic: $topicId", Toast.LENGTH_LONG).show()
            appTitleTextView.text = getString(R.string.update_topic_title)
        }
        // get current user
        firebaseUser = MainActivity.getFireBaseUser(this)

        // set event

        saveChangeButton.setOnClickListener {
            if (topicId.isBlank()) { // add
                lifecycleScope.launch {
                    val topicToAdd = reverseBindingTopic(topic)
                    val addedTopic = FirebaseService.addTopic(topicToAdd, 0)

                    if (addedTopic == null) {
                        Toast.makeText(
                            this@ChangeTopicActivity,
                            "Lưu không thành công.",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@launch
                    }

                    // update vocabulary
                    val result = FirebaseService.addVocabularies(list, addedTopic.id)
                    if (result) {
                        Toast.makeText(
                            this@ChangeTopicActivity,
                            "Lưu thành công.",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this@ChangeTopicActivity,
                            "Lưu không thành công.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
                return@setOnClickListener
            }
            // update case
            lifecycleScope.launch {
                val topicToUpdate = reverseBindingTopic(topic)
                val vocabularyCount = 0L
                val updatedTopic = FirebaseService.updateTopic(topicToUpdate, vocabularyCount)

                if (updatedTopic == null) {
                    Toast.makeText(
                        this@ChangeTopicActivity,
                        "Cập nhật không thành công.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }

                val result = FirebaseService.addVocabularies(list, updatedTopic.id)
                FirebaseService.deleteVocabularies(deleteList)
                Toast.makeText(this@ChangeTopicActivity, "Cập nhật thành công.", Toast.LENGTH_SHORT)
                    .show()
                finish()

            }
        }


        backButton.setOnClickListener {
            finish()
        }

        addMoreButton.setOnClickListener {
            val dialog = Dialog(this)
            val view: View = layoutInflater.inflate(R.layout.dialog_vocabulary, null)
            dialog.setContentView(view)
            // set width and height
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            // get dialog view
            val cancelButton: Button = view.findViewById(R.id.cancel_button)
            val okButton: Button = view.findViewById(R.id.ok_btn)
            val termEditText: EditText = view.findViewById(R.id.term_edit_text)
            val definitionEditText: EditText = view.findViewById(R.id.definition_edit_text)

            cancelButton.setOnClickListener {
                dialog.dismiss()
            }

            okButton.setOnClickListener {
                Toast.makeText(this, "term ${termEditText.text}", Toast.LENGTH_LONG)
                    .show()

                val vocabulary = Vocabulary(
                    "",
                    termEditText.text.toString(),
                    definitionEditText.text.toString()
                )

                list.add(vocabulary)
                setCardView(vocabulary)
                dialog.dismiss()
            }

            dialog.show()
        }

        addDescButton.setOnClickListener {
            addDescButton.visibility = View.GONE
            descEditText.visibility = View.VISIBLE
        }
        exportfileCSV();
    }


    override fun onResume() {
        Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show()
        loadData()
        super.onResume()
    }

    // Lấy thông tin từ ui đưa vào topic
    private fun reverseBindingTopic(topic: Topic?): Topic {
        var result = topic
        if (result == null) {
            result = Topic()
        }

        result.title = titleEditText.text.toString()
        result.desc = descEditText.text.toString()
        result.ownerId = firebaseUser!!.uid
        result.isPublic = isPublicSwitch.isChecked
        result.vocabularyCount = -1

        return result
    }

    private fun loadData() {
        if (topicId.isEmpty()) {
            return
        }
        lifecycleScope.launch {
            val topic = FirebaseService.getTopic(topicId) ?: run {
                Toast.makeText(this@ChangeTopicActivity, "Topic không tồn tại.", Toast.LENGTH_SHORT)
                    .show()
                finish()
                return@launch
            }

            bindingTopic(topic)
            val result = FirebaseService.getVocabularies(topicId)
            bindingVocabularies(result)
        }
    }

    private fun bindingVocabularies(list: ArrayList<Vocabulary>) {
        this.list = list
        linearLayout.removeAllViews()
        // set card view
        list.forEach {
            setCardView(it)
        }
    }

    private fun bindingTopic(topic: Topic) {
        // set topic id
        this.topic = topic

        titleEditText.setText(topic.title)
        if (topic.desc.isBlank()) {
            descEditText.visibility = View.GONE
        } else {
            descEditText.setText(topic.desc)
        }
        isPublicSwitch.isChecked = topic.isPublic
    }

    private fun setCardView(vocabulary: Vocabulary) {
        val view: View = layoutInflater.inflate(R.layout.item_vocabulary_view, linearLayout, false)
        // map view
        val termTextView: TextView = view.findViewById(R.id.term_text_view)
        val definitionTextView: TextView = view.findViewById(R.id.definition_text_view)
        val deleteImageView: ImageView = view.findViewById(R.id.delete_image_view)
        // set text
        termTextView.text = vocabulary.term
        definitionTextView.text = vocabulary.definition
        // set event
        deleteImageView.setOnClickListener {
            list.removeIf { v -> v.id == vocabulary.id }
            deleteList.add(vocabulary)
            linearLayout.removeView(view)
        }

        view.setOnLongClickListener {
            if (deleteImageView.visibility == View.VISIBLE) {
                deleteImageView.visibility = View.GONE
            } else {
                deleteImageView.visibility = View.VISIBLE
            }
            return@setOnLongClickListener true
        }
        // add to ui
        linearLayout.addView(view)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private  fun exportfileCSV(){
        Btn_export = findViewById(R.id.btn_export);
        Btn_export.setOnClickListener {
            exportToCsv();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.R)
    fun exportToCsv() {
        val folderName = "Vocabulary"
        val fileName = "file.csv"
        val appDirectory = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            folderName
        )
        if (!appDirectory.exists()) {
            appDirectory.mkdirs()
        }
        Toast.makeText(this, "Export successfully", Toast.LENGTH_SHORT).show()
        val filePath = appDirectory.path + "/" + fileName
        val vocabulary: MutableList<Vocabulary?> = java.util.ArrayList<Vocabulary?>()
        try {
            CSVWriter(FileWriter(File(filePath))).use { writer ->
                // Header
                val header =
                    arrayOf("Thuật ngữ", "Nghĩa")
                writer.writeNext(header)

                // Data
                for (student in list) {
                    val data = arrayOf<String>(
                        student.term,
                        student.definition
                    )
                    writer.writeNext(data)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}






