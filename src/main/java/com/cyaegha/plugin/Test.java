package com.cyaegha.plugin;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.inject.Inject;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.util.Web;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.message.Message;
import com.icecreamqaq.yuq.message.MessageItemFactory;

import okhttp3.OkHttpClient;

@GroupController
public class Test
{
	private static final String station="http://pixiv.cat/";

	private int maxLength=3500;
	OkHttpClient okHttpClient=new OkHttpClient();

	@Inject
	MessageItemFactory factory;

	@Inject
	Web web;
	
	@Action("pixiv {num}")
	public Object test(long num,Group group)
	{
		group.sendMessage(new Message().plus("检索中……请耐心等待"));
		URL url;
		try
		{
			url=new URL(station+num+".jpg");
		}catch(MalformedURLException e)
		{
			return "连接错误";
		}
		BufferedImage bufferedImage;
		try
		{
			bufferedImage=getImage(url);
		}catch(IOException e)
		{
			return "获取失败";
		}
		if(bufferedImage==null)
			return "获取失败";
		bufferedImage=dealImage(bufferedImage);

		return factory.imageByBufferedImage(bufferedImage).plus(url.toString());
	}

	@Action("pixiv {num} {list}")
	public Object test(long num,long list,Group group)
	{
		group.sendMessage(new Message().plus("检索中……请耐心等待"));
		URL url;
		try
		{
			url=new URL(station+num+"-"+list+".jpg");
		}catch(MalformedURLException e)
		{
			return "连接错误";
		}
		BufferedImage bufferedImage;
		try
		{
			bufferedImage=getImage(url);
		}catch(IOException e)
		{
			return "获取失败";
		}
		bufferedImage=dealImage(bufferedImage);

		return factory.imageByBufferedImage(bufferedImage).plus(url.toString());
	}

	private BufferedImage getImage(URL url) throws IOException
	{
		InputStream inputStream=web.download(url.toString());
		BufferedImage bufferedImage=ImageIO.read(inputStream);
		inputStream.close();
		return bufferedImage;
	}

	private BufferedImage dealImage(BufferedImage image)
	{
		int srcHeight=image.getHeight();
		int srcWidth=image.getWidth();

		double rate=1;

		{
			int max=srcHeight>srcWidth? srcHeight:srcWidth;
			if(max>maxLength)
				rate=3500/maxLength;
		}

		if(rate==1)
			return image;

		BufferedImage newImage=new BufferedImage((int) (srcWidth*rate),(int) (srcHeight*rate),
				BufferedImage.TYPE_INT_RGB);
		newImage.getGraphics().drawImage(
				image.getScaledInstance((int) (srcWidth*rate),(int) (srcHeight*rate),Image.SCALE_SMOOTH),0,0,null);
		return newImage;
	}
}
