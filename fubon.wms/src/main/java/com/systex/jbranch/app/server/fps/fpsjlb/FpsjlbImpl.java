package com.systex.jbranch.app.server.fps.fpsjlb;

import static com.systex.jbranch.app.server.fps.fpsjlb.business.FpsjlbParamInf.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.cmjlb014.ResultObj;
import com.systex.jbranch.app.server.fps.fpsjlb.business.FpsjlbBusinessInf;
import com.systex.jbranch.comutil.collection.GenericMap;

@Component("fpsjlb")
public class FpsjlbImpl implements FPSJLB{
	@Autowired @Qualifier("PortfolioVolatility")
	private FpsjlbBusinessInf portfolioVolatility;
	
	@Autowired @Qualifier("PortEffFrontier")
	private FpsjlbBusinessInf portEffFrontier;
	
	@Autowired @Qualifier("MonteCarlo")
	private FpsjlbBusinessInf monteCarlo;
	
	@Autowired @Qualifier("CustIandIMark")
	private FpsjlbBusinessInf custIandIMark;
	
	@Autowired @Qualifier("PortRtnSim")
	private FpsjlbBusinessInf portRtnSim;
	
	/**投資組合波動度計算預設三年**/
	public ResultObj getPortfolioVolatility(List<Map<String,Object>> ret) throws Exception{
		return getPortfolioVolatility(ret, 3);
	}
	
	/**投資組合波動度計算**/
	public ResultObj getPortfolioVolatility(List<Map<String, Object>> ret, int year) throws Exception {
		return portfolioVolatility.excute(new GenericMap().put(RET_LIST, ret).put(DATA_DATE_YEAR , year)).get(RESULT , ResultObj.class);
	}

	/**效率前緣資產配置計算預設三年*/
	public GenericMap getPortEffFrontier(List<Map<String,Object>> ret) throws Exception{
		return getPortEffFrontier(ret , 3 , 50);
	}
	
	/**效率前緣資產配置計算*/
	public GenericMap getPortEffFrontier(List<Map<String, Object>> ret, int year) throws Exception {
		return getPortEffFrontier(ret , year , 50);
	}
	
	/**效率前緣資產配置計算*/
	public GenericMap getPortEffFrontierPoint(List<Map<String, Object>> ret, int pointSize) throws Exception {
		return getPortEffFrontier(ret , 3 , pointSize);
	}
	
	/**效率前緣資產配置計算*/
	public GenericMap getPortEffFrontier(List<Map<String, Object>> ret, int year, int pointSize) throws Exception {
		return portEffFrontier.excute(new GenericMap()
			.put(RET_LIST, ret)
			.put(DATA_DATE_YEAR , year)
			.put(POINT_SIZE, pointSize)
		);
	}
	
	/**效率前緣資產配置計算*/
	public GenericMap getPortEffFrontier(List<Map<String, Object>> ret, int year, int pointSize, double lower, double upper) throws Exception {
		return portEffFrontier.excute(new GenericMap()
		.put(RET_LIST, ret)
		.put(DATA_DATE_YEAR , year)
		.put(POINT_SIZE, pointSize)
		.put(LOWER, lower)
		.put(UPPER, upper)
	);
	}
	
	/**摩地卡羅單筆預設三年**/
	public GenericMap getMonteCarlo(List<Map<String,Object>> ret, double cashflow, int simPeriod) throws Exception{
		return getMonteCarlo(ret , cashflow , simPeriod , 3);
	}

	/**摩地卡羅單筆**/
	public GenericMap getMonteCarlo(List<Map<String, Object>> ret, double cashflow, int simPeriod, int year) throws Exception {
		return monteCarlo.excute(new GenericMap()
		.put(EXCUTE_TYPE, MONTE_CARLO)
		.put(RET_LIST, ret)
		.put(CASHFLOW, cashflow)
		.put(SIM_PERIOD, simPeriod)
		.put(SIM_TIMES, 10000)
		.put(DATA_DATE_YEAR, year));
	}
	
	/**摩地卡羅定期預設三年**/
	public GenericMap getMonteCarloReq(List<Map<String,Object>> ret, double cashflow, int simPeriod) throws Exception{
		return getMonteCarloReq(ret , cashflow , simPeriod , 3);
	}

