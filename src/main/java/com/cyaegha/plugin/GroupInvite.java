package com.cyaegha.plugin;

import com.IceCreamQAQ.Yu.annotation.Event;
import com.IceCreamQAQ.Yu.annotation.EventListener;
import com.icecreamqaq.yuq.event.GroupInviteEvent;

@EventListener
public class GroupInvite
{
	@Event
	public void agree(GroupInviteEvent event)
	{
		event.setAccept(true);
	}
}
