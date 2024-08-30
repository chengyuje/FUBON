package com.systex.jbranch.platform.common.util;

import java.util.List;

public class DBUtil {

	public static String getPreparedStatementString(List list){
		
		return getPreparedStatementString(list.toArray(new Object[list.size()]));
	}
	
	public static String getPreparedStatementString(Object[] arr){

		return getPreparedStatementString(arr, "?");
	}
	
	public static String getPreparedStatementString(Object[] arr, String pattern){
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < arr.length; i++) {
			result.append(pattern);
			if(i < arr.length -1){
				result.append(",");
			}
		}
		return result.toString();
	}
}
