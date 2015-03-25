package ru.ifmo.ctddev.sholokhov.iterativeparallelism;

/**
 * Created by Шолохов on 24.03.2015.
 */
import java.util.Queue;

public class ProcessorThread extends Thread {
    private final Queue<Runnable> pool;

    public ProcessorThread(Queue<Runnable> pool) {
        this.pool = pool;
    }

    @Override
    public void run() {
        Runnable task;

        synchronized (pool) {
            task = pool.poll();
        }

        if (task != null) {
            task.run();
        }
    }
}