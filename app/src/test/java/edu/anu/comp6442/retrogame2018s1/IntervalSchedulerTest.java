package edu.anu.comp6442.retrogame2018s1;

/*
 * Copyright (C) 2018,
 *
 * Jiewei Qian <u7472740@anu.edu.au>
 */

import org.junit.Test;

import java.util.Timer;
import java.util.TimerTask;

import edu.anu.comp6442.retrogame2018s1.model.Sprite;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class IntervalSchedulerTest {

    private int exec = 0;
    private IntervalScheduler s;

    @Test
    public void schedules() throws Exception {
        exec = 0;
        s = new IntervalScheduler(100, new Runnable(){
            public void run() {
                ++exec;
                if (exec == 3) s.stop();
                System.err.println("Schedule normal" + String.valueOf(exec));
            }
        });

        try { Thread.sleep(500); } catch(Exception e) {}
        assertEquals(0, exec);

        s.start();
        try { Thread.sleep(500); } catch(Exception e) {}
        assertEquals(3, exec);

        // multiple start won't crash
        exec = 0;
        s.start();
        s.start();
        s.start();
        try { Thread.sleep(500); } catch(Exception e) {}
        assertEquals(3, exec);

        // multiple stop won't crash
        exec = 0;
        s.start();
        try { Thread.sleep(50); } catch(Exception e) {}
        assertEquals(1, exec);
        s.stop();
        s.stop();

        try { Thread.sleep(500); } catch(Exception e) {}
        assertEquals(1, exec);
    }

}