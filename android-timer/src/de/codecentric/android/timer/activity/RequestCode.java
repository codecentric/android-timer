package de.codecentric.android.timer.activity;

enum RequestCode {
	PREFERENCES(1), MANAGE_FAVORITES(2);

	final int code;

	RequestCode(int code) {
		this.code = code;
	}
}
