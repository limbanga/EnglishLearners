package com.example.englishlearners.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.englishlearners.R;
import com.example.englishlearners.adapter.ViewListVocaularyAdapter;
import com.example.englishlearners.model.Vocabulary;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.util.Locale;

public class TypeWordListVocabularyViet extends AppCompatActivity {
    ViewListVocaularyAdapter adt;
    ViewListVocaularyAdapter adt2;
    ListView listView;
    private TextToSpeech textToSpeech;
    CardView cv_correct;
    CardView cv_incorrect;
    ImageView iv_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_choice_list_vocabulary);
        listView = findViewById(R.id.list);
        cv_correct = findViewById(R.id.cv_corect);
        cv_incorrect = findViewById(R.id.cv_incorect);

        adt = new ViewListVocaularyAdapter(this,TypeWordViet.IncorectWord,textToSpeech);
        adt2 = new ViewListVocaularyAdapter(this,TypeWordViet.CorectWord,textToSpeech);
        listView.setAdapter(adt);
        back();
        cv_correct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                listView.setAdapter(adt2);
                cv_correct.setCardBackgroundColor(Color.parseColor("#EAF6FA"));
                cv_incorrect.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
            }
        });
        cv_incorrect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listView.setAdapter(adt);
                cv_incorrect.setCardBackgroundColor(Color.parseColor("#EAF6FA"));
                cv_correct.setCardBackgroundColor(Color.parseColor("#FFFFFF"));

            }
        });
    }
    private void back(){
        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TypeWordViet.CorectWord.clear();
                TypeWordViet.IncorectWord.clear();
                Intent intent = new Intent(TypeWordListVocabularyViet.this, MainActivity.class);
                startActivity(intent);

            }
        });

    }
}
