// Copyright 2014 By jiaminchen, [jmchen.ggm@gmail.com]
package com.onecase.sdk.event;

import android.os.Looper;

/**
 * Base Event Pool interface
 * 
 * @author jiaminchen, [jiaminchen@tencent.com]
 **/
public interface IEventPool {

	public void addListener(final String eventId, final BaseEventListener baseEventListener);
	
	public void removeListener(final String eventId, final BaseEventListener baseEventListener);
	
	public boolean hasListenerForEvent(final BaseEvent baseEvent);
	
	public boolean hasListenerForEventId(final String eventId);
	
	public boolean containsListenerForEvent(final BaseEvent baseEvent, final BaseEventListener baseEventListener);
	
	public boolean containsListenerForEventId(final String eventId, final BaseEventListener baseEventListener);
	
	public boolean publish(final BaseEvent baseEvent);
	
	public void asyncPublic(final BaseEvent baseEvent, Looper looper);
	
}
