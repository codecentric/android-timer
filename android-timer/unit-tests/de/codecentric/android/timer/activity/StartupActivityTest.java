package de.codecentric.android.timer.activity;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.app.Activity;

import com.xtremelabs.robolectric.RobolectricTestRunner;

import de.codecentric.android.timer.R;

/**
 * Test class for {@link StartupActivity}.
 * 
 * @author Bastian Krol
 */
@RunWith(RobolectricTestRunner.class)
public class StartupActivityTest {

	private Activity activity;

	@Before
	public void before() {
		this.activity = new StartupActivity();
	}

	@Test
	public void shouldSayStartup() throws Exception {
		// this was the first robolectric unit test of this project. I admit it
		// doesn't provide a lot of value :-)
		String hello = this.activity.getResources().getString(
				R.string.startup_message);
		assertThat(hello, equalTo("Starting…"));
	}
}
