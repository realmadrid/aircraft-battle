package edu.anu.comp6442.retrogame2018s1;

/*
 * Copyright (C) 2018,
 *
 * Jiewei Qian <u7472740@anu.edu.au>
 */


/**
 * Basic FPS counter
 *
 * Computes FPS from last frame's performance
 */
public class FpsCounter {
    private long lastFrame = System.currentTimeMillis();
    private long result = 0;

    /**
     * registers a frame
     */
    public void count() {
        long current = System.currentTimeMillis();
        result = 1000 / Math.max(1, (current - lastFrame));
        lastFrame = current;
    }

    /**
     * returns fps number
     */
    public long get() {
        return result;
    }
}
