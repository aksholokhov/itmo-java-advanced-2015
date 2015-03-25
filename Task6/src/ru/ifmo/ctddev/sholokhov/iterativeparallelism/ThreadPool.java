package ru.ifmo.ctddev.sholokhov.iterativeparallelism;

/**
 * Created by Шолохов on 24.03.2015.
 */

import java.util.Queue;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ThreadPool {
    private final Queue<Runnable> pool;
    private final List<Thread> workerThreads;

    public ThreadPool(int threads) {
        pool = new LinkedList<>();
        workerThreads = new ArrayList<>();

        for (int i = 0; i < threads; i++) {
            Thread thread = new ProcessorThread(pool);
            workerThreads.add(thread);
            thread.start();
        }
    }

    public void execute(Runnable task) {
        synchronized (pool) {
            pool.add(task);
        }
    }

    public void shutdown() throws InterruptedException {
        for (Thread thread : workerThreads) {
            thread.join();
        }
    }
}