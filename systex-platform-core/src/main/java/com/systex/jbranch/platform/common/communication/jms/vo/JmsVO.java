package com.systex.jbranch.platform.common.communication.jms.vo;

import javax.jms.Session;
import javax.jms.DeliveryMode;


public final class JmsVO implements JmsVoIF {
	
	//ConnectionFactory
    private String userName = null;
    private String password = null;
    
    //Connection
    private int acknowledgeMode = Session.AUTO_ACKNOWLEDGE;
    private String clientID = null;
    
    //Session
    private String messageSelector = null;
    private boolean noLocal = false;
    private String durableSubscriptionName = null;
    
    //JmsMessageProducer
    private String destinationName = null;
	private int deliveryMode = DeliveryMode.PERSISTENT;
	private boolean disableMessageID = false;
	private boolean disableMessageTimestamp = false;
	private int priority = 4;
	private long timeToLive = 0;

	//when connection failed,the max times try to re-create connection
	private int reconnectTimes = 5;
	private int timeout = 5000;

    public int getReconnectTimes() {
        return reconnectTimes;
    }
    public void setReconnectTimes(int reconnectTimes) {
        this.reconnectTimes = reconnectTimes;
    }
    
    public int getTimeout() {
        return timeout;
    }
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
    	
	//ConnectionFactory
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    
    //Connection
    public int getAcknowledgeMode()
    {
    	return this.acknowledgeMode;
    }
    public void setAcknowledgeMode(int value)
    {
    	this.acknowledgeMode = value;
    }
    
    public String getClientID()
    {
    	return this.clientID;
    }
    public void setClientID(String value)
    {
    	this.clientID = value;
    }
    
    //Session
    public String getMessageSelector()
    {
    	return this.messageSelector;
    }
    public void setMessageSelector(String value)
    {
    	this.messageSelector = value;
    }
    
    public boolean getNoLocal()
    {
    	return this.noLocal;
    }
    public void setNoLocal(boolean value)
    {
    	this.noLocal = value;
    }
    
    public String getDurableSubscriptionName()
    {
    	return this.durableSubscriptionName;
    }
    public void setDurableSubscriptionName(String value)
    {
    	this.durableSubscriptionName = value;
    }
    
    //JmsMessageProducer
    public String getDestinationName() {
        return this.destinationName;
    }
    public void setDestinationName(String value) {
        this.destinationName = value;
    }
	
	public int getDeliveryMode()
	{
		return this.deliveryMode;
	}
	public void setDeliveryMode(int value)
	{
		this.deliveryMode = value;
	}
	
	public boolean getDisableMessageID()
	{
		return this.disableMessageID;
	}
	public void setDisableMessageID(boolean value)
	{
		this.disableMessageID = value;
	}
	
	public boolean getDisableMessageTimestamp()
	{
		return this.disableMessageTimestamp;
	}
	public void setDisableMessageTimestamp(boolean value)
	{
		this.disableMessageTimestamp = value;
	}
	
	public int getPriority()
	{
		return this.priority;
	}
	public void setPriority(int value)
	{
		this.priority = value;
	}
	
	public long getTimeToLive()
	{
		return this.timeToLive;
	}
	public void setTimeToLive(long value)
	{
		this.timeToLive = value;
	}
    
}
