package com.ingg.exercise.sicbo.solution;

import com.ingg.exercise.sicbo.model.BetFuture;
import com.ingg.exercise.sicbo.model.Selection;
import com.ingg.exercise.sicbo.model.exception.TableClosedException;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This class is responsible for handling bets. It creates new BetFuture objects and calculate prices after the roll.
 *
 * @author Jiri
 */
public class SimpleBetAcceptor implements BetAcceptor {
    boolean active = true;
    List<Bet> bets = new LinkedList<>();
    private String roundId;

    public SimpleBetAcceptor(String roundId) {
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
        Bet bet = new Bet(roundId, selection, stake);
        bets.add(bet);
        return bet;
    }

    /**
     * Handles calculating of prices. It will be done in separate thread so that current thread does not need to wait
     * for calculation to finish.
     *
     * @param result result of a dealers roll.
     */
    @Override
    public synchronized void finishRound(RoundResult result) {
        checkNotNull(result);
        if (!active) {
            throw new RuntimeException("Price can be calculated only once");
        }
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(new CalculatePrices(bets, result));
        active = false;
    }

    /**
     * The waiting for the price is done using the CountdownLatch. When the bet is created we do not know the price yet
     * The price is calculated once the round finishes.
     */
    private class Bet implements BetFuture {

        private final String roundId;
        private final Selection selection;
        private final Integer stake;
        private Integer price = null;
        private final CountDownLatch latch = new CountDownLatch(1);

        private Bet(String roundId, Selection selection, Integer stake) {
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

        public void calculatePrice(Selection selectionResult) {
            //This if would be only 1 line If there would be Triple selection in the Sellection Enum
            if (selectionResult == null) {
                price = 0;
            } else {
                price = selectionResult.equals(selection) ? stake * 2 : 0;
            }
            latch.countDown();
        }
    }

    /**
     * This Runnable is responsible for calculating prices. We want to calculate prices in a separate thread so that we
     * do not block the game thread when calculating it
     */
    private class CalculatePrices implements Runnable {
        private List<Bet> bets;
        private RoundResult result;

        public CalculatePrices(List<Bet> bets, RoundResult result) {
            this.bets = bets;
            this.result = result;
        }

        @Override
        public void run() {
            Selection resultSelection = SicBo.calculateSelection(result.getRoll());
            //This if would be not here if I would be allowed to change the Selection enum.
            if (SicBo.isTriple(result.getRoll())) {
                resultSelection = null;
            }
            for (Bet bet : bets) {
                bet.calculatePrice(resultSelection);
            }
        }
    }
}
