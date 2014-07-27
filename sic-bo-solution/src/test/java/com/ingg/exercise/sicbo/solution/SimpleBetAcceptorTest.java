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

        betAcceptor.finishRound(new RoundResult() {
            @Override
            public Iterable<Integer> getRoll() {
                return Arrays.asList(1, 2, 3);
            }
        });

        assertThat(betFuture.getPrize(), is(20));

    }

    @Test
    public void testAcceptBetSmallLose() throws Exception {
        SimpleBetAcceptor betAcceptor = new SimpleBetAcceptor("round");

        BetFuture betFuture = betAcceptor.acceptBet(Selection.SMALL, 10);
        assertThat(betFuture.getRoundId(), is("round"));

        betAcceptor.finishRound(new RoundResult() {
            @Override
            public Iterable<Integer> getRoll() {
                return Arrays.asList(4, 5, 6);
            }
        });

        assertThat(betFuture.getPrize(), is(0));

    }

    @Test
    public void testAcceptBetBigWin() throws Exception {
        SimpleBetAcceptor betAcceptor = new SimpleBetAcceptor("round");

        BetFuture betFuture = betAcceptor.acceptBet(Selection.BIG, 10);
        assertThat(betFuture.getRoundId(), is("round"));

        betAcceptor.finishRound(new RoundResult() {
            @Override
            public Iterable<Integer> getRoll() {
                return Arrays.asList(3, 5, 6);
            }
        });

        assertThat(betFuture.getPrize(), is(20));

    }

    @Test
    public void testAcceptBetBigLose() throws Exception {
        SimpleBetAcceptor betAcceptor = new SimpleBetAcceptor("round");

        BetFuture betFuture = betAcceptor.acceptBet(Selection.BIG, 10);
        assertThat(betFuture.getRoundId(), is("round"));

        betAcceptor.finishRound(new RoundResult() {
            @Override
            public Iterable<Integer> getRoll() {
                return Arrays.asList(1, 2, 3);
            }
        });

        assertThat(betFuture.getPrize(), is(0));

    }

    @Test
    public void fourBetsOnRound() throws Exception {
        SimpleBetAcceptor betAcceptor = new SimpleBetAcceptor("round");

        BetFuture betFuture1 = betAcceptor.acceptBet(Selection.BIG, 10);
        BetFuture betFuture2 = betAcceptor.acceptBet(Selection.SMALL, 30);
        BetFuture betFuture3 = betAcceptor.acceptBet(Selection.BIG, 20);
        BetFuture betFuture4 = betAcceptor.acceptBet(Selection.SMALL, 50);

        assertThat(betFuture1.getRoundId(), is("round"));
        assertThat(betFuture2.getRoundId(), is("round"));
        assertThat(betFuture3.getRoundId(), is("round"));
        assertThat(betFuture4.getRoundId(), is("round"));

        betAcceptor.finishRound(new RoundResult() {
            @Override
            public Iterable<Integer> getRoll() {
                return Arrays.asList(1, 2, 3);
            }
        });

        assertThat(betFuture1.getPrize(), is(0));
        assertThat(betFuture2.getPrize(), is(60));
        assertThat(betFuture3.getPrize(), is(0));
        assertThat(betFuture4.getPrize(), is(100));


    }

    @Test(expected = ArithmeticException.class)
    public void testBetToBig() throws Exception {
        SimpleBetAcceptor betAcceptor = new SimpleBetAcceptor("round");
        betAcceptor.acceptBet(Selection.BIG, Integer.MAX_VALUE / 2 + 1);
    }

    @Test
    public void testMaxBet() throws Exception {
        SimpleBetAcceptor betAcceptor = new SimpleBetAcceptor("round");
        BetFuture betFuture1 = betAcceptor.acceptBet(Selection.BIG, Integer.MAX_VALUE / 2);
        BetFuture betFuture2 = betAcceptor.acceptBet(Selection.SMALL, Integer.MAX_VALUE / 2);
        betAcceptor.finishRound(new RoundResult() {
            @Override
            public Iterable<Integer> getRoll() {
                return Arrays.asList(1, 2, 3);
            }
        });
        assertThat(betFuture1.getPrize(), is(0));
        assertThat(betFuture2.getPrize(), is(Integer.MAX_VALUE - 1));
    }

    @Test(expected = RuntimeException.class)
    public void cannotAcceptTwoResults() throws TableClosedException {
        SimpleBetAcceptor betAcceptor = new SimpleBetAcceptor("round");
        betAcceptor.finishRound(new RoundResult() {
            @Override
            public Iterable<Integer> getRoll() {
                return Arrays.asList(1, 2, 3);
            }
        });
        betAcceptor.finishRound(new RoundResult() {
            @Override
            public Iterable<Integer> getRoll() {
                return Arrays.asList(1, 2, 3);
            }
        });

    }


    @Test(expected = TableClosedException.class)
    public void cannotAcceptNewBetsAfterCaluclatedResult() throws TableClosedException {
        SimpleBetAcceptor betAcceptor = new SimpleBetAcceptor("round");
        betAcceptor.finishRound(new RoundResult() {
            @Override
            public Iterable<Integer> getRoll() {
                return Arrays.asList(1, 2, 3);
            }
        });
        betAcceptor.acceptBet(Selection.BIG, Integer.MAX_VALUE / 2);
    }
}