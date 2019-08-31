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
import android.graphics.Rect;

import java.util.List;

import edu.anu.comp6442.retrogame2018s1.view.GameView;

/**
 * This class implements an explosion effect for a destroyed plane.
 */
public class Explosion extends Sprite {

    // the current segment
    private int currentFrame = 0;
    // the times of redrawing
    private int frame = 0;

    private Sprite sprite;
    List<Bitmap> frames;

    public Explosion(Sprite sprite) {
        super(null);
        this.sprite = sprite;
        frames = GameView.getExplosionBitmaps();
        setBitmap(frames.get(0));
    }

    @Override
    protected void beforeDraw(Canvas canvas, Paint paint) {
        if (sprite instanceof BossEnemyPlane) {
            move(((BossEnemyPlane) sprite).getDirection() * sprite.getSpeed() * GameView.getDensity(), 0);
        } else if (sprite instanceof EnemyPlane) {
            move(0, sprite.getSpeed() * GameView.getDensity());
        }
    }

    @Override
    protected void afterDraw(Canvas canvas, Paint paint) {
        frame++;
        if (!isDestroyed()) {
            // evey explosion segment lasts for 4 frames
            if (frame % 4 == 0) {
                ++currentFrame;
                if (currentFrame >= frames.size()) {    // explosion finishes
                    destroy();
                    onExplosionFinish();
                } else {
                    setBitmap(frames.get(currentFrame));    // set bitmap to next frame
                }
            }
        }
    }

    protected void onExplosionFinish() {}
}
