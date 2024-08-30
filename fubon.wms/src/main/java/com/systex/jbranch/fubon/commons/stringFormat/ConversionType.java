package com.systex.jbranch.fubon.commons.stringFormat;


public enum ConversionType {
	DEFAULT(DefaultConversionType.class);
	
	@SuppressWarnings("rawtypes")
	private Class cls;
	
	@SuppressWarnings("rawtypes")
	private ConversionType(Class cls){
		if(!ConversionTypeInf.class.isAssignableFrom(cls)){
			throw new RuntimeException(this.name() + " 's argument is error type in the contreuctor");
		}
		else if(cls.isInterface()){
			throw new RuntimeException(this.name() + " 's argument can not is interface");
		}
		
		this.cls = cls;
	}
		
	public <T> T conversionType(String value) throws Exception{
		ConversionTypeInf conversionType = (ConversionTypeInf)cls.newInstance();
		return conversionType.conversionType(value);
	}

	@SuppressWarnings({ "rawtypes" })
	public <T> T conversionType(String value , Class formatType) throws Exception{
		ConversionTypeInf conversionType = (ConversionTypeInf)cls.newInstance();
		return conversionType.conversionType(value , formatType);
	}
}
