package com.systex.jbranch.platform.common.communication;

import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.UUID;
import java.io.PrintWriter;


public final class MessageProducer implements MessageProducerIF {

	private PrintWriter out = null;
	
	public static MessageProducerIF getInstance(UUID uuid)
	{
		return new MessageProducer(uuid);
	}
	
	private MessageProducer(UUID uuid)
	{
		out = DataManager.getSection(uuid).getOut();
	}
	
	public void send(String value)
	{
		out.write(value);
		out.flush();
	}
}