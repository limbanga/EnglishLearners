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
import com.example.englishlearners.R
import com.example.englishlearners.activity.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class ProfileFragment : Fragment() {
    private  lateinit var user: FirebaseUser

    private lateinit var mAuth : FirebaseAuth

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
        val view = requireView()

        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser
        if (user == null) {
            // User is not logged in
            println("User is not logged in")
            val intent = Intent (requireContext(), LoginActivity::class.java)
            requireActivity().startActivity(intent)
            return
        }

        // map view
        logoutButton = view.findViewById(R.id.logout_btn)
        displayNameTextView = view.findViewById(R.id.display_name_text_view)

        displayNameTextView.text = user.email
        logoutButton.setOnClickListener{
            signOut()
        }
    }

    private fun signOut() {
        mAuth.signOut()
        Toast.makeText(requireContext(), "Đăng xuất thành công", Toast.LENGTH_SHORT).show()
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
    }
}