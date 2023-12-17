package com.example.englishlearners.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.englishlearners.R;

import java.io.BufferedInputStream;

public class FlashCardStart extends AppCompatActivity {
    String topicID;
    ImageView iv_back;
    Button btn_start;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_card_start);
        Intent intent = getIntent() ;
        topicID = intent.getStringExtra("KEY_TOPIC_ID");
        back();
        Start();
    }
    private void Start(){
        btn_start = findViewById(R.id.btn_start);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(FlashCardStart.this, FlashCardActivity.class);
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
                Intent back = new Intent(FlashCardStart.this, MainActivity.class);
                startActivity(back);
            }
        });
    }
}