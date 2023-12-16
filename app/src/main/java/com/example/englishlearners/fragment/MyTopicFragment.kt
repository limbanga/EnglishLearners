package com.example.englishlearners.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.englishlearners.FirebaseService
import com.example.englishlearners.R
import com.example.englishlearners.activity.MainActivity
import com.google.firebase.auth.FirebaseUser

class MyTopicFragment : Fragment() {

    private lateinit var firebaseUser: FirebaseUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_my_topic, container, false)
        val firebaseUser = MainActivity.getFireBaseUser(requireContext()) ?: return rootView

        val fragmentManager = childFragmentManager
        val transaction = fragmentManager.beginTransaction()
        val fragment = TopicFragment(userId = firebaseUser.uid)
        transaction.replace(R.id.list, fragment)
        transaction.commit()

        return rootView
    }
}