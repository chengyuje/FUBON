package com.systex.jbranch.platform.common.dataaccess.dao.impl.hibernate;

public interface DaoListener {

	public void onCreateBefore(DaoEvent event);
	
	public void onCreated(DaoEvent event);
	
	public void onUpdateBefore(DaoEvent event);
	
	public void onUpdated(DaoEvent event);
	
	public void onDeleteBefore(DaoEvent event);
	
	public void onDeleted(DaoEvent event);
}
