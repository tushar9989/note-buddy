package com.tushar.spen_helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.File;

public class BootReceiver extends BroadcastReceiver {
	Boolean enable,henable;
    @Override
    public void onReceive(final Context context, Intent intent) {
    	SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(context);
    	if(intent.getAction().equals("com.tushar.cm_spen.BUTTON_ACTIVATE"))
    	{
    		SharedPreferences.Editor edit = pref.edit();
    		edit.putBoolean("button_features", true);
    		edit.commit();
    		context.startActivity(new Intent(context, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    		return;
    	}
        if(intent.getAction().equals(Intent.ACTION_PACKAGE_FULLY_REMOVED))
        {
            if(pref.getBoolean("enable", false))
            {
                SPenService.refreshNoti(context, false);
                SPenService.refreshQuickBar(context);
            }
            if(pref.getBoolean("henable", false))
            {
                HeadsetService.refreshNoti(context, false);
                HeadsetService.refreshQuickBar(context);
            }
            return;
        }
        long bootTime = System.currentTimeMillis();
        SharedPreferences.Editor edit2 = pref.edit();
        edit2.putLong("bootTime", bootTime);
        edit2.commit();
		MainActivity.setAlarm(context);

		enable = pref.getBoolean("enable", false);
		henable = pref.getBoolean("henable", false);
		if(enable)
		{
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
