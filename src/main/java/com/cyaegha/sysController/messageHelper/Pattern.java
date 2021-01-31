package com.cyaegha.sysController.messageHelper;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.IceCreamQAQ.Yu.annotation.LoadBy;

/**
 * 针对骰娘的特化型工具<br>
 * 标记本注解之后，在受到消息后会自动处理为适合yuq识别的命令<br>
 * 
 * @author GuoJiaCheng
 *
 */
@Documented
@Retention(RUNTIME)
@Target(ElementType.METHOD)
public @interface Pattern
{
	/**
	 * 能够匹配的正则
	 * 
	 * @return
	 */
	public String pattern();

	/**
	 * 替换的字符串
	 * 
	 * @return
	 */
	public String format();

	public int length() default -1000;
}
