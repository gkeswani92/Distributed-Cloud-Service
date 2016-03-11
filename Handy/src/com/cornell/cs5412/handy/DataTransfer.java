package com.cornell.cs5412.handy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Base64;
import android.util.Log;

public class DataTransfer 
{
	private final static int timeoutConnection = 65000;
	private final static int timeoutSocket = 65000;

	public static boolean getConnectivityStatus(final Context context) 
	{
		final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (cm == null) 
			return false;

		if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected())
			return true;
		else 
			return false;
	}
	
	public static JSONObject getJSONResultOAuth(final String url, final ArrayList<NameValuePair> params, final Context ctx) throws Exception 
	{
		final HttpClient client = new DefaultHttpClient();
		final HttpPost httpPost = new HttpPost(url);

		final HttpResponse responsePost;
		final HttpEntity resEntity;
		try
		{
			Log.e("CS5999", "REQUEST URL in connection " + params);
			boolean DEBUG = true;
			if (DEBUG) 
			{
				final StringBuffer buffer = new StringBuffer();
				buffer.append(url + "?");

				for (int i = 0; i < params.size(); i++) 
				{
					if (i != 0) 
					{
						buffer.append("&");
					}
					buffer.append(params.get(i).getName() + "="	+ params.get(i).getValue());
				}
				//Log.e("CS5999", "url:  " + buffer.toString());
			}

			HttpConnectionParams.setConnectionTimeout(httpPost.getParams(), timeoutConnection);
			// Set the default socket timeout (SO_TIMEOUT)
			// in milliseconds which is the timeout for waiting for data.

			HttpConnectionParams.setSoTimeout(httpPost.getParams(), timeoutSocket);
			final UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
			httpPost.setEntity(ent);
			
			//DataTransfer.setHTTPHeader(httpPost);
			httpPost.setHeader("Authorization", "Basic " + Base64.encodeToString(("test-client" + ":" + "test-client-secret").getBytes(), Base64.NO_WRAP));
			
			responsePost = client.execute(httpPost);
			resEntity = responsePost.getEntity();

			if (resEntity != null) 
			{
				InputStream instream = resEntity.getContent();
				String result = convertStreamToString(instream);
				instream.close();
				JSONObject jsonObject = new JSONObject(result);
				return jsonObject;
			}
			return null;
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
	
	public static JSONArray getJSONResult(final String url, final ArrayList<NameValuePair> params) throws Exception 
	{
		final HttpClient client = new DefaultHttpClient();
		final HttpGet httpGet = new HttpGet();

		final HttpResponse response;
		final HttpEntity resEntity;
		try
		{
			final StringBuffer buffer = new StringBuffer();
			buffer.append(url + "?");
			for (int i = 0; i < params.size(); i++) 
			{
				if (i != 0) 
					buffer.append("&");
				
				buffer.append(params.get(i).getName() + "="	+ params.get(i).getValue());
			}
			
			Log.e("CS5999", "REQUEST URL in connection " + buffer.toString());
			
			HttpConnectionParams.setConnectionTimeout(httpGet.getParams(), timeoutConnection);
			HttpConnectionParams.setSoTimeout(httpGet.getParams(), timeoutSocket);
			
			httpGet.setHeader("Authorization", "Bearer " + Globals.sharedPrefs.getString("access_token"));
			httpGet.setURI(new URI(buffer.toString()));
			response = client.execute(httpGet);
			resEntity = response.getEntity();

			if (resEntity != null) 
			{
				InputStream instream = resEntity.getContent();
				String result = convertStreamToString(instream);
				instream.close();
				JSONArray jsonArray = new JSONArray(result);
				return jsonArray;
			}
			return null;
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
	
	public static String convertStreamToString(InputStream is) 
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try 
		{
			while ((line = reader.readLine()) != null)
				sb.append(line + "\n");
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		finally 
		{
			try 
			{
				is.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
}
