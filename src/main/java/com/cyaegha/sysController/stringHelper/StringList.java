package com.cyaegha.sysController.stringHelper;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.jdom2.Document;

import com.cyaegha.tools.XMLUtils;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.var;


/**
 * 这个类对应着字符串文件记录表，相当于一个实体类
 * @author GuoJiaCheng
 *
 */
@Getter
@ToString
public class StringList
{
	private static final String $PLUGIN_NAME="plugin";
	private static final String $FILE_NAME="name";
	private static final String $AUTHER="auther";
	private static final String $HELP="help";
	private static final String $STRING="string";
	private static final String $ATTR="id";
	
	private Document document;
	
	private String auther;
	private String help;
	private String pluginName;
	private String fileName;
	private Map<String,String> map;
	
	protected StringList(@NonNull Document document) throws IllegalArgumentException
	{
		this.document=document;
		var root=document.getRootElement();
		pluginName=root.getChildText($PLUGIN_NAME);
		fileName=root.getChildText($FILE_NAME);
		if(pluginName==null||fileName==null)
			throw new IllegalArgumentException("字符串文件不合格！");
		auther=root.getChildText($AUTHER);
		help=root.getChildText($HELP);
		map=new HashMap<String,String>();
		var elements=root.getChildren($STRING);
		elements.forEach((entity)->{
			var id=entity.getAttributeValue($ATTR);
			if(id!=null)
			{
				var text=entity.getText();
				map.put(id,text);
			}
		});
	}
	protected String getText(@NonNull String id)
	{
		return map.get(id);
	}
	protected boolean writeXML(@NonNull File file)
	{
		return XMLUtils.writeDocument(document,file);
	}
	//FIXME:这里之后加上set的方法
}
