package de.lrosenberg.kata.babystep_timer.domain;

import de.lrosenberg.kata.babystep_timer.domain.BabyStepTimer.TimerExpiredListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static de.lrosenberg.kata.babystep_timer.domain.BabyStepTimer.TWO_MINUTES;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

public class BabyStepTimerTest {

    private BabyStepTimer timer;
    private TimerExpiredListener expiredListener;

    @BeforeEach
    void setUp() {
        expiredListener = mock(TimerExpiredListener.class);
        timer = new BabyStepTimer(expiredListener);
    }

    @Test
    void testTimerIsNotRunning() {
        assertThat(timer.isRunning(), is(false));
    }

    @Test
    void testTimeLeftIs2MinutesAfterInitialization() {
        assertThat(timer.getTimeLeft(), is(TWO_MINUTES));
    }

    @Test
    void testStepCallRemovesOneSecondFromTimer() {
        timer.step();
        assertThat(timer.getTimeLeft(), is(TWO_MINUTES - 1));
    }

    @Test
    void testExpirationListenerWillBeCalledWhenTimeLeftIsZero() {
        stepSeconds(timer, TWO_MINUTES + 1);
        verify(expiredListener).timerExpired(eq(timer));
    }

    @Test
    void testRunningWillBeSetToFalseWhenAutoRestartIsFalseAndTimeLeftIsZero() {
        timer.setRunning(true);
        stepSeconds(timer, TWO_MINUTES + 1);
        assertThat(timer.isRunning(), is(false));
    }

    @Test
    void testTimerRestartResetTimeLeftToTwoMinutes() {
        stepSeconds(timer, 2);
        assertThat(timer.getTimeLeft(), is(TWO_MINUTES - 2));
        timer.restart();
        assertThat(timer.getTimeLeft(), is(TWO_MINUTES));
    }

    @Test
    void testTimerRestartsAutomaticallyWhenAutoRestartIsTrue() {
        BabyStepTimer spyTimer = spy(new BabyStepTimer(expiredListener));
        spyTimer.setAutoRestart(true);
        stepSeconds(spyTimer, TWO_MINUTES);
        spyTimer.step();
        verify(spyTimer).restart();
    }

    @Test
    void testTimerIsRunnable() {
        assertThat(timer instanceof Runnable, is(true));
    }

    private void stepSeconds(BabyStepTimer timer, int seconds) {
        for (int i = 0; i < seconds; i++) {
            timer.step();
        }
    }
}
