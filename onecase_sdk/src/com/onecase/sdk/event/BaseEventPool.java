// Copyright 2014 By jiaminchen, [jmchen.ggm@gmail.com]
package com.onecase.sdk.event;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;

import android.os.Handler;
import android.os.Looper;

import com.onecase.sdk.Util;
import com.onecase.sdk.log.Log;

/**
 * Base Event interface
 * 
 * @author jiaminchen, [jiaminchen@tencent.com]
 **/
public class BaseEventPool implements IEventPool {
	private final static String TAG = "Onecase.BaseEventPool";
	private final HashMap<String, LinkedList<BaseEventListener>> listenersMap
		= new HashMap<String, LinkedList<BaseEventListener>>();
	
	@Override
	public void addListener(String eventId, BaseEventListener baseEventListener) {
		Log.v(TAG, "add listener id = %s", eventId);
		LinkedList<BaseEventListener> container = listenersMap.get(eventId);
		if (container == null) {
			container = new LinkedList<BaseEventListener>();
			listenersMap.put(eventId, container);
		}
		container.add(baseEventListener);
	}

	@Override
	public void removeListener(String eventId, BaseEventListener baseEventListener) {
		Log.v(TAG, "remove listener id = %s", eventId);
		LinkedList<BaseEventListener> container = listenersMap.get(eventId);
		if (container == null) {
			return;
		}
		container.remove(baseEventListener);
		if (container.isEmpty()) {
			listenersMap.remove(eventId);
		}
	}

	@Override
	public boolean hasListenerForEvent(BaseEvent baseEvent) {
		Log.v(TAG, "hasListenerForEvent id = %s", baseEvent.getId());
		return listenersMap.containsKey(baseEvent.getId());
	}

	@Override
	public boolean hasListenerForEventId(String eventId) {
		Log.v(TAG, "hasListenerForEventId id = %s", eventId);
		return listenersMap.containsKey(eventId);
	}

	@Override
	public boolean containsListenerForEvent(BaseEvent baseEvent,
			BaseEventListener baseEventListener) {
		Log.v(TAG, "containsListenerForEvent id = %s", baseEvent.getId());
		LinkedList<BaseEventListener> container = listenersMap.get(baseEvent.getId());
		if (container == null) {
			return false;
		} else {
			return container.contains(baseEventListener);
		}
	}

	@Override
	public boolean containsListenerForEventId(String eventId,
			BaseEventListener baseEventListener) {
		Log.v(TAG, "containsListenerForEventId id = %s", eventId);
		LinkedList<BaseEventListener> container = listenersMap.get(eventId);
		if (container == null) {
			return false;
		} else {
			return container.contains(baseEventListener);
		}
	}

	@Override
	public boolean publish(final BaseEvent baseEvent) {
		Log.v(TAG, "publish %s", baseEvent.getId());
		String eventId = baseEvent.getId();
		LinkedList<BaseEventListener> listeners = listenersMap.get(eventId);
		if (listeners == null) {
			Log.w(TAG, "No listener for this event %s, Stack: %s.", eventId, Util.getStack());
			return false;
		}
		trigger(listeners, baseEvent);
		return true;
	}

	@Override
	public void asyncPublic(final BaseEvent baseEvent, final Looper looper) {
		Log.v(TAG, "async publish %s", baseEvent.getId());
		Handler handler = new Handler(looper);
		handler.post(new Runnable() {
			@Override
			public void run() {
				BaseEventPool.this.publish(baseEvent);
			}
		});
	}

	private void trigger(LinkedList<BaseEventListener> listeners,
			BaseEvent baseEvent) {
		if (baseEvent.isOrder()) {
			Collections.sort(listeners, new Comparator<BaseEventListener>() {
				@Override
				public int compare(BaseEventListener lhs, BaseEventListener rhs) {
					return rhs.getPriority() - lhs.getPriority();
				}
			});
		}

		BaseEventListener[] lstArr = new BaseEventListener[listeners.size()];
		listeners.toArray(lstArr);
		for (BaseEventListener listener : lstArr) {
			if (listener.callback(baseEvent) && baseEvent.isOrder()) {
				break;
			}
		}

		if (baseEvent.callback != null) {
			baseEvent.callback.run();
		}		
	}
}
