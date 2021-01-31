package com.cyaegha.sysController.stringHelper;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;

import com.cyaegha.surveillance.Log;
import com.cyaegha.tools.XMLUtils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.var;

class LocalStringMapping
{
	private static final String $ATTR="class";
	private static final String $ELEMENT_NAME="element";
	private static final String $ROOTELEMENT="root";

	private Document document;

	@Getter(value=AccessLevel.PROTECTED)
	private Map<Class<?>,String> map;

	LocalStringMapping(@NonNull Document document)
	{
		this.document=document;
		map=new HashMap<Class<?>,String>();
		Element root=document.getRootElement();
		var elements=root.getChildren();
		elements.forEach((e)->
		{
			String className=e.getAttributeValue($ATTR);
			if(className!=null)
			{
				String listName=e.getText();
				try
				{
					map.put(Class.forName(className),listName);
				}catch(ClassNotFoundException e1)
				{
					Log.e("加载本地设置表时出现了不存在的类！");
				}
			}
		});
	}
	
	public LocalStringMapping(Map<Class<?>,String> listName)
	{
		Element root=new Element($ROOTELEMENT);
		this.document=new Document(root);
		map=listName;
	}

	/**
	 * 改变映射
	 * 
	 * @param clazz    类名
	 * @param listName 文件名
	 * @return 如果是原来不存在的类则会返回true，否则则是false
	 */
	protected boolean changeMapping(Class<?> clazz,String listName)
	{
		var b=map.put(clazz,listName)==null? true:false;

		Element root=document.getRootElement();
		root.removeChildren($ELEMENT_NAME);
		for(var e:map.entrySet())
		{
			root.addContent(new Element($ELEMENT_NAME).setAttribute($ATTR,e.getKey().getName()).setText(e.getValue()));
		}
		return b;
	}

	protected boolean saveDocument(@NonNull File file)
	{
		return XMLUtils.writeDocument(document,file);
	}
}
