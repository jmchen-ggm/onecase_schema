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
	protected String id;
	/**
	 * has order when publish events
	 */
	protected boolean order;
	
	public String getId() {
		return id;
	}
	
	public boolean isOrder() {
		return order;
	}
}
