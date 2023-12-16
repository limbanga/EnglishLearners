package com.example.englishlearners.activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.englishlearners.R
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var backButton: ImageView
    private lateinit var oldPasswordEditText: EditText
    private lateinit var newPasswordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var saveChangeButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        // get view
        backButton = findViewById(R.id.back_btn)
        oldPasswordEditText = findViewById(R.id.old_pass)
        newPasswordEditText = findViewById(R.id.new_password)
        confirmPasswordEditText = findViewById(R.id.re_password)
        saveChangeButton = findViewById(R.id.save_change_btn)
        // set event
        backButton.setOnClickListener {
            finish()
        }
        saveChangeButton.setOnClickListener {
            val oldPassword = oldPasswordEditText.text.toString()
            val newPassword = newPasswordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()
            if (!newPassword.equals(confirmPassword, true)) {
                Toast.makeText(this, "Xác nhận mật khẩu không khớp", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            changePassword(oldPassword, newPassword)
        }

    }

    private fun changePassword(oldPassword: String, newPassword: String) {
        val user = FirebaseAuth.getInstance().currentUser

        user?.let {
            val credential = EmailAuthProvider.getCredential(user.email!!, oldPassword)
            user.reauthenticate(credential)
                .addOnCompleteListener { reAuthTask ->
                    if (reAuthTask.isSuccessful) {
                        user.updatePassword(newPassword)
                            .addOnCompleteListener { updatePasswordTask ->
                                if (updatePasswordTask.isSuccessful) {
                                    Toast.makeText(
                                        this,
                                        "Đổi mật khẩu thành công.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    finish()
                                } else {
                                    Toast.makeText(this, "Có lỗi xảy ra.", Toast.LENGTH_SHORT)
                                        .show()
                                    Toast.makeText(
                                        this,
                                        "Không thể đổi mật khẩu",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    } else {
                        Toast.makeText(this, "Mật khẩu cũ không đúng.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

}