package com.systex.jbranch.platform.formatter;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import org.apache.commons.lang.StringUtils;

import com.systex.jbranch.platform.formatter.DateFormatter.DataType;

public class FormatUtils
{
	private static final String REGEX_TYPE_DATE		="D[A-Z0-9]+[/(][NX]{1}[/)]?|[DT][A-Z0-9]+";
	private static final String REGEX_TYPE_NUMBER	="N[A-Z0-9]+[/(][0-9]+,[0-9]+[/)]";
	
	private FormatUtils()
	{}
	
	private static String getFormatterClassName(String expression)
	{
		String typeName=StringUtils.substringBefore(expression, "(");
		return String.format("com.systex.jbranch.platform.formatter.%sFormatter", typeName);
	}
	
	private static Class getFormatterClass(String expression) throws ClassNotFoundException
	{
		return Class.forName(getFormatterClassName(expression));
	}
	
	public static boolean isDateExpression(String expression)
	{
		if(expression!=null && expression.matches(REGEX_TYPE_DATE))
		{
			try
			{
				getFormatterClass(expression);
				return true;
			}
			catch (ClassNotFoundException e)
			{}
		}
		return false;
	}
	
	public static boolean isNumberExpression(String expression)
	{
		if(expression!=null && expression.matches(REGEX_TYPE_NUMBER))
		{
			try
			{
				getFormatterClass(expression);
				return true;
			}
			catch (ClassNotFoundException e)
			{}
		}
		return false;
	}
	
