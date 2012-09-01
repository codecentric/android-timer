package de.codecentric.android.timer.activity;

import static de.codecentric.android.timer.util.PreferencesKeysValues.*;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;
import de.codecentric.android.timer.persistence.Db;
import de.codecentric.android.timer.persistence.Timer;
import de.codecentric.android.timer.persistence.TimerDatabaseOpenHelper;
import de.codecentric.android.timer.persistence.TimerRepository;
import de.codecentric.android.timer.util.TimeParts;

public class ManageTimersListActivity extends ListActivity {

	private static final int COLUMN_INDEX_MILLIS_IN_QUERY = Db.TimerTable.Columns.MILLIS
			.ordinal();
	private static final String LOAD_TIMER_RESULT_SUFFIX = "load_timer_result";
	static final String LOAD_TIMER_RESULT = APPLICATION_PACKAGE_PREFIX
			+ LOAD_TIMER_RESULT_SUFFIX;

	private TimerRepository timerRepository;
	private Cursor cursor;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		TimerDatabaseOpenHelper helper = new TimerDatabaseOpenHelper(this);
		this.timerRepository = new TimerRepository(helper);

		// TODO Do we want to do this here in the activity? Each time it is
		// created? Rather not.
		this.timerRepository.createSampleEntriesIfEmpty();

		this.cursor = this.timerRepository.findAllTimers();
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_2, cursor, new String[] {
						Db.TimerTable.Columns.NAME.name,
						Db.TimerTable.Columns.MILLIS.name }, new int[] {
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

	@Override
	protected void onListItemClick(ListView listView, View itemView,
			int position, long id) {
		Cursor itemAtPosition = (Cursor) getListView().getItemAtPosition(
				position);
		Timer timer = this.timerRepository.readTimerFromCursor(itemAtPosition);
		Intent resultIntent = new Intent();
		resultIntent.putExtra(LOAD_TIMER_RESULT, timer);
		this.setResult(RESULT_OK, resultIntent);
		this.finish();
	}
}