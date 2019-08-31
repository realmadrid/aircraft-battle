package edu.anu.comp6442.retrogame2018s1.model;

/*
 * Copyright (C) 2018,
 *
 * Haotian Shi <u6158063@anu.edu.au>
 */

import android.graphics.Bitmap;

public class AdvancedEnemyPlane extends EnemyPlane {

    public AdvancedEnemyPlane(Bitmap bitmap) {
        super(bitmap);
        setHp(10);
        setSpeed(4);
        setScore(120);
    }
}
