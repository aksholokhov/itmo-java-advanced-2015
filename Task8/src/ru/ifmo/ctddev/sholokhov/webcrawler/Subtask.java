package ru.ifmo.ctddev.sholokhov.webcrawler;


import java.util.concurrent.atomic.AtomicInteger;

/**
 * Processor wrapper that increments counter after the wrapped job's done.
 */
abstract class Subtask implements Runnable {
    private AtomicInteger v;

    public Subtask(AtomicInteger v) {
        this.v = v;
    }
}