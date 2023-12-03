package com.example.englishlearners

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChangeTopicActivity : AppCompatActivity() {

    lateinit var titleEditText: EditText
    lateinit var saveChangeButton: ImageView
    lateinit var backButton: ImageView

    private val database = Firebase.database


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_topic)

        titleEditText = findViewById(R.id.title_edit_text)
        saveChangeButton = findViewById(R.id.save_change_btn)
        backButton = findViewById(R.id.back_btn)

        saveChangeButton.setOnClickListener {
            val myRef = database.getReference(AppConst.KEY_TOPIC)
            val newRef = myRef.push()
            val data = mapOf(
                "title" to titleEditText.text.toString(),
            )
            newRef.setValue(data)
            { databaseError, _ ->
                if (databaseError != null) {
                    Toast.makeText(this, "Some thing went wrong", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Topic created successfully!", Toast.LENGTH_LONG).show()
                    titleEditText.text.clear()
                }
            }

        }

        backButton.setOnClickListener{
            finish()
        }
    }


}