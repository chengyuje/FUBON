package com.systex.jbranch.app.server.fps.fpsjlb.business;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.cmjlb014.CMJLB014;
import com.systex.jbranch.app.server.fps.cmjlb014.ResultObj;
import com.systex.jbranch.app.server.fps.fpsjlb.conf.TableType;
import com.systex.jbranch.comutil.collection.GenericMap;

import java.util.Arrays;

/**蒙地卡羅模擬法-單筆投資*/
@Component("MonteCarlo")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class MonteCarlo extends AbstractFpsjlbBusinessRtn{
	
	@Override
	public GenericMap excute(GenericMap paramGenericMap) throws Exception {
		String type = paramGenericMap.get(EXCUTE_TYPE); //執行類型
		
		return new GenericMap().put(RESULT ,
			//蒙地卡羅模擬法-單筆投資
			MONTE_CARLO.equals(type) ? excuteMonteCarlo(paramGenericMap , findSource(paramGenericMap)) :
			//蒙地卡羅模擬法-定期定額投資
			MONTE_CARLO_REQ.equals(type) ? excuteMonteCarloReq(paramGenericMap , findSource(paramGenericMap)) :
			//蒙地卡羅模擬法 - 單筆 + 定期定額投資
			MONTE_CARLO_SINGLE_REQ.equals(type) ? excuteMonteCarloSingleReq(paramGenericMap , findSource(paramGenericMap)) : null	
		);
	}
	
	/**蒙地卡羅模擬法-單筆投資*/
	public ResultObj excuteMonteCarlo(GenericMap paramGenericMap , GenericMap sourceData) throws Exception{
		return checkErrorCode(new CMJLB014().cf_MonteCarlo(
			//產品日報酬
			sourceData.get(RETURN_ANN_M , double[][].class) , 
			//權重
			sourceData.get(WEIGHT , double[].class) , 
			//單筆投資金額
			paramGenericMap.get(CASHFLOW , double.class) , 
			//投資的期數
			paramGenericMap.get(SIM_PERIOD , int.class) ,
			//模擬次數
			paramGenericMap.get(SIM_TIMES , int.class) 
		));
	}
	
	/**蒙地卡羅模擬法-定期定額投資*/
	public ResultObj excuteMonteCarloReq(GenericMap paramGenericMap , GenericMap sourceData) throws Exception {
		double [] cashFlows = new double[paramGenericMap.getBigDecimal(SIM_PERIOD).intValue()];
		Arrays.fill(cashFlows, paramGenericMap.get(CASHFLOW , double.class));
		
		return checkErrorCode(new CMJLB014().cf_MonteCarloReg(
			//產品日報酬
			sourceData.get(RETURN_ANN_M , double[][].class) , 
			//權重
			sourceData.get(WEIGHT , double[].class) , 
			//單筆投資金額 ，投資的期數
			cashFlows ,
			//模擬次數
			paramGenericMap.get(SIM_TIMES , int.class) 
		));
	}
	
	/**蒙地卡羅模擬法 - 單筆 + 定期定額投資*/
	public double[][] excuteMonteCarloSingleReq(GenericMap paramGenericMap , GenericMap sourceData) throws Exception{
		int simTimes = paramGenericMap.get(SIM_TIMES);//模擬次數
		int simPeriod = paramGenericMap.get(SIM_PERIOD);//投資的期數
		double cashflow = paramGenericMap.get(CASHFLOW);//單筆投資金額
		double single = paramGenericMap.get(SINGLE_CASHFLOW);//單筆投資金額
		double [] cashFlows = new double[paramGenericMap.getBigDecimal(SIM_PERIOD).intValue()];
		Arrays.fill(cashFlows, paramGenericMap.get(CASHFLOW , double.class));
		double[] weights = sourceData.get(WEIGHT);//權重
		double[][] returnAnnMonths = sourceData.get(RETURN_ANN_M);//產品日報酬
		
		//單筆投資模擬
		ResultObj singleRtn = checkErrorCode(new CMJLB014().cf_MonteCarlo(returnAnnMonths , weights, single, simPeriod, simTimes));
		//定期定額投資模擬
		ResultObj regularRtn = checkErrorCode(new CMJLB014().cf_MonteCarloReg(returnAnnMonths, weights, cashFlows , simTimes));
		
		//加總
		for(int i = 0; i < singleRtn.getResultArray().length ; i++){
			for(int j = 0 ; j < singleRtn.getResultArray()[i].length ; j++ ){
				singleRtn.getResultArray()[i][j] = singleRtn.getResultArray()[i][j]  + regularRtn.getResultArray()[i][j];
			}
		}
		
		return singleRtn.getResultArray();
	}

	@Override
	public TableType getTbConifg(GenericMap paramGenericMap) {
		return TableType.MONTE_CARLO_TO_M;
	}
}
