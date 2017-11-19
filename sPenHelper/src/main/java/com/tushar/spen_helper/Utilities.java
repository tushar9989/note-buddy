package com.tushar.spen_helper;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.ipaulpro.afilechooser.FileChooserActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Service;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;


public class Utilities {
	public static Intent intent;
	public static String name;
	//public static String pkgname;
	//public static Bitmap bitmap;
	public static Drawable icon;
	//public static ShortcutIconResource iconResource;
	public static LaunchableItem item;

	static Bitmap createBitmapThumbnail(Bitmap bitmap, Context context) {
		int sIconWidth;
		int sIconHeight;
		final Resources resources = context.getResources();
		sIconWidth = sIconHeight = (int) resources.getDimension(
				android.R.dimen.app_icon_size);

		int width = sIconWidth;
		int height = sIconHeight;

		final int bitmapWidth = bitmap.getWidth();
		final int bitmapHeight = bitmap.getHeight();
		final float ratio = (float) bitmapWidth / bitmapHeight;

		if (bitmapWidth > bitmapHeight) {
			height = (int) (width / ratio);
		} else if (bitmapHeight > bitmapWidth) {
			width = (int) (height * ratio);
		}

		Bitmap scaledBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);

		float ratioX = width / (float) bitmap.getWidth();
		float ratioY = height / (float) bitmap.getHeight();
		float middleX = width / 2.0f;
		float middleY = height / 2.0f;

		Matrix scaleMatrix = new Matrix();
		scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

		Canvas canvas = new Canvas(scaledBitmap);
		canvas.setMatrix(scaleMatrix);
		if (width < bitmapWidth || height < bitmapHeight)
		{
			canvas.drawBitmap(bitmap, middleX - bitmap.getWidth() / 2, middleY - bitmap.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
		}
		else
		{
			canvas.drawBitmap(bitmap, middleX - bitmap.getWidth() / 2, middleY - bitmap.getHeight() / 2, new Paint());
		}

		return scaledBitmap;
	}

	public static void globalChooser(final Activity myact, final String str, final int resultCode)
	{
		globalChooser(myact, str, resultCode, null, null);
	}

