package edu.anu.comp6442.retrogame2018s1.service;

/*
 * Copyright (C) 2018,
 *
 * Haotian Shi <u6158063@anu.edu.au>
 */

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import java.io.IOException;

import edu.anu.comp6442.retrogame2018s1.R;

/**
 * A background music service for the game.
 */
public class MusicService extends Service {

    private MediaPlayer player;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        player.start();

        // playing in a continuous loop
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                try {
                    mp.start();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        });

        // error handler
        player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            public boolean onError(MediaPlayer mp, int what, int extra) {
                try {
                    mp.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
        super.onStart(intent, startId);
    }

    /**
     * Initialize the music resource.
     */
    @Override
    public void onCreate() {
        try {
            player = new MediaPlayer();
            player = MediaPlayer.create(MusicService.this, R.raw.starwar);
            player.prepare();
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
        }
        super.onCreate();
    }

    /**
     * Stop the music and release resource when the service is destroyed.
     */
    @Override
    public void onDestroy() {
        player.stop();
        player.release();
        super.onDestroy();
    }
}
