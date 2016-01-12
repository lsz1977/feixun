/**
 * 
 */
package com.cchat.service;

import java.io.IOException;
import java.util.Timer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.cchat.manager.ConnectionManager;

/**
 * @author HD
 * 
 */
public class ConnectionService extends Service {

	private static final String TAG = "ConnectService";
	private Timer mTimer;// = new Timer();

	private int mValue = 0;
	String[] colors = new String[] { "red", "orange", "black" };
	double[] weights = new double[] { 2.3, 1.5, 5.6 };
	private MediaPlayer player;

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, " AidlService onBind()");
		
		if (IConnectService.class.getName().equals(intent.getAction())) {
			return mConnectionManager.onBind();
		}
		return null;
	}
	
	public static ConnectionManager mConnectionManager;
 
	private MediaPlayer ring() throws Exception, IOException {
		Uri alert = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		MediaPlayer player = new MediaPlayer();
		player.setDataSource(this, alert);
		final AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) != 0) {
			player.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
			// player.setLooping(true);
			player.prepare();
			player.start();
		}
		return player;
	}
	
	@Override
	public void onCreate() {
		Log.d(TAG, " AidlService onCreate()");
		super.onCreate();
		//android.os.Debug.waitForDebugger();
		mConnectionManager = new ConnectionManager(this);
		
		/*Toast.makeText(getApplicationContext(), "service start...",
				Toast.LENGTH_SHORT).show();
		try {
			player = ring();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			player.stop();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			player.stop();
		}*/
		
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, " AidlService onDestroy()");
		mConnectionManager.clear();
		super.onDestroy();

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, " AidlService onStartCommand() mValue:" + mValue);
		return START_STICKY;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.d(TAG, " AidlService onUnbind()");
		return super.onUnbind(intent);
	}

	@Override
	public void onRebind(Intent intent) {
		Log.d(TAG, " AidlService onRebind()");
		super.onRebind(intent);
	}
	
}
