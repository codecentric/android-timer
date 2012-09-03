package de.codecentric.android.timer.persistence;

import java.io.Serializable;

/**
 * Represents a timer that can be saved to/retrieved from the database.
 * 
 * @author Bastian Krol
 */
public class Timer implements Serializable {

	private static final long serialVersionUID = 1L;

	private long id;
	private String name;
	// TODO Make Timer use TimeParts instead of milliseconds
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

	@Override
	public String toString() {
		return "Timer [id=" + id + ", name=" + name + ", millis=" + millis
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + (int) (millis ^ (millis >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		if (millis != other.millis)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
