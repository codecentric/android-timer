package de.codecentric.android.timer.persistence;

import static org.junit.Assert.*;
import static org.mockito.MockitoAnnotations.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.database.sqlite.SQLiteDatabase;

import com.xtremelabs.robolectric.RobolectricTestRunner;

import de.codecentric.android.timer.persistence.DbAccess.DatabaseAction;
import de.codecentric.android.timer.service.CountdownService;

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
		fillDatabase(this.repository);
		assertDatabaseHasCorrectValues(this.repository);
	}

	private void fillDatabase(final TimerRepository repository) {
		repository.doInTransaction(new DatabaseAction() {
			@Override
			public void execute(SQLiteDatabase db) {
				repository.insert(new Timer("timer-01", 30000));
				repository.insert(new Timer("timer-02", 60000));
				repository.insert(new Timer("timer-03", 12000));
				repository.insert(new Timer("timer-04", 9000));
			}
		});
	}

	private void assertDatabaseHasCorrectValues(TimerRepository repository) {
		assertEquals(30000, repository.findByName("timer-01").getMillis());
		assertEquals(60000, repository.findByName("timer-02").getMillis());
		assertEquals(12000, repository.findByName("timer-03").getMillis());
		assertEquals(9000, repository.findByName("timer-04").getMillis());
	}
}
