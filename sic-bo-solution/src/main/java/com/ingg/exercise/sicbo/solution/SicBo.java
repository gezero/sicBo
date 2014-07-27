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
        startNewRound();
        dealer.subscribe(this);
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
        betAcceptor.finishRound(new ImmutableRoundResult(lastRoll));
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
        acceptor.finishRound(new ImmutableRoundResult(roll));
        resultDisplay.displayResult(roundId, roll);
    }

}
