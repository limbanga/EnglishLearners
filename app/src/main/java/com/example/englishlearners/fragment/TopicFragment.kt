package com.example.englishlearners.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.englishlearners.AppConst
import com.example.englishlearners.FirebaseService
import com.example.englishlearners.R
import com.example.englishlearners.Utils
import com.example.englishlearners.activity.TopicDetailActivity
import com.example.englishlearners.model.AppUser
import com.example.englishlearners.model.Topic
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class TopicFragment(private var folderId: String? = null) : Fragment() {

    private val database = Firebase.database

    private lateinit var linearLayout: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_topic, container, false)
        // map view
        linearLayout = rootView.findViewById(R.id.list)
        // load data
        loadData()
        return rootView
    }


    private fun loadData() {
        lifecycleScope.launch {
            try {
                val topicsList =
                    if (folderId != null) FirebaseService.getTopicsByFolderId(folderId!!) else FirebaseService.getTopics()

                topicsList.forEach {
                    val user = FirebaseService.getUser(it.ownerId)
                    if (user != null) {
                        it.owner = user
                        setCardView(it)
                    }
                }
            } catch (e: Exception) {
                Log.e("Firebase", "Error loading topics: ${e.message}")
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setCardView(topic: Topic) {
        if (!isAdded) {
            return
        }

        val view: View = requireActivity().layoutInflater
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
            val intent = Intent(requireContext(), TopicDetailActivity::class.java)
            // truyền topic id
            val data = topic.id
            intent.putExtra(TopicDetailActivity.KEY_TOPIC_ID, data)
            requireContext().startActivity(intent)
        }

        linearLayout.addView(view)
    }

}


