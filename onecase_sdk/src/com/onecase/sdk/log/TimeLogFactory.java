// Copyright 2014 By jiaminchen, [jmchen.ggm@gmail.com]
package com.onecase.sdk.log;

/**
 * Time Log Factory
 * 
 * @author jiaminchen, [jiaminchen@tencent.com]
 **/
public class TimeLogFactory {

	public static ITimeLog craete() {
		return new TimeLogImpl();
	}
}
