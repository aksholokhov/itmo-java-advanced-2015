package ru.ifmo.ctddev.sholokhov.iterativeparallelism;

import java.util.List;

/**
 * Created by Шолохов on 18.03.2015.
 */
public interface Worker<T> extends Runnable{
    T getResult();
    T doYourWork(List<? extends T> otherPars);
}
