package de.codecentric.android.timer.activity;

import static org.junit.Assert.*;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.xtremelabs.robolectric.RobolectricTestRunner;

import de.codecentric.android.timer.service.ServiceState;

/**
 * Test class for {@link ShowCountdownActivity}.
 * 
 * @author Bastian Krol
 */
@RunWith(RobolectricTestRunner.class)
public class ShowCountdownActivityTest extends
		AbstractCountdownServiceClientTest<ShowCountdownActivity> {

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
		this.timeDisplayHelper = this.mockCollaborator(TimeDisplayHelper.class,
				"timeDisplay");
	}

	@Test
	public void shouldHandleStateCountingDown() throws Exception {
		assertThat(this.activity.getHandledServiceStates(),
				Matchers.hasItemInArray(ServiceState.COUNTING_DOWN));
	}

}
