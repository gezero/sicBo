package com.ingg.exercise.sicbo.solution;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

public class NormalDealerTest {


    private static final byte[] SEED = {1, 2, 3, 4, 5};
    private static final long EPS = 100_000_000L; // this many nanoseconds should be 100 milliseconds

    @Test
    public void testFlow() throws Exception {
        int timeout = 300;
        NormalDealer dealer = new NormalDealer(new SessionRandomGenerator(SEED), timeout);

        int rounds = 5;
        CountDownLatch latch = new CountDownLatch(rounds);

        RollCollector rollCollector = new RollCollector(latch);
        Iterable<Integer> firstRoll = dealer.subscribe(rollCollector);
        assertThat(firstRoll, is(notNullValue()));
        int count = 0;
        for (Integer ignored : firstRoll) {
            count++;
        }
        assertThat(count, is(3));
        assertThat(rollCollector.getList().size(), is(0));

        long timeStart = System.nanoTime(); //to find out why not to use System.currentTimeMillis() check this StackOverflow answer: http://stackoverflow.com/a/1776053/550859
        latch.await();
        long timeStop = System.nanoTime();

        dealer.stop();

        assertThat(timeStop - timeStart, is(lessThan(timeout * rounds * 1000 * 1000 + EPS)));
        assertThat(rollCollector.getList().size(), is(equalTo(rounds)));

        Thread.sleep(500);
        assertThat(rollCollector.getList().size(), is(rounds));

    }

    private class RollCollector implements DealerObserver {
        public List<Iterable<Integer>> list = new ArrayList<>();
        private CountDownLatch latch;

        public RollCollector(CountDownLatch latch) {

            this.latch = latch;
        }

        @Override
        public void newRoll(Iterable<Integer> roll) {
            list.add(roll);
            latch.countDown();
        }

        public List<Iterable<Integer>> getList() {
            return list;
        }
    }

    @Test(expected = RuntimeException.class)
    public void testCannotSubscribeTwice() {
        NormalDealer dealer = new NormalDealer(new SessionRandomGenerator(SEED), 100);
        RollCollector rollCollector = new RollCollector(null);
        dealer.subscribe(rollCollector);
        dealer.subscribe(rollCollector);

    }
}