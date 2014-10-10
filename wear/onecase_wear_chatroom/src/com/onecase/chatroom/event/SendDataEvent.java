// Copyright 2014 By jiaminchen, [jmchen.ggm@gmail.com]
package com.onecase.chatroom.event;

import com.onecase.sdk.event.BaseEvent;

/**
 * @author jiaminchen, [jiaminchen@tencent.com]
 **/
public class SendDataEvent extends BaseEvent {
	public final static String ID = "Onecase.Wear.SendDataEvent";
	public String path;
	public byte[] data;

	@Override
	public String getId() {
		return ID;
	}
}
