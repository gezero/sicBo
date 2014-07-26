package com.ingg.exercise.sicbo.solution;

import com.ingg.exercise.sicbo.model.BetFuture;
import com.ingg.exercise.sicbo.model.ResultDisplay;
import com.ingg.exercise.sicbo.model.Selection;
import com.ingg.exercise.sicbo.model.Table;
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
public class SicBo implements Table, DealerObserver {

    private final ResultDisplay resultDisplay;
    private Dealer dealer;

    private String currentRound;

    public SicBo(ResultDisplay resultDisplay, Dealer dealer) {
        this.resultDisplay = resultDisplay;
        this.dealer = dealer;
    }

    /**
     * The constraint to have predefined construction is strange. It limits the way how to inject dependencies properly.
     * The following code I would rather put into configuration or autowiring of injecting dependencies. The
     * requirements however expect that this constructor exists and the evaluation program calls it.
     */
    public SicBo(ResultDisplay resultDisplay) {
        this.resultDisplay = resultDisplay;
        //TODO: do not forget to configure here the rest of dependencies before sending the solution back.
    }

    @Override
    public void open() {
        dealer.subscribe(this);
    }

    @Override
    public void close() {
        // TODO
    }

    @Override
    public BetFuture acceptBet(Selection selection, Integer stake) throws TableClosedException {
        if (currentRound == null) {
            throw new TableClosedException();
        }
        return null;
    }

    @Override
    public void newRoll(int i) {

    }
}
