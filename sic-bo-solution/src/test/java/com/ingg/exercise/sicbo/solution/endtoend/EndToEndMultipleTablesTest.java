package com.ingg.exercise.sicbo.solution.endtoend;

import com.ingg.exercise.sicbo.model.Table;
import com.ingg.exercise.sicbo.solution.SicBo;
import org.junit.Test;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Jiri
 */
public class EndToEndMultipleTablesTest {

    @Test
    public void testEndToEnd() throws InterruptedException {
        int tables = 30;
        CountDownLatch latch = new CountDownLatch(tables+1);
        SecureRandom random = new SecureRandom();
        ExecutorService executor = Executors.newFixedThreadPool(10);
        for (int i = 0; i < tables; i++) {
            executor.execute(new RunTableTest(i, latch, random.nextInt(12) * 2000 + 1000));
        }
        executor.shutdown();
        latch.await();
    }

    private class RunTableTest implements Runnable {
        private int id;
        private CountDownLatch latch;
        private long openedTime;

        public RunTableTest(int id, CountDownLatch latch, long openedTime) {
            this.id = id;
            this.latch = latch;
            this.openedTime = openedTime;
        }

        @Override
        public void run() {
            try {
                runTest();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        private void runTest() throws InterruptedException {
            ResultGatherer resultGatherer = new ResultGatherer();
            Table table = new SicBo(resultGatherer);

            List<Player> playerList = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                playerList.add(new Player(i, table, 1000));
            }

            ExecutorService playersService = Executors.newFixedThreadPool(10);
            for (Player player : playerList) {
                playersService.execute(player);
            }
            playersService.shutdown();

            Thread.sleep(1000);
            System.out.println("Opening table " + id + " for " + openedTime / 1000 + " seconds");
            table.open();

            Thread.sleep(openedTime);

            table.close();
            System.out.println("Table " + id + " closed");
            Thread.sleep(1000);

            int totalBets = 0;
            for (Player player : playerList) {
                totalBets += player.checkBets(resultGatherer);
            }

            System.out.println("There was " + totalBets + " placed bets by " + playerList.size() + " players on table " + id);
            latch.countDown();
            playersService.shutdownNow();
        }
    }
}
