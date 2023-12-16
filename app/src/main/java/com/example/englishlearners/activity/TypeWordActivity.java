package com.example.englishlearners.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.englishlearners.R;
import com.example.englishlearners.fragment.HomeFragment;

public class TypeWordActivity extends AppCompatActivity {
    Button btn_eng;
    Button btn_vn;
    ImageView iv_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_word);

        btn_eng = findViewById(R.id.type_word_eng);
        btn_vn = findViewById(R.id.type_word_vn);
        iv_back = findViewById(R.id.back);

        btn_eng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TypeWordActivity.this, TypeWordEnglish.class);
                startActivity(intent);
            }
        });
        btn_vn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TypeWordActivity.this, TypeWordViet.class);
                startActivity(intent);
            }
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TypeWordActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}