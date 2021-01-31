package com.cyaegha.sysController.systemInitializationCore;

@SystemCalss
public interface SystemInit
{
	void init();
	/**
	 * 权重，数值从0~100，越高越先加载
	 * @return
	 */
	int width();
}
