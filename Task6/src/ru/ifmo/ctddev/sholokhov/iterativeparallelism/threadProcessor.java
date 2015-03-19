package ru.ifmo.ctddev.sholokhov.iterativeparallelism;

import java.util.List;

/**
 * Created by Шолохов on 18.03.2015.
 */
public interface threadProcessor<T> extends Runnable{
    T getCalculatedRes();
    T merge(List<? extends T> otherPars);
}
