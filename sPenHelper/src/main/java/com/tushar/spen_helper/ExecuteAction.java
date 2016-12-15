package com.tushar.spen_helper;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.IBinder;
import android.view.KeyEvent;

public class ExecuteAction extends Service {
	String musicPackage = "";
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		String action = intent.getStringExtra("name");
		if(action.equals("home"))
		{
			Intent i = new Intent(Intent.ACTION_MAIN);
			i.addCategory(Intent.CATEGORY_HOME);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			if(intent.hasExtra("net.dinglisch.android.tasker.extras.HOME_PAGE"))
				i.putExtra("net.dinglisch.android.tasker.extras.HOME_PAGE", intent.getIntExtra("net.dinglisch.android.tasker.extras.HOME_PAGE", 0));
			startActivity(i);
		}
        if(action.equals("screenshot"))
        {
            Intent i = new Intent("com.tushar.cmspen.Screenshot");
            sendBroadcast(i);
        }
        if(action.equals("keyboard"))
        {
            Intent i = new Intent("com.tushar.cmspen.KBSWITCH");
            i.putExtra("switchTo", intent.getStringExtra("switchTo"));
            i.putExtra("id", intent.getStringExtra("id"));
            sendBroadcast(i);
        }
        if(action.equals("back"))
        {
            Intent i = new Intent("com.tushar.cmspen.PKE");
            i.putExtra("code", 4);
            sendBroadcast(i);
        }
		if(action.equals("pause"))
		{
			musicPackage = intent.getStringExtra("musicPackage");
			if(musicPackage == null)
				musicPackage = "";
			pauseMusic();
		}
		if(action.equals("play"))
		{
			musicPackage = intent.getStringExtra("musicPackage");
			if(musicPackage == null)
				musicPackage = "";
			playMusic();
		}
		if(action.equals("toggle"))
		{
			musicPackage = intent.getStringExtra("musicPackage");
			if(musicPackage == null)
				musicPackage = "";
			AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			if(am.isMusicActive())
				pauseMusic();
			else
				playMusic();
				
		}
		if(action.equals("webaddr"))
		{
			String address = intent.getStringExtra("com.tushar.spen_helper.webaddress");
			Uri uri = Uri.parse(address);
        	Intent i = new Intent(Intent.ACTION_VIEW, uri);
        	i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	startActivity(i);
		}
		if(action.equals("lock"))
		{
			DeviceAdmin.lock(getApplicationContext());
		}
        if(action.equals("touch_block"))
        {
            Intent i = new Intent("com.tushar.cmspen.Touch_Block");
            i.putExtra("blockWhat", intent.getStringExtra("target"));
            sendBroadcast(i);
        }
		stopSelf();
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	void playMusic()
	{
		Intent mediaEvent = new Intent(Intent.ACTION_MEDIA_BUTTON);
		KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY); 
		mediaEvent.putExtra(Intent.EXTRA_KEY_EVENT, event);
		if(!musicPackage.equals(""))
			mediaEvent.setPackage(musicPackage);
		sendBroadcast(mediaEvent,null);

		new Timer().schedule(new TimerTask() {
		    @Override
		    public void run() {
		        Intent mediaEvent = new Intent(Intent.ACTION_MEDIA_BUTTON);
		        KeyEvent event = new KeyEvent(KeyEvent.ACTION_UP,KeyEvent.KEYCODE_MEDIA_PLAY); 
		        mediaEvent.putExtra(Intent.EXTRA_KEY_EVENT, event);
		        if(!musicPackage.equals(""))
					mediaEvent.setPackage(musicPackage);
		        sendBroadcast(mediaEvent,null);
		    }
		}, 100);
		
		AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		if(!am.isMusicActive())
		{
			Intent i = new Intent("com.android.music.musicservicecommand");
			i.putExtra("command", "play");
			if(!musicPackage.equals(""))
				i.setPackage(musicPackage);
			sendBroadcast(i);
		}
	}
	
	void pauseMusic()
	{
		Intent mediaEvent = new Intent(Intent.ACTION_MEDIA_BUTTON);
		KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PAUSE); 
		mediaEvent.putExtra(Intent.EXTRA_KEY_EVENT, event);
		if(!musicPackage.equals(""))
			mediaEvent.setPackage(musicPackage);
		sendBroadcast(mediaEvent,null);
		
		new Timer().schedule(new TimerTask() {
		    @Override
		    public void run() {
		        Intent mediaEvent = new Intent(Intent.ACTION_MEDIA_BUTTON);
		        KeyEvent event = new KeyEvent(KeyEvent.ACTION_UP,KeyEvent.KEYCODE_MEDIA_PAUSE); 
		        mediaEvent.putExtra(Intent.EXTRA_KEY_EVENT, event);
		        if(!musicPackage.equals(""))
					mediaEvent.setPackage(musicPackage);
		        sendBroadcast(mediaEvent,null);
		    }
		}, 100);
		
		AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		if(am.isMusicActive())
		{
			Intent i = new Intent("com.android.music.musicservicecommand");
			i.putExtra("command", "pause");
			if(!musicPackage.equals(""))
				i.setPackage(musicPackage);
			sendBroadcast(i);
		}
	}
}
