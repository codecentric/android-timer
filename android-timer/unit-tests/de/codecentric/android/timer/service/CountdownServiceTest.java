package de.codecentric.android.timer.service;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.util.reflection.Whitebox;

import android.os.CountDownTimer;
import android.os.IBinder;

import com.xtremelabs.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class CountdownServiceTest {

	private static final int ONE_SECOND = 1000;
	private CountdownService countdownService;

	@Before
	public void before() {
		this.countdownService = new CountdownService();
		this.countdownService.onCreate();
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
		assertThat(this.getServiceState(),
				is(equalTo(ServiceState.COUNTING_DOWN)));
		// unfortunately CountdDownTimer object is instantiated inside
		// startCountdown, thus it's quite difficult to verify it has been
		// started. We can verify it has been created, though.
		assertNotNull(this.getCountdownTimer());
	}

	@Test
	public void shouldThrowWhenCountdownIsStartedInWrongState() {
		for (ServiceState serviceState : ServiceState.values()) {
			if (serviceState != ServiceState.WAITING) {
				try {
					this.setServiceState(serviceState);
					this.countdownService.startCountdown(ONE_SECOND);
					fail("Should have thrown exception");
				} catch (IllegalStateException e) {
					// expected
				}
			}
		}
	}

	private void setServiceState(ServiceState serviceState) {
		Whitebox.setInternalState(this.countdownService, "serviceState",
				serviceState);
	}

	private ServiceState getServiceState() {
		return (ServiceState) Whitebox.getInternalState(this.countdownService,
				"serviceState");
	}

	private void setCountdownTimer(CountDownTimer countdownTimer) {
		Whitebox.setInternalState(this.countdownService, "countdownTimer",
				countdownTimer);
	}

	private CountDownTimer getCountdownTimer() {
		return (CountDownTimer) Whitebox.getInternalState(
				this.countdownService, "countdownTimer");
	}

}