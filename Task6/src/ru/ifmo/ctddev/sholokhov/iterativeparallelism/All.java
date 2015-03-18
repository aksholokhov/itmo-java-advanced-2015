package ru.ifmo.ctddev.sholokhov.iterativeparallelism;

import java.util.List;
import java.util.function.Predicate;

/**
 * Created by Шолохов on 18.03.2015.
 */
public class All<T> implements Worker<Boolean> {
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
    public Boolean getResult() {
        return result;
    }

    @Override
    public Boolean doYourWork(List<? extends Boolean> work) {
        Worker<Boolean> worker = new All<>(work, Predicate.isEqual(true));
        worker.run();
        return worker.getResult();
    }
}
