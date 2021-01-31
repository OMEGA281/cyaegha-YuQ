package com.cyaegha.plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Event;
import com.IceCreamQAQ.Yu.annotation.EventListener;
import com.IceCreamQAQ.Yu.event.events.AppStartEvent;
import com.cyaegha.pluginHelper.AuthirizerUser;
import com.cyaegha.pluginHelper.annotations.MinimumAuthority;
import com.cyaegha.pluginHelper.annotations.RegistCommand;
import com.cyaegha.pluginHelper.dataExchanger.DataExchanger;
import com.cyaegha.surveillance.Log;
import com.cyaegha.tools.XMLUtils;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.PrivateController;
import com.icecreamqaq.yuq.entity.Friend;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.message.MessageItemFactory;

@EventListener
@GroupController
@PrivateController
public class Draw
{
	private File CARDPOOL_PATH;
	private DataExchanger exchanger;

	@Inject
	private MessageItemFactory factory;

	private int stack=0;

	@Event
	public void init(AppStartEvent event)
	{
		exchanger=DataExchanger.getDataExchanger(getClass());
		CARDPOOL_PATH=exchanger.getPluginDataFloder();
	}

	@RegistCommand(CommandString="draw",Help="抽牌")
	@Action(".draw {name} {time}")
	@com.cyaegha.sysController.messageHelper.Pattern(pattern="[\\.。]draw\\s*(.+?)\\s*([0-9]+)",format=".draw %s %s")
	public String draw(String name,int time,Friend qq,Member sender)
	{
		long qqNum,groupNum;
		if(qq==null)
		{
			qqNum=sender.getId();
			groupNum=sender.getGroup().getId();
		}else
		{
			qqNum=qq.getId();
			groupNum=0l;
		}
		Document file;
		try
		{
			file=getDraw(name);
		}catch(Exception e1)
		{
			return e1.getMessage();
		}
		String string=advanceDraw(file,time,qq==null? sender.getNameCard():qq.getName(),qqNum);
		return string;
	}

	@RegistCommand(CommandString="draw",Help="抽牌")
	@Action(".draw {name}")
	@com.cyaegha.sysController.messageHelper.Pattern(pattern="[\\.。]draw\\s*(.+?)",format=".draw %s")
	public String draw(String name,Friend qq,Member sender)
	{
		return draw(name,1,qq,sender);
	}

	@RegistCommand(CommandString="draw",Help="抽牌")
	@Action(".draw {time}")
	@com.cyaegha.sysController.messageHelper.Pattern(pattern="[\\.。]draw\\s*([0-9]+)",format=".draw %s")
	public String draw(Integer time,Friend qq,Member sender)
	{
		long qqNum,groupNum;
		if(qq==null)
		{
			qqNum=sender.getId();
			groupNum=sender.getGroup().getId();
		}else
		{
			qqNum=qq.getId();
			groupNum=0l;
		}
		try
		{
			return draw(getDefaultDrawName(qqNum,groupNum),time,qq,sender);
		}catch(Exception e1)
		{
			return e1.getMessage();
		}
	}

	@RegistCommand(CommandString="draw",Help="抽牌")
	@Action(".draw")
	@com.cyaegha.sysController.messageHelper.Pattern(pattern="[\\.。]draw",format=".draw")
	public String draw(Friend qq,Member sender)
	{
		return draw(1,qq,sender);
	}

	@RegistCommand(CommandString="draw list",Help="列出所有牌库")
	@Action(".draw list")
	@com.cyaegha.sysController.messageHelper.Pattern(pattern="[\\.。]draw\\s*list",format=".draw list")
	public String drawlist()
	{
		ArrayList<String> arrayList=list();
		StringBuilder stringBuilder=new StringBuilder("目前存在着如下牌库：\n");
		for(String string:arrayList)
		{
			stringBuilder.append(string+"/");
		}
		if(stringBuilder.length()>0)
			stringBuilder.deleteCharAt(stringBuilder.length()-1);
		return stringBuilder.toString();
	}

	@MinimumAuthority(AuthirizerUser.GROUP_MANAGER)
	@RegistCommand(CommandString="draw set",Help="在本环境里设置默认牌库")
	@Action(".draw set {string}")
	@com.cyaegha.sysController.messageHelper.Pattern(pattern="[\\.。]draw\\s*set\\s*(.+?)",format=".draw set %s")
	public String drawset(String string,Friend qq,Member sender)
	{
		long qqNum,groupNum;
		if(qq==null)
		{
			qqNum=sender.getId();
			groupNum=sender.getGroup().getId();
		}else
		{
			qqNum=qq.getId();
			groupNum=0l;
		}
		ArrayList<String> arrayList=list();
		if(!arrayList.contains(string))
			return "未查询到牌库："+string;
		exchanger.setItem(getMark(qqNum,groupNum),string);
		return "将"+string+"设置为默认牌库";
	}

