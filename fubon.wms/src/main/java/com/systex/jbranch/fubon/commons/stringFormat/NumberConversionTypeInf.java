package com.systex.jbranch.fubon.commons.stringFormat;

public interface NumberConversionTypeInf{
	public <T> T conversionType(String value) throws Exception;
	public <T> T conversionType(String value , int point) throws Exception;
	public <T> T conversionType(String value, int point , Object defaultVal) throws Exception;
}
