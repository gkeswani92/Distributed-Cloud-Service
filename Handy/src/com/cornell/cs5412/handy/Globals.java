package com.cornell.cs5412.handy;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.LocationManager;

public class Globals 
{
	public static String ipAddress;
	public static SharedPreferences appPref;	
	public static SharedPreferences.Editor prefEditor;
	public static SharedPrefsHelper sharedPrefs;
	
	static 
	{ 
		sharedPrefs = new SharedPrefsHelper();
	}
	
	public static boolean isGPSEnabled(final Context ctx) 
	{
		final LocationManager manager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
		return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}
	
	public static void showProgress(final ProgressDialog progress, final String message) 
	{
		try 
		{
			if (!progress.isShowing()) 
			{
				progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progress.setMessage(message);
				progress.setIndeterminate(true);
				progress.setCancelable(false);
				progress.show();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void showAlert(final String title, final String message, final Activity activity) 
	{
		try 
		{
			// prepare the alert box
			final AlertDialog.Builder alertbox = new AlertDialog.Builder(activity);

			// set the message to display
			alertbox.setMessage(message);
			alertbox.setTitle(title);

			// add a neutral button to the alert box and assign a click listener
			alertbox.setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) 
				{
					dialog.dismiss();
				}
			});
			alertbox.show();
			return;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

	}
	
}
