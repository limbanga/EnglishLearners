package com.example.englishlearners.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.speech.tts.TextToSpeech;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.englishlearners.R;
import com.example.englishlearners.activity.FlashCardActivity;
import com.example.englishlearners.model.Vocabulary;
import java.util.ArrayList;
import java.util.Locale;


public class ViewListVocaularyAdapter extends ArrayAdapter<Vocabulary> {
    private ArrayList<Vocabulary> items;
    private ArrayList<Boolean> itemcheck;
    private Context context;
    private TextToSpeech textToSpeech;

    public ViewListVocaularyAdapter(@NonNull Context context, ArrayList<Vocabulary> items,TextToSpeech textToSpeech) {
        super(context, 0, items);
        this.items = items;
        this.context = context;
        this.textToSpeech = textToSpeech;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_item_vocabulary, parent, false);
        }
        TextView tv_term = convertView.findViewById(R.id.tv_term);
        TextView tv_defination = convertView.findViewById(R.id.tv_defination);
        ImageView iv_audio = convertView.findViewById(R.id.iv_audio);
        tv_term.setText(items.get(position).getTerm());
        tv_defination.setText(items.get(position).getDefinition());

        textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int langResult = textToSpeech.setLanguage(Locale.US);
                    if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                        //  Toast.makeText(FlashCardActivity.this, "Text-to-Speech is not supported on your device.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //Toast.makeText(FlashCardActivity.this, "Initialization failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        iv_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String termToSpeak = getItem(position).getTerm();
                speak(termToSpeak);
            }
        });
        return convertView;
    }



    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int langResult = textToSpeech.setLanguage(Locale.US);
            if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                //Toast.makeText(this, "Text-to-Speech is not supported on your device.", Toast.LENGTH_SHORT).show();
            }
        } else {

        }
    }

    private void speak(String text) {
        if (textToSpeech != null) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }





}
