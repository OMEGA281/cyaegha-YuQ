package com.cyaegha.sysController.stringHelper;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.IceCreamQAQ.Yu.annotation.LoadBy;

/**
 * 表明你要连接的字符串资源，这些资源必须存在于同目录下的资源文件夹
 * @author GuoJiaCheng
 *
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
@LoadBy(DefaultStringLoader.class)
public @interface LinkStringXML
{
	/**
	 * 连接的资源字符串文件
	 * @return
	 */
	String[] value();
	String defaultString() default "";
}
