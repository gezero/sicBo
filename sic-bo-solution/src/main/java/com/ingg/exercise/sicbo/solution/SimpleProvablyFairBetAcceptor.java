package com.ingg.exercise.sicbo.solution;

import com.ingg.exercise.sicbo.model.BetFuture;
import com.ingg.exercise.sicbo.model.Selection;
import com.ingg.exercise.sicbo.model.exception.TableClosedException;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This class is responsible for handling bets. It creates new BetFuture objects and calculate prices after the roll.
 *
 * @author Jiri
 */
public class SimpleProvablyFairBetAcceptor implements ProvablyFairBetAcceptor {
    boolean active = true;
    List<ProvablyFairBet> provablyFairBets = new LinkedList<>();
    private String roundId;

    public SimpleProvablyFairBetAcceptor(String roundId) {
        this.roundId = roundId;
    }

    /**
     * Creates new BetFuture object that will contain price when the roll happens. The method for getting price will
     * block until the roll happens.
     *
     * @param selection Player has to pick one of the selection options
     * @param stake     Amount of money that player can bet
     * @return BetFuture object that will block on reading the price attribute unless the round has already finished.
     * @throws TableClosedException
     */
    @Override
    public synchronized BetFuture acceptBet(Selection selection, Integer stake) throws TableClosedException {
        if (!active) {
            throw new TableClosedException();
        }
        checkNotNull(selection);
        checkNotNull(stake);
        if (stake > Integer.MAX_VALUE / 2) {
            throw new ArithmeticException("Bet is to high, If you would win, we would not be able to calculate how much. " +
                    "Try to split the bet into few smaller ones...");
        }
        ProvablyFairBet provablyFairBet = new ProvablyFairBet(roundId, selection, stake);
        provablyFairBets.add(provablyFairBet);
        return provablyFairBet;
    }

    /**
     * Handles calculating of prices. It will be done in separate thread so that current thread does not need to wait
     * for calculation to finish.
     *
     * @param result result of a dealers roll.
     */
    @Override
    public synchronized void finishRound(ProvablyFairResult result) {
        checkNotNull(result);
        if (!active) {
            throw new RuntimeException("Price can be calculated only once");
        }
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new CalculatePrices(provablyFairBets, result));
        executor.shutdown();
        active = false;
    }

    /**
     * The waiting for the price is done using the CountdownLatch. When the bet is created we do not know the price yet
     * The price is calculated once the round finishes.
     */
    private class ProvablyFairBet implements ProvablyFairBetFuture {

        private final String roundId;
        private final Selection selection;
        private final Integer stake;
        private Integer price = null;
        private final CountDownLatch latch = new CountDownLatch(1);
        private String salt;
        private Iterable<Integer> roll;

        private ProvablyFairBet(String roundId, Selection selection, Integer stake) {
            this.roundId = roundId;
            this.selection = selection;
            this.stake = stake;
        }

        @Override
        public String getRoundId() {
            return roundId;
        }

        /**
         * If the price is not known yet, the method will block.
         *
         * @return price that the player receives depending on the bet and stake that he made and roll that was done
         * @throws InterruptedException
         */
        @Override
        public Integer getPrize() throws InterruptedException {
            latch.await();
            return price;
        }

        public String getSalt() throws InterruptedException {
            latch.await();
            return salt;
        }

        public Iterable<Integer> getRoll() throws InterruptedException {
            latch.await();
            return roll;
        }

        public void setRoll(Iterable<Integer> roll) {
            this.roll = roll;
        }

        public void calculatePrice(ProvablyFairResult selectionResult) {
            price = selectionResult.calculatePrice(selection,stake);
            salt = selectionResult.getSalt();
            roll = selectionResult.getRoll();
            latch.countDown();
        }
    }

    /**
     * This Runnable is responsible for calculating prices. We want to calculate prices in a separate thread so that we
     * do not block the game thread when calculating it
     */
    private class CalculatePrices implements Runnable {
        private List<ProvablyFairBet> provablyFairBets;
        private ProvablyFairResult result;

        public CalculatePrices(List<ProvablyFairBet> provablyFairBets, ProvablyFairResult result) {
            this.provablyFairBets = provablyFairBets;
            this.result = result;
        }

        @Override
        public void run() {
            for (ProvablyFairBet provablyFairBet : provablyFairBets) {
                provablyFairBet.calculatePrice(result);
            }
        }
    }
}
