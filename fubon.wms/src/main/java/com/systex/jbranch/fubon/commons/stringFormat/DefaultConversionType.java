package com.systex.jbranch.fubon.commons.stringFormat;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultConversionType implements ConversionTypeInf{
	
	@Override
	public <T> T conversionType(String value) throws Exception{
		return conversionType(value , String.class , null);
	}

	@SuppressWarnings({ "rawtypes" })
	@Override
	public <T> T conversionType(String value , Class formatType) throws Exception {
		return conversionType(value , formatType , null);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T conversionType(String value , Class formatType , Object defaultVal) throws Exception {
		Object result = null;
		
		if(BigDecimal.class.equals(formatType))
			result = NumberConversionType.BIG_DECIMAL.getParseInstance().conversionType(value);
		else if(Character.class.equals(formatType))
			result = value == null ? defaultVal : value.charAt(0);
		else if(Date.class.equals(formatType))
			result = DateConversionType.DATE.getParseInstance().conversionType(value);
		else if(Timestamp.class.equals(formatType))
			result = DateConversionType.TIMESTAMP.getParseInstance().conversionType(value);
		else
			result = value == null ? defaultVal : value;
		
		return (T) result;
	}
}
