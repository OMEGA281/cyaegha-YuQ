package com.cyaegha.sysController.messageHelper;

import java.util.ArrayList;

import javax.inject.Inject;

import com.IceCreamQAQ.Yu.annotation.Event;
import com.IceCreamQAQ.Yu.annotation.Event.Weight;
import com.IceCreamQAQ.Yu.annotation.EventListener;
import com.cyaegha.sysController.SystemInfo;
import com.icecreamqaq.yuq.FunKt;
import com.icecreamqaq.yuq.event.GroupMessageEvent;
import com.icecreamqaq.yuq.message.At;
import com.icecreamqaq.yuq.message.MessageItem;
import com.icecreamqaq.yuq.message.MessageItemFactory;
import com.icecreamqaq.yuq.message.Text;

@EventListener
public class GroupMessagePointDealer
{
	@Inject
	private MessageItemFactory factory;

	@Event(weight=Weight.high)
	public void test(GroupMessageEvent event)
	{
		System.out.println(event);
	}

//	@Event(weight=Weight.high)
	public void deal(GroupMessageEvent event)
	{
		ArrayList<Long> list=new ArrayList<>();
		ArrayList<MessageItem> arrayList=event.getMessage().getBody();
		int itemLocation=0,pathNum=0;
		for(MessageItem messageItem=arrayList.get(itemLocation);itemLocation<arrayList.size();itemLocation++)
		{
			messageItem=arrayList.get(itemLocation);
			if(messageItem instanceof At)
			{
				At at=(At) messageItem;
				list.add(at.getUser());
				pathNum++;
			}else if(messageItem instanceof Text)
			{
				Text text=(Text) messageItem;
				if(text.getText().trim().equals(""))
					continue;
				else
					break;
			}else
				break;
		}
		boolean shouldRespone=false;
		if(list.size()==0)
			shouldRespone=true;
		else
			for(Long long1:list)
			{
				if(shouldRespone==true)
					break;
				if(long1==-1)
					shouldRespone=true;
				else if(long1==SystemInfo.getBotQQ())
					shouldRespone=true;
			}
		if(!shouldRespone)
		{
			event.setCancel(true);
			return;
		}

		for(int i=0;i<itemLocation;i++)
			arrayList.remove(0);
		for(int i=0;i<pathNum;i++)
			event.getMessage().path.remove(0);
		MessageItem firstItem=arrayList.get(0);
		if(firstItem==null)
		{
			event.setCancel(true);
			return;
		}
		if(firstItem instanceof Text)
		{
			Text text=(Text) firstItem;
			String string=text.getText();
			if(!string.trim().equals(""))
			{
				if(string.charAt(0)==' ')
				{
					Text text2=factory.text(string.substring(1));
					arrayList.remove(0);
					arrayList.add(0,text2);
					return;
				}else
					return;
			}else
				return;
		}
	}
}
