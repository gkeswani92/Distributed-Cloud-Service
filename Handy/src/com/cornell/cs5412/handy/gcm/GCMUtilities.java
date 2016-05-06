package com.cornell.cs5412.handy.gcm;

import java.util.ArrayList;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.cornell.cs5412.handy.DataTransfer;
import com.cornell.cs5412.handy.Globals;
import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public final class GCMUtilities
{
	public static void displayMessage(Context context, String message) 
	{
		Intent intent = new Intent(PushMessageReceiver.DISPLAY_MESSAGE_ACTION);
		intent.putExtra(PushMessageReceiver.EXTRA_MESSAGE, message);
		context.sendBroadcast(intent);
	}

	public static void registerWithGCM(final Context context, final String senderId) 
	{
		try
		{
			final Context ctx = context.getApplicationContext();
			GCMRegistrar.checkManifest(ctx);
			final String regId = GCMRegistrar.getRegistrationId(ctx);
				
			if (regId.equals("")) 
			{
				 GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
				 gcm.register(senderId);
				
				// fresh install case, no previous token record exists
				if (Globals.sharedPrefs.getString("newGCMToken").equalsIgnoreCase("") && Globals.sharedPrefs.getString("oldGCMToken").equalsIgnoreCase(""))
				{
					Globals.sharedPrefs.saveString("newGCMToken", GCMRegistrar.getRegistrationId(ctx));
					Globals.sharedPrefs.saveBoolean("firstTimeInstall", true);
				}
				else
				{
					Globals.sharedPrefs.saveString("oldGCMToken", Globals.sharedPrefs.getString("newGCMToken"));
					Globals.sharedPrefs.saveString("newGCMToken", GCMRegistrar.getRegistrationId(ctx));
					Globals.sharedPrefs.saveBoolean("tokenRefresh", true);
				}
			
				return;
			} 
			else 
			{
				if (Globals.sharedPrefs != null && Globals.sharedPrefs.contains("username") && Globals.sharedPrefs.getString("signedIn").equalsIgnoreCase("1"))
				{
					new Thread(new Runnable() {
						@Override
						public void run() 
						{
							try 
							{
								boolean state = false;
								state = registerWithEMP(ctx, Globals.ipAddress + "/registerAndroidDeviceForGCMPush", regId);
								if (state) 
									GCMRegistrar.setRegisteredOnServer(ctx, true);
							} 
							catch (Exception ex) 
							{
								ex.printStackTrace();
							}
						}
					}).start();
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return;
		}
	}
	
	public static void unregisterWithGCM(final Context context) 
	{
		try
		{
			final Context ctx = context.getApplicationContext();

			GCMRegistrar.checkManifest(ctx);

			final String regId = GCMRegistrar.getRegistrationId(ctx);
			if (!regId.equals(""))
			{
				GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
				gcm.unregister();
				GCMRegistrar.setRegisteredOnServer(ctx, false);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return;
		}
	}
	
	public static boolean registerWithEMP(Context ctx, String url, String token) 
	{
		try 
		{
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

			//params.add(new BasicNameValuePair("format", "json"));
			//params.add(new BasicNameValuePair("device_make", Build.MANUFACTURER));
			//params.add(new BasicNameValuePair("device_os", "Android"));
			//params.add(new BasicNameValuePair("osversion", Build.VERSION.RELEASE));
			//params.add(new BasicNameValuePair("device_model", Build.MODEL));
			params.add(new BasicNameValuePair("username", Globals.sharedPrefs.getString("username")));
			if(Globals.sharedPrefs.getBoolean("loginSPComplete"))
			{
				params.add(new BasicNameValuePair("userType", "SP"));
			}
			else if(Globals.sharedPrefs.getBoolean("loginSRComplete"))
			{
				params.add(new BasicNameValuePair("userType", "SR"));
			}
			
			
			if (Globals.sharedPrefs.getBoolean("firstTimeInstall"))
			{
				Globals.sharedPrefs.saveString("newGCMToken", token);
				params.add(new BasicNameValuePair("new_push_device_token", token));
				params.add(new BasicNameValuePair("old_push_device_token", ""));
				Globals.sharedPrefs.saveBoolean("firstTimeInstall", false);
			}
			else if (Globals.sharedPrefs.getBoolean("tokenRefresh"))
			{
				params.add(new BasicNameValuePair("new_push_device_token", token));
				params.add(new BasicNameValuePair("old_push_device_token", Globals.sharedPrefs.getString("oldGCMToken")));
				Globals.sharedPrefs.saveBoolean("tokenRefresh", false);
			}
			else
			{
				params.add(new BasicNameValuePair("new_push_device_token", token));
				params.add(new BasicNameValuePair("old_push_device_token", token));
			}
			
			final JSONObject json = DataTransfer.postJSONResult(url, params);

			Log.d("", "[JSON RESPONSE]: " + json.toString());
				if (json != null && (json.optInt("status") == 0)) 
				{
					//String trackingId = json.optString(Consts.EMP_DEVICE_PUSH_ID);
					//Log.d("", "EMP PUSH Tracking Id: " + trackingId);
					//if (trackingId != null) 
					//{
						//Globals.sharedPrefs.saveBoolean(Consts.PUSH_NOTIFICATION, true);
						//Globals.trackingIdGCM = trackingId;
						//Globals.sharedPrefs.saveString(Consts.EMP_DEVICE_PUSH_ID, trackingId);
						return true;
				//	}
				}
			return false;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return false;
		}
	}
	
}
