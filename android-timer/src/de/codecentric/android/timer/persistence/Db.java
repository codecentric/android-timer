package de.codecentric.android.timer.persistence;

class Db {

	static final int VERSION = 2;

	static final class Timer {
		static final String TABLE_NAME = "timer";

		static final class Columns {
			static final String ID = "ID";
			static final String NAME = "NAME";
			static final String MILLIS = "MILLIS";
			static final String SOUND = "SOUND";
		}

		static final class Statements {
			private static final String CREATE_TABLE = //
			"CREATE TABLE " + TABLE_NAME + " (" //
					+ Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " //
					+ Columns.NAME + " TEXT NOT NULL, " //
					+ Columns.MILLIS + " INTEGER NOT NULL, " //
					+ Columns.SOUND + " TEXT" //
					+ ")";
			private static final String INDEX_NAME_UNIQUE = "IX_TIMER_NAME";
			private static final String CREATE_INDEX = //
			"CREATE UNIQUE INDEX IF NOT EXISTS " + INDEX_NAME_UNIQUE + " ON "
					+ TABLE_NAME + " (" + Columns.NAME + ")";
			static final String[] ALL_CREATE_STATEMENTS = new String[] {
					CREATE_TABLE, CREATE_INDEX };

			private static final String DROP_TABLE = "DROP TABLE IF EXISTS "
					+ TABLE_NAME;
			private static final String DROP_INDEX = "DROP INDEX IF EXISTS "
					+ INDEX_NAME_UNIQUE;
			static final String[] ALL_DROP_STATEMENTS = new String[] {
					DROP_TABLE, DROP_INDEX };
		}
	}
}
