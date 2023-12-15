package com.example.englishlearners.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.englishlearners.FirebaseService
import com.example.englishlearners.R
import com.example.englishlearners.activity.LoginActivity
import com.example.englishlearners.activity.MainActivity
import com.example.englishlearners.model.AppUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.launch
import java.util.*


class ProfileFragment : Fragment() {

    private val CHOOSE_IMAGE_CODE = 349008

    private val mAuth = FirebaseAuth.getInstance()
    private var firebaseUser: FirebaseUser? = null
    private lateinit var appUser: AppUser
    private val database = Firebase.database

    private lateinit var logoutButton: Button
    private lateinit var displayNameTextView: TextView
    private lateinit var avatarImage: CircleImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view1 = inflater.inflate(R.layout.fragment_profile, container, false)
        // get current user
        firebaseUser = MainActivity.getFireBaseUser(requireContext())
        // map view
        logoutButton = view1.findViewById(R.id.logout_btn)
        displayNameTextView = view1.findViewById(R.id.display_name_text_view)
        avatarImage = view1.findViewById(R.id.avatar_image)
        // set event
        logoutButton.setOnClickListener {
            signOut()
        }
        avatarImage.setOnClickListener {
            // Sử dụng Intent để chọn ảnh từ thiết bị
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, CHOOSE_IMAGE_CODE)
        }

        // init data
        loadData()
        return view1
    }

    private fun loadData() {

        lifecycleScope.launch {
            val tempUser = FirebaseService.getUser(firebaseUser!!.uid)

            if (tempUser == null) {
                Toast.makeText(requireContext(), "User không tồn tại", Toast.LENGTH_SHORT).show()
                return@launch
            }
            bindingAppUser(tempUser)
        }
    }

    private fun bindingAppUser(appUser: AppUser) {
        this.appUser = appUser
        Glide.with(this)
            .load(appUser.imgPath)
            .into(avatarImage)
        displayNameTextView.text = appUser.displayName
    }

    private fun signOut() {
        mAuth.signOut()
        Toast.makeText(requireContext(), "Đăng xuất thành công", Toast.LENGTH_SHORT).show()
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CHOOSE_IMAGE_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                lifecycleScope.launch {
                    val result = FirebaseService.uploadImage(uri)
                    if (result == null) {

                        Toast.makeText(
                            requireContext(),
                            "Không thể thay đổi avatar.",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@launch
                    }

                    Toast.makeText(
                        requireContext(),
                        "Thay đổi avatar thành công.",
                        Toast.LENGTH_SHORT
                    ).show()
                    val updateResult = FirebaseService.updateImagePathForUser(firebaseUser!!.uid, result)
                }
            }
        }
    }


}