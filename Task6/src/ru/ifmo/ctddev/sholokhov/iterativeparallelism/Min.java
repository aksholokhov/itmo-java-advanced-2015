package ru.ifmo.ctddev.sholokhov.iterativeparallelism;

import java.util.Comparator;
import java.util.List;

/**
 * Class which realizes finding first maximum of the list according to the {@code comparator}
 * @param <T> type of the {@code list's} elements and the result
 */

public class Min<T> extends LazyProcessor<T> {
    private List<? extends T> list;
    private Comparator<? super T> comparator;

    public Min(List<? extends T> list, Comparator<? super T> comparator) {
        super();
        this.list = list;
        this.comparator = comparator;
    }

    @Override
    public T calcResult() {
        return list.stream()
                .min(comparator)
                .get();
    }

    @Override
    public T merge(List<T> results) {
        Processor<T> worker = new Min<>(results, comparator);
        worker.run();
        return worker.getResult();
    }
}