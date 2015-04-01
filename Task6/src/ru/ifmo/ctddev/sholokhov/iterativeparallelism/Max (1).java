package ru.ifmo.ctddev.sholokhov.iterativeparallelism;

import java.util.Comparator;
import java.util.List;

/**
 * Class which realizes finding first maximum of the list according to the {@code comparator}
 * @param <T> type of the {@code list's} elements and the result
 */
public class Max<T> implements Processor<T> {
    private List<? extends T> list;
    private Comparator<? super T> comparator;
    private T result;

    /**
     * Public constructor
     * @param list list of the elements needs to be processed
     * @param comparator comparator for the {@code list} elements
     */
    Max (List<? extends T> list, Comparator<? super T> comparator) {
        this.list = list;
        this.comparator = comparator;
    }

    /**
     * Getter for the result
     * @return {@code result} of the calculation
     */
    @Override
    public T getCalculatedRes() {
        return result;
    }


    /**
     * Merges results of other Max-threadProcessor's results to one common result
     * @param parts list with the partial results
     * @return final result of the calculation Any-predicate in the initial list
     */
    @Override
    public T merge(List<? extends T> parts) {
        Processor<T> Processor = new Max<T>(parts, comparator);
        Processor.run();
        return Processor.getCalculatedRes();
    }

    /**
     * Runs the task
     */
    @Override
    public void run() {
        T max = list.get(0);
        for (T cur : list) {
            if (comparator.compare(cur, max) > 0) {
                max = cur;
            }
        }
        result = max;
    }
}
