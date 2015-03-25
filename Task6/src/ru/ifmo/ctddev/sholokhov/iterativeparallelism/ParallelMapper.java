package ru.ifmo.ctddev.sholokhov.iterativeparallelism;

/**
 * Created by Шолохов on 24.03.2015.
 */
import java.util.List;
import java.util.function.Function;

/**
 * ParallelMapper interface.
 *
 * @see java.lang.AutoCloseable
 */
public interface ParallelMapper extends AutoCloseable {
    <T, R> List<R> map(
            Function<? super T, ? extends R> f,
            List<? extends T> args
    ) throws InterruptedException;

    @Override
    void close() throws InterruptedException;
}