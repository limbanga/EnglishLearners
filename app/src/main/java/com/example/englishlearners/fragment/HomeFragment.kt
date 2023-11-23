package com.example.englishlearners.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.englishlearners.R
import com.example.englishlearners.activity.TopicDetailActivity


class HomeFragment : Fragment() {

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
        for (i in 1..15) {
            val card : View = requireActivity().layoutInflater
                .inflate(R.layout.item_topic, linearLayout, true)

            card.setOnClickListener {
                val intent = Intent(requireContext(), TopicDetailActivity::class.java)
                requireContext().startActivity(intent)
            }
        }
    }
}


