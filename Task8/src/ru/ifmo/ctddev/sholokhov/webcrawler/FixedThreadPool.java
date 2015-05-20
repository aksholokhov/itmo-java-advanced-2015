package ru.ifmo.ctddev.sholokhov.webcrawler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread pool with fixed amount of workers.
 */
public class FixedThreadPool {
    private final Queue<DownloadTask> pool;
    private List<Thread> workerThreads;
    private boolean shutdownFlag;
    private final Map<String, Integer> poolCount;
    private final Map<String, Integer> threadCount;
    private int maxThreads;
    private AtomicInteger freeThreads;
    private int perHost;

    /**
     * Creates fixed thread pool.
     * @param nThreads number of worker threads.
     */
    public FixedThreadPool(int nThreads, int _perHost) {
        pool = new ConcurrentLinkedQueue<>();
        workerThreads = new ArrayList<>();
        shutdownFlag = false;
        poolCount = new ConcurrentHashMap<>();
        threadCount = new ConcurrentHashMap<>();
        maxThreads = nThreads;
        freeThreads = new AtomicInteger(0);
        perHost = _perHost;
    }

    /**
     * Interrupts all worker threads and clears task queue.
     * @throws InterruptedException is thrown from Thread.interrupt() method
     */
    public void shutdown() {
        pool.clear();

        workerThreads.forEach(Thread::interrupt);
        shutdownFlag = true;
    }

    /**
     * Add new task to thread pool.
     * @param host group identifier of a task.
     * @param command task to be added.
     */
    public void execute(String host, Runnable command) {
        if (!shutdownFlag) {
            synchronized (poolCount) {
                int count = poolCount.containsKey(host) ? poolCount.get(host) : 0;
                poolCount.put(host, count + 1);
            }

            pool.add(new DownloadTask(host, command));

            if (workerThreads.size() < maxThreads) {
                Thread workerThread = new Thread() {
                    @Override
                    public void run() {
                        DownloadTask task;

                        freeThreads.incrementAndGet();

                        while (!Thread.interrupted()) {
                            task = pool.poll();

                            if (task != null) {
                                int hostPoolCount = poolCount.get(task.host);
                                int hostThreadCount = threadCount.containsKey(task.host) ? threadCount.get(task.host) : 0;

                                poolCount.put(task.host, hostPoolCount - 1);

                                if (hostThreadCount < perHost) {
                                    threadCount.put(task.host, hostThreadCount + 1);
                                    freeThreads.decrementAndGet();
                                    task.command.run();
                                    freeThreads.incrementAndGet();
                                    threadCount.put(task.host, hostThreadCount - 1);
                                } else if (false && hostPoolCount == pool.size()) {
                                    // ???
                                } else {
                                    pool.add(task);
                                }
                            }
                        }
                    }
                };
                workerThreads.add(workerThread);
                workerThread.start();
            }
        }
    }

    private static final class DownloadTask {
        public final String host;
        public final Runnable command;

        public DownloadTask(String host, Runnable command) {
            this.host = host;
            this.command = command;
        }
    }
}
