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
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.englishlearners.AppConst
import com.example.englishlearners.R
import com.example.englishlearners.activity.FolderDetailActivity
import com.example.englishlearners.model.AppUser
import com.example.englishlearners.model.Folder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

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
        val myRef = database.getReference(AppConst.KEY_FOLDER).orderByChild("updated")

        myRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // remove all view before add
                linearLayout.removeAllViews()

                for (snapshot in dataSnapshot.children.reversed()) {

                    val itemValue = snapshot.getValue(Folder::class.java)
                    val itemKey = snapshot.key.toString()

                    if (itemValue != null) {
                        itemValue.id = itemKey
                        // lay ten ben khoa ngoai
                        val userRef =
                            database.getReference(AppConst.KEY_USER).child(itemValue.ownerId)

                        userRef.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val appUser: AppUser? = dataSnapshot.getValue(AppUser::class.java)
                                if (appUser != null) {
                                    appUser.id = dataSnapshot.key.toString()
                                    itemValue.owner = appUser
                                    initCard(itemValue)
                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        "owner is null",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                Log.d(AppConst.DEBUG_TAG, "Khong the doc owner")
                            }
                        })

                    } else {
                        Log.d(AppConst.DEBUG_TAG, itemKey + "is null")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(
                    AppConst.DEBUG_TAG,
                    "Failed to read value.${error.message}",
                    error.toException()
                )
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun initCard(folder: Folder) {
        if (!isAdded) return
        val view: View = requireActivity().layoutInflater
            .inflate(R.layout.item_folder, linearLayout, false)

        val folderNameTextView: TextView = view.findViewById(R.id.folder_name_text_view)
        val displayNameTextView: TextView = view.findViewById(R.id.display_name_text_view)

        folderNameTextView.text = folder.name
        displayNameTextView.text = folder.owner?.displayName

        view.setOnClickListener {
            val intent = Intent(requireContext(), FolderDetailActivity::class.java)
            val data = folder.id
            intent.putExtra(FolderDetailActivity.KEY_FOLDER_ID, data)
            requireContext().startActivity(intent)
        }

        linearLayout.addView(view)
    }
}