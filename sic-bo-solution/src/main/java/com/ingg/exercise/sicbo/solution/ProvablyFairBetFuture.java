package com.ingg.exercise.sicbo.solution;

import com.ingg.exercise.sicbo.model.BetFuture;

/**
 * @author Jiri
 */
public interface ProvablyFairBetFuture extends BetFuture {
    Iterable<Integer> getRoll() throws InterruptedException;

    String getSalt() throws InterruptedException;
}