	private String drawCard(ArrayList<String> pool)
	{
		Random random=new Random();
		return pool.get(random.nextInt(pool.size()));
	}

	private ArrayList<String> list()
	{
		File[] files=CARDPOOL_PATH.listFiles();
		ArrayList<String> arrayList=new ArrayList<String>();
		for(File file:files)
		{
			if(file.getName().endsWith(".xml"))
			{
				String filename=file.getName();
				String simpleName=filename.substring(0,filename.length()-4);
				arrayList.add(simpleName);
			}
		}
		return arrayList;
	}

	private Document getDraw(String name) throws Exception
	{
		String file=CARDPOOL_PATH+"\\"+name+".xml";
		Document document;
		try
		{
			document=XMLUtils.getDocument(file,false);
		}catch(JDOMException e)
		{
			throw new Exception("读取出现错误！");
		}catch(IOException e)
		{
			throw new FileNotFoundException("找不到牌库："+name);
		}
		return document;
	}

	private String getDefaultDrawName(long qq,long group) throws Exception
	{
		String mark=getMark(qq,group);
		String name=exchanger.getItem(mark);
		if(name==null)
			throw new Exception("没有设置默认牌库");
		return name;
	}

	private String getMark(long qq,long group)
	{
		return group==0? String.format("P%d",qq):String.format("G%d",group);
	}

	private String advanceDraw(Document document,int num,String qqName,long qq)
	{
		ArrayList<String> list=new ArrayList<>();
		int limit;

		Element root=document.getRootElement();
		Element ver=root.getChild("version");
		Element e_limit=root.getChild("limit");
		if(e_limit==null)
//			这里后来加上受到默认值的影响
			limit=10;
		try
		{
			limit=Integer.parseInt(e_limit.getText());
		}catch(Exception e)
		{
			Log.e("读取限制数字有问题");
			limit=10;
		}
		if(num>limit)
			num=limit;
		int version;
		if(ver==null)
			version=1;
		else
			try
			{
				version=Integer.parseInt(ver.getText());
			}catch(NumberFormatException e)
			{
				return null;
			}
		if(version<2)
		{
			list.add(qqName+"抽到了：\n");
			List<Element> es=document.getRootElement().getChildren();
			ArrayList<String> ss=new ArrayList<String>();
			for(Element element:es)
				ss.add(element.getText());
			for(int i=0;i<num;i++)
				list.add(drawCard(ss)+"\n");
		}else
		{
			stack=0;
			Element start=root.getChild("start");
			Element main=root.getChild("main");
			Element end=root.getChild("end");
			if(start!=null)
				list.add(getText(root,start.getText()));
			if(main!=null)
				for(int i=0;i<num;i++)
					list.add(getText(root,main.getText()));
			if(end!=null)
				list.add(getText(root,end.getText()));
		}
		StringBuilder stringBuilder=new StringBuilder();
		for(String string:list)
			stringBuilder.append(string);
		return stringBuilder.toString().replaceAll("@name#",qqName)
				.replaceAll("@at#",factory.at(qq).toMessage().toString()).replaceAll("@me#","我");

	}

	public String getText(Element root,String text)
	{
		stack++;
		if(stack>30)
			return null;

		if(text==null)
			return "";

		Pattern pattern=Pattern.compile("\\{(.+?)\\}");
		Matcher matcher=pattern.matcher(text);
		while(matcher.find())
		{
			int start=matcher.start();
			int end=matcher.end();
			String[] part=matcher.group(1).split(",");
			Random random=new Random();
			String name=part[random.nextInt(part.length)];
			String aim=getText(root,getElementText(root,name));
			if(aim==null)
				aim="";
			StringBuilder builder=new StringBuilder(text);
			builder.replace(start,end,aim);
			text=builder.toString();
			matcher=pattern.matcher(text);
		}
		stack--;
		return text;
	}

	private String getElementText(Element root,String element)
	{
		Element e=root.getChild(element);
		if(e==null)
			return null;
		else
			return getElementText(e);
	}

	private String getElementText(Element element)
	{
		if(element.getChildren().size()==0)
			return element.getText();
		Element main=element.getChild("main");
		if(main!=null)
			return main.getText();
		else
		{
			List<Element> elements=element.getChildren();
			Random random=new Random();
			Element choose=elements.get(random.nextInt(elements.size()));
			return choose.getText();
		}
	}
}
