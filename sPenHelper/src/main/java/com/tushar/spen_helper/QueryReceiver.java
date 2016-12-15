package com.tushar.spen_helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;


public class QueryReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences settings=context.getSharedPreferences("Config",0);
		SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(context);
		if(pref.getBoolean("enable", false) == true)
		{
			Bundle bundle = intent.getBundleExtra(com.tushar.spen_helper.Intent.EXTRA_BUNDLE);
			Boolean conditionState = bundle.getBoolean("com.tushar.spen_helper.extra.BOOLEAN_STATE");
			Boolean bDetached=settings.getBoolean("spen", false);
					
					if (bDetached==true) {
						
						if (conditionState==true)
		                {	
							setResultCode(com.tushar.spen_helper.Intent.RESULT_CONDITION_SATISFIED);
		                }
		                else
		                {
		                    setResultCode(com.tushar.spen_helper.Intent.RESULT_CONDITION_UNSATISFIED);
		                }
					
					} else {
						
						if (conditionState==true)
		                {
		                    setResultCode(com.tushar.spen_helper.Intent.RESULT_CONDITION_UNSATISFIED);
		                }
		                else
		                {
		                    setResultCode(com.tushar.spen_helper.Intent.RESULT_CONDITION_SATISFIED);
		                }
					}
		}
		else
		{
			Toast.makeText(context, "Please enable S Pen detection from the Application settings first!", Toast.LENGTH_LONG).show();
		}
	}
}
