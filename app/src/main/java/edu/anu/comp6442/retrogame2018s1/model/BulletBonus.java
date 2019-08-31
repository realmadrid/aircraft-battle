package edu.anu.comp6442.retrogame2018s1.model;

/*
 * Copyright (C) 2018,
 *
 * Haotian Shi <u6158063@anu.edu.au>
 */

import android.graphics.Bitmap;

import edu.anu.comp6442.retrogame2018s1.view.GameView;

/**
 * This bonus could turn the player plane to double bullets mode.
 */
public class BulletBonus extends Bonus {

    public BulletBonus(Bitmap bitmap) {
        super(bitmap);
    }

    @Override
    protected void getBonusEffect(PlayerPlane player) {
        player.setBitmap(GameView.images.get(1));
        player.setBulletNum(PlayerPlane.DOUBLE_BULLET);
        player.resetDoubleBulletTime();
    }
}
