package ru.ifmo.ctddev.sholokhov.webcrawler;

/**
 * Created by Шолохов on 01.04.2015.
 */
import java.util.Queue;

/**
 * Worker thread for FixedThreadPool.
 * Polls jobs from task queue and executes one if present.
 */
public class MyThread extends Thread {
    private final Queue<Runnable> pool;

    public MyThread(Queue<Runnable> pool) {
        this.pool = pool;
    }

    @Override
    public void run() {
        Runnable task;

        while (!Thread.interrupted()) {
            task = pool.poll();

            if (task != null) {
                task.run();
            }
        }
    }
}