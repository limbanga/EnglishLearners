package com.example.englishlearners.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.englishlearners.R;

public class MultipleChoiceViewResult extends AppCompatActivity {
    TextView tv_score;
    TextView tv_fb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_choice_view_result);
        tv_score = findViewById(R.id.score);
        tv_fb = findViewById(R.id.tv_feedback);
        int score = MultipleChoiceActivity.score;
        int size = MultipleChoiceActivity.vocabularies_size;
        float per= (float)score/size;
        if(per < 0.2){
            tv_fb.setText("Số câu trả lời đúng của bạn chỉ đạt "+(per*100)+"% ở mức rất thấp bạn cần học nhiều hơn để cải thiện điểm số" );
        } else if (per < 0.5) {
            tv_fb.setText("Số câu trả lời đúng của bạn chỉ đạt "+(per*100)+"% ở mức thấp bạn cần học nhiều hơn để cải thiện điểm số" );
        } else if (per < 0.7) {
            tv_fb.setText("Số câu trả lời đúng của bạn chỉ đạt "+(per*100)+"% ở mức trung bình bạn có thể học lại để cải thiện điểm số" );
        }
        else  if (per == 1) {
            tv_fb.setText("Số câu trả lời đúng của bạn chỉ đạt "+(per*100)+"%. Bây giờ bạn có thể học những chủ đề khác" );
        } else  {
            tv_fb.setText("Số câu trả lời đúng của bạn chỉ đạt "+(per*100)+"% ở mức tốt bạn cần chút ít thời gian để hoàn thành" );
        }
        tv_score.setText("Điểm: "+score+"/"+size);

    }
}