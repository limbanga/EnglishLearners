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
import androidx.fragment.app.Fragment
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


class TopicFragment : Fragment() {

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
        val myRef = database.getReference(AppConst.KEY_TOPIC).orderByChild("updated")

        myRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // remove all view before add
                linearLayout.removeAllViews()

                for (snapshot in dataSnapshot.children.reversed()) {

                    val itemValue = snapshot.getValue(Topic::class.java)
                    val itemKey = snapshot.key.toString()

                    if (itemValue != null) {
                        itemValue.id = itemKey
                        // lay ten ben khoa ngoai
                        val userRef =
                            database.getReference(AppConst.KEY_USER).child(itemValue.ownerId)

                        userRef.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val appUser: AppUser? = dataSnapshot.getValue(AppUser::class.java)
                                if (appUser != null) {
                                    appUser.id = dataSnapshot.key.toString()
                                    itemValue.owner = appUser
                                    initCard(itemValue)
                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        "owner is null",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                Log.d(AppConst.DEBUG_TAG, "Khong the doc owner")
                            }
                        })

                    } else {
                        Log.d(AppConst.DEBUG_TAG, itemKey + "is null")
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

    @SuppressLint("SetTextI18n")
    private fun initCard(topic: Topic) {
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


