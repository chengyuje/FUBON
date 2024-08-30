package com.systex.jbranch.platform.common.communication.jms;

import javax.jms.Message;
import javax.jms.Session;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.SimpleMessageConverter;


public class BasicMessageConverter implements MessageConverterIF {

	MessageConverter converter = new SimpleMessageConverter();
	
	public Object fromMessage(Message message) throws JmsException {

		try
		{
			return converter.fromMessage(message);
		}
		catch(Exception ex)
		{
			throw JmsUtil.convertJmsAccessException(ex);
		}
	}

	public Message toMessage(Object object, Session session)
			throws JmsException {
		try
		{
			return converter.toMessage(object, session);
		}
		catch(Exception ex)
		{
			throw JmsUtil.convertJmsAccessException(ex);
		}
	}

}
