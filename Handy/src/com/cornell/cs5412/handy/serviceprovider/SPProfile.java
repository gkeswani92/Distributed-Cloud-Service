package com.cornell.cs5412.handy.serviceprovider;

import java.util.ArrayList;
import java.util.List;
import com.cornell.cs5412.handy.Globals;
import com.cornell.cs5412.handy.R;
import android.app.Activity;
import android.os.Bundle;
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
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);
		setupScreen();
	}
	
	public void setupScreen()
	{
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
				String name = edittextName.getText().toString();
				String location = edittextLocation.getText().toString();
				String radius = edittextRadius.getText().toString();
				String cost = edittextCost.getText().toString();
				String description = editTextDescription.getText().toString();
				int selectServiceType = spinnerServiceType.getSelectedItemPosition();
				if(name.equalsIgnoreCase("") || location.equalsIgnoreCase("") || radius.equalsIgnoreCase("") || cost.equalsIgnoreCase("") || selectServiceType == 0)
				{
					Globals.showAlert("Error", "Please enter all necessary information!", SPProfile.this);
				}
				else
				{
					if (switchAvailability.isChecked()) // upload to server
					{
						
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
