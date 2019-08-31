package edu.anu.comp6442.retrogame2018s1.model;

/*
 * Copyright (C) 2018,
 *
 * Haotian Shi <u6158063@anu.edu.au>
 */


import android.graphics.Bitmap;

public class PlayerBullet extends Bullet {

    public PlayerBullet(Bitmap bitmap) {
        super(bitmap);
        // negative number means the bullet is going towards the top of the screen
        setSpeed(-25);
    }
}
