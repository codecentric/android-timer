package de.codecentric.android.timer.service;

/**
 * Represents the internal state of this service.
 * 
 * @author bastian.krol
 */
public enum ServiceState {

	/**
	 * This is the initial state, either the service has not yet begun to count
	 * down or a former countdown has reached zero or was stopped by the user
	 * (in which case this service returns to this initial state).
	 * 
	 * It can be started by calling
	 * {@link CountdownService#startCountdownTimer(long)} or stopped (without
	 * being run first) by calling {@link CountdownService#stopCountdown()}.
	 */
	WAITING,

	/**
	 * The service is currently counting down. It can be paused by calling
	 * {@link CountdownService#pauseCountdown()} or stopped by calling
	 * {@link CountdownService#stopCountdown()}.
	 */
	COUNTING_DOWN,

	/**
	 * The was started at least once and has been paused. It can be continued by
	 * calling {@link CountdownService#continueCountdown()} or stopped by
	 * calling {@link CountdownService#stopCountdown()}.
	 */
	PAUSED,

	/**
	 * The countdown has reached zero and the alarm is ringing. The alarm has
	 * not yet been stopped. It can be notified about the fact that the alarm
	 * sound has been stopped by calling
	 * {@link CountdownService#notifyAlarmSoundHasStopped()} or stopped by
	 * calling {@link CountdownService#stopCountdown()}.
	 */
	BEEPING,

	/**
	 * The countdown had reached zero and the alarm has been ringing and has
	 * been stopped by the user. Clients can still call
	 * {@link CountdownService#stopCountdown()}, but it will have no effect.
	 * Clients can start a new countdown by calling
	 * {@link CountdownService#resetToWaiting()} followed by
	 * {@link CountdownService#startCountdown(long)}.
	 */
	FINISHED,

	/**
	 * The user has left the application.
	 */
	EXIT
}