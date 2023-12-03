package com.example.englishlearners.dialog

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.englishlearners.AppConst
import com.example.englishlearners.R
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FolderDialog(context : Context): Dialog(context) {
    init {
        val view: View = layoutInflater.inflate(R.layout.add_folder_dialog_layout, null)
        this.setContentView(view)
        // set width and height
        this.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // get dialog view
        val cancelButton: Button = view.findViewById(R.id.cancel_button)
        val okButton: Button = view.findViewById(R.id.ok_btn)
        val nameEditText: EditText = view.findViewById(R.id.folder_name_edit_text)
        val descEditText: EditText = view.findViewById(R.id.folder_desc_edit_text)

        cancelButton.setOnClickListener {
            this.dismiss()
        }

        okButton.setOnClickListener{
            val database = Firebase.database
            //
            val myRef = database.getReference(AppConst.KEY_FOLDER)
            val newRef = myRef.push()
            val data = mapOf(
                "name" to nameEditText.text.toString(),
                "desc" to descEditText.text.toString(),
            )
            newRef.setValue(data)
            { databaseError, _ ->
                if (databaseError != null) {
                    Toast.makeText(context, "Some thing went wrong", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Folder created successfully!", Toast.LENGTH_LONG).show()
                    this.dismiss()
                }
            }
        }
    }
}