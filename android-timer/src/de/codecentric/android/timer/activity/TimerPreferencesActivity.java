package de.codecentric.android.timer.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;
import de.codecentric.android.timer.R;

/**
 * Activity to configure the timer countdown app.
 * 
 * @author Bastian Krol
 */
public class TimerPreferencesActivity extends PreferenceActivity {

	private static final String TAG = SetTimerWheelsActivity.class.getName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		super.addPreferencesFromResource(R.xml.preferences);
	}
}