package com.example.englishlearners.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.englishlearners.R

class HomeFragment : Fragment() {

    private lateinit var linearLayout: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)
        // map view
        linearLayout = rootView.findViewById(R.id.list)

        return rootView
    }

    override fun onResume() {
        loadData()
        super.onResume()
    }

    private fun loadData() {
        val childFragmentManager = childFragmentManager
        val transaction = childFragmentManager.beginTransaction()
        val fragment = TopicFragment(getPublicOnly = true)
        transaction.replace(R.id.list, fragment)
        transaction.commit()
    }
}