package edu.anu.comp6442.retrogame2018s1.activity;

/*
 * Copyright (C) 2018,
 *
 * Haotian Shi <u6158063@anu.edu.au>
 * Yang Zheng <u6287751@anu.edu.au>
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import edu.anu.comp6442.retrogame2018s1.R;
import edu.anu.comp6442.retrogame2018s1.service.MusicService;

public class WelcomeActivity extends AppCompatActivity {

    private Button play, ranking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        play = id(R.id.play);
        ranking = id(R.id.ranking);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        ranking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), RankActivity.class);
                startActivity(intent);
            }
        });

        Intent intent = new Intent(WelcomeActivity.this, MusicService.class);
        startService(intent);
    }

    /**
     * Stop the music service when the user exits.
     */
    @Override
    protected void onDestroy() {
        Intent intent = new Intent(WelcomeActivity.this, MusicService.class);
        stopService(intent);
        super.onDestroy();
    }

    @SuppressWarnings("unchecked")
    private <T extends View> T id(int id) {
        return (T) super.findViewById(id);
    }
}
