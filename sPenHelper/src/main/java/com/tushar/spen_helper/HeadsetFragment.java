package com.tushar.spen_helper;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.widget.ListAdapter;
import android.widget.Toast;

public class HeadsetFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
	String headsettext;
	static Preference hqbapp1;
	static Preference hqbapp2;
	static Preference hqbapp3;
	static Preference hqbapp4;
	static Preference hqbapp5;
	static Preference hqbapp6;
	static Preference happ_selection;
	Preference blacklist;
	static Preference headset_rem_act;
	static Preference hoff_al_app;
	CustomSwitchPreference hcbpref,henable,hqben,hnotif,htransparent,hoff_al_enable,headset_rem_act_enable;
	EditTextPreference hnotift;
	static final int REQUEST_QB = 13574;
	static final int REQUEST_AL = 123;
	static final int REQUEST_AL_HOFF = 125;
	static final int REQUEST_REM_AL = 998;
	PreferenceScreen hqb;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference_headset);
		int mode = getActivity().getIntent().getIntExtra("mode", 0);
		final SharedPreferences settings=getActivity().getSharedPreferences("Config",0);
		Boolean flag=settings.getBoolean("flag", false);
		headsettext = settings.getString("headsettext", "");
		blacklist = findPreference("blacklist");
		headset_rem_act = findPreference("headset_rem_act");
		Utilities.updatePreference(headset_rem_act, getActivity(), "headset_rem_act");
		happ_selection = findPreference("happ_selection");
		Utilities.updatePreference(happ_selection, getActivity(), "happ_selection");
		hqbapp1 = findPreference("hqbapp1");
		Utilities.updatePreference(hqbapp1, getActivity(), "hqbapp0");
		hqbapp2 = findPreference("hqbapp2");
		Utilities.updatePreference(hqbapp2, getActivity(), "hqbapp1");
		hqbapp3 = findPreference("hqbapp3");
		Utilities.updatePreference(hqbapp3, getActivity(), "hqbapp2");
		hqbapp4 = findPreference("hqbapp4");
		Utilities.updatePreference(hqbapp4, getActivity(), "hqbapp3");
		hqbapp5 = findPreference("hqbapp5");
		Utilities.updatePreference(hqbapp5, getActivity(), "hqbapp4");
		hqbapp6 = findPreference("hqbapp6");
		Utilities.updatePreference(hqbapp6, getActivity(), "hqbapp5");
		hoff_al_app = findPreference("hoff_al_app");
		Utilities.updatePreference(hoff_al_app, getActivity(), "hoff_al_app");
		hcbpref= (CustomSwitchPreference)findPreference("hcbpref");
		hnotif= (CustomSwitchPreference)findPreference("hnotifpref");
		hqben= (CustomSwitchPreference)findPreference("hqben");
		hqb=(PreferenceScreen)findPreference("hqb");
		htransparent=(CustomSwitchPreference)findPreference("htransparent");
		hoff_al_enable= (CustomSwitchPreference) findPreference("hoff_al_enable");
		headset_rem_act_enable = (CustomSwitchPreference) findPreference("headset_rem_act_enable");
		hnotift= (EditTextPreference)findPreference("hnotiftext");
		if(headsettext.equals(""))
		{
			headsettext = "Headset has been connected";
		}
		hnotift.setSummary(headsettext);
		henable= (CustomSwitchPreference)findPreference("henable");
		refresh();
		if(mode==2 && flag)
		{
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean("flag",false);
			editor.apply();
			final Preference preference = findPreference("hqb");
			final PreferenceScreen preferenceScreen = getPreferenceScreen();
			final ListAdapter listAdapter = preferenceScreen.getRootAdapter();
			final int itemsCount = listAdapter.getCount();
			int itemNumber;
			for (itemNumber = 0; itemNumber < itemsCount; ++itemNumber) {
				if (listAdapter.getItem(itemNumber).equals(preference)) {
					preferenceScreen.onItemClick(null, null, itemNumber, 0);
					break;
				}
			}
		}
		happ_selection.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				Utilities.globalChooser(getActivity(), "happ_selection", REQUEST_AL, preference, HeadsetFragment.this);
				return false;
			}
		});
		headset_rem_act.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				Utilities.globalChooser(getActivity(), "headset_rem_act", REQUEST_REM_AL, preference, HeadsetFragment.this);
				return false;
			}
		});
		hoff_al_app.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(final Preference preference) {
				Utilities.globalChooser(getActivity(),  "hoff_al_app",  REQUEST_AL_HOFF, preference, HeadsetFragment.this);
				return false;
			}
		});
		hqbapp1.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				Utilities.globalChooser(getActivity(), "hqbapp0", REQUEST_QB, preference, HeadsetFragment.this);
				return false;
			}
		});
		hqbapp2.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				Utilities.globalChooser(getActivity(), "hqbapp1", REQUEST_QB+1, preference, HeadsetFragment.this);
				return false;
			}
		});
		hqbapp3.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				Utilities.globalChooser(getActivity(), "hqbapp2", REQUEST_QB+2, preference, HeadsetFragment.this);
				return false;
			}
		});
		hqbapp4.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				Utilities.globalChooser(getActivity(), "hqbapp3", REQUEST_QB+3, preference, HeadsetFragment.this);
				return false;
			}
		});
		hqbapp5.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				Utilities.globalChooser(getActivity(), "hqbapp4", REQUEST_QB+4, preference, HeadsetFragment.this);
				return false;
			}
		});
		hqbapp6.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				Utilities.globalChooser(getActivity(), "hqbapp5", REQUEST_QB+5, preference, HeadsetFragment.this);
				return false;
			}
		});
		hnotift.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference,Object v) {
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("headsettext", hnotift.getEditText().getText().toString());
				editor.apply();
				hnotift.setSummary(hnotift.getEditText().getText().toString());
				hnotift.setText(hnotift.getEditText().getText().toString());
				Intent service = new Intent(preference.getContext(), HeadsetService.class);
				(preference.getContext()).stopService(service);
				(preference.getContext()).startService(service);
				return false;
			}
		});
		blacklist.setOnPreferenceClickListener(new OnPreferenceClickListener(){

			@Override
			public boolean onPreferenceClick(Preference arg0) {
				startActivity(new Intent(getActivity(),Blacklist.class));
				return true;
			}

		});
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
		if(!pref.getBoolean("pro", false))
		{
			hqb.removePreference(hqbapp4);
			hqb.removePreference(hqbapp5);
			hqb.removePreference(hqbapp6);
			PreferenceScreen al = (PreferenceScreen) findPreference("al");
			al.removePreference(hoff_al_app);
			al.removePreference(hoff_al_enable);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == Activity.RESULT_OK ) {
			{
				LaunchableItem item = Utilities.item;
				item.save(getActivity());
				if(requestCode == REQUEST_AL)
				{
					Utilities.updatePreference(happ_selection, getActivity(), "happ_selection");
					HeadsetService.setAutoClose(item.pkg);
				}
				if(requestCode == REQUEST_AL_HOFF)
				{
					Utilities.updatePreference(hoff_al_app, getActivity(), "hoff_al_app");
				}
				if(requestCode == REQUEST_REM_AL)
				{
					Utilities.updatePreference(headset_rem_act, getActivity(), "headset_rem_act");
				}
				else
				{
					int number = requestCode - REQUEST_QB;
					switch(number)
					{
						case 0:
							Utilities.updatePreference(hqbapp1, getActivity(), "hqbapp0");
							break;
						case 1:
							Utilities.updatePreference(hqbapp2, getActivity(), "hqbapp1");
							break;
						case 2:
							Utilities.updatePreference(hqbapp3, getActivity(), "hqbapp2");
							break;
						case 3:
							Utilities.updatePreference(hqbapp4, getActivity(), "hqbapp3");
							break;
						case 4:
							Utilities.updatePreference(hqbapp5, getActivity(), "hqbapp4");
							break;
						case 5:
							Utilities.updatePreference(hqbapp6, getActivity(), "hqbapp5");
							break;
					}
					HeadsetService.refreshQuickBar(getActivity());
				}
				Toast.makeText(getActivity(), R.string.app_selected, Toast.LENGTH_SHORT).show();
				onResume();
			}
		}
	}

	void refresh()
	{
		henable.setEnabled(true);
		hnotif.setEnabled(true);
		happ_selection.setEnabled(true);
		hcbpref.setEnabled(true);
		hnotift.setEnabled(true);
		hqben.setEnabled(true);
		hqb.setEnabled(true);
		blacklist.setEnabled(true);
		htransparent.setEnabled(true);
		hoff_al_app.setEnabled(true);
		hoff_al_enable.setEnabled(true);
		headset_rem_act_enable.setEnabled(true);
		headset_rem_act.setEnabled(true);
		if(!henable.isChecked())
		{
			hnotif.setEnabled(false);
			happ_selection.setEnabled(false);
			hcbpref.setEnabled(false);
			hnotift.setEnabled(false);
			hqben.setEnabled(false);
			hqb.setEnabled(false);
			blacklist.setEnabled(false);
			htransparent.setEnabled(false);
			hoff_al_app.setEnabled(false);
			hoff_al_enable.setEnabled(false);
			headset_rem_act_enable.setEnabled(false);
			headset_rem_act.setEnabled(false);
		}
		else
		{
			if(!hcbpref.isChecked())
			{
				happ_selection.setEnabled(false);
				blacklist.setEnabled(false);
				hoff_al_app.setEnabled(false);
				hoff_al_enable.setEnabled(false);
			}
			if(!hnotif.isChecked())
			{
				hnotift.setEnabled(false);
				hqb.setEnabled(false);
				hqben.setEnabled(false);
				htransparent.setEnabled(false);
			}
			if(!hqben.isChecked())
			{
				hqb.setEnabled(false);
			}
			if(!hoff_al_enable.isChecked())
			{
				hoff_al_app.setEnabled(false);
			}
			if(!headset_rem_act_enable.isChecked())
			{
				headset_rem_act.setEnabled(false);
			}
		}
		Utilities.updatePreference(happ_selection, getActivity(), "happ_selection");
		//onResume();
		if (getView() != null)
			getView().invalidate();
	}

	static void updateTheme(Activity act)
	{
		Utilities.updatePreference(hoff_al_app, act, "hoff_al_app");
		Utilities.updatePreference(happ_selection, act, "happ_selection");
		Utilities.updatePreference(hqbapp1, act, "hqbapp0");
		Utilities.updatePreference(hqbapp2, act, "hqbapp1");
		Utilities.updatePreference(hqbapp3, act, "hqbapp2");
		Utilities.updatePreference(hqbapp4, act, "hqbapp3");
		Utilities.updatePreference(hqbapp5, act, "hqbapp4");
		Utilities.updatePreference(hqbapp6, act, "hqbapp5");
		Utilities.updatePreference(headset_rem_act, act, "headset_rem_act");
	}

	@Override
	public void onResume() {
		super.onResume();
		// Set up a listener whenever a key changes
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		// Unregister the listener whenever a key changes
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}
	@Override
	public void onSharedPreferenceChanged(SharedPreferences arg0, String key) {

		if(key.equals("henable"))
		{
			try
			{
				Intent service3 = new Intent(getActivity(), HeadsetService.class);
				if(!henable.isChecked())
				{
					(getActivity()).stopService(service3);
				}
				else
				{
					(getActivity()).startService(service3);
				}
				refresh();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		if(key.equals("hnotifpref"))
		{
			refresh();
			HeadsetService.refreshNoti(getActivity(), hnotif.isChecked());
		}
		if(key.equals("hqben"))
		{
			refresh();
			HeadsetService.refreshNoti(getActivity(), hnotif.isChecked());
		}
		if(key.equals("hcbpref"))
		{
			refresh();
		}
		if(key.equals("htransparent"))
		{
			refresh();
			HeadsetService.refreshNoti(getActivity(), hnotif.isChecked());
		}
		if(key.equals("hoff_al_enable"))
		{
			refresh();
		}
		if(key.equals("headset_rem_act_enable"))
		{
			refresh();
		}
	}
}

