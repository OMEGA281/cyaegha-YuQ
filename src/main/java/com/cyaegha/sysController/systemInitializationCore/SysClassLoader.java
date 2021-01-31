package com.cyaegha.sysController.systemInitializationCore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.inject.Inject;

import com.IceCreamQAQ.Yu.di.YuContext;
import com.IceCreamQAQ.Yu.loader.LoadItem;
import com.IceCreamQAQ.Yu.loader.Loader;

public class SysClassLoader implements Loader
{

	@Inject
	private YuContext context;
	
	@Override
	public void load(Map<String,LoadItem> arg0)
	{
		List<SystemInit> list=new ArrayList<>();
		for(Entry<String,LoadItem> entry:arg0.entrySet())
		{
			Class<?> clazz=entry.getValue().getType();
			Object object=context.get(clazz);
			if(object==null)
				continue;
			
			list.add(SystemInit.class.cast(object));
		}
		Collections.sort(list,(SystemInit o1,SystemInit o2)->o2.width()-o1.width());
		list.forEach((obj)->obj.init());
	}

	@Override
	public int width()
	{
		// TODO Auto-generated method stub
		return 10;
	}
	
}
