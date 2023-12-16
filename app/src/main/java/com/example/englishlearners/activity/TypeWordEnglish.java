package com.example.englishlearners.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.englishlearners.R;
import com.example.englishlearners.model.Vocabulary;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TypeWordEnglish extends AppCompatActivity {
    TextView tv_quesion;
    EditText edt_answer;
    Button btn_complete;
    int score = 0;
    int currentIndex = 0;
    TextView tv_curentState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_word_english);
        getVocabularies("a");
    }
    private boolean Question(Vocabulary word){
        tv_quesion = findViewById(R.id.textViewWord);
        edt_answer = findViewById(R.id.edt_answer);
        tv_quesion.setText(word.getDefinition());
        if(edt_answer.getText().equals(word.getTerm())){
            return true;
        }
        else {
            return false;
        }


    }
    private void getVocabularies(String topicId) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("vocabularies");

        // Query the data based on the "topicId"
        //myRef.orderByChild("topicId").equalTo(topicId).addListenerForSingleValueEvent(new ValueEventListener() {
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Vocabulary> vocabularies = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Vocabulary vocabulary = snapshot.getValue(Vocabulary.class);
                    if (vocabulary != null) {
                        vocabulary.setId(snapshot.getKey());
                        vocabularies.add(vocabulary);

                    } else {
                        showToast("null");
                    }

                }


                tv_quesion = findViewById(R.id.textViewWord);
                tv_quesion.setText(vocabularies.get(currentIndex).getDefinition());
                btn_complete = findViewById(R.id.CompleteAnswer);
                btn_complete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showToast(currentIndex+"");
                        if(Question(vocabularies.get(currentIndex))){
                            score ++ ;
                            showToast("Đúng");
                        }
                        else {
                            showToast("Sai");
                        }
                        currentIndex ++ ;
                        getVocabularies("abc");
                    }
                });



            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}