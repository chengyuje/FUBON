package com.systex.jbranch.platform.common.communication.debug;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Console {
	
	private Console()
	{
	}

	public static void WriteLine(String value)
	{
		System.out.println(value);
	}
	
	public static String ReadLine()
	{
		String value = null;
		try
		{
			// Read all standard input and send it as a message.
            BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
            value = stdin.readLine();
		}
		catch(Exception err)
		{			
		}
		return value;
           
	}
}