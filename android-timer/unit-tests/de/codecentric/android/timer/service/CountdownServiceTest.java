package de.codecentric.android.timer.service;

import static de.codecentric.android.timer.ServiceStateIterator.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;
import static org.mockito.internal.util.reflection.Whitebox.*;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.CountDownTimer;
import android.os.IBinder;

import com.xtremelabs.robolectric.RobolectricTestRunner;

import de.codecentric.android.timer.ActionForServiceState;
import de.codecentric.android.timer.TimerWhitebox;

@RunWith(RobolectricTestRunner.class)
public class CountdownServiceTest {

	private static final long ONE_SECOND = 1000L;
	private static final long FIVE_SECONDS = 5000L;

	@Spy
	private CountdownService countdownService;

	@Mock
	private SoundGizmo soundGizmo;

	@Mock
	private NotificationManager notificationManager;

	@Mock
	private CountDownTimer countDownTimer;

	@Before
	public void before() {
		this.countdownService = new CountdownService();
		initMocks(this);
		this.countdownService.onCreate();
		this.setNotificationManager(this.notificationManager);
		this.setSoundGizmo(this.soundGizmo);
		doNothing().when(this.countdownService)
				.startShowAlarmActivityFromService();
	}

	@Test
	public void shouldProvideBinder() {
		IBinder binder = this.countdownService.onBind(null);
		assertNotNull(binder);
		assertThat(binder, is(instanceOf(CountdownServiceBinder.class)));
	}

	@Test
	public void shouldStopCountdownOnDestroy() {
		CountDownTimer countdownTimer = mock(CountDownTimer.class);
		this.setCountdownTimer(countdownTimer);
		this.countdownService.onDestroy();
		verify(countdownTimer).cancel();
	}

	@Test
	public void shouldStartCountdown() {
		// given a service instance in state WAITING without a running
		// CountdDownTimer object
		this.setServiceState(ServiceState.WAITING);
		assertNull(this.getCountdownTimer());

		// when countdown is started
		this.countdownService.startCountdown(ONE_SECOND);

		// then state is set to COUNTING_DOWN
		assertTrue(this.countdownService.isCountingDown());

		// unfortunately CountdDownTimer object is instantiated inside
		// startCountdown, thus it's quite difficult to verify it has been
		// started. We can verify it has been created, though.
		assertNotNull(this.getCountdownTimer());
	}

	@Test
	public void shouldThrowWhenCountdownIsStartedInWrongState() {
		forAllServiceStatesExcept(ServiceState.WAITING,
				new ActionForServiceState() {
					@Override
					public void execute(ServiceState serviceState) {
						try {
							setServiceState(serviceState);
							countdownService.startCountdown(ONE_SECOND);
							fail("Should have thrown exception");
						} catch (IllegalStateException e) {
							// expected
						}
					}
				});
	}

	@Test
	public void shouldPauseCountdown() {
		// given a service instance in state COUNTING_DOWN with a
		// CountdDownTimer object
		this.setServiceState(ServiceState.COUNTING_DOWN);
		this.setCountdownTimer(this.countDownTimer);

		// when countdown is paused
		this.countdownService.pauseCountdown();

		// then state is set to PAUSED
		assertTrue(this.countdownService.isPaused());

		// and countdown timer is cancelled
		verify(this.countDownTimer).cancel();
	}

	@Test
	public void shouldThrowWhenCountdownIsPausedInWrongState() {
		forAllServiceStatesExcept(ServiceState.COUNTING_DOWN,
				new ActionForServiceState() {
					@Override
					public void execute(ServiceState serviceState) {
						try {
							setServiceState(serviceState);
							countdownService.pauseCountdown();
							fail("Should have thrown exception");
						} catch (IllegalStateException e) {
							// expected
						}
					}
				});
	}

	@Test
	public void shouldContinueCountdown() {
		// given a service instance in state PAUSED
		this.countdownService.startCountdown(ONE_SECOND);
		this.countdownService.pauseCountdown();
		assertTrue(this.countdownService.isPaused());

		// when countdown is continued
		this.countdownService.continueCountdown();

		// then state is set to COUNTING_DOWN again
		assertTrue(this.countdownService.isCountingDown());
	}

