package com.systex.jbranch.app.server.fps.fpsjlb;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.app.server.fps.cmjlb014.ResultObj;
import com.systex.jbranch.comutil.collection.GenericMap;

public interface FPSJLB {
	/**投資組合波動度計算*/
	public ResultObj getPortfolioVolatility(List<Map<String,Object>> ret) throws Exception;
	/**有無投保註記 */
	public boolean getCustIandIMark(String custID) throws Exception ;
	/**摩地卡羅單筆*/
	public GenericMap getMonteCarlo(List<Map<String,Object>> ret, double cashflow, int simPeriod) throws Exception;
	/**摩地卡羅定期*/
	public GenericMap getMonteCarloReq(List<Map<String,Object>> ret, double cashflow, int simPeriod) throws Exception;
	/**摩地卡羅單筆定期*/
	public GenericMap getMonteCarloSim(List<Map<String,Object>> ret, double single, double cashflow, int simPeriod) throws Exception;
	/**投資組合波動度計算*/
	public ResultObj getPortfolioVolatility(List<Map<String,Object>> ret , int year) throws Exception;
	/**摩地卡羅單筆*/
	public GenericMap getMonteCarlo(List<Map<String,Object>> ret, double cashflow, int simPeriod , int year) throws Exception;
	/**摩地卡羅定期*/
	public GenericMap getMonteCarloReq(List<Map<String,Object>> ret, double cashflow, int simPeriod , int year) throws Exception;
	/**摩地卡羅單筆定期*/
	public GenericMap getMonteCarloSim(List<Map<String,Object>> ret, double single, double cashflow, int simPeriod , int year) throws Exception;
	/**效率前緣資產配置計算預設三年*/
	public GenericMap getPortEffFrontier(List<Map<String,Object>> ret) throws Exception;
	/**效率前緣資產配置計算*/
	public GenericMap getPortEffFrontier(List<Map<String, Object>> ret, int year) throws Exception;
	/**效率前緣資產配置計算*/
	public GenericMap getPortEffFrontierPoint(List<Map<String, Object>> ret, int pointSize) throws Exception;
	/**效率前緣資產配置計算*/
	public GenericMap getPortEffFrontier(List<Map<String, Object>> ret, int year, int pointSize) throws Exception;
	/**效率前緣資產配置計算*/
	public GenericMap getPortEffFrontier(List<Map<String, Object>> ret, int year, int pointSize, double lower, double upper) throws Exception;
	/**預期投資組合年化報酬模擬*/
	@Deprecated
	public GenericMap getPortRtnSim(List<Map<String,Object>> prd, double cashflow, Date endDate) throws Exception;
	/**預期投資組合年化報酬模擬*/
	public GenericMap getPortRtnSim(List<Map<String, Object>> ret, double cashflow, double eachYear, int stayYear, int year) throws Exception;
	/**預期投資組合年化報酬模擬*/
	public GenericMap getPortRtnSim(List<Map<String, Object>>[] purchasedList, BigDecimal purchasedValue[], int stayYear, int minMonth, int maxMonth, int reminingMonth) throws Exception;
}
