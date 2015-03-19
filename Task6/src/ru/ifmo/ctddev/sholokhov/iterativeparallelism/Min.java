package ru.ifmo.ctddev.sholokhov.iterativeparallelism;

import java.util.Comparator;
import java.util.List;

/**
 * Created by Шолохов on 18.03.2015.
 */
public class Min<T> implements threadProcessor<T> {
    private List<? extends T> work;
    private Comparator<? super T> comparator;
    private T result;

    Min (List<? extends T> work, Comparator<? super T> comparator) {
        this.work = work;
        this.comparator = comparator;
    }

    @Override
    public T getCalculatedRes() {
        return result;
    }

    @Override
    public T merge(List<? extends T> work) {
        threadProcessor<T> threadProcessor = new Min<T>(work, comparator);
        threadProcessor.run();
        return threadProcessor.getCalculatedRes();
    }

    @Override
    public void run() {
        T min = work.get(0);
        for (T cur : work) {
            if (comparator.compare(min, cur) > 0) {
                min = cur;
            }
        }
        result = min;
    }
}