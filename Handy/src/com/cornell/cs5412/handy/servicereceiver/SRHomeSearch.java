package com.cornell.cs5412.handy.servicereceiver;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.cornell.cs5412.handy.DataTransfer;
import com.cornell.cs5412.handy.Globals;
import com.cornell.cs5412.handy.R;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SRHomeSearch extends Activity 
{
	private TableLayout searchResult;
	private EditText editSearch;
	private Spinner spinnerServiceType;
	private Button btnSearch;
	private ProgressDialog progress;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
	}
	
	public void setupScreen()
	{
		setContentView(R.layout.home_sr);
		searchResult = (TableLayout) findViewById(R.id.messageTableLayout);
		progress = new ProgressDialog(this);
		
		editSearch = (EditText)findViewById(R.id.editSearch);
		spinnerServiceType = (Spinner)findViewById(R.id.spinnerServiceType);
		
	    List<String> categories = new ArrayList<String>();
	    categories.add("Please Select a Type of Service");
	    categories.add("Gardening");
	    categories.add("Plumbing");
	    categories.add("Taxi");
	    categories.add("Baby Sitting");
	    
	    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_cell, categories);
	    spinnerServiceType.setAdapter(dataAdapter);
	    
		btnSearch = (Button)findViewById(R.id.btnSearch);
		btnSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(editSearch.getWindowToken(), 0);
				
				//if (searchResult != null)
					//searchResult.cle
				
				if(editSearch.getText().toString().equalsIgnoreCase("") || spinnerServiceType.getSelectedItemPosition() == 0)
				{
					Globals.showAlert("Missing Information", "Please fill out all the search criteria", SRHomeSearch.this);
				}
				else
				{
					//editSearch.setText("");
					createServiceList(spinnerServiceType.getSelectedItem().toString(), editSearch.getText().toString());
				}
			}
		});
	}
	
	public void onResume()
	{
		super.onResume();
		setupScreen();
	}
	
	public void createServiceList(final String serviceType, final String serviceLocation)
	{
		Globals.showProgress(progress, "Loading...");
		Thread t2 = new Thread(new Runnable() {
			public void run()
			{
				try
				{
					ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
					
					Log.e("Handy", serviceLocation);
					
					params.add(new BasicNameValuePair("location", serviceLocation));
					params.add(new BasicNameValuePair("type", serviceType));
					
					final JSONObject json = DataTransfer.getJSONResult(Globals.ipAddress + "/getService", params);
					
					Log.e("Handy", "Response: " + json.toString());
					
					
					if (json.optInt("status") == 0)
					{
						final JSONArray jsonArray = new JSONArray(json.optJSONObject("data").optString("data"));
					
						runOnUiThread(new Runnable(){
							public void run()
							{
								if (progress.isShowing())
									progress.dismiss();
								
								int length = jsonArray.length();
								for (int i = 0; i < length; i++) 
								{
									try
									{
										JSONObject serviceObj = jsonArray.getJSONObject(i);
										String name = serviceObj.optString("name");
										String description = serviceObj.optString("description");
										String cost = serviceObj.optString("cost");
										String id = serviceObj.optString("id");
										//String availability = serviceObj.optString("availability");
										searchResult.addView(createDirectionStepLayout(id, name, description, cost));
									}
									catch (Exception e)
									{
										e.printStackTrace();
									}
								}
							}
						});
					}
					else
					{
						runOnUiThread(new Runnable(){
							public void run()
							{
								if (progress.isShowing())
									progress.dismiss();
									
								Globals.showAlert("Error", "No service found!", SRHomeSearch.this);
							}
						});
					}
				}
				catch (Exception e)
				{
					runOnUiThread(new Runnable(){
						public void run()
						{
							if (progress.isShowing())
								progress.dismiss();
								
							Globals.showAlert("Error", "An error has occurred. Please try again.", SRHomeSearch.this);
						}
					});
				}
			}	
		});
		t2.start();
	}
	
	public LinearLayout createDirectionStepLayout(final String id, final String name, final String description, final String cost)
	{
		final LayoutInflater inflater = this.getLayoutInflater();
		final LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.table_cell_search_result, null);
		
		linearLayout.setFocusable(true);
		
		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		int height= display.getHeight();
		
		final RelativeLayout relativeLayout = (RelativeLayout) linearLayout.findViewById(R.id.searchCell);
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) relativeLayout.getLayoutParams();
		params.width = (int) Math.round(width * 0.93);
		params.height = (int) Math.round(height * 0.18);
		relativeLayout.setLayoutParams(params);
		final TextView serviceName = (TextView) linearLayout.findViewById(R.id.serviceName);
		final TextView serviceDescription = (TextView) linearLayout.findViewById(R.id.serviceDescription);
		final TextView serviceCost = (TextView) linearLayout.findViewById(R.id.serviceCost);
		
		serviceName.setText("Name: " + name);
		serviceDescription.setText("Description: " + description);
		serviceCost.setText("Cost: $" + cost + "/hr");
		
		// setup the custom dialog box
		final Dialog dialog = new Dialog(SRHomeSearch.this);
		dialog.setContentView(R.layout.custom_dialog);
		dialog.setTitle("Request Service Confirmation");
		
		final EditText edittextAddress = (EditText) dialog.findViewById(R.id.edittextAddress);
		Button btnRequest = (Button) dialog.findViewById(R.id.btnRequest);
		btnRequest.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) 
			{
				final String address = edittextAddress.getText().toString();
				if(address.equalsIgnoreCase(""))
				{
					Toast.makeText(SRHomeSearch.this, "Please fill our your current location address", Toast.LENGTH_SHORT).show();
				}
				else
				{
					dialog.dismiss();
					Globals.showProgress(progress, "Loading...");
					Thread t2 = new Thread(new Runnable() {
						public void run()
						{
							try
							{
								ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
								
								params.add(new BasicNameValuePair("serviceID", id));
								params.add(new BasicNameValuePair("address", address));
								params.add(new BasicNameValuePair("username", Globals.sharedPrefs.getString("username")));
								
								final JSONObject json = DataTransfer.postJSONResult(Globals.ipAddress + "/requestService", params);
								
								Log.e("Handy", "Response: " + json.toString());
								
								if (json.optInt("status") == 0)
								{
									runOnUiThread(new Runnable(){
										public void run()
										{
											if (progress.isShowing())
												progress.dismiss();
											
											Globals.showAlert("Success", "Your request has been successfully submitted, we will notify you when the service provider accepts your request!", SRHomeSearch.this);
										}
									});
								}
								else
								{
									runOnUiThread(new Runnable(){
										public void run()
										{
											if (progress.isShowing())
												progress.dismiss();
												
											Globals.showAlert("Error", "An error has occurred. Please try again.", SRHomeSearch.this);
										}
									});
								}
							}
							catch (Exception e)
							{
								runOnUiThread(new Runnable(){
									public void run()
									{
										if (progress.isShowing())
											progress.dismiss();
											
										Globals.showAlert("Error", "An error has occurred. Please try again.", SRHomeSearch.this);
									}
								});
							}
						}	
					});
					t2.start();
				}
			}
		});
		
		Button btnDismiss = (Button) dialog.findViewById(R.id.btnDismiss);
		btnDismiss.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) 
			{
				dialog.dismiss();
			}
		});
		
		relativeLayout.setOnClickListener(new OnClickListener(){
		    public void onClick(View v)
		    {
				// shift the user to a new activity viewing the details of the message
				//Intent intent = new Intent().setClass(SRHomeSearch.this, ServiceDetails.class);
				//intent.putExtra("serviceID", id);
				//intent.putExtra("serviceID", id);
				//intent.putExtra("serviceID", id);
				//intent.putExtra("serviceID", id);
				//startActivity(intent);
		    	dialog.show();
		    }
		});
		
		return linearLayout;
	}
}
