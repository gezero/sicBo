package com.ingg.exercise.sicbo.solution;

import com.ingg.exercise.sicbo.model.BetFuture;
import com.ingg.exercise.sicbo.model.Selection;
import com.ingg.exercise.sicbo.model.exception.TableClosedException;

/**
 * @author Jiri
 */
public interface ProvablyFairBetAcceptor {
    public BetFuture acceptBet(Selection selection, Integer stake) throws TableClosedException;
    void finishRound(ProvablyFairResult result);
}
