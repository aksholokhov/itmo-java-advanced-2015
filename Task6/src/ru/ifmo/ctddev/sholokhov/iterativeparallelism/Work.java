package ru.ifmo.ctddev.sholokhov.iterativeparallelism;

/**
 * Created by Шолохов on 25.03.2015.
 */
public class Work implements Runnable {
    private PublicCounter counter;
    private Runnable runnable;

    public Work(PublicCounter counter, Runnable runnable) {
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