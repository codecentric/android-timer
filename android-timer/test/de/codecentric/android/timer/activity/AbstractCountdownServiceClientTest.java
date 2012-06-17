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
 * Test class for {@link CountdownServiceClient}.
 * 
 * @author Bastian Krol
 */
@RunWith(RobolectricTestRunner.class)
public abstract class AbstractCountdownServiceClientTest<T extends CountdownServiceClient> {

	protected T activity;

	@Mock
	protected CountdownService countdownService;

	@Before
	public void before() {
		MockitoAnnotations.initMocks(this);

		// Weird: when using mockito annotation @InjectMocks, the
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
		this.mockCollaborator(this.countdownService, "countdownService");
		when(this.countdownService.getState()).thenReturn(
				this.getCurrentServiceState());
	}

	protected <C> void mockCollaborator(C collaboratorMock,
			String collaboratorName) {
		Whitebox.setInternalState(this.activity, collaboratorName,
				collaboratorMock);
	}

	protected abstract T createActivityInstance();

	protected abstract ServiceState getCurrentServiceState();
}
