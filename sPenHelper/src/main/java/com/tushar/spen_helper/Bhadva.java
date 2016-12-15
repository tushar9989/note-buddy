package com.tushar.spen_helper;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;

public class Bhadva extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle b = getIntent().getExtras();
		Intent i = new Intent(this,ExecuteAction.class);
		i.putExtras(b);
		startService(i);
		finish();
	}
}
