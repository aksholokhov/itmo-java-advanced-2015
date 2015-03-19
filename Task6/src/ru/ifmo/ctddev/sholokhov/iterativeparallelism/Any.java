package ru.ifmo.ctddev.sholokhov.iterativeparallelism;

import java.util.List;
import java.util.function.Predicate;

/**
 * Created by Шолохов on 18.03.2015.
 */
public class Any<T> implements threadProcessor<Boolean> {
    private List<? extends T> list;
    private Predicate<? super T> predicate;
    private boolean result;

    public Any(List<? extends T> list, Predicate<? super T> predicate) {
        this.list = list;
        this.predicate = predicate;
    }

    @Override
    public void run() {
        result = false;
        for (T elem : list) {
            if (predicate.test(elem)) {
                result = true;
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
        threadProcessor<Boolean> threadProcessor = new Any<>(work, Predicate.isEqual(true));
        threadProcessor.run();
        return threadProcessor.getCalculatedRes();
    }
}
