package de.codecentric.android.timer.activity;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import com.xtremelabs.robolectric.RobolectricTestRunner;

import de.codecentric.android.timer.ActionForServiceState;
import de.codecentric.android.timer.ServiceStateIterator;
import de.codecentric.android.timer.service.ServiceState;

/**
 * Test class for {@link ShowCountdownActivity}.
 * 
 * @author Bastian Krol
 */
@RunWith(RobolectricTestRunner.class)
public class ShowCountdownActivityTest extends
		AbstractCountdownServiceClientTest<ShowCountdownActivity> {

	@Mock
	private TimeDisplayHelper timeDisplayHelper;

	@Override
	protected ShowCountdownActivity createActivityInstance() {
		return new ShowCountdownActivity();
	}

	@Override
	protected ServiceState getCurrentServiceState() {
		return ServiceState.COUNTING_DOWN;
	}

	@Override
	protected void doAdditionalMockingBeforeOnResume() {
		this.mockCollaborator(this.timeDisplayHelper, "timeDisplay");
	}

	@Test
	public void shouldHandleStateCountingDown() throws Exception {
		assertThat(this.activity.getHandledServiceStates(),
				Matchers.hasItemInArray(ServiceState.COUNTING_DOWN));
	}

	@Test
	public void shouldHandleStatePaused() throws Exception {
		assertThat(this.activity.getHandledServiceStates(),
				Matchers.hasItemInArray(ServiceState.PAUSED));
	}

	@Test
	public void shouldHandleNoStateExceptCountingDownAndPaused()
			throws Exception {
		ServiceStateIterator.forAllServiceStatesExcept(new ActionForServiceState() {
			@Override
			public void execute(ServiceState serviceState) {
				assertThat(activity.getHandledServiceStates(),
						not(Matchers.hasItemInArray(serviceState)));
			}
		}, ServiceState.COUNTING_DOWN, ServiceState.PAUSED);
	}

	@Test
	public void shouldFetchCurrentTimeFromService() {
		this.activity.handleState(ServiceState.COUNTING_DOWN);
		verify(countdownService, atLeastOnce()).getRemainingMilliseconds();
	}
}
