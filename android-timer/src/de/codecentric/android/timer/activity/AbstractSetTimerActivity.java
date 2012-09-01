package de.codecentric.android.timer.activity;

import static android.view.View.*;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import de.codecentric.android.timer.R;
import de.codecentric.android.timer.persistence.Timer;
import de.codecentric.android.timer.service.ServiceState;
import de.codecentric.android.timer.util.TimeParts;

/**
 * Base class for activities to configure the timer.
 * 
 * @author Bastian Krol
 */
abstract class AbstractSetTimerActivity extends CountdownServiceClient {

	private static final ServiceState[] HANDLED_STATES = {
			ServiceState.WAITING, ServiceState.FINISHED,
			ServiceState.FINISHED_AUTOMATICALLY };
	private static final ServiceState[] FINISHING_STATES = { ServiceState.EXIT };

	private Button buttonStart;
	private Button buttonClose;

	private TimeParts time = TimeParts.FIFTEEN_MINUTES;
	private boolean useHours;
	private boolean useMinutes;
	private boolean useSeconds;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(this.getTag(), "onCreate [AbstractSetTimerActivity]");
		super.onCreate(savedInstanceState);
		super.setContentView(this.getViewId());
		this.fetchViewObjects();
		this.configureButtons();
	}

	@Override
	protected void onResume() {
		Log.d(this.getTag(), "onResume() [AbstractSetTimerActivity]");
		SharedPreferences preferences = this.loadPreferences();
		this.checkIfCorrectClassIsUsed(preferences);
		this.configureTimeInputControls();
		this.refreshView();
		super.onResume();
	}

	private void fetchViewObjects() {
		this.buttonStart = (Button) this.findViewById(R.id.buttonStart);
		this.buttonClose = (Button) this.findViewById(R.id.buttonClose);
		this.fetchViewObjectsForTimeInputControls();
	}

	protected abstract void fetchViewObjectsForTimeInputControls();

	private void checkIfCorrectClassIsUsed(SharedPreferences preferences) {
		Log.d(this.getTag(), "checkIfCorrectClassIsUsed");
		Class<? extends CountdownServiceClient> setTimerActivityClass = this
				.getNavigation().loadSetTimerActivityClassFromPreferences(
						preferences);
		if (this.setTimerMethodHasChanged(setTimerActivityClass)) {
			// finish the current SetTimerActivity to get to StartupActivity
			// which will start the correct SetTimerActivity subclass
			Log.d(this.getTag(),
					"The set timer input method has changed. This activity will finish now so that StartupActivity can launch the correct activity.");
			this.finish();
		}
	}

	private final boolean setTimerMethodHasChanged(
			Class<? extends CountdownServiceClient> setTimerActivityClass) {
		Log.d(this.getTag(), "this.getClass(): " + this.getClass());
		Log.d(this.getTag(), "class from preferences: " + setTimerActivityClass);
		return !this.getClass().equals(setTimerActivityClass);
	}

	@Override
	public void onBackPressed() {
		this.leaveApp();
		super.onBackPressed();
	}

	protected abstract int getViewId();

	protected abstract void configureTimeInputControls();

	protected abstract TimeParts getTimePartsFromControls();

	protected abstract void setTime(TimeParts timeParts);

	protected abstract void showAndHideTimeInputControls(boolean useHours,
			boolean useMinutes, boolean useSeconds);

	@Override
	protected ServiceState[] getHandledServiceStates() {
		return HANDLED_STATES;
	}

	@Override
	protected ServiceState[] getFinishingServiceStates() {
		return FINISHING_STATES;
	}

	private void configureButtons() {
		this.configureButtonStart();
		this.configureButtonClose();
	}

	private void configureButtonStart() {
		this.buttonStart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				onStartClicked();
			}
		});
	}

	private void configureButtonClose() {
		this.buttonClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				leaveApp();
			}
		});
	}

	private void refreshView() {
		Log.d(this.getTag(), "refreshView()");
		this.adaptTimeToMissingControls();
		this.showAndHideLabels();
		this.showAndHideTimeInputControls(this.useHours, this.useMinutes,
				this.useSeconds);
		this.setTime(this.time);
	}

	private SharedPreferences loadPreferences() {
		Log.d(this.getTag(), "loadPreferences()");
		SharedPreferences preferences = this.getDefaultPreferences();
		this.loadFromPreferencesTime(preferences);
		this.loadFromPreferencesFieldsToUse(preferences);
		this.loadPreferencesForSubclass(preferences);
		return preferences;
	}

	private void loadFromPreferencesTime(SharedPreferences preferences) {
		Log.d(this.getTag(), "loadTimerFromPreferences()");
		long initialTimerMilliseconds = preferences.getLong(
				this.getPreferencesKeysValues().keyLastTimer,
				TimeParts.FIFTEEN_MINUTES.getMillisecondsTotal());
		this.time = TimeParts.fromMillisExactly(initialTimerMilliseconds);
	}

	private void loadFromPreferencesFieldsToUse(SharedPreferences preferences) {
		this.loadFromPreferencesFieldsToUse(preferences, false, true, true);
	}

	private void loadFromPreferencesFieldsToUse(SharedPreferences preferences,
			boolean defaultUseHours, boolean defaultUseMinutes,
			boolean defaultUseSeconds) {
		Log.d(this.getTag(), "loadFromPreferencesFieldsToUse");
		this.useHours = preferences.getBoolean(
				this.getPreferencesKeysValues().keyUseHoursInput,
				this.getPreferencesKeysValues().defaultValueUseHoursInput);
		this.useMinutes = preferences.getBoolean(
				this.getPreferencesKeysValues().keyUseMinutesInput,
				this.getPreferencesKeysValues().defaultValueUseMinutesInput);
		this.useSeconds = preferences.getBoolean(
				this.getPreferencesKeysValues().keyUseSecondsInput,
				this.getPreferencesKeysValues().defaultValueUseSecondsInput);
		Log.d(this.getTag(), "loaded: " + this.useHours + ", "
				+ this.useMinutes + ", " + this.useSeconds);
	}

	protected void loadPreferencesForSubclass(SharedPreferences preferences) {
		// Empty default implementation
	}

	@Override
	protected boolean isManageTimersEnabled() {
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(this.getTag(), "onOptionsItemSelected(...)");
		switch (item.getItemId()) {
		case R.id.itemLoadTimer:
			Log.d(this.getTag(), "item load timer clicked");
			Intent manageTimersListActivity = new Intent(this,
					ManageTimersListActivity.class);
			super.startActivityForResult(manageTimersListActivity,
					REQUEST_CODE_LOAD_TIMER);
			return true;
		case R.id.itemSaveTimer:
			Log.d(this.getTag(), "item save timer clicked");
			Intent saveTimerActivity = new Intent(this, SaveTimerActivity.class);
			// TODO Make this activity use Timer object as model
			long millis = this.getMillisFromControls();
			Timer timer = new Timer(null, millis);
			saveTimerActivity.putExtra(SaveTimerActivity.SAVE_TIMER_PARAM,
					timer);
			super.startActivity(saveTimerActivity);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(this.getTag(), "onActivityResult(" + requestCode + ", "
				+ resultCode + ", " + data + ")");
		if (requestCode == REQUEST_CODE_PREFERENCES) {
			handlePreferenceResult();
		} else if (requestCode == REQUEST_CODE_LOAD_TIMER) {
			handleLoadTimerResult(requestCode, resultCode, data);
		}
	}

	private void handlePreferenceResult() {
		Log.d(this.getTag(), "handlePreferenceResult()");
		this.loadFromPreferencesFieldsToUse(this.getDefaultPreferences(),
				this.useHours, this.useMinutes, this.useSeconds);
		this.refreshView();
	}

	private void handleLoadTimerResult(int requestCode, int resultCode,
			Intent data) {
		Log.d(this.getTag(), "handleLoadTimerResult(" + requestCode + ", "
				+ resultCode + ", " + data + ")");
		Timer loadedTimer = (Timer) data
				.getSerializableExtra(ManageTimersListActivity.LOAD_TIMER_RESULT);
		if (resultCode == RESULT_OK && loadedTimer != null) {
			this.time = TimeParts.fromMillisExactly(loadedTimer.getMillis());
			this.adaptMissingControlsToTime();
			this.saveCurrentStateToPreferences(loadedTimer.getMillis());
		} else if (requestCode == RESULT_OK && loadedTimer == null) {

			Log.w(this.getTag(),
					"ManageTimersListActivity returned RESULT_OK but no timer object.");
		}
	}

	private void adaptTimeToMissingControls() {
		Log.d(this.getTag(), "adaptTimeToMissingControls()");
		if (this.time.getDays() != 0) {
			Log.d(this.getTag(), "Removing days portion from " + this.time
					+ " because corresponding input field is not displayed.");
			this.time = this.time.removeDays();
		}
		if (!this.useHours && this.time.getHours() != 0) {
			Log.d(this.getTag(), "Removing hours portion from " + this.time
					+ " because corresponding input field is not displayed.");
			this.time = this.time.removeHours();
		}
		if (!this.useMinutes && this.time.getMinutes() != 0) {
			Log.d(this.getTag(), "Removing minutes portion from " + this.time
					+ " because corresponding input field is not displayed.");
			this.time = this.time.removeMinutes();
		}
		if (!this.useSeconds && this.time.getSeconds() != 0) {
			Log.d(this.getTag(), "Removing seconds portion from " + this.time
					+ " because corresponding input field is not displayed.");
			this.time = this.time.removeSeconds();
		}
	}

	private void adaptMissingControlsToTime() {
		Log.d(this.getTag(), "adaptMissingControlsToTime()");
		// This first "if" needs to change if we ever support days in the UI.
		// For now it removes the time, just as in adaptTimeToMissingControls
		// instead of enabling the control.
		if (this.time.getDays() != 0) {
			Log.d(this.getTag(), "Removing days portion from " + this.time
					+ " because corresponding input field is not displayed.");
			this.time = this.time.removeDays();
		}
		if (!this.useHours && this.time.getHours() != 0) {
			Log.d(this.getTag(),
					"Adding hours, minutes and seconds controls because time "
							+ this.time + " has hours.");
			this.useHours = true;
			this.useMinutes = true;
			this.useSeconds = true;
		} else if (!this.useMinutes && this.time.getMinutes() != 0) {
			Log.d(this.getTag(),
					"Adding minutes and seconds controls because time "
							+ this.time + " has minutes.");
			this.useMinutes = true;
			this.useSeconds = true;
		} else if (!this.useSeconds && this.time.getSeconds() != 0) {
			Log.d(this.getTag(), "Adding seconds controls because time "
					+ this.time + " has seconds.");
			this.useSeconds = true;
		}
	}

	private void showAndHideLabels() {
		this.findViewById(R.id.textViewHours).setVisibility(
				flagToVisibility(this.useHours));
		this.findViewById(R.id.textViewMinutes).setVisibility(
				flagToVisibility(this.useMinutes));
		this.findViewById(R.id.textViewSeconds).setVisibility(
				flagToVisibility(this.useSeconds));
	}

	protected int flagToVisibility(boolean flag) {
		return flag ? VISIBLE : GONE;
	}

	private void onStartClicked() {
		Log.d(this.getTag(), "onStartClicked()");
		if (this.isServiceBound()) {
			if (this.getCountdownService().isFinished()) {
				this.getCountdownService().resetToWaiting();
			}
			this.startCountdown();
			this.navigate();
		} else {
			Log.e(this.getTag(), "service not bound in onStartClicked()");
		}
	}

	private void startCountdown() {
		Log.d(this.getTag(), "startCountdown()");
		long millis = getMillisFromControls();
		Log.d(this.getTag(), "starting countdown with " + millis
				+ " milliseconds");
		this.getCountdownService().startCountdown(millis);
		Log.d(this.getTag(), "countdown started");
		this.saveCurrentStateToPreferences(millis);
	}

	private long getMillisFromControls() {
		TimeParts timeParts = this.getTimePartsFromControls();
		return timeParts.getMillisecondsTotal();
	}

	private void leaveApp() {
		Log.d(this.getTag(), "leaveApp()");
		if (this.isServiceBound()) {
			this.getCountdownService().exit();
		} else {
			Log.d(this.getTag(), "service not bound in leaveApp()");
		}
		super.finish();
	}

	private void saveCurrentStateToPreferences(final long milliseconds) {
		Log.d(this.getTag(), "saveStateToPreferences(): " + milliseconds + ", "
				+ this.useHours + ", " + this.useMinutes + ", "
				+ this.useSeconds);
		doWithEditablePreferences(new PreferenceEditAction() {
			@Override
			public void execute(Editor editor) {
				editor.putLong(getPreferencesKeysValues().keyLastTimer,
						milliseconds);
				editor.putBoolean(getPreferencesKeysValues().keyUseHoursInput,
						useHours);
				editor.putBoolean(
						getPreferencesKeysValues().keyUseMinutesInput,
						useMinutes);
				editor.putBoolean(
						getPreferencesKeysValues().keyUseSecondsInput,
						useSeconds);
			}
		});
	}
}