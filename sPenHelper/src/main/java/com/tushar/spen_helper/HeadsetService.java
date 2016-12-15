package com.tushar.spen_helper;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.ActivityManager.RunningTaskInfo;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.Toast;

public class HeadsetService extends Service {
	BroadcastReceiver mRec;
	static String hselection,headsettext;
    static PendingIntent pint[] = new PendingIntent[6];
    String current2 = "Ignore";
	static Notification.Builder mBuilder2,mBuilder;
	static RemoteViews abcd;
	static boolean headOut = false;
	BroadcastReceiver testing;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override 
    public void onCreate() { 
		IntentFilter filter = new IntentFilter();
    	filter.addAction(Intent.ACTION_HEADSET_PLUG);
    	filter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
    	filter.addAction(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED);
        mRec = new BroadcastReceiver() {
        	
        	@Override
        	public void onReceive(Context context, Intent intent) {
        		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        		Boolean state1 = settings.getBoolean("state1", false);
        		Boolean state2 = settings.getBoolean("state2", false);
        		Boolean state3 = settings.getBoolean("state3", false);
        		if(intent.getAction().equals(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED) && isInitialStickyBroadcast() == false)
        		{
        			if(intent.getExtras().getInt(BluetoothHeadset.EXTRA_STATE, 0) == BluetoothHeadset.STATE_DISCONNECTED || intent.getExtras().getInt(BluetoothHeadset.EXTRA_STATE, 0) == BluetoothHeadset.STATE_DISCONNECTING)
        			{
        				if(state2 == false && state3 == false)
        					HeadsetDisconnected();
        				state1 = false;
        				SharedPreferences.Editor editor = settings.edit();
        				editor.putBoolean("state1",state1);
        				editor.commit();
        			}
        			else if(intent.getExtras().getInt(BluetoothHeadset.EXTRA_STATE, 0) == BluetoothHeadset.STATE_CONNECTED)
        			{
        				if(state2 == false && state3 == false)
        					HeadsetConnected();
        				state1 = true;
        				SharedPreferences.Editor editor = settings.edit();
        				editor.putBoolean("state1",state1);
        				editor.commit();
        			}
        		}
        		if(intent.getAction().equals(Intent.ACTION_HEADSET_PLUG) && isInitialStickyBroadcast() == false) {
                    //Toast.makeText(HeadsetService.this, String.valueOf(intent.getExtras().getInt("state")), Toast.LENGTH_LONG).show();
					Toast.makeText(HeadsetService.this, "Headset", Toast.LENGTH_LONG).show();
                    if (intent.getExtras().getInt("state") == 0) {
                        if (state1 == false && state3 == false)
                            HeadsetDisconnected();
                        state2 = false;
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("state2", state2);
                        editor.commit();
                    } else {
                        if (state1 == false && state3 == false)
                            HeadsetConnected();
                        state2 = true;
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("state2", state2);
                        editor.commit();
                    }
                }
        		if(intent.getAction().equals(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED) && isInitialStickyBroadcast() == false)
        		{
        			if(intent.getExtras().getInt(BluetoothHeadset.EXTRA_STATE, 0) == BluetoothHeadset.STATE_DISCONNECTED || intent.getExtras().getInt(BluetoothHeadset.EXTRA_STATE, 0) == BluetoothHeadset.STATE_DISCONNECTING)
        			{
        				if(state1 == false && state2 == false)
        					HeadsetDisconnected();
        				state3 = false;
        				SharedPreferences.Editor editor = settings.edit();
        				editor.putBoolean("state3",state3);
        				editor.commit();
        			}
        			else if(intent.getExtras().getInt(BluetoothHeadset.EXTRA_STATE, 0) == BluetoothHeadset.STATE_CONNECTED)
        			{
        				if(state1 == false && state2 == false)
        					HeadsetConnected();
        				state3 = true;
        				SharedPreferences.Editor editor = settings.edit();
        				editor.putBoolean("state3",state3);
        				editor.commit();
        			}
        		}
        	}
        	
        };
        registerReceiver( mRec, filter );
    }
	