	/**摩地卡羅定期**/
	public GenericMap getMonteCarloReq(List<Map<String, Object>> ret, double cashflow, int simPeriod, int year) throws Exception {
		return monteCarlo.excute(new GenericMap()
		.put(EXCUTE_TYPE, MONTE_CARLO_REQ)
		.put(RET_LIST, ret)
		.put(CASHFLOW, cashflow)
		.put(SIM_PERIOD, simPeriod)
		.put(SIM_TIMES, 10000)
		.put(DATA_DATE_YEAR, year));
	}

	/**摩地卡羅單筆定期預設三年**/
	public GenericMap getMonteCarloSim(List<Map<String,Object>> ret, double single, double cashflow, int simPeriod) throws Exception{
		return getMonteCarloSim(ret , single , cashflow , simPeriod , 3);
	}

	/**摩地卡羅單筆定期**/
	public GenericMap getMonteCarloSim(List<Map<String, Object>> ret, double single, double cashflow, int simPeriod, int year) throws Exception{
		return monteCarlo.excute(new GenericMap()
			.put(EXCUTE_TYPE, MONTE_CARLO_SINGLE_REQ)
			.put(RET_LIST, ret)
			.put(CASHFLOW, cashflow)
			.put(SINGLE_CASHFLOW, single)
			.put(SIM_PERIOD, simPeriod)
			.put(SIM_TIMES, 10000)
			.put(DATA_DATE_YEAR, year));
	}

	/**有無投保註記 */
	public boolean getCustIandIMark(String custID) throws Exception {
		return custIandIMark.excute(new GenericMap().put(CUST_ID, custID)).get(RESULT , Boolean.class);
	}

	/**預期投資組合年化報酬模擬*/
	public GenericMap getPortRtnSim(List<Map<String, Object>> ret, double cashflow, Date endDate) throws Exception {
		return getPortRtnSim(ret, cashflow, endDate , 3);
	}
	
	/**預期投資組合年化報酬模擬*/
	@Deprecated
	public GenericMap getPortRtnSim(List<Map<String, Object>> ret, double cashflow, Date endDate , int year) throws Exception {
		return portRtnSim.excute(new GenericMap()
			//執行類型
			.put(EXCUTE_TYPE, FpsjlbBusinessInf.END_DATE)
			//產品權重矩陣
			.put(RET_LIST, ret)
			//單筆投資金額
			.put(CASHFLOW , cashflow)
			//投資訖日
			.put(END_DATE, endDate)
			//年度範圍區間
			.put(DATA_DATE_YEAR, year)
		);
	}
	
	/**預期投資組合年化報酬模擬*/
	public GenericMap getPortRtnSim(List<Map<String, Object>> ret, double cashflow, double eachYear, int stayYear, int year) throws Exception {
		return portRtnSim.excute(new GenericMap()
			//執行類型
			.put(EXCUTE_TYPE, FpsjlbBusinessInf.EACH_YEAR)
			//產品權重矩陣
			.put(RET_LIST, ret)
			//單筆投資金額
			.put(CASHFLOW , cashflow)
			//定期投資金額
			.put(EACH_YEAR, eachYear)
			//投資年期
      .put(STAY_YEAR, stayYear)
			//共同區間年
			.put(DATA_DATE_YEAR, year)
		);
	}
	
	// 改版的
	/**預期投資組合年化報酬模擬*/
	public GenericMap getPortRtnSim(List<Map<String, Object>>[] purchasedListArray, BigDecimal[] purchasedValueArray, int stayYear, int minMonth, int maxMonth, int reminingMonth) throws Exception {
		return portRtnSim.excute(new GenericMap()
			//執行類型
			.put(EXCUTE_TYPE, FpsjlbBusinessInf.EACH_YEAR)
			//單筆 產品權重矩陣
			.put(PURCHASED_SINGLE_LIST, purchasedListArray[0])
			//定額 產品權重矩陣
			.put(PURCHASED_PERIOD_LIST, purchasedListArray[1])
			//單筆投資金額
			.put(PURCHASED_SINGLE_VALUE , purchasedValueArray[0].doubleValue())
			//定期投資金額
			.put(PURCHASED_PERIOD_VALUE, purchasedValueArray[1].doubleValue())
			//投資年期
			.put(STAY_YEAR, stayYear)
			//共同區間年最小
			.put(DATA_DATE_MONTH_MIN, minMonth)
			//共同區間年最大
			.put(DATA_DATE_MONTH_MAX, maxMonth)
			//剩餘月
			.put(REMAINING_MONTH, reminingMonth)
		);
	}
}
