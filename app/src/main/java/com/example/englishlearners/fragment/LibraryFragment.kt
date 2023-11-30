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
        val view = inflater.inflate(R.layout.fragment_library, container, false)

        val tabLayout: TabLayout = view.findViewById(R.id.tab_layout)
        val viewPager: ViewPager = view.findViewById(R.id.view_pager)
        val adapter = ViewPagerAdapter(childFragmentManager) // Sử dụng childFragmentManager ở trong Fragment

        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)

        adapter.addFragment(HomeFragment(), "Topic")
        adapter.addFragment(FolderFragment(), "Folder")
        adapter.notifyDataSetChanged()

        return view
    }

}