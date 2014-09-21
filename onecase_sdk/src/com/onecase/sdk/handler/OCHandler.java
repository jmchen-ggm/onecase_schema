// Copyright 2014 By jiaminchen, [jmchen.ggm@gmail.com]
package com.onecase.sdk.handler;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

/**
 * @author jiaminchen, [jiaminchen@tencent.com]
 **/
public class OCHandler extends Handler {
	private final static String TAG = "Onecase.OCHandler";
	private Long threadId;
	
	public OCHandler() {
		super();
		Log.i(TAG, "Create Handler");
	}

	public OCHandler(Callback callback) {
		super(callback);
		Log.i(TAG, "Create Handler");
	}

	public OCHandler(Looper looper, Callback callback) {
		super(looper, callback);
		Log.i(TAG, "Create Handler");
	}

	public OCHandler(Looper looper) {
		super(looper);
		Log.i(TAG, "Create Handler");
	}
	
	public void setThreadId(Long threadId) {
		this.threadId = threadId;
	}
	
	public Long getThreadId() {
		return threadId;
	}
}
