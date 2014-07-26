package com.ingg.exercise.sicbo.solution;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class NormalDealerTest {

    @Test
    public void testFlow() throws Exception {
        NormalDealer dealer = new NormalDealer(new SessionRandomGenerator(),100);

        RollCollector rollCollector = new RollCollector();
        Iterable<Integer> firstRoll = dealer.subscribe(rollCollector);
        assertThat(firstRoll, is(notNullValue()));
        int count = 0;
        for (Integer ignored : firstRoll) {
            count++;
        }
        assertThat(count, is(3));
        assertThat(rollCollector.getList().size(),is(0));

        Thread.sleep(1000);

        assertThat(rollCollector.getList().size(),is(10));

    }

    private class RollCollector implements DealerObserver {
        public List<Iterable<Integer>> list = new ArrayList<>();

        @Override
        public void newRoll(Iterable<Integer> roll) {
            list.add(roll);
        }

        public List<Iterable<Integer>> getList() {
            return list;
        }
    }
}