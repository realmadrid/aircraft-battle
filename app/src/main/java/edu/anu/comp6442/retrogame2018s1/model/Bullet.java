package edu.anu.comp6442.retrogame2018s1.model;

/*
 * Copyright (C) 2018,
 *
 * Haotian Shi <u6158063@anu.edu.au>
 */

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import edu.anu.comp6442.retrogame2018s1.view.GameView;

/**
 * Basic bullet class, the basic damage is 1 hp.
 */
public class Bullet extends Sprite {

    private int damage;

    public Bullet(Bitmap bitmap) {
        super(bitmap);
        setDamage(1);
    }

    @Override
    protected void beforeDraw(Canvas canvas, Paint paint) {
        move(0, getSpeed() * GameView.getDensity());
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }
}
