package com.ingg.exercise.sicbo.solution;

import com.ingg.exercise.sicbo.model.BetFuture;
import com.ingg.exercise.sicbo.model.Table;
import com.ingg.exercise.sicbo.model.ResultDisplay;
import com.ingg.exercise.sicbo.model.Selection;
import com.ingg.exercise.sicbo.model.exception.TableClosedException;
import net.jcip.annotations.ThreadSafe;

/**
 * <p>
 * <b>Add code to this class to solve the exercise.</b>
 * </p>
 * <hr/>
 * <p>
 * Your responsibilities include:
 * <ul>
 * <li>managing the lifecycle of the table,</li>
 * <li>generating identifiers for each round,</li>
 * <li>accepting (or rejecting) bets from players,</li>
 * <li>generating results and displaying them,</li>
 * <li>settling bets, and delivering prizes to the players.</li>
 * </ul>
 * </p>
 *
 * @author Jiri Peinlich
 */
@ThreadSafe
public class SicBo implements Table {

    private final ResultDisplay resultDisplay;

    public SicBo(ResultDisplay resultDisplay) {
        this.resultDisplay = resultDisplay;
    }

    @Override
    public void open() {
        // TODO
    }

    @Override
    public void close() {
        // TODO
    }

    @Override
    public BetFuture acceptBet(Selection selection, Integer stake) throws TableClosedException {
        // TODO
        return null;
    }

}
