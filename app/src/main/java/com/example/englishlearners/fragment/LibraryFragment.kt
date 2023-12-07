package com.example.englishlearners.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.englishlearners.R
import com.example.englishlearners.adapter.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout

class LibraryFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_library, container, false)

        val tabLayout: TabLayout = rootView.findViewById(R.id.tab_layout)
        val viewPager: ViewPager = rootView.findViewById(R.id.view_pager)
        val adapter = ViewPagerAdapter(childFragmentManager)

        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)

        adapter.addFragment(TopicFragment(), "Topic")
        adapter.addFragment(FolderFragment(), "Folder")
        adapter.notifyDataSetChanged()

        return rootView
    }

}