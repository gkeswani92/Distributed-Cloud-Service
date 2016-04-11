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
	private TextView textViewrating;
	private TextView textViewDescription;
	private TextView textViewServiceName;
	private String serviceID;
	private Button btnRequest;
	private Button btnSubmitReview;
	private ProgressDialog progress;
	
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
		textViewrating = (TextView) findViewById(R.id.textViewrating);
		textViewDescription = (TextView) findViewById(R.id.textViewDescription);
		btnRequest = (Button) findViewById(R.id.btnRequest);
		btnSubmitReview = (Button) findViewById(R.id.btnSubmitReview);
		
		progress = new ProgressDialog(this);
		
		Globals.showProgress(progress, "Loading...");
		Thread t2 = new Thread(new Runnable() {
			public void run()
			{
				try
				{
					ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("serviceID", serviceID));
					
					final JSONObject json = DataTransfer.getJSONResult(Globals.ipAddress + "/getServiceDetails", params);
					final JSONObject jsonData = json.optJSONObject("data");

					runOnUiThread(new Runnable(){
						public void run()
						{
							if (progress.isShowing())
								progress.dismiss();
								
							String name = jsonData.optString("name");
							String type = jsonData.optString("type");
							String cost = jsonData.optString("cost");
							String description = jsonData.optString("description");
							String rating = jsonData.optString("rating");
							
							textViewServiceName.setText("Service Name: " + name);
							textViewServicetype.setText("Type of Service: " + type);
							textViewCost.setText("Hourly Cost: $" + cost);
							if(rating.equalsIgnoreCase("-1"))
								textViewrating.setText("Rating: unrated");
							else
								textViewrating.setText("Rating: " + rating + "/5");
							textViewDescription.setText("Description: " + description);
							
							btnRequest.setVisibility(View.VISIBLE);
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
							
							textViewServiceName.setText("");
							textViewServicetype.setText("");
							textViewCost.setText("");
							textViewrating.setText("");
							textViewDescription.setText("");
							
							Globals.showAlert("Error", "An error has occurred. Please try again.", ServiceDetails.this);
						}
					});
				}
			}	
		});
		t2.start();
		
		btnRequest.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) 
			{
				// forward the request to the server
				
				
				showAlert("Success", "Your request has been sent to the service provider!", ServiceDetails.this);
			}
		});
		
		btnSubmitReview.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) 
			{
				// forward the request to the server
			}
		});
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		setupScreen();
	}
	
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
