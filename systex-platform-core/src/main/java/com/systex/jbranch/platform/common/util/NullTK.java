package com.systex.jbranch.platform.common.util;

public class NullTK {
	private NullTK(){
    }

    public static boolean isNull(Object obj){
        return obj == null;
    }

    public static Object checkNull(Object obj, Object defaultValue){
        if(isNull(obj))
            return defaultValue;
        else
            return obj;
    }

    public static String checkNull(String str, String defaultValue){
        if(isNull(str))
            return defaultValue;
        else
            return str;
    }

    public static void checkNull(Object obj, Exception ex)
        throws Exception{
        if(isNull(obj))
            throw ex;
        else
            return;
    }

    public static void checkNull(Object obj, RuntimeException ex){
        if(isNull(obj))
            throw ex;
        else
            return;
    }
}
