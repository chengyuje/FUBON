package com.systex.jbranch.app.server.fps.fpsjlb.business;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.cmjlb014.CMJLB014;
import com.systex.jbranch.app.server.fps.fpsjlb.conf.TableType;
import com.systex.jbranch.comutil.collection.GenericMap;

/**投資組合波動度計算*/
@Component("PortfolioVolatility")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class PortfolioVolatility extends AbstractFpsjlbBusinessRtn{
	
	@Override
	public GenericMap excute(GenericMap paramGenericMap) throws Exception {
		GenericMap sourceData = findSource(paramGenericMap);
		double[] weights = sourceData.get(WEIGHT);//權重
		double[][] returnDays = sourceData.get(RETURN_D);//產品日報酬	
		return new GenericMap().put(RESULT , new CMJLB014().port_Variance(returnDays , weights));
	}
	
	@Override
	public TableType getTbConifg(GenericMap paramGenericMap) {
		return TableType.PORTFOLIO_VOLATILITY;
	}
}
