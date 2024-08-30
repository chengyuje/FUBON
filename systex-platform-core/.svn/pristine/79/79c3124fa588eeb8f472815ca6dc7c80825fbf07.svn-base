package com.systex.jbranch.platform.common.communication.jms;

import java.util.Vector;

public class DeliveryMode {

	private int value;
	private String name;
	private static final Vector<DeliveryMode> valueList = new Vector<DeliveryMode>();
	public static final DeliveryMode NON_PERSISTENT = new DeliveryMode("NON_PERSISTENT",1);
	public static final DeliveryMode PERSISTENT = new DeliveryMode("PERSISTENT",2);

	public static DeliveryMode valueOf(int value)
	{
		for(int i=0;i<DeliveryMode.valueList.size();i++)
		{
			if(value==DeliveryMode.valueList.get(i).intValue())return valueList.get(i); 
		}
		return DeliveryMode.NON_PERSISTENT;
	}
	
	private DeliveryMode(String name,int value)
	{
		this.name = name;
		this.value = value;
		DeliveryMode.valueList.add(this);
	}

	public final int intValue()
	{
		return value;
	}
	
	public final String toString()
	{
		return name;
	} 
	
	public final boolean equals(Object anObject)
	{
		return super.equals(anObject);
	}
	
	public final int hashCode()
	{
		return super.hashCode();
	}
}
