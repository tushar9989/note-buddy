package com.tushar.spen_helper;

import android.accessibilityservice.AccessibilityService;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.ArrayList;

public class SPenService extends AccessibilityService {

	BroadcastReceiver mReceiver;
	static String selection,alt_selection;
	static String notiftext;
	String current2="Ignore";
	static boolean penOut = false;
	static PendingIntent pint[]=new PendingIntent[6];
	static ArrayList<String> runningApps = new ArrayList<>();
	static String foregroundApp = null;
	MediaPlayer mp = new MediaPlayer();
	protected static final Intent INTENT_REQUEST_REQUERY =
			new Intent(com.tushar.spen_helper.Intent.ACTION_REQUEST_QUERY).putExtra(com.tushar.spen_helper.Intent.EXTRA_ACTIVITY,
					EditActivity.class.getName());
	static RemoteViews abcd;
	static Notification.Builder mBuilder2, mBuilder;
	boolean wasScreenOn = true;
	static boolean screen = true;
	BroadcastReceiver ScreenReceiver;
	IntentFilter screenFilter;

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {

		if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED)
		{
			String pkg = String.valueOf(event.getPackageName());
			//Log.d("NoteBuddy", "Window Package: " + pkg);
			if(runningApps.contains(pkg))
				runningApps.remove(pkg);
			runningApps.add(0, pkg);
			if(runningApps.size() > 10)
				runningApps.remove(10);
			foregroundApp = pkg;
		}
	}

	@Override
	public void onInterrupt() {

	}

	@Override
	public void onCreate() {
		super.onCreate();
		final SharedPreferences settings=getApplicationContext().getSharedPreferences("Config",0);
		final SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		if(!MainActivity.appInstalledOrNot("com.tushar.spen_pro",getApplicationContext()))
		{
			SharedPreferences.Editor edit = pref.edit();
			edit.putBoolean("pro", false);
			edit.putBoolean("icon_theme_en", false);
			edit.apply();
		}

		//Auto Lock

		ScreenReceiver = new BroadcastReceiver(){

			@Override
			public void onReceive(Context arg0, Intent arg1) {
				if(arg1.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                    screen = false;
                    Intent i = new Intent("com.tushar.cmspen.Touch_Block");
                    i.putExtra("blockWhat", "Stop");
                    sendBroadcast(i);
                }
				if(arg1.getAction().equals(Intent.ACTION_SCREEN_ON))
					screen = true;
			}
		};
		screenFilter = new IntentFilter();
		screenFilter.addAction(Intent.ACTION_SCREEN_OFF);
		screenFilter.addAction(Intent.ACTION_SCREEN_ON);
		registerReceiver(ScreenReceiver, screenFilter);


		LaunchableItem xyz = new LaunchableItem("spen_launch");
		if(xyz.load(getApplicationContext()))
			selection = xyz.pkg;
		notiftext = settings.getString("1", "");
		if(pref.getBoolean("pro", false))
			abcd = new RemoteViews(getApplicationContext().getPackageName(),R.layout.quick_notification_bar);
		else
			abcd = new RemoteViews(getApplicationContext().getPackageName(),R.layout.quick_bar_free);
		refreshNoti(this,false);
		refreshQuickBar(this);
		Intent resultIntent = new Intent(this, MainActivity.class);
		PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(), 14, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		final NotificationManager mNotificationManager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);



		IntentFilter filter = new IntentFilter();
		filter.addAction("com.samsung.pen.INSERT");
		mReceiver = new BroadcastReceiver(){
			@SuppressWarnings("deprecation")
			@Override
			public void onReceive(Context arg0, Intent i) {
				long bootTime = pref.getLong("bootTime", 10);

				if(System.currentTimeMillis() - bootTime <= 2000)
					return;
				if(!i.getBooleanExtra("penInsert", false) && !isInitialStickyBroadcast())
				{
					//Detached
					//Auto Launch
					wasScreenOn = screen;
					penOut = true;
					//Tasker
					SharedPreferences.Editor editor = settings.edit();
					editor.putBoolean("spen", true);
					editor.apply();
					sendBroadcast(INTENT_REQUEST_REQUERY);

					//Sound
					playSound("det_s", pref);

					TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
					if(tm.getCallState()==TelephonyManager.CALL_STATE_IDLE)
						Utilities.autoLaunch(SPenService.this,"spen_launch","cbpref", false);
					else
					if(!pref.getBoolean("ddcall", true))
						Utilities.autoLaunch(SPenService.this,"spen_launch", "cbpref", false);
					if(pref.getBoolean("notifpref", false))
						if(pref.getBoolean("qben", false))
							mNotificationManager.notify(1, mBuilder2.build());
						else
							mNotificationManager.notify(1, mBuilder.build());
				}
				else if(i.getBooleanExtra("penInsert", false) && !isInitialStickyBroadcast())
				{
					//Inserted
					penOut = false;
					mNotificationManager.cancel(1);

					//Launch list Testing
                    Utilities.autoLaunch(SPenService.this, "test_multi3", "spen_att_act_enable", false);

					//Tasker
					SharedPreferences.Editor editor = settings.edit();
					editor.putBoolean("spen",false);
					editor.apply();
					sendBroadcast(INTENT_REQUEST_REQUERY);

					//Sound
					playSound("ins_s", pref);

					//Auto Lock
					if(pref.getBoolean("auto_lock", false))
					{
						if(pref.getBoolean("smart_lock", false))
						{
							if(!wasScreenOn)
								DeviceAdmin.lock(getApplicationContext());
						}
						else
						{
							DeviceAdmin.lock(getApplicationContext());
						}
					}

					if(android.os.Build.VERSION.SDK_INT < 21)
					{
						ActivityManager am = (ActivityManager) SPenService.this.getSystemService(ACTIVITY_SERVICE);
						RunningTaskInfo info;
						if(am.getRunningTasks(1).size() != 0)
						{
							info = am.getRunningTasks(1).get(0);
							current2 = info.topActivity.getPackageName();
							if(!current2.equals("Ignore") && pref.getBoolean("cbpref", false) && (current2.equals(selection) || current2.equals(alt_selection)) && pref.getBoolean("pro", false))
							{
								Utilities.LaunchComponent(am.getRunningTasks(2).get(1).topActivity.getPackageName(), getApplicationContext());
							}
						}
					}
					else
					{
						if(foregroundApp != null && pref.getBoolean("pro", false))
						{
							if(pref.getBoolean("cbpref", false) && (foregroundApp.equals(selection) || foregroundApp.equals(alt_selection)))
							{
								Utilities.LaunchComponent(runningApps.get(1), getApplicationContext());
							}
						}
						else if(pref.getBoolean("pro", false))
						{
							Toast.makeText(getApplicationContext(), R.string.lollipop_auto_warning,Toast.LENGTH_LONG).show();
						}
					}

					Utilities.autoLaunch(SPenService.this, "spen_att_act", "spen_att_act_enable", false);
				}
			}
		};
		registerReceiver(mReceiver, filter);

		IntentFilter buttonFilter = new IntentFilter();
		buttonFilter.addAction("com.tushar.cm_spen.SPEN_EVENT");
		BroadcastReceiver buttonReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				int ev_code = intent.getIntExtra("EVENT_CODE", 0);
				String item_name = "";
				switch(ev_code)
				{
					case 1:
						//Toast.makeText(SPenService.this, "Button Press", Toast.LENGTH_SHORT).show();
						item_name = "button_single";
						break;
					case 2:
						//Toast.makeText(SPenService.this, "Long Button Press", Toast.LENGTH_SHORT).show();
						item_name = "button_long";
						break;
					case 3:
						//Toast.makeText(SPenService.this, "Double Button Press", Toast.LENGTH_SHORT).show();
						item_name = "button_double";
				}
				if(pref.getBoolean("button_features", false))
				{
					Utilities.autoLaunch(SPenService.this, item_name, item_name + "_enable", true);
				}
			}

		};
		registerReceiver(buttonReceiver, buttonFilter);
	}

	void playSound(String key, SharedPreferences pref)
	{
		if(pref.getBoolean("sounden", false))
		{
			key = pref.getString(key, "");
			if(key != null && !(key.equals("")))
			{
				try {
					mp.reset();
					mp.setDataSource(key);
					int currVolume = pref.getInt("volume", 10);
					float log1 = (float)(Math.log(10 - currVolume) / Math.log(10));
					if(pref.getBoolean("sound_channel", false))
						mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
					else
						mp.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
					mp.prepare();
					if(pref.getBoolean("pro", false))
						mp.setVolume(1 - log1, 1 - log1);
					mp.start();
					Handler h = new Handler();
					h.postDelayed(new Runnable() {
						@Override
						public void run()
						{
							mp.reset();
						}
					}, mp.getDuration());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	static void refreshQuickBar(Context ctx)
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
		for(int i=0;i<6;i++)
		{
			try
			{
				LaunchableItem item = new LaunchableItem("sqbapp"+i);
				if(item.load(ctx))
				{
					if(item.application)
					{
						if(MainActivity.appInstalledOrNot(item.pkg, ctx))
							pint[i]=PendingIntent.getActivity(ctx, i, item.intent, PendingIntent.FLAG_CANCEL_CURRENT);
						else
						{
							pint[i]=PendingIntent.getActivity(ctx, i, new Intent(ctx,MainActivity.class), PendingIntent.FLAG_CANCEL_CURRENT);
							item.delete(ctx);
						}
					}
					else
						pint[i]=PendingIntent.getActivity(ctx, i, item.intent, PendingIntent.FLAG_CANCEL_CURRENT);
				}
				else
				{
					if(!item.rem)
						pint[i]=PendingIntent.getActivity(ctx, i, new Intent(ctx,MainActivity.class), PendingIntent.FLAG_CANCEL_CURRENT);
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
			Utilities.setQuickBarIcon(abcd, "sqbapp0", ctx, R.id.icon1);
			Utilities.setQuickBarIcon(abcd, "sqbapp1", ctx, R.id.icon2);
			Utilities.setQuickBarIcon(abcd, "sqbapp2", ctx, R.id.icon3);
			Utilities.setQuickBarIcon(abcd, "sqbapp3", ctx, R.id.icon4);
			Utilities.setQuickBarIcon(abcd, "sqbapp4", ctx, R.id.icon5);
			Utilities.setQuickBarIcon(abcd, "sqbapp5", ctx, R.id.icon6);

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
			SharedPreferences settings=ctx.getSharedPreferences("Config",0);
			if(settings.getBoolean("spen", false) && pref.getBoolean("notifpref", false))
			{
				if(pref.getBoolean("qben", false))
					mNotificationManager.notify(1, mBuilder2.build());
				else
					mNotificationManager.notify(1, mBuilder.build());
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	static void setAutoClose(String pkg)
	{
		selection = pkg;
	}

	static void refreshNoti(Context ctx, boolean mode)
	{
		SharedPreferences boob=PreferenceManager.getDefaultSharedPreferences(ctx);
		mBuilder =
				new Notification.Builder(ctx)
						.setWhen(0)
						.setOngoing(true)
						.setContentTitle(notiftext)
						.setContentText("")
						.setPriority(Notification.PRIORITY_MAX);
		if(boob.getBoolean("transparent", false))
		{
			mBuilder.setSmallIcon(R.drawable.transparent);
		}
		else
		{
			mBuilder.setSmallIcon(R.drawable.notify_spen);
			mBuilder.setTicker(notiftext);
		}
		mBuilder2 =
				new Notification.Builder(ctx)
						.setWhen(0)
						.setOngoing(true)
						.setContentTitle(notiftext)
						.setContentText("")
						.setPriority(Notification.PRIORITY_MAX);
		if(boob.getBoolean("transparent", false))
		{
			mBuilder2.setSmallIcon(R.drawable.transparent);
		}
		else
		{
			mBuilder2.setSmallIcon(R.drawable.notify_spen);
			mBuilder2.setTicker(notiftext);
		}
		mBuilder2.setContent(abcd);
		SharedPreferences settings=ctx.getSharedPreferences("Config",0);
		NotificationManager mNotificationManager =
				(NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		if(settings.getBoolean("spen", false))
		{
			if(mode)
			{
				if(boob.getBoolean("notifpref", false))
					if(boob.getBoolean("qben", false))
						mNotificationManager.notify(1, mBuilder2.build());
					else
						mNotificationManager.notify(1, mBuilder.build());
			}
			else
			{
				mNotificationManager.cancel(1);
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(ScreenReceiver != null)
			unregisterReceiver(ScreenReceiver);
		if(mReceiver != null)
			unregisterReceiver(mReceiver);
	}}
