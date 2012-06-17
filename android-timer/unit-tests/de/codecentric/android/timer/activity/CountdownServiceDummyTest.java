package de.codecentric.android.timer.activity;

import org.junit.Test;

import de.codecentric.android.timer.service.ServiceState;

public class CountdownServiceDummyTest extends
		AbstractCountdownServiceClientTest<DummyCountdownServiceClientImpl> {

	@Override
	protected DummyCountdownServiceClientImpl createActivityInstance() {
		return new DummyCountdownServiceClientImpl();
	}

	@Override
	protected ServiceState getCurrentServiceState() {
		return ServiceState.WAITING;
	}

	@Test
	public void testAbstractCountdownServiceClientTestSetup() {
		// just make sure super.before() works
	}
}
