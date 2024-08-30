package com.systex.jbranch.platform.common.communication.debug;

import com.systex.jbranch.platform.common.communication.jms.*;
import com.systex.jbranch.platform.common.dataManager.UUID;
import java.util.HashMap;


public class sample {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		JMSMessageProducerIF producer = null;
		try
		{
			//建立jms message producer
			producer = JMSMessageProducer.getInstance(new UUID());
			
			String body = "HelloWorld";//訊息本文
			DeliveryMode deliveryMode = DeliveryMode.PERSISTENT;//訊息傳輸模式(永續/非永續)
			int priority = 4;//訊息的優先權,預設是4
			long timeToLive = 180000;//訊息的存活期限(毫秒),0表示永久有效
			
			
			//訊息屬性 Map<String,Object> (Object指的是 String 跟基本型態的wrapper class,java.lang.Integer,java.lang.Double...)
			HashMap<String,Object> properties = new HashMap<String,Object>();
			properties.put("wsid", "ALL");
			properties.put("tlrid", "allenyeh");
			
			MessagePostProcessorIF processor = new BasicMessageProcessor(properties);
			
			//傳送訊息
			producer.send(body, deliveryMode, priority, timeToLive, processor);
		}
		catch(JmsException err)
		{
			System.out.println(err.toString());
		}
		finally
		{
			if(producer!=null)
				producer.close();
		}
	}

}
