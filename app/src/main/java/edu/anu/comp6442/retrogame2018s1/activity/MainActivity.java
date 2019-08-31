package edu.anu.comp6442.retrogame2018s1.activity;

/*
 * Copyright (C) 2018,
 *
 * Haotian Shi <u6158063@anu.edu.au>
 */

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import edu.anu.comp6442.retrogame2018s1.R;
import edu.anu.comp6442.retrogame2018s1.view.GameView;

public class MainActivity extends AppCompatActivity {

    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gameView = findViewById(R.id.gameView);
    }

    public void onStop(){
        super.onStop();
        gameView.stopTimer();
    }

    public void onDestroy(){
        // before destroy, writing scores to files
        gameView.writeScoresToFile();
        super.onDestroy();
    }
}
