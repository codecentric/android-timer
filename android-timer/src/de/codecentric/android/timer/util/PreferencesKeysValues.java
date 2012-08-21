package de.codecentric.android.timer.util;

import android.content.Context;
import de.codecentric.android.timer.R;

public class PreferencesKeysValues {

	public final String keyLastTimer;

	public final String keyUseHoursInput;
	public final String keyUseMinutesInput;
	public final String keyUseSecondsInput;
	public final boolean defaultValueUseHoursInput;
	public final boolean defaultValueUseMinutesInput;
	public final boolean defaultValueUseSecondsInput;

	public final String keySetTimerMethod;
	public final String valueSetTimerMethodWheels;
	public final String valueSetTimerMethodTextFields;
	public final String defaultValueSetTimerMethod;

	public final String keyWheelDirection;
	public final String valueWheelDirectionDescending;
	public final String valueWheelDirectionAscending;
	public final String defaultValueWheelDirection;

	public final String keyKeepDisplayOn;
	public final boolean defaultValueKeepDisplayOn;

	public final String keyTapAnywhereToPause;
	public final boolean defaultValueTapAnywhereToPause;

	public final String keyShowCountdownOptions;
	public final String valueShowCountdownOptionsPieChartAndText;
	public final String valueShowCountdownOptionsPieChartOnly;
	public final String valueShowCountdownOptionsTextOnly;
	public final String defaultValueShowCountdownOptions;

	public final int defaultValueAlarmDuration;
	public final String keyAlarmDuration;

	public PreferencesKeysValues(Context context) {
		this.keyLastTimer = context.getString(R.string.key_last_timer);

		this.keyUseHoursInput = context.getString(R.string.key_use_hours);
		this.keyUseMinutesInput = context.getString(R.string.key_use_minutes);
		this.keyUseSecondsInput = context.getString(R.string.key_use_seconds);
		this.defaultValueUseHoursInput = context.getResources().getBoolean(
				R.bool.default_value_use_hours);
		this.defaultValueUseMinutesInput = context.getResources().getBoolean(
				R.bool.default_value_use_minutes);
		this.defaultValueUseSecondsInput = context.getResources().getBoolean(
				R.bool.default_value_use_seconds);

		this.keySetTimerMethod = context
				.getString(R.string.key_set_timer_method);
		this.valueSetTimerMethodWheels = context
				.getString(R.string.value_set_timer_method_wheels);
		this.valueSetTimerMethodTextFields = context
				.getString(R.string.value_set_timer_method_text_fields);
		this.defaultValueSetTimerMethod = context
				.getString(R.string.default_value_set_timer_method);

		this.keyWheelDirection = context
				.getString(R.string.key_wheel_direction);
		this.valueWheelDirectionDescending = context
				.getString(R.string.value_wheel_direction_descending);
		this.valueWheelDirectionAscending = context
				.getString(R.string.value_wheel_direction_ascending);
		this.defaultValueWheelDirection = context
				.getString(R.string.default_value_wheel_direction);

		this.keyKeepDisplayOn = context.getString(R.string.key_keep_display_on);
		this.defaultValueKeepDisplayOn = context.getResources().getBoolean(
				R.bool.default_value_keep_display_on);

		this.keyShowCountdownOptions = context
				.getString(R.string.key_show_countdown_options);
		this.valueShowCountdownOptionsPieChartAndText = context
				.getString(R.string.value_show_countdown_as_pie_chart_and_text);
		this.valueShowCountdownOptionsPieChartOnly = context
				.getString(R.string.value_show_countdown_as_pie_chart_only);
		this.valueShowCountdownOptionsTextOnly = context
				.getString(R.string.value_show_countdown_as_text_only);
		this.defaultValueShowCountdownOptions = context
				.getString(R.string.default_value_show_countdown_options);

		this.keyTapAnywhereToPause = context
				.getString(R.string.key_tap_anywhere_to_pause);
		this.defaultValueTapAnywhereToPause = context.getResources()
				.getBoolean(R.bool.default_value_tap_anywhere_to_pause);

		this.defaultValueAlarmDuration = context.getResources().getInteger(
				R.integer.default_value_alarm_duration);
		this.keyAlarmDuration = context.getResources().getString(
				R.string.key_alarm_duration);
	}
}
