package de.codecentric.android.timer.service;

import android.os.Binder;

/**
 * {@link Binder} for {@link CountdownService}.
 * 
 * @author Bastian Krol
 */
public class CountdownServiceBinder extends Binder {

	private CountdownService service;

	public CountdownServiceBinder(CountdownService service) {
		this.service = service;
	}

	public CountdownService getCountdownService() {
		return this.service;
	}
}
