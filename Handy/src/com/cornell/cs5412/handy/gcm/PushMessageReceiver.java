package com.cornell.cs5412.handy.gcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class PushMessageReceiver extends BroadcastReceiver {
	/**
     * Intent's extra that contains the message to be displayed.
     */
    public static final String EXTRA_MESSAGE = "message";
    public static final String EXTRA_IDENTIFIER = "identifier";
    public static final String DISPLAY_MESSAGE_ACTION = "com.cornell.cs5412.handy.DISPLAY_MESSAGE";
    
    public static PushMessageReceiver receiver = null;
    private PushMessageListener listener = null;
    
    public static PushMessageReceiver getInstance() {
    	if(receiver == null) {
    		receiver = new PushMessageReceiver();
    	}
    	
    	return receiver;
    }
    
    public void setListener(PushMessageListener listener) {
    	this.listener = listener;
    }

	@Override
    public void onReceive(Context context, Intent intent)
	{
		Log.d("PushMessageReceiver", "on receive");
		Bundle bundle = intent.getExtras();
		String message = bundle.getString(EXTRA_MESSAGE);
		String identifier = bundle.getString(EXTRA_IDENTIFIER);
		
		Log.d("PushMessageReceiver", "message: " + message);
		Log.d("PushMessageReceiver", "identifier: " + identifier);

        
        if(listener != null) 
        {
        	listener.onMessage(message, identifier);
        }
    }
}
