package ru.ifmo.ctddev.sholokhov.iterativeparallelism;

/**
 * Created by Шолохов on 01.04.2015.
 */
/**
 * AtomicInteger-like counter
 * @see java.util.concurrent.atomic.AtomicInteger
 */
public class Counter {
    private int count;

    public Counter() {
        count = 0;
    }

    public void increment() {
        synchronized (this) {
            count++;
        }
    }

    public void waitFor(int other) {
        while (true) {
            synchronized (this) {
                if (count == other) {
                    break;
                }
            }
        }
    }
}