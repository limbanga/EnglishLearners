package com.example.englishlearners.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.englishlearners.R;

public class MultipleChoiceStart extends AppCompatActivity {
    String topicID;
    Button btn_eng;
    Button btn_vn;
    ImageView iv_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_choice_start);
        Intent intent = getIntent() ;
        topicID = intent.getStringExtra("KEY_TOPIC_ID");
        back();
        btn_eng_click();
    }

    private  void btn_eng_click(){
        btn_eng = findViewById(R.id.multiple_word_eng);
        btn_eng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(MultipleChoiceStart.this, MultipleChoiceActivity.class);
                it.putExtra("KEY_TOPIC_ID2", topicID);
                startActivity(it);
            }
        });
    }
    private void back(){
        iv_back = findViewById(R.id.back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back = new Intent(MultipleChoiceStart.this, MainActivity.class);
                startActivity(back);
            }
        });
    }
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


}