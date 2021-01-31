package com.cyaegha.pluginHelper;

import java.io.File;
import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.output.XMLOutputter;

import com.cyaegha.surveillance.Log;
import com.cyaegha.tools.XMLUtils;

public class DoubleAuthirizerList
{
	private Document document;
	private Element root;
	private File file;

	private static final String BLACK="BLACK";
	private static final String WHITE="WHITE";

	public DoubleAuthirizerList(String listPath)
	{
		file=new File(listPath);
		try
		{
			document=XMLUtils.getDocument(file,true);
		}catch(JDOMException|IOException e)
		{
			Log.e("无法读取或创建权限表"+e);
			return;
		}
		root=document.getRootElement();
	}

	protected boolean isWhite(long groupNum,long userNum)
	{
		return getAuthirizer(groupNum,userNum)>0;
	}

	protected boolean isBlack(long groupNum,long userNum)
	{
		return getAuthirizer(groupNum,userNum)<0;
	}

	/**
	 * 获得权限
	 * 
	 * @param groupNum 群号
	 * @param userNum  QQ号
	 * @return -1 黑名单<b>0 无记录<b>1 白名单
	 */
	protected int getAuthirizer(long groupNum,long userNum)
	{
		return getStatus(getElement(groupNum,userNum));
	}

	private Element getElement(long groupNum,long userNum)
	{
		boolean write;
		Element group=root.getChild("G"+groupNum);
		if(group==null)
		{
			group=new Element("G"+groupNum);
			root.addContent(group);
			write=true;
		}
		Element user=group.getChild("P"+userNum);
		if(user==null)
		{
			user=new Element("P"+userNum);
			group.addContent(group);
			write=true;
		}
		return user;
	}

	private int getStatus(Element element)
	{
		if(element==null)
			return 0;
		switch(element.getText().toUpperCase())
		{
			case WHITE:
				return 1;
			case BLACK:
				return -1;
			default:
				return -1000;
		}
	}
//	private boolean write()
//	{
//		XMLOutputter outputter=new 
//	}
}
