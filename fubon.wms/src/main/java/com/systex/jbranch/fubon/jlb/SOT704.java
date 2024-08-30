package com.systex.jbranch.fubon.jlb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;

public class SOT704 extends BizLogic {
	private Logger logger = LoggerFactory.getLogger(SOT704.class);
	/**
	 * ETF/股票適配
	 * @param dam
	 * @param prodID
	 * @param SOT701
	 * 		CUST_ID 客戶ID, 
	 * 		SOT701.W8BenDataVO.fatcaType, 
	 * 		SOT701.FC032675DataVO.custProFlag, 
	 * 		SOT701.FC032675DataVO.investType, 
	 * 		SOT701.FC032675DataVO.investDue
	 * @param pType
	 * @return
	 * @throws JBranchException
	 */
	public Map<String, Object> checkFitness (DataAccessManager dam, String prodID, Map<String, Object> SOT701, String pType) throws JBranchException {
		
		Map<String, Object> outputVO = new HashMap<String, Object>();
		try{
			outputVO.put("PROD_ID", "");
			outputVO.put("FundErrId", "");
			outputVO.put("MessageType", "");
			outputVO.put("Message", "");
			
			String msgType = (String) outputVO.get("MessageType");
			if (StringUtils.isNotBlank(msgType) && StringUtils.equals("error", msgType)) {
				throw new APException((String) outputVO.get("FundErrId"));
			}
			
			if(!StringUtils.equals(pType, "STOCK") && !StringUtils.equals(pType, "ETF")){
				throw new APException("ehl_01_common_026");
			}
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();		
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			sb.append("SELECT PROD.PRD_ID, PROD.").append(pType).append("_CNAME, PROD.PI_BUY, PROD.RISKCATE_ID ");
			sb.append(" ,VO.COUNTRY_ID "); //SOT702.FitnessBond011_013FATCA客戶註記檢核  use COUNTRY_ID 
			sb.append("FROM TBPRD_").append(pType).append(" PROD ");
			sb.append("LEFT JOIN TBPRD_NATIONALITY VO ON PROD.PRD_ID = VO.PROD_ID ");
			sb.append("WHERE PROD.PRD_ID = :prodID ");
			queryCondition.setQueryString(sb.toString());
			queryCondition.setObject("prodID", prodID);
			List<Map<String, Object>> prodDTLList = dam.exeQuery(queryCondition);
			
			
			SOT702 sot702 = new SOT702();
			boolean custAndProdRiskFitOK = false;//判斷客戶C值和產品P值，是否適配
			if (prodDTLList.size() > 0) {
				String prodRisk = (String)prodDTLList.get(0).get("RISKCATE_ID");  //P1、P2、P3
				String custRisk = (String)SOT701.get("kycLV");
				
				StringBuffer riskBf = new StringBuffer("SELECT PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE ='SOT.RISK_FIT_CONFIG'");
				riskBf.append("AND PARAM_CODE =:custRisk ");
				riskBf.append("AND PARAM_NAME like '%"+prodRisk+"%'");
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				queryCondition.setQueryString(riskBf.toString()); 
				queryCondition.setObject("custRisk", custRisk); 
				List<Map<String, Object>> prodcustRiskList = dam.exeQuery(queryCondition);
				custAndProdRiskFitOK = (prodcustRiskList.size()>0)? true:false;
				
				logger.info(String.format("SOT704.checkFitness() prodDTLList:%s \n  SOT701: %s \n" , prodDTLList.get(0) , SOT701));

				//檢查客戶FATCA註記
				
				outputVO = sot702.FitnessFund011_013(outputVO, SOT701, prodDTLList.get(0));
				
				//檢查專投購買限制
				if (!StringUtils.equals("error", (String) outputVO.get("MessageType"))) {
					outputVO = FitnessETF704_001_002(prodDTLList.get(0), SOT701);			
				}
			} else {
				outputVO.put("PROD_ID", "");
				outputVO.put("FundErrId", "ehl_01_SOT704_003");
				outputVO.put("MessageType", "error");
				outputVO.put("Message", "ehl_01_SOT704_003");
				return outputVO;
			}
			
			
			if (!StringUtils.equals("error", (String) outputVO.get("MessageType"))) {
				//客戶風險屬性檢核
				outputVO = sot702.FitnessFund009(outputVO, prodDTLList.get(0), SOT701);
			}
			if (!StringUtils.equals("error", (String) outputVO.get("MessageType"))) {
				//FitnessFund015 客戶風險屬性檢核
				outputVO = sot702.FitnessFund015(outputVO, prodDTLList.get(0), SOT701 , custAndProdRiskFitOK);
			}
			
			
			if (outputVO.get("MessageType") != null && StringUtils.isNotBlank(outputVO.get("MessageType").toString())) {
				logger.error(String.format("錯誤,SOT704適配檢核結果  cust %s , prod %s , errId %s , msgType %s , msg %s",
								SOT701.get("CUST_ID"), prodID, outputVO.get("FundErrId"), outputVO.get("MessageType"), outputVO.get("Message")));
			}
		} catch (Exception e) {
			logger.error(String.format("SOT704.checkFitness() prodID:%s ； SOT701:%s  error:%s", prodID, SOT701.toString(), e.getMessage()), e);
			if( StringUtils.equals(e.getMessage(), "ehl_01_common_026")){
				throw new JBranchException(e.getMessage());
			}
			throw new JBranchException(String.format("適配異常 商品代號:%s error:%s ", prodID, e.getMessage()));
		}
		return outputVO;
	}
	
//	/**
//	 * FACAT註記為N(不合作客戶)、Y(美國人客戶)、X(未簽署客戶)，不可交易
//	 * @param SOT701
//	 * @return
//	 */
//	public Map<String, Object> FitnessETF011_013 (Map<String, Object> SOT701) {
//		
//		Map<String, Object> outputVO = new HashMap<String, Object>();
//		String facaType = (String) SOT701.get("fatcaType");
//		
//		if (StringUtils.isNotBlank(facaType) && facaType.matches("N|Y|X")) {
//			outputVO.put("MessageType", "error");
//			
//			if (StringUtils.equals("N", (String) SOT701.get("fatcaType"))) {
//				outputVO.put("FundErrId", "ehl_01_SOT702_011");
//				outputVO.put("Message", "ehl_01_SOT702_011");
//			} else if (StringUtils.equals("Y", (String) SOT701.get("fatcaType"))) {
//				outputVO.put("FundErrId", "ehl_01_SOT702_012");
//				outputVO.put("Message", "ehl_01_SOT702_012");
//			} else if (StringUtils.equals("X", (String) SOT701.get("fatcaType"))) {
//				outputVO.put("FundErrId", "ehl_01_SOT702_013");
//				outputVO.put("Message", "ehl_01_SOT702_013");
//			}
//		}
//		
//		return outputVO;
//	}
	
