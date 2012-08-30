package de.codecentric.android.timer.persistence;

import static org.junit.Assert.*;
import static org.mockito.MockitoAnnotations.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.xtremelabs.robolectric.RobolectricTestRunner;

import de.codecentric.android.timer.service.CountdownService;
import de.codecentric.android.timer.util.TimeParts;

@RunWith(RobolectricTestRunner.class)
public class TimerRepositoryTest {

	private CountdownService countdownService;
	private TimerRepository repository;

	@Before
	public void before() {
		this.countdownService = new CountdownService();
		initMocks(this);
		this.countdownService.onCreate();

		TimerDatabaseOpenHelper helper = new TimerDatabaseOpenHelper(
				this.countdownService);
		this.repository = new TimerRepository(helper);
	}

	@After
	public void after() {
		this.repository.close();
	}

	@Test
	public void shouldReadAndWriteFromDatabase() {
		this.repository.deleteAll();
		assertTrue(this.repository.isEmpty());
		this.repository.createSampleEntriesIfEmpty();
		assertDatabaseHasCorrectValues(this.repository);
	}

	private void assertDatabaseHasCorrectValues(TimerRepository repository) {
		assertEquals(TimeParts.FIVE_MINUTES.getMillisecondsTotal(), repository
				.findByName("Five Minutes").getMillis());
		assertEquals(TimeParts.FIFTEEN_MINUTES.getMillisecondsTotal(),
				repository.findByName("Daily Scrum - 15 Minutes").getMillis());
		assertEquals(TimeParts.ONE_HOUR.getMillisecondsTotal(), repository
				.findByName("One Hour").getMillis());
	}
}
