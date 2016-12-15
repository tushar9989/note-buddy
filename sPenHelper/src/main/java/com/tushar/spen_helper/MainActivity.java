package com.tushar.spen_helper;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Map;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {
	Boolean enable,henable;
	String notiftext;
	ActionBar actionBar;
	private static final int LICENSE_REQ_CODE = 1156;
	ProHandler mHandler;
	private static final int MAX_RETRY = 5;
	ViewPager mViewPager;
	SharedPreferences pref,settings;
	
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
		settings = getSharedPreferences("Config",0);
		mHandler = new ProHandler();
		if(!appInstalledOrNot("com.tushar.cmspen2", MainActivity.this) && !appInstalledOrNot("com.tushar.cmspen", MainActivity.this))
		{
			SharedPreferences.Editor edit = pref.edit();
			edit.putBoolean("button_features", false);
			edit.apply();
		}
		if(!appInstalledOrNot("com.tushar.spen_pro", MainActivity.this))
		{
			SharedPreferences.Editor editor = pref.edit();
			int nagCount = pref.getInt("nagCount", 0);
			if(nagCount == 5) {
				editor.putInt("nagCount", 0);
                editor.apply();
				AlertDialog.Builder builderdonate = new AlertDialog.Builder(this);
				builderdonate.setTitle("Note Buddy");
				builderdonate.setMessage(R.string.pro_encourage);
				builderdonate.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
					}
				});
				builderdonate.setNeutralButton("Google Play", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.tushar.spen_pro");
						Intent intent = new Intent(Intent.ACTION_VIEW, uri);
						startActivity(intent);
					}
				});
				AlertDialog dialog = builderdonate.create();
				dialog.show();
			}
			else
			{
				nagCount++;
				editor.putInt("nagCount", nagCount);
                editor.apply();
			}
			editor.putBoolean("pro", false);
			editor.putBoolean("icon_theme_en", false);
			editor.apply();
			Intent service = new Intent(this,SPenService.class);
			this.stopService(service);
			this.startService(service);
			service = new Intent(this,HeadsetService.class);
			this.stopService(service);
			this.startService(service);
			mHandler.sendEmptyMessage(0);
		}
		else
		{
			MainActivity.checkPro(this, LICENSE_REQ_CODE);
		}
		
	}
	
	class ProHandler extends Handler
	{
		public void handleMessage(Message msg) {
			notiftext = settings.getString("1", "");
			if(notiftext.equals(""))
   			{
   				notiftext="S Pen has been detached";
   				SharedPreferences.Editor editor = settings.edit();
   	    	    editor.putString("1", notiftext);
   	    	    editor.apply();
   				
   			}
   			enable = pref.getBoolean("enable", false);
   			henable = pref.getBoolean("henable", false);
   			if(enable)
   			{
   				Intent service = new Intent(MainActivity.this, SPenService.class);
   				startService(service);

   			}
   			if(henable)
   			{
   				Intent service3 = new Intent(MainActivity.this, HeadsetService.class);
   				startService(service3);
   			}
   				
   			actionBar = getActionBar();
   		    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
   		    
   		    Tab spen = actionBar.newTab()
   		            .setText(R.string.spen_tab)
   		            .setIcon(R.drawable.notify_spen)
   		            .setTabListener(new TabListener<>(
   		            		MainActivity.this, "spen", SPenFragment.class,mViewPager));
   		    actionBar.addTab(spen);
   		    
   		    Tab headset = actionBar.newTab()
   		            .setText(R.string.headset_tab)
   		            .setIcon(R.drawable.notify_earphone)
   		            .setTabListener(new TabListener<>(
   		            		MainActivity.this, "headset", HeadsetFragment.class,mViewPager));
   		    actionBar.addTab(headset);
   		    
   		    Tab general = actionBar.newTab()
   		            .setText(R.string.gen_tab)
   		            .setTabListener(new TabListener<>(
   		            		MainActivity.this, "general", GeneralFragment.class,mViewPager));
   		    actionBar.addTab(general);
   		    TabsAdapter t = new TabsAdapter(getFragmentManager());
   		    mViewPager.setAdapter(t);
   		    if(getIntent().getIntExtra("mode", 0) == 2 || getIntent().getIntExtra("tab", 0) == 1)
   	        {
   	        	mViewPager.setCurrentItem(1,false);
   	        	actionBar.setSelectedNavigationItem(headset.getPosition());
   	        }
   		    mViewPager.setOnPageChangeListener(
   		            new ViewPager.SimpleOnPageChangeListener() {
   		                @Override
   		                public void onPageSelected(int position) {
   		                    getActionBar().setSelectedNavigationItem(position);
   		                }
   		            });
   		    if(!pref.getBoolean("theme_compatible", false))
   		    {
   		    	new ThemeCompat().execute();
   		    	SharedPreferences.Editor edit = pref.edit();
   		    	edit.putBoolean("theme_compatible", true);
   		    	edit.apply();
   		    }
		}
	}
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == LICENSE_REQ_CODE)
        	if(resultCode == RESULT_OK)
        	{
        		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        		String l_status = data.getStringExtra("License_Status");
        		if(l_status != null)
        		{
                    switch (l_status) {
                        case "Licensed":
                            if (!pref.getBoolean("pro", false)) {
                                AlertDialog.Builder builderdonate = new AlertDialog.Builder(this);
                                builderdonate.setTitle("Note Buddy");
                                builderdonate.setMessage(R.string.pro_thanks);
                                builderdonate.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                });
                                AlertDialog dialog = builderdonate.create();
                                dialog.show();
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putBoolean("pro", true);
                                editor.putInt("retry_count", 0);
                                editor.apply();
                                Intent service = new Intent(this, SPenService.class);
                                this.stopService(service);
                                this.startService(service);
                                service = new Intent(this, HeadsetService.class);
                                this.stopService(service);
                                this.startService(service);
                            }
                            break;
                        case "Not Licensed": {
                            AlertDialog.Builder builderdonate = new AlertDialog.Builder(this);
                            builderdonate.setTitle("Note Buddy");
                            builderdonate.setMessage(R.string.not_licensed);
                            builderdonate.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                            AlertDialog dialog = builderdonate.create();
                            dialog.show();
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putBoolean("pro", false);
                            editor.putBoolean("icon_theme_en", false);
                            editor.apply();
                            Intent service = new Intent(this, SPenService.class);
                            this.stopService(service);
                            this.startService(service);
                            service = new Intent(this, HeadsetService.class);
                            this.stopService(service);
                            this.startService(service);
                            break;
                        }
                        case "Network Error": {
                            AlertDialog.Builder builderdonate = new AlertDialog.Builder(this);
                            builderdonate.setTitle("Note Buddy");
                            builderdonate.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                            SharedPreferences.Editor editor = pref.edit();
                            int retry_count = pref.getInt("retry_count", 0);
                            retry_count++;
                            if (retry_count == MAX_RETRY) {
                                builderdonate.setMessage(R.string.license_network_error);
                                editor.putBoolean("pro", false);
                                editor.putBoolean("icon_theme_en", false);
                                editor.apply();
                                Intent service = new Intent(this, SPenService.class);
                                this.stopService(service);
                                this.startService(service);
                                service = new Intent(this, HeadsetService.class);
                                this.stopService(service);
                                this.startService(service);
                                retry_count--;
                            } else {
                                builderdonate.setMessage("Error connecting to the Google License Verification Server. " +
                                        "Remaining retries before Pro version features are disabled: " + (MAX_RETRY - retry_count));
                            }
                            editor.putInt("retry_count", retry_count);
                            editor.apply();
                            AlertDialog dialog = builderdonate.create();
                            dialog.show();
                            break;
                        }
                    }
        		}
        		mHandler.sendEmptyMessage(0);
        	}
    }
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		MenuItem item = menu.findItem(R.id.action_donate_google);
		if(appInstalledOrNot("com.tushar.spen_pro", MainActivity.this))
			item.setVisible(false);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.action_exit:
	        finish();
	        return true;
	    /*case R.id.action_donate_paypal:
	    	Uri uri = Uri.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_cart&business=tushar9989%40gmail%2ecom&lc=IN&item_name=S%20Pen%20Helper&amount=5%2e00&currency_code=USD&button_subtype=products&no_note=0&add=1&bn=PP%2dShopCartBF%3abtn_cart_LG%2egif%3aNonHostedGuest");
        	Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        	startActivity(intent);
        	break;*/
	    case R.id.action_donate_google:
	    	Uri uri2 = Uri.parse("https://play.google.com/store/apps/details?id=com.tushar.spen_pro");
        	Intent intent2 = new Intent(Intent.ACTION_VIEW, uri2);
        	startActivity(intent2);
        	break;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
		return true;
	}
	
	public static void checkPro(Activity act, int reqCode)
	{
		try
		{
			Intent i = new Intent("com.tushar.spen_pro.LICENSE_CHECK");
			act.startActivityForResult(i, reqCode);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static boolean appInstalledOrNot(String uri,Context ctx)
    {
        PackageManager pm = ctx.getPackageManager();
        boolean app_installed;
        try
        {
               pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
               app_installed = true;
        }
        catch (PackageManager.NameNotFoundException e)
        {
               app_installed = false;
        }
        return app_installed ;
    }
	class TabsAdapter extends FragmentPagerAdapter {
        public TabsAdapter(FragmentManager fm) {
		super(fm);
        }

		@Override
        public int getCount() {
            return 3;
        }

        @Override
        public Fragment getItem(int position) {
        	switch(position) {
            case 0:
                return new SPenFragment();
            case 1:
                return new HeadsetFragment();
            case 2:
            	return new GeneralFragment();
            default:
            	return new SPenFragment();
            }
        }
    }
	class ThemeCompat extends AsyncTask<Void, Void, Void> {
	    ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
	    ArrayList<String> names = new ArrayList<>();
	    
	    @Override
		protected void onPreExecute()
	    {
	        mDialog.setMessage(MainActivity.this.getString(R.string.theme_compat_loading));
	    	mDialog.setProgressStyle(ProgressDialog.THEME_HOLO_DARK);
	        mDialog.setIndeterminate(true);
	        mDialog.setCancelable(false);
	        mDialog.show();
	    }

		@Override
		protected Void doInBackground(Void... arg0) {
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
			Map<String,?> elements = pref.getAll();
			for (Map.Entry<String, ?> entry : elements.entrySet())
			{
			    if(entry.getKey().contains("intent"))
			    {
			    	if(entry.getValue() instanceof String)
			    	{
			    		try 
			    		{
							Intent i = Intent.parseUri((String)entry.getValue(),0);
							if(i.hasCategory(Intent.CATEGORY_LAUNCHER))
								names.add(entry.getKey().substring(0, entry.getKey().length() - 6));
						} 
			    		catch (URISyntaxException e) {
							e.printStackTrace();
						}
			    	}
			    }
			}
			return null;
		}
		
		@Override
	    protected void onPostExecute(Void v) {
			try
			{
				if(mDialog != null)
					if(mDialog.isShowing())
						mDialog.dismiss();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
			for(int i = 0; i < names.size(); i++)
			{
				edit.putBoolean(names.get(i)+"application", true);
			}
			edit.apply();
	    }
	}
}
