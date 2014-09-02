// Copyright 2014 By jiaminchen, [jmchen.ggm@gmail.com]
package com.onecase.sdk.event;

/**
 * Event, use t get the event pool impl
 * 
 * @author jiaminchen, [jiaminchen@tencent.com]
 **/
public class EventCenter {

	private static IEventPool impl = null;
	
	public static IEventPool getEventPool() {
		if (impl == null) {
			impl = new BaseEventPool();
		}
		return impl;
	}
}
