package com.example.englishlearners.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.englishlearners.AppConst
import com.example.englishlearners.R
import com.example.englishlearners.activity.LoginActivity
import com.example.englishlearners.model.AppUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class ProfileFragment : Fragment() {

    private var firebaseUser: FirebaseUser? = null
    private lateinit var mAuth : FirebaseAuth
    private val database = Firebase.database

    private lateinit var logoutButton: Button
    private lateinit var displayNameTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val view1 = requireView()
        // get current user
        mAuth = FirebaseAuth.getInstance()
        firebaseUser = mAuth.currentUser
        if (firebaseUser == null) {
            // User is not logged in
            val intent = Intent (requireContext(), LoginActivity::class.java)
            requireActivity().startActivity(intent)
            return
        }

        // map view
        logoutButton = view1.findViewById(R.id.logout_btn)
        displayNameTextView = view1.findViewById(R.id.display_name_text_view)
        // set event
        logoutButton.setOnClickListener{
            signOut()
        }
        // init data
        loadUserData()
    }

    private fun loadUserData() {
        val myRef = database.getReference(AppConst.KEY_USER).child(firebaseUser!!.uid)
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val appUser: AppUser? = dataSnapshot.getValue(AppUser::class.java)
                if (appUser != null) {
                    displayNameTextView.text = appUser.displayName
                    // Đã lấy được thông tin của AppUser từ Firebase
                    // Sử dụng thông tin ở đây (ví dụ: hiển thị thông tin, xử lý dữ liệu, ...)
                } else {
                    Toast.makeText(requireContext(), "Có lỗi xảy ra.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(requireContext(), "Có lỗi xảy ra.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun signOut() {
        mAuth.signOut()
        Toast.makeText(requireContext(), "Đăng xuất thành công", Toast.LENGTH_SHORT).show()
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
    }
}