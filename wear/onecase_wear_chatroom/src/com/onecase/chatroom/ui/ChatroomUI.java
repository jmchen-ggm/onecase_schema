// Copyright 2014 By jiaminchen, [jmchen.ggm@gmail.com]
package com.onecase.chatroom.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.onecase.chatroom.R;
import com.onecase.sdk.Util;
import com.onecase.sdk.log.Log;

/**
 * @author jiaminchen, [jiaminchen@tencent.com]
 **/
public class ChatroomUI extends Activity implements ConnectionCallbacks,
		OnConnectionFailedListener, DataApi.DataListener,
		MessageApi.MessageListener, NodeApi.NodeListener {
	private final static String TAG = "Onecase.ChatroomUI";

	private GoogleApiClient googleApiClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "Create ChatroomUI");

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.chatroom_ui);

		googleApiClient = new GoogleApiClient.Builder(this)
				.addApi(Wearable.API).addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).build();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (googleApiClient != null) {
			googleApiClient.disconnect();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (googleApiClient != null) {
			googleApiClient.connect();
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.e(TAG, "onConnectionFailed(): Failed to connect, with result: %s",
				Util.notNullToString(result));
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		Log.i(TAG,
				"onConnected(): Successfully connected to Google API client, connectionHint: %s",
				Util.notNullToString(connectionHint));
		Wearable.DataApi.addListener(googleApiClient, this);
		Wearable.MessageApi.addListener(googleApiClient, this);
		Wearable.NodeApi.addListener(googleApiClient, this);
	}

	@Override
	public void onConnectionSuspended(int cause) {
		Log.i(TAG,
				"onConnectionSuspended(): Connection to Google API client was suspended, cause: %d",
				cause);
	}

	@Override
	public void onDataChanged(DataEventBuffer dataEventBuffer) {
		Log.d(TAG, "onDataChanged(): %s", Util.notNullToString(dataEventBuffer));
	}

	@Override
	public void onMessageReceived(MessageEvent messageEvent) {
		Log.d(TAG, "onMessageReceived(): %s",
				Util.notNullToString(messageEvent));
	}

	@Override
	public void onPeerConnected(Node node) {
		Log.d(TAG, "onPeerConnected(): %s", Util.notNullToString(node));
	}

	@Override
	public void onPeerDisconnected(Node node) {
		Log.d(TAG, "onPeerDisconnected(): %s", Util.notNullToString(node));
	}
}
