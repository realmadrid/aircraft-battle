package edu.anu.comp6442.retrogame2018s1;

/*
 * Copyright (C) 2018,
 *
 * Jiewei Qian <u7472740@anu.edu.au>
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import edu.anu.comp6442.retrogame2018s1.model.Bonus;
import edu.anu.comp6442.retrogame2018s1.model.BossEnemyPlane;
import edu.anu.comp6442.retrogame2018s1.model.BulletBonus;
import edu.anu.comp6442.retrogame2018s1.model.EnemyPlane;
import edu.anu.comp6442.retrogame2018s1.model.Explosion;
import edu.anu.comp6442.retrogame2018s1.model.HpBonus;
import edu.anu.comp6442.retrogame2018s1.model.MediumEnemyPlane;
import edu.anu.comp6442.retrogame2018s1.model.PlayerBullet;
import edu.anu.comp6442.retrogame2018s1.view.GameView;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class GameViewInstrumentedTest {

    private static float EPSILON = 0.001f;
    private GameView gv;
    private Canvas cv;

    private float getPlayerCenterX() { return gv.getPlayer().getCenterX(); }
    private float getPlayerCenterY() { return gv.getPlayer().getCenterY(); }

    public void initGv() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        gv = new GameView(appContext);
        cv = new Canvas(Bitmap.createBitmap(480, 720, Bitmap.Config.ARGB_8888));
        gv.draw(cv);
        gv.initializeGameStates(cv.getWidth(), cv.getHeight());
    }

    private Bitmap getBitmap(int id) {
        return BitmapFactory.decodeResource(gv.getContext().getResources(), id);
    }

    @Test
    public void initialize() throws Exception {
        initGv();

        // player start at center bottom of screen
        assertEquals(getPlayerCenterX(), 480 / 2, EPSILON);
        assertTrue(getPlayerCenterY() > 720 * 0.75);

        float pX = getPlayerCenterX();
        float pY = getPlayerCenterY();

        gv.updateGameStates();

        // not touching, so player should not move
        assertEquals(getPlayerCenterX(), pX, EPSILON);
        assertEquals(getPlayerCenterY(), pY, EPSILON);
    }

    @Test
    public void touchMoving() throws Exception {
        initGv();
        
        float pX = getPlayerCenterX();
        float pY = getPlayerCenterY();

        gv.setTouchPosition(100, 360);
        gv.setTouching(true);
        gv.updateGameStates();

        // touched, next tick should move towards target{X, Y}
        assertTrue(getPlayerCenterX() < pX);
        assertTrue(getPlayerCenterY() < pY);

        pX = getPlayerCenterX();
        pY = getPlayerCenterY();

        gv.setTouching(false);
        gv.updateGameStates();
        // plane stops moving as soon as touch ends
        assertEquals(getPlayerCenterX(), pX, EPSILON);
        assertEquals(getPlayerCenterY(), pY, EPSILON);

        gv.setTouching(true);
        // give enough ticks so plane can reach target
        for (int i=0; i!=1000; ++i)
            gv.updateGameStates();

        // should be at target
        assertEquals(getPlayerCenterX(), 100, EPSILON);
        assertEquals(getPlayerCenterY(), 360, gv.getPlayer().getHeight() * 2);    // allow fingertip offset
    }

    @Test
    public void playFiresBullets() throws Exception {
        initGv();

        for (int i = 0; i != 10; ++i)
            gv.updateGameStates();

        assertTrue(gv.getPlayerBullets().size() > 0);
    }

    @Test
    public void scoreIncreaseAsTimeFlies() throws Exception {
        initGv();

        for (int i=0; i != 100; ++i)
            gv.updateGameStates();

        assertTrue(gv.getScore() > 0);
    }

    @Test
    public void destroyEnemyAddScores() throws Exception {
        initGv();

        EnemyPlane en = new EnemyPlane(getBitmap(R.drawable.enemy1));
        en.setHp(0);
        en.centerTo(240, 10);
        GameView.addSprite(en);

        for (int i=0; i != 10; ++i) {
            gv.updateGameStates();
            gv.draw(cv);
        }

        assertEquals(true, en.isDestroyed());
        assertEquals(1 + en.getScore(), gv.getScore());   // first tick score + enemy score
    }

    @Test
    public void gameStopsWhenPlayerDies() throws Exception {
        initGv();

        for (int i = 0; i != 5; ++i) {
            gv.updateGameStates();
            gv.draw(cv);
        }

        gv.getPlayer().setHp(0);

        for (int i = 0; i != 100; ++i) {
            gv.updateGameStates();
            gv.draw(cv);
        }

        assertEquals(GameView.GAME_OVER, GameView.gameStatus);
    }

    @Test
    public void bulletHitAndDestroyedSpritesAreRemoved() throws Exception {
        initGv();

        MediumEnemyPlane en = new MediumEnemyPlane(getBitmap(R.drawable.enemy2));
        PlayerBullet b = new PlayerBullet(getBitmap(R.drawable.bullet));
        en.setHp(1);
        b.setDamage(1);

        en.centerTo(30,10);
        b.centerTo(30, cv.getHeight() - b.getHeight());

        GameView.addSprite(en);
        GameView.addSprite(b);

        int initialHp = en.getHp();

        for (int i = 0; i != 100; ++i) {
            gv.draw(cv);
        }

        /* effects of bullet hit:
         *   bullet itself is destroyed
         *   enemy hp is reduced by bullet damage (currently 1)
         *   enemy is destroyed (because its hp is reduced to 0)
         *   enemy is removed from armed enemy plane list
         */
        assertEquals(true, b.isDestroyed());
        assertEquals(initialHp - 1, en.getHp());
        assertEquals(true, en.isDestroyed());
        assertEquals(0, gv.getArmedEnemyPlanes().size());
    }

    @Test
    public void bossFliesIntoPosition() throws Exception {
        initGv();

        BossEnemyPlane boss = new BossEnemyPlane(getBitmap(R.drawable.enemy4));

        // boss first moves downwards
        boss.centerTo(540, 1);
        boss.draw(cv, null);
        assertEquals(540, boss.getCenterX(), EPSILON);
        assertTrue(boss.getCenterY() > 1);

        // allow enough ticks to for boss to get into position
        for (int i = 0; i != 200; ++i)
            boss.draw(cv, null);

        float oldY = boss.getCenterY();
        float oldX = boss.getCenterX();

        // boss then removed horizontally
        boss.draw(cv, null);
        assertEquals(oldY, boss.getCenterY(), EPSILON);
        assertNotEquals(oldX, boss.getCenterY(), EPSILON);
    }

    @Test
    public void bossSpawnsAndShoots() throws Exception {
        initGv();

        // a rather high level test
        gv.getPlayer().setHp(1);    // if boss works properly, after enough ticks, player will be destroyed
        gv.setScore(999999);        // should be high enough to trigger boss generation

        for (int i = 0; i != 900; ++i) {    // simulate 30 second
            gv.updateGameStates();
            gv.draw(cv);
        }

        assertEquals(true, gv.getPlayer().isDestroyed());
    }

    @Test
    public void explosionRelated() throws Exception {
        initGv();

        gv.getPlayer().setHp(1);
        gv.getPlayer().centerTo(240, 360);

        EnemyPlane en = new EnemyPlane(getBitmap(R.drawable.enemy1));
        Explosion ex = new Explosion(en);

        ex.centerTo(240, 360);
        GameView.addSprite(ex);

        for (int i = 0; i != 2; ++i) {
            gv.updateGameStates();
            gv.draw(cv);
        }

        // explosion will not collide with anything
        // therefore: player is unharmed, explosion is still on screen
        assertEquals(1, gv.getPlayer().getHp());
        assertEquals(false, ex.isDestroyed());

        for (int i = 0; i != 100; ++i) {
            gv.updateGameStates();
            gv.draw(cv);
        }

        // destroys eventually and destroys
        assertEquals(true, ex.isDestroyed());
    }

    @Test
    public void playerHitByPlane() throws Exception {
        initGv();

        gv.getPlayer().moveTo(240, 360);

        MediumEnemyPlane en = new MediumEnemyPlane(getBitmap(R.drawable.enemy2));
        en.setHp(gv.getPlayer().getHp());    // ensures mutual destruction
        en.centerTo(230, 340);

        GameView.addSprite(en);

        for (int i = 0; i != 10; ++i) {
            gv.updateGameStates();
            gv.draw(cv);
        }

        /*
         * effect of player hit enemy with same hp
         *   player and enemy plane are destroyed (hp reduced to 0)
         */
        assertEquals(true, gv.getPlayer().isDestroyed());
        assertEquals(0, gv.getPlayer().getHp());
        assertEquals(true, en.isDestroyed());
        assertEquals(0, en.getHp());
    }

    @Test
    public void enemyPlane() throws Exception {
        initGv();

        gv.getPlayer().centerTo(240, 500);

        EnemyPlane en = new EnemyPlane(getBitmap(R.drawable.enemy1));
        en.setHp(1);    // 1 hit guarantee destruction
        en.centerTo(240, 10);

        GameView.addSprite(en);

        for (int i = 0; i != 100; ++i) {
            gv.updateGameStates();
            gv.draw(cv);
        }

        assertEquals(0, en.getHp());
        assertEquals(true, en.isDestroyed());
    }

    @Test
    public void hpBonus() throws Exception {
        initGv();

        gv.getPlayer().centerTo(240, 500);
        gv.getPlayer().setHp(10);

        Bonus bn = new HpBonus(getBitmap(R.drawable.bonus1));
        bn.centerTo(240, 500);
        gv.bonus = bn;

        // bonus consumption tick
        gv.updateGameStates();
        gv.draw(cv);

        // bonus destroy tick
        gv.updateGameStates();
        gv.draw(cv);

        assertTrue(gv.getPlayer().getHp() > 10);
        assertEquals(true, bn.isDestroyed());
    }

    @Test
    public void bulletBonus() throws Exception {
        initGv();

        gv.getPlayer().centerTo(240, 500);

        Bonus bn = new BulletBonus(getBitmap(R.drawable.bonus2));
        bn.centerTo(240, 500);
        gv.bonus = bn;

        // bonus consumption tick
        gv.updateGameStates();
        gv.draw(cv);

        // bonus destroy tick
        gv.updateGameStates();
        gv.draw(cv);

        assertEquals(true, gv.getPlayer().isDoubleBullets());
    }

}
