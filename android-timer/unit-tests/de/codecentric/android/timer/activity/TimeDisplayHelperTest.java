package de.codecentric.android.timer.activity;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import android.widget.TextView;

import com.xtremelabs.robolectric.RobolectricTestRunner;

import de.codecentric.android.timer.util.TimePartType;
import de.codecentric.android.timer.util.TimeParts;

@RunWith(RobolectricTestRunner.class)
public class TimeDisplayHelperTest {

	@Mock
	private TextView hours;

	@Mock
	private TextView minutes;

	@Mock
	private TextView seconds;

	private TimeDisplayHelper timeDisplayHelper;

	@Before
	public void before() {
		initMocks(this);
		this.timeDisplayHelper = new TimeDisplayHelper(this.hours,
				this.minutes, this.seconds, true);
	}

	@Test
	public void shouldFormatTimePartsWithLeadingZeroes() {
		// given a time display helper with supress leading zeroes = false
		this.timeDisplayHelper = new TimeDisplayHelper(this.hours,
				this.minutes, this.seconds);
		testFormatting(1, 2, 3, "01", "02", "03");
	}

	@Test
	public void shouldFormatTimePartsWithoutLeadingZeroes1() {
		testFormatting(1, 11, 11, "1", "11", "11");
	}

	@Test
	public void shouldFormatTimePartsWithoutLeadingZeroes2() {
		testFormatting(0, 1, 11, "0", "1", "11");
	}

	@Test
	public void shouldFormatTimePartsWithoutLeadingZeroes3() {
		testFormatting(0, 0, 1, "0", "0", "1");
	}

	@Test
	public void shouldFormatTimePartsWithoutLeadingZeroes4() {
		testFormatting(0, 0, 0, "0", "0", "0");
	}

	@Test
	public void shouldFormatTimePartsWithoutLeadingZeroes5() {
		testFormatting(0, 1, 1, "0", "1", "01");
	}

	@Test
	public void shouldFormatTimePartsWithoutLeadingZeroes6() {
		testFormatting(1, 1, 1, "1", "01", "01");
	}

	@Test
	public void shouldFormatTimePartsWithoutLeadingZeroes7() {
		testFormatting(11, 0, 0, "11", "00", "00");
	}

	private void testFormatting(int hoursInt, int minutesInt, int secondsInt,
			String hoursString, String minutesString, String secondsString) {
		TimeParts timeParts = TimeParts.fromTimeParts(hoursInt, minutesInt,
				secondsInt);
		this.timeDisplayHelper.setTime(timeParts);

		verify(this.hours).setText(hoursString);
		verify(this.minutes).setText(minutesString);
		verify(this.seconds).setText(secondsString);
	}

	@Test
	public void shouldNotRollSecondsIfNotNeccessary() {
		assertThat(
				this.timeDisplayHelper.rollIfNeccessary(0, TimePartType.SECOND),
				is(equalTo(0)));
		assertThat(this.timeDisplayHelper.rollIfNeccessary(59,
				TimePartType.SECOND), is(equalTo(59)));
	}

	@Test
	public void shouldNotRollMinutesIfNotNeccessary() {
		assertThat(
				this.timeDisplayHelper.rollIfNeccessary(0, TimePartType.MINUTE),
				is(equalTo(0)));
		assertThat(this.timeDisplayHelper.rollIfNeccessary(59,
				TimePartType.MINUTE), is(equalTo(59)));
	}

	@Test
	public void shouldNotRollHoursIfNotNeccessary() {
		assertThat(
				this.timeDisplayHelper.rollIfNeccessary(0, TimePartType.HOUR),
				is(equalTo(0)));
		assertThat(
				this.timeDisplayHelper.rollIfNeccessary(23, TimePartType.HOUR),
				is(equalTo(23)));
	}

	@Test
	public void shouldRollSecondsIfNeccessary() {
		assertThat(this.timeDisplayHelper.rollIfNeccessary(-1,
				TimePartType.SECOND), is(equalTo(59)));
		assertThat(this.timeDisplayHelper.rollIfNeccessary(Integer.MIN_VALUE,
				TimePartType.SECOND), is(equalTo(59)));
		assertThat(this.timeDisplayHelper.rollIfNeccessary(60,
				TimePartType.SECOND), is(equalTo(0)));
		assertThat(this.timeDisplayHelper.rollIfNeccessary(Integer.MAX_VALUE,
				TimePartType.SECOND), is(equalTo(0)));
	}

	@Test
	public void shouldRollMinutesIfNeccessary() {
		assertThat(this.timeDisplayHelper.rollIfNeccessary(-1,
				TimePartType.MINUTE), is(equalTo(59)));
		assertThat(this.timeDisplayHelper.rollIfNeccessary(Integer.MIN_VALUE,
				TimePartType.MINUTE), is(equalTo(59)));
		assertThat(this.timeDisplayHelper.rollIfNeccessary(60,
				TimePartType.MINUTE), is(equalTo(0)));
		assertThat(this.timeDisplayHelper.rollIfNeccessary(Integer.MAX_VALUE,
				TimePartType.MINUTE), is(equalTo(0)));
	}

	@Test
	public void shouldRollHoursIfNeccessary() {
		assertThat(
				this.timeDisplayHelper.rollIfNeccessary(-1, TimePartType.HOUR),
				is(equalTo(23)));
		assertThat(this.timeDisplayHelper.rollIfNeccessary(Integer.MIN_VALUE,
				TimePartType.HOUR), is(equalTo(23)));
		assertThat(
				this.timeDisplayHelper.rollIfNeccessary(24, TimePartType.HOUR),
				is(equalTo(0)));
		assertThat(this.timeDisplayHelper.rollIfNeccessary(Integer.MAX_VALUE,
				TimePartType.HOUR), is(equalTo(0)));
	}
}
