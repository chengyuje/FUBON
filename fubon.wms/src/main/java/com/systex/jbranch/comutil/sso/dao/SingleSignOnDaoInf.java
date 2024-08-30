package com.systex.jbranch.comutil.sso.dao;

import java.util.Map;

import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

public interface SingleSignOnDaoInf {
	public Map queryMemberInfo(String custId) throws JBranchException;
	public GenericMap querySsoConfig() throws JBranchException;
	public GenericMap queryAasSsoConfig() throws JBranchException;
	public GenericMap queryConfig(String paramType) throws JBranchException;
	public GenericMap queryInsSigSsoConfig() throws JBranchException;
}
