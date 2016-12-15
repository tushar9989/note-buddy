package com.tushar.spen_helper;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Window;

public class Shortcuts extends ListActivity {
  static int RESULT_CODE=1337;

  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    Intent shortcut = new Intent(Intent.ACTION_CREATE_SHORTCUT);
	startActivityForResult(Intent.createChooser(shortcut, getString(R.string.shortcut_menu_title)),RESULT_CODE);
  }
  
  @Override
  public void onActivityResult(int requestCode, int resultCode,Intent data)
  {
	  if (requestCode == RESULT_CODE) {
          if (resultCode == RESULT_OK) {
        	  Intent intent = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_INTENT);
        	  String title = data.getStringExtra(Intent.EXTRA_SHORTCUT_NAME);
        	  Bitmap bitmap = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON);
        	  String pkg = null;
        	  Drawable icon;
        	  ShortcutIconResource iconResource;
        	  int id;
        	  if (bitmap != null) {
        		  //icon = new FastBitmapDrawable(Utilities.createBitmapThumbnail(bitmap, Shortcuts.this));
        		  try
        		  {
        			  Parcelable extra = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE);
					  if (extra != null && extra instanceof ShortcutIconResource) {

                          iconResource = (ShortcutIconResource) extra;
                          pkg = iconResource.packageName;

                      }
        		  }
        		  catch(Exception e)
        		  {
        			  e.printStackTrace();
        		  }
        	  } else {
        		  Parcelable extra = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE);
        		  if (extra != null && extra instanceof ShortcutIconResource) {
        			  try {
        				  iconResource = (ShortcutIconResource) extra;
                          pkg = iconResource.packageName;
                          icon = getPackageManager().getApplicationIcon(pkg);
        				  /*final PackageManager packageManager = Shortcuts.this.getPackageManager();
        				  Resources resources = packageManager.getResourcesForApplication(
        						  iconResource.packageName);
        				  id = resources.getIdentifier(iconResource.resourceName, null, null);
						  if(Build.VERSION.SDK_INT >= 21)
							  icon = resources.getDrawable(id, null);
						  else
							  //noinspection deprecation
							  icon = resources.getDrawable(id);*/
						  if(icon != null)
        				  	bitmap = ((BitmapDrawable)icon).getBitmap();

        			  } catch (Exception e) {
        				  e.printStackTrace();
        			  }
        		  }
        	  }
        	  LaunchableItem item = new LaunchableItem(intent, title, bitmap, getIntent().getStringExtra("shact"));
        	  item.pkg = pkg;
        	  Utilities.item = item;
        	  Intent resultIntent = new Intent();
        	  setResult(Activity.RESULT_OK,resultIntent);
        	  finish();
          }
          else
          {
        	  onBackPressed();
          }
	  }
  }
}
