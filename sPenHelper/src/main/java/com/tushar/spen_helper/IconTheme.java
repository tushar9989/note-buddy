package com.tushar.spen_helper;

import java.util.Collections;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class IconTheme extends ListActivity {
	IconThemeAdapter adapter;
	PackageManager pm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_list);
		Intent main=new Intent(Intent.ACTION_MAIN);
		main.addCategory("com.anddoes.launcher.THEME");
		pm=getPackageManager();
		List<ResolveInfo> launchables=pm.queryIntentActivities(main, 0);
    	Collections.sort(launchables, new ResolveInfo.DisplayNameComparator(pm)); 
    	adapter=new IconThemeAdapter(pm, launchables);
    	setListAdapter(adapter);
	}
	
	@Override
	  protected void onListItemClick(ListView l, View v,
	                                 int position, long id) {
		ResolveInfo launchable=adapter.getItem(position);
        ActivityInfo activity=launchable.activityInfo;
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("icon_theme", activity.applicationInfo.packageName);
        edit.commit();
        Toast.makeText(this, R.string.theme_sel, Toast.LENGTH_SHORT).show();
        setResult(Activity.RESULT_OK);
        finish();
	}
	
	public static Drawable getThemedIcon(String pkg,Context ctx,boolean isQB)
	{
		boolean found = false;
		try
		{
			String theme = PreferenceManager.getDefaultSharedPreferences(ctx).getString("icon_theme", "");
			if(theme == "")
				throw new Exception();
			if(PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean("icon_theme_en",false) == false)
				throw new Exception();
			Resources res = ctx.getPackageManager().getResourcesForApplication(theme);
			XmlResourceParser parser = res.getXml(res.getIdentifier("appfilter", "xml", theme));
            parser.next();
            while (parser.next() != XmlPullParser.END_DOCUMENT) 
            {
                if (parser.getEventType() == XmlPullParser.START_TAG)
                {
                	String name = parser.getName();
                    if(name.equals("item"))
                    {
                    	String temp = parser.getAttributeValue(null, "component");
                    	if(temp.contains(pkg))
                    	{
                    		String drawable_name = parser.getAttributeValue(null, "drawable");
                    		int drawable_id = res.getIdentifier(drawable_name, "drawable", theme);
                    		parser.close();
                    		found = true;
                    		Drawable d = res.getDrawable(drawable_id);
                    		if(isQB)
                    			return d;
                    		d = new FastBitmapDrawable(Utilities.createBitmapThumbnail(((BitmapDrawable)d).getBitmap(),ctx));
                    		return d;
                    	}
                    }
                }
            }
            if(found == false)
            	throw new Exception();
            parser.close();
		}
		catch(Exception e)
		{
			//e.printStackTrace();
			try
			{
				PackageManager pm = ctx.getPackageManager();
	        	ApplicationInfo ai = pm.getApplicationInfo(pkg, 0);
	        	return pm.getApplicationIcon(ai);
			}
			catch(Exception e2)
			{
				//e2.printStackTrace();
				return ctx.getResources().getDrawable(R.drawable.transparent);
			}
		}
		return ctx.getResources().getDrawable(R.drawable.transparent);
	}
	
	class IconThemeAdapter extends ArrayAdapter<ResolveInfo> {
	    private PackageManager pm=null;
	    
	    IconThemeAdapter(PackageManager pm, List<ResolveInfo> apps) {
	      super(IconTheme.this, R.layout.icon_theme_row, apps);
	      this.pm=pm;
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
	      return(getLayoutInflater().inflate(R.layout.icon_theme_row, parent, false));
	    }
	    
	    private void bindView(int position, View row) {
	      TextView label=(TextView)row.findViewById(R.id.label);
	      
	      label.setText(getItem(position).loadLabel(pm));
	      
	      ImageView icon=(ImageView)row.findViewById(R.id.icon);
	      
	      icon.setImageDrawable(getItem(position).loadIcon(pm));
	    }
	  }

}
