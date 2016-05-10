package com.cornell.cs5412.handy.serviceprovider;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.cornell.cs5412.handy.DataTransfer;
import com.cornell.cs5412.handy.Globals;
import com.cornell.cs5412.handy.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

public class SPProfile extends Activity 
{
	private EditText edittextLocation;
	private EditText edittextCost;
	private EditText editTextDescription;
	private EditText edittextName;
	private Button btnSubmit;
	private Spinner spinnerServiceType;
	private Switch switchAvailability;
	private ProgressDialog progress;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
	}
	
	public void setupScreen()
	{
		setContentView(R.layout.profile);
		progress = new ProgressDialog(this);
		
		edittextName = (EditText) findViewById(R.id.edittextName);
		edittextLocation = (EditText) findViewById(R.id.edittextLocation);
		edittextCost = (EditText) findViewById(R.id.edittextCost);
		editTextDescription = (EditText) findViewById(R.id.editTextDescription);
		switchAvailability = (Switch) findViewById(R.id.switchAvailability);
		spinnerServiceType = (Spinner) findViewById(R.id.spinnerServiceType);
	      
		// Spinner Drop down elements
	    List<String> categories = new ArrayList<String>();
	    categories.add("Please Select a Type of Service");
	    categories.add("Gardening");
	    categories.add("Plumbing");
	    categories.add("Taxi");
	    categories.add("Baby Sitting");
	    //categories.add("Other");
	      
	    // Creating adapter for spinner
	    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_cell, categories);
	    spinnerServiceType.setAdapter(dataAdapter);
		
		btnSubmit = (Button) findViewById(R.id.btnSubmit);
		btnSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) 
			{
				final String name = edittextName.getText().toString();
				final String location = edittextLocation.getText().toString();
				final String cost = edittextCost.getText().toString();
				final String description = editTextDescription.getText().toString();
				final String type = spinnerServiceType.getSelectedItem().toString();
				int selectServiceType = spinnerServiceType.getSelectedItemPosition();
				if(name.equalsIgnoreCase("") || location.equalsIgnoreCase("") || cost.equalsIgnoreCase("") || selectServiceType == 0)
				{
					Globals.showAlert("Error", "Please enter all necessary information!", SPProfile.this);
				}
				else
				{
					if(Globals.sharedPrefs.getBoolean("servicePosted") && !Globals.sharedPrefs.getString("serviceID").equalsIgnoreCase("")) // update post API
					{
						if(!name.equals(Globals.sharedPrefs.getString("serviceName")) || !location.equals(Globals.sharedPrefs.getString("serviceLocation")) ||
								!cost.equals(Globals.sharedPrefs.getString("servicePrice")) || !description.equals(Globals.sharedPrefs.getString("serviceDescription")) ||
								!type.equals(Globals.sharedPrefs.getString("serviceType")))
						{
							updateService(name, type, location, cost, description);
						}
						
						if (Globals.sharedPrefs.getBoolean("serviceAvailability") != switchAvailability.isChecked())
						{
							changeServiceAvailability();
						}
					}
					else // new post API
					{
						postService(name, type, location, cost, description, switchAvailability.isChecked());
					}
				}
			}
		});
		
		// populate data if this is an edit
		if(Globals.sharedPrefs.getBoolean("servicePosted"))
		{
			edittextName.setText(Globals.sharedPrefs.getString("serviceName"));
			edittextLocation.setText(Globals.sharedPrefs.getString("serviceLocation"));
			edittextCost.setText(Globals.sharedPrefs.getString("servicePrice"));
			editTextDescription.setText(Globals.sharedPrefs.getString("serviceDescription"));
			ArrayAdapter adapter1 = (ArrayAdapter) spinnerServiceType.getAdapter();
			int selectServiceType = adapter1.getPosition(Globals.sharedPrefs.getString("serviceType"));
			spinnerServiceType.setSelection(selectServiceType);
			switchAvailability.setChecked(Globals.sharedPrefs.getBoolean("serviceAvailability"));
		}
	}
	
	public void changeServiceAvailability()
	{
		Globals.showProgress(progress, "Loading...");
		Thread t2 = new Thread(new Runnable() {
			public void run()
			{
				try
				{
					ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("serviceID", Globals.sharedPrefs.getString("serviceID")));
					
					final JSONObject json = DataTransfer.postJSONResult(Globals.ipAddress + "/changeServiceAvailability", params);
					Log.e("Handy", "Response: " + json.toString());
					runOnUiThread(new Runnable(){
						public void run()
						{
							if (progress.isShowing())
								progress.dismiss();
							
							if(json.optInt("status") == 0)
							{
								Globals.sharedPrefs.saveBoolean("serviceAvailability", !Globals.sharedPrefs.getBoolean("serviceAvailability"));
								Globals.showAlert("Success", "Your service availability has been changed!", SPProfile.this);
							}
							else
							{
								Globals.showAlert("Error", "An error has occurred. Please try again.", SPProfile.this);
							}
						}
					});
				}
				catch (Exception e)
				{
					runOnUiThread(new Runnable(){
						public void run()
						{
							if (progress.isShowing())
								progress.dismiss();
								
							Globals.showAlert("Error", "An error has occurred. Please try again.", SPProfile.this);
						}
					});
				}
			}	
		});
		t2.start();
	}
	
	public void updateService(final String name, final String type, final String location, final String cost, final String description)
	{
		Globals.showProgress(progress, "Loading...");
		Thread t2 = new Thread(new Runnable() {
			public void run()
			{
				try
				{
					ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("serviceID", Globals.sharedPrefs.getString("serviceID")));
					params.add(new BasicNameValuePair("name", name));
					params.add(new BasicNameValuePair("type", type));
					params.add(new BasicNameValuePair("location", location));
					params.add(new BasicNameValuePair("cost", cost));
					params.add(new BasicNameValuePair("description", description));
					
					final JSONObject json = DataTransfer.postJSONResult(Globals.ipAddress + "/updateService", params);

					runOnUiThread(new Runnable(){
						public void run()
						{
							if (progress.isShowing())
								progress.dismiss();
							
							if(json.optInt("status") == 0)
							{
								Globals.sharedPrefs.saveBoolean("servicePosted", true);
								Globals.sharedPrefs.saveString("serviceName", name);
								Globals.sharedPrefs.saveString("serviceType", type);
								Globals.sharedPrefs.saveString("serviceLocation", location);
								Globals.sharedPrefs.saveString("servicePrice", cost);
								Globals.sharedPrefs.saveString("serviceDescription", description);
								Globals.showAlert("Success", "Your service has been updated!", SPProfile.this);
							}
						}
					});
				}
				catch (Exception e)
				{
					runOnUiThread(new Runnable(){
						public void run()
						{
							if (progress.isShowing())
								progress.dismiss();
								
							Globals.showAlert("Error", "An error has occurred. Please try again.", SPProfile.this);
						}
					});
				}
			}	
		});
		t2.start();
	}
	
	public void postService(final String name, final String type, final String location, final String cost, final String description, final Boolean availability)
	{
		Globals.showProgress(progress, "Loading...");
		Thread t2 = new Thread(new Runnable() {
			public void run()
			{
				try
				{
					ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("name", name));
					params.add(new BasicNameValuePair("username", Globals.sharedPrefs.getString("username")));
					params.add(new BasicNameValuePair("type", type));
					params.add(new BasicNameValuePair("location", location));
					params.add(new BasicNameValuePair("cost", cost));
					params.add(new BasicNameValuePair("description", description));
					if(availability)
						params.add(new BasicNameValuePair("availability", "0"));
					else
						params.add(new BasicNameValuePair("availability", "1"));
					
					final JSONObject json = DataTransfer.postJSONResult(Globals.ipAddress + "/postService", params);

					runOnUiThread(new Runnable(){
						public void run()
						{
							if (progress.isShowing())
								progress.dismiss();
							
							if(json.optString("message").startsWith("Success"))
							{
								Globals.sharedPrefs.saveBoolean("servicePosted", true);
								Globals.sharedPrefs.saveString("serviceID", json.optString("serviceID"));
								Globals.sharedPrefs.saveString("serviceName", name);
								Globals.sharedPrefs.saveString("serviceType", spinnerServiceType.getSelectedItem().toString());
								Globals.sharedPrefs.saveString("serviceLocation", location);
								Globals.sharedPrefs.saveString("servicePrice", cost);
								Globals.sharedPrefs.saveString("serviceDescription", description);
								Globals.sharedPrefs.saveBoolean("serviceAvailability", availability);
								Globals.showAlert("Success", "Your service has been posted! We will notify you if someone requests for it.", SPProfile.this);
							}
						}
					});
				}
				catch (Exception e)
				{
					runOnUiThread(new Runnable(){
						public void run()
						{
							if (progress.isShowing())
								progress.dismiss();
								
							Globals.showAlert("Error", "An error has occurred. Please try again.", SPProfile.this);
						}
					});
				}
			}	
		});
		t2.start();
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		setupScreen();
	}
}
