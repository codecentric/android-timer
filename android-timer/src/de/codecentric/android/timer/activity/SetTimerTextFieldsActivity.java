package de.codecentric.android.timer.activity;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import de.codecentric.android.timer.R;
import de.codecentric.android.timer.util.TimePartType;
import de.codecentric.android.timer.util.TimeParts;

public class SetTimerTextFieldsActivity extends AbstractSetTimerActivity {

	private static final String TAG = SetTimerTextFieldsActivity.class
			.getName();

	private EditText editTextHours;
	private EditText editTextMinutes;
	private EditText editTextSeconds;

	private TimeDisplayHelper timeDisplay;

	@Override
	protected int getViewId() {
		return R.layout.set_timer_text_fields;
	}

	@Override
	protected void configureTimeInputControls() {
		this.configureEditTextFields();
		this.configurePlusMinusButtons();
	}

	private void configureEditTextFields() {
		this.editTextHours = (EditText) findViewById(R.id.editTextHours);
		this.configureFocusListener(this.editTextHours, TimePartType.HOUR);
		this.editTextMinutes = (EditText) findViewById(R.id.editTextMinutes);
		this.configureFocusListener(this.editTextMinutes, TimePartType.MINUTE);
		this.editTextSeconds = (EditText) findViewById(R.id.editTextSeconds);
		this.configureFocusListener(this.editTextSeconds, TimePartType.SECOND);
		this.timeDisplay = new TimeDisplayHelper(this.editTextHours,
				this.editTextMinutes, this.editTextSeconds);
	}

	private void configureFocusListener(final EditText editText,
			final TimePartType timePartType) {
		editText.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean hasFocus) {
				if (!hasFocus) {
					timeDisplay.formatAndRollValue(editText, timePartType);
				}
			}
		});
	}

	private void configurePlusMinusButtons() {
		this.configureClickListenerForPlusMinusButton(
				(Button) this.findViewById(R.id.buttonPlusHours),
				this.editTextHours, +1, TimePartType.HOUR);
		this.configureClickListenerForPlusMinusButton(
				(Button) this.findViewById(R.id.buttonPlusMinutes),
				this.editTextMinutes, +1, TimePartType.MINUTE);
		this.configureClickListenerForPlusMinusButton(
				(Button) this.findViewById(R.id.buttonPlusSeconds),
				this.editTextSeconds, +1, TimePartType.SECOND);
		this.configureClickListenerForPlusMinusButton(
				(Button) this.findViewById(R.id.buttonMinusHours),
				this.editTextHours, -1, TimePartType.HOUR);
		this.configureClickListenerForPlusMinusButton(
				(Button) this.findViewById(R.id.buttonMinusMinutes),
				this.editTextMinutes, -1, TimePartType.MINUTE);
		this.configureClickListenerForPlusMinusButton(
				(Button) this.findViewById(R.id.buttonMinusSeconds),
				this.editTextSeconds, -1, TimePartType.SECOND);
	}

	private void configureClickListenerForPlusMinusButton(Button button,
			final EditText editText, final int increaseDecrease,
			final TimePartType timePartType) {
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				onPlusMinusClick(editText, increaseDecrease, timePartType);
			}
		});
	}

	private void onPlusMinusClick(final EditText editText,
			final int increaseDecrease, TimePartType timePartType) {
		int value = this.timeDisplay.getValueFromTextView(editText);
		value += increaseDecrease;
		value = this.timeDisplay.rollIfNeccessary(value, timePartType);
		this.timeDisplay.setValueInTextView(editText, value);
	}

	@Override
	protected TimeParts getTimePartsFromControls() {
		return this.timeDisplay.getTimePartsFromEditTextFields();
	}

	@Override
	protected void setTime(TimeParts timeParts) {
		this.timeDisplay.setTime(timeParts);
	}

	@Override
	protected void showAndHideTimeInputControls(boolean useHours,
			boolean useMinutes, boolean useSeconds) {
		this.setVisibility(useHours, this.editTextHours, R.id.buttonPlusHours,
				R.id.buttonMinusHours);
		this.setVisibility(useMinutes, this.editTextMinutes,
				R.id.buttonPlusMinutes, R.id.buttonMinusMinutes);
		this.setVisibility(useSeconds, this.editTextSeconds,
				R.id.buttonPlusSeconds, R.id.buttonMinusSeconds);
	}

	private void setVisibility(boolean use, EditText editText, int idButtonUp,
			int idButtonDown) {
		editText.setVisibility(flagToVisibility(use));
		this.findViewById(idButtonUp).setVisibility(flagToVisibility(use));
		this.findViewById(idButtonDown).setVisibility(flagToVisibility(use));
	}

	@Override
	protected String getTag() {
		return TAG;
	}
}
