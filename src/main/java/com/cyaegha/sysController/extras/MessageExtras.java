package com.cyaegha.sysController.extras;

import java.util.Enumeration;
import com.icecreamqaq.yuq.event.MessageEvent;

public class MessageExtras
{
	public Object put(MessageEvent event,String symbol,Object object)
	{
		return ExtrasStore.getExtrasStore().put(getSymbol(event,symbol),object);
	}

	public Object get(MessageEvent event,String symbol)
	{
		return ExtrasStore.getExtrasStore().get(getSymbol(event,symbol));
	}

	public Object remove(MessageEvent event,String symbol)
	{
		return ExtrasStore.getExtrasStore().remove(getSymbol(event,symbol));
	}

	public int size(MessageEvent event)
	{
		int size=0;
		Enumeration<String> keys=ExtrasStore.getExtrasStore().keys();
		for(String key=null;keys.hasMoreElements();key=keys.nextElement())
			if(key.startsWith(event.getMessage().getId()+"-"))
				size++;
		return size;
	}

	public boolean containsKey(MessageEvent event,String symbol)
	{
		return ExtrasStore.getExtrasStore().containsKey(getSymbol(event,symbol));
	}

	private String getSymbol(MessageEvent event,String symbol)
	{
		return event.getMessage().getId()+"-"+symbol;
	}
}
