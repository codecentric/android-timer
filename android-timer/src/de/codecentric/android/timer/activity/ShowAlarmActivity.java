package de.codecentric.android.timer.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import de.codecentric.android.timer.R;
import de.codecentric.android.timer.service.ServiceState;

public class ShowAlarmActivity extends CountdownServiceClient {

	private static final String TAG = ShowAlarmActivity.class.getName();

	private static final ServiceState[] HANDLED_STATES = { ServiceState.BEEPING };

	private static final ServiceState[] FINISHING_STATES = {
			ServiceState.WAITING, ServiceState.COUNTING_DOWN,
			ServiceState.PAUSED, ServiceState.FINISHED, ServiceState.EXIT };

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.show_alarm);

		Button buttonStopAlarm = (Button) this
				.findViewById(R.id.buttonStopAlarm);
		buttonStopAlarm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				onStopAlarmClicked();
			}
		});
	}

	@Override
	protected ServiceState[] getHandledServiceStates() {
		return HANDLED_STATES;
	}

	@Override
	protected ServiceState[] getFinishingServiceStates() {
		return FINISHING_STATES;
	}

	@Override
	public void onBackPressed() {
		Log.d(TAG, "onBackPressed()");
		this.stopAlarmSound();
		super.onBackPressed();
	}

	private void onStopAlarmClicked() {
		Log.d(TAG, "onStopAlarmClicked()");
		this.stopAlarmSound();
		this.finish();
	}

	private void stopAlarmSound() {
		Log.d(TAG, "notifying service that alarm has stopped");
		this.getCountdownService().stopAlarmSound();
	}

	@Override
	protected String getTag() {
		return TAG;
	}
}
