package com.ingg.exercise.sicbo.solution;

import com.ingg.exercise.sicbo.model.BetFuture;
import com.ingg.exercise.sicbo.model.Selection;

/**
 * @author Jiri
 */
public interface BetAcceptor {

    public BetFuture acceptBet(Selection selection, Integer stake);

    public void finishRound(Iterable<Integer> roll, String salt);

}
