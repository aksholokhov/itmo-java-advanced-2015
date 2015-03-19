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

    /**
     * Returns maximal element of the given list according to the given comparator
     *
     * @param threads is threads count
     * @param values is the 
     * @param comparator
     * @param <T>
     * @return
     * @throws InterruptedException
     */

    @Override
    public <T> T maximum(int threads, List<? extends T> values, final Comparator<? super T> comparator) throws InterruptedException {
        return runInThreads(threads, values, work -> new Max<T>(work, comparator));
    }

    @Override
    public <T> T minimum(int threads, List<? extends T> values, Comparator<? super T> comparator) throws InterruptedException {
        return runInThreads(threads, values, work -> new Min<T>(work, comparator));
    }

    @Override
    public <T> boolean all(int threads, List<? extends T> values, Predicate<? super T> predicate) throws InterruptedException {
        return runInThreads(threads, values, work -> new All<T>(work, predicate));
    }

    @Override
    public <T> boolean any(int threads, List<? extends T> values, Predicate<? super T> predicate) throws InterruptedException {
        return runInThreads(threads, values, work -> new Any<T>(work, predicate));

    }

    private <T, P> P runInThreads(int parts, List<? extends T> values, Function<List<? extends T>, threadProcessor<P>> work) {

        //Splitting job and creating workers
        List<threadProcessor<P>> threadProcessors = splitJob(parts, values).stream().map(work).collect(Collectors.toList());

        //run all workers and waiting for finishing all of them
        List<Thread> threadsList = new ArrayList<>();
        for (threadProcessor<P> threadProcessor : threadProcessors) {
            Thread thread = new Thread(threadProcessor);
            threadsList.add(thread);
            thread.start();
        }
        try {
            for (Thread thread : threadsList) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Collect results of all processors to one list
        List<P> results = threadProcessors.stream().map(threadProcessor::getCalculatedRes).collect(Collectors.toList());

        //Get any processor and merge results
        P result = threadProcessors.get(0).merge(results);
        return result;
    }

    private <T> List<List<? extends T>> splitJob(int parts, List<? extends T> values) {
        //calculates how many list elements should be processed by one processor
        int amount = values.size();
        int workPerProcessor = Math.max(amount/parts, 1);
     //   System.out.println(parts + " " + amount + " " + workPerProcessor);
        //creates list of the task per processoe
        List<List<? extends T>> splittedWork = new ArrayList<>();
          for (int l = 0; l < amount; l+=workPerProcessor) {
                int r = Math.min(amount, l + workPerProcessor);
                splittedWork.add(values.subList(l, r));
            }

        return splittedWork;
    }
}
