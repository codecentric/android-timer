package de.codecentric.android.timer.activity;

import android.os.Bundle;
import android.util.Log;
import de.codecentric.android.timer.R;
import de.codecentric.android.timer.service.ServiceState;

/**
 * Entry point activity for the timer count down app. This activity should never
 * show, but instead navigate directly to a subclass of
 * AbstractSetTimerActivity.
 * 
 * @author Bastian Krol
 */
public class StartupActivity extends CountdownServiceClient {

	private static final String TAG = StartupActivity.class.getName();

	private static final ServiceState[] HANDLED_STATES = {};

	private static final ServiceState[] FINISHING_STATES = { ServiceState.EXIT };

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(this.getTag(), "onCreate");
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.startup);
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
	protected void onBeforeFinish() {
		Log.d(this.getTag(), "onBeforeFinish()");
		this.getCountdownService().stopService();
	}

	@Override
	protected String getTag() {
		return TAG;
	}
}