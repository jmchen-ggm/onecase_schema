// Copyright 2014 By jiaminchen, [jmchen.ggm@gmail.com]
package com.onecase.sdk.task;

/**
 * It will call when thread is idle
 * 
 * @author jiaminchen, [jiaminchen@tencent.com]
 **/
interface ThreadIdleListener {
	void idle(Long id);
}
