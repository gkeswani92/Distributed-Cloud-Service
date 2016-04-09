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
	private EditText edittextRadius;
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
		edittextRadius = (EditText) findViewById(R.id.edittextRadius);
		edittextCost = (EditText) findViewById(R.id.edittextCost);
		editTextDescription = (EditText) findViewById(R.id.editTextDescription);
		switchAvailability = (Switch) findViewById(R.id.switchAvailability);
		spinnerServiceType = (Spinner) findViewById(R.id.spinnerServiceType);
	      
		// Spinner Drop down elements
	    List<String> categories = new ArrayList<String>();
	    categories.add("Please Select a Type of Service");
	    categories.add("Gardening");
	    categories.add("Plubming");
	    categories.add("Automobiles");
	    categories.add("Housing");
	    categories.add("Other");
	      
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
				final String radius = edittextRadius.getText().toString();
				final String cost = edittextCost.getText().toString();
				final String description = editTextDescription.getText().toString();
				final String type = spinnerServiceType.getSelectedItem().toString();
				int selectServiceType = spinnerServiceType.getSelectedItemPosition();
				if(name.equalsIgnoreCase("") || location.equalsIgnoreCase("") || radius.equalsIgnoreCase("") || cost.equalsIgnoreCase("") || selectServiceType == 0)
				{
					Globals.showAlert("Error", "Please enter all necessary information!", SPProfile.this);
				}
				else
				{
					if (switchAvailability.isChecked() && !Globals.sharedPrefs.getBoolean("servicePosted")) // upload to server
					{
						Globals.showProgress(progress, "Loading...");
						Thread t2 = new Thread(new Runnable() {
							public void run()
							{
								try
								{
									ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
									params.add(new BasicNameValuePair("name", name));
									params.add(new BasicNameValuePair("type", type));
									params.add(new BasicNameValuePair("location", location));
									params.add(new BasicNameValuePair("radius", radius));
									params.add(new BasicNameValuePair("cost", cost));
									params.add(new BasicNameValuePair("description", description));
									
									final JSONObject json = DataTransfer.postJSONResult(Globals.ipAddress + "/postService", params);

									runOnUiThread(new Runnable(){
										public void run()
										{
											if (progress.isShowing())
												progress.dismiss();
											
											if(json.optString("message").equalsIgnoreCase("success"))
											{
												Globals.sharedPrefs.saveBoolean("servicePosted", true);
												Globals.sharedPrefs.saveString("serviceID", json.optString("serviceID"));
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
					else if (!switchAvailability.isChecked() && Globals.sharedPrefs.getBoolean("servicePosted")) // send remove request to server
					{
						Globals.showProgress(progress, "Loading...");
						Thread t2 = new Thread(new Runnable() {
							public void run()
							{
								try
								{
									ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
									params.add(new BasicNameValuePair("serviceID", Globals.sharedPrefs.getString("serviceID")));
									
									final JSONObject json = DataTransfer.postJSONResult(Globals.ipAddress + "/deleteService", params);

									runOnUiThread(new Runnable(){
										public void run()
										{
											if (progress.isShowing())
												progress.dismiss();
											
											if(json.optString("message").equalsIgnoreCase("success"))
											{
												Globals.sharedPrefs.saveBoolean("servicePosted", false);
												Globals.showAlert("Success", "Your service has been removed!", SPProfile.this);
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
					else // save info locally
					{
						Globals.sharedPrefs.saveBoolean("serviceProfileComplete", true);
						Globals.sharedPrefs.saveString("serviceName", name);
						Globals.sharedPrefs.saveString("serviceType", spinnerServiceType.getSelectedItem().toString());
						Globals.sharedPrefs.saveString("serviceLocation", location);
						Globals.sharedPrefs.saveString("serviceRadius", radius);
						Globals.sharedPrefs.saveString("servicePrice", cost);
						Globals.sharedPrefs.saveString("serviceDescription", description);
						Toast.makeText(SPProfile.this, "Your information has been saved!!", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
		
	    switchAvailability.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
			{
				if(isChecked)
					btnSubmit.setText("Submit");
				else
					btnSubmit.setText("Save");
			}
		});
	    
		// populate data if this is an edit
		if(Globals.sharedPrefs.getBoolean("serviceProfileComplete"))
		{
			edittextName.setText(Globals.sharedPrefs.getString("serviceName"));
			edittextLocation.setText(Globals.sharedPrefs.getString("serviceLocation"));
			edittextRadius.setText(Globals.sharedPrefs.getString("serviceRadius"));
			edittextCost.setText(Globals.sharedPrefs.getString("servicePrice"));
			editTextDescription.setText(Globals.sharedPrefs.getString("serviceDescription"));
			ArrayAdapter adapter1 = (ArrayAdapter) spinnerServiceType.getAdapter();
			int selectServiceType = adapter1.getPosition(Globals.sharedPrefs.getString("serviceType"));
			spinnerServiceType.setSelection(selectServiceType);
		}
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		setupScreen();
	}
}
