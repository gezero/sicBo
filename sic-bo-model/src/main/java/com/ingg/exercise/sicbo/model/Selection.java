package com.ingg.exercise.sicbo.model;

/**
 * <p>
 * A player's prediction of the <i>result</i> of a round of Sic Bo, which he is invited to place bets on.
 * </p>
 *
 * @author iKernel Team
 * @author Inspired Gaming Group
 */
public enum Selection {

    /**
     * <p>
     * The dice:
     * <ul>
     * <li>will show a total value of 11 or more, and</li>
     * <li>not a <i>triple</i>.</li>
     * </ul>
     * </p>
     */
    BIG,

    /**
     * <p>
     * The dice:
     * <ul>
     * <li>will show a total value of 10 or fewer, and</li>
     * <li>not a <i>triple</i>.</li>
     * </ul>
     * </p>
     */
    SMALL,

    ;

}
