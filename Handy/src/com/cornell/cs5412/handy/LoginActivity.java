package com.cornell.cs5412.handy;

import com.cornell.cs5412.handy.serviceprovider.SPProfile;
import com.cornell.cs5412.handy.servicereceiver.SRHomeSearch;
import com.cornell.cs5412.handy.servicereceiver.ServiceReview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class LoginActivity extends Activity 
{
	private Button btnSignIn;
	private Button btnSignUp;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setupScreen();
	}
	
	public void setupScreen()
	{
		setContentView(R.layout.login);
		
		btnSignIn = (Button) findViewById(R.id.btnSignIn);
		btnSignIn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) 
			{
				Intent intent;
				intent = new Intent().setClass(LoginActivity.this, ServiceReview.class);
				startActivity(intent);
			}
		});
		
		btnSignUp = (Button) findViewById(R.id.btnSignUp);
		btnSignUp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) 
			{
				Intent intent;
				intent = new Intent().setClass(LoginActivity.this, SRHomeSearch.class);
				startActivity(intent);
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
