package de.codecentric.android.timer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.codecentric.android.timer.service.ServiceState;

public class ServiceStateIterator {

	public static void forAllServiceStates(ActionForServiceState action) {
		forAllServiceStatesExcept(action);
	}

	public static void forAllServiceStatesExcept(ServiceState except,
			ActionForServiceState action) {
		forAllServiceStatesExcept(action, except);
	}

	public static void forAllServiceStatesExcept(ActionForServiceState action,
			ServiceState... except) {
		List<ServiceState> statesToIterate = new ArrayList<ServiceState>(
				Arrays.asList(ServiceState.values()));
		for (ServiceState excludedState : except) {
			statesToIterate.remove(excludedState);
		}
		for (ServiceState serviceState : statesToIterate) {
			action.execute(serviceState);
		}
	}
}
