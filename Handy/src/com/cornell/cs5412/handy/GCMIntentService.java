package com.cornell.cs5412.handy;

import static com.cornell.cs5412.handy.gcm.GCMUtilities.displayMessage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;

import com.cornell.cs5412.handy.R;
import com.cornell.cs5412.handy.gcm.GCMUtilities;
import com.cornell.cs5412.handy.serviceprovider.SPConfirmation;
import com.cornell.cs5412.handy.servicereceiver.SRHomeSearch;
import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;


public class GCMIntentService extends GCMBaseIntentService 
{
	private static final String TAG = "GCMIntentService";

	public GCMIntentService()
	{
		super(Globals.SENDER_ID);
	}

	@Override
	protected void onRegistered(final Context context, final String registrationId)
	{
		Log.i(TAG, "Device registered: regId = " + registrationId);
		if (Globals.sharedPrefs != null && Globals.sharedPrefs.contains("username") && Globals.sharedPrefs.getString("signedIn").equalsIgnoreCase("1"))
		{
			new Thread(new Runnable() {
				@Override
				public void run() 
				{
					try 
					{
						boolean state = false;
						state = GCMUtilities.registerWithEMP(context, Globals.ipAddress + "/registerAndroidDeviceForGCMPush", registrationId);
						if (state) 
							GCMRegistrar.setRegisteredOnServer(context, true);
					} 
					catch (Exception ex) 
					{
						ex.printStackTrace();
					}
				}
			}).start();
		}
	}

	@Override
	protected void onUnregistered(Context context, String registrationId)
	{
		Log.i(TAG, "Device unregistered");
		//displayMessage(context, getString(R.string.gcm_unregistered));
		if (GCMRegistrar.isRegisteredOnServer(context)) 
		{
			// ServerUtilities.unregister(context, registrationId);
		} 
		else 
		{
			// This callback results from the call to unregister made on
			// ServerUtilities when the registration to the server failed.
			Log.i(TAG, "Ignoring unregister callback");
		}
	}

	@Override
	protected void onMessage(Context context, Intent intent)
	{
		Log.i(TAG, "Received message");
		
		Bundle bundle = intent.getExtras();
		
		Set<String> keys = bundle.keySet();
		Iterator<String> i = keys.iterator();
		while (i.hasNext()) 
		{
			String key = (String) i.next();
			String value = bundle.getString(key);
			Log.d("Handy", "key: " + key + " value: " + value);
		}

		String messageTitle = intent.getExtras().getString("messageTitle");
		String message = intent.getExtras().getString("data");
		String address = intent.getExtras().getString("address");
		String requestorUsername = intent.getExtras().getString("requestorUsername");
		
		// display message
		displayMessage(context, message);
		
		// notifies user
		generateNotification(context, message, messageTitle, address, requestorUsername);
		
        Intent intent2 = new Intent();
        intent2.putExtra("messageID", "");
        intent2.setAction("com.cornell.cs5412.handy.GCMIntentService");
        sendBroadcast(intent2);
	}

	@Override
	protected void onDeletedMessages(Context context, int total)
	{
		Log.i(TAG, "Received deleted messages notification");
		//String message = getString(R.string.gcm_deleted, total);
		//displayMessage(context, message);
		// notifies user
		//generateNotification(context, message);
	}

	@Override
	public void onError(Context context, String errorId) 
	{
		Log.i(TAG, "Received error: " + errorId);
		//displayMessage(context, getString(R.string.gcm_error, errorId));
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) 
	{
		// log message
		Log.i(TAG, "Received recoverable error: " + errorId);
		//displayMessage(context, getString(R.string.gcm_recoverable_error, errorId));
		return super.onRecoverableError(context, errorId);
	}

	/**
	 * Issues a notification to inform the user that server has sent a message.
	 */
	private static void generateNotification(Context context, String message, String messageTitle, String address, String requestorUsername)
	{
		int icon = R.drawable.ic_launcher;
		long when = System.currentTimeMillis();
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(icon, message, when);
		String title = "Handy";
		
		Intent notificationIntent;
		if (messageTitle.equalsIgnoreCase("Service Requested"))
			notificationIntent= new Intent(context, SPConfirmation.class);
		else
			notificationIntent= new Intent(context, SRHomeSearch.class);
		
		notificationIntent.putExtra("requestorUsername", requestorUsername);
		notificationIntent.putExtra("address", address);		
		notificationIntent.setAction(Long.toString(System.currentTimeMillis()));
		
		// Get instance of Vibrator from current Context
		Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		
		// Vibrate for 300 milliseconds
		v.vibrate(300);

		// play ringer sound
	    try 
	    {
	        Uri notificationRinger = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
	        Ringtone r = RingtoneManager.getRingtone(context, notificationRinger);
	        r.play();
	    } 
	    catch (Exception e) 
	    {
	    	e.printStackTrace();
	    }
		
		// set intent so it does not start a new activity
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(context, title, message, intent);
		notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
		notificationManager.notify(new Random().nextInt(), notification);
	}

}
