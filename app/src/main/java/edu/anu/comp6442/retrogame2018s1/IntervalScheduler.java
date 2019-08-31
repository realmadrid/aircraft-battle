package edu.anu.comp6442.retrogame2018s1;

/*
 * Copyright (C) 2018,
 *
 * Jiewei Qian <u7472740@anu.edu.au>
 */

import java.util.Timer;
import java.util.TimerTask;

/**
 * Execute Runnable at specified interval (may be slower, if Runnable takes long time to run)
 */
public class IntervalScheduler {
    private int interval;
    private Runnable rn;
    private Timer tm;

    public IntervalScheduler(int interval, Runnable rn) {
        this.interval = interval;
        this.rn = rn;
    }

    /**
     * starts timer
     * calling multiple times has no side effect
     */
    public void start() {
        if (tm != null) return;
        tm = new Timer();
        tm.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                rn.run();
            }
        }, 0, interval);
    }

    /**
     * stops timer
     * calling multiple times has no side effect
     */
    public void stop() {
        if (tm != null) {
            tm.cancel();
            tm = null;
        }
    }
}
