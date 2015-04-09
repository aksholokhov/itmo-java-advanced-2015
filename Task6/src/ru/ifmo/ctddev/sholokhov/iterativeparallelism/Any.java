package ru.ifmo.ctddev.sholokhov.iterativeparallelism;

import java.util.List;
import java.util.function.Predicate;

/**
 * Class which realizes determination whether at least one element of the {@code list} satisfies {@code predicate} or not
 * @param <T> type of the {@code list} elements and returned value
 */
public class Any<T> extends LazyProcessor<Boolean> {
    private All<T> allWorker;

    /**
     * Public constructor
     * @param list list of the elements needs to be processed
     * @param predicate predicate for the {@code list} elements
     * */

    public Any(List<? extends T> list, Predicate<? super T> predicate) {
        super();
        allWorker = new All<>(list, predicate.negate());
    }

    /**
     * Calculated result
     * @return {@code result} of the calculation
     */
    @Override
    public Boolean calcResult() {
        return !allWorker.getResult();
    }

    /**
     * Merges results of other All-threadProcessors to one common result
     * @param results list with the partial results
     * @return final result of the calculation Any-predicate in the initial list
     */
    @Override
    public Boolean merge(List<Boolean> results) {
        return results.stream()
                .reduce(false, Boolean::logicalOr);
    }
}
