package com.tushar.spen_helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

public class Blacklist extends Activity {
	AppAdapter adapter=null;
	static List<String> apps = new ArrayList<>();
	boolean select_mode = false;
	final static int FREE_LIMIT = 1;

    boolean button_mode = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.blacklist);
        button_mode = getIntent().getBooleanExtra("button_mode", false);
		display_mode();
	}
	
	void display_mode()
	{
		load_bl(this, button_mode);
		RelativeLayout rel_layout = (RelativeLayout) findViewById(R.id.rel_layout);
        rel_layout.setVisibility(View.VISIBLE);
		adapter = new AppAdapter(apps);
		final GridView gridview = (GridView) findViewById(R.id.blGrid);
        gridview.setAdapter(adapter);
        gridview.setOnItemLongClickListener(new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				apps.remove(arg2);
				save_bl(Blacklist.this, button_mode);
				adapter = new AppAdapter(apps);
				gridview.setAdapter(adapter);
				Toast.makeText(Blacklist.this, R.string.rem_bl, Toast.LENGTH_SHORT).show();
				return true;
			}
        	
        });
        ImageView bl_plus = (ImageView) findViewById(R.id.bl_plus);
        bl_plus.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(Blacklist.this);
				if(pref.getBoolean("pro", false) || button_mode)
				{
					select_mode();
				}
				else
				{
					if(apps.size() < FREE_LIMIT)
					{
						select_mode();
					}
					else
					{
						AlertDialog.Builder builderdonate = new AlertDialog.Builder(Blacklist.this);
						builderdonate.setTitle("Note Buddy");
						builderdonate.setMessage(R.string.free_nag_bl);
						builderdonate.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
							}
						});
						AlertDialog dialog = builderdonate.create();
						dialog.show();
					}
				}
			}
        	
        });
        select_mode = false;
	}
	
	void select_mode()
	{
		new chooseBlacklist().execute();
		select_mode = true;
	}
	
	static void save_bl(Context ctx, boolean button_mode)
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
		SharedPreferences.Editor editor = pref.edit();
        String button = "";
        if(button_mode)
            button = "button_";
		for(int i = 0; i < apps.size(); i++)
		{
			editor.putString(button + "blacklist"+i, apps.get(i));
		}
		editor.putInt(button + "blsize", apps.size());
		editor.apply();
	}
	
	static boolean check_bl(String pkg,Context ctx, boolean button_mode) {
		load_bl(ctx, button_mode);
		return apps != null && apps.contains(pkg);
	}
	
	static void load_bl(Context ctx, boolean button_mode)
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
		if(!apps.isEmpty())
			apps.clear();
        if(!button_mode)
        {
            int n = pref.getInt("blsize", 0);
            if(pref.getBoolean("pro", false))
            {
                for(int i = 0; i < n; i++)
                {
                    apps.add(pref.getString("blacklist"+i, ""));
                }
            }
            else
            {
                if(n < FREE_LIMIT)
                {
                    for(int i = 0; i < n; i++)
                    {
                        apps.add(pref.getString("blacklist"+i, ""));
                    }
                }
                else
                {
                    for(int i = 0; i < FREE_LIMIT; i++)
                    {
                        apps.add(pref.getString("blacklist"+i, ""));
                    }
                    save_bl(ctx, button_mode);
                }
            }
        }
		else
        {
            int n = pref.getInt("button_blsize", 0);
            for(int i = 0; i < n; i++)
            {
                apps.add(pref.getString("button_blacklist"+i, ""));
            }
        }
	}
	
	List<String> convertList(List<ResolveInfo> original)
	{
		List<String> temp = new ArrayList<>();
		for(int i = 0; i < original.size(); i++)
		{
			ActivityInfo activity = original.get(i).activityInfo;
			if(!apps.contains(activity.applicationInfo.packageName))
				temp.add(activity.applicationInfo.packageName);
		}
		return temp;
	}
	
	class AppAdapter extends ArrayAdapter<String> {
	    
	    AppAdapter(List<String> apps) {
	      super(Blacklist.this, R.layout.selector_item, apps);
	    }
	    
	    @Override
	    public View getView(int position, View convertView,
	                          ViewGroup parent) {
	      if (convertView==null) {
	        convertView=newView(parent);
	      }
	      
	      bindView(position, convertView);
	      
	      return(convertView);
	    }
	    
	    private View newView(ViewGroup parent) {
	      return(getLayoutInflater().inflate(R.layout.selector_item, parent, false));
	    }
	    
	    private void bindView(int position, View row) {
	    	try
	    	{
	    		PackageManager pm = Blacklist.this.getPackageManager();
	    		ApplicationInfo ai;
	    		ai = pm.getApplicationInfo(getItem(position), 0);
	    		TextView label=(TextView)row.findViewById(R.id.selector_title);
	    		
	    		label.setText(pm.getApplicationLabel(ai));
	    		
	    		ImageView icon=(ImageView)row.findViewById(R.id.selector_icon);
	    		
	    		icon.setImageDrawable(IconTheme.getThemedIcon(ai.packageName, Blacklist.this,false));
	    	}
	    	catch(Exception e)
	    	{
	    		e.printStackTrace();
	    	}
	    }
	  }
	
	class chooseBlacklist extends AsyncTask<Void, Void, Void> {
	    ProgressDialog mDialog = new ProgressDialog(Blacklist.this);
		
	    @Override
		protected void onPreExecute()
	    {
	        mDialog.setMessage(Blacklist.this.getString(R.string.loading));
	    	mDialog.setProgressStyle(ProgressDialog.THEME_HOLO_DARK);
	        mDialog.setIndeterminate(true);
	        mDialog.setCancelable(false);
	        mDialog.show();
	        RelativeLayout rel_layout = (RelativeLayout) findViewById(R.id.rel_layout);
	        rel_layout.setVisibility(View.GONE);
	    }

		@Override
		protected Void doInBackground(Void... arg0) {
			PackageManager pm=getPackageManager();
		    Intent main=new Intent(Intent.ACTION_MAIN, null);
		    main.addCategory(Intent.CATEGORY_LAUNCHER);
			List<ResolveInfo> launchables=pm.queryIntentActivities(main, 0);
        	Collections.sort(launchables, new ResolveInfo.DisplayNameComparator(pm));
        	adapter = new AppAdapter(convertList(launchables));
			return null;
		}
		
		@Override
	    protected void onPostExecute(Void v) {
			mDialog.dismiss();
	    	GridView gridview = (GridView) findViewById(R.id.blGrid);
	        gridview.setAdapter(adapter);
	        gridview.setOnItemClickListener(new OnItemClickListener() {
	            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	            	String launchable=adapter.getItem(position);
	            	apps.add(launchable);
	            	save_bl(Blacklist.this, button_mode);
	            	Toast.makeText(Blacklist.this, R.string.bl_added, Toast.LENGTH_SHORT).show();
	            	display_mode();
	            }
	        });
	    }
	}
	
	@Override
	public void onBackPressed()
	{
		if(select_mode)
			display_mode();
		else
			super.onBackPressed();
	}
}
