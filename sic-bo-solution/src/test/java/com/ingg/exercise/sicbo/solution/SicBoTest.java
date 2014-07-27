package com.ingg.exercise.sicbo.solution;

import com.ingg.exercise.sicbo.model.BetFuture;
import com.ingg.exercise.sicbo.model.ConsoleResultDisplay;
import com.ingg.exercise.sicbo.model.ResultDisplay;
import com.ingg.exercise.sicbo.model.Selection;
import com.ingg.exercise.sicbo.model.exception.TableClosedException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Mockito.*;

public class SicBoTest {

    private final List<Integer> SMALL_ROLL = Arrays.asList(1, 2, 3);
    private final List<Integer> BIG_ROLL = Arrays.asList(4, 5, 5);
    private final List<Integer> SMALL_TRIPLE_ROLL = Arrays.asList(2, 2, 2);
    private final List<Integer> BIG_TRIPLE_ROLL = Arrays.asList(5, 5, 5);

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
    @Captor
    private ArgumentCaptor<RoundResult> captor;

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
        verify(betAcceptor).finishRound(any(RoundResult.class));
        when(betAcceptor.acceptBet(Selection.BIG, 10)).thenThrow(new TableClosedException());
        sicBo.acceptBet(Selection.BIG, 10);
    }

    @Test
    public void testAcceptBet() throws Exception {
        when(dealer.subscribe(sicBo)).thenReturn(SMALL_ROLL);
        sicBo.open();

        BetFuture bet = mock(BetFuture.class);
        when(betAcceptor.acceptBet(Selection.SMALL, 10)).thenReturn(bet);
        BetFuture betFuture = sicBo.acceptBet(Selection.SMALL, 10);
        assertThat(betFuture, is(bet));
    }

    @Test
    public void testResultDisplay() {
        when(dealer.subscribe(sicBo)).thenReturn(SMALL_ROLL);
        sicBo.open();
        sicBo.newRoll(SMALL_ROLL);
        verify(consoleResultDisplay).displayResult(any(String.class), anyCollectionOf(Integer.class));
    }

    @Test
    public void testNewRoll() {
        when(dealer.subscribe(sicBo)).thenReturn(BIG_ROLL);
        sicBo.open();
        sicBo.newRoll(SMALL_ROLL);
        verify(betAcceptor).finishRound(captor.capture());

        RoundResult value = captor.getValue();
        assertThat(value.toString(), is(new ImmutableRoundResult(SMALL_ROLL).toString()));
    }

    @Test(expected = RuntimeException.class)
    public void testCannotCloseClosedTable() {
        sicBo.close();
    }

    @Test(expected = RuntimeException.class)
    public void testCannotOpenOpenedTable() {
        sicBo.open();
        sicBo.open();
    }

    @Test(expected = RuntimeException.class)
    public void testHasToBeOpenToAcceptRoll() {
        sicBo.newRoll(SMALL_ROLL);
    }

}