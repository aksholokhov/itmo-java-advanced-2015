package ru.ifmo.ctddev.sholokhov.iterativeparallelism;

/**
 * Created by Шолохов on 01.04.2015.
 */
import java.util.List;
import java.util.function.Function;

/**
 * Parallel map worker, calculating and storing result in outList.
 * @param <T> type of argument.
 * @param <R> type of result.
 * @see java.lang.Runnable
 */
public class MapProcessor<T, R> implements Runnable {
    private final Function<? super T, ? extends R> f;
    private final T arg;
    private final List<R> outList;
    private final int index;

    public MapProcessor(Function<? super T, ? extends R> f, T arg, List<R> outList, int index) {
        this.f = f;
        this.arg = arg;
        this.outList = outList;
        this.index = index;
    }

    @Override
    public void run() {
        R value = f.apply(arg);

        synchronized (outList) {
            outList.set(index, value);
        }
    }
}