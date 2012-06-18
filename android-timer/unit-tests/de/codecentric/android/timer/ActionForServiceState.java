package de.codecentric.android.timer;

import de.codecentric.android.timer.service.ServiceState;

public interface ActionForServiceState {
	public void execute(ServiceState serviceState);
}
