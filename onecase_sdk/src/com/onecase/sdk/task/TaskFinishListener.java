// Copyright 2014 By jiaminchen, [jmchen.ggm@gmail.com]
package com.onecase.sdk.task;

/**
 * @author jiaminchen, [jiaminchen@tencent.com]
 **/
public interface TaskFinishListener {
	void onFinish(BaseTask task, boolean isSuccess, Exception e);
}
