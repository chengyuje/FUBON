package com.systex.jbranch.app.server.fps.service.inssig;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

public interface WmsInsSigDaoInf {
	public List<Map<String , Object>> querySignList(GenericMap paramMap) throws DAOException, JBranchException;
	public void updateCaseStatus(Map<String , Object> paramMap) throws Exception;
	public void updateCasePDF(GenericMap paramMap) throws Exception;
	public List<Map<String , Object>> queryForJwtString(String str) throws DAOException, JBranchException;
	public int delTbsysSsoInfoTimeOutData() throws DAOException, JBranchException;
}
