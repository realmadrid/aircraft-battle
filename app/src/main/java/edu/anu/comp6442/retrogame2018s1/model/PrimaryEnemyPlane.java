package edu.anu.comp6442.retrogame2018s1.model;

/*
 * Copyright (C) 2018,
 *
 * Haotian Shi <u6158063@anu.edu.au>
 */


import android.graphics.Bitmap;

public class PrimaryEnemyPlane extends EnemyPlane {

    public PrimaryEnemyPlane(Bitmap bitmap) {
        super(bitmap);
        setHp(2);
        setSpeed(8);
        setScore(10);
    }
}
