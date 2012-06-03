package de.codecentric.android.timer.util;

import static de.codecentric.android.timer.util.TimeParts.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class TimePartsRoundingUpTest {

	@Test(expected = IllegalArgumentException.class)
	public void negative() {
		TimeParts.fromMillisRoundingUp(-1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void tooLarge() {
		TimeParts.fromMillisRoundingUp(TimeParts.MILLIS_PER_YEAR + 1);
	}

	@Test
	public void zero() {
		test(0, 0, 0, 0, 0, 0);
	}

	@Test
	public void oneMillisecond() {
		test(1, 0, 0, 0, 1, 0);
	}

	@Test
	public void nearlyOneSecond() {
		test(MILLIS_PER_SECOND - 1, 0, 0, 0, 1, 0);
	}

	@Test
	public void exactlyOneSecond() {
		test(MILLIS_PER_SECOND, 0, 0, 0, 1, 0);
	}

	@Test
	public void oneSecondAndALittle() {
		test(MILLIS_PER_SECOND + 1, 0, 0, 0, 2, 0);
	}

	@Test
	public void nearlyTwoSeconds() {
		test(1999, 0, 0, 0, 2, 0);
	}

	@Test
	public void exactlyTwoSeconds() {
		test(2000, 0, 0, 0, 2, 0);
	}

	@Test
	public void twoSecondsAndALittle() {
		test(2001, 0, 0, 0, 3, 0);
	}

	@Test
	public void nearlyThreeSeconds() {
		test(2999, 0, 0, 0, 3, 0);
	}

	@Test
	public void nearly0neMinute() {
		test(MILLIS_PER_MINUTE - 1, 0, 0, 1, 0, 0);
	}

	@Test
	public void exactly0neMinute() {
		test(MILLIS_PER_MINUTE, 0, 0, 1, 0, 0);
	}

	@Test
	public void oneMinuteAndALittle() {
		test(MILLIS_PER_MINUTE + 1, 0, 0, 1, 1, 0);
	}

	@Test
	public void nearly0neHour() {
		test(MILLIS_PER_HOUR - 1, 0, 1, 0, 0, 0);
	}

	@Test
	public void exactly0neHour() {
		test(MILLIS_PER_HOUR, 0, 1, 0, 0, 0);
	}

	@Test
	public void oneHourAndALittle() {
		test(MILLIS_PER_HOUR + 1, 0, 1, 0, 1, 0);
	}

	@Test
	public void nearly0neDay() {
		test(MILLIS_PER_DAY - 1, 1, 0, 0, 0, 0);
	}

	@Test
	public void exactly0neDay() {
		test(MILLIS_PER_DAY, 1, 0, 0, 0, 0);
	}

	@Test
	public void oneDayAndALittle() {
		test(MILLIS_PER_DAY + 1, 1, 0, 0, 1, 0);
	}

	private void test(long millisecondsTotal, int days, int hours, int minutes,
			int seconds, int milliseconds) {
		TimeParts timeParts = TimeParts.fromMillisRoundingUp(millisecondsTotal);
		assertEquals(millisecondsTotal, timeParts.getMillisecondsTotal());
		assertEquals(days, timeParts.getDays());
		assertEquals(hours, timeParts.getHours());
		assertEquals(minutes, timeParts.getMinutes());
		assertEquals(seconds, timeParts.getSeconds());
		assertEquals(milliseconds, timeParts.getMilliseconds());
	}
}
