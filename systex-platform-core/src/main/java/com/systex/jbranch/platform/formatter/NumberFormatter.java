package com.systex.jbranch.platform.formatter;

import java.math.BigDecimal;

public abstract class NumberFormatter implements INumberFormatter
{
	private int fractionDigits;
	private int integerDigits;

	public String format(String value)
	{
		if(value==null || "".equals(value.trim()))
			return formatNull();
		
		return format(new BigDecimal(value));
	}
	
	public String format(Number value)
	{
		if(value==null)
			return formatNull();
		return format(new BigDecimal(value.toString()));
	}
	
	protected abstract String format(BigDecimal value);
	
	protected abstract String formatNull();
	
	public abstract BigDecimal parse(String value)throws ParseException;

	protected abstract void onFractionDigitsChanged(int oldVal,int newVal);
	
	protected abstract void onIntegerDigitsChanged(int oldVal,int newVal);
	
	public void setFractionDigits(int fractionDigits)
	{
		if(fractionDigits<0)
			fractionDigits=0;
		
		if(this.fractionDigits!=fractionDigits)
		{
			int oldVal=this.fractionDigits;
			this.fractionDigits = fractionDigits;
			onFractionDigitsChanged(oldVal,fractionDigits);
		}
	}

	public int getFractionDigits() 
	{
		return fractionDigits;
	}

	public void setIntegerDigits(int integerDigits)
	{
		if(integerDigits<0)
			integerDigits=0;
		
		if(this.integerDigits!=integerDigits)
		{
			int oldVal=this.integerDigits;
			this.integerDigits = integerDigits;
			onIntegerDigitsChanged(oldVal,integerDigits);
		}
	}

	public int getIntegerDigits()
	{
		return integerDigits;
	}
}