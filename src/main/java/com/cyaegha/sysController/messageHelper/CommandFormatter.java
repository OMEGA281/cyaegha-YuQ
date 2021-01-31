package com.cyaegha.sysController.messageHelper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.inject.Inject;

import com.IceCreamQAQ.Yu.annotation.Event;
import com.IceCreamQAQ.Yu.annotation.Event.Weight;
import com.IceCreamQAQ.Yu.annotation.EventListener;
import com.IceCreamQAQ.Yu.event.EventBus;
import com.IceCreamQAQ.Yu.event.events.AppStartEvent;
import com.IceCreamQAQ.Yu.loader.LoadItem;
import com.IceCreamQAQ.Yu.loader.Loader;
import com.cyaegha.surveillance.Log;
import com.cyaegha.sysController.systemInitializationCore.SystemCalss;
import com.cyaegha.sysController.systemInitializationCore.SystemInit;
import com.cyaegha.tools.ClassUtils;
import com.icecreamqaq.yuq.FunKt;
import com.icecreamqaq.yuq.event.MessageEvent;
import com.icecreamqaq.yuq.message.MessageItem;
import com.icecreamqaq.yuq.message.MessageItemFactory;

public class CommandFormatter implements SystemInit
{
	private Set<String> className;
	private HashMap<Pattern,String> map=new HashMap<>();
	private String packageName="com.cyaegha.plugin";
	private ArrayList<Integer> idList=new ArrayList<>();
	@Inject
	private EventBus bus;
	@Inject
	private MessageItemFactory factory;
	
	private String patternFormat(String string)
	{
		Matcher matcher=null;
		String format=null;
		for(Entry<Pattern,String> entry:map.entrySet())
		{
			Matcher $matcher=entry.getKey().matcher(string);
			if($matcher.matches())
			{
				if(format==null||format.length()<entry.getValue().length())
				{
					matcher=$matcher;
					format=entry.getValue();
				}
			}
		}
		if(matcher==null)
			return null;
		int groupCount=matcher.groupCount();
		if(groupCount==0)
			return format;
		else
		{
			String[] groups=new String[groupCount];
			for(int i=1;i<=groupCount;i++)
				groups[i-1]=matcher.group(i);
			return String.format(format,groups);
		}
	}

	@Event(weight=Weight.high)
	public void deal(MessageEvent event)
	{
		String string=patternFormat(event.getMessage().getCodeStr());
		if(string==null)
			return;
		event.getMessage().getBody().clear();
		event.getMessage().getBody().add(factory.text(string));
		ArrayList<MessageItem> list=new ArrayList<>();
		for(String part:string.split(" "))
			list.add(factory.text(part));
		event.getMessage().setPath(list);
	}

	@Override
	public void init()
	{
		className=ClassUtils.getClassName(packageName,false);
		for(String string:className)
		{
			Class<?> clazz;
			try
			{
				clazz=Class.forName(string);
			}catch(ClassNotFoundException e)
			{
				Log.e("无法找到类"+e);
				continue;
			}
			Method[] methods=clazz.getMethods();
			for(Method method:methods)
			{
				com.cyaegha.sysController.messageHelper.Pattern annotation=method
						.getAnnotation(com.cyaegha.sysController.messageHelper.Pattern.class);
				if(annotation==null)
					continue;
				try
				{
					map.put(Pattern.compile(annotation.pattern()),annotation.format());
				}catch(PatternSyntaxException e)
				{
					Log.e("形成匹配失败");
					continue;
				}
			}
		}
	}

	@Override
	public int width()
	{
		// TODO Auto-generated method stub
		return 10;
	}
}
