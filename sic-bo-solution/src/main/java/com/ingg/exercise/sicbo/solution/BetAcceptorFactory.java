package com.ingg.exercise.sicbo.solution;

/**
 * @author Jiri
 */
public interface BetAcceptorFactory {
    BetAcceptor createNewAcceptor(String roundId);
}
