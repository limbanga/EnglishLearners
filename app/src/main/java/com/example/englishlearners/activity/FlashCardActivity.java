package com.example.englishlearners.activity;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.englishlearners.R;
import com.example.englishlearners.model.Vocabulary;
import com.example.englishlearners.FirebaseService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;


public class FlashCardActivity extends AppCompatActivity {

    private int currentCardIndex = 0;
    private ArrayList<Vocabulary> vocabularies;
    private ViewFlipper viewFlipper;
    private TextView txt_content;
    private TextView txt_back;
    private CardView cardView;
    private TextToSpeech textToSpeech;
    private ImageView imageView;
    private   ArrayList<Vocabulary>word = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_card);
        FlashCard("-NlK5ffZxq2IG5lk6oo8");

//        viewFlipper = findViewById(R.id.flipper);
//        cardView = findViewById(R.id.cardview);
//
//        viewFlipper.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (viewFlipper.getDisplayedChild() == 0) {
//                    viewFlipper.showNext();
//                } else {
//
//                    viewFlipper.showPrevious();
//                }
//            }
//        });
//        Speech("Cat");

    }
    private void showFlashCard(String term, String definition) {
        txt_content = findViewById(R.id.txt_content);
        txt_content.setText(term);
    }
    private void getVocabularies(String topicId) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("vocabularies");

        // Query the data based on the "topicId"
       //myRef.orderByChild("topicId").equalTo(topicId).addListenerForSingleValueEvent(new ValueEventListener() {
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Vocabulary>    vocabularies = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Vocabulary vocabulary = snapshot.getValue(Vocabulary.class);
                    if (vocabulary != null) {
                        vocabulary.setId(snapshot.getKey());
                        vocabularies.add(vocabulary);

                    } else {
                       showToast("null");
                    }
                }
                showToast(String.valueOf(vocabularies.size()));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //flascard list

    private void FlashCard(String topicId){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("vocabularies");

        // Query the data based on the "topicId"
        //myRef.orderByChild("topicId").equalTo(topicId).addListenerForSingleValueEvent(new ValueEventListener() {
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Vocabulary>    vocabularies = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Vocabulary vocabulary = snapshot.getValue(Vocabulary.class);
                    if (vocabulary != null) {
                        vocabulary.setId(snapshot.getKey());
                        vocabularies.add(vocabulary);

                    } else {
                        showToast("null");
                    }
                }
                txt_content = findViewById(R.id.txt_content);
                txt_back = findViewById(R.id.txt_back);
                viewFlipper = findViewById(R.id.flipper);
                cardView = findViewById(R.id.cardview);
                txt_content.setText(vocabularies.get(0).getTerm());
                txt_back.setText(vocabularies.get(0).getDefinition());
                Speech(vocabularies.get(0).getTerm());
                SpeechAutomatical(vocabularies.get(0).getTerm());
                viewFlipper.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (viewFlipper.getDisplayedChild() == 0) {
                            viewFlipper.showNext();
                            Speech(vocabularies.get(0).getDefinition());
                            SpeechAutomatical(vocabularies.get(0).getDefinition());
                        } else {

                            viewFlipper.showPrevious();
                            Speech(vocabularies.get(0).getTerm());
                            SpeechAutomatical(vocabularies.get(0).getTerm());
                        }
                    }
                });

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void showNextCard() {
        if (currentCardIndex < vocabularies.size() - 1) {
            currentCardIndex++;

        } else {
            Toast.makeText(this, "Already on the last card", Toast.LENGTH_SHORT).show();
        }
    }
    //Audio
    public void SpeechAutomatical(String word) {
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int langResult = textToSpeech.setLanguage(Locale.US);
                    if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                        //Toast.makeText(FlashCardActivity.this, "Text-to-Speech is not supported on your device.", Toast.LENGTH_SHORT).show();
                    } else {
                        // Speak the word after successful initialization
                        speak(word);
                    }
                } else {
                    Toast.makeText(FlashCardActivity.this, "Initialization failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void Speech(String word){
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int langResult = textToSpeech.setLanguage(Locale.US);
                    if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                      //  Toast.makeText(FlashCardActivity.this, "Text-to-Speech is not supported on your device.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(FlashCardActivity.this, "Initialization failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // Đặt sự kiện click cho ImageView (âm thanh)
        ImageView listenImageView = findViewById(R.id.imv_listen);
        listenImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speak(word); // Thay thế "Cat" bằng từ cần phát âm
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

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
    //End audio



    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
