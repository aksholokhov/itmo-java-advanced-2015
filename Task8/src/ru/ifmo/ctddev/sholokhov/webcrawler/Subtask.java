package ru.ifmo.ctddev.sholokhov.webcrawler;


import java.util.concurrent.atomic.AtomicInteger;

abstract class Subtask implements Runnable {
    private AtomicInteger v;

    public Subtask(AtomicInteger v) {
        this.v = v;
    }
}