	public static void globalChooser(final Activity myact, final String str, final int resultCode
			, final Preference pref, final PreferenceFragment frag)
	{
		final ArrayList<myListItem> items = new ArrayList<>();
		items.add(new myListItem(R.drawable.action, myact.getString(R.string.action_menu)));
		items.add(new myListItem(R.drawable.applications, myact.getString(R.string.app_menu)));
		items.add(new myListItem(R.drawable.shortcut, myact.getString(R.string.shortcut_menu)));
		items.add(new myListItem(R.drawable.app_launcher, myact.getString(R.string.act_app_shade)));

		if(pref != null)
			items.add(new myListItem(R.drawable.ic_action_cancel, myact.getString(R.string.rem_menu)));

		AppAdapter adapter = new AppAdapter(myact.getApplicationContext(),items,myact);
		AlertDialog.Builder alert = new AlertDialog.Builder(myact);
		alert.setAdapter(adapter, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int position) {
				Intent temp = new Intent();
				if(items.get(position).name.equals(myact.getString(R.string.action_menu)))
				{
					temp = new Intent(myact, Actions.class);
					temp.putExtra("acact", str);
				}
				else if(items.get(position).name.equals(myact.getString(R.string.app_menu)))
				{
					temp = new Intent(myact, launchalot.class);
					temp.putExtra("name", str);
				}
				else if(items.get(position).name.equals(myact.getString(R.string.shortcut_menu)))
				{
					temp = new Intent(myact,Shortcuts.class);
					temp.putExtra("shact", str);
				}
				else if(items.get(position).name.equals(myact.getString(R.string.act_app_shade)))
				{
					final Intent i = new Intent(myact, LauncherActivity.class);
					AlertDialog.Builder alert = new AlertDialog.Builder(myact);
					final EditText input = new EditText(myact);
					alert.setTitle(R.string.shade_name);
					alert.setView(input);
					alert.setMessage(R.string.shade_guide);
					alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							Editable value = input.getText();
							i.putExtra("com.tushar.spen_helper.launchername", value.toString());
							i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
							if (pref == null) {
								Utilities.item = new LaunchableItem(i, value.toString()
										, BitmapFactory.decodeResource(myact.getResources(), R.drawable.app_launcher)
										, str);
                                if(myact instanceof LauncherActivity)
								    ((LauncherActivity) myact).add_item();
                                else
                                {
                                    LaunchableItem item = new LaunchableItem(i, value.toString()
                                            , BitmapFactory.decodeResource(myact.getResources(), R.drawable.app_launcher)
                                            , str);
                                    item.save(myact);
                                    ((AutoLaunchActivity) myact).add_item();
                                }
							}
							else {
								LaunchableItem item = new LaunchableItem(i, value.toString()
										, BitmapFactory.decodeResource(myact.getResources(), R.drawable.app_launcher)
										, str);
								item.save(myact);
								Toast.makeText(myact, R.string.app_selected, Toast.LENGTH_SHORT).show();
								Utilities.updatePreference(pref, myact, str);
								if (frag.getView() != null)
									frag.getView().invalidate();
								if (frag instanceof HeadsetFragment)
									HeadsetService.refreshQuickBar(myact);
								else if (frag instanceof SPenFragment)
									SPenService.refreshQuickBar(myact);
							}
						}
					});
					alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						}
					});
					alert.setIcon(R.drawable.app_launcher);
					AlertDialog dialog2 = alert.create();
					dialog2.show();
				}
				else if(items.get(position).name.equals(myact.getString(R.string.rem_menu)))
				{
					LaunchableItem temp_item = new LaunchableItem(str);
					temp_item.delete(myact);
					temp_item.setRemoved(myact);
					Utilities.updatePreference(pref, myact, str);
					if(frag.getView() != null)
						frag.getView().invalidate();
					if(frag instanceof HeadsetFragment)
						HeadsetService.refreshQuickBar(myact);
					else if(frag instanceof SPenFragment)
						SPenService.refreshQuickBar(myact);
					Toast.makeText(myact, R.string.app_removed, Toast.LENGTH_SHORT).show();
				}
				try
				{
					if(frag == null)
						myact.startActivityForResult(temp, resultCode);
					else
						frag.startActivityForResult(temp, resultCode);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		});
		alert.setTitle(R.string.menu_title);
		alert.show();
	}

	public static void updatePreference(Preference pref,Activity myact,String str)
	{
		LaunchableItem item = new LaunchableItem(str);
		if(item.load(pref.getContext()))
		{
			pref.setTitle(item.title);
			pref.setIcon(item.icon);
		}
		else
		{
			if(item.rem)
				pref.setTitle(R.string.menu_blank);
			else
				pref.setTitle(R.string.sm_app_title);
			Drawable d = new FastBitmapDrawable(Utilities.createBitmapThumbnail(
					BitmapFactory.decodeResource(myact.getResources(), R.drawable.new_icon)
					, myact.getApplicationContext()
			));
			pref.setIcon(d);
		}
	}

	public static void autoLaunch(final Service service, String str, String lpreference, boolean button_mode)
	{
		SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(service.getApplicationContext());
		ActivityManager am = (ActivityManager) service.getSystemService(Context.ACTIVITY_SERVICE);
		RunningTaskInfo info;
		boolean bl_possible = true;
		String currentApp;
		if(Build.VERSION.SDK_INT < 21)
		{
			//noinspection deprecation
			info = am.getRunningTasks(1).get(0);
			currentApp = info.topActivity.getPackageName();
		}
		else
		{
			List<String> currentApplications = SPenService.getTopPackages(service.getApplicationContext());
			if(currentApplications.size() == 0)
			{
				currentApp = null;
				bl_possible = false;
			}
			else
			{
				currentApp = currentApplications.get(0);
			}
		}

		if(pref.getBoolean("soff_al_enable", false))
		{
			if(!SPenService.screen && SPenService.penOut)
			{
				str = "soff_al_app";
				SPenService.penOut = false;
			}
		}
		if(pref.getBoolean("hoff_al_enable", false))
		{
			if(!SPenService.screen && HeadsetService.headOut)
			{
				str = "hoff_al_app";
				HeadsetService.headOut = false;
			}
		}
		if(pref.getBoolean(lpreference, false))
		{
			if(bl_possible)
			{
				if(Blacklist.check_bl(currentApp, service, button_mode))
					return;
			}
			else
				Toast.makeText(service, "Please upgrade to android 22 or higher for this feature to work",Toast.LENGTH_LONG).show();

			LaunchableItem item = new LaunchableItem(str);
			if(item.load(service.getApplicationContext()))
			{
				Intent i = item.intent;
				if(i.hasExtra("listID"))
				{
					final String listID = i.getStringExtra("listID");
                    final int count = pref.getInt("multi" + listID + "count", 0);

					new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for(int iter = 0; iter < count; iter++) {
                                LaunchableItem temp = new LaunchableItem("multi" + listID + iter);
                                temp.load(service);
                                temp.intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                temp.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                try {
                                    service.startActivity(temp.intent);
                                    Thread.sleep(2000);
                                } catch (Exception e) {
                                    Log.d("Note Buddy", e.getMessage());
                                }
                            }
                        }
                    }).start();

				}
				else {
					if (!i.hasExtra("com.tushar.spen_helper.launchername")) {
						i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
						i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					}
					try {
						service.startActivity(i);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public static void LaunchComponent (String packageName, Context ctx){
		Intent i;
		try
		{
			PackageManager manager = ctx.getPackageManager();
			i = manager.getLaunchIntentForPackage(packageName);
			i.addCategory(Intent.CATEGORY_LAUNCHER);
			i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			ctx.startActivity(i);
		}
		catch(Exception e)
		{
			TelephonyManager tm = (TelephonyManager)ctx.getSystemService(Context.TELEPHONY_SERVICE);
			if((tm.getCallState() == TelephonyManager.CALL_STATE_OFFHOOK
					|| tm.getCallState() == TelephonyManager.CALL_STATE_RINGING))
			{
				Utilities.LaunchComponent("com.android.contacts", ctx.getApplicationContext());
			}
			else
			{
				i = new Intent(Intent.ACTION_MAIN);
				i.addCategory(Intent.CATEGORY_HOME);
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				ctx.startActivity(i);
			}
		}
	}

	public static void setQuickBarIcon(RemoteViews abcd, String str, Context ctx, int iconid)
	{
		LaunchableItem item = new LaunchableItem(str);
		if(item.load_for_QB(ctx))
		{
			if(item.application && PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean("icon_theme_en", false))
				abcd.setImageViewBitmap(iconid, ((BitmapDrawable) item.icon).getBitmap());
			else
				abcd.setImageViewBitmap(iconid, item.bitmap);
		}
		else
		{
			if(!item.rem)
			{
				abcd.setImageViewBitmap(iconid, BitmapFactory.decodeResource(ctx.getResources(), R.drawable.new_icon));
			}
			else
			{
				abcd.setImageViewBitmap(iconid, null);
			}
		}
	}

	public static void chooseSound(final Activity act,final SPenFragment frag,final int requestCode)
	{
		AlertDialog.Builder build = new AlertDialog.Builder(act);
		build.setTitle(R.string.menu_title);
		final myListItem items[] = {
				new myListItem(R.drawable.new_icon,act.getString(R.string.sel_sound)),
				new myListItem(R.drawable.new_icon,act.getString(R.string.sel_sound_ext)),
				new myListItem(R.drawable.ic_action_cancel,act.getString(R.string.rem_sound))
		};
		AppAdapter adapter = new AppAdapter(act,items,act);
		build.setAdapter(adapter, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int position) {
				if(items[position].name.equals(act.getString(R.string.sel_sound_ext)))
				{
					Intent i = new Intent(Intent.ACTION_GET_CONTENT);
					i.setType("audio/*");
					frag.startActivityForResult(i, requestCode);
				}
				else if(items[position].name.equals(act.getString(R.string.sel_sound)))
				{
					Intent i = new Intent(act,FileChooserActivity.class);
					frag.startActivityForResult(i, requestCode);
				}
				else if(items[position].name.equals(act.getString(R.string.rem_sound)))
				{
					SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(act);
					SharedPreferences.Editor editor = pref.edit();
					if(requestCode == SPenFragment.REQUEST_DETACH)
						editor.putString("det_s", "");
					else
						editor.putString("ins_s", "");
					editor.apply();
					frag.refresh();
					Toast.makeText(act, R.string.sound_removed, Toast.LENGTH_SHORT).show();
				}
			}
		});
		build.show();
	}
}

