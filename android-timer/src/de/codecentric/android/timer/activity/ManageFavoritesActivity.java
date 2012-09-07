package de.codecentric.android.timer.activity;

import static de.codecentric.android.timer.util.PreferencesKeysValues.*;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;
import de.codecentric.android.timer.R;
import de.codecentric.android.timer.persistence.Db;
import de.codecentric.android.timer.persistence.Timer;
import de.codecentric.android.timer.persistence.TimerDatabaseOpenHelper;
import de.codecentric.android.timer.persistence.TimerRepository;
import de.codecentric.android.timer.util.TimeParts;

public class ManageFavoritesActivity extends ListActivity {

	private static final String TAG = SaveAsFavoriteActivity.class.getName();

	private static final int COLUMN_INDEX_MILLIS_IN_QUERY = Db.TimerTable.Columns.MILLIS
			.ordinal();
	private static final String LOAD_TIMER_RESULT_SUFFIX = "load_timer_result";
	static final String LOAD_TIMER_RESULT = APPLICATION_PACKAGE_PREFIX
			+ LOAD_TIMER_RESULT_SUFFIX;

	private static final int CONTEXT_MENU_ITEM_ID_DELETE = 0;
	private static final int CONTEXT_MENU_ITEM_ID_RENAME = 1;

	private TimerRepository timerRepository;
	private Cursor cursor;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		this.initRepository();
		super.registerForContextMenu(super.getListView());
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.refreshViewFromDatabase();
	}

	@Override
	protected void onPause() {
		this.closeCursor();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.closeCursor();
		this.closeRepository();
	}

	private void initRepository() {
		TimerDatabaseOpenHelper helper = new TimerDatabaseOpenHelper(this);
		this.timerRepository = new TimerRepository(helper);
		// TODO Do we want to do this here in the activity? Each time it is
		// created? Rather not.
		this.timerRepository.createSampleEntriesIfEmpty();
	}

	private void closeRepository() {
		if (this.timerRepository != null) {
			this.timerRepository.close();
			this.timerRepository = null;
		}
	}

	private void refreshViewFromDatabase() {
		this.closeCursor();
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

	private void closeCursor() {
		if (this.cursor != null) {
			this.cursor.close();
			this.cursor = null;
		}
	}

	@Override
	protected void onListItemClick(ListView listView, View itemView,
			int position, long id) {
		Log.d(TAG, "onListItemClick(" + position + ", " + id + ")");
		Cursor itemAtPosition = (Cursor) getListView().getItemAtPosition(
				position);
		Timer timer = this.timerRepository.readTimerFromCursor(itemAtPosition);
		Intent resultIntent = new Intent();
		resultIntent.putExtra(LOAD_TIMER_RESULT, timer);
		this.setResult(RESULT_OK, resultIntent);
		this.finish();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo menuInfo) {
		menu.add(Menu.NONE, CONTEXT_MENU_ITEM_ID_DELETE, 0,
				R.string.manage_timers_context_menu_delete_label);
		menu.add(Menu.NONE, CONTEXT_MENU_ITEM_ID_RENAME, 1,
				R.string.manage_timers_context_menu_rename_label);
	}

	@Override
	public boolean onContextItemSelected(MenuItem contextMenuItem) {
		AdapterView.AdapterContextMenuInfo info;
		try {
			info = (AdapterView.AdapterContextMenuInfo) contextMenuItem
					.getMenuInfo();
		} catch (ClassCastException e) {
			Log.e(TAG, "bad menuInfo", e);
			return false;
		}

		long timerId = getListAdapter().getItemId(info.position);

		if (contextMenuItem.getItemId() == CONTEXT_MENU_ITEM_ID_DELETE) {
			this.timerRepository.delete(timerId);
			this.refreshViewFromDatabase();
			return true;
		} else if (contextMenuItem.getItemId() == CONTEXT_MENU_ITEM_ID_RENAME) {
			Intent updateFavoriteIntent = new Intent(this,
					SaveAsFavoriteActivity.class);
			Timer timer = this.timerRepository.findById(timerId);
			updateFavoriteIntent.putExtra(
					SaveAsFavoriteActivity.SAVE_TIMER_PARAM, timer);
			updateFavoriteIntent.setAction(Intent.ACTION_EDIT);
			this.startActivity(updateFavoriteIntent);
			return true;
		} else {
			return false;
		}
	}
}
