package de.codecentric.android.timer.service;

import static de.codecentric.android.timer.ServiceStateIterator.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;

import android.app.NotificationManager;
import android.os.CountDownTimer;
import android.os.IBinder;

import com.xtremelabs.robolectric.RobolectricTestRunner;

import de.codecentric.android.timer.ActionForServiceState;

@RunWith(RobolectricTestRunner.class)
public class CountdownServiceTest {

	private static final int ONE_SECOND = 1000;

	private CountdownService countdownService;

	@Mock
	private SoundGizmo soundGizmo;

	@Mock
	private NotificationManager notificationManager;

	@Mock
	private CountDownTimer countDownTimer;

	@Before
	public void before() {
		initMocks(this);
		this.countdownService = new CountdownService();
		this.countdownService.onCreate();
		this.setNotificationManager(this.notificationManager);
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

		// when countdown is pause
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

	// TODO test countdownTimer#onTick

	// TODO test onCountdownTimerFinish()/countdownTimer#onFinish (should start
	// startDelayAlarmTimer)

	// TODO test startDelayAlarmTimer#onDelayTimerFinish

	@Test
	public void shouldStopAlarmSound() {
		// given a service instance in state BEEPING
		this.setServiceState(ServiceState.BEEPING);
		this.setSoundGizmo(this.soundGizmo);

		// when alarm sound is stopped
		this.countdownService.stopAlarmSound();

		// then sound is cancelled
		verify(this.soundGizmo).stopAlarm();
		// and notification is removed
		verify(this.notificationManager).cancel(CountdownService.TAG,
				CountdownService.ALARM_NOTIFICATION_ID);
		// and state if FINISHED
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

	private void setServiceState(ServiceState serviceState) {
		Whitebox.setInternalState(this.countdownService, "serviceState",
				serviceState);
	}

	private void setCountdownTimer(CountDownTimer countdownTimer) {
		Whitebox.setInternalState(this.countdownService, "countdownTimer",
				countdownTimer);
	}

	private CountDownTimer getCountdownTimer() {
		return (CountDownTimer) Whitebox.getInternalState(
				this.countdownService, "countdownTimer");
	}

	private void setSoundGizmo(SoundGizmo soundGizmo) {
		Whitebox.setInternalState(this.countdownService, "soundGizmo",
				soundGizmo);
	}

	private void setNotificationManager(NotificationManager notificationManager) {
		Whitebox.setInternalState(this.countdownService, "notificationManager",
				notificationManager);
	}

}