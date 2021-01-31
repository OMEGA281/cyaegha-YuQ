package com.cyaegha.plugin;

import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Pattern;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Event;
import com.IceCreamQAQ.Yu.annotation.Event.Weight;
import com.IceCreamQAQ.Yu.annotation.EventListener;
import com.IceCreamQAQ.Yu.event.events.AppStartEvent;
import com.cyaegha.Permission;
import com.cyaegha.pluginHelper.AuthirizerUser;
import com.cyaegha.pluginHelper.NormalAuthirizer;
import com.cyaegha.pluginHelper.annotations.MinimumAuthority;
import com.cyaegha.pluginHelper.annotations.RegistCommand;
import com.cyaegha.pluginHelper.annotations.RegistListener.MessageReceiveListener;
import com.cyaegha.pluginHelper.dataExchanger.DataExchanger;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.PrivateController;
import com.icecreamqaq.yuq.event.MessageEvent;

@EventListener
@GroupController
@PrivateController
public class PhraseListener
{
	private static final String Split="##";
	private static final String LISTNAME="return";

	DataExchanger exchanger;

	@Event()
	public void init(AppStartEvent event)
	{
		exchanger=DataExchanger.getDataExchanger(getClass());
	}

	@MessageReceiveListener
	@Event(weight=Weight.low)
	public String listener(MessageEvent event)
	{
		// 正则表 格式：正则表达式#回复1#回复2……
		ArrayList<String> list=exchanger.getList(LISTNAME);
		if(list==null)
			return null;
		for(String string:list)
		{
			String[] ss=string.split(Split,2);
			if(ss.length<2)
				continue;
			String patternString=ss[0];
			Pattern pattern=Pattern.compile(patternString);
			if(pattern.matcher(event.getMessage().getCodeStr()).matches())
			{
				String[] answer=ss[1].split(Split);
				if(answer.length==1)
					return answer[0];
				else
				{
					Random random=new Random();
					return answer[random.nextInt(answer.length)];
				}
			}
		}
		return null;
	}

	@MinimumAuthority(AuthirizerUser.OP)
	@RegistCommand(CommandString="answer add",Help="添加一个监听器回复")
	@Action("{pt:\\.。}answer{sp:\b*?}add{sp1:\b*?}{string1}{sp2:\b+}{string2}")
	public String addanswer(String string1,String string2,long qq)
	{
		if(NormalAuthirizer.getPermission(qq)>=Permission.OP)
		{
			ArrayList<String> texts=exchanger.getList(LISTNAME);
//			完全空的表
			if(texts==null||texts.isEmpty())
			{
				exchanger.addList(LISTNAME,string1+Split+string2,false);
				return "成功添加了条目";
			}
//			表中有了
			for(int i=0;i<texts.size();i++)
			{
				String string=texts.get(i);
				String[] ss=string.split(Split,2);
				String pattern;
				String[] answer;
				if(ss.length<2)
				{
					exchanger.deleteList(LISTNAME,string);
					continue;
				}else
				{
					pattern=ss[0];
					answer=ss[1].split(Split);
				}
				if(!pattern.equals(string1))
					continue;
				for(String s:answer)
					if(s.equals(string2))
						return "表中已有相同条目";
				exchanger.deleteList(LISTNAME,string);
				exchanger.addList(LISTNAME,string+Split+answer,false);
				return "成功添加了条目";
			}
			exchanger.addList(LISTNAME,string1+Split+string2,false);
			return "成功添加了条目";
		}else
			return "你没有权限";

	}

	@MinimumAuthority(AuthirizerUser.OP)
	@RegistCommand(CommandString="answer delete",Help="删除一个监听器回复")
	@Action("{pt:\\.。}answer{sp:\b*?}add{sp1:\b*?}{patten}{sp2:\b+}{aim}")
	public String deleteanswer(String patten,String aim,long qq)
	{
		if(NormalAuthirizer.getPermission(qq)>=Permission.OP)
		{
			ArrayList<String> list=exchanger.getList(LISTNAME);
			boolean b=false;
			for(int i=0;i<list.size();i++)
			{
				String text=list.get(i);
				String[] source=text.split(Split,2);
				String pattern;
				String[] answer;
				if(source.length<2)
				{
					exchanger.deleteList(LISTNAME,text);
					continue;
				}else
				{
					pattern=source[0];
					answer=source[1].split(Split);
				}

				if(!pattern.equals(patten))
					continue;
				if(aim!=null&&!aim.isEmpty())
				{
					boolean find=false;
					for(int o=0;o<answer.length;o++)
					{
						String string=answer[o];
						if(string.equals(aim))
						{
							answer[o]="";
							find=true;
							break;
						}
					}
					if(find)
					{
						StringBuffer buffer=new StringBuffer();
						for(String string:answer)
							if(!string.isEmpty())
								buffer.append(string+Split);
						b=exchanger.deleteList(LISTNAME,text);
						if(buffer.length()>0)
							exchanger.addList(LISTNAME,pattern+Split+buffer.toString());
						return b? "成功删除了条目":"没有这个条目";
					}else
					{
						return "未检测指定的回复";
					}
				}else
				{
					b=exchanger.deleteList(LISTNAME,text);
					break;
				}
			}
			return b? "成功删除了条目":"没有这个条目";
		}else
			return "你没有权限";
	}

	@MinimumAuthority(AuthirizerUser.OP)
	@RegistCommand(CommandString="answer delete",Help="删除一个监听器回复")
	@Action("{pt:\\.。}answer{sp:\b*?}add{sp1:\b*?}{patten}")
	public String deleteanswer(Object patten,long qq)
	{
		if(NormalAuthirizer.getPermission(qq)>=Permission.OP)
		{
			ArrayList<String> list=exchanger.getList(LISTNAME);
			boolean b=false;
			for(int i=0;i<list.size();i++)
			{
				String text=list.get(i);
				String pattern=text.split(Split,2)[0];
				if(!pattern.equals(patten))
					continue;

				b=exchanger.deleteList(LISTNAME,text);
				break;
			}
			return b? "成功删除了条目":"没有这个条目";
		}else
			return "你没有权限";
	}
}
