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
 * This class includes all the attributes of the player plane.
 */
public class PlayerPlane extends Sprite {

    public static int SINGLE_BULLET = 0;
    public static int DOUBLE_BULLET = 1;

    // the double bullets mode would be disabled after 400 ticks
    private static int MAX_DOUBLE_BULLET_TIME = 400;
    private int doubleBulletTime = 0;

    private int hp;
    private int bulletNum = SINGLE_BULLET;
    private boolean finishExplosion = false;

    public PlayerPlane(Bitmap bitmap) {
        super(bitmap);
        setSpeed(8);
        setHp(10);
    }

    @Override
    protected void afterDraw(Canvas canvas, Paint paint) {
        if (!isDestroyed()) {
            // detect whether the player is hit by any bullets of the enemies
            for (Bullet b : GameView.getActiveEnemyBullets()) {
                if (collideWith(b)) {
                    b.destroy();
                    removeHp(b.getDamage());
                }
            }

            // detect whether the player gets a bonus
            Bonus bonus = GameView.getBonus();
            if (bonus != null && !bonus.isDestroyed() && collideWith(bonus)) {
                if (bonus instanceof HpBonus) {
                    bonus.getBonusEffect(this);
                    bonus.destroy();
                } else if (bonus instanceof BulletBonus) {
                    bonus.getBonusEffect(this);
                    bonus.destroy();
                }
            }

            // reset the player plane to single bullet mode
            if (doubleBulletTime++ > MAX_DOUBLE_BULLET_TIME)
                toSingleBulletMode();

            if (hp <= 0)
                explode();
        }
    }

    // start playing exploding animation
    // then mark player as destroyed to trigger GAME_OVER status
    protected void explode() {
        Explosion explosion = new Explosion(this) {
            protected void onExplosionFinish() {
                finishExplosion = true;
            }
        };
        explosion.centerTo(getCenterX(), getCenterY());
        GameView.addSprite(explosion);
        destroy();
    }

    // return whether explision aniomation is finished
    public boolean finishExplosion() {
        return finishExplosion;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void addHp(int delta) {
        hp += delta;
    }

    public void removeHp(int delta) {
        hp = Math.max(0, hp - delta);
    }

    public boolean isSingleBullet() {
        return bulletNum == SINGLE_BULLET;
    }

    public boolean isDoubleBullets() {
        return bulletNum == DOUBLE_BULLET;
    }

    /**
     * Set the player plane to single bullet mode. The image should also be set to the basic one.
     */
    public void toSingleBulletMode() {
        setBitmap(GameView.images.get(0));
        setBulletNum(SINGLE_BULLET);
    }

    public void resetDoubleBulletTime() {
        doubleBulletTime = 0;
    }

    public int getBulletNum() {
        return bulletNum;
    }

    public void setBulletNum(int bulletNum) {
        this.bulletNum = bulletNum;
    }

}
