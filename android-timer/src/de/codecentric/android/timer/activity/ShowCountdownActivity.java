package de.codecentric.android.timer.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import de.codecentric.android.timer.R;
import de.codecentric.android.timer.service.CountdownService;
import de.codecentric.android.timer.service.ServiceState;
import de.codecentric.android.timer.util.TimeParts;
import de.codecentric.android.timer.view.PieChartView;

/**
 * This view shows the timer counting down and beeps when it has reached zero.
 * 
 * @author Bastian Krol
 */
public class ShowCountdownActivity extends CountdownServiceClient {

	private static final String TAG = ShowCountdownActivity.class.getName();

	private static final ServiceState[] HANDLED_STATES = {
			ServiceState.COUNTING_DOWN, ServiceState.PAUSED };

	private static final ServiceState[] FINISHING_STATES = {
			ServiceState.WAITING, ServiceState.FINISHED, ServiceState.EXIT };

	private PieChartView pieChart;
	private TimeDisplayHelper timeDisplay;
	private TextView textViewHours;
	private TextView textViewColonHoursMinutes;
	private TextView textViewMinutes;
	private TextView textViewColonMinutesSeconds;
	private TextView textViewSeconds;
	private TextView textViewPaused;

	private Button buttonPauseContinueStopAlarm;
	private Button buttonCancel;

	private CountDownTimer refreshTimer;

	private boolean keepDisplayOn;
	private boolean tapAnywhereToPause;
	private WakeLock wakeLock;
	private boolean showPieChart;
	private boolean showText;

