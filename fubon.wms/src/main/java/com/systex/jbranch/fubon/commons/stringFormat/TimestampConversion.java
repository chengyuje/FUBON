package com.systex.jbranch.fubon.commons.stringFormat;

import java.sql.Timestamp;
import java.util.Date;

public class TimestampConversion extends DateConversion{
	@SuppressWarnings("unchecked")
	public <T> T conversionType(String value) throws Exception {
		Date date = super.conversionType(value);
		return (T)(date == null ? date :new Timestamp(date.getTime()));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T conversionType(String value , String formatType) throws Exception {
		Date date = super.conversionType(value , formatType);
		return (T)(date == null ? date :new Timestamp(date.getTime()));
	}
	
	@SuppressWarnings("unchecked" )
	public <T> T conversionType(String value , String formatType , Object defaultVal) throws Exception {
		Date date = super.conversionType(value , formatType);
		return (T) (value == null ? defaultVal : new Timestamp(date.getTime()));
	}
}
