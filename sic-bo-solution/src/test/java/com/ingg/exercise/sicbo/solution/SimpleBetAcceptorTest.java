package com.ingg.exercise.sicbo.solution;

import com.ingg.exercise.sicbo.model.BetFuture;
import com.ingg.exercise.sicbo.model.Selection;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SimpleBetAcceptorTest {

    @Test
    public void testAcceptBetSmallWin() throws Exception {
        SimpleBetAcceptor betAcceptor = new SimpleBetAcceptor("round");

        BetFuture betFuture = betAcceptor.acceptBet(Selection.SMALL, 10);
        assertThat(betFuture.getRoundId(), is("round"));

        betAcceptor.finishRound(new RoundResult() {
            @Override
            public Iterable<Integer> getRoll() {
                return Arrays.asList(1,2,3);
            }
        });

        assertThat(betFuture.getPrize(), is(20));

    }
}