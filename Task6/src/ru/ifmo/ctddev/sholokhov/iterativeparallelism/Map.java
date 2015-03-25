package ru.ifmo.ctddev.sholokhov.iterativeparallelism;

import java.util.List;
import java.util.function.Function;

/**
 * Created by Шолохов on 25.03.2015.
 */
public class Map<T, R> implements Runnable {

    Function<? super T, ? extends R> func;
    T arg;
    int pos;
    List<R> res;

    Map (int pos, List<R> res, Function<? super T, ? extends R> func, T arg) {
        this.pos = pos;
        this.func = func;
        this.res = res;
        this.arg = arg;
    }

    @Override
    public void run() {
        R result = func.apply(arg);

        synchronized (res) {
            res.set(pos, result);
        }
    }
}
