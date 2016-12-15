package com.tushar.spen_helper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

public class launchalot extends Activity {
	//AppAdapter adapter=null;
	//boolean ready = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		new PopulateList().execute();
	}

	class AppAdapter extends ArrayAdapter<ResolveInfo> {
		private PackageManager pm=null;

		AppAdapter(PackageManager pm, List<ResolveInfo> apps) {
			super(launchalot.this, R.layout.selector_item, apps);
			this.pm = pm;
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
			TextView label=(TextView)row.findViewById(R.id.selector_title);

			label.setText(getItem(position).loadLabel(pm));

			ImageView icon=(ImageView)row.findViewById(R.id.selector_icon);

			icon.setImageDrawable(IconTheme.getThemedIcon(getItem(position).activityInfo.packageName,launchalot.this,false));
		}
	}
	class PopulateList extends AsyncTask<Void, Void, Void> {
		AppAdapter adapter=null;
		PackageManager pm=getPackageManager();
		Intent main=new Intent(Intent.ACTION_MAIN, null);
		ProgressDialog mDialog = new ProgressDialog(launchalot.this);

		@Override
		protected void onPreExecute()
		{
			mDialog.setMessage(launchalot.this.getString(R.string.loading));
			mDialog.setProgressStyle(ProgressDialog.THEME_HOLO_DARK);
			mDialog.setIndeterminate(true);
			mDialog.setCancelable(false);
			mDialog.show();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			try{
				main.addCategory(Intent.CATEGORY_LAUNCHER);
				List<ResolveInfo> launchables = pm.queryIntentActivities(main, 0);
				Collections.sort(launchables, new ResolveInfo.DisplayNameComparator(pm));
				adapter = new AppAdapter(pm, launchables);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {
			mDialog.dismiss();
			GridView gridview = (GridView) findViewById(R.id.gridview);
			gridview.setDrawingCacheEnabled(true);
			gridview.setAdapter(adapter);
			gridview.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
					ResolveInfo launchable = adapter.getItem(position);
					ActivityInfo activity = launchable.activityInfo;
					Intent i;
					try
					{
						PackageManager manager = launchalot.this.getPackageManager();
						i = manager.getLaunchIntentForPackage(activity.applicationInfo.packageName);
						i.addCategory(Intent.CATEGORY_LAUNCHER);
						LaunchableItem item = new LaunchableItem(i,(String) activity.loadLabel(pm),((BitmapDrawable)activity.loadIcon(pm)).getBitmap(),getIntent().getStringExtra("name"),true);
						item.pkg = activity.applicationInfo.packageName;
						Utilities.item = item;
						Intent resultIntent = new Intent();
						setResult(Activity.RESULT_OK,resultIntent);
					}
					catch(Exception e)
					{
						setResult(Activity.RESULT_CANCELED);
					}
					finish();
				}
			});
		}
	}
}




