package com.systex.jbranch.platform.formatter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import org.apache.commons.lang.StringUtils;

public class N3Formatter extends NumberFormatter implements INumberFormatter
{
	protected DecimalFormat decimalFormat=new DecimalFormat(); 
	
	public N3Formatter(int integerDigits, int fractionDigits)
	{
		this();
		setIntegerDigits(integerDigits);
		setFractionDigits(fractionDigits);
	}

	public N3Formatter()
	{
		decimalFormat.setGroupingUsed(false);
		decimalFormat.setNegativePrefix("-");
		decimalFormat.setNegativeSuffix("");
		decimalFormat.setPositivePrefix("");
		decimalFormat.setPositiveSuffix("");
	}
	
	@Override
	public String formatNull()
	{
		return StringUtils.repeat(" ", this.getIntegerDigits()+this.getFractionDigits());
	}
	
	@Override
	public String format(BigDecimal value)
	{
		value=value.setScale(this.getFractionDigits(), RoundingMode.DOWN).abs();
		
		String str=decimalFormat.format(value);
		str=StringUtils.remove(str, '.');
		
		return str;
	}

	@Override
	public BigDecimal parse(String value) throws ParseException
	{
		try
		{
			if(value==null || "".equals(value.trim()))
				return null;
			
			int index=value.length()-this.getFractionDigits();
			if(index<0)index=0;

			if(index==0)
			{
				value="0."+value;
			}
			else
			{
				value=value.substring(0, index)+"."+value.substring(index);
			}
			return BigDecimal.valueOf(decimalFormat.parse(value).doubleValue());			
		}
		catch(Exception e)
		{
			throw new ParseException(e);
		}
	}

	@Override
	protected void onFractionDigitsChanged(int oldVal, int newVal)
	{
		decimalFormat.setMinimumFractionDigits(newVal);
		decimalFormat.setMaximumFractionDigits(newVal);
	}

	@Override
	protected void onIntegerDigitsChanged(int oldVal, int newVal)
	{
		decimalFormat.setMinimumIntegerDigits(newVal);
		decimalFormat.setMaximumIntegerDigits(newVal);	
	}
	
	
}