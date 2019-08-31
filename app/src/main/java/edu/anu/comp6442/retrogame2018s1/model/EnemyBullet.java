package edu.anu.comp6442.retrogame2018s1.model;

/*
 * Copyright (C) 2018,
 *
 * Haotian Shi <u6158063@anu.edu.au>
 */

import android.graphics.Bitmap;

public class EnemyBullet extends Bullet {

    public EnemyBullet(Bitmap bitmap) {
        super(bitmap);
        setSpeed(6);
    }
}
