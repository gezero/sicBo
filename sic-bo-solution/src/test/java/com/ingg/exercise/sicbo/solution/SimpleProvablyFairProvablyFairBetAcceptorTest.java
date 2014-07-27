package com.ingg.exercise.sicbo.solution;

import com.ingg.exercise.sicbo.model.BetFuture;
import com.ingg.exercise.sicbo.model.Selection;
import com.ingg.exercise.sicbo.model.exception.TableClosedException;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SimpleProvablyFairProvablyFairBetAcceptorTest {

    @Test
    public void testAcceptBetSmallWin() throws Exception {
        SimpleProvablyFairBetAcceptor betAcceptor = new SimpleProvablyFairBetAcceptor("round");

        BetFuture betFuture = betAcceptor.acceptBet(Selection.SMALL, 10);
        assertThat(betFuture.getRoundId(), is("round"));

        betAcceptor.finishRound(new ImmutableProvablyFairResult(Arrays.asList(1, 2, 3), null));

        assertThat(betFuture.getPrize(), is(20));

    }

    @Test
    public void testAcceptBetSmallLose() throws Exception {
        SimpleProvablyFairBetAcceptor betAcceptor = new SimpleProvablyFairBetAcceptor("round");

        BetFuture betFuture = betAcceptor.acceptBet(Selection.SMALL, 10);
        assertThat(betFuture.getRoundId(), is("round"));

        betAcceptor.finishRound(new ImmutableProvablyFairResult(Arrays.asList(2, 2, 2), null));

        assertThat(betFuture.getPrize(), is(0));

    }

    @Test
    public void testAcceptBetBigWin() throws Exception {
        SimpleProvablyFairBetAcceptor betAcceptor = new SimpleProvablyFairBetAcceptor("round");

        BetFuture betFuture = betAcceptor.acceptBet(Selection.BIG, 10);
        assertThat(betFuture.getRoundId(), is("round"));

        betAcceptor.finishRound(new ImmutableProvablyFairResult(Arrays.asList(4, 5, 6), null));

        assertThat(betFuture.getPrize(), is(20));

    }

    @Test
    public void testAcceptBetBigLose() throws Exception {
        SimpleProvablyFairBetAcceptor betAcceptor = new SimpleProvablyFairBetAcceptor("round");

        BetFuture betFuture = betAcceptor.acceptBet(Selection.BIG, 10);
        assertThat(betFuture.getRoundId(), is("round"));

        betAcceptor.finishRound(new ImmutableProvablyFairResult(Arrays.asList(1, 2, 3), null));

        assertThat(betFuture.getPrize(), is(0));

    }

    @Test(expected = ArithmeticException.class)
    public void testBetToBig() throws Exception {
        SimpleProvablyFairBetAcceptor betAcceptor = new SimpleProvablyFairBetAcceptor("round");
        betAcceptor.acceptBet(Selection.BIG, Integer.MAX_VALUE / 2 + 1);
    }


    @Test(expected = RuntimeException.class)
    public void testCannotAcceptTwoResults() throws TableClosedException {
        SimpleProvablyFairBetAcceptor betAcceptor = new SimpleProvablyFairBetAcceptor("round");
        betAcceptor.finishRound(new ImmutableProvablyFairResult(Arrays.asList(1, 2, 3), null));
        betAcceptor.finishRound(new ImmutableProvablyFairResult(Arrays.asList(1, 2, 3), null));

    }


    @Test(expected = TableClosedException.class)
    public void testCannotAcceptNewBetsAfterCaluclatedResult() throws TableClosedException {
        SimpleProvablyFairBetAcceptor betAcceptor = new SimpleProvablyFairBetAcceptor("round");
        betAcceptor.finishRound(new ImmutableProvablyFairResult(Arrays.asList(1, 2, 3), null));
        betAcceptor.acceptBet(Selection.BIG, Integer.MAX_VALUE / 2);
    }
}