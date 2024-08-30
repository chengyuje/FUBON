package com.systex.jbranch.app.server.fps.fpsjlb.business;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.cmjlb014.CMJLB014;
import com.systex.jbranch.app.server.fps.cmjlb014.ResultObj;
import com.systex.jbranch.app.server.fps.fpsjlb.conf.TableType;
import com.systex.jbranch.app.server.fps.fpsutils.FPSUtils;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.XmlInfo;

/**預期投資組合年化報酬模擬*/
@Component("PortRtnSim")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class PortRtnSim extends AbstractFpsjlbBusinessRtn{
	Logger logger = LoggerFactory.getLogger(PortRtnSim.class);
	@Override
	public GenericMap excute(GenericMap paramGenericMap) throws Exception {
		String type = paramGenericMap.get(FpsjlbParamInf.EXCUTE_TYPE);
		
		//判斷執行類型
		if(FpsjlbBusinessInf.EACH_YEAR.equals(type)){
			logger.debug("excute: "+FpsjlbBusinessInf.EACH_YEAR);
			return excuteForEachYear(paramGenericMap);
		}
		else if(FpsjlbBusinessInf.END_DATE.equals(type)){
			logger.debug("excute: "+FpsjlbBusinessInf.END_DATE);
			return excuteForEndDate(paramGenericMap);
		}
		
		return null;
	}
	
	/**
	 * 不知道誰會去用到
	 * @param paramGenericMap
	 * @return
	 * @throws Exception
	 */
	public GenericMap excuteForEndDate(GenericMap paramGenericMap) throws Exception {
		//撈取報酬率
		GenericMap sourceData = findSource(paramGenericMap);
		//權重
		double[] weights = sourceData.get(WEIGHT);
		//產品月報酬
		double[][] ret = sourceData.get(RETURN_1M);	
		//單筆投資金額
		double cashflow = paramGenericMap.getBigDecimal(CASHFLOW).doubleValue();
		//投資迄日
		Date endDate = paramGenericMap.getDate(END_DATE);
		
		GenericMap confgGmap = new GenericMap(
			new XmlInfo().doGetVariable("FPS.PORT_RTN_SIM_CONF", FormatHelper.FORMAT_3)
		);
		
		//取得計算參數
		final double eVal = confgGmap.getBigDecimal("E_VAL").doubleValue();
		final double upZval = confgGmap.getBigDecimal("UP_Z_VAL").doubleValue();
		final double upZNormal = confgGmap.getBigDecimal("UP_Z_NORMAL").doubleValue();
		final double downZval = confgGmap.getBigDecimal("DOWN_Z_VAL").doubleValue();
		final double stdVal = confgGmap.getBigDecimal("STD_VAL").doubleValue();
		
		//每月頭組報酬
		CMJLB014 cmjlb014 = new CMJLB014();
		ResultObj rtn = cmjlb014.port_Ret(ret , weights);

		double [][] everyMonthRets = rtn.getResultArray();

		// Juan 假設cmjlb014.port_Ret()回傳的質都為%
		double [][] dePercentMonthRet = new double[1][everyMonthRets[0].length];
		int monthLen = 0;
		for(double everyMonthRet : everyMonthRets[0]){
			dePercentMonthRet[0][monthLen] = everyMonthRet/100;
			monthLen++;
		}
		
		double sumEveryMonthRet = 0.0d;
		
		for(double everyMonthRet : dePercentMonthRet[0]){
			sumEveryMonthRet += everyMonthRet;
		}
		
		//計算投組的年化平均報酬
		double annRtn = Math.pow((1 + (sumEveryMonthRet / monthLen)) , 12) -1;
		
		//計算投組的年化標準差
		ResultObj annualStdResul = cmjlb014.util_AnnualStd(dePercentMonthRet);
		double ann_std = annualStdResul != null ? annualStdResul.getResultArray()[0][0] : 0.0;
		
		long period = (endDate.getTime() - new Date().getTime()) / 1000 / 60 / 60 / 24; 
		period = period / 365; //n年
		
		double initAmount = cashflow;//期初金額
		Double [] result = new Double[3];
		
		Double ePowParam = (annRtn - stdVal * Math.pow(ann_std , 2)) * period; // 年畫報酬率 - 在常態分配中預想的的點 * 期數 (轉為標準常態)
		Double annRtnPeriod = annRtn * Math.pow(period , 0.5);
		
		//樂觀
		result[0] = initAmount * Math.pow(eVal , (ePowParam + upZval * annRtnPeriod));
		//正常
		result[1] = initAmount * Math.pow(eVal , (ePowParam + upZNormal * annRtnPeriod));
		//悲觀
		result[2] = initAmount * Math.pow(eVal , (ePowParam + downZval * annRtnPeriod));

		return new GenericMap()
			.put("resultCode" , "00")
			.put("resultStr" , "Success")
			.put("resultArray" , result);
	}
	
	/**
	 * 績效模擬的五條線
	 * 
	 * @param paramGenericMap
	 * @return
	 * @throws Exception
	 */
	public GenericMap excuteForEachYear(GenericMap paramGenericMap) throws Exception {
		// 撈取報酬率
		// GenericMap sourceData = findSource(paramGenericMap);
		paramGenericMap.getParamMap();
		
		// 所有運算資料參數  單筆 & 定額
		Map<String, Object> invSingleParamMap = investmentParamter(paramGenericMap, "SINGLE");
		Map<String, Object> invPeriodParamMap = investmentParamter(paramGenericMap, "PERIOD");
		
		// 單筆投資 Three Case + 定期定額 Three Case = SUM(CASE)
		Double[] sumCaseArr = sumSingleInvPeriodInv(getSingleInvestment(invSingleParamMap), getPeriodInestment(invPeriodParamMap));
		
		return new GenericMap()
			.put("resultCode" , "00")
			.put("resultStr" , "Success")
			.put("resultArray" , sumCaseArr);
	}
	
	/**
	 * 績效模擬參數設定
	 * @param purchasedValue
	 * @param paramGenericMap
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> investmentParamter(GenericMap paramGenericMap, String type) throws Exception {
		GenericMap confgGmap = new GenericMap(new XmlInfo().doGetVariable("FPS.PORT_RTN_SIM_CONF", FormatHelper.FORMAT_3));
		List<Map<String, Object>> purchasedList = (List<Map<String, Object>>) paramGenericMap.get("PURCHASED_" + type + "_LIST");
		if(CollectionUtils.isEmpty(purchasedList)) return null;
		
		int minMonth = paramGenericMap.getBigDecimal(DATA_DATE_MONTH_MIN).intValue();
		
		int maxMonth = paramGenericMap.getBigDecimal(DATA_DATE_MONTH_MAX).intValue();
		
		// RET_LIST
		// 取得年度報酬率
		BigDecimal annRtnBigD = FPSUtils.getYRate(this.getDataAccessManager(), purchasedList, maxMonth, minMonth);
		
		if(annRtnBigD == null) {
			throw new Exception("Err-Data-001");
		}
		
		double rtn = annRtnBigD.doubleValue();
		
		BigDecimal annStdBigD = FPSUtils.getStandardDeviation(this.getDataAccessManager(), purchasedList, maxMonth, minMonth);
		
		// 取得波動率
		if(annStdBigD == null) {
			throw new Exception("Err-Data-002");
		}
		
		double std = annStdBigD.doubleValue();
		
		//年期
		int years = paramGenericMap.getBigDecimal(STAY_YEAR).intValue();
		int months = paramGenericMap.getBigDecimal(REMAINING_MONTH).intValue();
			
		//取得計算參數
		final double upZval = confgGmap.getBigDecimal("UP_Z_VAL").doubleValue();
		final double downZval = confgGmap.getBigDecimal("DOWN_Z_VAL").doubleValue();
		
		Map<String, Object> investmentParamMap = new HashMap<String, Object>();
//		investmentParamMap.put("year", years);							// 年
		investmentParamMap.put("month", months);							// 月
		investmentParamMap.put("rtn", rtn);								// 年報酬率
		investmentParamMap.put("std", std);								// 標準差
		investmentParamMap.put("val", Math.pow(std, 2));				// 變異數
		investmentParamMap.put("purchasedValue", paramGenericMap.getBigDecimal("PURCHASED_" + type + "_VALUE").doubleValue());					// 單筆投資
		investmentParamMap.put("muVal", rtn - (Math.pow(std, 2) * 0.5));		// 年報酬-0.5*變異數
		investmentParamMap.put("upZval", std * upZval);
		investmentParamMap.put("downZval", std * downZval);
		return investmentParamMap;
	}
	
	/**
	 * 實際計算單筆投資績效模擬
	 * @param investmentParamMap
	 * @return
	 */
	private Double[] getSingleInvestment(Map<String, Object> investmentParamMap) {
		// 一般單筆投資 * EXP(muVal * year)
		// 較佳單筆投資 * EXP(muVal * year + upZval * (year ^ 0.5))
		// 較差單筆投資 * EXP(muVal * year + downZval * (year ^ 0.5))
		Double [] singleInvArray = new Double[]{0.0d, 0.0d, 0.0d};
		if(MapUtils.isEmpty(investmentParamMap)) return singleInvArray;
		double singleInv = (double)investmentParamMap.get("purchasedValue");
		double muVal = (double)investmentParamMap.get("muVal");
		double year = new GenericMap(investmentParamMap).getBigDecimal("month").divide(new BigDecimal(12), 6, BigDecimal.ROUND_HALF_UP).doubleValue();
		double downZval = (double)investmentParamMap.get("downZval");
		double upZval = (double)investmentParamMap.get("upZval");
		singleInvArray[0] = singleInv * Math.exp(muVal * year + upZval * Math.sqrt(year));
		singleInvArray[1] = singleInv * Math.exp(muVal * year);
		singleInvArray[2] = singleInv * Math.exp(muVal * year + downZval * Math.sqrt(year));
		return singleInvArray;
	}
	
	/**
	 * 實際計算定額投資績效模擬
	 * @param investmentParamMap
	 * @return
	 */
	private Double[] getPeriodInestment(Map<String, Object> investmentParamMap) {
		// 一般定期投資 * EXP(muVal * (期數(單位月)/12))
		// 較佳較佳投資 * EXP(muVal * (期數(單位月)/12) + upZval * ((期數(單位月)/12) ^ 0.5))
		// 較差較差投資 * EXP(muVal * (期數(單位月)/12) + downZval * ((期數(單位月)/12) ^ 0.5))
		Double [] periodInvArray = new Double[]{0.0d, 0.0d, 0.0d};
		if(MapUtils.isEmpty(investmentParamMap)) return periodInvArray;
		double periodInv = (double)investmentParamMap.get("purchasedValue");
		double muVal = (double)investmentParamMap.get("muVal");
		int month = (int)investmentParamMap.get("month");
		double downZval = (double)investmentParamMap.get("downZval");
		double upZval = (double)investmentParamMap.get("upZval");
		
		System.out.println("較佳 \t\t 一般 \t\t 較差 \t\t ");
		for(int i = 1; i <= month ; i++) {
			double valueYear = new BigDecimal(i).divide(new BigDecimal(12), 8, BigDecimal.ROUND_HALF_UP).doubleValue();
			periodInvArray[0] += periodInv * Math.exp(muVal * valueYear + upZval * Math.sqrt(valueYear));
			periodInvArray[1] += periodInv * Math.exp(muVal * valueYear);
			periodInvArray[2] += periodInv * Math.exp(muVal * valueYear + downZval * Math.sqrt(valueYear));
			System.out.println(periodInv * Math.exp(muVal * valueYear + upZval * Math.sqrt(valueYear)) + " \t\t " + periodInv * Math.exp(muVal * (valueYear)) + " \t\t " + periodInv * Math.exp(muVal * (valueYear) + downZval * Math.sqrt(valueYear)));
		}
		return periodInvArray;
	}
	
	/**
	 * 合併單筆投資 * 定額投資
	 * @param singleInvArr
	 * @param periodInvArr
	 * @return
	 */
	private Double[] sumSingleInvPeriodInv (Double[] singleInvArr, Double[] periodInvArr) {
		Double[] sum = new Double[4];
		for(int i=0; i<3; i++) {
			sum[i] = singleInvArr[i] + periodInvArr[i];
			System.out.println((i == 0 ? "較佳" : (i == 1 ? "一般" : "較差")) + singleInvArr[i] + " \t\t " + periodInvArr[i] + " \t\t " + sum[i]);
		}
		return sum;
	}
	
	public static void main(String...args){
		double a= 0.0;
		double b = 0.0;
		System.out.println(b/a);
	}
	
	@Override
	public TableType getTbConifg(GenericMap paramGenericMap) {
		return TableType.PORT_RTN_SIM;
	}
}
