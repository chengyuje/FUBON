package com.systex.jbranch.fubon.commons.stringFormat;

import java.math.BigDecimal;

import com.systex.jbranch.fubon.commons.esb.EsbUtil;

public class BigDecimalConversion implements NumberConversionTypeInf{
	@Override
	public <T> T conversionType(String value) throws Exception {
		return (T) new BigDecimal(value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T conversionType(String value, int point) throws Exception {
		return (T)new EsbUtil().decimalPoint(value , point);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T conversionType(String value, int point, Object defaultVal) throws Exception {
		Object result = conversionType(value , point);
		return (T)(result == null ? defaultVal : result);
	}
}
