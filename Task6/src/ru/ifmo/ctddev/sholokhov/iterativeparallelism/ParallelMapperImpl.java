package ru.ifmo.ctddev.sholokhov.iterativeparallelism;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Created by Шолохов on 24.03.2015.
 */
public class ParallelMapperImpl implements ParallelMapper {

    ThreadPool threadPool;

    ParallelMapperImpl (int Threads) {
        this.threadPool = new ThreadPool(Threads);
    }

    @Override
    public <T, R> List<R> map(Function<? super T, ? extends R> f, List<? extends T> args) throws InterruptedException {
        List<R> res = new ArrayList<R>();
        PublicCounter counter = new PublicCounter();

        for (int i = 0; i < args.size(); i++) {
            res.add(null);
            threadPool.execute(new Work(counter, new Map<T, R>(i, res, f, args.get(i))));
        }

        counter.sleepUntil(res.size());

        return res;
    }

    @Override
    public void close() throws InterruptedException {

    }
}
