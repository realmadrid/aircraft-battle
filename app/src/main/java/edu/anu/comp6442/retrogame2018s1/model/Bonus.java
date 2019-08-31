package edu.anu.comp6442.retrogame2018s1.model;

/*
 * Copyright (C) 2018,
 *
 * Haotian Shi <u6158063@anu.edu.au>
 */

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.Random;

/**
 * Basic bonus class, the random movements would have be computed before drawing.
 * The subclasses must implement the method getBonusEffect() for specific purpose.
 */
public abstract class Bonus extends Sprite {

    private int stage = 1;
    private boolean hasInitialized = false;
    private Movement m1, m2, m3;  // three stages when the bonus move in the screen

    public Bonus(Bitmap bitmap) {
        super(bitmap);
        setSpeed(2);
    }

    /**
     * The parameters should be computed before first drawing.
     */
    private void initParameters(Canvas canvas) {
        hasInitialized = true;
        Random random = new Random();
        int unitY = canvas.getHeight() / 4;
        int x1 = 0;
        if (random.nextBoolean()) {
            x1 = canvas.getWidth();
            this.moveTo(random.nextInt(canvas.getWidth() / 4) + canvas.getWidth() / 4, -getHeight());
        } else {
            this.moveTo(random.nextInt(canvas.getWidth() / 4) + canvas.getWidth() / 2, -getHeight());
        }
        int x2 = canvas.getWidth() - x1;
        int x3 = x1;
        int y1 = random.nextInt(unitY) + unitY;
        int y2 = y1 + unitY;
        int y3 = y1 + 3 * unitY;

        m1 = computeMovement((int) getX(), (int) getY(), x1, y1);
        m2 = computeMovement(x1, y1, x2, y2);
        m3 = computeMovement(x2, y2, x3, y3);
    }

    /**
     * Compute the movement for ticks in each stage.
     */
    private Movement computeMovement(int startX, int startY, int targetX, int targetY) {
        float deltaX = targetX - startX;
        float deltaY = targetY - startY;
        float distance = (float) Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
        float unitDis = 3;
        float moveX = getSpeed() * unitDis * deltaX / distance;
        float moveY = getSpeed() * unitDis * deltaY / distance;
        return new Movement(moveX, moveY);
    }

    private class Movement {
        float x, y;

        Movement(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    @Override
    protected void beforeDraw(Canvas canvas, Paint paint) {
        super.beforeDraw(canvas, paint);
        if (!hasInitialized)
            initParameters(canvas);
        if (stage == 1) {
            move(m1.x, m1.y);
        } else if (stage == 2) {
            move(m2.x, m2.y);
        } else if (stage == 3) {
            move(m3.x, m3.y);
        } else {
            destroy();
        }
    }

    @Override
    protected void afterDraw(Canvas canvas, Paint paint) {
        super.afterDraw(canvas, paint);
        if (getX() + getWidth() >= canvas.getWidth() || getX() <= 0) {
            stage++;
        }
    }

    protected abstract void getBonusEffect(PlayerPlane player);
}
