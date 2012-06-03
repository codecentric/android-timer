package de.codecentric.android.timer.activity;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import de.codecentric.android.timer.service.ServiceState;
import de.codecentric.android.timer.util.PreferencesKeysValues;

class Navigation {

	private PreferencesKeysValues preferencesKeysValues;
	private SharedPreferences preferences;

	Navigation(Context context, PreferencesKeysValues preferencesKeysValues) {
		this.preferencesKeysValues = preferencesKeysValues;
		this.preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
	}

	private enum NavigationTarget {

		/**
		 * Navigate to an activity to set the timer.
		 */
		SET_TIMER(null),

		/**
		 * Navigate to an activity to show the running countdown.
		 */
		SHOW_COUNTDOWN(ShowCountdownActivity.class),

		/**
		 * Navigate to an activity to show the ringing alarm.
		 */
		SHOW_ALARM(ShowAlarmActivity.class);

		private final Class<? extends CountdownServiceClient> activityClass;

		NavigationTarget(Class<? extends CountdownServiceClient> activityClass) {
			this.activityClass = activityClass;
		}

		Class<? extends CountdownServiceClient> getActivity(Context context) {
			return this.activityClass;
		}
	}

	private static final Map<ServiceState, NavigationTarget> NAVIGATION_RULES;
	static {
		NAVIGATION_RULES = new HashMap<ServiceState, NavigationTarget>();
		NAVIGATION_RULES.put(ServiceState.WAITING, NavigationTarget.SET_TIMER);
		NAVIGATION_RULES.put(ServiceState.FINISHED, NavigationTarget.SET_TIMER);
		NAVIGATION_RULES.put(ServiceState.COUNTING_DOWN,
				NavigationTarget.SHOW_COUNTDOWN);
		NAVIGATION_RULES.put(ServiceState.PAUSED,
				NavigationTarget.SHOW_COUNTDOWN);
		NAVIGATION_RULES.put(ServiceState.BEEPING, NavigationTarget.SHOW_ALARM);
	}

	Class<? extends CountdownServiceClient> getActivityClassForState(
			Context context, ServiceState currentServiceState) {
		NavigationTarget target = NAVIGATION_RULES.get(currentServiceState);
		if (target == null) {
			throw new IllegalStateException(
					"There is no navigation target defined for state "
							+ currentServiceState + ".");
		} else if (target == NavigationTarget.SET_TIMER) {
			// TODO This else-if is ugly. Maybe NavigationTargets shouldn't be
			// enums?
			return this
					.loadSetTimerActivityClassFromPreferences(this.preferences);
		}
		return target.getActivity(context);
	}

	Class<? extends CountdownServiceClient> loadSetTimerActivityClassFromPreferences(
			SharedPreferences preferences) {
		String setTimerPreferenceValue = preferences.getString(
				this.preferencesKeysValues.keySetTimerMethod,
				this.preferencesKeysValues.defaultValueSetTimerMethod);
		if (setTimerPreferenceValue
				.equals(this.preferencesKeysValues.valueSetTimerMethodTextFields)) {
			return SetTimerTextFieldsActivity.class;
		} else {
			// Also return wheels activity if value defined in
			// preferences is illegal for any reason
			return SetTimerWheelsActivity.class;
		}
	}
}
