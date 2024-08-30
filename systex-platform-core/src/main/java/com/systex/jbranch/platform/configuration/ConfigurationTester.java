package com.systex.jbranch.platform.configuration;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class ConfigurationTester
{
	public static void main(String...args) throws ConfigurationException
	{
		System.out.println("ApplicationContext init begin.");
		ApplicationContext context = new FileSystemXmlApplicationContext("C:\\jbranch\\config.bean.xml");
		
		System.out.println("ApplicationContext init end.");
		IConfigBrowser configBrowser=(IConfigBrowser)context.getBean("config.configBrowser");
		
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@");
		{
			Map filter=new HashMap();
			filter.put("IP", "10.23.0.64");
			filter.put("USERID", "Z00012395");
			
			Map configMap=configBrowser.getConfigMap(filter);
			print(configMap);	
		}
		
		{
			Map filter=new HashMap();
			filter.put("IP", "10.23.0.64");
			
			Map configMap=configBrowser.getConfigMap(filter);
			print(configMap);	
		}
		
		{
			Map filter=new HashMap();
			filter.put("USERID", "Z00012395");
			
			Map configMap=configBrowser.getConfigMap(filter);
			print(configMap);	
		}

		{
			Map filter=new HashMap();			
			Map configMap=configBrowser.getConfigMap(filter);
			print(configMap);	
		}
		
		
	}
	
	private static void print(Map map)
	{
		Iterator i=map.keySet().iterator();
		while(i.hasNext())
		{
			Object key=i.next();
			System.out.println(String.format("%s %s", key,map.get(key)));
		}
		System.out.println("-------------------------------");
	}
}