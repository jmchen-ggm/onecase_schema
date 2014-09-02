// Copyright 2014 By jiaminchen, [jmchen.ggm@gmail.com]
package com.onecase.sdk;

/**
 * @author jiaminchen, [jmchen.ggm@gmail.com]
 **/
public class BaseConfig {
	public final static int BUILD_STATUS_DEBUG = 0;
	public final static int BUILD_STATUS_RELEASE = 1;
	
	public static int BUILD_STATUS = BUILD_STATUS_DEBUG;
	
	public static boolean isDebug() {
		return BUILD_STATUS == BUILD_STATUS_DEBUG;
	}
}
