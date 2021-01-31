package com.cyaegha.sysController.stringHelper;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * 这个表用来记录每个类下存在的字符表
 * @author GuoJiaCheng
 *
 */
@Getter
public class StringMap
{
	private Class<?> clazz;
	private List<StringList> list;
	@Setter
	private StringList defaultStringList;
	
	protected StringMap(Class<?> clazz)
	{
		this.clazz=clazz;
		list=new ArrayList<StringList>();
		
	}
	
	protected boolean addStringList(StringList list)
	{
		return this.list.add(list);
	}
	
	protected boolean removeStringList(StringList list)
	{
		return this.list.remove(list);
	}
}
