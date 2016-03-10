package com.cornell.cs5412.handy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;

public class LaunchActivity extends Activity {

	private Intent intent;

	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.launch);

		// setup preference storage
		//loadPreferences(LaunchActivity.this);
		
		/*if(Globals.sharedPrefs != null && Globals.sharedPrefs.contains("signedIn"))
			intent = new Intent().setClass(Launch.this, HomeActivity.class);
		else
			intent = new Intent().setClass(Launch.this, LoginActivity.class);*/
		
		intent = new Intent().setClass(LaunchActivity.this, LoginActivity.class);

		int timer = 3000;
		new CountDownTimer(timer, 1000) {

			public void onFinish() 
			{
				if (intent != null) 
				{
					startActivity(intent);
					finish();
				}
			}

			@Override
			public void onTick(long arg0) {}

		}.start();
	}
	
	public static void loadPreferences(Activity act)
	{
		//Globals.appPref = PreferenceManager.getDefaultSharedPreferences(act.getApplicationContext());
		//Globals.prefEditor = Globals.appPref.edit();
	}

	public void onRestart() {
		super.onRestart();
	}


}