	/**
	 * {@inheritDoc}
	 * 
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate(Bundle)");
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.show_countdown);
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
	protected void handleState(ServiceState serviceState) {
		Log.d(TAG, "handleState(" + serviceState + ")");
		switch (serviceState) {
		case COUNTING_DOWN:
			Log.d(TAG, "COUNTING_DOWN");
			// Already counting down, keep time display up to date
			this.refreshTextViewsFromService();
			this.updateViewForRunningState();
			this.startRefreshTimer();
			break;
		case PAUSED:
			Log.d(TAG, "PAUSED");
			// Countdown is paused, update display and wait for user input
			this.refreshTextViewsFromService();
			this.updateViewForPausedState();
			break;
		default:
			throw new IllegalStateException("Unhandled state: " + serviceState);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.loadPreferences();
		this.configureView();
		this.acquireWakeLock();
	}

	@Override
	protected void onPause() {
		this.releaseWakeLock();
		super.onPause();
	}

	@Override
	protected void onStop() {
		Log.d(TAG, "onStop()");
		this.cancelRefreshTimer();
		// just in case
		this.releaseWakeLock();
		super.onStop();
	}

	private void loadPreferences() {
		Log.d(this.getTag(), "loadPreferences()");
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		this.loadFromPreferencesKeepDisplayOn(preferences);
		this.loadFromPreferencesShowCountdownOptions(preferences);
		this.loadFromPreferencesTapAnywhereToPause(preferences);
	}

	private void loadFromPreferencesKeepDisplayOn(SharedPreferences preferences) {
		this.keepDisplayOn = preferences.getBoolean(
				this.getPreferencesKeysValues().keyKeepDisplayOn,
				this.getPreferencesKeysValues().defaultValueKeepDisplayOn);
	}

	private void loadFromPreferencesShowCountdownOptions(
			SharedPreferences preferences) {
		String showCountdownOptions = preferences
				.getString(
						this.getPreferencesKeysValues().keyShowCountdownOptions,
						this.getPreferencesKeysValues().defaultValueShowCountdownOptions);
		if (showCountdownOptions
				.equals(this.getPreferencesKeysValues().valueShowCountdownOptionsPieChartOnly)) {
			this.showPieChart = true;
			this.showText = false;
		} else if (showCountdownOptions
				.equals(this.getPreferencesKeysValues().valueShowCountdownOptionsTextOnly)) {
			this.showPieChart = false;
			this.showText = true;
		} else {
			this.showPieChart = true;
			this.showText = true;
		}
	}

	private void loadFromPreferencesTapAnywhereToPause(
			SharedPreferences preferences) {
		this.tapAnywhereToPause = preferences.getBoolean(
				this.getPreferencesKeysValues().keyTapAnywhereToPause,
				this.getPreferencesKeysValues().defaultValueTapAnywhereToPause);
	}

	void configureView() {
		this.configurePieChart();
		this.configureTextViews();
		this.configureButtons();
	}

	private void configurePieChart() {
		Log.d(TAG, "configurePieChart");
		this.pieChart = (PieChartView) super.findViewById(R.id.pieChart);
		if (this.showPieChart) {
			this.pieChart.setVisibility(View.VISIBLE);
		} else {
			this.pieChart.setVisibility(View.GONE);
		}
	}

	private void configureTextViews() {
		Log.d(TAG, "configureTextViews()");
		this.textViewHours = (TextView) super.findViewById(R.id.textViewHours);
		this.textViewColonHoursMinutes = (TextView) super
				.findViewById(R.id.textViewColon1);
		this.textViewMinutes = (TextView) super
				.findViewById(R.id.textViewMinutes);
		this.textViewColonMinutesSeconds = (TextView) super
				.findViewById(R.id.textViewColon2);
		this.textViewSeconds = (TextView) super
				.findViewById(R.id.textViewSeconds);
		this.timeDisplay = new TimeDisplayHelper(this.textViewHours,
				this.textViewMinutes, this.textViewSeconds, true);
		this.textViewPaused = (TextView) super
				.findViewById(R.id.textViewPaused);

		int visibility = this.showText ? View.VISIBLE : View.GONE;
		this.textViewHours.setVisibility(visibility);
		this.textViewColonHoursMinutes.setVisibility(visibility);
		this.textViewMinutes.setVisibility(visibility);
		this.textViewColonMinutesSeconds.setVisibility(visibility);
		this.textViewSeconds.setVisibility(visibility);
	}

	private void configureButtons() {
		Log.d(TAG, "configureButtons");
		this.configureButtonPauseContinueStopAlarm();
		this.configureButtonCancel();
	}

	private void configureButtonPauseContinueStopAlarm() {
		this.buttonPauseContinueStopAlarm = (Button) super
				.findViewById(R.id.buttonPauseContinueStopAlarm);
		View wholeScreen = super.findViewById(R.id.showCountdownActivity);
		View viewToClickForPauseAndContinue;
		if (!this.tapAnywhereToPause) {
			this.buttonPauseContinueStopAlarm.setVisibility(View.VISIBLE);
			viewToClickForPauseAndContinue = this.buttonPauseContinueStopAlarm;
			wholeScreen.setOnClickListener(null);
		} else {
			this.buttonPauseContinueStopAlarm.setVisibility(View.GONE);
			viewToClickForPauseAndContinue = wholeScreen;
			this.buttonPauseContinueStopAlarm.setOnClickListener(null);
		}
		viewToClickForPauseAndContinue
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						onClickPauseOrContinue();
					}
				});
	}

	private void configureButtonCancel() {
		this.buttonCancel = (Button) this.findViewById(R.id.buttonCancel);
		this.buttonCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				cancelAndLeave();
			}
		});
	}

	/**
	 * Cancels the refresh timer.
	 */
	@Override
	protected void onBeforeServiceDisconnected() {
		Log.d(TAG, "onBeforeServiceDisconnected()");
		this.cancelRefreshTimer();
	}

	@Override
	protected void onAfterServiceDisconnected() {
		Log.d(TAG, "onAfterServiceDisconnected()");
		this.cancelRefreshTimer();
	}

	private void refreshTextViewsFromService() {
		long remainingMilliseconds = this.getCountdownService()
				.getRemainingMilliseconds();
		TimeParts timeParts = TimeParts
				.fromMillisRoundingUp(remainingMilliseconds);
		this.setTime(timeParts);
	}

	private void acquireWakeLock() {
		Log.d(TAG, "acquireWakeLock()");
		if (this.keepDisplayOn) {
			PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
			this.wakeLock = powerManager.newWakeLock(
					PowerManager.SCREEN_BRIGHT_WAKE_LOCK, TAG);
			this.wakeLock.acquire();
		}
	}

	private void releaseWakeLock() {
		Log.d(TAG, "releaseWakeLock()");
		if (this.wakeLock != null && this.wakeLock.isHeld()) {
			this.wakeLock.release();
		}
		this.wakeLock = null;
	}

	private void startRefreshTimer() {
		Log.d(TAG, "startRefreshTimer()");
		if (this.isServiceBound()) {
			long remainingMilliseconds = this.getCountdownService()
					.getRemainingMilliseconds();
			this.refreshTimer = new CountDownTimer(remainingMilliseconds,
					CountdownService.GUI_UPDATE_INTERVALL) {

				public void onTick(long millisUntilFinished) {
					ShowCountdownActivity.this.onRefreshTick();
				}

				public void onFinish() {
					// Nothing to do
				}
			}.start();
		}
	}

