package com.systex.jbranch.fubon.commons.stringFormat;


public enum NumberConversionType {
	BIG_DECIMAL(BigDecimalConversion.class);
	
	@SuppressWarnings("rawtypes")
	private Class cls;
	
	@SuppressWarnings("rawtypes")
	private NumberConversionType(Class cls){
		if(!NumberConversionTypeInf.class.isAssignableFrom(cls))
			throw new RuntimeException(this.name() + " 's argument is error type in the contreuctor");
		else if(cls.isInterface())
			throw new RuntimeException(this.name() + " 's argument can not is interface");
		this.cls = cls;
	}
	
	public <T> T conversionType(String value) throws Exception{
		return getParseInstance().conversionType(value);
	}
	
	public <T> T conversionType(String value , int point) throws Exception{
		return getParseInstance().conversionType(value , point);
	}

	public <T> T conversionType(String value , int point , Object defaultVal) throws Exception{
		return getParseInstance().conversionType(value , point , defaultVal);
	}
	
	public NumberConversionTypeInf getParseInstance() throws InstantiationException, IllegalAccessException{
		return (NumberConversionTypeInf) cls.newInstance();
	}
}
