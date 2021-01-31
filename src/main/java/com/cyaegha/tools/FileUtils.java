package com.cyaegha.tools;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;

import lombok.NonNull;

public class FileUtils
{
	/**
	 * 创建File
	 * 
	 * @param parent     父文件
	 * @param name       子文件名
	 * @param fileFilter 子文件后缀名
	 * @return
	 */
	public static File newFile(@NonNull File parent,@NonNull String name,@NonNull String fileFilter)
	{
		return new File(parent.getPath()+"/"+name+"."+fileFilter);
	}

	/**
	 * 创建File
	 * 
	 * @param parent 父文件
	 * @param name   子文件名
	 * @return
	 */
	public static File newFile(@NonNull File parent,@NonNull String name)
	{
		return new File(parent.getPath()+"/"+name);
	}

	/**
	 * 通过父文件夹获得子文件
	 * @param parent 父文件
	 * @param childName 子文件名
	 * @return
	 * @throws FileNotFoundException 子文件不存在
	 */
	public static File getChildFile(@NonNull File parent,@NonNull String childName) throws FileNotFoundException
	{
		File result=new File(parent.getPath()+"\\"+childName);
		if(!result.exists())
			throw new FileNotFoundException();
		return result;
	}

	/**
	 * 获取子文件
	 * 
	 * @param parent 父文件夹
	 * @return
	 */
	public static File[] getChildrenFile(@NonNull File parent)
	{
		return parent.listFiles();
	}

	/**
	 * 获取子文件
	 * 
	 * @param parent     父文件夹
	 * @param fileFilter 过滤器
	 * @return
	 */
	public static File[] getChildrenFile(@NonNull File parent,@NonNull FileFilter fileFilter)
	{
		return parent.listFiles(fileFilter);
	}
}
