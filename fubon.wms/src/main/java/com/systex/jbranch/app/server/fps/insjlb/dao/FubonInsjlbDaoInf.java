package com.systex.jbranch.app.server.fps.insjlb.dao;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.errHandle.JBranchException;

public interface FubonInsjlbDaoInf {
	/**查詢家庭成員與財務安全資訊*/
	public Map<String, Object> queryFamilyMemberFinanceSafetyInf(String custId) throws JBranchException;
	
	/**保障缺口對應SORTNO*/
	public List<Map<String, Object>> querySecurityGapSortNo() throws JBranchException;
	
	/**子女教育試算*/
	public List<Map<String, Object>> queryLstSppedu(String custId) throws JBranchException;
}
