package com.systex.esoaf.jms;

import javax.jms.JMSException;

public class AdvancedJMSException extends JMSException 
{
	private Throwable cause;
	
	public AdvancedJMSException(String reason,Throwable e) 
	{	
		super(reason);
		cause=e;
	}
	
	public AdvancedJMSException(Throwable e) 
	{	
		super(e.getMessage());
		cause=e;
	}
	
	public AdvancedJMSException(String reason, String errorCode) 
	{
		super(reason, errorCode);
	}

	public AdvancedJMSException(String reason) 
	{
		super(reason);
	}
	
	@Override
	public Throwable getCause() 
	{
		if(cause==null)
			return super.getCause();
			
		return cause;
	}
}
