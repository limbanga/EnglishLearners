package com.example.englishlearners

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.englishlearners.activity.TopicDetailActivity

class FolderFragment : Fragment() {
    private lateinit var linearLayout : LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_folder, container, false)

        linearLayout = view.findViewById(R.id.list)
        loadData()

        return view
    }

    private fun loadData() {
        for (i in 1..15) {
            val card : View = requireActivity().layoutInflater
                .inflate(R.layout.item_folder, linearLayout, true)

            card.setOnClickListener {
                val intent = Intent(requireContext(), TopicDetailActivity::class.java)
                requireContext().startActivity(intent)
            }
        }
    }
}