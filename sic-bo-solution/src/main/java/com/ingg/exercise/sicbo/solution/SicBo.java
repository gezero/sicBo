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

    boolean open = false;
    private BetAcceptor betAcceptor;
    private RandomStringGenerator randomStringGenerator;
    private String currentRoundId;


    @SuppressWarnings("UnusedDeclaration") //This method is used by Mockito when injecting mocks
    public SicBo(ResultDisplay resultDisplay, Dealer dealer, BetAcceptorFactory betAcceptorFactory, RandomStringGenerator randomStringGenerator) {
        this.resultDisplay = resultDisplay;
        this.dealer = dealer;
        this.betAcceptorFactory = betAcceptorFactory;
        this.randomStringGenerator = randomStringGenerator;
    }

    /**
     * The constraint to have predefined construction is strange. It limits the way how to inject dependencies properly.
     * The following code I would rather put into configuration or autowiring of injecting dependencies. The
     * requirements however expect that this constructor exists and the evaluation program probably calls it.
     */
    public SicBo(ResultDisplay resultDisplay) {
        this.resultDisplay = resultDisplay;
        SessionRandomGenerator generator = new SessionRandomGenerator();
        this.randomStringGenerator = generator;
        this.betAcceptorFactory = new SimpleBetAcceptorFactory();
        this.dealer = new NormalDealer(generator, 5_000);
    }

    @Override
    public synchronized void open() {
        if (open) {
            throw new RuntimeException("This table is already opened");
        }
        dealer.subscribe(this);
        startNewRound();
        open = true;
    }

    private void startNewRound() {
        currentRoundId = randomStringGenerator.generateString();
        betAcceptor = betAcceptorFactory.createNewAcceptor(currentRoundId);
    }

    @Override
    public synchronized void close() {
        if (!open) {
            throw new RuntimeException("You need to open the table first");
        }
        open = false;
        Iterable<Integer> lastRoll = dealer.stop();
        betAcceptor.finishRound(new RoundResultPojo(lastRoll));
        resultDisplay.displayResult(currentRoundId, lastRoll);
    }

    @Override
    public synchronized BetFuture acceptBet(Selection selection, Integer stake) throws TableClosedException {
        if (!open) {
            throw new TableClosedException();
        }
        return betAcceptor.acceptBet(selection, stake);
    }

    @Override
    public synchronized void newRoll(Iterable<Integer> roll) {
        if (!open) {
            throw new RuntimeException("Cannot accept rolls when not open");
        }
        BetAcceptor acceptor = betAcceptor;
        String roundId = currentRoundId;

        startNewRound();
        acceptor.finishRound(new RoundResultPojo(roll));
        resultDisplay.displayResult(roundId, roll);
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

    /**
     * For some reason the Selection Enum does not have Triple selection... I would prefer to have this or similar
     * method on the Selection enum and it should return the proper selection. It is prohibited to change the module
     * where Selection is declared however...
     *
     * @param roll roll to be decided on
     * @return returns true if all values in the roll are the same.
     */
    public static boolean isTriple(Iterable<Integer> roll) {
        Integer number = null;
        for (Integer integer : roll) {
            if (number == null) {
                number = integer;
            }
            if (!number.equals(integer)) {
                return false;
            }
        }
        return true;
    }

    /**
     * This method should be on the Selection enum to properly choose the selection, it is here because I am prohibited
     * to change the module where the Selection is declared. This method should have been returning Selection.Triple,
     * but cannot.
     *
     * @param roll roll to be decided on
     * @return returns SMALL in case the sum smaller or equal to 10, returns BIG in case the sum is bigger then 10
     */
    public static Selection calculateSelection(Iterable<Integer> roll) {
        int total = 0;
        for (Integer integer : roll) {
            total += integer;
        }
        return total > 10 ? Selection.BIG : Selection.SMALL;
    }
}
