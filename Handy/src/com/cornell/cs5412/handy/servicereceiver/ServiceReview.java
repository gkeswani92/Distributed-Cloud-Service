package com.cornell.cs5412.handy.servicereceiver;

import com.cornell.cs5412.handy.Globals;
import com.cornell.cs5412.handy.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

public class ServiceReview extends Activity 
{
	public ProgressDialog progress;

	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.service_review);
		setupScreen();
	}

	public void setupScreen() 
	{
		try
		{
			progress = new ProgressDialog(ServiceReview.this);

			final EditText editTextReview = (EditText) findViewById(R.id.editTextReview);
			editTextReview.setTypeface(null, Typeface.NORMAL);
			editTextReview.setText("");
			
			final EditText editTextSubmitBy = (EditText) findViewById(R.id.editTextSubmitBy);
			editTextSubmitBy.setText("");
			
			final RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);

			final Button btnSubmit = (Button) findViewById(R.id.btnSubmit);
			btnSubmit.setOnClickListener(new OnClickListener() {
				public void onClick(View v) 
				{
					String submitBy = editTextSubmitBy.getText().toString();
					String review = editTextReview.getText().toString();
					int ratingStar = (int) ratingBar.getRating();
					
					if (submitBy.equalsIgnoreCase(""))
					{
						Globals.showAlert("Information Missing", "Please enter your name.", ServiceReview.this);
					}
					else if (review.equalsIgnoreCase(""))
					{
						Globals.showAlert("Information Missing", "Please enter a review.", ServiceReview.this);
					}
					else if (ratingStar == 0)
					{
						Globals.showAlert("Information Missing", "Please enter a rating of at least 1 star.", ServiceReview.this);
					}
					else
					{
						submitReview(submitBy, review, ratingStar);
					}

					final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(editTextReview.getWindowToken(), 0);

				}
			});

		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	@Override
	public void onResume() 
	{
		super.onResume();
	}

	@Override
	public void onDestroy() 
	{
		super.onDestroy();
		progress = null;
		System.gc();
	}

	// TODO: connect with the server and send the review
	public void submitReview (final String name, final String review, final int rating)
	{
		
	}
}
