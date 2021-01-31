package com.cyaegha.sysController.extras;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Set;

import com.IceCreamQAQ.Yu.annotation.Event;
import com.IceCreamQAQ.Yu.annotation.EventListener;
import com.IceCreamQAQ.Yu.event.events.AppStartEvent;

@EventListener
public class ExtrasStore
{
	private static Hashtable<String,Object> hashtable=new Hashtable<>();

	private static ExtrasStore extrasStore;

	@Event
	public void init(AppStartEvent event)
	{
		extrasStore=new ExtrasStore();
	}

	protected static ExtrasStore getExtrasStore()
	{
		return extrasStore;
	}

	protected Object put(String key,Object object)
	{
		return hashtable.put(key,object);
	}

	protected Object get(String key)
	{
		return hashtable.get(key);
	}

	protected void clear()
	{
		hashtable.clear();
	}

	protected int size()
	{
		return hashtable.size();
	}

	protected Object remove(String key)
	{
		return hashtable.remove(key);
	}

	protected Enumeration<String> keys()
	{
		return hashtable.keys();
	}

	protected Enumeration<Object> values()
	{
		return hashtable.elements();
	}

	protected Set<Entry<String,Object>> name()
	{
		return hashtable.entrySet();
	}

	protected boolean containsKey(String key)
	{
		return hashtable.containsKey(key);
	}

	protected boolean containsValues(Object object)
	{
		return hashtable.containsValue(object);
	}
}
