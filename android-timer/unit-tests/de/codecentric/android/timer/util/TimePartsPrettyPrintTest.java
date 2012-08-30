package de.codecentric.android.timer.util;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class TimePartsPrettyPrintTest {

	@Test
	public void zero() {
		test(0, 0, 0, 0, 0, "00:00:00");
	}

	@Test
	public void seconds() {
		test(0, 0, 0, 42, 0, "00:00:42");
	}

	@Test
	public void minutes() {
		test(0, 0, 13, 42, 0, "00:13:42");
	}

	@Test
	public void hours() {
		test(0, 23, 13, 42, 0, "23:13:42");
	}

	@Test
	public void oneDay() {
		test(1, 23, 13, 42, 0, "1 day, 23:13:42");
	}

	@Test
	public void severalDays() {
		test(3, 23, 13, 42, 0, "3 days, 23:13:42");
	}

	@Test
	public void millisecondsAreOmmitted() {
		test(0, 0, 0, 0, 999, "00:00:00");
	}

	private void test(int days, int hours, int minutes, int seconds,
			int milliseconds, String expected) {
		assertThat(
				TimeParts.fromTimeParts(days, hours, minutes, seconds,
						milliseconds).prettyPrint(), is(equalTo(expected)));
	}
}
