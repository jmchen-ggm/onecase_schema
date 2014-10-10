// Copyright 2014 By jiaminchen, [jmchen.ggm@gmail.com]
package com.onecase.chatroom.ui;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.CircledImageView;
import android.support.wearable.view.WatchViewStub;
import android.widget.TextView;

import com.onecase.chatroom.R;
import com.onecase.chatroom.event.SendDataEvent;
import com.onecase.chatroom.model.FitnessData;
import com.onecase.chatroom.util.ChatroomConstants;
import com.onecase.sdk.Util;
import com.onecase.sdk.event.EventCenter;
import com.onecase.sdk.log.Log;

/**
 * @author jiaminchen, [jiaminchen@tencent.com]
 **/
public class FitnessUI extends Activity implements SensorEventListener {

	private final static String TAG	= "Onecase.Wear.FitnessUI";
	
	// UI Elements
	boolean isUIReady;
	private CircledImageView circledImageView;
	private TextView heartRateTV;
	private TextView stepCountTV;

	// Sensor
	private Sensor heartRateSensor;
	private Sensor stepCountSensor;
	private SensorManager sensorManager;
	
	private int heartRate;
	private int stepCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "Create FitnessUI");
		setContentView(R.layout.fitness_ui);

		// View
		final WatchViewStub stub = (WatchViewStub) findViewById(R.id.fitness_ui_wvs);
		stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
			@Override
			public void onLayoutInflated(WatchViewStub stub) {
				circledImageView = (CircledImageView) stub
						.findViewById(R.id.circle);
				heartRateTV = (TextView) stub.findViewById(R.id.heart_rate_tv);
				stepCountTV = (TextView) stub.findViewById(R.id.step_count_tv);
				isUIReady = true;
				updateUI();
			}
		});
		
		sensorManager = ((SensorManager)getSystemService(SENSOR_SERVICE));
		heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
		stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Register the listener
		if (sensorManager != null) {
			sensorManager.registerListener(this, heartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
			sensorManager.registerListener(this, stepCountSensor, SensorManager.SENSOR_DELAY_NORMAL);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Unregister the listener
		if (sensorManager != null) {
			sensorManager.unregisterListener(this);
		}
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// Update your data. This check is very raw. You should improve it when
		// the sensor is unable to calculate the heart rate
		if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
			if ((int) event.values[0] > 0 && event.accuracy >= 2) {
				heartRate = (int) event.values[0];
			}
			Log.d(TAG, "HeartRate value=%s | accuracy=%d | Name=%s", Util.notNullToString(event.values[0]), event.accuracy,
					event.sensor.getName());
			updateUI();
		} else if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
			if ((int) event.values[0] > 0 && event.accuracy >= 2) {
				stepCount = (int) event.values[0];
			}
			Log.d(TAG, "StepCount value=%s | accuracy=%d | Name=%s", Util.notNullToString(event.values[0]), event.accuracy,
					event.sensor.getName());
			updateUI();
			sendFitnessData();
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		Log.d(TAG, "onAccuracyChanged(), sensor: %s, accuracy: %d", Util.notNullToString(sensor), accuracy);
	}
	
	private void updateUI() {
		if (!isUIReady) {
			return;
		}
		circledImageView.setCircleColor(getResources().getColor(
				R.color.green));
		heartRateTV.setText("" + heartRate);
		stepCountTV.setText("" + stepCount);
	}
	
	private void sendFitnessData() {
		FitnessData fitnessData = new FitnessData();
		fitnessData.heartRate = heartRate;
		fitnessData.stepCount = stepCount;
		SendDataEvent sendDataEvent = new SendDataEvent();
		sendDataEvent.path = ChatroomConstants.DataLayer.FITNESS_PATH;
		sendDataEvent.data = Util.toJSONString(fitnessData).getBytes();
		EventCenter.getEventPool().publish(sendDataEvent);
	}
}
