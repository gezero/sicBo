package com.ingg.exercise.sicbo.model;

import com.ingg.exercise.sicbo.model.exception.TableClosedException;

/**
 * <p>
 * Represents a Sic Bo table in a casino.<br/>
 * The table has a lifecycle (it can be opened and closed), and players attempt to place bets on it.
 * </p>
 *
 * @author iKernel Team
 * @author Inspired Gaming Group
 */
public interface Table {

    /**
     * <p>
     * Begin operating rounds of Sic Bo.
     * </p>
     * <p>
     * This method is guaranteed to be invoked:
     * <ul>
     * <li>exactly once in the lifetime of the application,</li>
     * <li>before {@link #close()}.</li>
     * </ul>
     * </p>
     * <p>
     * This method can be invoked from any thread.
     * </p>
     */
    void open();

    /**
     * <p>
     * Cease operating rounds of Sic Bo.<br/>
     * The current round must run to completion (i.e. result generated & displayed, bets settled & prizes delivered etc.).<br/>
     * However, the five second period for accepting bets <i>may</i> be curtailed, at your discretion.
     * </p>
     * <p>
     * This method is guaranteed to be invoked:
     * <ul>
     * <li>exactly once in the lifetime of the application,</li>
     * <li>after {@link #open()} has returned.</li>
     * </ul>
     * </p>
     * <p>
     * This method can be invoked from any thread.
     * </p>
     */
    void close();

    /**
     * <p>
     * A player wishes to place a bet for the current round of Sic Bo (if any).
     * </p>
     * <p>
     * This method may be invoked at <u>any</u> time (possibly before {@link #open()}, or after {@link #close()}).<br/>
     * This method may be invoked from multiple threads, concurrently.
     * </p>
     *
     * @param selection the {@link Selection} which the player wishes to place a bet on
     * @param stake     the amount of money which the player wishes to bet
     * @return a {@link BetFuture} which allows a prize to be delivered to the player
     * @throws TableClosedException if the table is not (yet) open
     */
    public abstract BetFuture acceptBet(Selection selection, Integer stake) throws TableClosedException;

}
