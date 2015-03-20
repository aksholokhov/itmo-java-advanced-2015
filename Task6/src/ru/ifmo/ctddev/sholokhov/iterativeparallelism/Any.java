package ru.ifmo.ctddev.sholokhov.iterativeparallelism;

import java.util.List;
import java.util.function.Predicate;

/**
 * Class which realizes determination whether at least one element of the {@code list} satisfies {@code predicate} or not
 * @param <T> type of the {@code list} elements and returned value
 */
public class Any<T> implements threadProcessor<Boolean> {
    private List<? extends T> list;
    private Predicate<? super T> predicate;
    private boolean result;

    /**
     * Public constructor
     * @param list list of the elements needs to be processed
     * @param predicate predicate for the {@code list} elements
     */

    public Any(List<? extends T> list, Predicate<? super T> predicate) {
        this.list = list;
        this.predicate = predicate;
    }

    /**
     * Runs the task
     */
    @Override
    public void run() {
        result = false;
        for (T elem : list) {
            if (predicate.test(elem)) {
                result = true;
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
     * Merges results of other Any-threadProcessor's results to one common result
     * @param parts list with the partial results
     * @return final result of the calculation Any-predicate in the initial list
     */
    @Override
    public Boolean merge(List<? extends Boolean> parts) {
        threadProcessor<Boolean> threadProcessor = new Any<>(parts, Predicate.isEqual(true));
        threadProcessor.run();
        return threadProcessor.getCalculatedRes();
    }
}