	/**
	 * This cancels (stops) the timer that updates this activity's display.
	 */
	private void cancelRefreshTimer() {
		Log.d(TAG, "cancelRefreshTimer()");
		if (this.refreshTimer != null) {
			this.refreshTimer.cancel();
			this.refreshTimer = null;
		}
	}

	/**
	 * This is called when the user clicks on the Pause/Continue button.
	 */
	private void onClickPauseOrContinue() {
		Log.d(TAG, "onClickPauseOrContinueOrStopAlarm()");
		if (this.isServiceBound()) {
			if (this.getCountdownService().isCountingDown()) {
				this.pauseCountdown();
			} else if (this.getCountdownService().isPaused()) {
				this.continueCountdown();
			} else {
				Log.e(TAG,
						"Illegal state in onClickPauseOrContinueOrStopAlarm: "
								+ this.getCountdownService().getState());
			}
		} else {
			Log.w(TAG,
					"service not bound in onClickPauseOrContinueOrStopAlarm()");
		}
	}

	private void continueCountdown() {
		Log.d(TAG, "continueCountdown()");
		if (this.isServiceBound()) {
			this.getCountdownService().continueCountdown();
			Log.d(TAG, "countdown continued");
			this.startRefreshTimer();
			this.updateViewForRunningState();
		} else {
			Log.w(TAG, "service not bound in startOrContinueCountdown");
		}
	}

	private void pauseCountdown() {
		Log.d(TAG, "pauseCountdown()");
		if (this.isServiceBound()) {
			this.getCountdownService().pauseCountdown();
			Log.d(TAG, "countdown paused");
			this.cancelRefreshTimer();
			this.updateViewForPausedState();
		} else {
			Log.w(TAG, "service not bound in pauseCountdown()");
		}
	}

	private void stopCountdown() {
		Log.d(TAG, "stopCountdown()");
		if (this.isServiceBound()) {
			this.getCountdownService().stopCountdown();
			Log.d(TAG, "countdown stopped");
			this.cancelRefreshTimer();
		} else {
			Log.w(TAG, "service not bound in stopCountdown()");
		}
	}

	private void onRefreshTick() {
		Log.d(TAG, "onRefreshTick()");
		if (this.isServiceBound()) {
			long remainingMilliseconds = this.getCountdownService()
					.getRemainingMilliseconds();
			Log.v(TAG, "refresh tick - remainingMilliseconds: "
					+ remainingMilliseconds);
			TimeParts timeParts = TimeParts
					.fromMillisRoundingUp(remainingMilliseconds);
			this.setTime(timeParts);
			if (this.showPieChart) {
				this.pieChart.setFraction(this.getCountdownService()
						.getRemainingFractionRoundedUpToFullSeconds());
			}
		} else {
			Log.w(TAG, "service not bound in onRefreshTick");
		}
	}

	private void setTime(TimeParts timeParts) {
		this.timeDisplay.setTime(timeParts);
		this.hideUnusedTimeParts(timeParts);
	}

	private void hideUnusedTimeParts(TimeParts timeParts) {
		if (timeParts.getHours() == 0) {
			this.textViewHours.setVisibility(View.GONE);
			this.textViewColonHoursMinutes.setVisibility(View.GONE);
		}
		if (timeParts.getHours() == 0 && timeParts.getMinutes() == 0) {
			this.textViewMinutes.setVisibility(View.GONE);
			this.textViewColonMinutesSeconds.setVisibility(View.GONE);
		}
	}

	@Override
	public void onBackPressed() {
		Log.d(TAG, "onBackPressed()");
		this.cancelAndLeave();
	}

	private void cancelAndLeave() {
		Log.d(TAG, "cancelAndLeave()");
		this.stopCountdown();
		this.finish();
	}

	private void updateViewForRunningState() {
		this.buttonPauseContinueStopAlarm
				.setText(R.string.show_countdown_button_pause);
		this.textViewPaused.setVisibility(View.GONE);
	}

	private void updateViewForPausedState() {
		this.buttonPauseContinueStopAlarm
				.setText(R.string.show_countdown_button_continue);
		this.textViewPaused.setVisibility(View.VISIBLE);
	}

	@Override
	protected String getTag() {
		return TAG;
	}
}