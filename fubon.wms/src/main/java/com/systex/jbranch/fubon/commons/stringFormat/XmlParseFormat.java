package com.systex.jbranch.fubon.commons.stringFormat;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface XmlParseFormat {
	public static final String DEFAULT_CTYPE = "DEFAULT_CTYPE";
	public static final String DATE_CTYPE = "DATE_CTYPE";
	public static final String NUMBER_CTYPE = "NUMBER_CTYPE";
	public static final String SPECIAL_CTYPE = "SPACIAL_CTYPE";
	
	String value();
	
	//預設，字串、基本的Date、BigDecimal無特定格式化、Character取第一個字元
	ConversionType defaultConversion() default ConversionType.DEFAULT;
	
	//日期
	DateConversionType dateConversion() default DateConversionType.DATE;
	//日期格式例如yyyy/MM/dd、yyyy/MM/dd hh:mm:ss SSSS，解析的字串必須格式需要完全符合
	String dateFormat() default "";
	
	//數值，若傳入字串為整數格式，但實際有浮點數時，指定數值的第幾位為小數
	NumberConversionType numberConversion() default NumberConversionType.BIG_DECIMAL;
	int point() default 0;//小數
	
	//特殊轉換，需實作ScpecialConversionType，為上述基本格是無法處理時給予特定解析器，實作必須有無參數建構式
	@SuppressWarnings("rawtypes")
	Class specialConversion();
}
