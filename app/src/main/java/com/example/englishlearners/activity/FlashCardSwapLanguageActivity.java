package com.example.englishlearners.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ImageButton;
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
import java.util.Collections;
import java.util.Locale;


public class FlashCardSwapLanguageActivity extends AppCompatActivity {

    private int currentCardIndex = 0;
    private ArrayList<Vocabulary> vocabularies;
    private ViewFlipper viewFlipper;
    private TextView txt_content;
    private TextView txt_back;
    private CardView cardView;
    private TextToSpeech textToSpeech;
    private ImageView imageView;
    private ImageView iv_previous;
    private ImageView iv_next;
    private ImageView iv_swap;
    private ImageView iv_auto;
    private ImageView iv_swap_language;
    private int size;
    private int langugeStatus=0;
    TextView txt_status;
    private final Handler autoTransferHandler = new Handler();
    private   ArrayList<Vocabulary>word = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_card_swap_language);

        swapLanguge();
        previousAndNextCard();


    }
    private void swapLanguge(){
        iv_swap_language = findViewById(R.id.imv_swap_language);
        iv_swap_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FlashCardSwapLanguageActivity.this, FlashCardActivity.class);
                startActivity(intent);
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
                ArrayList<Vocabulary>    vocabularies = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Vocabulary vocabulary = snapshot.getValue(Vocabulary.class);
                    if (vocabulary != null) {
                        vocabulary.setId(snapshot.getKey());
                        //vocabularies.add(vocabulary);
                        word.add(vocabulary);
                    } else {
                        showToast("null");
                    }
                }
                //showToast(String.valueOf(word.size()));
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


            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showFlashCard(String term, String definition) {
        txt_content = findViewById(R.id.txt_content);
        txt_back = findViewById(R.id.txt_back);
        viewFlipper = findViewById(R.id.flipper);
        viewFlipper.setDisplayedChild(0);
        cardView = findViewById(R.id.cardview);

        txt_content.setText(definition);
        txt_back.setText(term);
        Speech(definition);
        SpeechAutomatical(definition);
        viewFlipper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewFlipper.getDisplayedChild() == 0) {
                    viewFlipper.showNext();
                    Speech(term);
                    SpeechAutomatical(term);

                } else {

                    viewFlipper.showPrevious();
                    Speech(definition);
                    SpeechAutomatical(definition);
                }
            }
        });



    }
    private void previousAndNextCard(){
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
                iv_auto = findViewById(R.id.imv_auto);

                int status_auto = 0;
                iv_auto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int temporaryColor = Color.RED; // Change this to your desired color
                        iv_auto.setColorFilter(temporaryColor);

                        // Revert the color back to the original after a short delay
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                iv_auto.clearColorFilter();
                            }
                        }, 1000);
                        showToast("Tự động duyệt");
                        autoTransferHandler.postDelayed(new Runnable() {
                            int a = 0;
                            @Override
                            public void run() {
                                a++;
                                if(a<vocabularies.size()){
                                    iv_next.performClick();
                                    autoTransferHandler.postDelayed(this, 3000);
                                }
                                else {
                                    autoTransferHandler.removeCallbacks(this);
                                }

                            }
                        }, 3000);
                    }
                });
                iv_swap = findViewById(R.id.imv_swap_vocabulary);
                iv_swap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int temporaryColor = Color.RED; // Change this to your desired color
                        iv_swap.setColorFilter(temporaryColor);

                        // Revert the color back to the original after a short delay
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                iv_swap.clearColorFilter();
                            }
                        }, 1000);

                        Collections.shuffle(vocabularies);
                        showToast("Xáo từ");
                        currentCardIndex = 0;

                        // Update UI with the first card after shuffling
                        showFlashCard(vocabularies.get(currentCardIndex).getTerm(), vocabularies.get(currentCardIndex).getDefinition());

                        // Update the status text
                        txt_status.setText((currentCardIndex + 1) + "/" + size);
                    }
                });
                size = vocabularies.size();
                showFlashCard(vocabularies.get(0).getTerm(),vocabularies.get(0).getDefinition());
                iv_previous = findViewById(R.id.iv_previous);
                iv_next = findViewById(R.id.iv_next);

                txt_status = findViewById(R.id.status);
                txt_status.setText((currentCardIndex+1)+"/"+size);

                iv_next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(currentCardIndex < vocabularies.size()-1){
                            currentCardIndex ++;
                            txt_status.setText((currentCardIndex+1)+"/"+size);
                        }
                        showFlashCard(vocabularies.get(currentCardIndex).getTerm(),vocabularies.get(currentCardIndex).getDefinition());
                    }
                });
                iv_previous.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(currentCardIndex > 0){
                            currentCardIndex --;
                            txt_status.setText((currentCardIndex+1)+"/"+size);
                        }
                        showFlashCard(vocabularies.get(currentCardIndex).getTerm(),vocabularies.get(currentCardIndex).getDefinition());
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

                }
            }
        });
        // Đặt sự kiện click cho ImageView (âm thanh)
        ImageView listenImageView = findViewById(R.id.imv_listen);
        listenImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int temporaryColor = Color.RED; // Change this to your desired color
                listenImageView.setColorFilter(temporaryColor);

                // Revert the color back to the original after a short delay
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        listenImageView.clearColorFilter();
                    }
                }, 1000);
                showToast("Phát âm");
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
