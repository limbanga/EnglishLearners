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
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.englishlearners.FirebaseService
import com.example.englishlearners.R
import com.example.englishlearners.activity.FolderDetailActivity
import com.example.englishlearners.model.Folder
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.launch

class FolderFragment : Fragment() {

    private val database = Firebase.database

    private lateinit var linearLayout: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_folder, container, false)
        // map view
        linearLayout = view.findViewById(R.id.list)
        // load data
        loadData()

        return view
    }

    private fun loadData() {
        lifecycleScope.launch {
            try {
                val folderList = FirebaseService.getFolders()
                folderList.forEach {
                    val user = FirebaseService.getUser(it.ownerId)
                    if (user != null) {
                        it.owner = user
                        setCardView(it)
                    }
                }
            } catch (e: Exception) {
                Log.e("Firebase", "Error loading topics: ${e.message}")
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setCardView(folder: Folder) {
        if (!isAdded) return
        val view: View = requireActivity().layoutInflater
            .inflate(R.layout.item_folder, linearLayout, false)

        val folderNameTextView: TextView = view.findViewById(R.id.folder_name_text_view)
        val displayNameTextView: TextView = view.findViewById(R.id.display_name_text_view)
        val avatarImage: CircleImageView = view.findViewById(R.id.avatar_image)

        folderNameTextView.text = folder.name
        displayNameTextView.text = folder.owner?.displayName
        Glide.with(this)
            .load(folder.owner?.imgPath)
            .into(avatarImage)

        view.setOnClickListener {
            val intent = Intent(requireContext(), FolderDetailActivity::class.java)
            val data = folder.id
            intent.putExtra(FolderDetailActivity.KEY_FOLDER_ID, data)
            requireContext().startActivity(intent)
        }

        linearLayout.addView(view)
    }
}