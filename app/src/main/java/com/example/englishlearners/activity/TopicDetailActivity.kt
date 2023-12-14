package com.example.englishlearners.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.example.englishlearners.AppConst
import com.example.englishlearners.FirebaseService
import com.example.englishlearners.R
import com.example.englishlearners.model.Topic
import com.example.englishlearners.model.Vocabulary
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.collections.ArrayList


class TopicDetailActivity : AppCompatActivity() {
    companion object {
        const val KEY_TOPIC_ID = "KEY_TOPIC_ID"
    }

    private lateinit var topicId: String
    private lateinit var topic: Topic
    private lateinit var list: ArrayList<Vocabulary>

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
    }

    override fun onResume() {
        loadData()
        super.onResume()
    }

    override fun onDestroy() {
        textToSpeech?.shutdown()
        super.onDestroy()
    }

    private fun loadData() {
        lifecycleScope.launch {
            val topic = FirebaseService.getTopic(topicId) ?: run {
                Toast.makeText(this@TopicDetailActivity, "Topic không tồn tại.", Toast.LENGTH_SHORT)
                    .show()
                finish()
                return@launch
            }

            bindingTopic(topic)
            val result = FirebaseService.getVocabularies(topicId)
            bindingVocabularies(result)
        }
    }

    private fun bindingVocabularies(result: ArrayList<Vocabulary>) {
        this.list = result
        linearLayout.removeAllViews()
        result.forEach {
            setCardView(it)
        }
    }

    private fun bindingTopic(topic: Topic) {
        this.topic = topic
        titleTextView.text = topic.title
        displayNameTextView.text = topic.owner?.displayName
        if (topic.desc.isEmpty()) {
            descTextView.visibility = View.GONE
        } else {
            descTextView.text = topic.desc
        }
    }

    private fun setCardView(vocabulary: Vocabulary) {
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
                dialog.dismiss()
                lifecycleScope.launch {
                    val deleteResult = FirebaseService.deleteTopic(topicId)

                    if (deleteResult) {
                        Toast.makeText(
                            this@TopicDetailActivity,
                            "Xóa bản ghi thành công",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(this@TopicDetailActivity, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            this@TopicDetailActivity,
                            "Xóa bản ghi thất bại",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
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
}