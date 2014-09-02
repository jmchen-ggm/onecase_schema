// Copyright 2014 By jiaminchen, [jmchen.ggm@gmail.com]
package com.onecase.sdk.handler;

import android.os.Handler;
import android.os.Looper;

/**
 * @author jiaminchen, [jiaminchen@tencent.com]
 **/
public class OCHandler extends Handler {
	private final static String TAG = "OneCase.OCHandler";

	public OCHandler() {
		super();
	}

	public OCHandler(Callback callback) {
		super(callback);
	}

	public OCHandler(Looper looper, Callback callback) {
		super(looper, callback);
	}

	public OCHandler(Looper looper) {
		super(looper);
	}
}
