package ru.ifmo.ctddev.sholokhov.iterativeparallelism;

import java.util.List;
import java.util.function.Predicate;

/**
 * Class which realizes determination whether all elements of the {@code list} satisfy {@code predicate} or not
 * @param <T> type of the {@code list} elements and returned value
 */
public class All<T> implements Processor<Boolean> {
    private List<? extends T> list;
    private Predicate<? super T> predicate;
    private boolean result;


    /**
     * Public constructor
     * @param list list of the elements needs to be processed
     * @param predicate predicate for the {@code list} elements
     */

    public All(List<? extends T> list, Predicate<? super T> predicate) {
        this.list = list;
        this.predicate = predicate;
    }


    /**
     * Runs the task
     */

    @Override
    public void run() {
        result = true;
        for (T elem : list) {
            if (!predicate.test(elem)) {
                result = false;
                break;
            }
        }
    }

    /**
     * Getter for the result
     * @return {@code result} of the calculation
     */
    @Override
    public Boolean getCalculatedRes() {
        return result;
    }

    /**
     * Merges results of other All-threadProcessor's results to one common result
     * @param parts list with the partial results
     * @return final result of the calculation Any-predicate in the initial list
     */
    @Override
    public Boolean merge(List<? extends Boolean> parts) {
        Processor<Boolean> Processor = new All<>(parts, Predicate.isEqual(true));
        Processor.run();
        return Processor.getCalculatedRes();
    }
}
