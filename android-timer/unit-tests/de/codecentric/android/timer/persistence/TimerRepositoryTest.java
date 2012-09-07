package de.codecentric.android.timer.persistence;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.MockitoAnnotations.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.database.Cursor;

import com.xtremelabs.robolectric.RobolectricTestRunner;

import de.codecentric.android.timer.persistence.DbAccess.DatabaseCursorAction;
import de.codecentric.android.timer.service.CountdownService;
import de.codecentric.android.timer.util.TimeParts;

@RunWith(RobolectricTestRunner.class)
public class TimerRepositoryTest {

	private static final String NAME = "test-timer";
	private static final String ANOTHER_NAME = "another-test-timer";
	private static final long MILLIS = 123L;
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

	@Test
	public void shouldInsertTwoTimersWithSameName() {
		this.repository.insert(new Timer(NAME, MILLIS));
		this.repository.insert(new Timer(NAME, ANOTHER_MILLIS));
		checkTwoTimersWithSameName();
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

	@Test
	public void shouldUpdateTwoTimersTooTheSameName() {
		this.repository.insert(new Timer(NAME, MILLIS));
		long id = this.repository
				.insert(new Timer(ANOTHER_NAME, ANOTHER_MILLIS));
		Timer secondTimer = this.repository.findById(id);
		secondTimer.setName(NAME);
		this.repository.update(secondTimer);

		checkTwoTimersWithSameName();
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

	private void checkTwoTimersWithSameName() {
		Cursor allTimers = this.repository
				.findAllTimers(Db.TimerTable.Columns.MILLIS);
		this.repository.doWithCursor(allTimers, new DatabaseCursorAction() {

			@Override
			public void execute(Cursor cursor) {
				assertThat(cursor.getCount(), is(equalTo(2)));
				cursor.moveToFirst();
				Timer timer1 = repository.readTimerFromCursor(cursor);
				cursor.moveToNext();
				Timer timer2 = repository.readTimerFromCursor(cursor);
				assertThat(timer1.getName(), is(equalTo(NAME)));
				assertThat(timer2.getName(), is(equalTo(NAME)));
				assertThat(timer1.getMillis(), is(equalTo(MILLIS)));
				assertThat(timer2.getMillis(), is(equalTo(ANOTHER_MILLIS)));
			}
		});
	}
}
