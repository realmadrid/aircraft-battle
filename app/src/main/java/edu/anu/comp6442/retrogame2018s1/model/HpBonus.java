package edu.anu.comp6442.retrogame2018s1.model;

/*
 * Copyright (C) 2018,
 *
 * Haotian Shi <u6158063@anu.edu.au>
 */


import android.graphics.Bitmap;

import java.util.Random;

/**
 * This bonus could add a random hp value to the player plane, at least 5.
 */
public class HpBonus extends Bonus {

    private int baseHpBonus = 5;

    public HpBonus(Bitmap bitmap) {
        super(bitmap);
    }

    @Override
    protected void getBonusEffect(PlayerPlane player) {
        int hpBonus = new Random().nextInt(15) + baseHpBonus;
        player.addHp(hpBonus);
    }

}
