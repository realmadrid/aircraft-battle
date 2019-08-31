package edu.anu.comp6442.retrogame2018s1.model;

/*
 * Copyright (C) 2018,
 *
 * Haotian Shi <u6158063@anu.edu.au>
 */


import android.graphics.Bitmap;

public class MediumEnemyPlane extends EnemyPlane {

    public MediumEnemyPlane(Bitmap bitmap) {
        super(bitmap);
        setHp(4);
        setSpeed(6);
        setScore(30);
    }
}
