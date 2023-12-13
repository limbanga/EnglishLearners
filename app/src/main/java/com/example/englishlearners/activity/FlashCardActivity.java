package com.example.englishlearners.activity;

import android.os.Bundle;
import android.view.View;
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



public class FlashCardActivity extends AppCompatActivity {

    private int currentCardIndex = 0;
    private ArrayList<Vocabulary> vocabularies;
    private ViewFlipper viewFlipper;
    private TextView txt_content;
    private CardView cardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_card);

        viewFlipper = findViewById(R.id.flipper);
        cardView = findViewById(R.id.cardview);

        viewFlipper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewFlipper.getDisplayedChild() == 0) {
                    viewFlipper.showNext();
                } else {

                    viewFlipper.showPrevious();
                }
            }
        });


    }
    private void showFlashCard(String term, String definition) {
        txt_content = findViewById(R.id.txt_content);
        txt_content.setText(term);
    }
    private void getVocabularies(String topicId) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("vocabularies");

        // Query the data based on the "topicId"
        myRef.orderByChild("topicId").equalTo(topicId).addListenerForSingleValueEvent(new ValueEventListener() {
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




    private void showNextCard() {
        if (currentCardIndex < vocabularies.size() - 1) {
            currentCardIndex++;

        } else {
            Toast.makeText(this, "Already on the last card", Toast.LENGTH_SHORT).show();
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
