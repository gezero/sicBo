package com.ingg.exercise.sicbo.solution;

import com.ingg.exercise.sicbo.model.BetFuture;
import com.ingg.exercise.sicbo.model.Selection;
import com.ingg.exercise.sicbo.model.exception.TableClosedException;
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

        betAcceptor.finishRound(new ImmutableRoundResult(Arrays.asList(1, 2, 3)));

        assertThat(betFuture.getPrize(), is(20));

    }

    @Test
    public void testAcceptBetSmallLose() throws Exception {
        SimpleBetAcceptor betAcceptor = new SimpleBetAcceptor("round");

        BetFuture betFuture = betAcceptor.acceptBet(Selection.SMALL, 10);
        assertThat(betFuture.getRoundId(), is("round"));

        betAcceptor.finishRound(new ImmutableRoundResult(Arrays.asList(2, 2, 2)));

        assertThat(betFuture.getPrize(), is(0));

    }

    @Test
    public void testAcceptBetBigWin() throws Exception {
        SimpleBetAcceptor betAcceptor = new SimpleBetAcceptor("round");

        BetFuture betFuture = betAcceptor.acceptBet(Selection.BIG, 10);
        assertThat(betFuture.getRoundId(), is("round"));

        betAcceptor.finishRound(new ImmutableRoundResult(Arrays.asList(4, 5, 6)));

        assertThat(betFuture.getPrize(), is(20));

    }

    @Test
    public void testAcceptBetBigLose() throws Exception {
        SimpleBetAcceptor betAcceptor = new SimpleBetAcceptor("round");

        BetFuture betFuture = betAcceptor.acceptBet(Selection.BIG, 10);
        assertThat(betFuture.getRoundId(), is("round"));

        betAcceptor.finishRound(new ImmutableRoundResult(Arrays.asList(1, 2, 3)));

        assertThat(betFuture.getPrize(), is(0));

    }

    @Test(expected = ArithmeticException.class)
    public void testBetToBig() throws Exception {
        SimpleBetAcceptor betAcceptor = new SimpleBetAcceptor("round");
        betAcceptor.acceptBet(Selection.BIG, Integer.MAX_VALUE / 2 + 1);
    }


    @Test(expected = RuntimeException.class)
    public void testCannotAcceptTwoResults() throws TableClosedException {
        SimpleBetAcceptor betAcceptor = new SimpleBetAcceptor("round");
        betAcceptor.finishRound(new ImmutableRoundResult(Arrays.asList(1, 2, 3)));
        betAcceptor.finishRound(new ImmutableRoundResult(Arrays.asList(1, 2, 3)));

    }


    @Test(expected = TableClosedException.class)
    public void testCannotAcceptNewBetsAfterCaluclatedResult() throws TableClosedException {
        SimpleBetAcceptor betAcceptor = new SimpleBetAcceptor("round");
        betAcceptor.finishRound(new ImmutableRoundResult(Arrays.asList(1, 2, 3)));
        betAcceptor.acceptBet(Selection.BIG, Integer.MAX_VALUE / 2);
    }
}