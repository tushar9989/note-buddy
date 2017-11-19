package com.tushar.spen_helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
	Boolean enable,henable;
    @Override
    public void onReceive(final Context context, Intent intent) {
    	SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(context);
		Log.d("Note Buddy", "received");
		enable = pref.getBoolean("enable", false);
		henable = pref.getBoolean("henable", false);
		if(enable)
		{
			Log.d("Note Buddy", "woke service");
			Intent startServiceIntent = new Intent(context, SPenService.class);
	        context.startService(startServiceIntent);
		}
		if(henable)
		{
			Intent service3 = new Intent(context, HeadsetService.class);
			context.startService(service3);
		}
    }
}
