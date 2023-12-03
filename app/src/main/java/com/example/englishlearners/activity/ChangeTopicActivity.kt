package com.example.englishlearners.activity

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.englishlearners.AppConst
import com.example.englishlearners.R
import com.example.englishlearners.model.Vocabulary
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChangeTopicActivity : AppCompatActivity() {

    private val database = Firebase.database
    private var list: ArrayList<Vocabulary> = ArrayList<Vocabulary>()

    private lateinit var linearLayout: LinearLayout
    private lateinit var titleEditText: EditText
    private lateinit var saveChangeButton: ImageView
    private lateinit var backButton: ImageView
    private lateinit var addMoreButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_topic)

        linearLayout = findViewById(R.id.list)
        titleEditText = findViewById(R.id.title_edit_text)
        saveChangeButton = findViewById(R.id.save_change_btn)
        backButton = findViewById(R.id.back_btn)
        addMoreButton = findViewById(R.id.add_more_btn)

        saveChangeButton.setOnClickListener {
            val myRef = database.getReference(AppConst.KEY_TOPIC)
            val newRef = myRef.push()
            val data = mapOf(
                "title" to titleEditText.text.toString(),
                AppConst.KEY_VOCABULARY to list
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

        backButton.setOnClickListener {
            finish()
        }

        addMoreButton.setOnClickListener {
            val dialog = Dialog(this)
            val view: View = layoutInflater.inflate(R.layout.dialog_vocabulary, null)
            dialog.setContentView(view)
            // set width and height
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            // get dialog view
            val cancelButton: Button = view.findViewById(R.id.cancel_button)
            val okButton: Button = view.findViewById(R.id.ok_btn)
            val termEditText: EditText = view.findViewById(R.id.term_edit_text)
            val definitionEditText: EditText = view.findViewById(R.id.definition_edit_text)

            cancelButton.setOnClickListener {
                dialog.dismiss()
            }

            okButton.setOnClickListener {
                Toast.makeText(this, "term ${termEditText.text.toString()}", Toast.LENGTH_LONG)
                    .show()

                val vocabulary = Vocabulary(
                    "",
                    termEditText.text.toString(),
                    definitionEditText.text.toString()
                )

                list.add(vocabulary)
                initCard(vocabulary)
                dialog.dismiss()
            }

//                val myRef = database.getReference(AppConst.KEY_FOLDER)
//                val newRef = myRef.push()
//                val data = mapOf(
//                    "name" to nameEditText.text.toString(),
//                    "desc" to descEditText.text.toString(),
//                )
//                newRef.setValue(data)
//                { databaseError, _ ->
//                    if (databaseError != null) {
//                        Toast.makeText(this, "Some thing went wrong", Toast.LENGTH_LONG).show()
//                    } else {
//                        Toast.makeText(this, "Folder created successfully!", Toast.LENGTH_LONG).show()
//                        dialog.dismiss()
//                    }
//                }
            //}
            dialog.show()

        }
    }

    private fun initCard(vocabulary: Vocabulary) {
        val view: View = layoutInflater.inflate(R.layout.item_vocabulary_view, linearLayout, false)

        val termTextView: TextView = view.findViewById(R.id.term_text_view)
        val definitionTextView: TextView = view.findViewById(R.id.definition_text_view)

        // Log.d(AppConst.DEBUG_TAG, "title"+ topic.title)

        termTextView.text = vocabulary.term
        definitionTextView.text = vocabulary.definition

        linearLayout.addView(view)
    }
}