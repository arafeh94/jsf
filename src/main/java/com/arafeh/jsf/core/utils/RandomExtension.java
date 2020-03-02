package com.arafeh.jsf.core.utils;

import java.util.Random;

public class RandomExtension extends Random {
    public RandomExtension() {
        super();
    }

    public RandomExtension(int seed) {
        super(seed);
    }

    /**
     * return an int number between the provided range
     * @param min the minimum number inclusive
     * @param max the maximum number exclusive
     * @return
     */
    public int nextInt(int min, int max) {
        return this.nextInt((max - min)) + min;
    }

    public long nextLong(long min, long max) {
        return min + ((long) (this.nextDouble() * (max - min)));
    }

    public long nextLong(long min) {
        return nextLong(min, Long.MAX_VALUE);
    }

    public long UUIDLong() {
        return (System.currentTimeMillis() << 20) | (System.nanoTime() & ~9223372036854251520L);
    }

    public boolean nextBool(float trueProbability) {
        return this.nextFloat() < trueProbability;
    }
}
