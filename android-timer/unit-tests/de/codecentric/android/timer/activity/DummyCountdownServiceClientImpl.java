package de.codecentric.android.timer.activity;

import de.codecentric.android.timer.service.ServiceState;

public class DummyCountdownServiceClientImpl extends CountdownServiceClient {

	@Override
	protected ServiceState[] getHandledServiceStates() {
		// handle all states
		return ServiceState.values();
	}

	@Override
	protected ServiceState[] getFinishingServiceStates() {
		// handle all states
		return new ServiceState[] {};
	}

	@Override
	protected String getTag() {
		return DummyCountdownServiceClientImpl.class.getName();
	}

}