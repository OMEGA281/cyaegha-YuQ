package com.cyaegha.sysController;

import java.io.File;
import java.util.Map;

import com.IceCreamQAQ.Yu.annotation.Config;
import com.cyaegha.sysController.systemInitializationCore.SystemInit;

/**
 * 完整性检查
 * @author GuoJiaCheng
 *
 */
public class IntegrityCheck implements SystemInit
{
	@Config("cyaegha.path")
	private Map<String,String> path;
	
	@Override
	public void init()
	{
		for(String string:path.values())
		{
			File file=new File(string);
			if(!file.exists())
				file.mkdir();
		}
	}

	@Override
	public int width()
	{
		return 101;
	}

}
