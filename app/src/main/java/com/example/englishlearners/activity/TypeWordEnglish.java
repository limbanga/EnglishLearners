package com.example.englishlearners.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.Locale;

public class TypeWordEnglish extends AppCompatActivity {
    TextView tv_quesion;
    EditText edt_answer;
    Button btn_complete;
    public static int score = 0;
    int currentIndex = 0;
    TextView tv_score;
    TextView tv_curentState;
    View feedbackLayout;
    TextView tv_corect_word;
    ImageView iv_back;
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_word_english);
        getVocabularies("-NlcBn6rw-jpTzwukjj6");
        back();
    }
    private void back(){
        iv_back = findViewById(R.id.back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TypeWordEnglish.this, TypeWordActivity.class);
                startActivity(intent);
                score = 0;
            }
        });

    }
    private boolean Question(Vocabulary word){
        tv_quesion = findViewById(R.id.textViewWord);
        edt_answer = findViewById(R.id.edt_answer);
        tv_quesion.setText(word.getDefinition());
        SpeechAutomatical(word.getTerm());


        if(edt_answer.getText().toString().equals(word.getTerm())){

            return true;


        }
        else {
            return false;
        }


    }
    private void getVocabularies(String topicId) {

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("vocabularies");

        // Query the data based on the "topicId"
       myRef.orderByChild("topicId").equalTo(topicId).addListenerForSingleValueEvent(new ValueEventListener() {
        //myRef.addListenerForSingleValueEvent(new ValueEventListener() {
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

                if(currentIndex<vocabularies.size()){
                    tv_quesion = findViewById(R.id.textViewWord);
                    tv_quesion.setText(vocabularies.get(currentIndex).getDefinition());
                    btn_complete = findViewById(R.id.CompleteAnswer);
                    tv_score = findViewById(R.id.score);
                    edt_answer = findViewById(R.id.edt_answer);
                    tv_curentState = findViewById(R.id.currentState);
                    tv_curentState.setText(currentIndex+"/"+vocabularies.size());



                    btn_complete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                                if(Question(vocabularies.get(currentIndex))){
                                    feedbackLayout = getLayoutInflater().inflate(R.layout.feedback_correct_type_word, null);
                                    tv_corect_word = feedbackLayout.findViewById(R.id.tv_correct_word1);
                                    tv_corect_word.setText(vocabularies.get(currentIndex).getTerm());
                                    ViewGroup rootView = findViewById(android.R.id.content);
                                    rootView.addView(feedbackLayout);
                                    feedbackLayout.setVisibility(View.GONE);
                                    score ++ ;
                                    feedbackLayout.setVisibility(View.VISIBLE);
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            feedbackLayout.setVisibility(View.GONE);
                                            edt_answer.setText("");
                                            currentIndex ++ ;
                                            tv_score.setText("Điểm: "+score);
                                            tv_curentState.setText(currentIndex+"/"+vocabularies.size());

                                            getVocabularies("-NlcBn6rw-jpTzwukjj6");
                                        }
                                    }, 2000);
                                }
                                else {
                                    feedbackLayout = getLayoutInflater().inflate(R.layout.feedback_incorrect_type_word, null);
                                    tv_corect_word = feedbackLayout.findViewById(R.id.tv_correct_word);
                                    tv_corect_word.setText(vocabularies.get(currentIndex).getTerm());
                                    ViewGroup rootView = findViewById(android.R.id.content);
                                    rootView.addView(feedbackLayout);
                                    feedbackLayout.setVisibility(View.GONE);

                                    feedbackLayout.setVisibility(View.VISIBLE);
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            feedbackLayout.setVisibility(View.GONE);
                                            edt_answer.setText("");
                                            currentIndex ++ ;
                                            tv_score.setText("Điểm: "+score);
                                            tv_curentState.setText(currentIndex+"/"+vocabularies.size());

                                            getVocabularies("-NlcBn6rw-jpTzwukjj6");
                                        }
                                    }, 2000);
                                }





                        }
                    });
                }
                else {
                    Intent intent = new Intent(TypeWordEnglish.this, TypeWordResult.class);
                    startActivity(intent);
                }




            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void SpeechAutomatical(String word) {
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int langResult = textToSpeech.setLanguage(Locale.US);
                    if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                        //Toast.makeText(FlashCardActivity.this, "Text-to-Speech is not supported on your device.", Toast.LENGTH_SHORT).show();
                    } else {
                        speak(word);
                    }
                } else {

                }
            }
        });
    }


    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int langResult = textToSpeech.setLanguage(Locale.US);
            if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                //Toast.makeText(this, "Text-to-Speech is not supported on your device.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Initialization failed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void speak(String text) {
        if (textToSpeech != null) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }


}