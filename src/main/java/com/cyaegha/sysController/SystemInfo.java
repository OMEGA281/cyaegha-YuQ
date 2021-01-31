package com.cyaegha.sysController;

import javax.inject.Inject;

import com.IceCreamQAQ.Yu.annotation.Event;
import com.IceCreamQAQ.Yu.annotation.EventListener;
import com.IceCreamQAQ.Yu.event.events.AppStartEvent;
import com.icecreamqaq.yuq.YuQ;

@EventListener
public class SystemInfo
{
	private static long botQQ;

	@Inject
	private YuQ yuQ;

	@Event
	public void init(AppStartEvent event)
	{
		botQQ=yuQ.getBotId();
	}

	public static long getBotQQ()
	{
		return botQQ;
	}
}
