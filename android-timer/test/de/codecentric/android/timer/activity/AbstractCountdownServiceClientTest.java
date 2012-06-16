package de.codecentric.android.timer.activity;

import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.internal.util.reflection.Whitebox;

import com.xtremelabs.robolectric.RobolectricTestRunner;

import de.codecentric.android.timer.service.CountdownService;
import de.codecentric.android.timer.service.ServiceState;

/**
 * Test class for {@link CountdownServiceClient}.
 * 
 * @author Bastian Krol
 */
@RunWith(RobolectricTestRunner.class)
public abstract class AbstractCountdownServiceClientTest<T extends CountdownServiceClient> {

	private CountdownService countdownService;

	protected T activity;

	@Before
	public void before() {

		// Weird: when using mockito annotations (@Mock & @InjectMocks), the
		// robolectric magic does not seem to work correctly anymore and we get
		// a NPE during activity.onCreate in the constructor of
		// PreferencesKeysValues at the first call to
		// context.getResources().getXxx
		// When we set up the mocks manually, this seems to work

		this.activity = createActivityInstance();
		this.doAdditionalMockingBeforeOnCreate();
		this.activity.onCreate(null);
		this.mockCountdownService();
		this.doAdditionalMockingBeforeOnResume();
		this.activity.onResume();

	}

	protected void doAdditionalMockingBeforeOnCreate() {
		// Hook: Override this if needed
	}

	protected void doAdditionalMockingBeforeOnResume() {
		// Hook: Override this if needed
	}

	private void mockCountdownService() {
		this.countdownService = this.mockCollaborator(CountdownService.class,
				"countdownService");
		when(this.countdownService.getState()).thenReturn(
				this.getCurrentServiceState());
	}

	protected <C> C mockCollaborator(Class<C> collaboratorClass,
			String collaboratorName) {
		C collaborator = mock(collaboratorClass);
		Whitebox.setInternalState(this.activity, collaboratorName, collaborator);
		return collaborator;
	}

	protected abstract T createActivityInstance();

	protected abstract ServiceState getCurrentServiceState();
}
