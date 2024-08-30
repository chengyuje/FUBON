package com.systex.jbranch.app.server.fps.insjlb.dao;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.app.server.fps.insjlb.vo.ThirdInsProdInputVO;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

public interface InsjlbDaoInf {
	/**保險公司清單*/
	public List<Map<String , Object>> queryInsCompanyAll(String conType) throws  JBranchException;
	/**保險公司清單*/
	public List<Map<String , Object>> queryInsCompanyAll() throws JBranchException;
	/**產品資訊*/
	public List<Map<String, Object>> queryThirdInsProdMsg(ThirdInsProdInputVO inputVO) throws JBranchException;
	/**給附項目中文*/
	public List<Map<String , Object>> querySortMap() throws JBranchException;
	/**資訊源產品檔**/
	public List<Map<String , Object>> queryInfSourceProdData(List<String> productIds) throws JBranchException;
	/**既有保障缺口對應SORTNO*/
	public List<Map<String, Object>> queryOlditemSortnoMap(String[] paramCodes) throws JBranchException;
	/**撈取設定*/
	public List<Map<String, Object>> queryParameterConf(String paramType) throws JBranchException;
	/**資訊源商品檔*/
	public List<Map<String, Object>> queryFinancialProduct(List<String> keyNos) throws JBranchException;
	/**資訊源商品檔*/
	public List<Map<String, Object>> queryParameterForType(String paramType) throws JBranchException;
}
