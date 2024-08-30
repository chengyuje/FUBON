package com.systex.jbranch.app.server.fps.service.sso;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

public interface WmsSsoDaoInf {
	public List<Map<String , Object>> queryForRandomNo(String sysCode , BigDecimal rabdomNum) throws DAOException, JBranchException;
	public int delTbsysSsoInfoTimeOutData() throws DAOException, JBranchException;
}
