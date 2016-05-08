package com.cornell.cs5412.handy;

import com.cornell.cs5412.handy.gcm.GCMUtilities;
import com.cornell.cs5412.handy.serviceprovider.SPProfile;
import com.cornell.cs5412.handy.servicereceiver.SRHomeSearch;
import com.cornell.cs5412.handy.servicereceiver.ServiceDetails;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;

public class LaunchActivity extends Activity {

	private Intent intent;

	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.launch);

		// setup preference storage
		loadPreferences(LaunchActivity.this);
		
		Log.e("Handy", "Launching Activity");
		
		//intent = new Intent().setClass(LaunchActivity.this, SRHomeSearch.class);
		//intent.putExtra("serviceID", "1");
		//Globals.sharedPrefs.saveInt("requestStatus1", 2);
		
		Thread t2 = new Thread(new Runnable() {

			@Override
			public void run() 
			{	
				GCMUtilities.registerWithGCM(getApplicationContext(), Globals.SENDER_ID);
			}
		});
		
		try
		{
			t2.start();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		if (Globals.sharedPrefs.getBoolean("loginSPComplete"))
		{
			intent = new Intent().setClass(LaunchActivity.this, SPProfile.class);
		}
		else if (Globals.sharedPrefs.getBoolean("loginSRComplete"))
		{
			intent = new Intent().setClass(LaunchActivity.this, SRHomeSearch.class);
		}
		else
		{
			intent = new Intent().setClass(LaunchActivity.this, LoginActivity.class);
		}
		
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
		Globals.appPref = PreferenceManager.getDefaultSharedPreferences(act.getApplicationContext());
		Globals.prefEditor = Globals.appPref.edit();
	}

	public void onRestart() {
		super.onRestart();
	}


}
