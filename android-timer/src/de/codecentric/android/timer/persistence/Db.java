package de.codecentric.android.timer.persistence;

public class Db {

	static final int VERSION = 1;
	static final String DATABASE_NAME = "codecentric_timer";

	private Db() {
		// disallow instantiation
	}

	public static final class TimerTable {

		private TimerTable() {
		}

		static final String TABLE_NAME = "timer";

		public enum Columns {
			ID("_id"), NAME("name"), MILLIS("millis"), SOUND("sound");

			/**
			 * The name of this column in the database.
			 */
			public final String name;

			private Columns(String name) {
				this.name = name;
			}
		}

		static final class Statements {

			private static final String CREATE_TABLE = //
			"CREATE TABLE " + TABLE_NAME + " (" + Columns.ID.name
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, " //
					+ Columns.NAME.name + " TEXT NOT NULL, " //
					+ Columns.MILLIS.name + " INTEGER NOT NULL, " //
					+ Columns.SOUND.name + " TEXT)";
			static final String[] ALL_CREATE_STATEMENTS = new String[] { CREATE_TABLE };

			// private static final String DROP_TABLE = "DROP TABLE IF EXISTS "
			// + TABLE_NAME;
			// static final String[] ALL_DROP_STATEMENTS = new String[] {
			// DROP_TABLE };
			private static final String INDEX_NAME_UNIQUE = "IX_TIMER_NAME";
			private static final String DROP_TABLE = "DROP TABLE IF EXISTS "
					+ TABLE_NAME;
			private static final String DROP_INDEX = "DROP INDEX IF EXISTS "
					+ INDEX_NAME_UNIQUE;
			static final String[] ALL_DROP_STATEMENTS = new String[] {
					DROP_TABLE, DROP_INDEX };
		}
	}
}
