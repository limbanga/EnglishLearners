package com.example.englishlearners.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.englishlearners.AppConst
import com.example.englishlearners.R
import com.example.englishlearners.model.Topic
import com.example.englishlearners.model.Vocabulary
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class TopicDetailActivity : AppCompatActivity() {
    companion object {
        const val KEY_TOPIC_ID = "KEY_TOPIC_ID"
    }

    private lateinit var topicId: String
    private lateinit var topic: Topic

    private val database = Firebase.database

    private lateinit var linearLayout: LinearLayout
    private lateinit var flashCard: CardView
    private lateinit var multipleChoice: CardView
    private lateinit var typeWord: CardView
    private lateinit var backButton: ImageView
    private lateinit var editButton: ImageView
    private lateinit var titleTextView: TextView
    private lateinit var descTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topic_detail)

        val receivedIntent = intent
        if (receivedIntent == null || !receivedIntent.hasExtra(KEY_TOPIC_ID)) {
            Toast.makeText(this@TopicDetailActivity, "No topic id pass.", Toast.LENGTH_LONG).show()
            finish()
        }
        topicId = receivedIntent.getStringExtra(KEY_TOPIC_ID) as String
        // map view
        linearLayout = findViewById(R.id.card_list)
        flashCard = findViewById(R.id.flash_card)
        multipleChoice = findViewById(R.id.multiple_choice)
        typeWord = findViewById(R.id.type_word)
        backButton = findViewById(R.id.back_btn)
        editButton = findViewById(R.id.edit_btn)
        titleTextView = findViewById(R.id.title_text_view)
        descTextView = findViewById(R.id.desc_text_view)

        // set event
        flashCard.setOnClickListener {
            Toast.makeText(this@TopicDetailActivity, "open flashcard", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@TopicDetailActivity, FlashCardActivity::class.java)
            startActivity(intent)
        }

        multipleChoice.setOnClickListener {
            Toast.makeText(this@TopicDetailActivity, "open multiple choice", Toast.LENGTH_SHORT)
                .show()
            val intent = Intent(this@TopicDetailActivity, MultipleChoiceActivity::class.java)
            startActivity(intent)
        }

        typeWord.setOnClickListener {
            Toast.makeText(this@TopicDetailActivity, "open type word", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@TopicDetailActivity, TypeWordActivity::class.java)
            startActivity(intent)
        }

        editButton.setOnClickListener {
            Toast.makeText(this@TopicDetailActivity, "open edit", Toast.LENGTH_SHORT).show()
        }

        backButton.setOnClickListener {
            finish()
        }

        loadData()
    }

    private fun loadData() {
        Toast.makeText(this@TopicDetailActivity, "Topic id is:$topicId", Toast.LENGTH_SHORT).show()

        val myRef = database.getReference(AppConst.KEY_TOPIC).child(topicId)

        myRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                topic = documentSnapshot.getValue(Topic::class.java) as Topic
                bindingData()
            } else {
                Toast.makeText(this@TopicDetailActivity, "Topic không tồn tại.", Toast.LENGTH_SHORT)
                    .show()
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
                    val itemKey =snapshot.key.toString()

                    if (itemValue != null) {
                        itemValue.id = itemKey
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
        titleTextView.text = topic.title
        if (topic.desc.isNullOrEmpty()) {
            descTextView.visibility = View.GONE
        } else {
            descTextView.text = topic.desc
        }
    }

    private fun initCard(vocabulary: Vocabulary) {
        val view: View = layoutInflater.inflate(R.layout.item_vocabulary_view, linearLayout, false)

        // val titleTextView: TextView = view.findViewById(R.id.title)

        // Log.d(AppConst.DEBUG_TAG, "title"+ topic.title)
        //  titleTextView.text = topic.title

//        view.setOnClickListener {
//            val intent = Intent(requireContext(), TopicDetailActivity::class.java)
//            requireContext().startActivity(intent)
//        }

        linearLayout.addView(view)
    }
}