package com.systex.jbranch.platform.formatter;

import org.apache.commons.lang.StringUtils;

public class D4Formatter extends DateFormatter
{
	public D4Formatter(DataType datatype)
	{
		super("ddMMyyyy",datatype);
	}

	public D4Formatter()
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