	public static IDateFormatter createDateFormatter(String expression)
	{
		Constructor constructor;
		IDateFormatter formater=null;
		
		String datatype=StringUtils.substringBetween(expression,"(",")");
		
		try
		{
			if(datatype!=null)
			{
				constructor = getFormatterClass(expression).getConstructor(DataType.class);
				formater=(IDateFormatter)constructor.newInstance(DataType.valueOf(datatype));
			}
			else
			{
				constructor = getFormatterClass(expression).getConstructor();
				formater=(IDateFormatter)constructor.newInstance();
			}
			return formater;
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public static INumberFormatter createNumberFormatter(String expression)
	{
		Constructor constructor;
		INumberFormatter formater=null;
		String paramStr=StringUtils.substringBetween(expression,"(",")");
		String[] params=StringUtils.split(paramStr,",");
		
		Integer integerDigits=Integer.valueOf(params[0]);
		Integer fractionDigits=Integer.valueOf(params[1]);
		try
		{
			constructor = getFormatterClass(expression).getConstructor(int.class,int.class);
			formater=(INumberFormatter)constructor.newInstance(integerDigits,fractionDigits);
			return formater;
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	/**
	 * 
	 * @param integerDigits
	 * @param fractionDigits
	 * @param value 9(n)V9(m)
	 * @return
	 * @throws ParseException
	 */
	public static BigDecimal N3Parse(int integerDigits,int fractionDigits,String value) throws ParseException
	{
		String expression=String.format("N3(%d,%d)",integerDigits,fractionDigits);
		INumberFormatter numberFormater=createNumberFormatter(expression);
		return numberFormater.parse(value);
	}
	/**
	 * 
	 * @param integerDigits
	 * @param fractionDigits
	 * @param value
	 * @return 9(n)V9(m)
	 */
	public static String N3Format(int integerDigits,int fractionDigits,Number value)
	{
		String expression=String.format("N3(%d,%d)",integerDigits,fractionDigits);
		INumberFormatter numberFormater=createNumberFormatter(expression);
		return numberFormater.format(value);
	}
	
	/**
	 * 
	 * @param integerDigits
	 * @param fractionDigits
	 * @param value 9(n)V9(m)
	 * @return
	 * @throws ParseException
	 */
	public static BigDecimal N7Parse(int integerDigits,int fractionDigits,String value) throws ParseException
	{
		String expression=String.format("N7(%d,%d)",integerDigits,fractionDigits);
		INumberFormatter numberFormater=createNumberFormatter(expression);
		return numberFormater.parse(value);
	}
	/**
	 * 
	 * @param integerDigits
	 * @param fractionDigits
	 * @param value
	 * @return 9(n)V9(m)
	 */
	public static String N7Format(int integerDigits,int fractionDigits,Number value)
	{
		String expression=String.format("N7(%d,%d)",integerDigits,fractionDigits);
		INumberFormatter numberFormater=createNumberFormatter(expression);
		return numberFormater.format(value);
	}	
	/**
	 * 
	 * @param integerDigits
	 * @param fractionDigits
	 * @param value
	 * @return 9(n)V9(m)
	 */
	public static String N3Format(int integerDigits,int fractionDigits,String value)
	{
		String expression=String.format("N3(%d,%d)",integerDigits,fractionDigits);
		INumberFormatter numberFormater=createNumberFormatter(expression);
		return numberFormater.format(value);
	}
	/**
	 * 
	 * @param integerDigits
	 * @param fractionDigits
	 * @param value S9(n)V9(m)
	 * @return
	 * @throws ParseException
	 */
	public static BigDecimal N11Parse(int integerDigits,int fractionDigits,String value) throws ParseException
	{
		String expression=String.format("N11(%d,%d)",integerDigits,fractionDigits);
		INumberFormatter numberFormater=createNumberFormatter(expression);
		return numberFormater.parse(value);
	}
	/**
	 * 
	 * @param integerDigits
	 * @param fractionDigits
	 * @param value
	 * @return S9(n)V9(m)
	 */
	public static String N11Format(int integerDigits,int fractionDigits,Number value)
	{
		String expression=String.format("N11(%d,%d)",integerDigits,fractionDigits);
		INumberFormatter numberFormater=createNumberFormatter(expression);
		return numberFormater.format(value);
	}
	/**
	 * 
	 * @param integerDigits
	 * @param fractionDigits
	 * @param value
	 * @return S9(n)V9(m)
	 */
	public static String N11Format(int integerDigits,int fractionDigits,String value)
	{
		String expression=String.format("N11(%d,%d)",integerDigits,fractionDigits);
		INumberFormatter numberFormater=createNumberFormatter(expression);
		return numberFormater.format(value);
	}
	/**
	 * 
	 * @param integerDigits
	 * @param fractionDigits
	 * @param value 9(n)V9(m)S
	 * @return
	 * @throws ParseException
	 */
	public static BigDecimal N8Parse(int integerDigits,int fractionDigits,String value) throws ParseException
	{
		String expression=String.format("N8(%d,%d)",integerDigits,fractionDigits);
		INumberFormatter numberFormater=createNumberFormatter(expression);
		return numberFormater.parse(value);
	}
	/**
	 * 
	 * @param integerDigits
	 * @param fractionDigits
	 * @param value
	 * @return 9(n)V9(m)S
	 */
	public static String N8Format(int integerDigits,int fractionDigits,Number value)
	{
		String expression=String.format("N8(%d,%d)",integerDigits,fractionDigits);
		INumberFormatter numberFormater=createNumberFormatter(expression);
		return numberFormater.format(value);
	}
	/**
	 * 
	 * @param integerDigits
	 * @param fractionDigits
	 * @param value
	 * @return 9(n)V9(m)S
	 */
	public static String N8Format(int integerDigits,int fractionDigits,String value)
	{
		String expression=String.format("N8(%d,%d)",integerDigits,fractionDigits);
		INumberFormatter numberFormater=createNumberFormatter(expression);
		return numberFormater.format(value);
	}	
	/**
	 * D4(X)
	 * @param dateStr ddMMyyyy
	 * @return Timestamp
	 * @throws ParseException
	 */	
	public static Timestamp D4Parse(String dateStr) throws ParseException
	{
		IDateFormatter dateFormater=createDateFormatter("D4");
		return dateFormater.parse(dateStr);
	}
	/**
	 * 
	 * @param datatype X:補滿空白 N:補滿零
	 * @param date
	 * @return ddMMyyyy
	 */	
	public static String D4Format(DataType datatype,Date date)
	{
		String expression=String.format("D4(%s)",datatype);
		IDateFormatter dateFormater=createDateFormatter(expression);
		return dateFormater.format(date);
	}
	/**
	 * 
	 * @param date
	 * @return ddMMyyyy
	 */
	public static String D4Format(Date date)
	{
		IDateFormatter dateFormater=createDateFormatter("D4");
		return dateFormater.format(date);
	}
	/**
	 * D2(X)
	 * @param dateStr yyyyMMdd
	 * @return Timestamp
	 * @throws ParseException
	 */
	public static Timestamp D2Parse(String dateStr) throws ParseException
	{
		IDateFormatter dateFormater=createDateFormatter("D2");
		return dateFormater.parse(dateStr);
	}
	/**
	 * 
	 * @param datatype X:補滿空白 N:補滿零
	 * @param date
	 * @return yyyyMMdd
	 */
	public static String D2Format(DataType datatype,Date date)
	{
		String expression=String.format("D2(%s)",datatype);
		IDateFormatter dateFormater=createDateFormatter(expression);
		return dateFormater.format(date);
	}
	/**
	 * 
	 * @param date
	 * @return yyyyMMdd
	 */
	public static String D2Format(Date date)
	{
		IDateFormatter dateFormater=createDateFormatter("D2");
		return dateFormater.format(date);
	}
	/**民國年月日
	 * D1(X)
	 * @param dateStr yyyMMdd
	 * @return Timestamp
	 * @throws ParseException
	 */
	public static Timestamp D1Parse(String dateStr) throws ParseException
	{
		IDateFormatter dateFormater=createDateFormatter("D1");
		return dateFormater.parse(dateStr);
	}
	/**
	 * 民國年月日
	 * @param datatype X:補滿空白 N:補滿零
	 * @param date
	 * @return yyyMMdd
	 */
	public static String D1Format(DataType datatype,Date date)
	{
		String expression=String.format("D1(%s)",datatype);
		IDateFormatter dateFormater=createDateFormatter(expression);
		return dateFormater.format(date);
	}
	/**
	 * 民國年月日
	 * @param date
	 * @return yyyMMdd
	 */
	public static String D1Format(Date date)
	{
		IDateFormatter dateFormater=createDateFormatter("D1");
		return dateFormater.format(date);
	}
	
	/**民國年月日
	 * T1(X)
	 * @param dateStr yyy/MM/dd-hh:mm:ss
	 * @return Timestamp
	 * @throws ParseException
	 */
	public static Timestamp T1Parse(String dateStr) throws ParseException
	{
		IDateFormatter dateFormater=createDateFormatter("T1");
		return dateFormater.parse(dateStr);
	}
	/**
	 * 民國年月日
	 * @param date
	 * @return yyy/MM/dd-hh:mm:ss
	 */
	public static String T1Format(Date date)
	{
		IDateFormatter dateFormater=createDateFormatter("T1");
		return dateFormater.format(date);
	}
}