	/**
	 * 特定客戶取得專投身分未滿2週不得購買此商品
	 * 非專業投資人不得購買此商品
	 * @param prodDTL
	 * @param SOT701
	 * @return
	 */
	private Map<String, Object> FitnessETF704_001_002 (Map<String, Object> prodDTL, 
												   	   Map<String, Object> SOT701) {
		
		Map<String, Object> outputVO = new HashMap<String, Object>();
		boolean isFitnessOK = false;
		boolean isErrorA = false;
		boolean isErrorB = false;
		
		if (StringUtils.equals("Y", (String) prodDTL.get("PI_BUY")) && 
			StringUtils.equals("Y", (String) SOT701.get("custProFlag")) && 
			StringUtils.equals("Y", (String) SOT701.get("investType")) &&
			!StringUtils.equals("Y", (String) SOT701.get("investDue"))) {
			isErrorA = true;
			isFitnessOK = false;
		} else if (StringUtils.equals("Y", (String) prodDTL.get("PI_BUY")) && 
				   !StringUtils.equals("Y", (String) SOT701.get("custProFlag"))) {
			isErrorB = true;
			isFitnessOK = false;
		} else {
			isFitnessOK = true;
		}

		if (!isFitnessOK) {
			outputVO.put("PROD_ID", prodDTL.get("PRD_ID"));
			outputVO.put("MessageType", "error");
			
			if (isErrorA) {
				outputVO.put("FundErrId", "ehl_01_SOT704_001");
				outputVO.put("Message", "ehl_01_SOT704_001");
			} else if (isErrorB) {
				outputVO.put("FundErrId", "ehl_01_SOT704_002");
				outputVO.put("Message", "ehl_01_SOT704_002");
			}
		}

		return outputVO;
	}	
}
