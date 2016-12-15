package com.tushar.spen_helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.DragEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;

public class LauncherActivity extends Activity {
	GridView appsGrid;
	LinearLayout GridL;
	ImageView close,new_app;
	List<LaunchableItem> items;
	static int resultCode = 1337;
	String tag;
	int count = 0;
	BroadcastReceiver mReceiver;
	TimerTask task;
	Timer timer;
	final static int FREE_LIMIT = 3;
	int xPer,yPer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.launcher_layout);
		appsGrid = (GridView) findViewById(R.id.appsGrid);
		GridL = (LinearLayout) findViewById(R.id.GridL);
		close = (ImageView) findViewById(R.id.close_launcher);
		//settings = (ImageView) findViewById(R.id.settings);
		new_app = (ImageView) findViewById(R.id.new_app);
		setup();
		//load_content();
		appsGrid.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				try
				{
					Intent i = items.get(position).intent;
					startActivity(i);
					if(!i.hasExtra("com.tushar.spen_helper.launchername"))
						dismiss();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		});
		close.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v)
			{
				dismiss();
			}
		});
		close.setOnDragListener(new OnDragListener(){

			@Override
			public boolean onDrag(View v, DragEvent e) {
				if(e.getAction() == DragEvent.ACTION_DRAG_STARTED){
					return true;
				}
				if(e.getAction() == DragEvent.ACTION_DRAG_ENTERED)
				{
					close.setBackgroundColor(Color.RED);
				}
				if(e.getAction() == DragEvent.ACTION_DRAG_EXITED)
				{
					close.setBackgroundResource(R.drawable.selector_with_transparency);
				}
				if(e.getAction() == DragEvent.ACTION_DROP)
				{
					ClipData data = e.getClipData();
					if(data.getItemCount() != 0)
					{
						String loc = String.valueOf(data.getItemAt(0).getText());
						final int arg2 = Integer.parseInt(loc);
						AlertDialog.Builder confirm = new AlertDialog.Builder(LauncherActivity.this);
						confirm.setTitle("Confirm Removal");
						confirm.setMessage("Are you sure you want to remove "+ items.get(arg2).title+"?");
						confirm.setIcon(items.get(arg2).icon);
						confirm.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								delete(arg2);
								LaunchableItemAdapter adapter = new LaunchableItemAdapter(LauncherActivity.this,items,LauncherActivity.this, R.layout.launcher_item);
								appsGrid.setAdapter(adapter);
								Toast.makeText(LauncherActivity.this, R.string.deleted, Toast.LENGTH_SHORT).show();
							}
						});
						confirm.setNegativeButton("No", new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface arg0, int arg1) {

							}
						});
						confirm.show();
						close.setBackgroundResource(R.drawable.selector_with_transparency);
						return true;
					}
				}
				return false;
			}

		});
		/*settings.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v)
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(LauncherActivity.this);
				builder.setTitle("Window Settings");
				myListItem items[] = {
					new myListItem(R.drawable.launcher_width,"Width"),
					new myListItem(R.drawable.launcher_height,"Height"),
					new myListItem(R.drawable.launcher_bgd,"Background")
				};
				AppAdapter adapter = new AppAdapter(LauncherActivity.this,items,LauncherActivity.this);
				builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
					}
				});
				builder.show();
			}
		});*/
		new_app.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v)
			{
				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(LauncherActivity.this);
				if(pref.getBoolean("pro", false))
				{
					Utilities.globalChooser(LauncherActivity.this, tag+"item"+items.size(), resultCode);
				}
				else
				{
					if(items.size() < FREE_LIMIT)
					{
						Utilities.globalChooser(LauncherActivity.this, tag+"item"+items.size(), resultCode);
					}
					else
					{
						AlertDialog.Builder builderdonate = new AlertDialog.Builder(LauncherActivity.this);
						builderdonate.setTitle("Note Buddy");
						builderdonate.setMessage(R.string.free_nag);
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
		appsGrid.setOnItemLongClickListener(new OnItemLongClickListener(){
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View view,
										   final int arg2, long arg3) {
				for(int i = 0; i < items.size(); i++)
				{
					if(i != arg2)
					{
						appsGrid.getChildAt(i).setOnDragListener(new OnDragListener(){

							@Override
							public boolean onDrag(View v, DragEvent e) {
								int height = 150, width = 100;
								Bitmap.Config conf = Bitmap.Config.ARGB_8888;
								Bitmap leftLine = Bitmap.createBitmap(width, height, conf);
								Bitmap rightLine = Bitmap.createBitmap(width, height, conf);
								int c = Color.DKGRAY;
								int center;
								for(int i = 0; i < height; i++)
								{
									for(int j = 0; j < width; j++)
									{
										leftLine.setPixel(j, i, c);
										rightLine.setPixel(j, i, c);
									}
								}
								c = Color.parseColor("#33B5E5");
								int tri_no = 3;
								for(int k = 1; k <= tri_no; k++)
								{
									center = (height/tri_no)*(k);
									if(k == tri_no)
										break;
									for(int i = 0; i < 10; i++)
									{
										leftLine.setPixel(i, center - i, c);
										leftLine.setPixel(i, center + i, c);
										rightLine.setPixel(width - i - 1, center - i, c);
										rightLine.setPixel(width - i - 1, center + i, c);
										for(int j = center - i; j < center + i; j++)
										{
											leftLine.setPixel(i, j, c);
											rightLine.setPixel(width - i - 1, j, c);
										}
									}
								}
								if(e.getAction() == DragEvent.ACTION_DRAG_STARTED){
									return true;
								}
								if(e.getAction() == DragEvent.ACTION_DRAG_ENDED)
								{
									for(int i = 0; i < items.size(); i++)
									{
										if(i != arg2)
										{
											appsGrid.getChildAt(i).setOnDragListener(null);
										}
									}
								}
								if(e.getAction() == DragEvent.ACTION_DRAG_ENTERED)
								{
									if(appsGrid.getPositionForView(v) > arg2)
										v.setBackground(new BitmapDrawable(getResources(),leftLine));
									else
										v.setBackground(new BitmapDrawable(getResources(),rightLine));
								}
								if(e.getAction() == DragEvent.ACTION_DRAG_EXITED)
								{
									v.setBackgroundColor(Color.TRANSPARENT);
								}
								if(e.getAction() == DragEvent.ACTION_DROP)
								{
									int position = appsGrid.getPositionForView(v);
									LaunchableItem temp = items.get(arg2);
									items.remove(arg2);
									items.add(position, temp);
									save();
									LaunchableItemAdapter adapter = new LaunchableItemAdapter(LauncherActivity.this,items,LauncherActivity.this, R.layout.launcher_item);
									appsGrid.setAdapter(adapter);
									v.setBackgroundColor(Color.TRANSPARENT);
									return true;
								}
								return false;
							}

						});
					}
				}
				ClipData data = ClipData.newPlainText("location", String.valueOf(arg2));
				DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
				view.startDrag(data, shadowBuilder, view, 0);
				return true;
			}
		});
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		if(pref.getBoolean("pro", false))
		{
			IntentFilter filter = new IntentFilter();
			filter.addAction("com.samsung.pen.INSERT");
			filter.addAction(Intent.ACTION_HEADSET_PLUG);
			mReceiver = new BroadcastReceiver(){
				@Override
				public void onReceive(Context arg0, Intent i) {
					if(i.getBooleanExtra("penInsert", false) && !isInitialStickyBroadcast())
					{
						dismiss();
					}
					if (i.getIntExtra("state",-1) == 0 && !isInitialStickyBroadcast())
					{
						dismiss();
					}
				}
			};
			registerReceiver(mReceiver, filter);
		}
		timer = new Timer();
		task = new TimerTask(){
			public void run() {
				dismiss();
			}
		};
		timer.schedule(task, 120000);
	}

	void delete(int location)
	{
		items.get(location).delete(LauncherActivity.this);
		items.remove(location);
		for(int i = 0;i < (items.size() - location); i++)
			if(items.size() != 0 && items.size() > (location+i))
			{
				items.get(location+i).tag = tag+"item"+(location+i);
				items.get(location+i).save(LauncherActivity.this);
			}
		LaunchableItem temp = new LaunchableItem(tag+"item"+items.size());
		temp.delete(LauncherActivity.this);
		save();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCodeNumber,Intent data)
	{
		if(resultCodeNumber == RESULT_OK)
			if(requestCode == resultCode)
			{
				add_item();
			}
	}

	void add_item()
	{
		if(Utilities.item.intent.hasExtra("com.tushar.spen_helper.launchername"))
		{
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
			if(pref.getBoolean("pro", false))
			{
				items.add(Utilities.item);
				items.get(items.size()-1).save(LauncherActivity.this);
				save();
				items.get(items.size()-1).load(LauncherActivity.this);
				LaunchableItemAdapter adapter = new LaunchableItemAdapter(LauncherActivity.this,items,LauncherActivity.this, R.layout.launcher_item);
				appsGrid.setAdapter(adapter);
				Toast.makeText(this, R.string.app_selected, Toast.LENGTH_SHORT).show();
			}
			else
			{
				AlertDialog.Builder builderdonate = new AlertDialog.Builder(this);
				builderdonate.setTitle("Note Buddy");
				builderdonate.setMessage(R.string.free_nag2);
				builderdonate.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
					}
				});
				AlertDialog dialog = builderdonate.create();
				dialog.show();
			}
		}
		else
		{
			items.add(Utilities.item);
			items.get(items.size()-1).save(LauncherActivity.this);
			save();
			items.get(items.size()-1).load(LauncherActivity.this);
			LaunchableItemAdapter adapter = new LaunchableItemAdapter(LauncherActivity.this,items,LauncherActivity.this, R.layout.launcher_item);
			appsGrid.setAdapter(adapter);
			Toast.makeText(this, R.string.app_selected, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if(mReceiver != null)
			unregisterReceiver(mReceiver);
		timer.cancel();
	}

	@SuppressWarnings("SuspiciousNameCombination")
	void setup()
	{
		tag = getIntent().getStringExtra("com.tushar.spen_helper.launchername");
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		this.setTitle(tag);
		load_content();
		GridL.setLayoutParams(new FrameLayout.LayoutParams((int)((xPer * 0.01) * size.x)
				,/*(int)((yPer*0.01)*size.y)))*/LayoutParams.WRAP_CONTENT));
		int width = (int) (((xPer * 0.01) * size.x) / 11);
		RelativeLayout.LayoutParams param = (android.widget.RelativeLayout.LayoutParams) new_app.getLayoutParams();
		param.height = width;
		new_app.setLayoutParams(param);
		param = (android.widget.RelativeLayout.LayoutParams) close.getLayoutParams();
		param.height = width;
		close.setLayoutParams(param);
	}

	void save()
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor edit = pref.edit();
		for(int i = 0; i < items.size(); i++)
		{
			items.get(i).tag = tag+"item"+i;
			items.get(i).save(this);
		}
		edit.putInt(tag, items.size());
		edit.putInt(tag+"xPer", xPer);
		edit.putInt(tag+"yPer", yPer);
		edit.apply();
	}

	void load_content()
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		count = pref.getInt(tag, 0);
		xPer = /*pref.getInt(tag+"xPer", 75)*/80;
		yPer = /*pref.getInt(tag+"yPer", 75)*/80;
		items = new ArrayList<>();
		if(pref.getBoolean("pro", false))
		{
			for(int i = 0; i < count; i++)
			{
				items.add(new LaunchableItem(tag+"item"+i));
				items.get(i).load(this);
			}
		}
		else
		{
			if(count < FREE_LIMIT)
			{
				for(int i = 0; i < count; i++)
				{
					items.add(new LaunchableItem(tag+"item"+i));
					items.get(i).load(this);
				}
			}
			else
			{
				for(int i = 0; i < FREE_LIMIT; i++)
				{
					items.add(new LaunchableItem(tag+"item"+i));
					items.get(i).load(this);
				}
			}
		}
		int check = 0;
		while(check < items.size())
		{
			items.get(check).load(LauncherActivity.this);
			if(MainActivity.appInstalledOrNot(items.get(check).pkg, LauncherActivity.this))
				check++;
			else if(items.get(check).application)
				delete(check);
			else
				check++;
		}
		LaunchableItemAdapter adapter = new LaunchableItemAdapter(this,items,this, R.layout.launcher_item);
		appsGrid.setAdapter(adapter);
	}

	void dismiss()
	{
		this.finish();
	}
}
