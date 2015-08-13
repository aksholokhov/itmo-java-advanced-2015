package ru.ifmo.ctddev.sholokhov.concurrent;

/**
 * Created by Шолохов on 01.04.2015.
 */
import java.util.Optional;

/**
 * Worker interface implementation with memoization of result.
 *
 * @param <R> result type.
 */
public abstract class LazyProcessor<R> implements Processor<R> {
    private Optional<R> result;

    public LazyProcessor() {
        result = Optional.empty();
    }

    public void run() {
        getResult();
    }

    public R getResult() {
        if (!result.isPresent()) {
            result = Optional.of(calcResult());
        }

        return result.get();
    }

    /**
     * Returns the result of thread's work.
     *
     * @return result of {@link java.lang.Runnable#run() run()} method.
     */
    protected abstract R calcResult();
}
