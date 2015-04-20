package ru.ifmo.ctddev.sholokhov.webcrawler;

/**
 * AtomicInteger-like counter
 * @see java.util.concurrent.atomic.AtomicInteger
 */
public class FinishTrigger {
    private int count;

    public FinishTrigger(int i) {
        count = i;
    }

    public void set(int i) {
        synchronized (this) {
            count = i;
        }
    }
}