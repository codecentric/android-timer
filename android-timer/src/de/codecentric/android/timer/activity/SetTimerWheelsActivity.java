package de.codecentric.android.timer.activity;

import kankan.wheel.widget.WheelView;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import de.codecentric.android.timer.R;
import de.codecentric.android.timer.util.TimeParts;
import de.codecentric.android.timer.view.DirectedNumericWheelAdapter;
import de.codecentric.android.timer.view.DirectedNumericWheelAdapter.Direction;

public class SetTimerWheelsActivity extends AbstractSetTimerActivity {

	private static final String TAG = SetTimerWheelsActivity.class.getName();

	private Direction wheelDirection;

	private WheelView wheelHours;
	private DirectedNumericWheelAdapter wheelAdapterHours;
	private WheelView wheelMinutes;
	private DirectedNumericWheelAdapter wheelAdapterMinutes;
	private WheelView wheelSeconds;
	private DirectedNumericWheelAdapter wheelAdapterSeconds;

	private Button buttonResetHours;
	private Button buttonResetMinutes;
	private Button buttonResetSeconds;

	@Override
	protected int getViewId() {
		return R.layout.set_timer_wheels;
	}

	@Override
	protected void loadPreferencesForSubclass(SharedPreferences preferences) {
		Log.d(this.getTag(), "loadPreferencesForSubclass");
		String wheelDirectionAsString = preferences.getString(
				this.getPreferencesKeysValues().keyWheelDirection,
				this.getPreferencesKeysValues().defaultValueWheelDirection);
		this.wheelDirection = Direction.DESCENDING;
		if (wheelDirectionAsString
				.equals(this.getPreferencesKeysValues().valueWheelDirectionAscending)) {
			this.wheelDirection = Direction.ASCENDING;
		}
	}

	@Override
	protected void fetchViewObjectsForTimeInputControls() {
		this.buttonResetHours = (Button) super
				.findViewById(R.id.buttonResetHours);
		this.buttonResetMinutes = (Button) super
				.findViewById(R.id.buttonResetMinutes);
		this.buttonResetSeconds = (Button) super
				.findViewById(R.id.buttonResetSeconds);
	}

	@Override
	protected void configureTimeInputControls() {
		this.configureWheels();
		this.configureResetButtons();
	}

	private void configureWheels() {
		this.wheelAdapterHours = this.createWheelAdapter(23,
				this.wheelDirection);
		this.wheelAdapterMinutes = this.createWheelAdapter(59,
				this.wheelDirection);
		this.wheelAdapterSeconds = this.createWheelAdapter(59,
				this.wheelDirection);
		this.wheelHours = this.configureWheel(R.id.wheelHours,
				this.wheelAdapterHours, 23);
		this.wheelMinutes = this.configureWheel(R.id.wheelMinutes,
				this.wheelAdapterMinutes, 59);
		this.wheelSeconds = this.configureWheel(R.id.wheelSeconds,
				this.wheelAdapterSeconds, 59);
	}

	private DirectedNumericWheelAdapter createWheelAdapter(int maxValue,
			Direction direction) {
		return new DirectedNumericWheelAdapter(this, 0, maxValue, "%02d",
				direction);
	}

	private WheelView configureWheel(int id,
			final DirectedNumericWheelAdapter adapter, int maxValue) {
		final WheelView wheel = (WheelView) findViewById(id);
		wheel.setViewAdapter(adapter);
		wheel.setCyclic(true);
		wheel.setVisibleItems(maxValue);
		// wheel.addLongClickingListener(new OnWheelLongClickedListener() {
		// @Override
		// public void onItemLongClicked(WheelView wheel, int itemIndex) {
		// wheel.setCurrentItem(adapter.valueToIndex(0));
		// }
		// });
		return wheel;
	}

	private void configureResetButtons() {
		this.configureResetButton(this.buttonResetHours, this.wheelHours,
				this.wheelAdapterHours);
		this.configureResetButton(this.buttonResetMinutes, this.wheelMinutes,
				this.wheelAdapterMinutes);
		this.configureResetButton(this.buttonResetSeconds, this.wheelSeconds,
				this.wheelAdapterSeconds);
	}

	private void configureResetButton(final Button buttonReset,
			final WheelView wheel,
			final DirectedNumericWheelAdapter wheelAdapter) {
		buttonReset.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				wheel.setCurrentItem(wheelAdapter.valueToIndex(0));
			}
		});
	}

	@Override
	protected TimeParts getTimePartsFromControls() {
		int hours = this.wheelAdapterHours.indexToValue(wheelHours
				.getCurrentItem());
		int minutes = this.wheelAdapterMinutes.indexToValue(this.wheelMinutes
				.getCurrentItem());
		int seconds = this.wheelAdapterSeconds.indexToValue(this.wheelSeconds
				.getCurrentItem());
		Log.d(TAG, "getTimePartsFromControls(), returning: " + hours + ":"
				+ minutes + ":" + seconds);
		return TimeParts.fromTimeParts(hours, minutes, seconds);
	}

	@Override
	protected void setTime(TimeParts timeParts) {
		Log.d(TAG, "setTime(" + timeParts + ")");
		this.wheelHours.setCurrentItem(this.wheelAdapterHours
				.valueToIndex(timeParts.getHours()));
		this.wheelMinutes.setCurrentItem(this.wheelAdapterMinutes
				.valueToIndex(timeParts.getMinutes()));
		this.wheelSeconds.setCurrentItem(this.wheelAdapterSeconds
				.valueToIndex(timeParts.getSeconds()));
	}

	@Override
	protected void showAndHideTimeInputControls(boolean useHours,
			boolean useMinutes, boolean useSeconds) {
		setVisibilty(useHours, this.wheelHours, this.buttonResetHours);
		setVisibilty(useMinutes, this.wheelMinutes, this.buttonResetMinutes);
		setVisibilty(useSeconds, this.wheelSeconds, this.buttonResetSeconds);
	}

	private void setVisibilty(boolean use, WheelView wheel, Button buttonReset) {
		wheel.setVisibility(this.flagToVisibility(use));
		buttonReset.setVisibility(this.flagToVisibility(use));
	}

	@Override
	protected String getTag() {
		return TAG;
	}
}
