package com.systex.jbranch.fubon.commons.stringFormat;

public interface ConversionTypeInf {
	public <T> T conversionType(String value) throws Exception;
	
	@SuppressWarnings("rawtypes")
	public <T> T conversionType(String value , Class formatType) throws Exception;
	
	@SuppressWarnings("rawtypes")
	public <T> T conversionType(String value , Class formatType , Object defaultVal) throws Exception;
}
