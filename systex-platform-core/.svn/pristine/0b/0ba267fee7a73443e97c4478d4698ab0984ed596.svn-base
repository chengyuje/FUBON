package com.systex.jbranch.platform.common.security.impl;

import java.util.Hashtable;
import java.util.Map;

public final class Parameters {
	private static Map<String, byte[]> params = new Hashtable<String, byte[]>();
	
	final static byte[] getParameter(String key){
		return params.get(key);
	}
	
	final static void setParameter(String key, byte[] value){
		params.put(key, value);
	}
}
