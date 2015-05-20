package ru.ifmo.ctddev.sholokhov.webcrawler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Thread pool with fixed amount of workers.
 */
public class FixedThreadPool {
    private final Queue<Runnable> pool;
    private final List<Thread> processorThreads;
    private boolean shutdownFlag;

    /**
     * Creates fixed thread pool
     * @param threads number of worker threads.
     */
    public FixedThreadPool(int threads) {
        pool = new ConcurrentLinkedQueue<>();
        processorThreads = new ArrayList<>();
        shutdownFlag = false;

        for (int i = 0; i < threads; i++) {
            Thread thread = new MyThread(pool);
            processorThreads.add(thread);
            thread.start();
        }
    }

    /**
     * Add new task to thread pool.
     * @param task task to be added.
     */
    public void execute(Runnable task) {
        if (!shutdownFlag) pool.add(task);
    }

    /**
     * Interrupts all worker threads and clears task queue.
     * @throws InterruptedException is thrown from Thread.interrupt() method
     */
    public void shutdown() {
        pool.clear();
        processorThreads.forEach(x->x.interrupt());
        shutdownFlag = true;
    }
}