	public void HeadsetConnected()
	{
		final SharedPreferences settings=getApplicationContext().getSharedPreferences("Config",0);
		headOut = true;
		LaunchableItem xyz = new LaunchableItem("happ_selection");
		if(xyz.load(getApplicationContext()))
			hselection = xyz.pkg;
		headsettext = settings.getString("headsettext", "");
		if(headsettext.equals(""))
		{
			headsettext="Headset has been connected";
		}
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		if(MainActivity.appInstalledOrNot("com.tushar.spen_pro",getApplicationContext()) == false)
		{
			SharedPreferences.Editor edit = pref.edit();
			edit.putBoolean("pro", false);
			edit.putBoolean("icon_theme_en", false);
			edit.commit();
		}
		if(pref.getBoolean("pro", false))
			abcd = new RemoteViews(getApplicationContext().getPackageName(),R.layout.quick_notification_bar);
		else
			abcd = new RemoteViews(getApplicationContext().getPackageName(),R.layout.quick_bar_free);
		refreshNoti(this,false);
		refreshQuickBar(this);
		Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
		PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(), 13, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		final NotificationManager mNotificationManager =
				(NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
		SharedPreferences boob=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		if(boob.getBoolean("hnotifpref", false))
			if(boob.getBoolean("hqben", false))
				mNotificationManager.notify(2, mBuilder2.build());
			else
				mNotificationManager.notify(2, mBuilder.build());
		Utilities.autoLaunch(HeadsetService.this, "happ_selection","hcbpref", false);
	}
	
	public void HeadsetDisconnected()
	{
		headOut = false;
		final NotificationManager mNotificationManager =
				(NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(2);
		SharedPreferences boob=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		Boolean hcbpref = boob.getBoolean("hcbpref", false);
		//ActivityManager am = (ActivityManager) HeadsetService.this.getSystemService(ACTIVITY_SERVICE);
		
		//Temp
		AudioManager mAudioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
		if (mAudioManager.isMusicActive()) {
			Intent i = new Intent("com.android.music.musicservicecommand");
			i.putExtra("command", "pause");
			sendBroadcast(i);
			//Intent musicIntent = new Intent("com.maxmpz.audioplayer.API_COMMAND");
			//musicIntent.putExtra("cmd", 2);
			//startService(musicIntent);
		}
		//end
		/*RunningTaskInfo info;
		if(am.getRunningTasks(1).size() != 0)
		{
			info = am.getRunningTasks(1).get(0);
			current2=info.topActivity.getPackageName();
			if(current2!="Ignore" && hcbpref && current2.equals(hselection) && boob.getBoolean("pro", false))
			{
				Utilities.LaunchComponent(am.getRunningTasks(2).get(1).topActivity.getPackageName(),getApplicationContext());
			}
		}*/
		if(android.os.Build.VERSION.SDK_INT < 21)
		{
			ActivityManager am = (ActivityManager) HeadsetService.this.getSystemService(ACTIVITY_SERVICE);
			RunningTaskInfo info;
			if(am.getRunningTasks(1).size() != 0)
			{
				info = am.getRunningTasks(1).get(0);
				current2 = info.topActivity.getPackageName();
				if(current2!="Ignore" && hcbpref && current2.equals(hselection) && boob.getBoolean("pro", false))
				{
					Utilities.LaunchComponent(am.getRunningTasks(2).get(1).topActivity.getPackageName(), getApplicationContext());
				}
			}
		}
		else
		{
			if(SPenService.foregroundApp != null && boob.getBoolean("pro", false))
			{
				if(hcbpref && SPenService.foregroundApp.equals(hselection))
				{
					Utilities.LaunchComponent(SPenService.runningApps.get(1), getApplicationContext());
				}
			}
			else if(boob.getBoolean("pro", false))
			{
				Toast.makeText(getApplicationContext(), R.string.lollipop_auto_warning, Toast.LENGTH_LONG).show();
			}
		}
		Utilities.autoLaunch(HeadsetService.this,"headset_rem_act","headset_rem_act_enable", false);
	}
	
	@SuppressWarnings("deprecation")
	static void refreshQuickBar(Context ctx)
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
		for(int i=0;i<6;i++)
		{
			try
			{
				LaunchableItem item = new LaunchableItem("hqbapp"+i);
				if(item.load(ctx))
                {
                    if(item.application)
                    {
                        if(MainActivity.appInstalledOrNot(item.pkg, ctx))
                            pint[i]=PendingIntent.getActivity(ctx, i + 6, item.intent, PendingIntent.FLAG_CANCEL_CURRENT);
                        else
                        {
                            pint[i]=PendingIntent.getActivity(ctx, i + 6, new Intent(ctx,MainActivity.class), PendingIntent.FLAG_CANCEL_CURRENT);
                            item.delete(ctx);
                        }
                    }
                    else
                        pint[i]=PendingIntent.getActivity(ctx, i + 6, item.intent, PendingIntent.FLAG_CANCEL_CURRENT);
                }
					//pint[i]=PendingIntent.getActivity(ctx, i+6, item.intent, PendingIntent.FLAG_CANCEL_CURRENT);
				else
				{
					if(item.rem == false)
						pint[i]=PendingIntent.getActivity(ctx, i+6, new Intent(ctx,MainActivity.class), PendingIntent.FLAG_CANCEL_CURRENT);
					else
					{
						pint[i] = null;
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		try {
			Utilities.setQuickBarIcon(abcd, "hqbapp0", ctx, R.id.icon1);
			Utilities.setQuickBarIcon(abcd, "hqbapp1", ctx, R.id.icon2);
			Utilities.setQuickBarIcon(abcd, "hqbapp2", ctx, R.id.icon3);
			Utilities.setQuickBarIcon(abcd, "hqbapp3", ctx, R.id.icon4);
			Utilities.setQuickBarIcon(abcd, "hqbapp4", ctx, R.id.icon5);
			Utilities.setQuickBarIcon(abcd, "hqbapp5", ctx, R.id.icon6);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		try
		{
			abcd.setOnClickPendingIntent(R.id.icon1,pint[0]);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		try
		{
			abcd.setOnClickPendingIntent(R.id.icon2,pint[1]);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		try
		{
			abcd.setOnClickPendingIntent(R.id.icon3,pint[2]);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		try
		{
			abcd.setOnClickPendingIntent(R.id.icon4,pint[3]);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		try
		{
			abcd.setOnClickPendingIntent(R.id.icon5,pint[4]);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		try
		{
			abcd.setOnClickPendingIntent(R.id.icon6,pint[5]);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		try
		{
			mBuilder2.setContent(abcd);
	        NotificationManager mNotificationManager =
	    		    (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
	        AudioManager am = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
	        if(am.isWiredHeadsetOn() && pref.getBoolean("hnotifpref", false))
	        {
	        	if(pref.getBoolean("hqben", false))
	        		mNotificationManager.notify(2, mBuilder2.build());
	        	else
	        		mNotificationManager.notify(2, mBuilder.build());
	        }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("deprecation")
	static void refreshNoti(Context ctx, boolean mode)
	{
		SharedPreferences boob=PreferenceManager.getDefaultSharedPreferences(ctx);
		mBuilder =
		        new Notification.Builder(ctx)
		        .setWhen(0)
		        .setOngoing(true)
		        .setContentTitle(headsettext)
		        .setContentText("")
		        .setPriority(Notification.PRIORITY_MAX);

		if(boob.getBoolean("htransparent", false))
		{
			mBuilder.setSmallIcon(R.drawable.transparent);
		}
		else
		{
			mBuilder.setSmallIcon(R.drawable.notify_earphone);
			mBuilder.setTicker(headsettext);
		}
		mBuilder2 =
		        new Notification.Builder(ctx)
                .setWhen(0)
                .setOngoing(true)
                .setContentTitle(headsettext)
                .setContentText("")
                .setPriority(Notification.PRIORITY_MAX);
		if(boob.getBoolean("htransparent", false))
		{
			mBuilder2.setSmallIcon(R.drawable.transparent);
		}
		else
		{
			mBuilder2.setSmallIcon(R.drawable.notify_earphone);
			mBuilder2.setTicker(headsettext);
		}
		mBuilder2.setContent(abcd);
		NotificationManager mNotificationManager =
    		    (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		AudioManager am = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
		if(am.isWiredHeadsetOn())
        {
			if(mode)
			{
				if(boob.getBoolean("hnotifpref", false)==true)
					if(boob.getBoolean("hqben", false)==true)
						mNotificationManager.notify(2, mBuilder2.build());
					else
						mNotificationManager.notify(2, mBuilder.build());
			}
			else
			{
				mNotificationManager.cancel(2);
			}
        }
	}
	
	static void setAutoClose(String pkg)
	{
		hselection = pkg;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	    unregisterReceiver(mRec);
	}
}
