package ru.ifmo.ctddev.sholokhov.iterativeparallelism;

/**
 * Created by Шолохов on 24.03.2015.
 */
public class PublicCounter {
    private int count;

    public PublicCounter() {
        count = 0;
    }

    public synchronized void increment() {
        count++;
    }

    public void sleepUntil(int other) {
        while (true) {
            synchronized (this) {
                if (count == other) {
                    break;
                }
            }
        }
    }
}
