package com.cyaegha.sysController.systemInitializationCore;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.IceCreamQAQ.Yu.annotation.LoadBy;

/**
 * 在类上标记该注解用于系统初始化，其执行顺序先于登录<br>
 * 标记了该注解的类内部必须实现{@link SystemInit}
 * @author GuoJiaCheng
 *
 */
@Inherited
@Documented
@Retention(RUNTIME)
@Target(TYPE)
@LoadBy(SysClassLoader.class)
public @interface SystemCalss
{
}
