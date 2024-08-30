package com.systex.jbranch.platform.common.communication.jms;

import javax.jms.Message;
import javax.jms.JMSException;

public interface MessagePostProcessorIF {
	
	Message postProcessMessage(Message message) throws JMSException;
}
