package ru.ifmo.ctddev.sholokhov.webcrawler;


/**
 * Processor wrapper that increments counter after the wrapped job's done.
 */
public class Subtask implements ThrowedRunnable {
    private FinishTrigger finishTrigger;
    private ThrowedRunnable runnable;

    public Subtask(FinishTrigger finishTrigger, ThrowedRunnable runnable) {
        this.finishTrigger = finishTrigger;
        this.runnable = runnable;
    }

    @Override
    public void run() {
        try {
            runnable.run();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        finally {
            finishTrigger.set(0);
        }
    }
}