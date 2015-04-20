package ru.ifmo.ctddev.sholokhov.webcrawler;

/**
 * Created by Шолохов on 01.04.2015.
 */
import java.util.Queue;

/**
 * Worker thread for FixedThreadPool.
 * Polls jobs from task queue and executes one if present.
 */
public class ProcessorThread extends Thread {
    private final Queue<ThrowedRunnable> pool;

    public ProcessorThread(Queue<ThrowedRunnable> pool) {
        this.pool = pool;
    }

    @Override
    public void run() {
        ThrowedRunnable task;

        while (!Thread.interrupted()) {
            synchronized (pool) {
                task = pool.poll();
            }

            if (task != null) {
                try {
                    task.run();
                }
                catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }
}