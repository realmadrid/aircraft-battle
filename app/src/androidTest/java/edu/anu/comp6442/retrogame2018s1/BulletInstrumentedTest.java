package edu.anu.comp6442.retrogame2018s1;

/*
 * Copyright (C) 2018,
 *
 * Jiewei Qian <u7472740@anu.edu.au>
 */

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import edu.anu.comp6442.retrogame2018s1.model.Bullet;

import static org.junit.Assert.*;
import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class BulletInstrumentedTest {

    private static float EPSILON = 0.001f;

    @Test
    public void bulletFlies() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        Bitmap bmp = BitmapFactory.decodeResource(appContext.getResources(), R.drawable.bullet);

        Canvas cv = new Canvas() {
            public int getWidth() { return 100; }
            public int getHeight() { return 300; }
        };

        Bullet a = new Bullet(bmp);
        a.centerTo(50, 200);

        a.draw(cv, null);
        assertEquals(50, a.getCenterX(), EPSILON);
        assertEquals(200.0f + a.getSpeed(), a.getCenterY() , EPSILON);

        // allow enough ticks to bullet are out of painting area
        for (int i = 0; i != 200; ++i)
            a.draw(cv, null);

        assertTrue(a.isDestroyed());
    }
}
