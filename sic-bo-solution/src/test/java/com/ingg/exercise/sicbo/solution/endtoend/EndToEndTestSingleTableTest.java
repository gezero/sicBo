package com.ingg.exercise.sicbo.solution.endtoend;

import com.ingg.exercise.sicbo.model.Table;
import com.ingg.exercise.sicbo.solution.ProvablyFairSicBo;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Jiri
 */
public class EndToEndTestSingleTableTest {

    @Test
    public void testEndToEndTest() throws InterruptedException {
        ResultGatherer resultGatherer = new ResultGatherer();
        Table table = new ProvablyFairSicBo(resultGatherer);

        List<Player> playerList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            playerList.add(new Player(i, table, 100));
        }

        ExecutorService executor = Executors.newFixedThreadPool(playerList.size());
        for (Player player : playerList) {
            executor.execute(player);
        }
        executor.shutdown();


        Thread.sleep(1000);
        System.out.println("Opening table for 60 seconds");
        table.open();

        Thread.sleep(60_000);

        table.close();
        System.out.println("Table closed");
        Thread.sleep(1000);

        int totalBets = 0;
        for (Player player : playerList) {
            totalBets += player.checkBets(resultGatherer);
        }

        System.out.println("There was " + totalBets + " placed bets by " + playerList.size() + " players..");
    }

}
