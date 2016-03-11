package com.cornell.cs5412.handy.servicereceiver;

import com.cornell.cs5412.handy.R;
import com.cornell.cs5412.handy.R.layout;

import android.app.Activity;
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
	private Button btnRequest;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.service_details);
		setupScreen();
	}
	
	public void setupScreen()
	{
		textViewServicetype = (TextView) findViewById(R.id.textViewServicetype);
		textViewCost = (TextView) findViewById(R.id.textViewCost);
		textViewrating = (TextView) findViewById(R.id.textViewrating);
		textViewDescription = (TextView) findViewById(R.id.textViewDescription);
		
		// TODO: connect to the server to populate the texeview with information
		
		btnRequest = (Button) findViewById(R.id.btnRequest);
		btnRequest.setOnClickListener(new OnClickListener() {
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
}
