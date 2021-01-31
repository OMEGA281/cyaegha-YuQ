package com.cyaegha.sysController.stringHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jdom2.JDOMException;

import com.cyaegha.sysController.SysPool;
import com.cyaegha.sysController.systemInitializationCore.SystemInit;
import com.cyaegha.tools.FileUtils;
import com.cyaegha.tools.XMLUtils;

import lombok.var;

public class StringLoader implements SystemInit
{
	private static final String $FILENAME_LOACLSTRINGMAPPING="LocalMapping.xml";
	
	private LocalStringMapping mapping;
	@Override
	public void init()
	{
		File stringFile=SysPool.getStringPathFile();
		
		ArrayList<StringList> list=new ArrayList<StringList>();
		
		
		//获取目录下所有的表
		File[] files=FileUtils.getChildrenFile(stringFile,(file)->file.isFile()&&file.getName().endsWith(".xml"));
		
		for(var file:files)
		{
			try
			{
				var document=XMLUtils.getDocument(file,false);
				var stringList=new StringList(document);
				list.add(stringList);
			}catch(JDOMException|IOException e)
			{
				e.printStackTrace();
			}
		}
		
		//检测是否存在映射表
		try
		{
			File localMapping=FileUtils.getChildFile(stringFile,$FILENAME_LOACLSTRINGMAPPING);
			mapping=new LocalStringMapping(XMLUtils.getDocument(localMapping,false));
		}catch(JDOMException | IOException e)
		{
//			未检测到映射表，启用默认表
			mapping=DefaultStringLoader.getDefaultStringMapping();
		}
		
		
	}

	@Override
	public int width()
	{
		return 50;
	}
	
}
