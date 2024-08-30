package com.systex.jbranch.platform.formatter.test;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import com.systex.jbranch.platform.formatter.D2Formatter;
import com.systex.jbranch.platform.formatter.D4Formatter;
import com.systex.jbranch.platform.formatter.FormatUtils;
import com.systex.jbranch.platform.formatter.N3Formatter;
import com.systex.jbranch.platform.formatter.N11Formatter;
import com.systex.jbranch.platform.formatter.N8Formatter;
import com.systex.jbranch.platform.formatter.ParseException;
import com.systex.jbranch.platform.formatter.T1Formatter;
import com.systex.jbranch.platform.formatter.DateFormatter.DataType;

public class Tester {

	/**
	 * @param args
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws ParseException
	{		
		
		
		
		Timestamp t=new Timestamp(System.currentTimeMillis()); 
		System.out.println(t);
		String s=FormatUtils.T1Format(t);
		System.out.println(s);
		System.out.println(FormatUtils.T1Parse(" 99:03:03-24:00:00"));
		
		if(true)
			return;
		
		
		System.out.println(FormatUtils.isNumberExpression("N8(16,0)"));
		
		
		System.out.println("["+FormatUtils.D4Format(DataType.N,null)+"]");
		System.out.println("["+FormatUtils.D2Format(null)+"]");
		

		System.out.println("["+FormatUtils.D1Format(DataType.X,(Date)null)+"]");
		System.out.println("["+FormatUtils.D1Format(DataType.N,(Date)null)+"]");
		System.out.println("["+FormatUtils.D1Format(DataType.X,new Date())+"]");
		System.out.println("["+FormatUtils.D1Parse("0680311")+"]");
		
		
		System.out.println(FormatUtils.D4Format(DataType.X,new Date()));
		System.out.println(FormatUtils.D4Parse("07102009"));
		
		System.out.println(FormatUtils.N8Format(6,4,-9.9999));
		System.out.println(FormatUtils.N8Parse(6,4,"0000099999-"));
		
		
		System.out.println(FormatUtils.N8Parse(9,4,"0000083704300+"));
		
		System.out.println("------------------------------------------------------");
		System.out.println(FormatUtils.createDateFormatter("D4(N)").format(new Date()));
		System.out.println(FormatUtils.createNumberFormatter("N8(6,4)").format(-9.9999));
		System.out.println("------------------------------------------------------");
		
		
		
		
		
		
		
		
		N8Format(6,0,"");
		N8Format(6,0,null);
		N8Parse(6,0,"");
		N8Parse(6,0,null);
		
		
		N8Format(6,0,"-123456");
		N8Parse(6,0,"123456-");
		
		N11Format(6,4,"-9.9999");
		N11Parse(6,4,"-0000099999");

		N3Format(6,4,"-9.9999");
		N3Parse(6,4,"-0000099999");
		
		N8Format(6,4,"-9.9999");
		N8Parse(6,4,"0000099999-");

		D2Format(new Date());
		D2Parse("20091006");

		D4Format(new Date());
		D4Parse("06102009");
		
		
		D4Format(null);
		D4Parse(null);
		D4Parse("");
	}

	public static void log(Object...args)
	{
		for(int i=0;i<args.length;i++)
		{
			System.out.print("[");
			System.out.print(args[i]);
			System.out.print("]");
			System.out.print("\t");
		}
		System.out.print("\n");
	}
	
	public static void N3Format(int integerDigits,int fractionDigits,String value)
	{
		N3Formatter formater=new N3Formatter(integerDigits,fractionDigits);		
		log("N3Format ",integerDigits,fractionDigits,value,formater.format(value));

	}
	
	public static void N3Parse(int integerDigits,int fractionDigits,String value) throws ParseException
	{	
		N3Formatter formater=new N3Formatter(integerDigits,fractionDigits);
		log("N3Parse  ",integerDigits,fractionDigits,value,formater.parse(value));
	}

	public static void N11Format(int integerDigits,int fractionDigits,String value)
	{
		N11Formatter formater=new N11Formatter(integerDigits,fractionDigits);		
		log("N11Format ",integerDigits,fractionDigits,value,formater.format(value));

	}
	
	public static void N11Parse(int integerDigits,int fractionDigits,String value) throws ParseException
	{	
		N11Formatter formater=new N11Formatter(integerDigits,fractionDigits);
		log("N11Parse  ",integerDigits,fractionDigits,value,formater.parse(value));
	}
	
	
	public static void N8Format(int integerDigits,int fractionDigits,String value)
	{
		N8Formatter formater=new N8Formatter(integerDigits,fractionDigits);		
		log("N8Format ",integerDigits,fractionDigits,value,formater.format(value));

	}
	
	public static void N8Parse(int integerDigits,int fractionDigits,String value) throws ParseException
	{	
		N8Formatter formater=new N8Formatter(integerDigits,fractionDigits);
		log("N8Parse  ",integerDigits,fractionDigits,value,formater.parse(value));
	}
	
	
	
	public static void D2Format(Date value)
	{
		D2Formatter formater=new D2Formatter(DataType.N);
		log("D2Format ",value.toString(),formater.format(value));

	}
	
	public static void D2Parse(String value) throws ParseException
	{	
		D2Formatter formater=new D2Formatter(DataType.N);
		log("D2Parse  ",value,formater.parse(value).toString());
	}
	
	
	public static void D4Format(Date value)
	{
		D4Formatter formater=new D4Formatter(DataType.N);
		log("D4Format ",value,formater.format(value));

	}
	
	public static void D4Parse(String value) throws ParseException
	{	
		D4Formatter formater=new D4Formatter(DataType.N);
		log("D4Parse  ",value,formater.parse(value));
	}
	
}
