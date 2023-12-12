package com.example.englishlearners.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.englishlearners.R
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.example.englishlearners.model.Vocabulary
import com.example.englishlearners.FirebaseService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
class FlashCardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flash_card)
        GlobalScope.launch(Dispatchers.Main) {
            val vocabularies = getVocabularies("-NlK5ffZxq2IG5lk6oo8")
            showToast("Vocabulary List Length: ${vocabularies.size}")
        }
    }

    private suspend fun getVocabularies(topicId: String): List<Vocabulary> {
        return withContext(Dispatchers.IO) {
            // Use FirebaseService to get the vocabularies
            FirebaseService.getVocabularies(topicId)
        }
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}