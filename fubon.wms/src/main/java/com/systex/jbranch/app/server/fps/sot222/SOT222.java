package com.systex.jbranch.app.server.fps.sot222;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.sot705.CustAssetETFVO;
import com.systex.jbranch.app.server.fps.sot705.SOT705;
import com.systex.jbranch.app.server.fps.sot705.SOT705InputVO;
import com.systex.jbranch.app.server.fps.sot705.SOT705OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * sot222
 * 
 * @author ocean
 * @date 2016/09/29
 * @spec ETF股票庫存查詢
 */
@Component("sot222")
@Scope("request")
public class SOT222 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	
	public void getCustAssetETFData (Object body, IPrimitiveMap header) throws JBranchException, Exception {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		SOT222InputVO inputVO = (SOT222InputVO) body;
		SOT222OutputVO outputVO = new SOT222OutputVO();
		SOT705InputVO inputVO_705 = new SOT705InputVO();
		SOT705OutputVO outputVO_705 = new SOT705OutputVO();
		
		inputVO_705.setCustId(inputVO.getCustID());
		inputVO_705.setIsOBU(inputVO.getIsOBU());
		inputVO_705.setIsInTran(true);
		
		SOT705 sot705 = (SOT705) PlatformContext.getBean("sot705");
		outputVO_705 = sot705.getCustAssetETFData(inputVO_705);
		
		String errorMsg = outputVO_705.getErrorMsg();
		if (!"".equals(errorMsg) && null != errorMsg) {
			outputVO.setErrorMsg(errorMsg);
		} else {
			outputVO.setEtfList(outputVO_705.getCustAssetETFList());
			outputVO.setStockList(outputVO_705.getCustAssetStockList());
		}
		this.sendRtnObject(outputVO);
	}
	
	public Map<String, Object> returnMap (List<Map<String, Object>> list, CustAssetETFVO vo, String pType) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("PTYPE", pType);
		if(list.size() > 0){
			map.put("PROD_ID", list.get(0).get("PRD_ID"));
			map.put("TXN_UNIT", list.get(0).get("TXN_UNIT"));
			map.put("TRADING_UNIT", list.get(0).get("TRADING_UNIT"));
//			map.put("CUR_AMT", list.get(0).get("CUR_AMT"));//參考收盤價改由下行電文接取
			map.put("SOU_DATE", list.get(0).get("SOU_DATE"));
			map.put("COUNTRY_CODE", list.get(0).get("COUNTRY_CODE"));
			map.put("STOCK_CODE", list.get(0).get("STOCK_CODE"));
			map.put("RISKCATE_ID", list.get(0).get("RISKCATE_ID"));
			map.put("MARKET_PRICE", list.get(0).get("MARKET_PRICE"));
		}else{
			map.put("PROD_ID", vo.getInsuranceNo());
		}
		//參考收盤價改由下行電文接取
//		map.put("CUR_AMT",vo.getCurAmt());
//.		map.put("REF_PRICE", vo.getForCurBal());	
//		map.put("PROD_NAME", vo.getProductName());
//		map.put("CURRENCY_STD_ID", vo.getCurCode());
//		map.put("TRUST_ACCT", vo.getTrustAcct());
//		map.put("NUMBER", vo.getNumber());
//		map.put("ENTRUST_CUR", vo.getEntrustCur());
//.		map.put("AVG_BUYING_PRICE", vo.getAvgBuyingPrice());	
//		map.put("FOR_CUR_BAL", vo.getForCurBal());
//.		map.put("RETURN_RATE_SIGN", vo.getReturnRateSign());	
//.		map.put("RETURN_RATE", vo.getReturnRate());
//		map.put("IS_NEW_PROD", ("Y".equals(vo.getProductType2()) ? "N" : "Y"));
//		map.put("TRUST_CURR_TYPE", ("N".equals(vo.getTrustBusinessType()) ? "N" : "Y"));
		return map;
	}
}