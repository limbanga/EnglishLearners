package com.example.englishlearners.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.englishlearners.R;
import com.example.englishlearners.model.Vocabulary;
import com.example.englishlearners.FirebaseService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class MultipleChoiceActivity extends AppCompatActivity {
    RelativeLayout Rl_answerA;
    RelativeLayout Rl_answerB;
    RelativeLayout Rl_answerC;
    RelativeLayout Rl_answerD;
    TextView Tv_answerA;
    TextView Tv_answerB;
    TextView Tv_answerC;
    TextView Tv_answerD;
    TextView Tv_question;
    TextView tv_curentState;
    TextView tv_score;
    int score = 0;
    int currentIndex = 1;
    Button btn_continous;
    ArrayList<Vocabulary> CorectWord = new ArrayList<Vocabulary>();
    ArrayList<Vocabulary> IncorectWord = new ArrayList<Vocabulary>();
    private final Handler autoTransferHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_choice);
        moveOnQuestion();
    }
    private void generateQuestion(Vocabulary correctAnswer) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("vocabularies");

        // Query the data based on the "topicId"
        // myRef.orderByChild("topicId").equalTo(topicId).addListenerForSingleValueEvent(new ValueEventListener() {
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Vocabulary> allVocabularies = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Vocabulary vocabulary = snapshot.getValue(Vocabulary.class);
                    if (vocabulary != null) {
                        vocabulary.setId(snapshot.getKey());
                        allVocabularies.add(vocabulary);
                    }
                }

                Collections.shuffle(allVocabularies);

                // Lấy index của correctAnswer trong danh sách xáo trộn
                int correctAnswerIndex = allVocabularies.indexOf(correctAnswer);

                // Tạo danh sách đáp án (bao gồm correctAnswer và 3 đáp án ngẫu nhiên khác)
                ArrayList<Vocabulary> answerOptions = new ArrayList<>();
                answerOptions.add(correctAnswer);

                // Lấy 3 từ vựng ngẫu nhiên từ danh sách xáo trộn (không bao gồm correctAnswer)
                for (int i = 0; i < allVocabularies.size(); i++) {
                    if (answerOptions.size()<4) {
                        if(allVocabularies.get(i).getId().equals(correctAnswer.getId())==false){
                            answerOptions.add(allVocabularies.get(i));
                        }

                    }
                    else {
                        break;
                    }
                }

                // Xáo trộn lại danh sách đáp án
                Collections.shuffle(answerOptions);

                Tv_answerA = findViewById(R.id.textViewAnswerA);
                Tv_answerB = findViewById(R.id.textViewAnswerB);
                Tv_answerC = findViewById(R.id.textViewAnswerC);
                Tv_answerD = findViewById(R.id.textViewAnswerD);
                Tv_question = findViewById(R.id.txt_question);

                Rl_answerA = findViewById(R.id.AnswerA);
                Rl_answerB = findViewById(R.id.AnswerB);
                Rl_answerC = findViewById(R.id.AnswerC);
                Rl_answerD = findViewById(R.id.AnswerD);

                Tv_answerA.setText(answerOptions.get(0).getTerm());
                Tv_answerB.setText(answerOptions.get(1).getTerm());
                Tv_answerC.setText(answerOptions.get(2).getTerm());
                Tv_answerD.setText(answerOptions.get(3).getTerm());

                Tv_question.setText("Từ nào sau đây có nghĩa là " + correctAnswer.getDefinition() + "?");

                Rl_answerA.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if(Tv_answerA.getText().toString().equals(correctAnswer.getTerm())){
                            showToast("Đúng");
                            Rl_answerA.setBackgroundColor(Color.GREEN);
                            Rl_answerA.setEnabled(false);
                            Rl_answerB.setEnabled(false);
                            Rl_answerC.setEnabled(false);
                            Rl_answerD.setEnabled(false);
                            score++;
                            CorectWord.add(correctAnswer);
                            tv_score = findViewById(R.id.score);
                            tv_score.setText("Điểm: "+score);
                        }
                        else {
                            showToast("Sai");
                            Rl_answerA.setBackgroundColor(Color.RED);
                            Rl_answerA.setEnabled(false);
                            Rl_answerB.setEnabled(false);
                            Rl_answerC.setEnabled(false);
                            Rl_answerD.setEnabled(false);
                            IncorectWord.add(correctAnswer);
                            tv_score = findViewById(R.id.score);
                            tv_score.setText("Điểm: "+score);
                        }

                    }

                });
                Rl_answerB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(Tv_answerB.getText().toString().equals(correctAnswer.getTerm())){
                            showToast("Đúng");
                            Rl_answerB.setBackgroundColor(Color.GREEN);
                            Rl_answerA.setEnabled(false);
                            Rl_answerB.setEnabled(false);
                            Rl_answerC.setEnabled(false);
                            Rl_answerD.setEnabled(false);
                            score++;
                            CorectWord.add(correctAnswer);
                            tv_score = findViewById(R.id.score);
                            tv_score.setText("Điểm: "+score);
                        }
                        else {
                            showToast("Sai");
                            Rl_answerB.setBackgroundColor(Color.RED);
                            Rl_answerA.setEnabled(false);
                            Rl_answerB.setEnabled(false);
                            Rl_answerC.setEnabled(false);
                            Rl_answerD.setEnabled(false);
                            IncorectWord.add(correctAnswer);
                            tv_score = findViewById(R.id.score);
                            tv_score.setText("Điểm: "+score);
                        }

                    }

                });
                Rl_answerC.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(Tv_answerC.getText().toString().equals(correctAnswer.getTerm())){
                            showToast("Đúng");
                            Rl_answerC.setBackgroundColor(Color.GREEN);
                            Rl_answerA.setEnabled(false);
                            Rl_answerB.setEnabled(false);
                            Rl_answerC.setEnabled(false);
                            Rl_answerD.setEnabled(false);
                            score++;
                            CorectWord.add(correctAnswer);
                            tv_score = findViewById(R.id.score);
                            tv_score.setText("Điểm: "+score);
                        }
                        else {
                            showToast("Sai");
                            Rl_answerC.setBackgroundColor(Color.RED);
                            Rl_answerA.setEnabled(false);
                            Rl_answerB.setEnabled(false);
                            Rl_answerC.setEnabled(false);
                            Rl_answerD.setEnabled(false);
                            IncorectWord.add(correctAnswer);
                            tv_score = findViewById(R.id.score);
                            tv_score.setText("Điểm: "+score);
                        }

                    }
                });
                Rl_answerD.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(Tv_answerD.getText().toString().equals(correctAnswer.getTerm())){
                            showToast("Đúng");
                            Rl_answerD.setBackgroundColor(Color.GREEN);
                            Rl_answerA.setEnabled(false);
                            Rl_answerB.setEnabled(false);
                            Rl_answerC.setEnabled(false);
                            tv_score = findViewById(R.id.score);
                            tv_score.setText("Điểm: "+score);
                        }
                        else {
                            showToast("Sai");
                            Rl_answerD.setBackgroundColor(Color.RED);
                            Rl_answerA.setEnabled(false);
                            Rl_answerB.setEnabled(false);
                            Rl_answerC.setEnabled(false);
                            IncorectWord.add(correctAnswer);
                            tv_score = findViewById(R.id.score);
                            tv_score.setText("Điểm: "+score);
                        }

                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle onCancelled event
            }
        });
    }
    private void resetQuestion(){
        Rl_answerA = findViewById(R.id.AnswerA);
        Rl_answerB = findViewById(R.id.AnswerB);
        Rl_answerC = findViewById(R.id.AnswerC);
        Rl_answerD = findViewById(R.id.AnswerD);
        Rl_answerA.setEnabled(true);
        Rl_answerB.setEnabled(true);
        Rl_answerC.setEnabled(true);
        Rl_answerD.setEnabled(true);
        Rl_answerA.setBackgroundColor(Color.WHITE);
        Rl_answerB.setBackgroundColor(Color.WHITE);
        Rl_answerC.setBackgroundColor(Color.WHITE);
        Rl_answerD.setBackgroundColor(Color.WHITE);
    }
    private void checkAnswer(String selectedAnswer, String corectAnswer) {

        if (selectedAnswer.equals(corectAnswer)) {

            Toast.makeText(getApplicationContext(), "Đúng!", Toast.LENGTH_SHORT).show();

        } else {

            Toast.makeText(getApplicationContext(), "Sai!", Toast.LENGTH_SHORT).show();
        }

    }

    private void moveOnQuestion(){

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
                tv_curentState=findViewById(R.id.currentState);
                tv_score = findViewById(R.id.score);
                tv_curentState.setText(currentIndex+"/"+vocabularies.size());

                btn_continous = findViewById(R.id.continous);
                generateQuestion(vocabularies.get(0));
                btn_continous.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(currentIndex<vocabularies.size()){
                            resetQuestion();
                            generateQuestion(vocabularies.get(currentIndex));
                            currentIndex++;
                            tv_curentState.setText(currentIndex+"/"+vocabularies.size());
                        }
                        else {

                        }



                    }
                });


            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
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
                generateQuestion(vocabularies.get(0));

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
