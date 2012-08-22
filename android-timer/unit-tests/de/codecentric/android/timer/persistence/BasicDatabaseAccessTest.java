package de.codecentric.android.timer.persistence;

import static de.codecentric.android.timer.persistence.DbAccess.*;
import static org.junit.Assert.*;
import static org.mockito.MockitoAnnotations.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xtremelabs.robolectric.RobolectricTestRunner;

import de.codecentric.android.timer.persistence.DbAccess.DatabaseAction;
import de.codecentric.android.timer.service.CountdownService;

@RunWith(RobolectricTestRunner.class)
public class BasicDatabaseAccessTest {

	private CountdownService countdownService;

	@Before
	public void before() {
		this.countdownService = new CountdownService();
		initMocks(this);
		this.countdownService.onCreate();
	}

	@Test
	public void shouldReadAndWriteFromDatabase() {
		this.exerciseTheDatabase(this.countdownService);
	}

	private void exerciseTheDatabase(Context context) {
		TimerDatabaseOpenHelper h = new TimerDatabaseOpenHelper(context);
		SQLiteDatabase db = h.getWritableDatabase();
		doWithDatabaseConnection(db, new DatabaseAction() {
			@Override
			public void execute(SQLiteDatabase db) {
				cleanDatabase(db);
				assertDatabaseIsEmpty(db);
				fillDatabase(db);
				assertDatabaseHasCorrectValues(db);
			}
		});
	}

	private void cleanDatabase(SQLiteDatabase db) {
		doInTransaction(db, new DatabaseAction() {
			@Override
			public void execute(SQLiteDatabase db) {
				db.delete(Db.Timer.TABLE_NAME, null, null);
			}
		});
	}

	private void assertDatabaseIsEmpty(SQLiteDatabase db) {
		Cursor cursor = db.query(Db.Timer.TABLE_NAME,
				new String[] { Db.Timer.Columns.MILLIS }, null, null, null,
				null, null);
		assertEquals(0, cursor.getCount());
	}

	private void fillDatabase(SQLiteDatabase db) {
		doInTransaction(db, new DatabaseAction() {
			@Override
			public void execute(SQLiteDatabase db) {
				insert(db, "timer-01", 30000);
				insert(db, "timer-02", 60000);
				insert(db, "timer-03", 12000);
				insert(db, "timer-04", 9000);
			}
		});
	}

	private void insert(SQLiteDatabase db, String name, long millis) {
		ContentValues values = new ContentValues(3);
		values.put(Db.Timer.Columns.NAME, name);
		values.put(Db.Timer.Columns.MILLIS, millis);
		db.insertOrThrow(Db.Timer.TABLE_NAME, null, values);
	}

	private void assertDatabaseHasCorrectValues(SQLiteDatabase db) {
		assertEquals(30000, this.find(db, "timer-01"));
		assertEquals(60000, this.find(db, "timer-02"));
		assertEquals(12000, this.find(db, "timer-03"));
		assertEquals(9000, this.find(db, "timer-04"));
	}

	private long find(SQLiteDatabase db, String name) {
		Cursor cursor = db.query(Db.Timer.TABLE_NAME,
				new String[] { Db.Timer.Columns.MILLIS }, Db.Timer.Columns.NAME
						+ " = ?", new String[] { name }, null, null, null);
		assertFalse(cursor.getCount() == 0);
		cursor.moveToFirst();
		return cursor.getLong(0);
	}
}
