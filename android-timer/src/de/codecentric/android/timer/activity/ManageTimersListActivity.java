package de.codecentric.android.timer.activity;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;
import de.codecentric.android.timer.persistence.Db;
import de.codecentric.android.timer.persistence.TimerDatabaseOpenHelper;
import de.codecentric.android.timer.persistence.TimerRepository;
import de.codecentric.android.timer.util.TimeParts;

public class ManageTimersListActivity extends ListActivity {

	private static final int COLUMN_INDEX_MILLIS_IN_QUERY = Db.Timer.Columns.MILLIS
			.ordinal();

	private TimerDatabaseOpenHelper helper;
	private TimerRepository timerRepository;
	private Cursor cursor;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		this.helper = new TimerDatabaseOpenHelper(this);
		this.timerRepository = new TimerRepository(helper);

		// TODO Do we want to do this here in the activity? Each time it is
		// created? Rather not.
		this.timerRepository.createSampleEntriesIfEmpty();

		this.cursor = this.timerRepository.findAllTimers();
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_2, cursor, new String[] {
						Db.Timer.Columns.NAME.name,
						Db.Timer.Columns.MILLIS.name }, new int[] {
						android.R.id.text1, android.R.id.text2 });
		adapter.setViewBinder(new ViewBinder() {
			public boolean setViewValue(View view, Cursor cursor,
					int columnIndex) {
				if (columnIndex == COLUMN_INDEX_MILLIS_IN_QUERY) {
					long millis = cursor.getLong(columnIndex);
					TimeParts time = TimeParts.fromMillisRoundingUp(millis);
					TextView textView = (TextView) view;
					textView.setText(time.prettyPrint());
					return true;
				}
				return false;
			}
		});
		setListAdapter(adapter);

		// TODO Load timer when item is clicked
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