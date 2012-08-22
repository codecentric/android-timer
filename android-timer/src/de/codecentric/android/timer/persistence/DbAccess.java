package de.codecentric.android.timer.persistence;

import android.database.sqlite.SQLiteDatabase;

/**
 * Common code for connection and transaction handling.
 * 
 * @author Bastian Krol
 */
class DbAccess {

	/**
	 * Interface for something that is done with a database connection or inside
	 * a transaction.
	 */
	interface DatabaseAction {

		/**
		 * Executes some updates/inserts on the given database.
		 */
		void execute(SQLiteDatabase dbw);

	}

	static void doWithDatabaseConnection(SQLiteDatabase db,
			DatabaseAction action) {
		try {
			action.execute(db);
		} finally {
			db.close();
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
