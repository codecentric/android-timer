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

	private static final String NAME = "test-timer";
	private static final long MILLIS = 123456L;

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
		this.repository.deleteAll();
		assertTrue(this.repository.isEmpty());
	}

	@After
	public void after() {
		this.repository.close();
	}

	@Test
	public void shouldInsert() {
		Timer timer = new Timer(NAME, MILLIS);
		long id = this.repository.insert(timer);
		assertTrue(id >= 0);
		Timer timerFromDb = this.repository.findByName(NAME);
		assertNotNull(timerFromDb);
		assertEquals(timer, timerFromDb);
	}

	@Test
	public void shouldDeleteById() {
		long id = this.repository.insert(new Timer(NAME, MILLIS));
		assertFalse(this.repository.isEmpty());
		this.repository.delete(id);
		assertTrue(this.repository.isEmpty());
	}

	@Test
	public void shouldCreateSampleEntries() {
		this.repository.createSampleEntriesIfEmpty();
		assertDatabaseHasSampleEntries(this.repository);
	}

	private void assertDatabaseHasSampleEntries(TimerRepository repository) {
		assertEquals(TimeParts.FIVE_MINUTES.getMillisecondsTotal(), repository
				.findByName("Five Minutes").getMillis());
		assertEquals(TimeParts.FIFTEEN_MINUTES.getMillisecondsTotal(),
				repository.findByName("Daily Scrum - 15 Minutes").getMillis());
		assertEquals(TimeParts.ONE_HOUR.getMillisecondsTotal(), repository
				.findByName("One Hour").getMillis());
	}

}
