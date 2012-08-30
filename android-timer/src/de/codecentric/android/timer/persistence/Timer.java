package de.codecentric.android.timer.persistence;

/**
 * Represents a timer that can be saved to/retrieved from the database.
 * 
 * @author Bastian Krol
 */
public class Timer {

	private long id;
	private String name;
	private long millis;

	public Timer(String name, long millis) {
		this.name = name;
		this.millis = millis;
	}

	public Timer(long id, String name, long millis) {
		this.id = id;
		this.name = name;
		this.millis = millis;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getMillis() {
		return millis;
	}

	public void setMillis(long millis) {
		this.millis = millis;
	}
}
