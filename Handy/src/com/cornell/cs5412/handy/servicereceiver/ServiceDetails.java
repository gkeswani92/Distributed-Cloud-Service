package com.cornell.cs5412.handy.servicereceiver;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.cornell.cs5412.handy.DataTransfer;
import com.cornell.cs5412.handy.Globals;
import com.cornell.cs5412.handy.R;
import com.cornell.cs5412.handy.R.layout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ServiceDetails extends Activity 
{
	private TextView textViewServicetype;
	private TextView textViewCost;
	private TextView textViewDescription;
	private TextView textViewServiceName;
	private String serviceID;
	private Button btnRequest;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		serviceID = getIntent().getExtras().getString("serviceID");
	}
	
	public void setupScreen()
	{
		setContentView(R.layout.service_details);
		
		textViewServiceName = (TextView) findViewById(R.id.textViewServiceName);
		textViewServicetype = (TextView) findViewById(R.id.textViewServicetype);
		textViewCost = (TextView) findViewById(R.id.textViewCost);
		textViewDescription = (TextView) findViewById(R.id.textViewDescription);
		btnRequest = (Button) findViewById(R.id.btnRequest);
		
		// request status:
		//		0 = no request submitted
		//		1 = request submitted but waiting for SP to accept
		// 		2 = request accepted waiting for SR to mark as completed
		if (Globals.sharedPrefs.getInt("requestStatus" + serviceID) == 1)
		{
			btnRequest.setEnabled(false);
			btnRequest.setText("Waiting for the service provider to accept your request...");
		}
		else if (Globals.sharedPrefs.getInt("requestStatus" + serviceID) == 2)
		{
			btnRequest.setEnabled(true);
			btnRequest.setText("Mark Service Complete");
		}
		else
		{
			btnRequest.setEnabled(true);
			btnRequest.setText("Request Service");
		}
		
		//textViewServiceName.setText("Service Name: " + name);
		//textViewServicetype.setText("Type of Service: " + type);
		//textViewCost.setText("Hourly Cost: $" + cost);
		//textViewDescription.setText("Description: " + description);
		
		
		btnRequest.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) 
			{
				Button b = (Button)v;
				
				if (b.getText().toString().equalsIgnoreCase("Request Service"))
				{
					// forward the request to the server
					showAlert("Success", "Your request has been sent to the service provider!", ServiceDetails.this);
				}
				else if (b.getText().toString().equalsIgnoreCase("Mark Service Complete"))
				{
					Globals.sharedPrefs.Remove("requestStatus"+serviceID);
					showAlert("Success", "Thank you for using Handy!", ServiceDetails.this);
				}
			}
		});
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		setupScreen();
	}
	
	/*public void showAlertWithOptions(final String title, final String message, final Activity activity) 
	{
		try 
		{
			// prepare the alert box
			final AlertDialog.Builder alertbox = new AlertDialog.Builder(activity);

			// set the message to display
			alertbox.setMessage(message);
			alertbox.setTitle(title);

			// add a neutral button to the alert box and assign a click listener
			alertbox.setPositiveButton("No", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) 
				{
					dialog.dismiss();
					btnRequest.setText("Request Service");
					btnRequest.setEnabled(true);
				}
			});
			
			alertbox.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) 
				{
					dialog.dismiss();
					btnRequest.setText("Request Service");
					btnRequest.setEnabled(true);
					Intent intent = new Intent().setClass(ServiceDetails.this, ServiceReview.class);
					intent.putExtra("serviceID", serviceID);
					startActivity(intent);
				}
			});
			
			alertbox.show();
			return;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

	}*/
	
	public void showAlert(final String title, final String message, final Activity activity) 
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
					btnRequest.setText("Waiting for the service provider to accept your request...");
					btnRequest.setEnabled(false);
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
