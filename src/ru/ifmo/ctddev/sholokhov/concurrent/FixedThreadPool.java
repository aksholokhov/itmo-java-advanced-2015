package ru.ifmo.ctddev.sholokhov.concurrent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Thread pool with fixed amount of workers.
 */
public class FixedThreadPool {
    private final Queue<Runnable> pool;
    private final List<Thread> processorThreads;

    /**
     * Creates fixed thread pool
     * @param threads number of worker threads.
     */
    public FixedThreadPool(int threads) {
        pool = new LinkedList<>();
        processorThreads = new ArrayList<>();

        for (int i = 0; i < threads; i++) {
            Thread thread = new ProcessorThread(pool);
            processorThreads.add(thread);
            thread.start();
        }
    }

    /**
     * Add new task to thread pool.
     * @param task task to be added.
     */
    public void execute(Runnable task) {
        synchronized (pool) {
            pool.add(task);
        }
    }

    /**
     * Interrupts all worker threads and clears task queue.
     * @throws InterruptedException is thrown from Thread.interrupt() method
     */
    public void shutdown() throws InterruptedException {
        synchronized (pool) {
            pool.clear();
        }

        processorThreads.forEach(java.lang.Thread::interrupt);
    }
}