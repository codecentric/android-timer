package de.codecentric.android.timer.activity;

import java.util.Arrays;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import de.codecentric.android.timer.R;
import de.codecentric.android.timer.service.CountdownService;
import de.codecentric.android.timer.service.CountdownServiceBinder;
import de.codecentric.android.timer.service.ServiceState;
import de.codecentric.android.timer.util.PreferencesKeysValues;

/**
 * Base class for all activities that use {@link CountdownService}. Starts the
 * service if neccessary and binds the activity to the service.
 * 
 * Subclasses that override any of the following methods need to call the super
 * implementation of that method:
 * 
 * <ul>
 * <li>onCreate(Bundle)</li>
 * <li>onResume()</li>
 * <li>onPause()</li>
 * <li>onStop()</li>
 * <li>onDestroy()</li>
 * </ul>
 * 
 * @author Bastian Krol
 */
abstract class CountdownServiceClient extends Activity {

	protected static final int REQUEST_CODE_PREFERENCES = 1;
	protected static final int REQUEST_CODE_LOAD_TIMER = 2;

	private ServiceConnection serviceConnection;
	private CountdownService countdownService;
	private Navigation navigation;
	private PreferencesKeysValues preferencesKeysValues;
	private Intent serviceIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.serviceIntent = new Intent(this, CountdownService.class);
		this.preferencesKeysValues = new PreferencesKeysValues(this);
		this.navigation = new Navigation(this, this.preferencesKeysValues);
	}

	@Override
	protected void onResume() {
		Log.d(this.getTag(), "onResume() [CountdownServiceClient]");
		super.onResume();
		this.startAndBindCountdownService();
	}

	@Override
	protected void onPause() {
		Log.d(this.getTag(), "onPause() [CountdownServiceClient]");
		this.unbindCountdownService();
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(this.getTag(), "onCreateOptionsMenu(...)");
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.xml.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(this.getTag(), "onOptionsItemSelected(...)");
		switch (item.getItemId()) {
		case R.id.itemOptions:
			Log.d(this.getTag(), "item options clicked");
			Intent preferencesIntent = new Intent(this,
					TimerPreferencesActivity.class);
			super.startActivityForResult(preferencesIntent,
					REQUEST_CODE_PREFERENCES);
			return true;
		case R.id.itemLoadTimer:
			Log.d(this.getTag(), "item load timer clicked");
			Intent manageTimersListActivity = new Intent(this,
					ManageTimersListActivity.class);
			super.startActivityForResult(manageTimersListActivity,
					REQUEST_CODE_LOAD_TIMER);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Checks if the service is currently bound to the activity.
	 * 
	 * @return {@code true} if and only if the service is bound to the activity
	 */
	protected final boolean isServiceBound() {
		return this.countdownService != null;
	}

	protected CountdownService getCountdownService() {
		if (this.isServiceBound()) {
			return this.countdownService;
		} else {
			throw new IllegalStateException(
					"CountdownService is not bound to activity");
		}
	}

	protected PreferencesKeysValues getPreferencesKeysValues() {
		return this.preferencesKeysValues;
	}

	protected Navigation getNavigation() {
		return this.navigation;
	}

	/**
	 * This method is called immediately after the activity first connects to
	 * the service and gives the activity a chance to change its behaviour
	 * depending on the current state of {@link CountdownService}.
	 * 
	 * This method is also called in the {@link #onResume()} method if the
	 * service is already bound to the activity for whatever reason.
	 * 
	 * First, the method checks if {@code serviceState} is handled by this
	 * activity, determined by {@link #getHandledServiceStates()}. If so,
	 * {@link #handleState(ServiceState)} is called. Otherwise, the method
	 * checks if this activity is ought to finished, determined by
	 * {@link #getFinishingServiceStates()}. If so, this activity is finished.
	 * Otherwise, the method will check the {@link #NAVIGATION_RULES} map to
	 * find the correct activity to navigate to.
	 * 
	 * @param serviceState
	 *            the current service state
	 */
	protected final void onAfterServiceConnected(ServiceState serviceState) {
		Log.d(this.getTag(), "onAfterServiceConnected(" + serviceState + ")");
		ServiceState[] handledStates = this.getHandledServiceStates();
		ServiceState[] finishingStates = this.getFinishingServiceStates();
		if (Arrays.binarySearch(handledStates, serviceState) >= 0) {
			Log.d(this.getTag(), "Current activity will handle state "
					+ serviceState + ".");
			this.handleState(serviceState);
		} else if (Arrays.binarySearch(finishingStates, serviceState) >= 0) {
			Log.d(this.getTag(),
					"Current activity will finish because of state "
							+ serviceState + ".");
			this.onBeforeFinish();
			this.finish();
		} else {
			Log.d(this.getTag(),
					"Current activity will execute navigation from state "
							+ serviceState + ".");
			this.navigate(serviceState);
		}
	}

	/**
	 * Returns the states from {@link ServiceState} that are handled by this
	 * activity.
	 * 
	 * {@link #handleState(ServiceState)} needs to be able to cope with all
	 * states in the returned array.
	 * 
	 * @return the states that are handled by this activity
	 */
	protected abstract ServiceState[] getHandledServiceStates();

	/**
	 * Returns the states from {@link ServiceState} that are not handled by this
	 * activity and on which the activity should simply shut down (call
	 * {@link #finish()}).
	 * 
	 * The states returned by {@link #getHandledServiceStates()},
	 * {@link #getFinishingServiceStates()} and the key set of
	 * {@link #getNavigationRules()} have to be disjoint partitioning of all
	 * states.
	 * 
	 * @return the states for which the activity is ougth to call
	 *         {@link #finish()}
	 */
	protected abstract ServiceState[] getFinishingServiceStates();

	/**
	 * This hook is called immediately after the activity first connects to the
	 * service if the service is currently in one of the states returned by
	 * {@link #getHandledServiceStates()}. It gives the activity a chance to
	 * change its behaviour depending on the current state of
	 * {@link CountdownService}.
	 * 
	 * This method is also called in the {@link #onResume()} method if the
	 * service is already bound to the activity for whatever reason, again, only
	 * if the service is currently in one of the states returned by
	 * {@link #getHandledServiceStates()}.
	 * 
	 * Subclasses can override this method. The default implementation is empty.
	 * 
	 * @param serviceState
	 *            the current service state
	 */
	protected void handleState(ServiceState serviceState) {
		// Empty default implementation
	}

	private void navigate(ServiceState serviceState) {
		Log.d(this.getTag(), "navigate(" + serviceState + ")");
		Class<? extends CountdownServiceClient> activityClass = this.navigation
				.getActivityClassForState(this, serviceState);
		Log.d(this.getTag(),
				"Going to activity class " + activityClass.getSimpleName()
						+ ".");
		Intent intent = new Intent(this, activityClass);
		this.startActivity(intent);
	}

	/**
	 * Triggers the navigation based on the current service state.
	 */
	protected final void navigate() {
		Log.d(this.getTag(), "navigate()");
		this.navigate(this.getCountdownService().getState());
	}

	/**
	 * This hook is called immediately before the activity disconnects from the
	 * service. It can be overridden. The default implementation is empty.
	 * 
	 * This method will only be called if the activity disconnects actively from
	 * the service, which happens in this class' {@link #onStop()} and
	 * {@link #onDestroy()} method implementations. If the service is
	 * terminated, the activity will only be notified by calling
	 * {@link #onAfterServiceDisconnected()}.
	 */
	protected void onBeforeServiceDisconnected() {
		// Empty default implementation
	}

	/**
	 * This hook is called immediately after the activity disconnects from the
	 * service. It can be overridden. The default implementation is empty.
	 */
	protected void onAfterServiceDisconnected() {
		// Empty default implementation
	}

	/**
	 * This hook is called in onAfterServiceConnected right before this activity
	 * is left by calling {@link Activity#finish()} (given the current service
	 * state is contained in the array returned by
	 * {@link #getFinishingServiceStates()}.
	 * 
	 * Subclasses can override this method. The default implementation is empty.
	 */
	protected void onBeforeFinish() {
		// Empty default implementation
	}

	private void startAndBindCountdownService() {
		Log.d(this.getTag(), "bindService()");
		if (!this.isServiceBound()) {
			Log.d(this.getTag(), "initializing service connection");
			this.startCountdownService();
			this.initServiceConnection();
			Log.d(this.getTag(), "about to call  : bindService");
			super.bindService(this.serviceIntent, this.serviceConnection, 0);
			Log.d(this.getTag(), "has been called: bindService");
		} else {
			// This else-branch is probably never executed on the device,
			// because the service is always unbound in onPause, so it is never
			// already bound when startAndBindCountdownService() is called in
			// onResume(). This branch is executed in the unit tests, though,
			// because the service mock is already injected when onResume is
			// called, thus, isServiceBound() returns true. As a side effect, it
			// is neccessary that all view objects which might be used in
			// handleState (for example) are already initialized before onResume
			// is called (that is, in onCreate).
			Log.d(this.getTag(),
					"service already bound in bindService(), will not bind again");
			this.onAfterServiceConnected(this.getCountdownService().getState());
		}
	}

	private void startCountdownService() {
		Log.d(this.getTag(), "startCountdownService()");
		Log.d(this.getTag(), "about to call  : startService");
		ComponentName result = super.startService(this.serviceIntent);
		Log.d(this.getTag(), "has been called: startService - result: "
				+ result);
	}

	private void initServiceConnection() {
		Log.d(this.getTag(), "initServiceConnection()");
		this.serviceConnection = new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName className,
					IBinder service) {
				Log.d(getTag(), "ServiceConnection#onServiceConnected()");
				CountdownServiceBinder binder = (CountdownServiceBinder) service;
				CountdownServiceClient.this.onServiceConnected(binder);
			}

			@Override
			public void onServiceDisconnected(ComponentName componentName) {
				Log.d(getTag(), "ServiceConnection#onServiceDisconnected()");
				CountdownServiceClient.this.onServiceDisconnected();
			}
		};
	}

	private void onServiceConnected(CountdownServiceBinder binder) {
		Log.d(this.getTag(), "onServiceConnected(CountdownServiceBinder)");
		this.countdownService = binder.getCountdownService();
		this.onAfterServiceConnected(this.getCountdownService().getState());
	}

	private void onServiceDisconnected() {
		Log.d(this.getTag(), "onServiceDisconnected()");
		this.countdownService = null;
		this.serviceConnection = null;
		this.onAfterServiceDisconnected();
	}

	/**
	 * This method needs to be called when the activity is stopped, paused or
	 * destroyed. It disconnects/unbinds this activity from the countdown
	 * service. Since the countdown service is a &quot;started&quot; service (in
	 * the sense of
	 * http://developer.android.com/guide/topics/fundamentals/services.html),
	 * the service will not be automatically stopped or destroyed.
	 */
	protected void unbindCountdownService() {
		Log.d(this.getTag(), "unbindCountdownService()");
		if (this.isServiceBound()) {
			this.onBeforeServiceDisconnected();
			Log.d(this.getTag(), "about to call  : super.unbindService");
			super.unbindService(this.serviceConnection);
			Log.d(this.getTag(), "has been called: super.unbindService");
			this.countdownService = null;
			this.serviceConnection = null;
		} else {
			Log.d(this.getTag(),
					"service not bound in unbindCountdownService()");
		}
	}

	/**
	 * Returns the logging tag for this activity.
	 * 
	 * @return the logging tag for this activity
	 */
	protected abstract String getTag();
}