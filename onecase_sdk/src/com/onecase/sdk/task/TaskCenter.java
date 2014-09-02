// Copyright 2014 By jiaminchen, [jmchen.ggm@gmail.com]
package com.onecase.sdk.task;

/**
 * @author jiaminchen, [jiaminchen@tencent.com]
 **/
public class TaskCenter {
	private static ITaskRunner impl = null;

	public static ITaskRunner getTaskRunner() {
		if (impl == null) {
			impl = new TaskRunnerImpl();
		}
		return impl;
	}
}
