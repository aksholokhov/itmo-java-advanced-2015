package ru.ifmo.ctddev.sholokhov.iterativeparallelism;

import java.util.List;

/**
 * Created by Шолохов on 25.03.2015.
 */
public class LazyProcessor<T> implements Processor<T>{

    @Override
    public T getCalculatedRes() {
        return null;
    }

    @Override
    public T merge(List<? extends T> parts) {
        return null;
    }

    @Override
    public void run() {

    }
}
