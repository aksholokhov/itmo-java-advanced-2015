package ru.ifmo.ctddev.sholokhov.iterativeparallelism;

import java.util.Comparator;
import java.util.List;

/**
 * Created by Шолохов on 18.03.2015.
 */
public class Max<T> implements Worker<T> {
    private List<? extends T> work;
    private Comparator<? super T> comparator;
    private T result;

    Max (List<? extends T> work, Comparator<? super T> comparator) {
        this.work = work;
        this.comparator = comparator;
    }

    @Override
    public T getResult() {
        return result;
    }

    @Override
    public T doYourWork(List<? extends T> work) {
        Worker<T> worker = new Max<T>(work, comparator);
        worker.run();
        return worker.getResult();
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
