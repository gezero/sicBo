package com.ingg.exercise.sicbo.model;

/**
 * <p>
 * Announces the result of a round of Sic Bo to the world.
 * </p>
 *
 * @author iKernel Team
 * @author Inspired Gaming Group
 */
public interface ResultDisplay {

    /**
     * <p>
     * Announce the result of a specific round of Sic Bo.<br/>
     * The same round id should also be made available to the {@link BetFuture} instances.
     * </p>
     *
     * @param roundId a unique identifier for the round
     * @param result  the value of each die, in an arbitrary order
     */
    public abstract void displayResult(String roundId, Iterable<Integer> result);

}
