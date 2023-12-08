package com.example.englishlearners.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.englishlearners.R

class MyFolderFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_my_folder, container, false)

        val fragmentManager = childFragmentManager
        val transaction = fragmentManager.beginTransaction()
        val fragment = FolderFragment()
        transaction.replace(R.id.list, fragment)
        transaction.commit()

        return rootView
    }
}