	@Test
	public void shouldThrowWhenCountdownIsContinuedInWrongState() {
		forAllServiceStatesExcept(ServiceState.PAUSED,
				new ActionForServiceState() {
					@Override
					public void execute(ServiceState serviceState) {
						try {
							setServiceState(serviceState);
							countdownService.continueCountdown();
							fail("Should have thrown exception");
						} catch (IllegalStateException e) {
							// expected
						}
					}
				});
	}

	@Test
	public void shouldStopAlarmSound() {
		// given a service instance in state BEEPING
		this.setServiceState(ServiceState.BEEPING);

		// when alarm sound is stopped
		this.countdownService.stopAlarmSound();

		// then sound is cancelled
		verify(this.soundGizmo).stopAlarm();
		// and notification is removed
		verify(this.notificationManager).cancel(CountdownService.TAG,
				CountdownService.ALARM_NOTIFICATION_ID);
		// and state is FINISHED
		assertTrue(this.countdownService.isFinished());
	}

	@Test
	public void shouldThrowWhenStoppingAlarmSoundInWrongState() {
		forAllServiceStatesExcept(ServiceState.BEEPING,
				new ActionForServiceState() {
					@Override
					public void execute(ServiceState serviceState) {
						try {
							setServiceState(serviceState);
							countdownService.stopAlarmSound();
							fail("Should have thrown exception");
						} catch (IllegalStateException e) {
							// expected
						}
					}
				});
	}

	@Test
	public void shouldStopCountdown() {
		// given a service instance in state COUNTING_DOWN
		this.setServiceState(ServiceState.COUNTING_DOWN);
		this.setCountdownTimer(this.countDownTimer);

		// when countdown is stopped
		this.countdownService.stopCountdown();

		// then countdown timer is cancelled
		verify(this.countDownTimer).cancel();
		// and sound is cancelled
		verify(this.soundGizmo).stopAlarm();
		// and state is set to FINISHED
		assertTrue(this.countdownService.isFinished());

	}

	@Test
	public void shouldResetToWaiting() {
		// given a service instance in state FINISHED
		this.setServiceState(ServiceState.FINISHED);

		// when countdown service is reset to state waiting
		this.countdownService.resetToWaiting();

		// then internal time values are reset
		assertNull(getInternalState(this.countdownService,
				"initialSecondsRoundedUp"));
		assertThat((Long) TimerWhitebox.getInternalState(this.countdownService,
				"remainingMilliseconds"), is(equalTo(Long.MAX_VALUE)));
		// and state is set to WAITING
		assertTrue(this.countdownService.isWaiting());

	}

	@Test
	public void shouldThrowWhenResetToWaitingInWrongState() {
		forAllServiceStatesExcept(ServiceState.FINISHED,
				new ActionForServiceState() {
					@Override
					public void execute(ServiceState serviceState) {
						try {
							setServiceState(serviceState);
							countdownService.resetToWaiting();
							fail("Should have thrown exception");
						} catch (IllegalStateException e) {
							// expected
						}
					}
				});
	}

	@Test
	public void shouldExit() {
		// given a service instance in state COUNTING_DOWN
		this.setServiceState(ServiceState.COUNTING_DOWN);
		this.setCountdownTimer(this.countDownTimer);

		// when the user wants to exit the application completely
		this.countdownService.exit();

		// then countdown timer is cancelled
		verify(this.countDownTimer).cancel();
		// and state is set to WAITING
		assertTrue(this.countdownService.isExit());

	}

	@Test
	public void shouldReturn0AsRemainingFractionIfInitialMillisIsNull() {
		setRemainingFractionInput(null, 5000L);
		assertThat(
				this.countdownService
						.getRemainingFractionRoundedUpToFullSeconds(),
				is(equalTo(0f)));
	}

