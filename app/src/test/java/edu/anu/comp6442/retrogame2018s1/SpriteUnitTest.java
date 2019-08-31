package edu.anu.comp6442.retrogame2018s1;

/*
 * Copyright (C) 2018,
 *
 * Jiewei Qian <u7472740@anu.edu.au>
 */

import org.junit.Test;

import static org.junit.Assert.*;
import edu.anu.comp6442.retrogame2018s1.model.Sprite;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class SpriteUnitTest {
    private static float EPSILON = 0.001f;

    @Test
    public void getterSetter() throws Exception {
        Sprite s = new Sprite(null);

        assertEquals(0, s.getX(), EPSILON);
        assertEquals(0, s.getY(), EPSILON);

        s.moveTo(1,2);
        assertEquals(1, s.getX(), EPSILON);
        assertEquals(2, s.getY(), EPSILON);

        s.setX(3);
        assertEquals(3, s.getX(), EPSILON);

        s.setY(4);
        assertEquals(4, s.getY(), EPSILON);
    }

    @Test
    public void move() throws Exception {
        Sprite s = new Sprite(null);
        s.moveTo(0,0);
        s.move(1,2);
        assertEquals(1, s.getX(), EPSILON);
        assertEquals(2, s.getY(), EPSILON);
    }
}