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

/**
 * @author jiaminchen, [jiaminchen@tencent.com]
 **/
public class HeartRateUI extends Activity implements SensorEventListener {

	// UI Elements
	private CircledImageView circledImageView;
	private TextView textView;

	private Sensor heartRateSensor;
	private SensorManager sensorManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.heart_rate_ui);

		// View
		final WatchViewStub stub = (WatchViewStub) findViewById(R.id.heart_rate_ui_wvs);
		stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
			@Override
			public void onLayoutInflated(WatchViewStub stub) {
				circledImageView = (CircledImageView) stub
						.findViewById(R.id.circle);
				textView = (TextView) stub.findViewById(R.id.value);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Register the listener
		if (sensorManager != null) {
			sensorManager.registerListener(this, heartRateSensor,
					SensorManager.SENSOR_DELAY_NORMAL);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Unregister the listener
		if (sensorManager != null)
			sensorManager.unregisterListener(this);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// Update your data. This check is very raw. You should improve it when
		// the sensor is unable to calculate the heart rate
		if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
			if ((int) event.values[0] > 0) {
				circledImageView.setCircleColor(getResources().getColor(
						R.color.green));
				textView.setText("" + (int) event.values[0]);
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}
}
