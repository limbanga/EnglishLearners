package com.example.englishlearners.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.englishlearners.AppConst
import com.example.englishlearners.R
import com.example.englishlearners.model.AppUser
import com.example.englishlearners.model.Topic
import com.example.englishlearners.model.Vocabulary
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*


class TopicDetailActivity : AppCompatActivity() {
    companion object {
        const val KEY_TOPIC_ID = "KEY_TOPIC_ID"
    }

    private lateinit var topicId: String
    private lateinit var topic: Topic

    private var textToSpeech: TextToSpeech? = null
    private val database = Firebase.database

    private lateinit var linearLayout: LinearLayout
    private lateinit var flashCard: CardView
    private lateinit var multipleChoice: CardView
    private lateinit var typeWord: CardView
    private lateinit var backButton: ImageView
    private lateinit var openMenuButton: ImageView
    private lateinit var titleTextView: TextView
    private lateinit var descTextView: TextView
    private lateinit var displayNameTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topic_detail)

        val receivedIntent = intent
        if (receivedIntent == null || !receivedIntent.hasExtra(KEY_TOPIC_ID)) {
            Toast.makeText(this, "No topic id pass.", Toast.LENGTH_LONG).show()
            finish()
        }
        topicId = receivedIntent.getStringExtra(KEY_TOPIC_ID) as String
        // map view
        linearLayout = findViewById(R.id.card_list)
        flashCard = findViewById(R.id.flash_card)
        multipleChoice = findViewById(R.id.multiple_choice)
        typeWord = findViewById(R.id.type_word)
        backButton = findViewById(R.id.back_btn)
        openMenuButton = findViewById(R.id.open_menu_btn)
        titleTextView = findViewById(R.id.title_text_view)
        descTextView = findViewById(R.id.desc_text_view)
        displayNameTextView = findViewById(R.id.display_name_text_view)

        // set event
        flashCard.setOnClickListener {
            Toast.makeText(this, "open flashcard", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, FlashCardActivity::class.java)
            startActivity(intent)
        }

        multipleChoice.setOnClickListener {
            Toast.makeText(this, "open multiple choice", Toast.LENGTH_SHORT)
                .show()
            val intent = Intent(this, MultipleChoiceActivity::class.java)
            startActivity(intent)
        }

        typeWord.setOnClickListener {
            Toast.makeText(this, "open type word", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, TypeWordActivity::class.java)
            startActivity(intent)
        }

        openMenuButton.setOnClickListener {
            openBottomDialog()
        }

        backButton.setOnClickListener {
            finish()
        }

        loadData()
    }

    private fun loadData() {
        val myRef = database.getReference(AppConst.KEY_TOPIC).child(topicId)
        myRef.get().addOnSuccessListener { documentSnapshot ->
            if (!documentSnapshot.exists()) {
                Toast.makeText(this, "Topic không tồn tại.", Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
            topic = documentSnapshot.getValue(Topic::class.java) as Topic
            val userRef =  database.getReference(AppConst.KEY_USER).child(topic.ownerId)
            userRef.get().addOnSuccessListener { userSnapshot ->
                if (!userSnapshot.exists()) {
                    Toast.makeText(this, "Owner không tồn tại.", Toast.LENGTH_SHORT)
                        .show()
                    finish()
                }
                topic.owner = userSnapshot.getValue(AppUser::class.java)
                bindingData()
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
        displayNameTextView.text = topic.owner?.displayName
        if (topic.desc.isEmpty()) {
            descTextView.visibility = View.GONE
        } else {
            descTextView.text = topic.desc
        }
    }

    private fun initCard(vocabulary: Vocabulary) {
        val view: View = layoutInflater.inflate(R.layout.item_vocabulary_view, linearLayout, false)
        // map view
        val termTextView: TextView = view.findViewById(R.id.term_text_view)
        val definitionTextView: TextView = view.findViewById(R.id.definition_text_view)
        val playSoundImageView: ImageView = view.findViewById(R.id.play_sound_image_view)

        termTextView.text = vocabulary.term
        definitionTextView.text = vocabulary.definition
        // set event
        playSoundImageView.setOnClickListener {
            textToSpeech = TextToSpeech(applicationContext) {
                if (it == TextToSpeech.SUCCESS) {
                    textToSpeech!!.language = Locale.US
                    textToSpeech!!.setSpeechRate(1.0f)
                    textToSpeech!!.speak(vocabulary.term, TextToSpeech.QUEUE_ADD, null)
                }
            }
        }

        linearLayout.addView(view)
    }

    override fun onDestroy() {
        textToSpeech?.shutdown()
        super.onDestroy()
    }

    @SuppressLint("InflateParams")
    private fun openBottomDialog() {
        val sheetDialog = BottomSheetDialog(this)
        val view: View = layoutInflater.inflate(R.layout.activity_topic_action, null)
        sheetDialog.setContentView(view)

        // map view
        val deleteTopicButton: LinearLayout = view.findViewById(R.id.delete_topic_button)
        val openUpdateButton: LinearLayout = view.findViewById(R.id.open_update_button)
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
            val okButton: Button = view1.findViewById(R.id.ok_btn)
            val cancelButton: Button = view1.findViewById(R.id.cancel_button)

            okButton.setOnClickListener {
                handleDelete()
                dialog.dismiss()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }

            cancelButton.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }

        openUpdateButton.setOnClickListener {
            val intent = Intent(this, ChangeTopicActivity::class.java)
            intent.putExtra(ChangeTopicActivity.KEY_TOPIC_ID, topicId)
            startActivity(intent)
            sheetDialog.dismiss()
        }

        backButton.setOnClickListener {
            sheetDialog.dismiss()
        }

        sheetDialog.show()

    }

    private fun handleDelete() {
        val myRef = database.getReference(AppConst.KEY_TOPIC).child(topicId)
        myRef.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Xóa bản ghi thành công", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Xóa bản ghi thất bại. ${task.exception}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}