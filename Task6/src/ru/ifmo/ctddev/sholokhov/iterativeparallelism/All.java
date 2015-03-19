package ru.ifmo.ctddev.sholokhov.iterativeparallelism;

import java.util.List;
import java.util.function.Predicate;

/**
 * Created by Шолохов on 18.03.2015.
 */
public class All<T> implements threadProcessor<Boolean> {
    private List<? extends T> list;
    private Predicate<? super T> predicate;
    private boolean result;

    public All(List<? extends T> list, Predicate<? super T> predicate) {
        this.list = list;
        this.predicate = predicate;
    }

    @Override
    public void run() {
        result = true;
        for (T elem : list) {
            if (!predicate.test(elem)) {
                result = false;
                break;
            }
        }
    }

    @Override
    public Boolean getCalculatedRes() {
        return result;
    }

    @Override
    public Boolean merge(List<? extends Boolean> work) {
        threadProcessor<Boolean> threadProcessor = new All<>(work, Predicate.isEqual(true));
        threadProcessor.run();
        return threadProcessor.getCalculatedRes();
    }
}
