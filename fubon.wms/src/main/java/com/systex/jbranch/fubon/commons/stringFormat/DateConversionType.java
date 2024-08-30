package com.systex.jbranch.fubon.commons.stringFormat;


public enum DateConversionType {
	DATE(DateConversion.class), 	
	TIMESTAMP(TimestampConversion.class);
	
	@SuppressWarnings("rawtypes")
	private Class cls;
	
	@SuppressWarnings("rawtypes")
	private DateConversionType(Class cls){
		if(!DateConversionTypeInf.class.isAssignableFrom(cls))
			throw new RuntimeException(this.name() + " 's argument is error type in the contreuctor");
		else if(cls.isInterface())
			throw new RuntimeException(this.name() + " 's argument can not is interface");
		this.cls = cls;
	}	

	public <T> T conversionType(String value) throws Exception{
		return getParseInstance().conversionType(value);
	}
	
	public <T> T conversionType(String value , String format) throws Exception{
		return getParseInstance().conversionType(value , format);
	}

	public <T> T conversionType(String value , String format , Object defaultVal) throws Exception{
		return getParseInstance().conversionType(value , format , defaultVal);
	}
	
	public DateConversionTypeInf getParseInstance() throws InstantiationException, IllegalAccessException{
		return (DateConversionTypeInf) cls.newInstance();
	}
}
