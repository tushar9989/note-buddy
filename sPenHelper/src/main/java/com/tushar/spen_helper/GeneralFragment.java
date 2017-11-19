package com.tushar.spen_helper;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;
import android.provider.Settings;
import android.util.Log;

public class GeneralFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
	CustomSwitchPreference devadmin,icon_theme_en, usageAccess;
	static int REQUEST_ENABLE = 14612584;
	Preference theme;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_general);
        Preference review = (Preference) findPreference("review");
        Preference version = (Preference) findPreference("version");
        Preference icon = (Preference) findPreference("icon");
        Preference guide = (Preference) findPreference("guide");
        theme = (Preference) findPreference("theme");
        devadmin = (CustomSwitchPreference) findPreference("devadmin");
        icon_theme_en = (CustomSwitchPreference) findPreference("icon_theme_en");
		usageAccess = (CustomSwitchPreference) findPreference("usage_access");
        try {
			PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
			version.setSummary(pInfo.versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
        review.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
            	Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.tushar.spen_helper");
            	Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            	startActivity(intent);
        		return false;
            }
        });
        Preference pro = (Preference) findPreference("pro");
        if(PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("pro", false))
        {
        	getPreferenceScreen().removePreference(pro);
        }
        else
        {
        	getPreferenceScreen().removePreference(theme);
        	getPreferenceScreen().removePreference(icon_theme_en);
        }
        pro.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
            	Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.tushar.spen_pro");
            	Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            	startActivity(intent);
        		return false;
            }
        });
        icon.setOnPreferenceClickListener(new OnPreferenceClickListener(){

			@Override
			public boolean onPreferenceClick(Preference arg0) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.ejeda.com/")));
				return true;
			}
        	
        });
        guide.setOnPreferenceClickListener(new OnPreferenceClickListener(){

			@Override
			public boolean onPreferenceClick(Preference arg0) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://galaxy-note-3.wonderhowto.com/how-to/ultimate-s-pen-customization-tool-for-your-galaxy-note-3-0157073/")));
				return true;
			}
        	
        });
        theme.setOnPreferenceClickListener(new OnPreferenceClickListener(){

			@Override
			public boolean onPreferenceClick(Preference arg0) {
				startActivityForResult(new Intent(arg0.getContext(),IconTheme.class),123);
				return true;
			}
        	
        });
        updateTheme();
        onResume();
    }
    
    void updateTheme()
    {
    	if(icon_theme_en.isChecked())
			theme.setEnabled(true);
		else
			theme.setEnabled(false);
    	SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
    	String pkg = pref.getString("icon_theme", "");
    	try
    	{
    		PackageManager pm = this.getActivity().getPackageManager();
        	ApplicationInfo ai = pm.getApplicationInfo(pkg, 0);
        	theme.setTitle(pm.getApplicationLabel(ai));
        	theme.setSummary(R.string.icon_theme);
        	Drawable d = pm.getApplicationIcon(ai);
        	d = new FastBitmapDrawable(Utilities.createBitmapThumbnail(((BitmapDrawable)d).getBitmap(),this.getActivity()));
        	theme.setIcon(d);
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	
    }
    
    @Override
    public void onResume()
    {
    	super.onResume();
    	devadmin.setChecked(DeviceAdmin.isActive(getActivity()));
		usageAccess.setChecked(!SPenService.needPermissionForBlocking(this.getActivity()));
    	getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }
    
    @Override
	public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		try
		{
			ComponentName mAdminName = new ComponentName(getActivity(), DeviceAdmin.class);
			DevicePolicyManager mDPM = (DevicePolicyManager)getActivity().getSystemService(Context.DEVICE_POLICY_SERVICE);

			if(key.equals("devadmin"))
			{
				if(!DeviceAdmin.isActive(getActivity()))
				{
					Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
					intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
					startActivityForResult(intent, REQUEST_ENABLE);
				}
				else
				{
					mDPM.removeActiveAdmin(mAdminName);
				}
			}

			if(key.equals("usage_access"))
			{
				Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
				startActivity(intent);
			}

			if(key.equals("icon_theme_en"))
			{
				updateTheme();
				onResume();
				Intent service = new Intent(getActivity(),SPenService.class);
				getActivity().stopService(service);
				getActivity().startService(service);
				service = new Intent(getActivity(),HeadsetService.class);
				getActivity().stopService(service);
				getActivity().startService(service);
				SPenFragment.updateTheme(getActivity());
				HeadsetFragment.updateTheme(getActivity());
			}
		}
		catch (Exception e)
		{
			Log.e("Note Buddy", e.getMessage());
		}
	}
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK ) {
        	if(requestCode == REQUEST_ENABLE)
        	{
        		onResume();
        	}
        	if(requestCode == 123)
        	{
        		updateTheme();
    			onResume();
    			SPenService.refreshQuickBar(getActivity());
    			HeadsetService.refreshQuickBar(getActivity());
    			SPenFragment.updateTheme(getActivity());
    			HeadsetFragment.updateTheme(getActivity());
        	}
        }
	}
}
