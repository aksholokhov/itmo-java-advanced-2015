package ru.ifmo.ctddev.sholokhov.iterativeparallelism;

import info.kgeorgiy.java.advanced.mapper.ParallelMapper;
import info.kgeorgiy.java.advanced.concurrent.ScalarIP;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Amazing class which realises most popular FP functions
 * @author Alexey Sholokhov
 *
 * @see info.kgeorgiy.java.advanced.concurrent.ScalarIP
 */
public class IterativeParallelism implements ScalarIP {

    ParallelMapper mapper;

    public IterativeParallelism() {
        this.mapper = null;
    }

    public IterativeParallelism(ParallelMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * Finds maximal element of the given list according to the given comparator
     *
     * @param threads quantity of threads
     * @param values list with data for processing
     * @param comparator comparator for comparing {@code values} elements
     * @param <T> result type, should be the same with the {@code values} elements type
     * @return the maximum in the {@code values} according to the {@code comparator}
     * @throws InterruptedException
     */
    @Override
    public <T> T maximum(int threads, List<? extends T> values, final Comparator<? super T> comparator) throws InterruptedException {
        return runInThreads(threads, values, work -> new Max<T>(work, comparator));
    }

    /**
     * Finds minimal element of the given list according to the given comparator
     *
     * @param threads quantity of threads
     * @param values list with data for processing
     * @param comparator comparator for comparing {@code values} elements
     * @param <T> result type, should be the same with the {@code values} elements type
     * @return the minimum in the {@code values} according to the {@code comparator}
     * @throws InterruptedException
     */
    @Override
    public <T> T minimum(int threads, List<? extends T> values, Comparator<? super T> comparator) throws InterruptedException {
        return runInThreads(threads, values, work -> new Min<T>(work, comparator));
    }

    /**
     * Determines whether all elements of the {@code list} satisfy {@predicate} or not
     *
     * @param threads quantity of threads
     * @param values list with data for processing
     * @param predicate comparator for comparing {@code values} elements
     * @param <T> result type, should be the same with the {@code values} elements type
     * @return true if all elements satisfy predicate, false otherwise
     * @throws InterruptedException
     */

    @Override
    public <T> boolean all(int threads, List<? extends T> values, Predicate<? super T> predicate) throws InterruptedException {
        return runInThreads(threads, values, work -> new All<T>(work, predicate));
    }


    /**
     * Determines whether at least one element of the {@code list} satisfies {@predicate} or not
     *
     * @param threads quantity of threads
     * @param values list with data for processing
     * @param predicate comparator for comparing {@code values} elements
     * @param <T> result type, should be the same with the {@code values} elements type
     * @return true if all elements satisfy predicate, false otherwise
     * @throws InterruptedException
     */
    @Override
    public <T> boolean any(int threads, List<? extends T> values, Predicate<? super T> predicate) throws InterruptedException {
        return runInThreads(threads, values, work -> new Any<T>(work, predicate));

    }

    private <T, P> P runInThreads(int parts, List<? extends T> values, Function<List<? extends T>, Processor<P>> work) throws InterruptedException{

        //Splitting job and creating workers
        List<Processor<P>> processors = splitJob(parts, values).stream().map(work).collect(Collectors.toList());
        List<P> results;

        if (mapper != null) {
            results = mapper.map(Processor::getCalculatedRes, processors);
        } else {
            //run all workers and waiting for finishing all of them
            List<Thread> threadsList = new ArrayList<>();
            for (Processor<P> Processor : processors) {
                Thread thread = new Thread(Processor);
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
            results = processors.stream().map(Processor::getCalculatedRes).collect(Collectors.toList());

        }


        //Get any processor and merge results
        P result = processors.get(0).merge(results);
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
