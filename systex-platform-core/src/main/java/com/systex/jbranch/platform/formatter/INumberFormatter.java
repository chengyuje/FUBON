package com.systex.jbranch.platform.formatter;

import java.math.BigDecimal;


public interface INumberFormatter
{	
	public String format(String value);
	
	public String format(Number value);
	
	public BigDecimal parse(String value)throws ParseException;

	public void setFractionDigits(int fractionDigits);

	public int getFractionDigits();

	public void setIntegerDigits(int integerDigits);

	public int getIntegerDigits();
}