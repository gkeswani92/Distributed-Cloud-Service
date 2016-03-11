package com.cornell.cs5412.handy.serviceprovider;

import java.util.ArrayList;
import java.util.List;

import com.cornell.cs5412.handy.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

public class SPProfile extends Activity 
{
	private EditText edittextLocation;
	private EditText edittextRadius;
	private EditText edittextCost;
	private EditText editTextDescription;
	private Button btnSubmit;
	private Spinner spinnerServiceType;
	private Switch switchAvailability;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);
		setupScreen();
	}
	
	public void setupScreen()
	{
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
	    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
	      
	    // attaching data adapter to spinner
	    spinnerServiceType.setAdapter(dataAdapter);
		
		btnSubmit = (Button) findViewById(R.id.btnSubmit);
		btnSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) 
			{
				
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
