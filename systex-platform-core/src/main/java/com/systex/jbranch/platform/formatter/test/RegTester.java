package com.systex.jbranch.platform.formatter.test;

public class RegTester
{
	public static void main(String...args)
	{
		System.out.println(Integer.valueOf("00"));
		System.out.println(Integer.valueOf("01"));

		
		
		
		//D2 yyyyMMdd
		match("\\d\\d\\d\\d(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])","20091012");
		match("\\d\\d\\d\\d(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])","12102009");
		
		//D4 ddMMyyyy
		match("(0[1-9]|[12][0-9]|3[01])(0[1-9]|1[012])\\d\\d\\d\\d","12102009");
		match("(0[1-9]|[12][0-9]|3[01])(0[1-9]|1[012])\\d\\d\\d\\d","20091012");
		//D4
		
		//N1
		match("[0-9]+","1234");
		
		//N2
		match("[/+/-]{1}[0-9]+","+1");

		//N4
		match("[0-9]+[/+/-]{1}","1234-");
		match("[0-9]+[/+/-]{1}","-");
		match("[0-9]+[/+/-]{1}","1234--");
		
		
		match("D[A-Z0-9]+[/(][NX]{1}[/)]?|D[A-Z0-9]+","D4(N)");
		match("D[A-Z0-9]+[/(][NX]{1}[/)]?|D[A-Z0-9]+","D4");
		
		
		
		
		match("D[A-Z0-9]+[/(][NX]{1}[/)]","D4(X)");
		match("D[A-Z0-9]+[/(][NX]{1}[/)]","D4(NX)");
		match("D[A-Z0-9]+[/(][NX]{1}[/)]","D4()");
		match("D[A-Z0-9]+[/(][NX]{1}[/)]","D(NX)");
		
		
		match("[0-9]{4}-[0-9]{6}","0920425126");
		match("[0-9]{4}-[0-9]{6}","0920-425126");
		
		
		//D type.
		match("D[A-Z0-9]+","D4");
		
		//N type.
		match("N[A-Z0-9]+[/(][0-9]+,[0-9]+[/)]","N11(10,2)");
	}
	
	public static void match(String regex,String value)
	{

		if(value.matches(regex))
			System.out.println(String.format("%s \t     match \t %s",regex,value));
		else
			System.out.println(String.format("%s \t not match \t %s",regex,value));		
	}
	
}
