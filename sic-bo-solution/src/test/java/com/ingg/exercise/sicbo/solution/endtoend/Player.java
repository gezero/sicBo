package com.ingg.exercise.sicbo.solution.endtoend;

import com.ingg.exercise.sicbo.model.BetFuture;
import com.ingg.exercise.sicbo.model.Selection;
import com.ingg.exercise.sicbo.model.Table;
import com.ingg.exercise.sicbo.model.exception.TableClosedException;

import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class Player implements Runnable {
    private final int minimumWaitingTimout;
    private int id;
    private Table table;
    SecureRandom random = new SecureRandom();
    private boolean betted = false;

    List<MyBet> bets = new LinkedList<>();

    public Player(int id, Table table, int mitBettingPeriod) {
        this.id = id;
        this.table = table;
        minimumWaitingTimout = mitBettingPeriod;
    }

    @Override
    public void run() {
        int counter = 0;
        while (counter<5) {
            Selection selection = random.nextInt(2) == 0 ? Selection.BIG : Selection.SMALL;
            int stake = random.nextInt(500);
            try {
                BetFuture betFuture = table.acceptBet(selection, stake);
                betted = true;
                bets.add(new MyBet(selection, stake, betFuture));
            } catch (TableClosedException e) {
                if (betted) {
                    return;
                }
                else{
                    counter++;
                }
            }
            try {
                Thread.sleep((random.nextInt(5)*50) + minimumWaitingTimout);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
//        System.out.println("Player did not have chance to bet...");
    }

    public int checkBets(ResultGatherer resultGatherer) throws InterruptedException {
        int correct = 0;
        for (MyBet bet : bets) {
            correct += resultGatherer.check(bet.getSelection(), bet.getStake(), bet.getBetFuture()) ? 1 : 0;
        }
        assertThat(correct, is(bets.size()));
        return correct;
    }

    private class MyBet {
        private Selection selection;
        private int stake;
        private BetFuture betFuture;

        public MyBet(Selection selection, int stake, BetFuture betFuture) {
            this.selection = selection;
            this.stake = stake;
            this.betFuture = betFuture;
        }


        public Selection getSelection() {
            return selection;
        }

        public int getStake() {
            return stake;
        }

        public BetFuture getBetFuture() {
            return betFuture;
        }
    }
}