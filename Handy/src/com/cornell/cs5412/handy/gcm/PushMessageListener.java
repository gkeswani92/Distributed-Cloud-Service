package com.cornell.cs5412.handy.gcm;

public interface PushMessageListener {
	public void onMessage(String alertMessage, String identifier);
}
