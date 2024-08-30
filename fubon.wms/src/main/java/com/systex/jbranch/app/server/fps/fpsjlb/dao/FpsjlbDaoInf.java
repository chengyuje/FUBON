package com.systex.jbranch.app.server.fps.fpsjlb.dao;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.app.server.fps.fpsjlb.conf.TableType;
import com.systex.jbranch.comutil.callBack.CallBackExcute;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

@SuppressWarnings("rawtypes")
public interface FpsjlbDaoInf {
	/**效率前緣資產配置計算*/
	
	/**投保註記*/
	public List<Map<String , Object>> queryCustIandIMark(String custID)throws Exception;
	
	public DataAccessManager getDataAccessManager();
	public void setDataAccessManager(DataAccessManager dataAccessManager);

	/**抓指定日期區間內，近一個月報酬加總除以12個月的年度報酬率*/
	public List<Map<String , Object>> queryDateByIdType(String[] cloumns , TableType table , Integer year , List<Map<String , Object>> idTypeList) throws JBranchException;
	public List<Map<String , Object>> queryHistoryData(TableType table , List<Map<String , Object>> idTypeDateList) throws JBranchException;
	public List<Map<String , Object>> queryHistoryData(TableType table , List<Map<String , Object>> idTypeDateList , CallBackExcute callback) throws JBranchException;

	// 取得共同區間資料
	public List<Map<String, Object>> queryCommonIntervalList(Map<String, List<String>> groupProdIDMap,List<Map<String, Object>> mfdEtfInsList, int maxMonth, boolean fullYear, String currentYearMonth) throws JBranchException, ParseException;

	// 取得共同區間內的所有資料
	public List<Map<String, Object>> queryYRateDataResource(Map<String, List<String>> groupProdIDMap, List<Map<String, Object>> mfdEtfInsList, String[] interval) throws JBranchException;
}
