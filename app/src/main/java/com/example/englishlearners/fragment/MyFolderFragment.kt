package com.example.englishlearners.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.englishlearners.R
import com.example.englishlearners.activity.MainActivity
import com.google.firebase.auth.FirebaseUser

class MyFolderFragment : Fragment() {

    private lateinit var firebaseUser: FirebaseUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_my_folder, container, false)

        val firebaseUser = MainActivity.getFireBaseUser(requireContext()) ?: return rootView

        val fragmentManager = childFragmentManager
        val transaction = fragmentManager.beginTransaction()
        val fragment = FolderFragment(firebaseUser.uid)
        transaction.replace(R.id.list, fragment)
        transaction.commit()

        return rootView
    }
}