package com.tushar.spen_helper;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.widget.Toast;

public class DeviceAdmin extends DeviceAdminReceiver {
	
	static boolean isActive(Context ctx)
	{
		DevicePolicyManager mDPM = (DevicePolicyManager)ctx.getSystemService(Context.DEVICE_POLICY_SERVICE);
		ComponentName mAdminName = new ComponentName(ctx, DeviceAdmin.class);
		return mDPM.isAdminActive(mAdminName);
	}
	
	static void lock(Context ctx)
	{
		DevicePolicyManager mDPM = (DevicePolicyManager)ctx.getSystemService(Context.DEVICE_POLICY_SERVICE);
		ComponentName mAdminName = new ComponentName(ctx, DeviceAdmin.class);
		if(mDPM.isAdminActive(mAdminName))
			mDPM.lockNow();
		else
			Toast.makeText(ctx, "You need to enable Device Administrator first!", Toast.LENGTH_LONG).show();
	}
}