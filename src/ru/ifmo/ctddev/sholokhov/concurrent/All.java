package ru.ifmo.ctddev.sholokhov.concurrent;

import java.util.List;
import java.util.function.Predicate;

/**
 * Class which realizes determination whether all elements of the {@code list} satisfy {@code predicate} or not
 * @param <T> type of the {@code list} elements and returned value
 */
public class All<T> extends LazyProcessor<Boolean> {
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
     * Calculated result
     * @return {@code result} of the calculation
     */
    @Override
    public Boolean calcResult() {
        return list.stream()
                .reduce(true, (a, b) -> a && predicate.test(b), Boolean::logicalAnd);
    }

    /**
     * Merges results of other All-threadProcessors to one common result
     * @param parts list with the partial results
     * @return final result of the calculation Any-predicate in the initial list
     */
    @Override
    public Boolean merge(List<Boolean> parts) {
        return parts.stream()
                .reduce(true, Boolean::logicalAnd);
    }
}
