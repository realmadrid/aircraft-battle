package edu.anu.comp6442.retrogame2018s1;

/*
 * Copyright (C) 2018,
 *
 * Jiewei Qian <u7472740@anu.edu.au>
 */

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import edu.anu.comp6442.retrogame2018s1.model.Scoreboard;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ScoreboardInstrumentedTest {

    private Scoreboard sb;

    private void initSb() {
        initSb(true);
    }

    private void initSb(boolean cleanStart) {
        Context ctx = InstrumentationRegistry.getTargetContext();
        String filename = "test_sb";

        if (cleanStart) {
            // make sure test file does not exist
            (new File(ctx.getFilesDir(), filename)).delete();
        }

        sb = new Scoreboard(ctx, filename);
    }

    @Test
    public void initToEmptyList() throws Exception {
        initSb();
        assertEquals(sb.size(), 0);
    }

    @Test
    public void add() throws Exception {
        initSb();
        sb.add(10);
        assertEquals(sb.size(), 1);
        assertEquals(sb.get(0).score, 10);
    }

    @Test
    public void persistOnFile() throws Exception {
        initSb();
        sb.add(10);
        sb.writeToFile();

        initSb(false);
        assertEquals(sb.size(), 1);
        assertEquals(sb.get(0).score, 10);
    }

    @Test
    public void dropsLowScoreRecord() throws Exception {
        int numOfRecords = Scoreboard.numberOfRecords;
        initSb();
        for (int i=1; i<=numOfRecords+3; ++i)
            sb.add(i);

        assertEquals(numOfRecords, sb.size());
        assertEquals(numOfRecords+3, sb.get(0).score);
        for (int i=1; i!=numOfRecords; ++i)    // assert ordered correctly
            assertTrue(sb.get(i-1).score > sb.get(i).score);

        sb.writeToFile();

        // order persist across load
        initSb(false);
        assertEquals(numOfRecords, sb.size());
        assertEquals(numOfRecords+3, sb.get(0).score);
        for (int i=1; i!=numOfRecords; ++i)    // assert ordered correctly
            assertTrue(sb.get(i-1).score > sb.get(i).score);
    }

    @Test
    public void clears() throws Exception {
        initSb();
        sb.add(1);
        sb.clear();
        assertEquals(0, sb.size());

        initSb(false);
        assertEquals(0, sb.size());
    }

    @Test
    public void iterable() throws Exception {
        initSb();
        sb.add(1);
        sb.add(2);
        int count = 2;
        // also checks iterator ordering
        for (Scoreboard.Record r: sb)
            assertEquals(count--, r.score);
    }
}
