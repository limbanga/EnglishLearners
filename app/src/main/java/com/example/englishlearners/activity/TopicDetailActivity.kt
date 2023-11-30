package com.example.englishlearners.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.englishlearners.R

class TopicDetailActivity : AppCompatActivity() {

    private lateinit var linearLayout: LinearLayout
    private lateinit var flashCard: CardView
    private lateinit var multipleChoice: CardView
    private lateinit var typeWord: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topic_detail)

        linearLayout = findViewById(R.id.card_list)
        flashCard = findViewById(R.id.flash_card)
        multipleChoice = findViewById(R.id.multiple_choice)
        typeWord = findViewById(R.id.type_word)

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

        loadData()
    }

    private fun loadData() {
        for (i in 1..15) {
            val card: View = layoutInflater.inflate(
                R.layout.fragment_card_item, linearLayout, true
            )
        }
    }
}