package com.systex.jbranch.platform.common.communication.jms;

import javax.jms.Message;
import javax.jms.Session;
import javax.jms.JMSException;

public interface MessageConverterIF  {

	Message toMessage(Object object, Session session) throws JMSException;

	Object fromMessage(Message message) throws JMSException;
}
