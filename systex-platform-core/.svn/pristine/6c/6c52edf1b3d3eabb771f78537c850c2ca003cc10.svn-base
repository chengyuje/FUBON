package com.systex.jbranch.platform.common.communication.jms;

public class JmsUtil {
	
	private JmsUtil(){}
	
	public static JmsException convertJmsAccessException(Exception ex) {

		if (ex instanceof JmsException)
		{
			return (JmsException)ex;
		}
		return new JmsException(ex);
	} 
}
