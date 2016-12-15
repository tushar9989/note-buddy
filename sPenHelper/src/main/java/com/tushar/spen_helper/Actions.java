package com.tushar.spen_helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.NumberPicker.Formatter;

public class Actions extends Activity{

	ArrayList<myListItem> items = new ArrayList<>();
	String musicPackage = "";
	String musicTitle = "";
	static final int KB_REQ = 15116;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		items.add(new myListItem(R.drawable.home,getString(R.string.act_home)));
		items.add(new myListItem(R.drawable.ic_action_pause,getString(R.string.act_pause_m)));
		items.add(new myListItem(R.drawable.ic_action_play,getString(R.string.act_play_m)));
		items.add(new myListItem(R.drawable.ic_action_toggle,getString(R.string.act_toggle_m)));
		items.add(new myListItem(R.drawable.website,getString(R.string.act_open_web)));
		items.add(new myListItem(R.drawable.lock,getString(R.string.act_lock)));

		if(MainActivity.appInstalledOrNot("com.tushar.cmspen2", this) || MainActivity.appInstalledOrNot("com.tushar.cmspen", this))
		{
			items.add(new myListItem(R.drawable.ic_action_picture,getString(R.string.act_screenshot)));
			items.add(new myListItem(R.drawable.ic_action_undo,getString(R.string.act_back)));
			items.add(new myListItem(R.drawable.ic_action_keyboard, getString(R.string.act_keyboard)));
			items.add(new myListItem(R.drawable.ic_action_touch_block, getString(R.string.act_tb_s)));
			items.add(new myListItem(R.drawable.ic_action_touch_pad_block, getString(R.string.act_tb_k)));
			items.add(new myListItem(R.drawable.ic_action_both_block, getString(R.string.act_tb_both)));
		}

		setContentView(R.layout.main);
		GridAdapter adapter = new GridAdapter(items, Actions.this);

