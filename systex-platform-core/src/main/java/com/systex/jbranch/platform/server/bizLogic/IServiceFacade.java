package com.systex.jbranch.platform.server.bizLogic;

import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.conversation.message.EnumShowType;

public interface IServiceFacade {

	DataAccessManager getDataAccessManager() throws DAOException,JBranchException;
	DataAccessManager getDataAccessManager(String dbID) throws DAOException,JBranchException;
	
	void sendWarningMsg(String message);
	void sendWarningMsg(String message,EnumShowType showType);
	
	void sendRtnMsg(String message);
	void sendRtnMsg(String message,EnumShowType showType);
	
	void sendRtnReport(Object obj);
	
	void sendErrMsg(JBranchException e);
	void sendErrMsg(JBranchException e,EnumShowType showType);
	void sendErrMsg(String ml_idGroup_s);
	void sendErrMsg(String ml_idGroup_s,EnumShowType showType);
	
	void sendRtnObject(String clientMethod, Object obj);
	void sendRtnObject(Object obj);
	
	Object getCommonVariable(String key) throws JBranchException;
	void setCommonVariable(String key,Object value) throws JBranchException;
		 
	Object getUserVariable(String key) throws JBranchException;
    void setUserVariable(String key,Object value) throws JBranchException;
    
    void notifyClientToDownloadFile(String fileUrl,String defaultFileName);
   
}
