package com.cornell.cs5412.handy;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.cornell.cs5412.handy.serviceprovider.SPProfile;
import com.cornell.cs5412.handy.servicereceiver.SRHomeSearch;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
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
	private ProgressDialog progress;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		setupScreen();
	}
	
	public void setupScreen()
	{
		progress = new ProgressDialog(LoginActivity.this);
		
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
				final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(edittextPassword.getWindowToken(), 0);
				
				username = edittextUsername.getText().toString();
				password = edittextPassword.getText().toString();
				
				if(username.equalsIgnoreCase("") || password.equalsIgnoreCase(""))
				{
					Globals.showAlert("Error", "Please fill in all necessary information", LoginActivity.this);
				}
				else
				{
					final int selectedId = radioGroupID.getCheckedRadioButtonId();
					Globals.showProgress(progress, "Loading...");
					new Thread(new Runnable() {
						@Override
						public void run() 
						{
							try 
							{
					           	ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
					           	params.add(new BasicNameValuePair("username", username));
					           	params.add(new BasicNameValuePair("password", password));
					           	if(selectedId == radioButtonSP.getId())
					           		params.add(new BasicNameValuePair("userType", "sp"));
					           	else if(selectedId == radioButtonSR.getId())
					           		params.add(new BasicNameValuePair("userType", "sr"));
								        
								final JSONObject json = DataTransfer.postJSONResult(Globals.ipAddress + "/authenticate", params);
										
								runOnUiThread(new Runnable() {
									public void run()
									{											
										try
										{
											if (progress.isShowing())
												progress.dismiss();
											
											if (json.optInt("status") == 0) // login successful
											{
												Intent intent = null;
												if(selectedId == radioButtonSP.getId())
												{
													Globals.sharedPrefs.saveBoolean("loginSPComplete", true);
													intent = new Intent().setClass(LoginActivity.this, SPProfile.class);
												}
												else if(selectedId == radioButtonSR.getId())
												{
													Globals.sharedPrefs.saveBoolean("loginSRComplete", true);
													intent = new Intent().setClass(LoginActivity.this, SRHomeSearch.class);
												}
												intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
												startActivity(intent);
											}
											else
											{
												String error = json.optString("message");
												Globals.showAlert("Error", error, LoginActivity.this);// show error message
											}
										}
										catch(Exception e)
										{
											if (progress.isShowing())
												progress.dismiss();
											
											e.printStackTrace();
											Toast.makeText(LoginActivity.this, "An error has occurred!!", Toast.LENGTH_SHORT).show();
										}
									}
								});
							} 
							catch (Exception e)
							{
								e.printStackTrace();
								runOnUiThread(new Runnable() {
									public void run()
									{
										if (progress.isShowing())
											progress.dismiss();
												
										Globals.showAlert("Error", "There was an error with this transaction", LoginActivity.this);// show error message
									}
								});
							}
						}
					}).start();
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
