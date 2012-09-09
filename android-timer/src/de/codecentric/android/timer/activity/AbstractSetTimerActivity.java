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

	private Timer timer = new Timer(null, TimeParts.FIFTEEN_MINUTES);
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

	protected final void setTime(Timer timer) {
		if (timer == null) {
			throw new IllegalArgumentException("timer must not be null");
		}
		if (timer.getTimeParts() == null) {
			throw new IllegalArgumentException(
					"timer.getTimeParts() must not be null");
		}
		this.setTime(timer.getTimeParts());
	}

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
		this.setTime(this.timer);
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
		long initialTimerId = preferences.getLong(
				this.getPreferencesKeysValues().keyLastTimerId,
				Timer.DEFAULT_ID_NOT_IN_DATABASE);
		String initialTimerName = preferences.getString(
				this.getPreferencesKeysValues().keyLastTimerName, null);
		long initialTimerMilliseconds = preferences.getLong(
				this.getPreferencesKeysValues().keyLastTimerMillis,
				TimeParts.FIFTEEN_MINUTES.getMillisecondsTotal());
		this.timer = new Timer(initialTimerId, initialTimerName,
				initialTimerMilliseconds);
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

	private void updateTimerFromControls() {
		Log.d(this.getTag(), "updateTimerFromControls()");
		this.timer.setTimeParts(this.getTimePartsFromControls());
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(this.getTag(), "onOptionsItemSelected(...)");
		switch (item.getItemId()) {
		case R.id.itemManageFavorites:
			Log.d(this.getTag(), "item \"Favorites\" clicked");
			Intent manageFavoritesActivity = new Intent(this,
					ManageFavoritesActivity.class);
			this.saveStateAndStartActivityForResult(manageFavoritesActivity,
					RequestCode.MANAGE_FAVORITES.code);
			return true;
		case R.id.itemSaveAsFavorite:
			Log.d(this.getTag(), "item \"Save as Favorite\" clicked");
			Intent saveAsFavoriteActivity = new Intent(this,
					SaveAsFavoriteActivity.class);
			this.updateTimerFromControls();
			saveAsFavoriteActivity.putExtra(
					SaveAsFavoriteActivity.SAVE_TIMER_PARAM, this.timer);
			saveAsFavoriteActivity.setAction(Intent.ACTION_INSERT_OR_EDIT);
			this.saveStateAndStartActivityForResult(saveAsFavoriteActivity,
					RequestCode.SAVE_FAVORITE.code);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void saveStateAndStartActivity(Intent intent) {
		this.saveCurrentStateToPreferences();
		super.startActivity(intent);
	}

	@Override
	protected void saveStateAndStartActivityForResult(Intent intent,
			int requestCode) {
		this.saveCurrentStateToPreferences();
		super.startActivityForResult(intent, requestCode);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(this.getTag(), "onActivityResult(" + requestCode + ", "
				+ resultCode + ", " + data + ")");
		if (requestCode == RequestCode.PREFERENCES.code) {
			handlePreferenceResult();
		} else if (requestCode == RequestCode.MANAGE_FAVORITES.code) {
			handleLoadFavoriteResult(requestCode, resultCode, data);
		} else if (requestCode == RequestCode.SAVE_FAVORITE.code) {
			handleSaveFavoriteResult(requestCode, resultCode, data);
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	private void handlePreferenceResult() {
		Log.d(this.getTag(), "handlePreferenceResult()");
		this.loadFromPreferencesFieldsToUse(this.getDefaultPreferences(),
				this.useHours, this.useMinutes, this.useSeconds);
		this.refreshView();
	}

	private void handleLoadFavoriteResult(int requestCode, int resultCode,
			Intent data) {
		Log.d(this.getTag(), "handleLoadFavoriteResult(" + requestCode + ", "
				+ resultCode + ", " + data + ")");
		String activityName = "ManageFavoritesActivity";
		String resultKey = ManageFavoritesActivity.LOAD_TIMER_RESULT;
		readTimerFromActivityResult(resultCode, data, resultKey, activityName);
	}

	private void handleSaveFavoriteResult(int requestCode, int resultCode,
			Intent data) {
		Log.d(this.getTag(), "handleSaveFavoriteResult(" + requestCode + ", "
				+ resultCode + ", " + data + ")");

		String resultKey = SaveAsFavoriteActivity.SAVE_TIMER_RESULT;
		String activityName = "SaveAsFavoritesActivity";
		readTimerFromActivityResult(resultCode, data, resultKey, activityName);
	}

	private void readTimerFromActivityResult(int resultCode, Intent data,
			String resultKey, String activityName) {
		if (resultCode == RESULT_OK && data != null) {
			this.timer = (Timer) data.getSerializableExtra(resultKey);
			if (this.timer != null) {
				this.adaptMissingControlsToTime();
				this.saveToPreferences(this.timer, this.useHours,
						this.useMinutes, this.useSeconds);
			} else {
				Log.w(this.getTag(),
						activityName
								+ " returned RESULT_OK but returned data has no timer object.");
			}
		} else if (resultCode == RESULT_OK) {
			Log.w(this.getTag(), activityName
					+ " returned RESULT_OK but returned not data.");
		}
	}

	private void adaptTimeToMissingControls() {
		Log.d(this.getTag(), "adaptTimeToMissingControls()");
		// The condition in the first "if" needs to change if we ever support
		// days in the UI. For now it removes the time, just as in
		// adaptTimeToMissingControls instead of enabling the control.
		if (this.timer.getTimeParts().getDays() != 0) {
			Log.d(this.getTag(),
					"Removing days portion from "
							+ this.timer.getTimeParts()
							+ " because corresponding input field is not displayed.");
			this.timer.setTimeParts(this.timer.getTimeParts().removeDays());
		}
		if (!this.useHours && this.timer.getTimeParts().getHours() != 0) {
			Log.d(this.getTag(),
					"Removing hours portion from "
							+ this.timer.getTimeParts()
							+ " because corresponding input field is not displayed.");
			this.timer.setTimeParts(this.timer.getTimeParts().removeHours());
		}
		if (!this.useMinutes && this.timer.getTimeParts().getMinutes() != 0) {
			Log.d(this.getTag(),
					"Removing minutes portion from "
							+ this.timer.getTimeParts()
							+ " because corresponding input field is not displayed.");
			this.timer.setTimeParts(this.timer.getTimeParts().removeMinutes());
		}
		if (!this.useSeconds && this.timer.getTimeParts().getSeconds() != 0) {
			Log.d(this.getTag(),
					"Removing seconds portion from "
							+ this.timer.getTimeParts()
							+ " because corresponding input field is not displayed.");
			this.timer.setTimeParts(this.timer.getTimeParts().removeSeconds());
		}
	}

	private void adaptMissingControlsToTime() {
		Log.d(this.getTag(), "adaptMissingControlsToTime()");
		// This first "if" needs to change if we ever support days in the UI.
		// For now it removes the time, just as in adaptTimeToMissingControls
		// instead of enabling the control.
		if (this.timer.getTimeParts().getDays() != 0) {
			Log.d(this.getTag(),
					"Removing days portion from "
							+ this.timer.getTimeParts()
							+ " because corresponding input field is not displayed.");
			this.timer.setTimeParts(this.timer.getTimeParts().removeDays());
		}
		if (!this.useHours && this.timer.getTimeParts().getHours() != 0) {
			Log.d(this.getTag(),
					"Adding hours, minutes and seconds controls because time "
							+ this.timer.getTimeParts() + " has hours.");
			this.useHours = true;
			this.useMinutes = true;
			this.useSeconds = true;
		} else if (!this.useMinutes
				&& this.timer.getTimeParts().getMinutes() != 0) {
			Log.d(this.getTag(),
					"Adding minutes and seconds controls because time "
							+ this.timer.getTimeParts() + " has minutes.");
			this.useMinutes = true;
			this.useSeconds = true;
		} else if (!this.useSeconds
				&& this.timer.getTimeParts().getSeconds() != 0) {
			Log.d(this.getTag(), "Adding seconds controls because time "
					+ this.timer.getTimeParts() + " has seconds.");
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
		Log.d(this.getTag(),
				"starting countdown with " + this.timer.getMillis()
						+ " milliseconds");
		this.getCountdownService().startCountdown(this.timer);
		Log.d(this.getTag(), "countdown started");
		this.saveCurrentStateToPreferences();
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

	private void saveCurrentStateToPreferences() {
		Log.d(this.getTag(), "saveStateToPreferences()");
		this.updateTimerFromControls();
		this.saveToPreferences(this.timer, this.useHours, this.useMinutes,
				this.useSeconds);
	}

	private void saveToPreferences(final Timer timer, final boolean useHours,
			final boolean useMinutes, final boolean useSeconds) {
		Log.d(this.getTag(), "saveToPreferences(): " + timer + ", " + useHours
				+ ", " + useMinutes + ", " + useSeconds);
		doWithEditablePreferences(new PreferenceEditAction() {
			@Override
			public void execute(Editor editor) {
				editor.putLong(getPreferencesKeysValues().keyLastTimerId,
						timer.getId());
				editor.putLong(getPreferencesKeysValues().keyLastTimerMillis,
						timer.getMillis());
				editor.putString(getPreferencesKeysValues().keyLastTimerName,
						timer.getName());
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