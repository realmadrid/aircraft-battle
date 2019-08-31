package edu.anu.comp6442.retrogame2018s1.model;

/*
 * Copyright (C) 2018,
 *
 * Haotian Shi <u6158063@anu.edu.au>
 * Jiewei Qian <u7472740@anu.edu.au>
 */

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Sprite is the base class for anything that will be painted on screen
 */
public class Sprite {

    private float x = 0;
    private float y = 0;
    private Bitmap bitmap = null;
    private int speed = 1;
    private boolean destroyed = false;

    public Sprite(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void draw(Canvas canvas, Paint paint) {
        beforeDraw(canvas, paint);
        onDraw(canvas, paint);
        afterDraw(canvas, paint);
    }

    protected void beforeDraw(Canvas canvas, Paint paint) {}

    protected void onDraw(Canvas canvas, Paint paint) {
        if (!destroyed && bitmap != null) {
            Rect srcRef = getBitmapRect();
            RectF destRecF = getRectF();
            canvas.drawBitmap(bitmap, srcRef, destRecF, paint);
        }
    }

    /**
     * afterDraw checks if sprite is out of painting area
     * if yes flag sprite as destroyed (so GameView can recycle it)
     */
    protected void afterDraw(Canvas canvas, Paint paint) {
        if (!isDestroyed()) {
            RectF canvasRecF = new RectF(0, 0, canvas.getWidth(), canvas.getHeight());
            RectF spriteRecF = getRectF();
            if (!RectF.intersects(canvasRecF, spriteRecF))
                destroy();
        }
    }

    public Rect getBitmapRect() {
        return new Rect(0, 0, (int) getWidth(), (int) getHeight());
    }

    public RectF getRectF() {
        return new RectF(x, y, x + getWidth(), y + getHeight());
    }

    public float getWidth() {
        if (bitmap != null)
            return bitmap.getWidth();
        return 0;
    }

    public float getHeight() {
        if (bitmap != null)
            return bitmap.getHeight();
        return 0;
    }

    /**
     * move sprite's top-left corner to (x, y)
     */
    public void moveTo(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * move sprite by (offsetX, offsetY)
     */
    public void move(float offsetX, float offsetY){
        x += offsetX;
        y += offsetY;
    }

    /**
     * move sprite's center to (x, y)
     */
    public void centerTo(float x, float y) {
        this.x = x - getWidth() / 2;
        this.y = y - getHeight() / 2;
    }

    public void destroy(){
        bitmap = null;
        destroyed = true;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getCenterX() { return getX() + getWidth() / 2; }

    public float getCenterY() { return getY() + getHeight() / 2; }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }

    /**
     * return overlapping rectangle area
     * if no overlap, result rect's area is 0
     */
    private RectF getOverlapRectF(RectF a, RectF b) {
        return new RectF(
                Math.max(a.left, b.left),
                Math.max(a.top, b.top),
                Math.min(a.right, b.right),
                Math.max(a.bottom, b.bottom)
        );
    }

    /**
     * return alpha value, 0 = transparent, 255 = not transparent
     */
    private int getPixelAlphaAtLocation(int sX, int sY) {
        if (bitmap != null) {
            int aX = sX - (int) x;
            int aY = sY - (int) y;
            if (aX < 0 || aX >= bitmap.getWidth() || aY < 0 || aY >= bitmap.getHeight()) return Color.TRANSPARENT;
            else return (bitmap.getPixel(sX - (int)x, sY - (int)y) & 0xFF000000) >>> 24;
        } else {
            return 0;
        }
    }

    /**
     * bitmap alpha channel collision detection
     * pixel must be non transparent enough to count as collision (currently, alpha > 0xB0).
     * check using 2px scan strip to improve performance
     */
    public boolean collideWith(Sprite other) {
        RectF a = getRectF();
        RectF b = other.getRectF();
        if (a.intersect(b)) {
            RectF collisionArea = getOverlapRectF(a, b);
            // iterates over overlapping region, check if any pixel in the region is not transparent
            for (int i = (int)collisionArea.left; i < (int)collisionArea.right; i += 2)
                for (int j = (int)collisionArea.top; j < (int)collisionArea.bottom; j += 2)
                    if (getPixelAlphaAtLocation(i, j) > 0xB0 && other.getPixelAlphaAtLocation(i, j) > 0xB0)
                        return true;
        }
        return false;
    }
}
