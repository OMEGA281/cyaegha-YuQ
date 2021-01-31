package com.cyaegha.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Speedometer
{
	private SpeedometerType type;
	private Speed speed;

	/**
	 * 速度限制种类
	 * 
	 * @author GuoJiaCheng
	 *
	 */
	enum SpeedometerType
	{
		/**
		 * 速度限制，请求时会计算是否超过该速度
		 */
		AVERAGE_SPEED,
		/**
		 * 次数限制，请求时会检测在控制时间内的请求次数
		 */
		TIMES_LIMIT
	}

	enum TimeUnit
	{
		ms,s,min,h,day;

		public static TimeUnit getTimeUnit(String string)
		{
			for(TimeUnit timeUnit:TimeUnit.values())
			{
				if(timeUnit.name().equals(string))
					return timeUnit;
			}
			return null;
		}
	}

	public static class Speed
	{
		int count,time;
		TimeUnit unit;

		/**
		 * 将字符串识别为速度 时间单位支持ms，s，min，h，day 类似：3/s，50/3min，100/1h，5/day
		 * 
		 * @param string
		 */
		public static Speed transSpeed(String string)
		{
			string=string.toLowerCase();
			String[] ss=string.split("/");
			if(ss.length<2)
				return null;
			int count,time;
			TimeUnit unit;
			try
			{
				count=Integer.parseInt(ss[0]);
				Pattern pattern=Pattern.compile("(\\d*)(.+)");
				Matcher matcher=pattern.matcher(ss[1]);
				if(matcher.find())
				{
					String $time=matcher.group(1);
					if($time==null||$time.isEmpty())
						time=1;
					else
						time=Integer.parseInt($time);
					unit=TimeUnit.getTimeUnit(matcher.group(2));
					if(unit==null)
						return null;
					return new Speed(count,time,unit);
				}else
					return null;
			}catch(NumberFormatException e)
			{
				return null;
			}
		}

		public Speed(int count,int time,TimeUnit unit)
		{
			this.count=count;
			this.time=time;
			this.unit=unit;
		}
	}

	public Speedometer(Speed speed,SpeedometerType type)
	{
		this.type=type;
		this.speed=speed;
		switch(type)
		{
			case AVERAGE_SPEED:

				break;

			default:
				break;
		}
	}
}
