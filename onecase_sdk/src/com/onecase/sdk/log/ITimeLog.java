// Copyright 2014 By jiaminchen, [jmchen.ggm@gmail.com]
package com.onecase.sdk.log;

/**
 * Time Log interface
 * 
 * @author jiaminchen, [jiaminchen@tencent.com]
 **/
public interface ITimeLog {
	public void start(int id);
	public void stage(int id, String commentFormat, Object... args);
	public void stop(int id);
}
