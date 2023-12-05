package com.example.englishlearners.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.englishlearners.AppConst
import com.example.englishlearners.R
import com.example.englishlearners.model.Topic
import com.example.englishlearners.model.Vocabulary
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChangeTopicActivity : AppCompatActivity() {

    companion object {
        const val KEY_TOPIC_ID = "KEY_TOPIC_ID"
    }

    private val database = Firebase.database

    private var topicId: String = ""
    private var topic: Topic? = null
    private var list: ArrayList<Vocabulary> = ArrayList()

    private lateinit var linearLayout: LinearLayout
    private lateinit var titleEditText: EditText
    private lateinit var saveChangeButton: ImageView
    private lateinit var backButton: ImageView
    private lateinit var addMoreButton: Button
    private lateinit var appTitleTextView: TextView
    private lateinit var addDescButton: TextView
    private lateinit var descEditText: EditText


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

        // if update
        val receivedIntent = intent
        if (receivedIntent != null && receivedIntent.hasExtra(KEY_TOPIC_ID)) {
            topicId = receivedIntent.getStringExtra(KEY_TOPIC_ID) as String
            Toast.makeText(this, "Update topic: $topicId", Toast.LENGTH_LONG).show()
            appTitleTextView.text = getString(R.string.update_topic_title)
            loadData()
        }

        // set event
        saveChangeButton.setOnClickListener {
            if (topicId.isBlank()) {
                handleAdd()
            } else {
                handleUpdate()
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
                initCard(vocabulary)
                dialog.dismiss()
            }

            dialog.show()
        }

        addDescButton.setOnClickListener {
            addDescButton.visibility = View.GONE
            descEditText.visibility = View.VISIBLE
        }
    }



    private fun handleAdd() {
        val myRef = database.getReference(AppConst.KEY_TOPIC)
        val newRef = myRef.push()
        // get data from ui
        val data = mapOf(
            "title" to titleEditText.text.toString(),
            "desc" to descEditText.text.toString(),
            "created" to System.currentTimeMillis(),
            "updated" to System.currentTimeMillis(),
            AppConst.KEY_VOCABULARY to list
        )
        newRef.setValue(data)
        { databaseError, _ ->
            if (databaseError != null) {
                Toast.makeText(this, "Có lỗi xảy ra. $databaseError.message", Toast.LENGTH_LONG)
                    .show()
            } else {
                Toast.makeText(this, "Thêm học phần thành công.", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    private fun handleUpdate() {
        val myRef = database.getReference(AppConst.KEY_TOPIC)
        val currentRef = myRef.child(topicId)
        // get data from ui
        val data = mapOf(
            "title" to titleEditText.text.toString(),
            "desc" to descEditText.text.toString(),
            "created" to topic!!.created, // không cập nhật trường này
            "updated" to System.currentTimeMillis(),
            AppConst.KEY_VOCABULARY to list
        )
        currentRef.setValue(data)
        { databaseError, _ ->
            if (databaseError != null) {
                Toast.makeText(this, "Có lỗi xảy ra. $databaseError.message", Toast.LENGTH_LONG)
                    .show()
            } else {
                Toast.makeText(this, "Thêm học phần thành công.", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    private fun loadData() {
//        Toast.makeText(this, "Topic id is:$topicId", Toast.LENGTH_SHORT).show()
        val myRef = database.getReference(AppConst.KEY_TOPIC).child(topicId)

        myRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                topic = documentSnapshot.getValue(Topic::class.java) as Topic
                bindingData()
            } else {
                Toast.makeText(this, "Topic không tồn tại.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        // get list vocabulary
        val listVocab = myRef.child(AppConst.KEY_VOCABULARY)
        listVocab.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // remove all view before add
                linearLayout.removeAllViews()

                for (snapshot in dataSnapshot.children) {

                    val itemValue = snapshot.getValue(Vocabulary::class.java)
                    val itemKey = snapshot.key.toString()

                    if (itemValue != null) {
                        itemValue.id = itemKey
                        list.add(itemValue)
                        initCard(itemValue)
                    } else {
                        Log.d(AppConst.DEBUG_TAG, itemKey + "is null")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(AppConst.DEBUG_TAG, "Failed to read value.", error.toException())
            }
        })
    }

    private fun bindingData() {
        titleEditText.setText(topic!!.title)
        if (topic!!.desc.isBlank()) {
            descEditText.visibility = View.GONE
        } else {
            descEditText.setText(topic!!.desc)
        }
    }

    private fun initCard(vocabulary: Vocabulary) {
        val view: View = layoutInflater.inflate(R.layout.item_vocabulary_view, linearLayout, false)

        val termTextView: TextView = view.findViewById(R.id.term_text_view)
        val definitionTextView: TextView = view.findViewById(R.id.definition_text_view)
        val deleteImageView: ImageView = view.findViewById(R.id.delete_image_view)

        termTextView.text = vocabulary.term
        definitionTextView.text = vocabulary.definition

        deleteImageView.setOnClickListener {
            list.removeIf { v -> v.id == vocabulary.id }
            linearLayout.removeView(view)
        }

        view.setOnLongClickListener {
            if ( deleteImageView.visibility == View.VISIBLE) {
                deleteImageView.visibility = View.GONE
            } else {
                deleteImageView.visibility = View.VISIBLE
            }
            return@setOnLongClickListener true
        }

        linearLayout.addView(view)
    }
}