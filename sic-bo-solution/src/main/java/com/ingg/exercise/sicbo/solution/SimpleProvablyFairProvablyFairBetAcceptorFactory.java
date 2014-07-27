package com.ingg.exercise.sicbo.solution;

/**
 * @author Jiri
 */
public class SimpleProvablyFairProvablyFairBetAcceptorFactory implements ProvablyFairBetAcceptorFactory {
    @Override
    public ProvablyFairBetAcceptor createNewAcceptor(String roundId) {
        return new SimpleProvablyFairBetAcceptor(roundId);
    }
}
