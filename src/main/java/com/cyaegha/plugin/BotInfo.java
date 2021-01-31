package com.cyaegha.plugin;

import java.io.IOException;
import java.io.InputStreamReader;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Synonym;
import com.cyaegha.pluginHelper.annotations.RegistCommand;
import com.cyaegha.surveillance.Log;
import com.cyaegha.tools.GetJarResources;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.PrivateController;

@GroupController
@PrivateController
public class BotInfo
{
	@RegistCommand(CommandString="bot",Help="显示信息")
	@Action("。bot")
	@Synonym(".bot")
	public String bot()
	{
		StringBuilder string=new StringBuilder();
		try
		{
			InputStreamReader inputStream=new InputStreamReader(new GetJarResources("BotInfo").getJarResources(),
					"UTF-8");
			int by=0;
			while((by=inputStream.read())!=-1)
			{
				string.append(String.valueOf((char) by));
			}
		}catch(IOException e)
		{
			Log.e("无法读取描述文件");
		}

		if(string.length()==0)
			return null;
		return string.toString();
	}
}
