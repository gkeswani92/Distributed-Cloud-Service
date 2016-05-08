package com.cornell.cs5412.handy.serviceprovider;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.cornell.cs5412.handy.DataTransfer;
import com.cornell.cs5412.handy.Globals;
import com.cornell.cs5412.handy.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SPConfirmation extends Activity
{
	public TextView textViewRequestDetails;
	public EditText edittextEta;
	public Button btnAccept;
	public Button btnDecline;
	private ProgressDialog progress;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.confirmation_sp);
	}
	
	public void setupScreen()
	{
		String address = getIntent().getStringExtra("address");
		final String requestorUsername = getIntent().getStringExtra("requestorUsername");
		
		progress = new ProgressDialog(SPConfirmation.this);
		
		textViewRequestDetails = (TextView) findViewById(R.id.textViewRequestDetails);
		textViewRequestDetails.setText("You have received a request from the following location:\n\n" + address + "\n\nPlease promptly fill in your estimated time of arrival below or decline the request.");
		
		edittextEta = (EditText) findViewById(R.id.edittextEta);
		
		btnAccept = (Button) findViewById(R.id.btnAccept);
		btnAccept.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				final String eta = edittextEta.getText().toString();
				if (eta.equalsIgnoreCase(""))
				{
					Globals.showAlert("Missing Information", "Please fill out your esimtated time of arrival before accepting this request.", SPConfirmation.this);
					return;
				}
				
				Globals.showProgress(progress, "Loading...");
				new Thread(new Runnable() {
					@Override
					public void run() 
					{
						try 
						{
				           	ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
				           	params.add(new BasicNameValuePair("requestorUsername", requestorUsername));
				           	params.add(new BasicNameValuePair("decision", "yes"));
				           	params.add(new BasicNameValuePair("eta", eta));
							        
							final JSONObject json = DataTransfer.postJSONResult(Globals.ipAddress + "/replyRequest", params);
									
							Log.e("Handy", "Response: " + json.toString());
							
							if (json.optInt("status") == 0)
							{
								runOnUiThread(new Runnable(){
									public void run()
									{
										if (progress.isShowing())
											progress.dismiss();
										
										showAlert("Success", "Your response has been successfully submitted to the service requestor.", SPConfirmation.this);
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
											
										Globals.showAlert("Error", "An error has occurred. Please try again.", SPConfirmation.this);
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
										
									Globals.showAlert("Error", "An error has occurred. Please try again.", SPConfirmation.this);
								}
							});
						}
					}	
				}).start();;
			}
		});
		
		btnDecline = (Button) findViewById(R.id.btnDecline);
		btnDecline.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				Globals.showProgress(progress, "Loading...");
				new Thread(new Runnable() {
					@Override
					public void run() 
					{
						try 
						{
				           	ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
				           	params.add(new BasicNameValuePair("requestorUsername", requestorUsername));
				           	params.add(new BasicNameValuePair("decision", "no"));
							        
							final JSONObject json = DataTransfer.postJSONResult(Globals.ipAddress + "/replyRequest", params);
									
							Log.e("Handy", "Response: " + json.toString());
							
							if (json.optInt("status") == 0)
							{
								runOnUiThread(new Runnable(){
									public void run()
									{
										if (progress.isShowing())
											progress.dismiss();
										
										showAlert("Success", "Your response has been successfully submitted to the service requestor.", SPConfirmation.this);
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
											
										Globals.showAlert("Error", "An error has occurred. Please try again.", SPConfirmation.this);
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
										
									Globals.showAlert("Error", "An error has occurred. Please try again.", SPConfirmation.this);
								}
							});
						}
					}	
				}).start();;
			}
		});
	}
	
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
					Intent intent;
					intent = new Intent().setClass(SPConfirmation.this, SPProfile.class);
					startActivity(intent);
					finish();
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
