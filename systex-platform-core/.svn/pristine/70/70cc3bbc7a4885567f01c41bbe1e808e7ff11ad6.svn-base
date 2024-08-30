package com.systex.jbranch.platform.formatter;

import org.apache.commons.lang.StringUtils;

public class D2Formatter extends DateFormatter
{
	public D2Formatter(DataType datatype) 
	{
		super("yyyyMMdd",datatype);
	}

	public D2Formatter()
	{
		this(DataType.X);
	}
	
	@Override
	protected String formatNull() {

		if(DataType.N.equals(datatype))
		{
			return StringUtils.repeat("0", 8);
		}
		else
		{
			return StringUtils.repeat(" ", 8);
		}
	}
}