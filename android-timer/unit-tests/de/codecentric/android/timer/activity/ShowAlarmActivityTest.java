package de.codecentric.android.timer.activity;

import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import android.widget.Button;
import de.codecentric.android.timer.R;
import de.codecentric.android.timer.service.ServiceState;

/**
 * Test class for {@link ShowAlarmActivityTest}.
 * 
 * @author Bastian Krol
 */
public class ShowAlarmActivityTest extends
		AbstractCountdownServiceClientTest<ShowAlarmActivity> {

	private Button buttonStopAlarm;

	@Before
	public void before() {
		this.buttonStopAlarm = (Button) this.activity
				.findViewById(R.id.buttonStopAlarm);
	}

	@Override
	protected ShowAlarmActivity createActivityInstance() {
		return new ShowAlarmActivity();
	}

	@Override
	protected ServiceState getCurrentServiceState() {
		return ServiceState.BEEPING;
	}

	@Test
	public void shouldStopAlarmSoundOnButtonClick() {
		this.buttonStopAlarm.performClick();
		verify(this.countdownService).stopAlarmSound();
	}

	@Test
	public void shouldStopAlarmSoundOnBackButton() {
		this.activity.onBackPressed();
		verify(this.countdownService).stopAlarmSound();
	}

	@Test
	public void shouldNotStopAlarmSoundOnHomeButton() {
		// Pressing home leaves the activity (among other calling onPause())
		this.activity.onPause();
		verify(this.countdownService, never()).stopAlarmSound();
	}
}
