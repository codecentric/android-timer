package de.codecentric.android.timer.activity;

import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;

import com.xtremelabs.robolectric.RobolectricTestRunner;

import de.codecentric.android.timer.service.CountdownService;
import de.codecentric.android.timer.service.ServiceState;

/**
 * Abstract base class for tests for activities that extend
 * {@link CountdownServiceClient}.
 * 
 * @author Bastian Krol
 */
@RunWith(RobolectricTestRunner.class)
public abstract class AbstractCountdownServiceClientTest<T extends CountdownServiceClient> {

	/**
	 * The activity under test, created by {@link #createActivityInstance()} and
	 * provided with a mock for CountdownService in
	 * {@link #beforeAbstractCountdownServiceClientTest()}.
	 */
	protected T activity;

	/**
	 * The CountdownService mock. This is already injected into the activity
	 * under test.
	 */
	@Mock
	protected CountdownService countdownService;

	/**
	 * Does some basic setup for activity testing, like injecting the
	 * CountdownService mock into the activity under test.
	 */
	@Before
	public final void beforeAbstractCountdownServiceClientTest() {
		MockitoAnnotations.initMocks(this);

		// Weird: when using the mockito annotation @InjectMocks on the
		// activity, the robolectric magic does not seem to work correctly
		// anymore and we get a NPE during activity.onCreate in the constructor
		// of PreferencesKeysValues at the first call to
		// context.getResources().getXxx. This problem might be caused by a
		// different order of creating the activity instance and the robolectric
		// runner setting up things.

		this.activity = createActivityInstance();
		this.injectAdditionalMocksBeforeOnCreate();
		this.activity.onCreate(null);
		this.mockCountdownService();
		this.injectAdditionalMocksBeforeOnResume();
		this.activity.onResume();
		this.injectAdditionalMocksAfterOnResume();
	}

	/**
	 * AbstractCountdownServiceClientTest already mocks the CountdownService
	 * instance and injects this mock into the activity; however, if you need to
	 * inject additional mocks into the activity you can use this method,
	 * {@link #injectAdditionalMocksBeforeOnResume()} or
	 * {@link #injectAdditionalMocksAfterOnResume()}, depending on when the
	 * collaborator needs to be injected.
	 * 
	 * If you don't care, injectAdditionalMocksAfterOnResume is probably your
	 * best bet. Some collaborators might already be needed in onCreate or
	 * onResume, that's why there are multiple methods.
	 * 
	 * The default implementation is empty.
	 */
	protected void injectAdditionalMocksBeforeOnCreate() {
		// Hook: Override this if needed
	}

	/**
	 * AbstractCountdownServiceClientTest already mocks the CountdownService
	 * instance and injects this mock into the activity; however, if you need to
	 * inject additional mocks into the activity you can use this method,
	 * {@link #injectAdditionalMocksBeforeOnCreate()} or
	 * {@link #injectAdditionalMocksAfterOnResume()}, depending on when the
	 * collaborator needs to be injected.
	 * 
	 * If you don't care, injectAdditionalMocksAfterOnResume is probably your
	 * best bet. Some collaborators might already be needed in onCreate or
	 * onResume, that's why there are multiple methods.
	 * 
	 * The default implementation is empty.
	 */
	protected void injectAdditionalMocksBeforeOnResume() {
		// Hook: Override this if needed
	}

	/**
	 * AbstractCountdownServiceClientTest already mocks the CountdownService
	 * instance and injects this mock into the activity; however, if you need to
	 * inject additional mocks into the activity you can use this method,
	 * {@link #injectAdditionalMocksBeforeOnCreate()} or
	 * {@link #injectAdditionalMocksBeforeOnResume()}, depending on when the
	 * collaborator needs to be injected.
	 * 
	 * If you don't care, this method is probably your best bet. Some
	 * collaborators might already be needed in onCreate or onResume, that's why
	 * there are multiple methods.
	 * 
	 * The default implementation is empty.
	 */
	protected void injectAdditionalMocksAfterOnResume() {
		// Hook: Override this if needed
	}

	/**
	 * Injects the mock {@link #countdownService} into the activity.
	 */
	private void mockCountdownService() {
		this.mockCollaborator(this.countdownService, "countdownService");
		when(this.countdownService.getState()).thenReturn(
				this.getCurrentServiceState());
	}

	/**
	 * Use this inject a mock for a collaborator that can not be set
	 * conventionally (because it has no setter). Internally, this method uses
	 * {@link Whitebox#setInternalState(Object, String, Object)}.
	 * 
	 * @param collaboratorMock
	 *            the mock object
	 * @param collaboratorName
	 *            the name of the field of the collaborator in the activity
	 *            class
	 */
	protected final <C> void mockCollaborator(C collaboratorMock,
			String collaboratorName) {
		Whitebox.setInternalState(this.activity, collaboratorName,
				collaboratorMock);
	}

	/**
	 * Creates an instance of the activity under test.
	 * 
	 * @return a new instance of the activity under test
	 */
	protected abstract T createActivityInstance();

	/**
	 * Returns the {@link ServiceState} that will be returned by the
	 * countdownService mock when {@link CountdownService#getState()} is called.
	 * Note that does not not influence what
	 * {@link CountdownService#isWaiting()},
	 * {@link CountdownService#isCountingDown()},
	 * {@link CountdownService#isPaused()}, {@link CountdownService#isBeeping()}
	 * , {@link CountdownService#isFinished()} or
	 * {@link CountdownService#isExit()} will return (all of them will return
	 * false), if needed, these need to be stubbed individually by the subclass.
	 * 
	 * @return the service state for the countdown service mock
	 */
	protected abstract ServiceState getCurrentServiceState();
}
