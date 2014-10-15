/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.onecase.chatroom.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import com.google.android.gms.wearable.DataApi.DataItemResult;
import com.onecase.chatroom.R;
import com.onecase.chatroom.event.SendDataEvent;
import com.onecase.chatroom.model.NotificationData;
import com.onecase.chatroom.ui.ChatroomUI;
import com.onecase.chatroom.ui.FitnessUI;
import com.onecase.chatroom.ui.MessageListUI;
import com.onecase.chatroom.util.ChatroomConstants;
import com.onecase.sdk.Util;
import com.onecase.sdk.event.BaseEvent;
import com.onecase.sdk.event.BaseEventListener;
import com.onecase.sdk.event.EventCenter;
import com.onecase.sdk.handler.OCHandler;
import com.onecase.sdk.log.Log;

/**
 * Listens to DataItems and Messages from the local node.
 */
public class DataLayerService extends WearableListenerService {
	private static final String TAG = "Onecase.Wear.DataLayerService";
	private static final String DATA_ITEM_RECEIVED_PATH = "/data-item-received";

	private GoogleApiClient googleApiClient;
	private OCHandler sendDataHandler;

	private void startSendThread() {
		Thread t = new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				sendDataHandler = new OCHandler(Looper.myLooper());
				Looper.loop();
			}
		};
		t.setPriority(Thread.MIN_PRIORITY);
		t.start();
		while(sendDataHandler == null);
	}

	private void stopSendThread() {
		if (sendDataHandler != null) {
			sendDataHandler.removeCallbacksAndMessages(null);
			sendDataHandler.getLooper().quit();
			sendDataHandler = null;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "Create DataLayerService");
		EventCenter.getEventPool().addListener(SendDataEvent.ID, sendDataEventListener);
		googleApiClient = new GoogleApiClient.Builder(this)
				.addApi(Wearable.API).build();
		googleApiClient.connect();
		startSendThread();
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "Destory DataLayerService");
		EventCenter.getEventPool().removeListener(SendDataEvent.ID, sendDataEventListener);
		googleApiClient.disconnect();
		stopSendThread();
		super.onDestroy();
	}

	@Override
	public void onDataChanged(DataEventBuffer dataEvents) {
		Log.d(TAG, "onDataChanged(): %s", dataEvents.toString());
		final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        dataEvents.close();
        if(!googleApiClient.isConnected()) {
            ConnectionResult connectionResult = googleApiClient
                    .blockingConnect(30, TimeUnit.SECONDS);
            if (!connectionResult.isSuccess()) {
                Log.e(TAG, "DataLayerListenerService failed to connect to GoogleApiClient.");
                return;
            }
        }

        // Loop through the events and send a message back to the node that created the data item.
        for (DataEvent event : events) {
        	Log.d(TAG, "event: %s", Util.notNullToString(event));
        	handleDataEvent(event);
        }
	}
	
	private void handleDataEvent(DataEvent event) {
		String path = event.getDataItem().getUri().getPath();
		Log.d(TAG, "handleDataEvent path=%s", path);
		if (path.equals(ChatroomConstants.DataLayer.HEART_BIT_PATH)) {
			Uri uri = event.getDataItem().getUri();
			String nodeId = uri.getHost();
			byte[] payload = uri.toString().getBytes();
			if (sendDataHandler != null) {
				sendDataHandler.post(new SendMessageTask(nodeId, payload));
			}
		} else if (path.startsWith(ChatroomConstants.DataLayer.DATA_PHAT_PREFIX)) {
			DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
        	String dataString = new String(dataMapItem.getDataMap().getByteArray(ChatroomConstants.DataLayer.KEY_DATA));
        	Log.d(TAG, "handleDataEvent dataString=%s", dataString);
        	if (path.equals(ChatroomConstants.DataLayer.NOTIFICATION_PATH)) {
        		NotificationData notificationData = Util.toJSONObject(dataString, NotificationData.class);
        		notify(notificationData);
        	}
		}
	}
	
	private void notify(NotificationData notificationData) {
		int notificationId = 001;
        Intent firstPageIntent = new Intent(this, FitnessUI.class);
        PendingIntent firstPagePendingIntent = PendingIntent.getActivity(this, 0, firstPageIntent, 0);
        NotificationCompat.Builder firstPageBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(notificationData.title)
                .setContentText(notificationData.content)
                .setContentIntent(firstPagePendingIntent).setLocalOnly(true);
        
        Intent secondPageIntent = new Intent(this, MessageListUI.class);
        PendingIntent secondPagePendingIntent = PendingIntent.getActivity(this, 0, secondPageIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
		Notification secondPageNotification = new NotificationCompat.Builder(
				this).extend(new NotificationCompat.WearableExtender().setDisplayIntent(
						secondPagePendingIntent).setCustomSizePreset(
						Notification.WearableExtender.SIZE_MEDIUM))
						.build();
		Notification notification = new NotificationCompat.WearableExtender()
				.addPage(secondPageNotification).extend(firstPageBuilder)
				.build();

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(notificationId++, notification);
	}

	@Override
	public void onMessageReceived(MessageEvent messageEvent) {
		Log.d(TAG, "onMessageReceived: %s", Util.notNullToString(messageEvent));
		// Check to see if the message is to start an activity
		if (messageEvent.getPath().equals(
				ChatroomConstants.StartActivity.START_FITNESS_PATH)) {
			Intent startIntent = new Intent(this, FitnessUI.class);
			startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(startIntent);
		}
		if (messageEvent.getPath().equals(
				ChatroomConstants.StartActivity.START_CHATROOM_PATH)) {
			Intent startIntent = new Intent(this, ChatroomUI.class);
			startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(startIntent);
		}
	}

	public void onPeerConnected(Node peer) {
		Log.i(TAG, "onPeerConnected: %s", Util.notNullToString(peer));
	}

	public void onPeerDisconnected(Node peer) {
		Log.i(TAG, "onPeerDisconnected: %s", Util.notNullToString(peer));
	}

	private BaseEventListener sendDataEventListener = new BaseEventListener(0) {
		@Override
		public boolean callback(BaseEvent baseEvent) {
			if (baseEvent instanceof SendDataEvent) {
				SendDataEvent sendDataEvent = (SendDataEvent) baseEvent;
				Log.d(TAG, "sendDataEvent, path=%s", sendDataEvent.path);
				PutDataMapRequest putDataMapRequest = PutDataMapRequest
						.create(sendDataEvent.path);
				putDataMapRequest.getDataMap().putByteArray(
						ChatroomConstants.DataLayer.KEY_DATA,
						sendDataEvent.data);
				PutDataRequest request = putDataMapRequest.asPutDataRequest();
				Log.d(TAG, "Generating DataItem: %s",
						Util.notNullToString(request));
				if (!googleApiClient.isConnected()) {
					return false;
				}
				if (sendDataHandler != null) {
					sendDataHandler.post(new SendDataTask(request));
				}
			}
			return false;
		}
	};

	private class SendDataTask implements Runnable {
		private PutDataRequest request;

		public SendDataTask(PutDataRequest request) {
			this.request = request;
		}

		@Override
		public void run() {
			Wearable.DataApi.putDataItem(googleApiClient, request)
					.setResultCallback(new ResultCallback<DataItemResult>() {
						@Override
						public void onResult(DataItemResult dataItemResult) {
							if (!dataItemResult.getStatus().isSuccess()) {
								Log.e(TAG,
										"ERROR: failed to putDataItem, status code: %d",
										dataItemResult.getStatus().getStatusCode());
							}
						}
					});
		}
	}
	
	private class SendMessageTask implements Runnable {
		
		private String nodeId;
		private byte[] payload; 

		public SendMessageTask(String nodeId, byte[] payload) {
			this.nodeId = nodeId;
			this.payload = payload;
		}
		
		@Override
		public void run() {
			Wearable.MessageApi.sendMessage(googleApiClient, nodeId,
					DATA_ITEM_RECEIVED_PATH, payload);			
		}
		
	}
}
