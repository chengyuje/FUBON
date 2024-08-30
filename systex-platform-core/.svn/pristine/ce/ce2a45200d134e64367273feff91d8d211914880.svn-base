package com.systex.jbranch.platform.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParamsUtil {

	public static String[] getParamsName(String patternStr, String data){

		List<String> list = new ArrayList<String>();
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(data);
		boolean matchFound = matcher.find();
		while (matchFound) {
			list.add(matcher.group(1));
			if (matcher.end() + 1 <= data.length()) {
				matchFound = matcher.find(matcher.end());
			} else {
				break;
			}
		}
		return list.toArray(new String[0]);
	}
	
	public static String[] getParamsName(String data){
		return getParamsName("@\\{(.+?)\\}", data);
	}
	
	public static String replace(String text, String paramName, String value){
		if(value == null){
			return value;
		}
		
		return text.replace("@{" + paramName + "}", value);
	}

}
