package com.ingg.exercise.sicbo.model;

/**
 * <p>
 * Allows a player to receive a prize after a bet has been settled by the dealer.
 * </p>
 *
 * @author iKernel Team
 * @author Inspired Gaming Group
 */
public interface BetFuture {

    /**
     * <p>
     * Returns the unique identifier for the round in which the bet was accepted.<br/>
     * This should be the same identifier passed to the {@link ResultDisplay} for this round.
     * </p>
     *
     * @return the round identifier
     */
    public abstract String getRoundId();

    /**
     * <p>
     * Returns the amount of money which the dealer has awarded the player as a prize.<br/>
     * This can be <code>0</code> if the bet loses.
     * </p>
     * <p>
     * This method should block until the bet has been settled by the dealer.
     * </p>
     *
     * @return the prize amount
     * @throws InterruptedException if the calling thread is interrupted whilst waiting for the bet to be settled
     */
    public abstract Integer getPrize() throws InterruptedException;

}
