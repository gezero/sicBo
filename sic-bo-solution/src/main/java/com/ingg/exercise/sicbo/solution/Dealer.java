package com.ingg.exercise.sicbo.solution;

/**
 * @author Jiri
 */
public interface Dealer {
    /**
     * This methods subscribes to a dealer and returns first roll in case the roll is needed immediately
     *
     * @param observer observer will be notified whenever dealer rolls again
     * @return integers representing values on dices.
     */
    Iterable<Integer> subscribe(DealerObserver observer);

    /**
     * This method let the dealer know that he should stop rolling. New roll will be returned in case it is necessary
     *
     * @return integers representing values on dices.
     */
    Iterable<Integer> stop();
}
