package ru.ifmo.ctddev.sholokhov.concurrent;

import java.util.Comparator;
import java.util.List;

/**
 * Class which realizes finding first maximum of the list according to the {@code comparator}
 * @param <T> type of the {@code list's} elements and the result
 */
public class Max<T> extends LazyProcessor<T> {
    private Min<T> minimumWorker;

    public Max(List<? extends T> list, Comparator<? super T> comparator) {
        super();
        minimumWorker = new Min<>(list, comparator.reversed());
    }

    public T calcResult() {
        return minimumWorker.getResult();
    }

    @Override
    public T merge(List<T> results) {
        return minimumWorker.merge(results);
    }
}
