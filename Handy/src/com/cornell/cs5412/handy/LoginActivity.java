package com.cornell.cs5412.handy;

import com.cornell.cs5412.handy.serviceprovider.SPProfile;
import com.cornell.cs5412.handy.servicereceiver.SRHomeSearch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class LoginActivity extends Activity 
{
	private Button btnSignIn;
	private Button btnSignUp;
	private RadioGroup radioGroupID;
	private RadioButton radioButtonSP;
	private RadioButton radioButtonSR;
	private EditText edittextUsername;
	private EditText edittextPassword;
	private String username = "";
	private String password = "";
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		setupScreen();
	}
	
	public void setupScreen()
	{
		radioGroupID = (RadioGroup) findViewById(R.id.radioGroupID);
		radioButtonSP = (RadioButton) findViewById(R.id.radioButtonSP);
		radioButtonSR = (RadioButton) findViewById(R.id.radioButtonSR);
		edittextUsername = (EditText) findViewById(R.id.edittextUsername);
		edittextPassword = (EditText) findViewById(R.id.edittextPassword);

		btnSignIn = (Button) findViewById(R.id.btnSignIn);
		btnSignIn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) 
			{
				username = edittextUsername.getText().toString();
				password = edittextPassword.getText().toString();
				
				if(username.equalsIgnoreCase("") || password.equalsIgnoreCase(""))
				{
					Globals.showAlert("Error", "Please fill in all necessary information", LoginActivity.this);
				}
				else
				{
					int selectedId = radioGroupID.getCheckedRadioButtonId();
				
					if(selectedId == radioButtonSP.getId()) //login as service provider
					{
						// TODO: need to add authentication with the server with username/password
						
						Intent intent;
						intent = new Intent().setClass(LoginActivity.this, SPProfile.class);
						startActivity(intent);
					} 
					else if(selectedId == radioButtonSR.getId())// login as service receiver
					{
						// TODO: need to add authentication with the server username/password
						
						Intent intent;
						intent = new Intent().setClass(LoginActivity.this, SRHomeSearch.class);
						startActivity(intent);
					}
					else
					{
						Toast.makeText(LoginActivity.this, "There was an error!", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
		
		btnSignUp = (Button) findViewById(R.id.btnSignUp);
		btnSignUp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) 
			{
				// go to an activity to setup an account
				
				//Intent intent;
				//intent = new Intent().setClass(LoginActivity.this, SRHomeSearch.class);
				//startActivity(intent);
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
