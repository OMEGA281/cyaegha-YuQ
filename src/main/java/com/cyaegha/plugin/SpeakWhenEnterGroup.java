package com.cyaegha.plugin;

import javax.inject.Inject;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Event;
import com.IceCreamQAQ.Yu.annotation.EventListener;
import com.IceCreamQAQ.Yu.event.events.AppStartEvent;
import com.IceCreamQAQ.Yu.job.JobManager;
import com.cyaegha.pluginHelper.annotations.RegistCommand;
import com.cyaegha.pluginHelper.dataExchanger.DataExchanger;
import com.cyaegha.sysController.messageHelper.Pattern;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.PrivateController;
import com.icecreamqaq.yuq.event.BotJoinGroupEvent;
import com.icecreamqaq.yuq.message.Message;

@EventListener
@PrivateController
@GroupController
public class SpeakWhenEnterGroup
{
	public static final String TITLE="TITLE";
	public static final String SWITCH="SWITCH";

	private DataExchanger exchanger;

	@Inject
	private JobManager job;

	@Event
	public void init(AppStartEvent event)
	{
		exchanger=DataExchanger.getDataExchanger(getClass());
	}

	@Event
	public void speak(BotJoinGroupEvent event)
	{
		if(Boolean.parseBoolean(exchanger.getItem(SWITCH)))
			job.registerTimer(()-> event.getGroup().sendMessage((new Message().plus(exchanger.getItem(TITLE)))),2000);
	}

	@RegistCommand(CommandString="group enter speak",Help="删除入群广播")
	@Pattern(pattern="[\\.。]enter\\s*(.*)",format=".enter %s")
	@Action(".enter {string}")
	public String enterSpeak(String string,long qq)
	{
		if(/* NormalAuthirizer.getPermission(qq)>=Permission.OP */true)
			if(string.toLowerCase().equals("on"))
			{
				exchanger.setItem(SWITCH,String.valueOf(true));
				return "开启入群广播"+exchanger.getItem(TITLE)==null||exchanger.getItem(TITLE).isEmpty()? "但是广播内容为空":"";
			}else if(string.toLowerCase().equals("off"))
			{
				exchanger.setItem(SWITCH,String.valueOf(true));
				return "关闭入群广播";
			}else
			{
				exchanger.setItem(TITLE,string);
				return "已修改入群广播";
			}
		else
			return "你没有权限修改";
	}
}
