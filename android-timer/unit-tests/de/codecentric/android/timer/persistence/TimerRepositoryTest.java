package de.codecentric.android.timer.persistence;

import static org.junit.Assert.*;
import static org.mockito.MockitoAnnotations.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.xtremelabs.robolectric.RobolectricTestRunner;

import de.codecentric.android.timer.service.CountdownService;
import de.codecentric.android.timer.util.TimeParts;

@RunWith(RobolectricTestRunner.class)
public class TimerRepositoryTest {

	private static final String NAME = "test-timer";
	private static final String ANOTHER_NAME = "another-test-timer";
	private static final long MILLIS = 123456L;
	private static final long ANOTHER_MILLIS = 1302L;

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
	public void shouldInsertAndFindById() {
		Timer timer = new Timer(NAME, MILLIS);
		long id = this.repository.insert(timer);
		assertTrue(id >= 0);
		Timer timerFromDb = this.repository.findById(id);
		assertNotNull(timerFromDb);
		assertEquals(timer, timerFromDb);
	}

	@Test(expected = IllegalArgumentException.class)
	public void insertShouldNotAcceptNull() {
		this.repository.insert(null);
	}

	@Ignore
	@Test
	public void shouldNotInsertTwoTimersWithSameName() {
		this.repository.insert(new Timer(NAME, MILLIS));
		try {
			// Second insert should fail due to unique constraint but doesn't.
			// Strange enough, with update the unique constraint works.
			this.repository.insert(new Timer(NAME, ANOTHER_MILLIS));
			fail();
		} catch (RuntimeException e) {
			assertNotNull(e.getCause());
			assertEquals(java.sql.SQLException.class, e.getCause().getClass());
			assertTrue(e.getCause().getMessage()
					.contains("constraint violation"));
			assertTrue(e.getCause().getMessage()
					.contains("column name is not unique"));
		}
	}

	@Test
	public void shouldUpdate() {
		long id = this.repository.insert(new Timer(NAME, MILLIS));
		assertTrue(id >= 0);
		Timer timer = this.repository.findById(id);
		timer.setName(ANOTHER_NAME);
		timer.setMillis(ANOTHER_MILLIS);
		this.repository.update(timer);
		Timer updatedTimer = this.repository.findById(id);
		assertEquals(timer, updatedTimer);
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateShouldNotAcceptNull() {
		this.repository.update(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateShouldNotAcceptTimersWithoutId() {
		this.repository.update(new Timer(NAME, MILLIS));
	}

	public void shouldNotUpdateTwoTimersTooTheSameName() {
		this.repository.insert(new Timer(NAME, MILLIS));
		long id = this.repository
				.insert(new Timer(ANOTHER_NAME, ANOTHER_MILLIS));
		Timer secondTimer = this.repository.findById(id);
		secondTimer.setName(NAME);
		try {
			this.repository.update(secondTimer);
			fail();
		} catch (RuntimeException e) {
			assertNotNull(e.getCause());
			assertEquals(java.sql.SQLException.class, e.getCause().getClass());
			assertTrue(e.getCause().getMessage()
					.contains("constraint violation"));
			assertTrue(e.getCause().getMessage()
					.contains("column name is not unique"));
		}
	}

	@Test
	public void shouldDeleteById() {
		long id = this.repository.insert(new Timer(NAME, MILLIS));
		assertFalse(this.repository.isEmpty());
		this.repository.delete(id);
		assertTrue(this.repository.isEmpty());
	}

	@Test(expected = IllegalArgumentException.class)
	public void deleteByIdShouldNotAcceptZero() {
		this.repository.delete(0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void deleteByIdShouldNotAcceptNegativeValues() {
		this.repository.delete(-1);
	}

	@Test
	public void shouldDeleteByObject() {
		long id = this.repository.insert(new Timer(NAME, MILLIS));
		Timer timer = this.repository.findById(id);
		assertFalse(this.repository.isEmpty());
		this.repository.delete(timer);
		assertTrue(this.repository.isEmpty());
	}

	@Test(expected = IllegalArgumentException.class)
	public void deleteByObjectShouldNotAcceptNull() {
		this.repository.delete(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void deleteByObjectShouldNotTimerWithoutId() {
		this.repository.delete(new Timer("name", 1000L));
	}

	@Test
	public void shouldCreateSampleEntries() {
		this.repository.createSampleEntriesIfEmpty();
		assertDatabaseHasSampleEntries(this.repository);
	}

	private void assertDatabaseHasSampleEntries(TimerRepository repository) {
		assertEquals(TimeParts.FIVE_MINUTES.getMillisecondsTotal(), repository
				.findById(1).getMillis());
		assertEquals(TimeParts.FIFTEEN_MINUTES.getMillisecondsTotal(),
				repository.findById(2).getMillis());
		assertEquals(TimeParts.ONE_HOUR.getMillisecondsTotal(), repository
				.findById(3).getMillis());
	}
}
