package com.tushar.spen_helper;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.TypedArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class EditActivity extends Activity {
	private ListView mList = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_edit);
		mList = ((ListView) findViewById(android.R.id.list));
        mList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice,
                android.R.id.text1,
                getResources().getStringArray(R.array.pen_states)));
	}

	public void finish()
	{
		if (AdapterView.INVALID_POSITION != mList.getCheckedItemPosition())
		{
			final int selectedResourceId =
					getResourceIdForPositionInArray(getApplicationContext(), R.array.pen_states,
							mList.getCheckedItemPosition());
			String xyz;

			final boolean isDisplayOn;
			if (R.string.list_on == selectedResourceId)
			{
				isDisplayOn = true;
				xyz="Pen Detached";
			}
			else if (R.string.list_off == selectedResourceId)
			{
				isDisplayOn = false;
				xyz="Pen Inserted";
			}
			else
			{
				throw new AssertionError();
			}

			final Bundle result = new Bundle();
			result.putBoolean("com.tushar.spen_helper.extra.BOOLEAN_STATE", isDisplayOn);

			final Intent resultIntent = new Intent();

            resultIntent.putExtra(com.tushar.spen_helper.Intent.EXTRA_BUNDLE, result);
			resultIntent.putExtra(com.tushar.spen_helper.Intent.EXTRA_STRING_BLURB,xyz);

			setResult(RESULT_OK, resultIntent);
		}


		super.finish();
	}

	static int getResourceIdForPositionInArray(final Context context, final int arrayId,
											   final int position)
	{

		TypedArray stateArray = null;
		try
		{
			stateArray = context.getResources().obtainTypedArray(arrayId);
			final int selectedResourceId = stateArray.getResourceId(position, 0);

			if (0 == selectedResourceId)
			{
				throw new IndexOutOfBoundsException();
			}

			return selectedResourceId;
		}
		finally
		{
			if (null != stateArray)
			{
				stateArray.recycle();
			}
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.twofortyfouram_locale_help_save_dontsave, menu);

		if(getActionBar() != null)
            getActionBar().setDisplayHomeAsUpEnabled(true);

		try {
			getActionBar().setIcon(getPackageManager().getApplicationIcon(getCallingPackage()));
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return true;
	}

	@Override
	public boolean onMenuItemSelected(final int featureId, final MenuItem item)
	{
		final int id = item.getItemId();

		if (android.R.id.home == id)
		{
			finish();
			return true;
		}
		else if (R.id.twofortyfouram_locale_menu_save == id)
		{
			finish();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

}
