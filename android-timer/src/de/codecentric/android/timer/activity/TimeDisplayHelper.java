package de.codecentric.android.timer.activity;

import java.text.NumberFormat;

import de.codecentric.android.timer.util.TimePartType;
import de.codecentric.android.timer.util.TimeParts;

import android.widget.EditText;
import android.widget.TextView;

class TimeDisplayHelper {

	private final TextView hours;
	private final TextView minutes;
	private final TextView seconds;
	private boolean supressLeadingZeroOnHighestNonNullTimePart;

	TimeDisplayHelper(TextView hours, TextView minutes, TextView seconds) {
		this(hours, minutes, seconds, false);
	}

	TimeDisplayHelper(TextView hours, TextView minutes, TextView seconds,
			boolean supressLeadingZeroOnHighestNonNullTimePart) {
		this.hours = hours;
		this.minutes = minutes;
		this.seconds = seconds;
		this.supressLeadingZeroOnHighestNonNullTimePart = supressLeadingZeroOnHighestNonNullTimePart;
	}

	private final NumberFormat numberFormatTwoDigits;
	{
		this.numberFormatTwoDigits = NumberFormat.getInstance();
		this.numberFormatTwoDigits.setMaximumIntegerDigits(2);
		this.numberFormatTwoDigits.setMinimumIntegerDigits(2);
		this.numberFormatTwoDigits.setGroupingUsed(false);
		this.numberFormatTwoDigits.setMaximumFractionDigits(0);
		this.numberFormatTwoDigits.setMinimumFractionDigits(0);
	}

	void setTime(TimeParts timeParts) {
		boolean supressLeadingZero = this.supressLeadingZeroOnHighestNonNullTimePart;
		this.setValueInTextView2(this.hours, timeParts.getHours(),
				supressLeadingZero);
		supressLeadingZero &= timeParts.getHours() == 0;
		this.setValueInTextView2(this.minutes, timeParts.getMinutes(),
				supressLeadingZero);
		supressLeadingZero &= timeParts.getMinutes() == 0;
		this.setValueInTextView2(this.seconds, timeParts.getSeconds(),
				supressLeadingZero);
	}

	protected int getValueFromTextView(TextView textView) {
		String valueAsString = textView.getText().toString();
		return this.stringToInt(valueAsString);
	}

	void setValueInTextView(TextView textView, int value) {
		this.setValueInTextView2(textView, value, false);
	}

	private void setValueInTextView2(TextView textView, int value,
			boolean supressLeadingZeroes) {
		if (supressLeadingZeroes) {
			textView.setText(String.valueOf(value));
		} else {
			textView.setText(this.numberFormatTwoDigits.format(value));
		}
	}

	private int stringToInt(String string) {
		if (string == null) {
			return 0;
		}
		try {
			return Integer.parseInt(string);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	long getTimeAsMillis() {
		TimeParts timeParts = this.getTimePartsFromEditTextFields();
		return timeParts.getMillisecondsTotal();
	}

	protected TimeParts getTimePartsFromEditTextFields() {
		int hours = this.getValueFromTextView(this.hours);
		int minutes = this.getValueFromTextView(this.minutes);
		int seconds = this.getValueFromTextView(this.seconds);
		return TimeParts.fromTimeParts(hours, minutes, seconds);
	}

	void formatAndRollValue(EditText editText, TimePartType timePartType) {
		int value = this.getValueFromTextView(editText);
		value = this.rollIfNeccessary(value, timePartType);
		this.setValueInTextView(editText, value);
	}

	int rollIfNeccessary(int value, TimePartType timePartType) {
		int min = 0;
		int max;
		switch (timePartType) {
		case HOUR:
			max = 23;
			break;
		case MINUTE:
			// fall through
		case SECOND:
			max = 59;
			break;
		default:
			throw new RuntimeException("Unknown TimePartType: " + timePartType);
		}
		if (value < min) {
			return max;
		}
		if (value > max) {
			return min;
		}
		return value;
	}
}
