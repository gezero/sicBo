package com.ingg.exercise.sicbo.solution;

import com.ingg.exercise.sicbo.model.BetFuture;
import com.ingg.exercise.sicbo.model.ConsoleResultDisplay;
import com.ingg.exercise.sicbo.model.ResultDisplay;
import com.ingg.exercise.sicbo.model.Selection;
import com.ingg.exercise.sicbo.model.exception.TableClosedException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SicBoTest {

    private final List<Integer> BIG_ROLL = Arrays.asList(5, 5, 5);
    @InjectMocks
    SicBo sicBo;

    @Mock
    Dealer dealer;
    @Mock
    RandomStringGenerator randomStringGenerator;
    @Mock
    BetAcceptorFactory betAcceptorFactory;
    @Mock
    BetAcceptor betAcceptor;

    @Spy
    ResultDisplay consoleResultDisplay = new ConsoleResultDisplay();
    private final List<Integer> SMALL_ROLL = Arrays.asList(1, 2, 3);

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(betAcceptorFactory.createNewAcceptor(any(String.class))).thenReturn(betAcceptor);
    }

    @Test(expected = TableClosedException.class)
    public void testOpenNotCalled() throws Exception {
        sicBo.acceptBet(Selection.BIG, 10);
    }

    @Test
    public void testOpen() throws Exception {
        when(dealer.subscribe(sicBo)).thenReturn(SMALL_ROLL);
        sicBo.open();
        sicBo.acceptBet(Selection.BIG, 10);
    }

    @Test(expected = TableClosedException.class)
    public void testClose() throws Exception {

        try {
            when(dealer.subscribe(sicBo)).thenReturn(SMALL_ROLL);
            sicBo.open();
            sicBo.acceptBet(Selection.BIG, 10);
            when(dealer.stop()).thenReturn(SMALL_ROLL);
            sicBo.close();
        } catch (TableClosedException e) {
            throw new RuntimeException("There should not have been any exceptions, but there was one", e);
        }
        verify(betAcceptor).finishRound(anyCollectionOf(Integer.class), any(String.class));
        when(betAcceptor.acceptBet(Selection.BIG, 10)).thenThrow(new TableClosedException());
        sicBo.acceptBet(Selection.BIG, 10);
    }

    @Test
    public void testAcceptBetBigLose() throws Exception {
        when(dealer.subscribe(sicBo)).thenReturn(SMALL_ROLL);
        sicBo.open();
        BetFuture betFuture = sicBo.acceptBet(Selection.BIG, 10);
        assertThat(betFuture, is(notNullValue()));
        assertThat(betFuture.getRoundId(), is(notNullValue()));
        sicBo.newRoll(SMALL_ROLL);
        assertThat(betFuture.getPrize(), is(0));
    }

    @Test
    public void testAcceptBetBigWin() throws Exception {
        when(dealer.subscribe(sicBo)).thenReturn(BIG_ROLL);
        sicBo.open();
        BetFuture betFuture = sicBo.acceptBet(Selection.BIG, 10);
        assertThat(betFuture, is(notNullValue()));
        assertThat(betFuture.getRoundId(), is(notNullValue()));

        sicBo.newRoll(SMALL_ROLL);
        assertThat(betFuture.getPrize(), is(20));
    }

    @Test
    public void testAcceptBetSmallLose() throws Exception {
        when(dealer.subscribe(sicBo)).thenReturn(BIG_ROLL);
        sicBo.open();
        BetFuture betFuture = sicBo.acceptBet(Selection.SMALL, 10);
        assertThat(betFuture, is(notNullValue()));
        assertThat(betFuture.getRoundId(), is(notNullValue()));
        sicBo.newRoll(SMALL_ROLL);
        assertThat(betFuture.getPrize(), is(0));
    }

    @Test
    public void testAcceptBetSmallWin() throws Exception {
        when(dealer.subscribe(sicBo)).thenReturn(SMALL_ROLL);
        sicBo.open();
        BetFuture betFuture = sicBo.acceptBet(Selection.SMALL, 10);
        assertThat(betFuture, is(notNullValue()));
        assertThat(betFuture.getRoundId(), is(notNullValue()));

        sicBo.newRoll(SMALL_ROLL);
        assertThat(betFuture.getPrize(), is(20));
    }
}