package de.codecentric.android.timer.persistence;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import de.codecentric.android.timer.persistence.DbAccess.DatabaseAction;
import de.codecentric.android.timer.persistence.DbAccess.DatabaseCursorAction;
import de.codecentric.android.timer.persistence.DbAccess.DatabaseCursorResultAction;
import de.codecentric.android.timer.util.TimeParts;

/**
 * Loads and saves timers from the database.
 * 
 * @author Bastian Krol
 */
public class TimerRepository {

	private SQLiteDatabase db;

	public TimerRepository(TimerDatabaseOpenHelper helper) {
		this.db = helper.getWritableDatabase();
	}

	/**
	 * @return {@code true} iff the database is empty
	 */
	public boolean isEmpty() {
		Cursor cursor = this.db.query(Db.TimerTable.TABLE_NAME,
				new String[] { Db.TimerTable.Columns.MILLIS.name }, null, null,
				null, null, null);
		boolean isEmpty = cursor.getCount() == 0;
		cursor.close();
		return isEmpty;
	}

	/**
	 * Returns a cursor that delivers all timers that are currently in the
	 * database, ordered by name.
	 * 
	 * @return a Cursor object that delivers all timers that are currently in
	 *         the database, ordered by name.
	 */
	public Cursor findAllTimers() {
		return this.findAllTimers(Db.TimerTable.Columns.NAME);
	}

	/**
	 * Returns a cursor that delivers all timers that are currently in the
	 * database, ordered by the given column.
	 * 
	 * @return a Cursor object that (if fully read) delivers all timers that are
	 *         currently in the database. Should to be closed when reading from
	 *         the cursor is finished.
	 */
	public Cursor findAllTimers(Db.TimerTable.Columns orderBy) {
		if (orderBy == null) {
			throw new IllegalArgumentException("orderBy must not be null");
		}
		return this.db.query(Db.TimerTable.TABLE_NAME, new String[] {
				Db.TimerTable.Columns.ID.name, Db.TimerTable.Columns.NAME.name,
				Db.TimerTable.Columns.MILLIS.name,
				Db.TimerTable.Columns.SOUND.name }, null, null, null, null,
				orderBy.name);
	}

	public Timer findById(long id) {
		Cursor cursor = this.db.query(Db.TimerTable.TABLE_NAME, new String[] {
				Db.TimerTable.Columns.ID.name, Db.TimerTable.Columns.NAME.name,
				Db.TimerTable.Columns.MILLIS.name },
				Db.TimerTable.Columns.ID.name + " = ?",
				new String[] { String.valueOf(id) }, null, null, null);
		return this.findSingleResult(cursor,
				"Found more than one timer with id " + id + ".");
	}

	private Timer findSingleResult(final Cursor cursor,
			final String messageForMultipleResults) {
		return this.doWithCursorForResult(cursor,
				new DatabaseCursorResultAction<Timer>() {
					@Override
					public Timer execute(Cursor cursor) {
						if (cursor.getCount() == 0) {
							return null;
						} else if (cursor.getCount() > 1) {
							throw new IllegalStateException(
									messageForMultipleResults);
						}
						cursor.moveToFirst();
						return readTimerFromCursor(cursor);
					}
				});
	}

	public Timer readTimerFromCursor(Cursor cursor) {
		return new Timer(cursor.getInt(0), cursor.getString(1),
				cursor.getLong(2));
	}

	/**
	 * Create some entries in the database so it's not so empty.
	 */
	public void createSampleEntriesIfEmpty() {
		if (isEmpty()) {
			this.doInTransaction(new DatabaseAction() {
				@Override
				public void execute(SQLiteDatabase db) {
					insert(new Timer("Five Minutes", TimeParts.FIVE_MINUTES
							.getMillisecondsTotal()));
					insert(new Timer("Daily Scrum - 15 Minutes",
							TimeParts.FIFTEEN_MINUTES.getMillisecondsTotal()));
					insert(new Timer("One Hour", TimeParts.ONE_HOUR
							.getMillisecondsTotal()));
				}
			});
		}
	}

	/**
	 * Inserts a new timer into the database. The timer must at least have a
	 * name and milliseconds set.
	 * 
	 * @param timer
	 *            the timer to save
	 * @return the id of the timer that was generated when inserting it into the
	 *         database. This value is also written to the id attribute of
	 *         {@code timer}.
	 */
	public long insert(Timer timer) {
		if (timer == null) {
			throw new IllegalArgumentException("timer must not be null");
		}
		ContentValues values = new ContentValues(2);
		values.put(Db.TimerTable.Columns.NAME.name, timer.getName());
		values.put(Db.TimerTable.Columns.MILLIS.name, timer.getMillis());
		long id = this.db.insertOrThrow(Db.TimerTable.TABLE_NAME, null, values);
		timer.setId(id);
		return id;
	}

	public void update(Timer timer) {
		if (timer == null) {
			throw new IllegalArgumentException("timer must not be null");
		}
		if (timer.getId() <= 0) {
			throw new IllegalArgumentException("id must not be 0 or negative.");
		}
		ContentValues values = new ContentValues(2);
		values.put(Db.TimerTable.Columns.NAME.name, timer.getName());
		values.put(Db.TimerTable.Columns.MILLIS.name, timer.getMillis());
		int rows = this.db.update(Db.TimerTable.TABLE_NAME, values,
				Db.TimerTable.Columns.ID.name + " = ?",
				new String[] { String.valueOf(timer.getId()) });
		if (rows != 1) {
			throw new IllegalStateException(
					"Expected 1 to be updated, instead " + rows
							+ " have been changed.");
		}
	}

	public void delete(Timer timer) {
		if (timer == null) {
			throw new IllegalArgumentException("timer must not be null.");
		}
		this.delete(timer.getId());
	}

	public void delete(final long id) {
		if (id <= 0) {
			throw new IllegalArgumentException("id must not be 0 or negative.");
		}
		this.doInTransaction(new DatabaseAction() {
			@Override
			public void execute(SQLiteDatabase db) {
				db.delete(Db.TimerTable.TABLE_NAME,
						Db.TimerTable.Columns.ID.name + " = ?",
						new String[] { String.valueOf(id) });
			}
		});
	}

	public void deleteAll() {
		this.doInTransaction(new DatabaseAction() {
			@Override
			public void execute(SQLiteDatabase db) {
				db.delete(Db.TimerTable.TABLE_NAME, null, null);
			}
		});
	}

	public void doInTransaction(DatabaseAction action) {
		DbAccess.doInTransaction(this.db, action);
	}

	public void doWithCursor(Cursor cursor, DatabaseCursorAction action) {
		DbAccess.doWithCursor(cursor, action);
	}

	public <T> T doWithCursorForResult(Cursor cursor,
			DatabaseCursorResultAction<T> action) {
		return DbAccess.doWithCursorForResult(cursor, action);
	}

	public void close() {
		this.db.close();
	}
}
