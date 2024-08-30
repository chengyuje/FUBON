package com.systex.jbranch.platform.common.dataaccess.dao.impl.hibernate;


public class DaoEvent<T> {
	
	private String eventName;
	private T vo;
	
	public DaoEvent(String eventName){
		this.eventName = eventName;
	}
	
	public DaoEvent(String eventName, T vo){
		this.eventName = eventName;
		this.vo = vo;
	}
	
	public String getEventName(){
		
		return eventName;
	}
	
	public T getVO(){
		
		return vo;
	}
}
