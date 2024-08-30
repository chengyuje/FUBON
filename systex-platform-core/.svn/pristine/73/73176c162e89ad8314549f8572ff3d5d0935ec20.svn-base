package com.systex.jbranch.platform.common.communication.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.Map;
import java.util.Set;

public class BasicMessageProcessor implements MessagePostProcessorIF {

	private Map<String,Object> propertiesMap = null;
	
	public BasicMessageProcessor(Map<String,Object> propertiesMap)
	{
		this.propertiesMap = propertiesMap;
	}
		
	public Message postProcessMessage(Message message) throws JMSException {

		Set<String> keys = this.propertiesMap.keySet();
		for(String key: keys)
			message.setObjectProperty(key,this.propertiesMap.get(key));

		return message;
	}

}
