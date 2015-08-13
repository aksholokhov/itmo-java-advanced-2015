package ru.ifmo.ctddev.sholokhov.concurrent;

import java.util.List;

/**
 * Interface for a runnable task with additional functions
 * @param <T> the type of the thread's returned value
 */

public interface Processor<T> extends Runnable{
    default void run() {
        getResult();
    }

    /**
     * Getter for the result of the task's calculation
     * @return the result of the given task
     */
    T getResult();

    /**
     * Additional function for merging results of the other threadProcessors
     * (ea) in one result
     * @param parts list with the partial results
     * @return general result for the initial list
     */
    T merge(List<T> parts);
}
