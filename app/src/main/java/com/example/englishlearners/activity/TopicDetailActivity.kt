package com.example.englishlearners.activity

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topic_detail)

        linearLayout = findViewById(R.id.card_list)
        flashCard = findViewById(R.id.flash_card)

        flashCard.setOnClickListener {
            Toast.makeText(this@TopicDetailActivity, "asfahksf", Toast.LENGTH_SHORT).show()
        }

        loadData()
    }

    private fun loadData(){
        for (i in 1 .. 15) {
            val card : View = layoutInflater.inflate(
                R.layout.fragment_card_item, linearLayout, true)
        }
    }
}