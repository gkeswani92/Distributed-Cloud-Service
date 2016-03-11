package com.cornell.cs5412.handy.servicereceiver;

import com.cornell.cs5412.handy.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

public class SRHomeSearch extends Activity 
{
	private TableLayout searchResult;
	

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setupScreen();
	}
	
	public void setupScreen()
	{
		setContentView(R.layout.home_sr);
		
		// TODO: make a connection to server. Populate test data for now
		searchResult = (TableLayout) findViewById(R.id.messageTableLayout);
		searchResult.addView(createDirectionStepLayout("Name: Bob the Gardner", "Service Type: Gardening", "Cost: $60/hr", "Service Availability: Yes"));
		searchResult.addView(createDirectionStepLayout("Name: Bob the Plumber", "Service Type: Plumbing", "Cost: $65/hr", "Service Availability: Yes"));
	}
	
	public void onResume()
	{
		super.onResume();
		setupScreen();
	}
	
	public LinearLayout createDirectionStepLayout(final String name, final String type, final String cost, final String availability)
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
		params.height = (int) Math.round(height * 0.16);
		relativeLayout.setLayoutParams(params);
		final TextView serviceName = (TextView) linearLayout.findViewById(R.id.serviceName);
		final TextView serviceType = (TextView) linearLayout.findViewById(R.id.serviceType);
		final TextView serviceCost = (TextView) linearLayout.findViewById(R.id.serviceCost);
		final TextView serviceAvailability = (TextView) linearLayout.findViewById(R.id.serviceAvailability);
		
		serviceName.setText(name);
		serviceType.setText(type);
		serviceCost.setText(cost);
		serviceAvailability.setText(availability);
		
		relativeLayout.setOnClickListener(new OnClickListener(){
		    public void onClick(View v)
		    {
				// shift the user to a new activity viewing the details of the message
				Intent intent = new Intent().setClass(SRHomeSearch.this, ServiceDetails.class);
				startActivity(intent);
				finish();
		    }
		});
		
		return linearLayout;
	}
}
