/* 
 * Copyright (C) 2012 Paul Burke
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */ 

package com.ipaulpro.afilechooser;

import java.io.File;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.BackStackEntry;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Main Activity that handles the FileListFragments 
 * 
 * @version 2012-10-28
 * 
 * @author paulburke (ipaulpro)
 * 
 */
public class FileChooserActivity extends FragmentActivity implements
		OnBackStackChangedListener {

	public static final String PATH = "path";
	static final int permissionRequestCode = 121;
	public static String EXTERNAL_BASE_PATH = /*Environment.getExternalStorageDirectory().getAbsolutePath()*/"/mnt";
	private FragmentManager mFragmentManager;
	private static boolean isValid = false;
	private static File selectedFile;
	MediaPlayer mp = new MediaPlayer();
	private BroadcastReceiver mStorageListener = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Toast.makeText(context, R.string.storage_removed, Toast.LENGTH_LONG).show();
			finishWithResult(null);
		}
	};

	private String mPath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.chooser);

		if (ContextCompat.checkSelfPermission(this,
				Manifest.permission.READ_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED) {

			ActivityCompat.requestPermissions(this,
					new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
					permissionRequestCode);
		}

		mFragmentManager = getSupportFragmentManager();
		mFragmentManager.addOnBackStackChangedListener(this);
		EXTERNAL_BASE_PATH = PreferenceManager.getDefaultSharedPreferences(this).getString("path", /*Environment.getExternalStorageDirectory().getAbsolutePath()*/"/mnt");
		//if (savedInstanceState == null) {
			mPath = EXTERNAL_BASE_PATH;
			addFragment(mPath);
		//} else {
			//mPath = savedInstanceState.getString(PATH);
		//}

		setTitle(mPath);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
										   String permissions[], int[] grantResults) {
		switch (requestCode) {
			case permissionRequestCode: {
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {

					replaceFragment(mPath);
				}
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterStorageListener();
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerStorageListener();
	}

	/*@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putString(PATH, mPath);
	}*/

	@Override
	public void onBackStackChanged() {
		mPath = EXTERNAL_BASE_PATH;
		mp.reset();
		int count = mFragmentManager.getBackStackEntryCount();
		if (count > 0) {
			BackStackEntry fragment = mFragmentManager
					.getBackStackEntryAt(count - 1);
			mPath = fragment.getName();
		}

		setTitle(mPath);
	}

	/**
	 * Add the initial Fragment with given path.
	 *
	 * @param path The absolute path of the file (directory) to display.
	 */
	private void addFragment(String path) {
		FileListFragment explorerFragment = FileListFragment.newInstance(mPath);
		mFragmentManager.beginTransaction()
				.add(R.id.explorer_fragment, explorerFragment).commit();
	}

	/**
	 * "Replace" the existing Fragment with a new one using given path.
	 * We're really adding a Fragment to the back stack.
	 *
	 * @param path The absolute path of the file (directory) to display.
	 */
	private void replaceFragment(String path) {
		FileListFragment explorerFragment = FileListFragment.newInstance(path);
		mFragmentManager.beginTransaction()
				.replace(R.id.explorer_fragment, explorerFragment)
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
				.addToBackStack(path).commit();
	}

	/**
	 * Finish this Activity with a result code and URI of the selected file.
	 *
	 * @param file The file selected.
	 */
	private void finishWithResult(File file) {
		if (file != null) {
			Uri uri = Uri.fromFile(file);
			setResult(RESULT_OK, new Intent().setData(uri));
			mp.reset();
			finish();
		} else {
			setResult(RESULT_CANCELED);
			mp.reset();
			finish();
		}
	}

	/**
	 * Called when the user selects a File
	 *
	 * @param file The file that was selected
	 */
	protected void onFileSelected(File file) {
		if (file != null) {
			mPath = file.getAbsolutePath();
			isValid = false;

			if (file.isDirectory()) {
				replaceFragment(mPath);
			} else {
				//finishWithResult(file);
				try
				{
					if(mp.isPlaying())
					{
						mp.stop();
					}
					mp.reset();
					mp.setDataSource(mPath);
					mp.prepare();
					if(mp.getDuration() <= 10000)
					{
						mp.start();
						isValid = true;
						selectedFile = file;
					}
					else
						Toast.makeText(FileChooserActivity.this, "Selected Sound file is too long", Toast.LENGTH_SHORT).show();
				}
				catch(Exception e)
				{

				}
			}
		} else {
			Toast.makeText(FileChooserActivity.this, R.string.error_selecting_file, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Register the external storage BroadcastReceiver.
	 */
	private void registerStorageListener() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_MEDIA_REMOVED);
		registerReceiver(mStorageListener, filter);
	}

	/**
	 * Unregister the external storage BroadcastReceiver.
	 */
	private void unregisterStorageListener() {
		unregisterReceiver(mStorageListener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    int id = item.getItemId();
	    if(id == R.id.action_select)
	    {
	    	if(isValid)
	    	{
	    		finishWithResult(selectedFile);
	    		Toast.makeText(FileChooserActivity.this, "Selected File: " + mPath, Toast.LENGTH_LONG).show();
	    	}
	    	else
	    		Toast.makeText(FileChooserActivity.this, "Invalid Selection", Toast.LENGTH_SHORT).show();
	    	return true;
	    }
	    if(id == R.id.set_default)
	    {
	    	SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(FileChooserActivity.this).edit();
	    	edit.putString("path", mPath);
	    	edit.commit();
	    	Toast.makeText(FileChooserActivity.this, "New Default Path is: " + mPath, Toast.LENGTH_LONG).show();
	    	return true;
	    }
	    if(id == R.id.reset_default)
	    {
	    	SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(FileChooserActivity.this).edit();
	    	edit.putString("path", /*Environment.getExternalStorageDirectory().getAbsolutePath()*/ "/mnt");
			replaceFragment("/mnt");
	    	edit.commit();
	    	Toast.makeText(FileChooserActivity.this, "Default Path has been Reset", Toast.LENGTH_LONG).show();
	    	return true;
	    }
		if(id == R.id.load_path)
		{
			final String[] inputString = {""};
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Enter Directory Path");

			final EditText input = new EditText(this);
			input.setInputType(InputType.TYPE_CLASS_TEXT);
			builder.setView(input);

			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					inputString[0] = input.getText().toString();
					File file = new File(inputString[0]);
					if(file.exists())
					{
						replaceFragment(inputString[0]);
					}
					else
					{
						Toast.makeText(FileChooserActivity.this, "Invalid Path", Toast.LENGTH_LONG).show();
					}
				}
			});
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});

			builder.show();
			return true;
		}
	    return super.onOptionsItemSelected(item);
	}
	
	@Override
	public Resources getResources() {
	    ResourcesImpl mResourcesImpl = null;
	    if (mResourcesImpl == null) {
	        mResourcesImpl = new ResourcesImpl(super.getResources());
	    }
	    return mResourcesImpl;
	}

	class ResourcesImpl extends Resources {
	    int targetId = 0;
	    ResourcesImpl(Resources resources) {
	        super(resources.getAssets(), resources.getDisplayMetrics(), resources.getConfiguration());
	        targetId = Resources.getSystem().getIdentifier("split_action_bar_is_narrow","bool", "android");
	    }
	    @Override
	    public boolean getBoolean(int id) throws NotFoundException {
	        if(targetId == id){
	            return true;
	        }
	        return super.getBoolean(id);
	    }
	}
}
