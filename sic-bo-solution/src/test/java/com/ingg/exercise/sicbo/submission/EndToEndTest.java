package com.ingg.exercise.sicbo.submission;

import com.ingg.exercise.sicbo.model.BetFuture;
import com.ingg.exercise.sicbo.model.ResultDisplay;
import com.ingg.exercise.sicbo.model.Selection;
import com.ingg.exercise.sicbo.model.Table;
import com.ingg.exercise.sicbo.model.exception.TableClosedException;
import com.ingg.exercise.sicbo.solution.SicBo;
import org.junit.Test;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Jiri
 */
public class EndToEndTest {


    @Test
    public void endToEndTest() throws InterruptedException {
        ResultGatherer resultGatherer = new ResultGatherer();
        Table table = new SicBo(resultGatherer);

        List<Player> playerList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            playerList.add(new Player(i, table));
        }

        Executor executor = Executors.newFixedThreadPool(playerList.size());
        for (Player player : playerList) {
            executor.execute(player);
        }

        Thread.sleep(1000);
        System.out.println("Opening table");
        table.open();

        Thread.sleep(50_000);
//        Thread.sleep(7_000);

        table.close();
        System.out.println("Table closed");
        Thread.sleep(1000);

        int totalBets = 0;
        for (Player player : playerList) {
            totalBets += player.checkBets(resultGatherer);
        }

        System.out.println("There was "+ totalBets +" placed bets by "+ playerList.size()+ " players..");


    }


    private class ResultGatherer implements ResultDisplay {
        Map<String, Selection> map = new HashMap<>();

        @Override
        public void displayResult(String roundId, Iterable<Integer> result) {
            checkNotNull(roundId);
            checkNotNull(result);
            if (map.get(roundId) != null) {
                throw new RuntimeException("There was already something in map!");
            }
            int total = 0;
            for (Integer integer : result) {
                total += integer;
            }
            Selection selection = total > 10 ? Selection.BIG : Selection.SMALL;
            System.out.println("Round " + roundId + " finished with total of " + total + " meaning selection is " + selection);
            map.put(roundId, selection);
        }

        public boolean check(Selection selection, int stake, BetFuture betFuture) throws InterruptedException {
            Integer expectedPrice = selection.equals(map.get(betFuture.getRoundId())) ? stake * 2 : 0;
            boolean check = expectedPrice.equals(betFuture.getPrize());
            if (!check) {
                System.out.println("Bet for round " + betFuture.getRoundId() + "should have been different...");
                System.out.println("Our selection: " + selection + " Round was: " + map.get(betFuture.getRoundId()));
                System.out.println("We got: "+ betFuture.getPrize() +" Should get: "+ expectedPrice);
            }

            return check;
        }
    }

    private class Player implements Runnable {
        private int id;
        private Table table;
        SecureRandom random = new SecureRandom();
        private boolean betted = false;

        List<MyBet> bets = new LinkedList<>();

        public Player(int id, Table table) {
            this.id = id;
            this.table = table;
        }

        @Override
        public void run() {
            while (true) {
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
                }
                try {
                    Thread.sleep(random.nextInt(10) * 50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        public int checkBets(ResultGatherer resultGatherer) throws InterruptedException {
            int correct = 0;
            for (MyBet bet : bets) {
                correct += resultGatherer.check(bet.getSelection(), bet.getStake(), bet.getBetFuture()) ? 1 : 0;
            }
            assertThat(correct,is(bets.size()));
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
}
