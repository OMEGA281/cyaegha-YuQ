package com.cyaegha.plugin;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Event;
import com.IceCreamQAQ.Yu.annotation.EventListener;
import com.IceCreamQAQ.Yu.event.events.AppStartEvent;
import com.cyaegha.Permission;
import com.cyaegha.pluginHelper.AuthirizerUser;
import com.cyaegha.pluginHelper.annotations.MinimumAuthority;
import com.cyaegha.pluginHelper.annotations.RegistCommand;
import com.cyaegha.pluginHelper.dataExchanger.DataExchanger;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.event.GroupMemberJoinEvent;

@EventListener
@GroupController
public class Welcome implements Permission
{
	DataExchanger exchanger;

	@Event
	public void init(AppStartEvent event)
	{
		exchanger=DataExchanger.getDataExchanger(getClass());
	}

	@Event
	public Object welcomeSender(GroupMemberJoinEvent event)
	{
		return exchanger.getItem("G"+event.getGroup());
	}

	@MinimumAuthority(AuthirizerUser.GROUP_MANAGER)
	@RegistCommand(CommandString="删除群成员入群欢迎词")
	@Action("{pt:\\.。}welcome{sp:\b*?}{string}")
	public String welcome(String string,Member member)
	{
		if(member.getPermission()>=GROUP_ADMIN)
		{
			if(string==null||string.isEmpty())
			{
				exchanger.deleteItem("G"+member.getGroup());
				return "已删除本群的入群欢迎";
			}else
			{
				exchanger.setItem("G"+member.getGroup(),string);
				return "修改了本群的入群欢迎";
			}
		}else
			return "您的权限不足";
	}
}
