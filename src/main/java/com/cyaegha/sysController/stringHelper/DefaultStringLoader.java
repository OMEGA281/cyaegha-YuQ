package com.cyaegha.sysController.stringHelper;

import java.util.HashMap;
import java.util.Map;

import com.IceCreamQAQ.Yu.loader.LoadItem;
import com.IceCreamQAQ.Yu.loader.Loader;

import lombok.Getter;
import lombok.var;

public class DefaultStringLoader implements Loader
{
	@Getter
	private static LocalStringMapping defaultStringMapping;
	@Override
	public void load(Map<String,LoadItem> arg0)
	{
		Map<Class<?>,String> map=new HashMap<Class<?>,String>();
		for(var entry:arg0.entrySet())
		{
			LinkStringXML annotation=entry.getValue().getType().getAnnotation(LinkStringXML.class);
			String[] stringLists=annotation.value();
			String defaultString=annotation.defaultString();
			map.put(entry.getValue().getType(),defaultString);
		}
		defaultStringMapping=new LocalStringMapping(map);
	}

	@Override
	public int width()
	{
		return 9;
	}

}