class FastBitmapDrawable extends Drawable {
	private Bitmap mBitmap;

	FastBitmapDrawable(Bitmap b) {
		mBitmap = b;
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawBitmap(mBitmap, 0.0f, 0.0f, null);
	}

	@Override
	public int getOpacity() {
		return PixelFormat.TRANSLUCENT;
	}

	@Override
	public void setAlpha(int alpha) {
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
	}

	@Override
	public int getIntrinsicWidth() {
		return mBitmap.getWidth();
	}

	@Override
	public int getIntrinsicHeight() {
		return mBitmap.getHeight();
	}

	@Override
	public int getMinimumWidth() {
		return mBitmap.getWidth();
	}

	@Override
	public int getMinimumHeight() {
		return mBitmap.getHeight();
	}
}

class AppAdapter extends ArrayAdapter<myListItem> {
	Context ctx;
	Activity myact;
	AppAdapter(Context ctx, myListItem[] items,Activity myact) {
		super(ctx, R.layout.row_app_selector, items);
		this.ctx = myact.getApplicationContext();
		this.myact = myact;
	}

	AppAdapter(Context ctx, ArrayList<myListItem> items, Activity myact) {
		super(ctx, R.layout.row_app_selector, items);
		this.ctx = myact.getApplicationContext();
		this.myact = myact;
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
		return(myact.getLayoutInflater().inflate(R.layout.row_app_selector, parent, false));
	}

