package de.codecentric.android.timer.persistence;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import de.codecentric.android.timer.persistence.DbAccess.DatabaseAction;
import de.codecentric.android.timer.persistence.DbAccess.DatabaseCursorAction;
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

	public Timer findByName(final String name) {
		Cursor cursor = this.db.query(Db.TimerTable.TABLE_NAME, new String[] {
				Db.TimerTable.Columns.ID.name, Db.TimerTable.Columns.NAME.name,
				Db.TimerTable.Columns.MILLIS.name },
				Db.TimerTable.Columns.NAME.name + " = ?",
				new String[] { name }, null, null, null);
		return this.doWithCursor(cursor, new DatabaseCursorAction<Timer>() {
			@Override
			public Timer execute(Cursor cursor) {
				if (cursor.getCount() == 0) {
					return null;
				} else if (cursor.getCount() > 1) {
					throw new IllegalStateException(
							"Found more than one timer with name " + name + ".");
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

	public void deleteAll() {
		this.doInTransaction(new DatabaseAction() {
			@Override
			public void execute(SQLiteDatabase db) {
				db.delete(Db.TimerTable.TABLE_NAME, null, null);
			}
		});
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
		ContentValues values = new ContentValues(2);
		values.put(Db.TimerTable.Columns.NAME.name, timer.getName());
		values.put(Db.TimerTable.Columns.MILLIS.name, timer.getMillis());
		long id = this.db.insertOrThrow(Db.TimerTable.TABLE_NAME, null, values);
		timer.setId(id);
		return id;
	}

	public void doInTransaction(DatabaseAction action) {
		DbAccess.doInTransaction(this.db, action);
	}

	public <T> T doWithCursor(Cursor cursor, DatabaseCursorAction<T> action) {
		return DbAccess.doWithCursor(cursor, action);
	}

	public void close() {
		this.db.close();
	}
}
