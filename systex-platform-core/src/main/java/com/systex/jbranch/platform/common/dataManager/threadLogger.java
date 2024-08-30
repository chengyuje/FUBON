package com.systex.jbranch.platform.common.dataManager;
import java.lang.System;

@Deprecated
public class threadLogger {
	public static void println(String strData) 
	{
		String threadName = Thread.currentThread().getName();
		System.out.println("[" + threadName + "] " + strData);
	}
}
