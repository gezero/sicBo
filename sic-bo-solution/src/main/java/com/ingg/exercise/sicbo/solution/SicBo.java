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
    private BetAcceptorFactory betAcceptorFactory;

    private BetAcceptor betAcceptor;
    private RandomStringGenerator randomStringGenerator;
    private String currentRoundId;

    public SicBo(ResultDisplay resultDisplay, Dealer dealer, BetAcceptorFactory betAcceptorFactory, RandomStringGenerator randomStringGenerator) {
        this.resultDisplay = resultDisplay;
        this.dealer = dealer;
        this.betAcceptorFactory = betAcceptorFactory;
        this.randomStringGenerator = randomStringGenerator;
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
        startNewRound();
    }

    private void startNewRound() {
        currentRoundId = randomStringGenerator.generateString();
        betAcceptor = betAcceptorFactory.createNewAcceptor(currentRoundId);
    }

    @Override
    public void close() {
        Iterable<Integer> lastRoll = dealer.stop();
        finishRound(lastRoll);
    }

    @Override
    public BetFuture acceptBet(Selection selection, Integer stake) throws TableClosedException {
        if (betAcceptor == null) {
            throw new TableClosedException();
        }
        return betAcceptor.acceptBet(selection, stake);
    }

    @Override
    public void newRoll(Iterable<Integer> roll) {
        finishRound(roll);
        resultDisplay.displayResult(currentRoundId, roll);
        startNewRound();
    }

    private void finishRound(Iterable<Integer> roll) {
        RoundResult result = new RoundResultPojo(roll);
        betAcceptor.finishRound(result);
    }

    private class RoundResultPojo implements RoundResult {
        private Iterable<Integer> roll;

        private RoundResultPojo(Iterable<Integer> roll) {
            this.roll = roll;
        }

        @Override
        public Iterable<Integer> getRoll() {
            return roll;
        }


    }
}