	private void bindView(int position, View row) {
		TextView label=(TextView)row.findViewById(R.id.label);

		label.setText(getItem(position).name);

		ImageView icon=(ImageView)row.findViewById(R.id.icon);

		//noinspection deprecation
		icon.setImageDrawable(ctx.getResources().getDrawable(getItem(position).rid));
	}
}

class LaunchableItem
{
	Intent intent;
	String title;
	Drawable icon;
	String tag;
	Bitmap bitmap;
	public String pkg;
	boolean rem;
	boolean application;

	LaunchableItem(Intent i, String title, Bitmap bitmap, String tag)
	{
		intent = i;
		this.title = title;
		this.bitmap = bitmap;
		this.tag = tag;
		application = false;
	}

	LaunchableItem(Intent i, String title, Bitmap bitmap, String tag, boolean application)
	{
		intent = i;
		this.title = title;
		this.bitmap = bitmap;
		this.tag = tag;
		this.application = application;
	}

	LaunchableItem(String tag)
	{
		this.tag = tag;
	}

    void setTag(String newTag)
    {
        tag = newTag;
    }

	boolean save(Context ctx)
	{
		try
		{
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
			SharedPreferences.Editor editor = pref.edit();
			editor.putBoolean(tag + "rem", rem);
			editor.putString(tag + "title", title);
			if(intent != null) {
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				editor.putString(tag + "intent", intent.toUri(1));
			}
			editor.putString(tag + "pkg", pkg);
			editor.putBoolean(tag + "application", application);
			editor.apply();
			FileOutputStream out = ctx.openFileOutput(tag + "icon", 0);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
			out.flush();
			out.close();
			return true;

		} catch (Exception e) {
			return false;
		}
	}

	boolean load(Context ctx)
	{
		return load_working(ctx, false);
	}

	boolean load_for_QB(Context ctx)
	{
		return load_working(ctx, true);
	}

	private boolean load_working(Context ctx, boolean mode)
	{
		try
		{
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
			rem = pref.getBoolean(tag + "rem", false);
			application = pref.getBoolean(tag + "application", false);
			title = pref.getString(tag + "title", "");
			String intent_string = pref.getString(tag + "intent", "");
			if(!intent_string.equals(""))
				intent = Intent.parseUri(intent_string, 0);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			pkg = pref.getString(tag + "pkg", "");
			FileInputStream fis = ctx.openFileInput(tag + "icon");
			bitmap = BitmapFactory.decodeStream(fis);
			icon = new FastBitmapDrawable(Utilities.createBitmapThumbnail(bitmap, ctx));
			if(pref.getBoolean("icon_theme_en", false))
			{
				if(application)
					icon = IconTheme.getThemedIcon(pkg, ctx, mode);
			}
			fis.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	void delete(Context ctx)
	{
		try
		{
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
			SharedPreferences.Editor editor = pref.edit();
			editor.remove(tag + "title");
			editor.remove(tag + "intent");
			editor.remove(tag + "pkg");
			editor.remove(tag + "rem");
			editor.apply();
			ctx.deleteFile(tag + "icon");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	void setRemoved(Context ctx)
	{
		rem = true;
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
		SharedPreferences.Editor editor = pref.edit();
		editor.putBoolean(tag + "rem", rem);
		editor.apply();
	}
}

class LaunchableItemAdapter extends ArrayAdapter<LaunchableItem> {
	Context ctx;
	Activity myact;
	int layoutID;

	LaunchableItemAdapter(Context ctx, List<LaunchableItem> items, Activity myact, int layoutID) {
		super(ctx, layoutID, items);
		this.ctx = myact.getApplicationContext();
		this.myact = myact;
		this.layoutID = layoutID;
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
		return(myact.getLayoutInflater().inflate(layoutID, parent, false));
	}

	private void bindView(int position, View row) {
		TextView label=(TextView)row.findViewById(R.id.launcher_title);

		label.setText(getItem(position).title);

		ImageView icon=(ImageView)row.findViewById(R.id.launcher_icon);

		icon.setImageDrawable(getItem(position).icon);
	}
}

class myListItem
{
	int rid;
	String name;
	myListItem(int rid, String name)
	{
		this.rid = rid;
		this.name = name;
	}
}
