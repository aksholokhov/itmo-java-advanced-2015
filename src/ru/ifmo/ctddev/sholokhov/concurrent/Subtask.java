package ru.ifmo.ctddev.sholokhov.concurrent;


/**
 * Processor wrapper that increments counter after the wrapped job's done.
 */
public class Subtask implements Runnable {
    private Counter counter;
    private Runnable runnable;

    public Subtask(Counter counter, Runnable runnable) {
        this.counter = counter;
        this.runnable = runnable;
    }

    @Override
    public void run() {
        try {
            runnable.run();
        } finally {
            counter.increment();
        }
    }
}