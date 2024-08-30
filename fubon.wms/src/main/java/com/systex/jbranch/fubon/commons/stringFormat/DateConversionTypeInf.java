package com.systex.jbranch.fubon.commons.stringFormat;

public interface DateConversionTypeInf {
	public <T> T conversionType(String value) throws Exception;
	public <T> T conversionType(String value , String format) throws Exception;
	public <T> T conversionType(String value, String format , Object defaultVal) throws Exception;
}
