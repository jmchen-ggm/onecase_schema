// Copyright 2014 By jiaminchen, [jmchen.ggm@gmail.com]
package com.onecase.chatroom.ui;

import java.util.HashSet;
import java.util.List;

import android.app.Activity;
import android.app.Notification;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataApi.DataItemResult;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageApi.SendMessageResult;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.onecase.chatroom.R;
import com.onecase.chatroom.model.NotificationData;
import com.onecase.chatroom.util.ChatroomConstants;
import com.onecase.sdk.Util;
import com.onecase.sdk.handler.OCHandler;
import com.onecase.sdk.log.Log;

/**
 * @author jiaminchen, [jiaminchen@tencent.com]
 **/
public class ChatroomUI extends Activity implements DataApi.DataListener,
		MessageApi.MessageListener, NodeApi.NodeListener, ConnectionCallbacks,
		OnConnectionFailedListener {
	private final static String TAG = "Onecase.Phone.ChatroomUI";
	/**
	 * Request code for launching the Intent to resolve Google Play services
	 * errors.
	 */
	private static final int REQUEST_RESOLVE_ERROR = 1000;

	private GoogleApiClient googleApiClient;
	private boolean isResolvingError = false;
	private int count;

	private OCHandler sendMsgHandler;

	private Button startChatroomBtn;
	private Button startFitnessBtn;
	private Button sendNotificationBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "Create ChatroomUI");
		setContentView(R.layout.chatroom_ui);
		initView();
		googleApiClient = new GoogleApiClient.Builder(this)
				.addApi(Wearable.API).addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).build();
	}

	private void initView() {
		startChatroomBtn = (Button) findViewById(R.id.start_chatroom_btn);
		startChatroomBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (sendMsgHandler != null) {
					sendMsgHandler
							.post(new StartWearActivityTask(
									ChatroomConstants.StartActivity.START_CHATROOM_PATH));
				}
			}
		});
		startFitnessBtn = (Button) findViewById(R.id.start_fitness_btn);
		startFitnessBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (sendMsgHandler != null) {
					sendMsgHandler
							.post(new StartWearActivityTask(
									ChatroomConstants.StartActivity.START_FITNESS_PATH));
				}
			}
		});
		sendNotificationBtn = (Button) findViewById(R.id.send_notification_btn);
		sendNotificationBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (sendMsgHandler != null) {
					NotificationData notificationData = new NotificationData();
					notificationData.title = "this is title";
					notificationData.content = "this is content";
					sendMsgHandler
							.post(new SendDataTask(
									ChatroomConstants.DataLayer.NOTIFICATION_PATH, Util.toJSONString(notificationData).getBytes()));
				}
			}
		});
	}

	private void startSendThread() {
		Thread t = new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				sendMsgHandler = new OCHandler(Looper.myLooper());
				Looper.loop();
			}
		};
		t.setPriority(Thread.MIN_PRIORITY);
		t.start();
		while(sendMsgHandler == null);
	}

	private void stopSendThread() {
		if (sendMsgHandler != null) {
			sendMsgHandler.removeCallbacksAndMessages(null);
			sendMsgHandler.getLooper().quit();
			sendMsgHandler = null;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		startToSendHeartBit();
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (!isResolvingError && googleApiClient != null) {
			googleApiClient.connect();
			startSendThread();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (!isResolvingError && googleApiClient != null) {
			Wearable.DataApi.removeListener(googleApiClient, this);
			Wearable.MessageApi.removeListener(googleApiClient, this);
			Wearable.NodeApi.removeListener(googleApiClient, this);
			googleApiClient.disconnect();
			stopSendThread();
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		if (isResolvingError) {
			// Already attempting to resolve an error.
			return;
		} else if (connectionResult.hasResolution()) {
			try {
				isResolvingError = true;
				connectionResult.startResolutionForResult(this,
						REQUEST_RESOLVE_ERROR);
			} catch (IntentSender.SendIntentException e) {
				// There was an error with the resolution intent. Try again.
				googleApiClient.connect();
				Log.i(TAG, "try to connect again %s",
						Util.notNullToString(connectionResult));
			}
		} else {
			Log.e(TAG, "Connection to Google API client has failed, %s",
					Util.notNullToString(connectionResult));
			isResolvingError = false;
			Wearable.DataApi.removeListener(googleApiClient, this);
			Wearable.MessageApi.removeListener(googleApiClient, this);
			Wearable.NodeApi.removeListener(googleApiClient, this);
		}
	}

	@Override
	public void onConnected(Bundle bundle) {
		Log.i(TAG, "onConnected(): %s", Util.notNullToString(bundle));
		isResolvingError = false;
		Wearable.DataApi.addListener(googleApiClient, this);
		Wearable.MessageApi.addListener(googleApiClient, this);
		Wearable.NodeApi.addListener(googleApiClient, this);
	}

	@Override
	public void onConnectionSuspended(int cause) {
		Log.i(TAG, "Connection to Google API client was suspended, cause: %d",
				cause);
	}

	@Override
	public void onPeerConnected(Node node) {
		Log.d(TAG, "onPeerConnected(): %s", Util.notNullToString(node));
	}

	@Override
	public void onPeerDisconnected(Node node) {
		Log.d(TAG, "onPeerDisconnected(): %s", Util.notNullToString(node));
	}

	@Override
	public void onMessageReceived(MessageEvent messageEvent) {
		Log.d(TAG, "onMessageReceived(): %s",
				Util.notNullToString(messageEvent));
	}

	@Override
	public void onDataChanged(DataEventBuffer dataEventBuffer) {
		final List<DataEvent> events = FreezableUtils
				.freezeIterable(dataEventBuffer);
		dataEventBuffer.close();
		for (DataEvent event : events) {
			if (event.getType() == DataEvent.TYPE_CHANGED) {
				String path = event.getDataItem().getUri().getPath();
				if (path.equals(ChatroomConstants.DataLayer.FITNESS_PATH)) {
					DataMapItem dataMapItem = DataMapItem.fromDataItem(event
							.getDataItem());
					String dataString = new String(dataMapItem.getDataMap()
							.getByteArray(ChatroomConstants.DataLayer.KEY_DATA));
					Log.d(TAG, "dataString=%s", dataString);
				}
				Log.d(TAG, "onDataChanged(): path=%s", path);
			} else if (event.getType() == DataEvent.TYPE_DELETED) {
				Log.d(TAG, "DataItem Deleted: %s", event.getDataItem()
						.toString());
			} else {
				Log.w(TAG, "Unknown data event type=%d", event.getType());
			}
		}
	}

	private class HeartBitTask implements Runnable {

		@Override
		public void run() {
			PutDataMapRequest putDataMapRequest = PutDataMapRequest
					.create(ChatroomConstants.DataLayer.HEART_BIT_PATH);
			PutDataRequest request = putDataMapRequest.asPutDataRequest();

			Log.d(TAG, "Generating DataItem: %s", Util.notNullToString(request));
			if (!googleApiClient.isConnected()) {
				return;
			}
			Wearable.DataApi.putDataItem(googleApiClient, request)
					.setResultCallback(new ResultCallback<DataItemResult>() {
						@Override
						public void onResult(DataItemResult dataItemResult) {
							if (!dataItemResult.getStatus().isSuccess()) {
								Log.e(TAG,
										"ERROR: failed to putDataItem, status code: %d",
										dataItemResult.getStatus()
												.getStatusCode());
							}
						}
					});
			startToSendHeartBit();
		}
	}

	private void startToSendHeartBit() {
		Log.i(TAG, "startToSendHeartBit: count=%d", count);
		if (sendMsgHandler != null) {
			sendMsgHandler.postDelayed(new HeartBitTask(), 5000);
			count++;
		}
	}

	private class SendDataTask implements Runnable {
		private String path;
		private byte[] data;

		public SendDataTask(String path, byte[] data) {
			this.path = path;
			this.data = data;
		}
		
		@Override
		public void run() {
			PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(path);
			putDataMapRequest.getDataMap().putByteArray(ChatroomConstants.DataLayer.KEY_DATA, data);
			PutDataRequest request = putDataMapRequest.asPutDataRequest();
			Log.d(TAG, "Generating DataItem: %s", Util.notNullToString(request));
			if (!googleApiClient.isConnected()) {
				return;
			}
			Wearable.DataApi.putDataItem(googleApiClient, request)
					.setResultCallback(new ResultCallback<DataItemResult>() {
						@Override
						public void onResult(DataItemResult dataItemResult) {
							if (!dataItemResult.getStatus().isSuccess()) {
								Log.e(TAG,
										"ERROR: failed to putDataItem, status code: %d",
										dataItemResult.getStatus()
												.getStatusCode());
							}
						}
					});
		}
	}

	private class StartWearActivityTask implements Runnable {
		private String path;

		public StartWearActivityTask(String path) {
			this.path = path;
		}

		@Override
		public void run() {
			HashSet<String> results = new HashSet<String>();
			NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi
					.getConnectedNodes(googleApiClient).await();

			for (Node node : nodes.getNodes()) {
				results.add(node.getId());
			}

			for (String node : results) {
				Wearable.MessageApi.sendMessage(googleApiClient, node, path,
						new byte[0]).setResultCallback(
						new ResultCallback<SendMessageResult>() {
							@Override
							public void onResult(
									SendMessageResult sendMessageResult) {
								if (!sendMessageResult.getStatus().isSuccess()) {
									Log.e(TAG,
											"Failed to send message with status code: "
													+ sendMessageResult
															.getStatus()
															.getStatusCode());
								}
							}
						});
			}
		}
	}
}
