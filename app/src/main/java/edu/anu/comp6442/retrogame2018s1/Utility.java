package edu.anu.comp6442.retrogame2018s1;

/*
 * Copyright (C) 2018,
 *
 * Jiewei Qian <u7472740@anu.edu.au>
 */

public class Utility {
    /**
     * clamps val to be within [lwr, upr] range
     */
    public static float clampRange(float val, float lwr, float upr) {
        if (val < lwr) return lwr;
        if (val > upr) return upr;
        return val;
    }
}