		GridView gridview = (GridView) findViewById(R.id.gridview);
		gridview.setDrawingCacheEnabled(true);
		gridview.setAdapter(adapter);
		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent_view, View v, int position, long id) {
				String launchable = items.get(position).name;
				if(launchable.equals(getString(R.string.act_home)))
				{
					final Intent i = new Intent(Actions.this,Bhadva.class);
					AlertDialog.Builder alert = new AlertDialog.Builder(Actions.this);
					i.putExtra("name", "home");
					alert.setTitle(R.string.act_home_menu);
					final NumberPicker num = new NumberPicker(Actions.this);
					num.setMaxValue(20);
					num.setMinValue(0);
					class formatter implements Formatter {

						@Override
						public String format(int value) {
							switch(value) {
								case 0:
									return "Default";
								case 1:
									return "Page 1";
								case 2:
									return "Page 2";
								case 3:
									return "Page 3";
								case 4:
									return "Page 4";
								case 5:
									return "Page 5";
								case 6:
									return "Page 6";
								case 7:
									return "Page 7";
								case 8:
									return "Page 8";
								case 9:
									return "Page 9";
								case 10:
									return "Page 10";
								case 11:
									return "Page 11";
								case 12:
									return "Page 12";
								case 13:
									return "Page 13";
								case 14:
									return "Page 14";
								case 15:
									return "Page 15";
								case 16:
									return "Page 16";
								case 17:
									return "Page 17";
								case 18:
									return "Page 18";
								case 19:
									return "Page 19";
								case 20:
									return "Page 20";
							}
							return "Unknown";
						}
					}
					num.setFormatter(new formatter());
					LinearLayout parent = new LinearLayout(Actions.this);
					parent.addView(num);
					parent.setGravity(Gravity.CENTER);
					alert.setView(parent);
					alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							if(num.getValue() != 0)
								i.putExtra("net.dinglisch.android.tasker.extras.HOME_PAGE", num.getValue()-1);
							i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
							Utilities.item = new LaunchableItem(i, Actions.this.getString(R.string.act_home)
									, BitmapFactory.decodeResource(getResources(), R.drawable.home)
									, getIntent().getStringExtra("acact"));
							Intent resultIntent = new Intent();
							setResult(Activity.RESULT_OK,resultIntent);
							finish();
						}
					});

					alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
						}
					});
					alert.show();
				}
				if(launchable.equals(getString(R.string.act_pause_m)))
				{
					new MusicActions("pause").execute();
				}
				if(launchable.equals(getString(R.string.act_play_m)))
				{
					new MusicActions("play").execute();
				}
				if(launchable.equals(getString(R.string.act_toggle_m)))
				{
					new MusicActions("toggle").execute();
				}
				if(launchable.equals(getString(R.string.act_app_shade)))
				{
					final Intent i = new Intent(Actions.this,LauncherActivity.class);
					AlertDialog.Builder alert = new AlertDialog.Builder(Actions.this);
					final EditText input = new EditText(Actions.this);
					alert.setTitle(R.string.shade_name);
					alert.setView(input);
					alert.setMessage(R.string.shade_guide);
					alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							Editable value = input.getText();
							i.putExtra("com.tushar.spen_helper.launchername", value.toString());
							i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
							Utilities.item = new LaunchableItem(i, value.toString()
									, BitmapFactory.decodeResource(getResources(), R.drawable.app_launcher)
									, getIntent().getStringExtra("acact"));
							Intent resultIntent = new Intent();
							setResult(Activity.RESULT_OK,resultIntent);
							finish();
						}
					});
					alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						}
					});
					alert.setIcon(R.drawable.app_launcher);
					AlertDialog dialog = alert.create();
					dialog.show();
				}
				if(launchable.equals(getString(R.string.act_open_web)))
				{
					final Intent i = new Intent(Actions.this,Bhadva.class);
					AlertDialog.Builder alert2 = new AlertDialog.Builder(Actions.this);
					final EditText input2 = new EditText(Actions.this);
					input2.setText("http://");
					alert2.setTitle(R.string.web_addr);
					alert2.setView(input2);
					alert2.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							Editable value = input2.getText();
							i.putExtra("com.tushar.spen_helper.webaddress", value.toString());
							i.putExtra("name", "webaddr");
							i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
							Utilities.item = new LaunchableItem(i, value.toString()
									, BitmapFactory.decodeResource(getResources(), R.drawable.website)
									, getIntent().getStringExtra("acact"));
							Intent resultIntent = new Intent();
							setResult(Activity.RESULT_OK,resultIntent);
							finish();
						}
					});
					alert2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						}
					});
					alert2.setIcon(R.drawable.website);
					AlertDialog dialog = alert2.create();
					dialog.show();
				}
				if(launchable.equals(getString(R.string.act_lock)))
				{
					Intent i = new Intent(Actions.this,Bhadva.class);
					i.putExtra("name", "lock");
					i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
					Utilities.item = new LaunchableItem(i, Actions.this.getString(R.string.act_lock)
							, BitmapFactory.decodeResource(getResources(), R.drawable.lock)
							, getIntent().getStringExtra("acact"));
					Intent resultIntent = new Intent();
					setResult(Activity.RESULT_OK,resultIntent);
					finish();
				}

				if(launchable.equals(getString(R.string.act_screenshot)))
				{
					Intent i = new Intent(Actions.this,Bhadva.class);
					i.putExtra("name", "screenshot");
					i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
					Utilities.item = new LaunchableItem(i, Actions.this.getString(R.string.act_screenshot)
							, BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_picture)
							, getIntent().getStringExtra("acact"));
					Intent resultIntent = new Intent();
					setResult(Activity.RESULT_OK,resultIntent);
					finish();
				}

				if(launchable.equals(getString(R.string.act_keyboard)))
				{
					Intent i = new Intent();
					try
					{
						String mPackage = "com.tushar.cmspen2";
						String mClass = ".KeyboardActivity";
						i.setComponent(new ComponentName(mPackage, mPackage + mClass));
						startActivityForResult(i, KB_REQ);
					}
					catch(Exception e)
					{
						String mPackage = "com.tushar.cmspen";
						String mClass = ".KeyboardActivity";
						i.setComponent(new ComponentName(mPackage, mPackage + mClass));
						startActivityForResult(i, KB_REQ);
					}

				}

				if(launchable.equals(getString(R.string.act_back)))
				{
					Intent i = new Intent(Actions.this, Bhadva.class);
					i.putExtra("name", "back");
					i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
					Utilities.item = new LaunchableItem(i, Actions.this.getString(R.string.act_back)
							, BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_undo)
							, getIntent().getStringExtra("acact"));
					Intent resultIntent = new Intent();
					setResult(Activity.RESULT_OK,resultIntent);
					finish();
				}

				if(launchable.equals(getString(R.string.act_tb_s)) || launchable.equals(getString(R.string.act_tb_k))
						|| launchable.equals(getString(R.string.act_tb_both)))
				{
					Intent i = new Intent(Actions.this, Bhadva.class);
					i.putExtra("name", "touch_block");
                    Bitmap bmp = null;
					if(launchable.equals(getString(R.string.act_tb_s))) {
                        i.putExtra("target", "Screen");
                        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_touch_block);
                    }
                    else if(launchable.equals(getString(R.string.act_tb_k))) {
                        i.putExtra("target", "Keys");
                        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_touch_pad_block);
                    }
                    else if(launchable.equals(getString(R.string.act_tb_both))) {
                        i.putExtra("target", "Both");
                        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_both_block);
                    }
					i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
					Utilities.item = new LaunchableItem(i, launchable
							, bmp
							, getIntent().getStringExtra("acact"));
					setResult(Activity.RESULT_OK);
					finish();
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == KB_REQ)
			if(resultCode == Activity.RESULT_OK)
			{
				Intent i = new Intent(Actions.this,Bhadva.class);
				i.putExtra("name", "keyboard");
				String s_kb;
				if((s_kb = data.getStringExtra("s_kb")) != null)
					i.putExtra("switchTo", s_kb);
				//String id = String.valueOf(Math.random() + (Math.random() * 100));
				String id;
				if((id = data.getStringExtra("name")) != null)
					i.putExtra("id", id);
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
				Utilities.item = new LaunchableItem(i, this.getString(R.string.act_keyboard)
						+ ": " + id
						, BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_keyboard)
						, getIntent().getStringExtra("acact"));
				Intent resultIntent = new Intent();
				setResult(Activity.RESULT_OK,resultIntent);
				finish();
			}
	}

	void pause()
	{
		Intent i = new Intent(Actions.this,Bhadva.class);
		i.putExtra("name", "pause");
		i.putExtra("musicPackage",musicPackage);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
		Utilities.item = new LaunchableItem(i, musicTitle + " :" + this.getString(R.string.act_pause_m)
				, BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_pause)
				, getIntent().getStringExtra("acact"));
		Intent resultIntent = new Intent();
		setResult(Activity.RESULT_OK,resultIntent);
		finish();
	}

	void play()
	{
		Intent i = new Intent(Actions.this,Bhadva.class);
		i.putExtra("name", "play");
		i.putExtra("musicPackage", musicPackage);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
		Utilities.item = new LaunchableItem(i, musicTitle + " :" + this.getString(R.string.act_play_m)
				, BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_play)
				, getIntent().getStringExtra("acact"));
		Intent resultIntent = new Intent();
		setResult(Activity.RESULT_OK,resultIntent);
		finish();
	}

	void toggle()
	{
		Intent i = new Intent(Actions.this,Bhadva.class);
		i.putExtra("name", "toggle");
		i.putExtra("musicPackage",musicPackage);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
		Utilities.item = new LaunchableItem(i, musicTitle + " :" + this.getString(R.string.act_toggle_m)
				, BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_toggle)
				, getIntent().getStringExtra("acact"));
		Intent resultIntent = new Intent();
		setResult(Activity.RESULT_OK, resultIntent);
		finish();
	}

	class MusicActions extends AsyncTask<Void,Void,Void>
	{
		ProgressDialog mDialog = new ProgressDialog(Actions.this);
		Intent main=new Intent(Intent.ACTION_MAIN, null);
		PackageManager pm = getPackageManager();
		MusicAdapter adapter = null;
		GridView grid;
		AlertDialog.Builder select;
		AlertDialog sel;
		String mode = "";

		MusicActions(String mode)
		{
			this.mode = mode;
		}

		@Override
		protected void onPreExecute()
		{
			mDialog.setMessage(Actions.this.getString(R.string.loading));
			mDialog.setProgressStyle(ProgressDialog.THEME_HOLO_DARK);
			mDialog.setIndeterminate(true);
			mDialog.setCancelable(false);
			mDialog.show();
			select = new AlertDialog.Builder(Actions.this);
			grid = new GridView(Actions.this);
			grid.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
			grid.setNumColumns(GridView.AUTO_FIT);
			final float scale = Actions.this.getResources().getDisplayMetrics().density;
			int pixels = (int) (10 * scale + 0.5f);
			grid.setPadding(pixels, pixels, pixels, pixels);
			grid.setVerticalSpacing(pixels);
			grid.setHorizontalSpacing(pixels);
			pixels = (int) (80 * scale + 0.5f);
			grid.setColumnWidth(pixels);
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			main.setAction(Intent.ACTION_MEDIA_BUTTON);
			List<ResolveInfo> launchables = pm.queryBroadcastReceivers(main, 0);
			for(int i = 0; i < launchables.size(); i++)
			{
				for(int j = i + 1; j < launchables.size(); j++)
				{
					if(launchables.get(i).activityInfo.packageName.equals(launchables.get(j).activityInfo.packageName))
					{
						launchables.remove(j);
						j--;
					}
				}
			}
			Collections.sort(launchables, new ResolveInfo.DisplayNameComparator(pm));
			adapter = new MusicAdapter(pm, launchables);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			mDialog.dismiss();
			grid.setAdapter(adapter);
			grid.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
					ResolveInfo launchable=adapter.getItem(position);
					ActivityInfo activity=launchable.activityInfo;
					musicPackage = activity.packageName;
					musicTitle = String.valueOf(launchable.loadLabel(getPackageManager()));
					switch (mode) {
						case "pause":
							pause();
							break;
						case "play":
							play();
							break;
						case "toggle":
							toggle();
							break;
					}
					sel.dismiss();
				}
			});
			select.setView(grid);
			sel = select.create();
			sel.show();
		}
	}

	class MusicAdapter extends ArrayAdapter<ResolveInfo> {
		private PackageManager pm=null;

		MusicAdapter(PackageManager pm, List<ResolveInfo> apps) {
			super(Actions.this, R.layout.selector_item, apps);
			this.pm = pm;
		}

		@Override
		public View getView(int position, View convertView,
							ViewGroup parent) {
			if (convertView == null) {
				convertView = newView(parent);
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

			icon.setImageDrawable(IconTheme.getThemedIcon(getItem(position).activityInfo.packageName, Actions.this, false));
		}
	}

	class GridAdapter extends ArrayAdapter<myListItem> {

		GridAdapter(List<myListItem> apps, Context ctx) {
			super(ctx, R.layout.selector_item, apps);
		}

		@Override
		public View getView(int position, View convertView,
							ViewGroup parent) {
			if (convertView == null) {
				convertView = newView(parent);
			}

			bindView(position, convertView);

			return(convertView);
		}

		private View newView(ViewGroup parent) {
			return(getLayoutInflater().inflate(R.layout.selector_item, parent, false));
		}

		private void bindView(int position, View row) {
			TextView label=(TextView)row.findViewById(R.id.selector_title);

			label.setText(getItem(position).name);

			ImageView icon=(ImageView)row.findViewById(R.id.selector_icon);

			icon.setImageResource(getItem(position).rid);
		}
	}
}
