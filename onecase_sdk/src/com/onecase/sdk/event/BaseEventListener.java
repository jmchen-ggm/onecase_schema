// Copyright 2014 By jiaminchen, [jmchen.ggm@gmail.com]
package com.onecase.sdk.event;

/**
 * Base Event interface
 * 
 * @author jiaminchen, [jiaminchen@tencent.com]
 **/
public abstract class BaseEventListener {

	private final int priority;
	
	public BaseEventListener(final int priority) {
		this.priority = priority;
	}
	
	public int getPriority() {
		return priority;
	}
	
	public abstract boolean callback(BaseEvent baseEvent); 
}
