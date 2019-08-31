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

public class BossEnemyPlane extends EnemyPlane {

    private int direction = 1;
    private boolean readyToAttack = false;

    public BossEnemyPlane(Bitmap bitmap) {
        super(bitmap);
        setHp(100);
        setSpeed(2);
        setScore(2000);
    }

    @Override
    protected void beforeDraw(Canvas canvas, Paint paint) {
        if (getY() < getHeight() / 3) {
            // boss is moving into the screen
            super.beforeDraw(canvas, paint);
        } else {
            // the boss will not attack before it is ready
            if (!readyToAttack)
                readyToAttack = true;

            // the boss will move around in the top area of the screen
            if (getX() + getWidth() > canvas.getWidth() || getX() < 0)
                direction *= -1;
            move(direction * getSpeed() * GameView.getDensity(), 0);
        }
    }

    public int getDirection() {
        return direction;
    }

    public boolean isReadyToAttack() {
        return !isDestroyed() && getHp() > 0 && readyToAttack;
    }

}
