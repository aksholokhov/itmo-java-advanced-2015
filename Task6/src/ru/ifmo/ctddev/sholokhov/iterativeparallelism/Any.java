package ru.ifmo.ctddev.sholokhov.iterativeparallelism;

import java.util.List;
import java.util.function.Predicate;

/**
 * Created by Шолохов on 18.03.2015.
 */
public class Any<T> implements Worker<Boolean> {
    private List<? extends T> list;
    private Predicate<? super T> predicate;
    private boolean result;

    public Any(List<? extends T> list, Predicate<? super T> predicate) {
        this.list = list;
        this.predicate = predicate;
    }

    @Override
    public void run() {
        for (T elem : list) {
            if (predicate.test(elem)) {
                result = true;
                break;
            }
        }
        result = false;
    }

    @Override
    public Boolean getResult() {
        return result;
    }

    @Override
    public Boolean doYourWork(List<? extends Boolean> work) {
        Worker<Boolean> worker = new Any<>(work, Predicate.isEqual(true));
        worker.run();
        return worker.getResult();
    }
}
