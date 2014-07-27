package com.ingg.exercise.sicbo.solution;

/**
 * @author Jiri
 */
public class SimpleBetAcceptorFactory implements BetAcceptorFactory {
    @Override
    public BetAcceptor createNewAcceptor(String roundId) {
        return new SimpleBetAcceptor(roundId);
    }
}