	@Test
	public void shouldReturn0AsRemainingFractionIfInitialMillisIsZero() {
		setRemainingFractionInput(BigDecimal.ZERO, 5000L);
		assertThat(
				this.countdownService
						.getRemainingFractionRoundedUpToFullSeconds(),
				is(equalTo(0f)));
	}

	@Test
	public void shouldReturnRemainingFraction() {
		setRemainingFractionInput(BigDecimal.TEN, 5000L);
		assertThat(
				this.countdownService
						.getRemainingFractionRoundedUpToFullSeconds(),
				is(equalTo(0.5f)));
	}

	@Test
	public void shouldReturnRemainingFractionRounded() {
		setRemainingFractionInput(BigDecimal.TEN, 4001L);
		assertThat(
				this.countdownService
						.getRemainingFractionRoundedUpToFullSeconds(),
				is(equalTo(0.5f)));
	}

	@Test
	public void shouldReduceRemainingMillisecondsOnTick() {
		// given a started countdown with 5 seconds
		this.setCountdownTimer(this.countDownTimer);
		this.countdownService.startCountdown(FIVE_SECONDS);

		// when the internal countDownTimer ticks
		TimerWhitebox.callInternalMethod(this.countdownService,
				"onCountdownTimerTick", new Object[] { 4950L },
				new Class<?>[] { long.class });

		// then remaining milliseconds should have been decreased
		assertThat(this.countdownService.getRemainingMilliseconds(),
				is(equalTo(4950L)));
	}

	@Test
	public void shouldFinishCountdown() {
		// when the internal countDownTimer finishes
		TimerWhitebox.callInternalMethod(this.countdownService,
				"onCountdownTimerFinish");

		// then remaining milliseconds should be zero
		assertThat(this.countdownService.getRemainingMilliseconds(),
				is(equalTo(0L)));

		// and the delay timer should have been started - but I have no clue how
		// to test that :-( It's created internally in startDelayAlarmTimer
	}

	@Test
	public void shouldStartAlarmWhenDelayTimerFinishes() {
		// when the delay timer finishes and the alarm is finally to be kicked
		// off
		TimerWhitebox.callInternalMethod(this.countdownService,
				"onDelayTimerFinish");

		// then service should be in state BEEPING
		assertTrue(this.countdownService.isBeeping());
		// then notificaiton should be added to status bar
		verify(notificationManager).notify(eq(CountdownService.TAG),
				eq(CountdownService.ALARM_NOTIFICATION_ID),
				(Notification) anyObject());
		// the sound should be played
		verify(this.getSoundGizmo()).playAlarmSound((Context) anyObject());
		// then ShowAlarmActivity shouldBeStarted
		verify(this.countdownService).startShowAlarmActivityFromService();
	}

	private void setServiceState(ServiceState serviceState) {
		TimerWhitebox.setInternalState(this.countdownService, "serviceState",
				serviceState);
	}

	private void setCountdownTimer(CountDownTimer countdownTimer) {
		TimerWhitebox.setInternalState(this.countdownService, "countdownTimer",
				countdownTimer);
	}

	private CountDownTimer getCountdownTimer() {
		return (CountDownTimer) TimerWhitebox.getInternalState(
				this.countdownService, "countdownTimer");
	}

	private void setSoundGizmo(SoundGizmo soundGizmo) {
		TimerWhitebox.setInternalState(this.countdownService, "soundGizmo",
				soundGizmo);
	}

	private SoundGizmo getSoundGizmo() {
		return (SoundGizmo) TimerWhitebox.getInternalState(
				this.countdownService, "soundGizmo");
	}

	private void setNotificationManager(NotificationManager notificationManager) {
		TimerWhitebox.setInternalState(this.countdownService,
				"notificationManager", notificationManager);
	}

	private void setRemainingFractionInput(BigDecimal initialSecondsRoundUp,
			long remainingMilliseconds) {
		TimerWhitebox.setInternalState(this.countdownService,
				"initialSecondsRoundedUp", initialSecondsRoundUp);
		TimerWhitebox.setInternalState(this.countdownService,
				"remainingMilliseconds", remainingMilliseconds);
	}
}