package edu.anu.comp6442.retrogame2018s1.model;

/*
 * Copyright (C) 2018,
 *
 * Haotian Shi <u6158063@anu.edu.au>
 * Jiewei Qian <u7472740@anu.edu.au>
 */

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import edu.anu.comp6442.retrogame2018s1.view.GameView;

public class EnemyPlane extends Sprite {

    private int hp;
    private int score;

    public EnemyPlane(Bitmap bitmap) {
        super(bitmap);
    }

    @Override
    protected void beforeDraw(Canvas canvas, Paint paint) {
        // move down the enemy plane before each drawing
        move(0, getSpeed() * GameView.getDensity());
    }

    @Override
    protected void afterDraw(Canvas canvas, Paint paint) {
        super.afterDraw(canvas, paint);
        if (!isDestroyed()) {
            // detect whether this enemy plane is hit by the player bullets
            for (Bullet b : GameView.getActivePlayerBullets()) {
                if (collideWith(b)) {
                    b.destroy();
                    removeHp(b.getDamage());
                }
            }

            if (hp <= 0)
                explode();
        }
    }

    /**
     * When the hp of an enemy plane is equals to or less than zero,
     * add its score to the total score and start an explosion.
     */
    protected void explode() {
        Explosion explosion = new Explosion(this);
        explosion.centerTo(getCenterX(), getCenterY());
        GameView.addSprite(explosion);
        GameView.addScore(score);
        destroy();
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void addHp(int delta) {
        hp += delta;
    }

    public void removeHp(int delta) {
        hp = Math.max(0, hp - delta);
    }
}
