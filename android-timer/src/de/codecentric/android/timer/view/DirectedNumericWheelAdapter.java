package de.codecentric.android.timer.view;

import kankan.wheel.widget.adapters.NumericWheelAdapter;
import android.content.Context;

public class DirectedNumericWheelAdapter extends NumericWheelAdapter {

	public enum Direction {

		/**
		 * Scrolling the wheel down yields higher values, scrolling up yields
		 * lower values.
		 */
		ASCENDING,

		/**
		 * Scrolling the wheel down yields lower values, scrolling up yields
		 * higher values.
		 */
		DESCENDING;
	}

	private Direction direction;

	public DirectedNumericWheelAdapter(Context context) {
		super(context);
		this.direction = Direction.ASCENDING;
	}

	public DirectedNumericWheelAdapter(Context context, int minValue,
			int maxValue) {
		super(context, minValue, maxValue);
		this.direction = Direction.ASCENDING;
	}

	public DirectedNumericWheelAdapter(Context context, int minValue,
			int maxValue, String format) {
		super(context, minValue, maxValue, format);
		this.direction = Direction.ASCENDING;
	}

	public DirectedNumericWheelAdapter(Context context, Direction direction) {
		super(context);
		this.direction = direction;
	}

	public DirectedNumericWheelAdapter(Context context, int minValue,
			int maxValue, Direction direction) {
		super(context, minValue, maxValue);
		this.direction = direction;
	}

	public DirectedNumericWheelAdapter(Context context, int minValue,
			int maxValue, String format, Direction direction) {
		super(context, minValue, maxValue, format);
		this.direction = direction;
	}

	@Override
	public CharSequence getItemText(int index) {
		return super.getItemText(indexToValue(index));
	}

	/**
	 * Use this method to post-process the values returned by getCurrentItem
	 * 
	 * @param index
	 * @return
	 */
	public int indexToValue(int index) {
		switch (this.direction) {
		case ASCENDING:
			return index;
		case DESCENDING:
			return super.getItemsCount() - 1 - index;

		default:
			throw new IllegalStateException("Unknown direction: " + direction);
		}
	}

	/**
	 * Use this to pre-process values that will be passed to setCurrentItem.
	 * 
	 * @param value
	 * @return
	 */
	public int valueToIndex(int value) {
		switch (this.direction) {
		case ASCENDING:
			return value;
		case DESCENDING:
			return super.getItemsCount() - value - 1;

		default:
			throw new IllegalStateException("Unknown direction: " + direction);
		}

	}
}