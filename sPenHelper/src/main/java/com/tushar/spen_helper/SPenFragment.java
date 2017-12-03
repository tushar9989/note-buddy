package com.tushar.spen_helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static android.content.ContentValues.TAG;

public class SPenFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
    static final int REQUEST_DETACH = 6384;
    static final int REQUEST_ATTACH = 63855;
    static final int REQUEST_QB = 13574;
    static final int REQUEST_AL = 123;
    static final int REQUEST_AL_SOFF = 125;
    static final int REQUEST_AL_ATT = 777;
    static final int REQUEST_BUTTON_SINGLE = 666, REQUEST_BUTTON_DOUBLE = 667, REQUEST_BUTTON_LONG = 668;
    static Preference spen_att_act, button_single, button_double, button_long, button_bl;
    static Preference sqbapp1;
    static Preference sqbapp2;
    static Preference sqbapp3;
    static Preference sqbapp4;
    static Preference sqbapp5;
    static Preference sqbapp6;
    static Preference fooBarPref;
    static Preference soff_al_app;
    String notiftext;
    Preference det_s, ins_s;
    Preference blacklist;
    CustomSwitchPreference sounden, test, ddcall, enable, qben, notif, transparent, 
            auto_lock, smart_lock, soff_al_enable, spen_att_act_enable, 
            button_single_enable, button_double_enable, button_long_enable, sound_channel;
    SeekBarPreference volume;
    EditTextPreference notift;
    PreferenceScreen sqb;

    static void updateTheme(Activity act) {
        Utilities.updatePreference(soff_al_app, act, "soff_al_app");
        Utilities.updatePreference(fooBarPref, act, "spen_launch");
        Utilities.updatePreference(sqbapp1, act, "sqbapp0");
        Utilities.updatePreference(sqbapp2, act, "sqbapp1");
        Utilities.updatePreference(sqbapp3, act, "sqbapp2");
        Utilities.updatePreference(sqbapp4, act, "sqbapp3");
        Utilities.updatePreference(sqbapp5, act, "sqbapp4");
        Utilities.updatePreference(sqbapp6, act, "sqbapp5");
        Utilities.updatePreference(spen_att_act, act, "spen_att_act");
        Utilities.updatePreference(button_single, act, "button_single");
        Utilities.updatePreference(button_double, act, "button_double");
        Utilities.updatePreference(button_long, act, "button_long");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_spen);
        final SharedPreferences settings = getActivity().getSharedPreferences("Config", 0);

        notiftext = settings.getString("1", "");

        det_s = findPreference("det_s");

        ins_s = findPreference("ins_s");

        sounden = (CustomSwitchPreference) findPreference("sounden");

        ddcall = (CustomSwitchPreference) findPreference("ddcall");

        auto_lock = (CustomSwitchPreference) findPreference("auto_lock");

        smart_lock = (CustomSwitchPreference) findPreference("smart_lock");

        soff_al_enable = (CustomSwitchPreference) findPreference("soff_al_enable");

        spen_att_act_enable = (CustomSwitchPreference) findPreference("spen_att_act_enable");

        button_single_enable = (CustomSwitchPreference) findPreference("button_single_enable");

        button_double_enable = (CustomSwitchPreference) findPreference("button_double_enable");

        button_long_enable = (CustomSwitchPreference) findPreference("button_long_enable");

        sound_channel = (CustomSwitchPreference) findPreference("sound_channel");

        button_bl = findPreference("button_bl");

        fooBarPref = findPreference("app_selection");
        Utilities.updatePreference(fooBarPref, getActivity(), "spen_launch");

        spen_att_act = findPreference("spen_att_act");
        Utilities.updatePreference(spen_att_act, getActivity(), "spen_att_act");

        soff_al_app = findPreference("soff_al_app");
        Utilities.updatePreference(soff_al_app, getActivity(), "soff_al_app");

        blacklist = findPreference("blacklist");

        volume = (SeekBarPreference) findPreference("volume");

        sqbapp1 = findPreference("sqbapp1");
        Utilities.updatePreference(sqbapp1, getActivity(), "sqbapp0");

        sqbapp2 = findPreference("sqbapp2");
        Utilities.updatePreference(sqbapp2, getActivity(), "sqbapp1");

        sqbapp3 = findPreference("sqbapp3");
        Utilities.updatePreference(sqbapp3, getActivity(), "sqbapp2");

        sqbapp4 = findPreference("sqbapp4");
        Utilities.updatePreference(sqbapp4, getActivity(), "sqbapp3");

        sqbapp5 = findPreference("sqbapp5");
        Utilities.updatePreference(sqbapp5, getActivity(), "sqbapp4");

        sqbapp6 = findPreference("sqbapp6");
        Utilities.updatePreference(sqbapp6, getActivity(), "sqbapp5");

        button_single = findPreference("button_single");
        Utilities.updatePreference(button_single, getActivity(), "button_single");

        button_double = findPreference("button_double");
        Utilities.updatePreference(button_double, getActivity(), "button_double");

        button_long = findPreference("button_long");
        Utilities.updatePreference(button_long, getActivity(), "button_long");

        test = (CustomSwitchPreference) findPreference("cbpref");
        notif = (CustomSwitchPreference) findPreference("notifpref");
        transparent = (CustomSwitchPreference) findPreference("transparent");
        qben = (CustomSwitchPreference) findPreference("qben");
        sqb = (PreferenceScreen) findPreference("sqb");
        notift = (EditTextPreference) findPreference("notiftext");
        notift.setSummary(notiftext);
        enable = (CustomSwitchPreference) findPreference("enable");
        refresh();

        fooBarPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(final Preference preference) {
                Utilities.globalChooser(getActivity(), "spen_launch", REQUEST_AL, preference, SPenFragment.this);
                return false;
            }
        });
        spen_att_act.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(final Preference preference) {
                Utilities.globalChooser(getActivity(), "spen_att_act", REQUEST_AL_ATT, preference, SPenFragment.this);
                /*LaunchableItem test = new LaunchableItem("test_multi3");
                test.intent = new Intent(SPenFragment.this.getActivity(), AutoLaunchActivity.class);
                test.title = "Test";
                test.intent.putExtra("listID", "test_multi3");
                test.pkg = "com.tushar.cmspen2";
                test.bitmap = BitmapFactory.decodeResource(SPenFragment.this.getResources(), R.drawable.app_launcher);
                SPenFragment.this.startActivity(test.intent);
                if(!test.save(SPenFragment.this.getActivity()))
                    Toast.makeText(SPenFragment.this.getActivity(), "Save failed", Toast.LENGTH_SHORT).show();*/
                return false;
            }
        });
        soff_al_app.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(final Preference preference) {
                Utilities.globalChooser(getActivity(), "soff_al_app", REQUEST_AL_SOFF, preference, SPenFragment.this);
                return false;
            }
        });
        sqbapp1.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Utilities.globalChooser(getActivity(), "sqbapp0", REQUEST_QB, preference, SPenFragment.this);
                return false;
            }
        });
        sqbapp2.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Utilities.globalChooser(getActivity(), "sqbapp1", REQUEST_QB + 1, preference, SPenFragment.this);
                return false;
            }
        });
        sqbapp3.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Utilities.globalChooser(getActivity(), "sqbapp2", REQUEST_QB + 2, preference, SPenFragment.this);
                return false;
            }
        });
        sqbapp4.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Utilities.globalChooser(getActivity(), "sqbapp3", REQUEST_QB + 3, preference, SPenFragment.this);
                return false;
            }
        });
        sqbapp5.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Utilities.globalChooser(getActivity(), "sqbapp4", REQUEST_QB + 4, preference, SPenFragment.this);
                return false;
            }
        });
        sqbapp6.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Utilities.globalChooser(getActivity(), "sqbapp5", REQUEST_QB + 5, preference, SPenFragment.this);
                return false;
            }
        });
        button_single.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Utilities.globalChooser(getActivity(), "button_single", REQUEST_BUTTON_SINGLE, preference, SPenFragment.this);
                return false;
            }
        });
        button_double.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Utilities.globalChooser(getActivity(), "button_double", REQUEST_BUTTON_DOUBLE, preference, SPenFragment.this);
                return false;
            }
        });
        button_long.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Utilities.globalChooser(getActivity(), "button_long", REQUEST_BUTTON_LONG, preference, SPenFragment.this);
                return false;
            }
        });
        notift.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object v3) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("1", notift.getEditText().getText().toString());
                editor.apply();
                notift.setSummary(notift.getEditText().getText().toString());
                notift.setText(notift.getEditText().getText().toString());
                Intent service = new Intent(preference.getContext(), SPenService.class);
                (preference.getContext()).stopService(service);
                (preference.getContext()).startService(service);
                return false;
            }
        });
        det_s.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Utilities.chooseSound(getActivity(), SPenFragment.this, REQUEST_DETACH);
                //Intent i = new Intent("com.tushar.cm_spen.PKE");
                //i.putExtra("code", 4);
                //SPenFragment.this.getActivity().sendBroadcast(i);
                return false;
            }
        });
        ins_s.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Utilities.chooseSound(getActivity(), SPenFragment.this, REQUEST_ATTACH);
                return false;
            }
        });
        blacklist.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference arg0) {
                startActivity(new Intent(getActivity(), Blacklist.class));
                return true;
            }

        });

        button_bl.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference arg0) {
                Intent i = new Intent(getActivity(), Blacklist.class);
                i.putExtra("button_mode", true);
                startActivity(i);
                return true;
            }

        });

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (!pref.getBoolean("pro", false)) {
            PreferenceScreen s_cat = (PreferenceScreen) findPreference("s_cat");
            s_cat.removePreference(volume);
            sqb.removePreference(sqbapp4);
            sqb.removePreference(sqbapp5);
            sqb.removePreference(sqbapp6);
            PreferenceScreen al = (PreferenceScreen) findPreference("al");
            al.removePreference(soff_al_app);
            al.removePreference(soff_al_enable);
        }
        if (!pref.getBoolean("button_features", false)) {
            PreferenceScreen button_features = (PreferenceScreen) findPreference("button_features");
            getPreferenceScreen().removePreference(button_features);
        }
    }

    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 4096;
        try {
            byte[] bytes = new byte[buffer_size];
            for (int count=0;count!=-1;) {
                count = is.read(bytes);
                if(count != -1) {
                    os.write(bytes, 0, count);
                }
            }
            os.flush();
            is.close();
            os.close();
        } catch (Exception ex) {
            Log.e(TAG,"CS "+ex);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_DETACH || requestCode == REQUEST_ATTACH) {
                MediaPlayer mp = new MediaPlayer();
                String filename = null;
                boolean external = false;
                if (data.getData() != null) {
                    try {
                        if(data.getData().getScheme().equals("file"))
                        {
                            filename = data.getData().getPath();
                            FileInputStream is2 = new FileInputStream(filename);
                            mp.setDataSource(is2.getFD());
                        }
                        else
                        {
                            external = true;
                            InputStream is = getActivity().getContentResolver().openInputStream(data.getData());
                            String[] projection = {MediaStore.MediaColumns.DISPLAY_NAME};
                            Cursor metaCursor = getActivity().getContentResolver().query(data.getData(), projection, null, null, null);

                            if(metaCursor != null && is != null)
                            {
                                metaCursor.moveToFirst();

                                filename = metaCursor.getString(0);

                                metaCursor.close();

                                CopyStream(is, this.getActivity().openFileOutput(filename, 0));

                                FileInputStream is2 = this.getActivity().openFileInput(filename);

                                mp.setDataSource(is2.getFD());

                                is2.close();

                                is.close();

                                try
                                {
                                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

                                    String oldFile;
                                    if (requestCode == REQUEST_DETACH) {
                                        oldFile = preferences.getString("det_s", null);
                                    } else {
                                        oldFile = preferences.getString("ins_s", null);
                                    }
                                    this.getActivity().deleteFile(oldFile);
                                }
                                catch (Exception ignored) {}
                            }
                            else
                            {
                                throw new Exception("Invalid File!");
                            }
                        }

                        mp.prepare();
                        if (mp.getDuration() > 10000) {
                            Toast.makeText(getActivity(), R.string.sound_too_long, Toast.LENGTH_LONG).show();
                        } else {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                            if (requestCode == REQUEST_DETACH) {
                                editor.putString("det_s", filename);
                                det_s.setSummary(filename);
                            } else {
                                editor.putString("ins_s", filename);
                                ins_s.setSummary(filename);
                            }
                            editor.apply();
                            Toast.makeText(getActivity(),R.string.sound_sel, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), R.string.invalid_file, Toast.LENGTH_LONG).show();
                        if(external)
                        {
                            try
                            {
                                this.getActivity().deleteFile(filename);
                            }
                            catch (Exception ignored) {}
                        }

                    }
                }
            } else {
                LaunchableItem item = Utilities.item;
                item.save(getActivity());
                if (requestCode == REQUEST_AL_SOFF) {
                    Utilities.updatePreference(soff_al_app, getActivity(), "soff_al_app");
                }
                if (requestCode == REQUEST_AL) {
                    Utilities.updatePreference(fooBarPref, getActivity(), "spen_launch");
                    SPenService.setAutoClose(item.pkg);
                }
                if (requestCode == REQUEST_AL_ATT) {
                    Utilities.updatePreference(spen_att_act, getActivity(), "spen_att_act");
                }
                if (requestCode == REQUEST_BUTTON_SINGLE) {
                    Utilities.updatePreference(button_single, getActivity(), "button_single");
                }
                if (requestCode == REQUEST_BUTTON_DOUBLE) {
                    Utilities.updatePreference(button_double, getActivity(), "button_double");
                }
                if (requestCode == REQUEST_BUTTON_LONG) {
                    Utilities.updatePreference(button_long, getActivity(), "button_long");
                } else {
                    int number = requestCode - REQUEST_QB;
                    switch (number) {
                        case 0:
                            Utilities.updatePreference(sqbapp1, getActivity(), "sqbapp0");
                            break;
                        case 1:
                            Utilities.updatePreference(sqbapp2, getActivity(), "sqbapp1");
                            break;
                        case 2:
                            Utilities.updatePreference(sqbapp3, getActivity(), "sqbapp2");
                            break;
                        case 3:
                            Utilities.updatePreference(sqbapp4, getActivity(), "sqbapp3");
                            break;
                        case 4:
                            Utilities.updatePreference(sqbapp5, getActivity(), "sqbapp4");
                            break;
                        case 5:
                            Utilities.updatePreference(sqbapp6, getActivity(), "sqbapp5");
                            break;
                    }
                    SPenService.refreshQuickBar(getActivity());
                }
                Toast.makeText(getActivity(), R.string.app_selected, Toast.LENGTH_SHORT).show();
                onResume();
            }
        }
    }

    void refresh() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        det_s.setSummary(settings.getString("det_s", ""));
        ins_s.setSummary(settings.getString("ins_s", ""));
        volume.setSummary(String.valueOf(settings.getInt("volume", 10) * 10) + "%");
        enable.setEnabled(true);
        notif.setEnabled(true);
        fooBarPref.setEnabled(true);
        test.setEnabled(true);
        notift.setEnabled(true);
        qben.setEnabled(true);
        sqb.setEnabled(true);
        det_s.setEnabled(true);
        ins_s.setEnabled(true);
        sounden.setEnabled(true);
        ddcall.setEnabled(true);
        volume.setEnabled(true);
        blacklist.setEnabled(true);
        transparent.setEnabled(true);
        auto_lock.setEnabled(true);
        smart_lock.setEnabled(true);
        soff_al_enable.setEnabled(true);
        soff_al_app.setEnabled(true);
        spen_att_act_enable.setEnabled(true);
        spen_att_act.setEnabled(true);
        button_single.setEnabled(true);
        button_double.setEnabled(true);
        button_long.setEnabled(true);
        sound_channel.setEnabled(true);
        if (!enable.isChecked()) {
            notif.setEnabled(false);
            fooBarPref.setEnabled(false);
            test.setEnabled(false);
            notift.setEnabled(false);
            qben.setEnabled(false);
            sqb.setEnabled(false);
            det_s.setEnabled(false);
            ins_s.setEnabled(false);
            sounden.setEnabled(false);
            ddcall.setEnabled(false);
            volume.setEnabled(false);
            blacklist.setEnabled(false);
            transparent.setEnabled(false);
            auto_lock.setEnabled(false);
            smart_lock.setEnabled(false);
            soff_al_enable.setEnabled(false);
            soff_al_app.setEnabled(false);
            spen_att_act_enable.setEnabled(false);
            spen_att_act.setEnabled(false);
            button_single.setEnabled(false);
            button_double.setEnabled(false);
            button_long.setEnabled(false);
            sound_channel.setEnabled(false);
        } else {
            if (!test.isChecked()) {
                fooBarPref.setEnabled(false);
                ddcall.setEnabled(false);
                blacklist.setEnabled(false);
                soff_al_enable.setEnabled(false);
                soff_al_app.setEnabled(false);
            }
            if (!notif.isChecked()) {
                notift.setEnabled(false);
                sqb.setEnabled(false);
                qben.setEnabled(false);
                transparent.setEnabled(false);
            }
            if (!qben.isChecked()) {
                sqb.setEnabled(false);
            }
            if (!sounden.isChecked()) {
                det_s.setEnabled(false);
                ins_s.setEnabled(false);
                volume.setEnabled(false);
                sound_channel.setEnabled(false);
            }
            if (!auto_lock.isChecked()) {
                smart_lock.setEnabled(false);
            }
            if (!soff_al_enable.isChecked()) {
                soff_al_app.setEnabled(false);
            }
            if (!spen_att_act_enable.isChecked()) {
                spen_att_act.setEnabled(false);
            }
            if (!button_single_enable.isChecked()) {
                button_single.setEnabled(false);
            }
            if (!button_double_enable.isChecked()) {
                button_double.setEnabled(false);
            }
            if (!button_long_enable.isChecked()) {
                button_long.setEnabled(false);
            }
        }
        Intent service = new Intent(getActivity().getApplicationContext(), SPenService.class);
        if (!enable.isChecked()) {
            (getActivity().getApplicationContext()).stopService(service);
        } else {
            (getActivity().getApplicationContext()).startService(service);
        }
        //onResume();
        if (getView() != null)
            getView().invalidate();
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

        if (key.equals("enable")) {
            refresh();
        }
        if (key.equals("notifpref")) {
            refresh();
            SPenService.refreshNoti(getActivity(), notif.isChecked());
        }
        if (key.equals("qben")) {
            refresh();
            SPenService.refreshNoti(getActivity(), notif.isChecked());
        }
        if (key.equals("cbpref")) {
            refresh();
        }
        if (key.equals("ddcall")) {
            refresh();
        }
        if (key.equals("sounden")) {
            refresh();
        }
        if (key.equals("transparent")) {
            refresh();
            SPenService.refreshNoti(getActivity(), notif.isChecked());
        }
        if (key.equals("auto_lock")) {
            refresh();
        }
        if (key.equals("soff_al_enable")) {
            refresh();
        }
        if (key.equals("spen_att_act_enable")) {
            refresh();
        }
        if (key.equals("button_single_enable")) {
            refresh();
        }
        if (key.equals("button_double_enable")) {
            refresh();
        }
        if (key.equals("button_long_enable")) {
            refresh();
        }
    }
}
