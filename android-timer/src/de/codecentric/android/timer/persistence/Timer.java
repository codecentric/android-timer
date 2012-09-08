package de.codecentric.android.timer.persistence;

import java.io.Serializable;

import de.codecentric.android.timer.util.TimeParts;

/**
 * Represents a timer that can be saved to/retrieved from the database.
 * 
 * @author Bastian Krol
 */
public class Timer implements Serializable {

	private static final long serialVersionUID = 1L;

	private long id;
	private String name;
	private TimeParts timeParts;

	public Timer(String name, TimeParts timeParts) {
		if (timeParts == null) {
			throw new IllegalArgumentException("timeParts must not be null.");
		}
		this.name = name;
		this.timeParts = timeParts;
	}

	public Timer(String name, long millis) {
		this.name = name;
		this.timeParts = TimeParts.fromMillisExactly(millis);
	}

	public Timer(long id, String name, TimeParts timeParts) {
		if (timeParts == null) {
			throw new IllegalArgumentException("timeParts must not be null.");
		}
		this.id = id;
		this.name = name;
		this.timeParts = timeParts;
	}

	public Timer(long id, String name, long millis) {
		this.id = id;
		this.name = name;
		this.timeParts = TimeParts.fromMillisExactly(millis);
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

	public TimeParts getTimeParts() {
		return timeParts;
	}

	public void setTimeParts(TimeParts timeParts) {
		if (timeParts == null) {
			throw new IllegalArgumentException("timeParts must not be null.");
		}
		this.timeParts = timeParts;
	}

	public long getMillis() {
		return timeParts.getMillisecondsTotal();
	}

	public void setMillis(long millis) {
		this.timeParts = TimeParts.fromMillisExactly(millis);
	}

	@Override
	public String toString() {
		return "Timer [id=" + id + ", name=" + name + ", time="
				+ this.timeParts + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((timeParts == null) ? 0 : timeParts.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Timer other = (Timer) obj;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (timeParts == null) {
			if (other.timeParts != null)
				return false;
		} else if (!timeParts.equals(other.timeParts))
			return false;
		return true;
	}
}
