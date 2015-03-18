package ru.ifmo.ctddev.sholokhov.iterativeparallelism;

import info.kgeorgiy.java.advanced.concurrent.ScalarIP;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by Шолохов on 18.03.2015.
 */
public class IterativeParallelism implements ScalarIP {

    @Override
    public <T> T maximum(int threads, List<? extends T> values, final Comparator<? super T> comparator) throws InterruptedException {
        return doInParallel(threads, values, work -> new Max<T>(work, comparator));
    }

    @Override
    public <T> T minimum(int threads, List<? extends T> values, Comparator<? super T> comparator) throws InterruptedException {
        return doInParallel(threads, values, work -> new Min<T>(work, comparator));
    }

    @Override
    public <T> boolean all(int threads, List<? extends T> values, Predicate<? super T> predicate) throws InterruptedException {
        return doInParallel(threads, values, work -> new All<T>(work, predicate));
    }

    @Override
    public <T> boolean any(int threads, List<? extends T> values, Predicate<? super T> predicate) throws InterruptedException {
        return doInParallel(threads, values, work -> new Any<T>(work, predicate));

    }

    private <T, P> P doInParallel(int parts, List<? extends T> values, Function<List<? extends T>, Worker<P>> work) {
        List<Worker<P>> workers = splitJob(parts, values).stream().map(work).collect(Collectors.toList());

        List<Thread> threads2 = new ArrayList<>();
        for (Worker<P> worker : workers) {
            Thread thread = new Thread(worker);
            threads2.add(thread);
            thread.start();
        }
        try {
            for (Thread thread : threads2) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<P> results = workers.stream().map(Worker::getResult).collect(Collectors.toList());

        return workers.get(0).doYourWork(results);
    }

    private <T> List<List<? extends T>> splitJob(int parts, List<? extends T> values) {
        int amount = values.size();
        int workPerWorker = amount/parts;

        List<List<? extends T>> splittedJob = new ArrayList<>();

        for (int l = 0; l < amount; l+=workPerWorker) {
            int r = Math.min(amount, l + workPerWorker);
            splittedJob.add(values.subList(l, r));
        }
        return splittedJob;
    }
}
