package com.systex.jbranch.platform.formatter;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.systex.jbranch.platform.formatter.DateFormatter.DataType;

public class D1Formatter implements IDateFormatter {

	private DataType datatype;
	
	public D1Formatter(DataType datatype)
	{
		this.datatype=datatype;
	}

	public D1Formatter()
	{
		this.datatype=DataType.X;
	}
	
	protected String formatNull() 
	{
		if(DataType.N.equals(datatype))
		{
			return StringUtils.repeat("0", 7);
		}
		else
		{
			return StringUtils.repeat(" ", 7);
		}
	}

	public String format(Date date)
	{
		if(date!=null)
		{
			Calendar calendar=Calendar.getInstance();
			calendar.setTimeInMillis(date.getTime());
			return String.format("%03d%02d%02d",calendar.get(Calendar.YEAR)-1911,calendar.get(Calendar.MONTH)+1,calendar.get(Calendar.DATE));	
		}
		else
		{
			return formatNull();
		}
	}

	public Timestamp parse(String value) throws ParseException 
	{
		try
		{
			if(value==null)
				return null;
			
			value=value.trim();
			
			if(NumberUtils.isNumber(value) && Integer.valueOf(value).intValue()==0)
			{
				return null;
			}
			else if("".equals(value))
			{
				return null;
			}
			
			value=StringUtils.leftPad(value, 7,"0");
			int year=Integer.valueOf(StringUtils.substring(value, 0, 3)).intValue()+1911;
			int month=Integer.valueOf(StringUtils.substring(value, 3, 5)).intValue()-1;
			int date=Integer.valueOf(StringUtils.substring(value, 5, 7)).intValue();
			Calendar calendar=Calendar.getInstance();
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.MONTH, month);
			calendar.set(Calendar.DATE, date);
			
			return new Timestamp(calendar.getTimeInMillis());
		}
		catch(Exception e)
		{
			throw new ParseException(e);
		}
	}
}
