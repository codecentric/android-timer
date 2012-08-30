package de.codecentric.android.timer.activity;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;
import de.codecentric.android.timer.persistence.Db;
import de.codecentric.android.timer.persistence.TimerDatabaseOpenHelper;
import de.codecentric.android.timer.persistence.TimerRepository;

public class ManageTimersListActivity extends ListActivity {

	private TimerDatabaseOpenHelper helper;
	private TimerRepository timerRepository;
	private Cursor cursor;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		this.helper = new TimerDatabaseOpenHelper(this);
		this.timerRepository = new TimerRepository(helper);

		// TODO Remove this line
		this.timerRepository.createSomeDummyEntries();

		this.cursor = this.timerRepository.findAllTimers();
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_1, cursor,
				new String[] { Db.Timer.Columns.NAME.name },
				new int[] { android.R.id.text1 });
		setListAdapter(adapter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (this.cursor != null) {
			this.cursor.close();
		}
		if (this.timerRepository != null) {
			this.timerRepository.close();
		}
	}
}