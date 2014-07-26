package com.ingg.exercise.sicbo.solution;

import com.ingg.exercise.sicbo.model.BetFuture;
import com.ingg.exercise.sicbo.model.ResultDisplay;
import com.ingg.exercise.sicbo.model.Selection;
import com.ingg.exercise.sicbo.model.Table;
import com.ingg.exercise.sicbo.model.exception.TableClosedException;
import net.jcip.annotations.ThreadSafe;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
    private Iterable<Integer> currentRoll;
    private String currentSalt;

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
        Iterable<Integer> roll = dealer.subscribe(this);
        startNewRound(roll);
    }

    private void startNewRound(Iterable<Integer> roll) {
        currentRoll = roll;
        currentSalt = randomStringGenerator.generateString();
        String currentRoundId = createCurrentRoundId(roll);
        betAcceptor = betAcceptorFactory.createNewAcceptor(currentRoundId);
    }

    private String createCurrentRoundId(Iterable<Integer> roll) {
        StringBuilder builder = new StringBuilder();
        for (Integer integer : roll) {
            builder.append(integer);
        }
        String currentRoundId;
        builder.append(currentSalt);
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            currentRoundId = new String(digest.digest(builder.toString().getBytes()));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA 256 was not found, now that is strange....");
        }
        return currentRoundId;
    }

    @Override
    public void close() {
        dealer.stop();
        betAcceptor.finishRound(currentRoll,currentSalt);
    }

    @Override
    public BetFuture acceptBet(Selection selection, Integer stake) throws TableClosedException {
        if (betAcceptor == null) {
            throw new TableClosedException();
        }
        return betAcceptor.acceptBet(selection,stake);
    }

    @Override
    public void newRoll(Iterable<Integer> roll) {

    }
}
