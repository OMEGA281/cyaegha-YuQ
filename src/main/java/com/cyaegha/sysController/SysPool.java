package com.cyaegha.sysController;
import java.io.File;
import java.util.Set;

import com.IceCreamQAQ.Yu.annotation.Config;
import com.cyaegha.sysController.systemInitializationCore.SystemInit;
import com.cyaegha.tools.ClassUtils;

import lombok.Getter;

public class SysPool implements SystemInit
{
	@Config("cyaegha.path.data")
	private String dataPath;
	@Config("cyaegha.path.authirizer")
	private String authirizerPath;
	@Config("cyaegha.path.string")
	private String stringPath;
	@Config("cyaegha.path.tmp")
	private String tmpPath;
	@Config("cyaegha.pluginPackages")
	private String pluginPackage;
	
	@Getter
	private static File dataPathFile;
	@Getter
	private static File authirizerPathFile;
	@Getter
	private static File stringPathFile;
	@Getter
	private static File tmpPathFile;
	@Getter
	private static Set<String> plugins;
	
	@Override
	public void init()
	{
		dataPathFile=new File(dataPath);
		authirizerPathFile=new File(authirizerPath);
		stringPathFile=new File(stringPath);
		tmpPathFile=new File(tmpPath);
		
		plugins=ClassUtils.getClassName(pluginPackage,false);
	}
	
	@Override
	public int width()
	{
		return 90;
	}
	
}
