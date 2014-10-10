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

import android.content.Intent;
import android.net.Uri;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import com.onecase.chatroom.ui.ChatroomUI;
import com.onecase.chatroom.util.ChatroomConstants;
import com.onecase.sdk.Util;
import com.onecase.sdk.log.Log;

/**
 * Listens to DataItems and Messages from the local node.
 */
public class DataLayerService extends WearableListenerService {
    private static final String TAG = "Onecase.Wear.DataLayerService";
    private static final String START_ACTIVITY_PATH = "/start-activity";
    private static final String DATA_ITEM_RECEIVED_PATH = "/data-item-received";
    private GoogleApiClient googleApiClient;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Create DataLayerService");
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        googleApiClient.connect();
    }
    
    @Override
    public void onDestroy() {
    	Log.i(TAG, "Destory DataLayerService");
    	googleApiClient.disconnect();
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
            Uri uri = event.getDataItem().getUri();
            String path = uri.getPath();
            if (ChatroomConstants.DataLayer.COUNT_PATH.equals(path)) {
                // Get the node id of the node that created the data item from the host portion of
                // the uri.
                String nodeId = uri.getHost();
                // Set the data of the message to be the bytes of the Uri.
                byte[] payload = uri.toString().getBytes();
                // Send the rpc
                Wearable.MessageApi.sendMessage(googleApiClient, nodeId, DATA_ITEM_RECEIVED_PATH,
                        payload);
            }
        }
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
    	Log.d(TAG, "onMessageReceived: %s", Util.notNullToString(messageEvent));
        // Check to see if the message is to start an activity
        if (messageEvent.getPath().equals(START_ACTIVITY_PATH)) {
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
}
