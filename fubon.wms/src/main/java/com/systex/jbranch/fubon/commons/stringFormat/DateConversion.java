package com.systex.jbranch.fubon.commons.stringFormat;

import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateConversion implements DateConversionTypeInf{	
	@SuppressWarnings("unchecked")
	@Override//預設
	public <T> T conversionType(String value) throws Exception{
		if(value == null){ 
			return null;
		}
		
		String dateFormat = "";
		String tempValue = "";
		Matcher matcher = Pattern.compile("\\d+").matcher(value);
		
		while(matcher.find())
			tempValue += matcher.group();
		
		for(String tmpFormat : new String[]{"yyyy" , "MM" , "dd" , "hh" , "mm" , "ss" , "SSS"}){
			if(dateFormat.length() >= tempValue.length())
				break;
			dateFormat += tmpFormat;
		}
		
		return (T) new SimpleDateFormat(dateFormat.trim()).parse(tempValue);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T conversionType(String value, String format) throws Exception {
		return (T)(value == null ? null : new SimpleDateFormat(format).parse(value));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T conversionType(String value, String format, Object defaultVal)throws Exception {
		Object result = conversionType(value, format);
		return (T)(result == null ? defaultVal : result);
	}
}
