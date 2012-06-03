package de.codecentric.android.timer.service;

import java.io.IOException;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

class SoundGizmo {

	private static final String TAG = SoundGizmo.class.getName();

	private MediaPlayer mediaPlayer;

	public SoundGizmo() {
		Log.d(TAG, "Created new SoundGizmo");
	}

	// Alternative implementation - not tested yet.
	//
	// private void startAlarm() {
	// Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
	// if(alert == null){
	// // alert is null, using backup
	// alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
	// if(alert == null){ // I can't see this ever being null (as always have a
	// default notification) but just incase
	// // alert backup is null, using 2nd backup
	// alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
	// }
	// }
	// Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(),
	// alert);
	// if (ringtone != null) {
	// ringtone.play();
	// }
	// }

	void playAlarmSound(Context context) {
		Log.d(TAG, "playAlarmSound");
		if (this.mediaPlayer == null) {
			this.mediaPlayer = new MediaPlayer();
			if (this.prepareSoundAndCheckPreconditions(context, mediaPlayer)) {
				this.mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
				this.mediaPlayer.setLooping(true);
				try {
					this.mediaPlayer.prepare();
				} catch (IllegalStateException e) {
					Log.e(TAG, "Could not prepare media player for playback.",
							e);
				} catch (IOException e) {
					Log.e(TAG, "Could not prepare media player for playback.",
							e);
				}
				if (!this.mediaPlayer.isPlaying()) {
					this.mediaPlayer.start();
				}
			}
		}
	}

	void stopAlarm() {
		Log.d(TAG, "stopAlarm");
		if (this.mediaPlayer != null) {
			this.mediaPlayer.stop();
		}
		this.mediaPlayer = null;
	}

	private boolean prepareSoundAndCheckPreconditions(Context context,
			MediaPlayer mediaPlayer) {
		Log.d(TAG, "prepareSoundAndCheckPreconditions");
		final AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		if (audioManager == null) {
			Log.e(TAG, "Could not get audio manager, sound will not be played.");
			return false;
		}
		if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) == 0) {
			Log.w(TAG, "Volume set to 0, alarm sound will not be played.");
			return false;
		}
		if (mediaPlayer == null) {
			Log.e(TAG,
					"Media player is not initialized, sound will not be played.");
			return false;
		}
		if (!this.findAndLoadAlarmSound(context, mediaPlayer)) {
			Log.e(TAG,
					"Could not find any alarm sound or could not set the alarm sound as data source in media player, alarm sound will not be played.");
			return false;
		}
		return true;
	}

	private boolean findAndLoadAlarmSound(Context context,
			MediaPlayer mediaPlayer) {
		Log.d(TAG, "findAndLoadAlarmSound");
		Uri alarmUri = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_ALARM);
		if (!this.setDataSourceInMediaPlayer(context, mediaPlayer, alarmUri)) {
			// alert is null or not readable, use notification sound as backup
			alarmUri = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			// I don't think this can ever being null (a default notification
			// should always be present ) but just in case
			if (!this
					.setDataSourceInMediaPlayer(context, mediaPlayer, alarmUri)) {
				// notification sound is null or not readable, use ringtone as
				// last resort
				alarmUri = RingtoneManager
						.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
				if (!this.setDataSourceInMediaPlayer(context, mediaPlayer,
						alarmUri)) {
					return false;
				}
			}
		}
		return true;
	}

	private boolean setDataSourceInMediaPlayer(Context context,
			MediaPlayer mediaPlayer, Uri alarmUri) {
		if (alarmUri == null) {
			return false;
		}
		try {
			mediaPlayer.setDataSource(context, alarmUri);
			return true;
		} catch (IllegalStateException e) {
			Log.w(TAG,
					"Could not load the alarm sound from "
							+ alarmUri.toString(), e);
			return false;
		} catch (IOException e) {
			Log.w(TAG,
					"Could not load the alarm sound from "
							+ alarmUri.toString(), e);
			return false;
		}
	}
}