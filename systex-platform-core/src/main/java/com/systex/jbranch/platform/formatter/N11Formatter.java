package com.systex.jbranch.platform.formatter;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang.StringUtils;

public class N11Formatter extends N3Formatter {

	public N11Formatter(int integerDigits, int fractionDigits)
	{
		this();
		setIntegerDigits(integerDigits);
		setFractionDigits(fractionDigits);
	}

	public N11Formatter()
	{
		decimalFormat.setGroupingUsed(false);
		decimalFormat.setNegativePrefix("-");
		decimalFormat.setNegativeSuffix("");
		decimalFormat.setPositivePrefix("+");
		decimalFormat.setPositiveSuffix("");
	}

	@Override
	public String formatNull()
	{
		return StringUtils.repeat(" ", this.getIntegerDigits()+this.getFractionDigits()+1);
	}
	
	@Override
	public String format(BigDecimal value)
	{
		value=value.setScale(this.getFractionDigits(), RoundingMode.DOWN);
		
		String str=decimalFormat.format(value);
		str=StringUtils.remove(str, '.');
		
		return str;
	}
}
