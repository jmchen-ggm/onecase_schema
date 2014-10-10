// Copyright 2014 By jiaminchen, [jmchen.ggm@gmail.com]
package com.onecase.sdk.event;

/**
 * Base Event interface
 * 
 * @author jiaminchen, [jiaminchen@tencent.com]
 **/
public abstract class BaseEvent {
	/**
	 * Event callback
	 */
	public Runnable callback = null;
	/**
	 * has order when publish events
	 */
	protected boolean order;
	
	public abstract String getId();
	
	public boolean isOrder() {
		return order;
	}
}
