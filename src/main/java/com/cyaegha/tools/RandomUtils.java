package com.cyaegha.tools;

public class RandomUtils
{
	private static String random(String str,int length)
	{
		StringBuilder result=new StringBuilder();
		for(int i=0;i<length;i++)
		{
			int at=(int) (Math.random()*str.length());
			result.append(str.charAt(at));
		}
		return result.toString();
	}

	public static String randomStr(int length)
	{
		return random("1234567890abcdefghijklmnopqrstuvwxyz",length);
	}
}
