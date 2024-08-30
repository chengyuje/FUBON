package com.systex.jbranch.platform.common.util;

import java.lang.reflect.*;
import java.text.*;
import java.util.*;

public class ReflectionUtil {
	
	public static Map<String, Object> valueObjectToMap(Object vo) throws SecurityException, 
					IllegalArgumentException, IllegalAccessException, InvocationTargetException{

		Class clazz = vo.getClass();
		Method[] methods = clazz.getMethods();
		Map<String, Object> voMap = new HashMap<String, Object>();
		for(int i=0; i<methods.length; i++){
			if(methods[i].getName().startsWith("get") && !methods[i].getName().equals("getClass")){
				String properity = methods[i].getName().substring(3).toLowerCase();
				Object value = methods[i].invoke(vo, null);
				voMap.put(properity, value);
			}
		}
		return voMap;
	}
	
	public static Object executeMethod(Object target, String methodName, List args)throws SecurityException, 
					IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException{	

		Class clazz = target.getClass();
		Class[] methodArgs = null;
		Object[] methodArgObjs = null;
		Method method = null;
		if(args != null){
			methodArgs = new Class[args.size()];
			methodArgObjs = new Object[args.size()];
			for(int i=0; i<args.size(); i++){
				methodArgs[i] = args.get(i).getClass();
				methodArgObjs[i] = args.get(i);
			}	
		}
		
		method = clazz.getMethod(methodName, methodArgs);
		return method.invoke(target, methodArgObjs);
	}
}






