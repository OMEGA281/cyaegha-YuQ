package com.cyaegha.plugin;

import java.io.IOException;

import javax.inject.Inject;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Before;
import com.IceCreamQAQ.Yu.annotation.Event;
import com.IceCreamQAQ.Yu.annotation.EventListener;
import com.IceCreamQAQ.Yu.event.events.AppStartEvent;
import com.cyaegha.Permission;
import com.cyaegha.pluginHelper.AuthirizerUser;
import com.cyaegha.pluginHelper.annotations.MinimumAuthority;
import com.cyaegha.pluginHelper.annotations.RegistCommand;
import com.cyaegha.pluginHelper.dataExchanger.DataExchanger;
import com.cyaegha.sysController.messageHelper.Pattern;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.PrivateController;
import com.icecreamqaq.yuq.entity.Contact;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.message.Image;
import com.icecreamqaq.yuq.message.Message;
import com.icecreamqaq.yuq.message.MessageItemFactory;

@EventListener
@GroupController
@PrivateController
public class ShowImage
{

	@Inject
	private MessageItemFactory factory;

	private DataExchanger exchanger;

	@Event
	public void init(AppStartEvent event)
	{
		exchanger=DataExchanger.getDataExchanger(getClass());
	}

	@RegistCommand(CommandString="image",Help="抽一张图片")
	@Action(".image")
	@Pattern(pattern="[\\.。]image",format=".image")
	public Object image(Message message,Contact qq) throws IOException
	{
		String mark;
		if(qq instanceof Member)
			mark="G"+Member.class.cast(qq).getGroup().getId();
		else
			mark="P"+qq.getId();
		boolean access=Boolean
				.parseBoolean(exchanger.getItem(mark));
		if(!access)
			return "现在不行，还没有接到许可的命令";
//		http://www.dmoe.cc/random.php
//		URL url=new URL("https://v1.alapi.cn/api/acg");
//		HttpURLConnection connection=(HttpURLConnection)url.openConnection();
//		connection.setRequestMethod("GET");
//		connection.setConnectTimeout(15000);
//		connection.setReadTimeout(60000);
//		connection.connect();
//		String result;
//		if (connection.getResponseCode() == 200) 
//		{
//            InputStream is = connection.getInputStream();
//            // 封装输入流is，并指定字符集
//            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
//            // 存放数据
//            StringBuffer sbf = new StringBuffer();
//            String temp = null;
//            while ((temp = br.readLine()) != null) {
//                sbf.append(temp);
//                sbf.append("\r\n");
//            }
//            result = sbf.toString();
//            br.close();
//            is.close();
//        }
//		connection.disconnect();

		Image image=factory.imageByUrl("https://v1.alapi.cn/api/acg");
		return image;
	}

	@Before(only={"image_on","image_off"})
	public void imageSwitchAuthority(Message message,Contact qq)
	{
		if(qq instanceof Member)
		{
			int i=Member.class.cast(qq).getPermission();
			if(i<Permission.GROUP_ADMIN)
				throw factory.text("您的权限不足").toMessage().toThrowable();
		}
	}

	@MinimumAuthority(AuthirizerUser.GROUP_MANAGER)
	@Action(".image {flag}")
	@Pattern(pattern="[\\.。]image\\s*(.+?)",format=".image %s")
	public String image_switch(boolean flag,Message message,Contact qq)
	{
		String mark;
		if(qq instanceof Member)
			mark="G"+Member.class.cast(qq).getGroup().getId();
		else
			mark="P"+qq.getId();
		boolean access=Boolean.parseBoolean(exchanger.getItem(mark));
		if(access==flag)
			return flag? "已经打开开关了":"已经关闭开关了";
		else
		{
			exchanger.setItem(mark,String.valueOf(flag));
			return flag? "打开开关了":"关闭开关了";
		}
	}
}
