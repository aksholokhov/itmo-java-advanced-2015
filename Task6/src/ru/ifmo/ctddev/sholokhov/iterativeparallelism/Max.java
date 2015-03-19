package ru.ifmo.ctddev.sholokhov.iterativeparallelism;

import java.util.Comparator;
import java.util.List;

/**
 * Created by Шолохов on 18.03.2015.
 */
public class Max<T> implements threadProcessor<T> {
    private List<? extends T> work;
    private Comparator<? super T> comparator;
    private T result;

    Max (List<? extends T> work, Comparator<? super T> comparator) {
        this.work = work;
        this.comparator = comparator;
    }

    @Override
    public T getCalculatedRes() {
        return result;
    }

    @Override
    public T merge(List<? extends T> work) {
        threadProcessor<T> threadProcessor = new Max<T>(work, comparator);
        threadProcessor.run();
        return threadProcessor.getCalculatedRes();
    }

    @Override
    public void run() {
        T max = work.get(0);
        for (T cur : work) {
            if (comparator.compare(cur, max) > 0) {
                max = cur;
            }
        }
        result = max;
    }
}
