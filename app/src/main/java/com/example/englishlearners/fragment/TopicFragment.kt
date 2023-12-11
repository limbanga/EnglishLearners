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
import android.widget.Toast
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.englishlearners.AppConst
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
        if (folderId != null) {
            loadDataInFolder()
        } else {
            loadData()
        }
        return rootView
    }

//    private fun loadDataInFolder() {
//        val myRef =
//            database.getReference(AppConst.KEY_FOLDER).child(folderId!!).child(AppConst.KEY_TOPIC)
//
//        myRef.addValueEventListener(object : ValueEventListener {
//
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                // remove all view before add
//                linearLayout.removeAllViews()
//
//                for (snapshot in dataSnapshot.children.reversed()) {
//                    val itemKey = snapshot.key.toString()
//
//                    val topicRef =
//                        database.getReference(AppConst.KEY_TOPIC).child(itemKey)
//
//                    topicRef.get().addOnSuccessListener { documentSnapshot ->
//                        if (!documentSnapshot.exists()) {
//                            Log.e(
//                                AppConst.DEBUG_TAG,
//                                "Topic khong ton tai"
//                            )
//                            return@addOnSuccessListener
//                        }
//
//                        val topic = documentSnapshot.getValue(Topic::class.java)
//                            ?: return@addOnSuccessListener
//
//                        val userRef =
//                            database.getReference(AppConst.KEY_USER).child(topic.ownerId)
//                                .get().addOnSuccessListener { documentSnapshot1 ->
//                                    if (!documentSnapshot1.exists()) {
//                                        Log.e(
//                                            AppConst.DEBUG_TAG,
//                                            "User khong ton tai"
//                                        )
//                                    } else {
//                                        topic.owner = documentSnapshot1.getValue(AppUser::class.java)
//                                        initCard(topic)
//                                    }
//                                }
//                    }
//
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.e(
//                    AppConst.DEBUG_TAG,
//                    "Failed to read value.${error.message}",
//                    error.toException()
//                )
//            }
//        })
//    }

    private fun loadDataInFolder() {
        val myRef = database.getReference(AppConst.KEY_FOLDER).child(folderId!!)
            .child(AppConst.KEY_TOPIC)

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Remove all views before adding
                linearLayout.removeAllViews()

                val topics = mutableListOf<Topic>()

                for (snapshot in dataSnapshot.children.reversed()) {
                    val itemKey = snapshot.key.toString()
                    val topicRef = database.getReference(AppConst.KEY_TOPIC).child(itemKey)

                    topicRef.get().addOnSuccessListener { documentSnapshot ->
                        if (documentSnapshot.exists()) {
                            val topic = documentSnapshot.getValue(Topic::class.java)
                            topic?.let {
                                topics.add(it)
                            }
                        }
                        // If all topics are fetched, initialize cards
                        if (topics.size == dataSnapshot.childrenCount.toInt()) {
                           // fetchOwners(topics)
                        }
                    }.addOnFailureListener { exception ->
                        Log.e(AppConst.DEBUG_TAG, "Error fetching topic: $exception")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(
                    AppConst.DEBUG_TAG,
                    "Failed to read value.${error.message}",
                    error.toException()
                )
            }
        })
    }

    private suspend fun getUser(userId: String): AppUser? {
        return try {
            val myRef = database.getReference(AppConst.KEY_USER).child(userId)
            val snapshot = myRef.get().await()

            if (snapshot.exists()) {
                val user = snapshot.getValue(AppUser::class.java)
                user
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }



    private fun loadData() {
        lifecycleScope.launch {
            try {
                val topicsList = loadTopicsFromFirebase()

                topicsList.forEach {
                    val user = getUser(it.ownerId)
                    if (user != null){
                        it.owner = user
                        setCardView(it)
                    }
                }
            } catch (e: Exception) {
                Log.e("Firebase", "Error loading topics: ${e.message}")
            }
        }
    }


    private suspend fun loadTopicsFromFirebase(): ArrayList<Topic> {
        return suspendCoroutine { continuation ->
            val topicsList = ArrayList<Topic>()
            val myRef = database.getReference(AppConst.KEY_TOPIC)

            myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children.reversed()) {
                        val itemValue = snapshot.getValue(Topic::class.java)
                        val itemKey = snapshot.key.toString()

                        if (itemValue != null) {
                            itemValue.id = itemKey
                            topicsList.add(itemValue)
                        } else {
                            Log.d(AppConst.DEBUG_TAG, "$itemKey is null")
                        }
                    }
                    continuation.resume(topicsList)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(AppConst.DEBUG_TAG, "Failed to read value. ${error.message}", error.toException())
                    continuation.resumeWithException(error.toException())
                }
            })
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


