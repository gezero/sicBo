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
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

public class SicBoTest {

    @InjectMocks
    SicBo sicBo;

    @Mock
    Dealer dealer;
    @Spy
    ResultDisplay consoleResultDisplay = new ConsoleResultDisplay();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = TableClosedException.class)
    public void testOpenNotCalled() throws Exception {
        sicBo.acceptBet(Selection.BIG, 10);
    }

    @Test
    public void testOpen() throws Exception {
        sicBo.open();
        sicBo.acceptBet(Selection.BIG, 10);
    }

    @Test(expected = TableClosedException.class)
    public void testClose() throws Exception {

        try {
            sicBo.open();
            sicBo.acceptBet(Selection.BIG, 10);
            sicBo.close();
        } catch (TableClosedException e) {
            throw new RuntimeException("There should not have been any exceptions, but there was one",e);
        }
        sicBo.acceptBet(Selection.BIG, 10);
        Thread.sleep(5000);
        sicBo.acceptBet(Selection.BIG, 10);
    }

    @Test
    public void testAcceptBetLose() throws Exception {
        sicBo.open();
        BetFuture betFuture = sicBo.acceptBet(Selection.BIG, 10);
        sicBo.newRoll(0);
        assertThat(betFuture.getPrize(),is(0));
    }
    @Test
    public void testAcceptBetWin() throws Exception {
        sicBo.open();
        BetFuture betFuture = sicBo.acceptBet(Selection.BIG, 10);

        assertThat(betFuture.getPrize(),is(20));
    }

    public class DelayedAnswer<T> implements Answer<T>{

        private T answer;
        private long delay;

        public DelayedAnswer(T answer, long delay) {
            this.answer = answer;
            this.delay = delay;
        }

        @Override
        public T answer(InvocationOnMock invocationOnMock) throws Throwable {
            Thread.sleep(delay);
            return answer;
        }
    }
}