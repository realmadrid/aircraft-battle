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

import edu.anu.comp6442.retrogame2018s1.model.Sprite;

import static org.junit.Assert.*;
import android.graphics.Bitmap;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class SpriteInstrumentedTest {

    private static float EPSILON = 0.001f;

    @Test
    public void bitmapRelatedGetter() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        Bitmap bmp = BitmapFactory.decodeResource(appContext.getResources(), R.drawable.player);

        Sprite s = new Sprite(bmp);

        assertEquals(bmp.getWidth(), s.getWidth(), EPSILON);
        assertEquals(bmp.getHeight(), s.getHeight(), EPSILON);


        s.moveTo(10,20);
        // checks centerX, centerY are computed correctly
        assertEquals(10.0f + bmp.getWidth() / 2.0, s.getCenterX(), EPSILON);
        assertEquals(20.0f + bmp.getHeight() / 2.0, s.getCenterY(), EPSILON);
    }

    @Test
    public void collision() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        Bitmap bmp = BitmapFactory.decodeResource(appContext.getResources(), R.drawable.player);

        Sprite a = new Sprite(bmp);
        Sprite b = new Sprite(bmp);

        assertEquals(true, a.collideWith(b));

        a.moveTo(2 * bmp.getWidth(), 2 * bmp.getHeight());
        assertEquals(false, a.collideWith(b));
    }
}
