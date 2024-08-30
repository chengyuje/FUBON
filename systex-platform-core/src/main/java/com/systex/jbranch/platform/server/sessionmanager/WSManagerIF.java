package com.systex.jbranch.platform.server.sessionmanager;

import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.errHandle.WSTimeOutException;

/**
 * @author Alex Lin
 * @version 2009/12/14 2:52:27 PM
 */
public interface WSManagerIF {
    void whenLogin(String wsIP, String tellerId, String apSvrName, String brchId) throws JBranchException;
    void whenLogin(String wsIP, String tellerId, String apSvrName, String brchId,Boolean allowDupLogin) throws JBranchException;
     
    void whenTransaction(String wsId, String tellerId, String apSvrName, String brchId) throws JBranchException;

    void whenLogout(String wsId, String tellerId, String apSvrName, String brchId) throws JBranchException;

    void whenTimeout(String wsId, String tellerId, String apSvrName, String brchId) throws JBranchException;
}
