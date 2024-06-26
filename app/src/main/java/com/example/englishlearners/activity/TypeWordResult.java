package com.example.englishlearners.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.englishlearners.R;

public class TypeWordResult extends AppCompatActivity {
    TextView tv_score;
    Button btn_exit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_word_result);
        tv_score = findViewById(R.id.score);
        tv_score.setText("Điểm: "+TypeWordEnglish.score+"/"+(TypeWordEnglish.IncorectWord.size()+ TypeWordEnglish.CorectWord.size()));
        btn_exit = findViewById(R.id.btn_exit);
        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(TypeWordResult.this, TypeWordListVocabularyEng.class);
                startActivity(intent);

            }
        });
    }
}