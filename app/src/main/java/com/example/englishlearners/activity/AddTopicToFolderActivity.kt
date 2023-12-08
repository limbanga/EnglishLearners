package com.example.englishlearners.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.englishlearners.R
import com.example.englishlearners.Utils
import com.example.englishlearners.model.Topic
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class AddTopicToFolderActivity : AppCompatActivity() {

    private var firebaseUser: FirebaseUser? = null
    private lateinit var linearLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_topic_to_folder)
        //
        firebaseUser = MainActivity.getFireBaseUser(this)
        // map view
        linearLayout = findViewById(R.id.list)
        // set event

        // load data
        fetchUserTopicsSortedByTitle()
    }

    private fun fetchUserTopicsSortedByTitle() {
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val topicsRef: DatabaseReference = database.getReference("topics")

        val query: Query = topicsRef.orderByChild("ownerId").equalTo(firebaseUser!!.uid)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (topicSnapshot in dataSnapshot.children) {
                    val topic = topicSnapshot.getValue(Topic::class.java)
                    if (topic != null) {
                        initCard(topic)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })

    }

    @SuppressLint("SetTextI18n")
    private fun initCard(topic: Topic) {

        val view: View = layoutInflater
            .inflate(R.layout.item_topic, linearLayout, false)

        val titleTextView: TextView = view.findViewById(R.id.title)
        val timeSinceTextView: TextView = view.findViewById(R.id.time_since_text_view)
        val displayNameTextView: TextView = view.findViewById(R.id.display_name_text_view)
        val vocabularyTextView: TextView = view.findViewById(R.id.vocabulary_count_text_view)

        titleTextView.text = topic.title
        timeSinceTextView.text = Utils.getTimeSince(topic.updated)
        displayNameTextView.text = topic.owner?.displayName
        vocabularyTextView.text = "${topic.vocabularyCount} thuật ngữ"

        view.setOnClickListener {

        }

        linearLayout.addView(view)
    }
}