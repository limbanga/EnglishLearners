package com.example.englishlearners.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.englishlearners.AppConst
import com.example.englishlearners.R
import com.example.englishlearners.activity.TopicDetailActivity
import com.example.englishlearners.model.Topic
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class HomeFragment : Fragment() {

    private val database = Firebase.database

    private lateinit var linearLayout: LinearLayout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rootView = requireView()

        linearLayout = rootView.findViewById(R.id.list)

        loadData();
    }


    private fun loadData() {
        val myRef = database.getReference(AppConst.KEY_TOPIC)

        myRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // remove all view before add
                linearLayout.removeAllViews()

                for (snapshot in dataSnapshot.children) {

                    val itemValue = snapshot.getValue(Topic::class.java)
                    val itemKey =snapshot.key.toString()

                    if (itemValue != null) {
                        val topicWithId = Topic(itemKey, itemValue.title)
                        initCard(topicWithId)
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

    private fun initCard(topic: Topic) {
        val view: View = requireActivity().layoutInflater
            .inflate(R.layout.item_topic, linearLayout, false)

        val titleTextView: TextView = view.findViewById(R.id.title)

        Log.d(AppConst.DEBUG_TAG, "title"+ topic.title)
        titleTextView.text = topic.title

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


