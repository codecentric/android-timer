package de.codecentric.android.timer.persistence;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Common code for connection and transaction handling.
 * 
 * @author Bastian Krol
 */
class DbAccess {

	private DbAccess() {
		// disallow instantiation
	}

	/**
	 * Interface for something that is done with a database connection or inside
	 * a transaction.
	 */
	interface DatabaseAction {

		/**
		 * Executes some updates/inserts on the given database.
		 */
		void execute(SQLiteDatabase db);

	}

	/**
	 * Interface for something that is done with a database cursor and returns
	 * no result.
	 */
	interface DatabaseCursorAction {

		/**
		 * Executes some reads on the given database.
		 */
		void execute(Cursor cursor);

	}

	/**
	 * Interface for something that is done with a database cursor and returns a
	 * result.
	 */
	interface DatabaseCursorResultAction<T> {

		/**
		 * Executes some reads on the given database and returns the result.
		 * 
		 * @return the result
		 */
		T execute(Cursor cursor);

	}

	static void doWithDatabaseConnection(SQLiteDatabase db,
			DatabaseAction action) {
		try {
			action.execute(db);
		} finally {
			db.close();
		}
	}

	static void doWithCursor(Cursor cursor, DatabaseCursorAction action) {
		try {
			action.execute(cursor);
		} finally {
			cursor.close();
		}
	}

	static <T> T doWithCursorForResult(Cursor cursor,
			DatabaseCursorResultAction<T> action) {
		try {
			return action.execute(cursor);
		} finally {
			cursor.close();
		}
	}

	static void doInTransaction(SQLiteDatabase db, DatabaseAction action) {
		db.beginTransaction();
		try {
			action.execute(db);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
}
