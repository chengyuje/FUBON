package com.systex.jbranch.platform.configuration;

import java.util.Map;

public interface IConfigBrowser 
{
	Map getConfigMap(Map<String,String> filter) throws ConfigurationException;
}
