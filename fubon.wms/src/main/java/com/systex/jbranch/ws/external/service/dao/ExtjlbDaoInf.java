package com.systex.jbranch.ws.external.service.dao;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.errHandle.JBranchException;

public interface ExtjlbDaoInf {
	public List<Map<String, Object>> queryParameterConf(String paramType) throws JBranchException;
	public List<Map<String, Object>> queryParameterForType(String paramType) throws JBranchException;
}
