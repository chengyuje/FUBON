package com.systex.jbranch.fubon.jlb;

import com.systex.jbranch.app.server.fps.sot712.SOT712;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SOT702 extends BizLogic {
	private Logger logger = LoggerFactory.getLogger(SOT702.class);
	 
	/**
	 * 基金適配檢核
	 * 商品管理 的搜尋 沒有信託幣別條件，所以trustCurrType帶null
	 * @param dam
	 * @param tradeType 交易類別
	 * @param tradeDate 交易日期
	 * @param prodID 基金代號
	 * @param SOT701 客戶基本資料與帳號 CUST_ID、obuFlag、noSale、custProFlag、fatcaType、ageUnder70Flag、kycLV
	 * @return
	 * @throws JBranchException
	 */
	public Map<String, Object> checkFitness (DataAccessManager dam, String tradeType, Date tradeDate, String prodID, Map<String, Object> SOT701) throws JBranchException {
		return checkFitness (dam, null, tradeType, tradeDate, prodID, SOT701);
	}
	
	/**
	 * 基金適配檢核
	 * @param dam
	 * @param trustCurrType 信託幣別 N台幣 Y外幣
	 * @param tradeType 交易類別
	 * @param tradeDate 交易日期
	 * @param prodID 基金代號
	 * @param SOT701 客戶基本資料與帳號 CUST_ID、obuFlag、noSale、custProFlag、fatcaType、ageUnder70Flag、kycLV
	 * @return
	 * @throws JBranchException
	 */
	public Map<String, Object> checkFitness (DataAccessManager dam, String trustCurrType, String tradeType, Date tradeDate, String prodID, Map<String, Object> SOT701) throws JBranchException {
		
		Map<String, Object> outputVO = new HashMap<String, Object>();
		try {
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT MAST.CUST_ID, MAST.BIRTH_DATE, CUST_RISK_ATR, NOTE.CURT_FATCA_ID, ");
			sb.append("(SELECT COUNT(*) CNT ");
			sb.append("FROM TBCRM_CUST_MAST TEMP ");
			sb.append("WHERE 1 = 1 ");
			sb.append("AND TEMP.CUST_ID = MAST.CUST_ID ");
			if (tradeDate != null) { //若有交易日期 (因有些功能沒有交易日)
				sb.append("AND TO_CHAR(:effectDate, 'yyyyMMdd') >= TO_CHAR(ADD_MONTHS(TEMP.BIRTH_DATE, 840), 'yyyyMMdd') ");
			}
			sb.append("AND TO_CHAR(ADD_MONTHS(SYSDATE, -840), 'yyyyMMdd') < TO_CHAR(TEMP.BIRTH_DATE, 'yyyyMMdd')) AS CNT ");
			sb.append("FROM TBCRM_CUST_MAST MAST ");
			sb.append("LEFT JOIN TBCRM_CUST_NOTE NOTE ON MAST.CUST_ID = NOTE.CUST_ID ");
			sb.append("WHERE MAST.CUST_ID = :custID ");
			queryCondition.setQueryString(sb.toString());
			queryCondition.setObject("custID", SOT701.get("CUST_ID"));
			if (tradeDate != null) { //若有交易日期 (因有些功能沒有交易日)
				queryCondition.setObject("effectDate", tradeDate);
			} else {
				logger.warn("checkFitness tradeDate is NULL");
			}
			List<Map<String, Object>> custDTLList = dam.exeQuery(queryCondition);
			
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			sb.append("SELECT FUND.PRD_ID, FUND.IS_SALE, FUND.OBU_PROD, FUND.OBU_BUY, FUND.BUY_TWD, FUND.QUOTAS, FUND.FLAG, FUND.HIGH_YIELD, FUND.OBU_AGE, FUND.RISKCATE_ID, INFO.SELLING, FINFO.FUS40 ");
			sb.append(" ,VO.COUNTRY_ID "); //SOT702.FitnessBond011_013FATCA客戶註記檢核  use COUNTRY_ID 
			sb.append("FROM TBPRD_FUND FUND ");
			sb.append("LEFT JOIN TBPRD_FUND_BONUSINFO INFO ON FUND.PRD_ID = INFO.PRD_ID ");
			sb.append("LEFT JOIN TBPRD_FUNDINFO FINFO ON FUND.PRD_ID = FINFO.PRD_ID ");
			sb.append("LEFT JOIN TBPRD_NATIONALITY VO ON FUND.PRD_ID = VO.PROD_ID ");
			sb.append("WHERE FUND.PRD_ID = :prodID ");
			queryCondition.setQueryString(sb.toString());
			queryCondition.setObject("prodID", prodID);
			List<Map<String, Object>> prodDTLList = dam.exeQuery(queryCondition);
	
			if (custDTLList.size() > 0 && prodDTLList.size() > 0) {
				//客戶風險等級和產品風險等級對照表
				String prodRisk = (String)prodDTLList.get(0).get("RISKCATE_ID");  //P1、P2、P3
				
				// 這欄是空的String custRisk = (String)custDTLList.get(0).get("CUST_RISK_ATR");
				if (SOT701.get("kycLV") == null) {
					logger.error("SOT701.kycLV is NULL 請在 SOT701Map.put('kycLV',  KycLV ");
				}
				String custRisk = (String)SOT701.get("kycLV");
				
				StringBuffer riskBf = new StringBuffer("SELECT PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE ='SOT.RISK_FIT_CONFIG'");
				riskBf.append("AND PARAM_CODE =:custRisk ");
				riskBf.append("AND PARAM_NAME like '%"+prodRisk+"%'");
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				queryCondition.setQueryString(riskBf.toString()); 
				queryCondition.setObject("custRisk", custRisk); 
				List<Map<String, Object>> prodcustRiskList = dam.exeQuery(queryCondition);
				boolean custAndProdRiskFitOK = (prodcustRiskList.size()>0)? true:false;
				logger.info(String.format("SOT702.checkFitness() custDTLList: %s \n prodDTLList:%s \n tradeType: %s \n tradeDate: %s \n SOT701: %s \n", custDTLList.get(0), prodDTLList.get(0), tradeType, tradeDate, SOT701));
				
				outputVO.put("PROD_ID", "");
				outputVO.put("FundErrId", "");
				outputVO.put("MessageType", "");
				outputVO.put("Message", "");
				
				outputVO = FitnessFund001(outputVO, custDTLList.get(0), prodDTLList.get(0), tradeType, tradeDate, SOT701);
				
				if (!StringUtils.equals("error", (String) outputVO.get("MessageType"))) {
					outputVO = FitnessFund002(outputVO, custDTLList.get(0), prodDTLList.get(0), tradeType, tradeDate, SOT701);
				}
				
				if (!StringUtils.equals("error", (String) outputVO.get("MessageType"))) {
					outputVO = FitnessFund003(outputVO, custDTLList.get(0), prodDTLList.get(0), tradeType, tradeDate, SOT701);
				}
				
				if (!StringUtils.equals("error", (String) outputVO.get("MessageType"))) {
					outputVO = FitnessFund004(outputVO, custDTLList.get(0), prodDTLList.get(0), trustCurrType, tradeType, tradeDate, SOT701);
				}
				
				if (!StringUtils.equals("error", (String) outputVO.get("MessageType"))) {
					outputVO = FitnessFund005(outputVO, custDTLList.get(0), prodDTLList.get(0), tradeType, tradeDate, SOT701);
				}
				
				if (!StringUtils.equals("error", (String) outputVO.get("MessageType"))) {
					outputVO = FitnessFund006(outputVO, custDTLList.get(0), prodDTLList.get(0), tradeType, tradeDate, SOT701);
				}
				
				if (!StringUtils.equals("error", (String) outputVO.get("MessageType"))) {
					outputVO = FitnessFund007(outputVO, custDTLList.get(0), prodDTLList.get(0), tradeType, tradeDate, SOT701);
				}
				
	//			if (!StringUtils.equals("error", (String) outputVO.get("MessageType"))) {
	//				outputVO = FitnessFund008(outputVO, custDTLList.get(0), prodDTLList.get(0), tradeType, tradeDate, SOT701, effectDate);
	//			}
				
				if (!StringUtils.equals("error", (String) outputVO.get("MessageType"))) {
					outputVO = FitnessFund009(outputVO, prodDTLList.get(0), SOT701);
				}
				
				if (!StringUtils.equals("error", (String) outputVO.get("MessageType"))) {
					outputVO = FitnessFund010(outputVO, custDTLList.get(0), prodDTLList.get(0), tradeType, tradeDate, SOT701);
				}
				
				if (!StringUtils.equals("error", (String) outputVO.get("MessageType"))) {
					outputVO = FitnessFund011_013(outputVO, SOT701, prodDTLList.get(0)); //FitnessFund011_013 FATCA客戶註記檢核
				}
				
				if (!StringUtils.equals("error", (String) outputVO.get("MessageType"))) {
					outputVO = FitnessFund014(outputVO, custDTLList.get(0), prodDTLList.get(0), tradeType, tradeDate, SOT701);
				}
				
				if (!StringUtils.equals("error", (String) outputVO.get("MessageType"))) {
					outputVO = FitnessFund015(outputVO, prodDTLList.get(0), SOT701, custAndProdRiskFitOK);
				}
				
				if (!StringUtils.equals("error", (String) outputVO.get("MessageType"))) {
					outputVO = FitnessFund016(outputVO, custDTLList.get(0), prodDTLList.get(0), tradeType, tradeDate, SOT701);
				}
				
				if (!StringUtils.equals("error", (String) outputVO.get("MessageType"))) {
					outputVO = FitnessFund017(outputVO, custDTLList.get(0), prodDTLList.get(0), tradeType, tradeDate, SOT701);
				}
			} else {
				if (custDTLList.size() == 0) {
					outputVO.put("PROD_ID", "");
					outputVO.put("FundErrId", "適配結果:查無客戶主檔。");
					outputVO.put("MessageType", "error");
					outputVO.put("Message", "適配結果:查無客戶主檔。");
				} else {
					outputVO.put("PROD_ID", "");
					outputVO.put("FundErrId", "適配結果:輸入代號/名稱錯誤。");
					outputVO.put("MessageType", "error");
					outputVO.put("Message", "適配結果:輸入代號/名稱錯誤。");
				} 
			}
			if (outputVO.get("MessageType") != null && StringUtils.isNotBlank(outputVO.get("MessageType").toString())) {
				logger.error(String.format("錯誤,SOT702適配檢核結果  cust %s , prod %s , errId %s , msgType %s , msg %s",
								SOT701.get("CUST_ID"), prodID, outputVO.get("FundErrId"), outputVO.get("MessageType"), outputVO.get("Message")));
			}
		} catch (Exception e) {
			logger.error(String.format("SOT702.checkFitness() prodID:%s ； SOT701:%s  error:%s", prodID , SOT701.toString(), e.getMessage()), e);
			throw new JBranchException(String.format("適配異常 商品代號:%s error:%s ", prodID, e.getMessage()));
		}
		return outputVO;
	}
	
	/*
	 * FitnessFund001 商品銷售註記檢核&商品OBU產品註記
	 */
	private Map<String, Object> FitnessFund001 (Map<String, Object> outputVO, 
												Map<String, Object> custDTL, 
												Map<String, Object> prodDTL, 
												String tradeType, 
												Date tradeDate,
												Map<String, Object> SOT701) {
		
		boolean isFitnessOK = false;
		
		if (StringUtils.equals("2", (String) prodDTL.get("IS_SALE"))) {
			if (StringUtils.equals("Y", (String) prodDTL.get("OBU_PROD")) && StringUtils.equals("Y", (String) SOT701.get("obuFlag"))) {
				isFitnessOK = true;
			}
		} else if (!StringUtils.equals("2", (String) prodDTL.get("IS_SALE"))) {
			isFitnessOK = true;
		}
		
		if (!isFitnessOK) {
			outputVO.put("PROD_ID", prodDTL.get("PRD_ID"));
			outputVO.put("FundErrId", "ehl_01_SOT702_001");
			outputVO.put("MessageType", "error");
			outputVO = messageAddFundErrId(outputVO);
		}
		
		return outputVO;
	}
	
	/*
	 * FitnessFund002客戶拒銷註記檢核
	 */
	public Map<String, Object> FitnessFund002 (Map<String, Object> outputVO, 
											   Map<String, Object> custDTL, 
											   Map<String, Object> prodDTL, 
											   String tradeType, 
											   Date tradeDate,
											   Map<String, Object> SOT701) {
		
		boolean isFitnessOK = false;
		
		if (StringUtils.equals("Y", (String) SOT701.get("noSale")) && !StringUtils.equals("3", tradeType) && !StringUtils.equals("4", tradeType)) {
			isFitnessOK = true;
		} else if (!StringUtils.equals("Y", (String) SOT701.get("noSale"))){
			isFitnessOK = true;
		}
		
		if (!isFitnessOK) {
			outputVO.put("PROD_ID", prodDTL.get("PRD_ID"));
			outputVO.put("FundErrId", "ehl_01_SOT702_002");
			outputVO.put("MessageType", "error");
			outputVO = messageAddFundErrId(outputVO);
		}
		
		return outputVO;
	}
	
	/*
	 * FitnessFund003 檢核事件_商品OBU申購註記檢核
	 */
	private Map<String, Object> FitnessFund003 (Map<String, Object> outputVO, 
												Map<String, Object> custDTL, 
												Map<String, Object> prodDTL, 
												String tradeType, 
												Date tradeDate,
												Map<String, Object> SOT701) {

		boolean isFitnessOK = false;
		
		if (StringUtils.equals("Y", (String) SOT701.get("obuFlag")) && StringUtils.equals("N", (String) prodDTL.get("OBU_BUY"))) {
			isFitnessOK = false;
		} else if (!StringUtils.equals("Y", (String) SOT701.get("obuFlag"))) {
			isFitnessOK = true;
		} else {
			isFitnessOK = true;
		}
		
		if (!isFitnessOK) {
			outputVO.put("PROD_ID", prodDTL.get("PRD_ID"));
			outputVO.put("FundErrId", "ehl_01_SOT702_003");
			outputVO.put("MessageType", "error");
			outputVO = messageAddFundErrId(outputVO);
		}
		
		return outputVO;
	}
	
	/**
	 * FitnessFund004 商品申購台幣信託註記檢核
	 * 若商品申購類型為台幣信託且台幣信託註記為N 
	 * @param outputVO
	 * @param custDTL
	 * @param prodDTL
	 * @param trustCurrType N台幣 Y外幣
	 * @param tradeType
	 * @param tradeDate
	 * @param SOT701
	 * @return
	 */
	private Map<String, Object> FitnessFund004 (Map<String, Object> outputVO, 
												Map<String, Object> custDTL, 
												Map<String, Object> prodDTL,
												String trustCurrType,
												String tradeType, 
												Date tradeDate,
												Map<String, Object> SOT701) {

		boolean isFitnessOK = false;
	
		//trustCurrType:N台幣信託
		if (StringUtils.equals("N", trustCurrType) && StringUtils.equals("N", (String) prodDTL.get("BUY_TWD"))) {
			isFitnessOK = false;
		} else if (!StringUtils.equals("N", (String) prodDTL.get("BUY_TWD"))) {
			isFitnessOK = true;
		} else { 
			isFitnessOK = true;
		}
	
		if (!isFitnessOK) {
			outputVO.put("PROD_ID", prodDTL.get("PRD_ID"));
			outputVO.put("FundErrId", "ehl_01_SOT702_004");
			outputVO.put("MessageType", "error");
			outputVO = messageAddFundErrId(outputVO);
		}
	
		return outputVO;
	}
	
	/*
	 * FitnessFund005 商品申購額度控管檢核
	 * 本次交易中有基金申購額度控管，交易是否成功依鍵入作業系統時之剩餘額度而定。基金代碼為：XXXX
	 */
	private Map<String, Object> FitnessFund005 (Map<String, Object> outputVO, 
												Map<String, Object> custDTL, 
												Map<String, Object> prodDTL, 
												String tradeType, 
												Date tradeDate,
												Map<String, Object> SOT701) {
	
		boolean isFitnessOK = false;
	
		if (((BigDecimal) prodDTL.get("QUOTAS")).compareTo(new BigDecimal(0)) == 1) {
			isFitnessOK = false;
		} else {
			isFitnessOK = true;
		}
	
		if (!isFitnessOK) {
			outputVO.put("PROD_ID", prodDTL.get("PRD_ID"));
			outputVO.put("FundErrId", "ehl_01_SOT702_005");
			outputVO.put("MessageType", "warning");
			//ehl_01_SOT702_#param1^param2
			outputVO.put("Params", prodDTL.get("PRD_ID"));//param1^param2
			outputVO = messageAddFundErrId(outputVO);
		}
		
		return outputVO;
	}
	
	/*
	 * FitnessFund006 基金報酬揭露檢核
	 * ehl_01_SOT702_006 基金報酬揭露資訊尚未覆核
	 */
	private Map<String, Object> FitnessFund006 (Map<String, Object> outputVO, 
												Map<String, Object> custDTL, 
												Map<String, Object> prodDTL, 
												String tradeType, 
												Date tradeDate,
												Map<String, Object> SOT701) {

		boolean isFitnessOK = false;

		if (StringUtils.equals("N", (String) prodDTL.get("FLAG"))) {
			isFitnessOK = false;
		} else {
			isFitnessOK = true;
		}

		if (!isFitnessOK) {
			outputVO.put("PROD_ID", prodDTL.get("PRD_ID"));
			outputVO.put("FundErrId", "ehl_01_SOT702_006");
			outputVO.put("MessageType", "error");
			outputVO = messageAddFundErrId(outputVO);
		}

		return outputVO;
	}
	
	/*
	 * FitnessFund007 高收益基金檢核
	 * 
	 ehl_01_SOT702_007 請提醒客戶: 基金代碼 XXXX
1.本類基金主要係投資於非投資等級之高風險債券且配息來源可能為本金，投資本類基金不宜占投資組合過高的比重。
2.屬退休人士之委託人需確認已充分了解本類基金之各項投資風險。
3.本類基金可能投資於符合美國Rule 144A規定具有私募性質之債券，雖其投資總金額不得超過基金淨資產價值之百分之十，惟本類基金之相關風險，均由委託人自行承擔。

	 */
	private Map<String, Object> FitnessFund007 (Map<String, Object> outputVO, 
												Map<String, Object> custDTL, 
												Map<String, Object> prodDTL, 
												String tradeType, 
												Date tradeDate,
												Map<String, Object> SOT701) {

		boolean isFitnessOK = false;

		if (StringUtils.equals("Y", (String) prodDTL.get("HIGH_YIELD"))) {
			isFitnessOK = false;
		} else {
			isFitnessOK = true;
		}

		if (!isFitnessOK) {
			outputVO.put("PROD_ID", prodDTL.get("PRD_ID"));
			outputVO.put("FundErrId", "ehl_01_SOT702_007");
			outputVO.put("MessageType", "warning");
			outputVO.put("Params", prodDTL.get("PRD_ID"));//param1^param2
			outputVO = messageAddFundErrId(outputVO);
		}

		return outputVO;
	}
	
//	/*
//	 * FitnessFund008 預約交易檢核
//	 */
//	private Map<String, Object> FitnessFund008 (Map<String, Object> outputVO, 
//												Map<String, Object> custDTL, 
//												Map<String, Object> prodDTL, 
//												String tradeType, 
//												Date tradeDate,
//												Map<String, Object> SOT701, 
//												Date effectDate) {
//
//		boolean isFitnessOK = false;
//		
//		if (StringUtils.equals("Y", ((BigDecimal) custDTL.get("CNT")).compareTo(new BigDecimal(0)) == 1 ? "Y" : "N")) {
//			isFitnessOK = false;
//		} else {
//			isFitnessOK = true;
//		}
//
//		if (!isFitnessOK) {
//			outputVO.put("PROD_ID", prodDTL.get("PRD_ID"));
//			outputVO.put("FundErrId", "ehl_01_SOT702_008");
//			outputVO.put("MessageType", "warning");
//			outputVO = messageAddFundErrId(outputVO);
//		}
//
//		return outputVO;
//	}
	
	/**
	 * FitnessFund009 客戶風險屬性檢核
	 *  ehl_01_SOT702_009 無客戶風險屬性，無法適配
	 */
	public Map<String, Object> FitnessFund009 (Map<String, Object> outputVO, 
											   Map<String, Object> prodDTL,
											   Map<String, Object> SOT701) throws Exception {

		boolean isFitnessOK = false;

		if (StringUtils.isBlank((String)SOT701.get("kycLV"))) {   //這欄是空的(String) 請參考SOT110.java SOT701.put("kycLV", inputVO.getKycLV());
			isFitnessOK = false;
		} else {
			isFitnessOK = true;
		}

		if (!isFitnessOK) {
			outputVO.put("PROD_ID", prodDTL.get("PRD_ID"));
			outputVO.put("FundErrId", "ehl_01_SOT702_009");//無客戶風險屬性，無法適配
			outputVO.put("MessageType", "error");
			outputVO = messageAddFundErrId(outputVO);
			
			String errorKycLV = "傳入適配檢核的kyc等級是空值。"; 
			Exception errKyc = new Exception(errorKycLV);
			logger.error(errorKycLV + " SOT702 FitnessFund009:  SOT701.kycLV 為空", errKyc);
		}

		return outputVO;
	}
	
	/*
	 * FitnessFund010 OBU客戶註記檢核
	 * ehl_01_SOT702_010 非專業投資人不得購買此商品
	 */
	private Map<String, Object> FitnessFund010 (Map<String, Object> outputVO, 
												Map<String, Object> custDTL, 
												Map<String, Object> prodDTL, 
												String tradeType, 
												Date tradeDate,
												Map<String, Object> SOT701) {
	
		boolean isFitnessOK = false;
		
		if (StringUtils.equals("1", (String) prodDTL.get("OBU_PRO")) && !StringUtils.equals("Y", (String) SOT701.get("custProFlag"))) {
			isFitnessOK = false;
		} else if (!StringUtils.equals("1", (String) prodDTL.get("OBU_PRO"))){
			isFitnessOK = true;
		} else {
			isFitnessOK = true;
		}
	
		if (!isFitnessOK) {
			outputVO.put("PROD_ID", prodDTL.get("PRD_ID"));
			outputVO.put("FundErrId", "ehl_01_SOT702_010");
			outputVO.put("MessageType", "error");
			outputVO = messageAddFundErrId(outputVO);
		}
	
		return outputVO;
	}
	 
	
	/*
	 * FitnessFund011_013 FATCA客戶註記檢核(電文fc032275 & PS_FP_NFNJNBBMP0.NBB03)
	 */
	/**
	 * @param outputVO
	 * @param SOT701
	 * @param prodDTL   TBPRD_NATIONALITY.COUNTRY_ID
	 * @return
	 */
	public Map<String, Object> FitnessFund011_013 (Map<String, Object> outputVO ,Map<String, Object> SOT701,Map<String, Object> prodDTL) throws JBranchException {
		String facaType = (String) SOT701.get("fatcaType");

		if (StringUtils.isNotBlank(facaType) && facaType.matches("N|Y|X")) {
			outputVO.put("MessageType", "error");

			if (StringUtils.equals("N", facaType) || CollectionUtils.isNotEmpty(
					((SOT712) PlatformContext.getBean("sot712"))
							.identifyEtfStockExchange((String)prodDTL.get("PRD_ID")))) {
				outputVO.put("FundErrId", "ehl_01_SOT702_011");
				outputVO.put("Message", "ehl_01_SOT702_011");
			} else if (StringUtils.equals("Y", facaType)) {
				outputVO.put("FundErrId", "ehl_01_SOT702_012");
				outputVO.put("Message", "ehl_01_SOT702_012");
			} else if (StringUtils.equals("X", facaType)) {
				outputVO.put("FundErrId", "ehl_01_SOT702_013");
				outputVO.put("Message", "ehl_01_SOT702_013");
			}
			logger.debug("FitnessFund011_013:"+outputVO.toString());
		}
		return outputVO;
	}
	
	/*
	 * FitnessFund014 OBU商品年限檢核
	 *  ehl_01_SOT702_014 年限檢測XX不適;年限檢測:客戶年齡超過70歲，不可購買此商品
	 */
	private Map<String, Object> FitnessFund014 (Map<String, Object> outputVO, 
													Map<String, Object> custDTL, 
													Map<String, Object> prodDTL, 
													String tradeType, 
													Date tradeDate,
													Map<String, Object> SOT701) {

		boolean isFitnessOK = false;

		if (StringUtils.equals("Y", (String) prodDTL.get("OBU_AGE")) && !StringUtils.equals("Y", (String) SOT701.get("ageUnder70Flag"))) {
			isFitnessOK = false;
		} if (!StringUtils.equals("Y", (String) prodDTL.get("OBU_AGE"))) {
			isFitnessOK = true;
	    } else {
			isFitnessOK = true;
		}
		
		if (!isFitnessOK) {
			outputVO.put("PROD_ID", prodDTL.get("PRD_ID"));
			outputVO.put("FundErrId", "ehl_01_SOT702_014");
			outputVO.put("MessageType", "error");
			outputVO.put("Params", prodDTL.get("PRD_ID"));//param1^param2
			outputVO = messageAddFundErrId(outputVO);
		}
		
		return outputVO;
	}
	
	/*
	 * TODO
	 * 
	 */
	/**
	 * FitnessFund015 客戶風險屬性檢核
	 * ehl_01_SOT702_015商品風險TBPRD_FUND_VO.RISKCATE_ID不適
	 * @param outputVO
	 * @param custDTL
	 * @param prodDTL
	 * @param SOT701
	 * @param custAndProdRiskFitOK 
	 * @return
	 */
	public Map<String, Object> FitnessFund015 (Map<String, Object> outputVO, 
												Map<String, Object> prodDTL,
												Map<String, Object> SOT701,
												boolean custAndProdRiskFitOK) {

		boolean isFitnessOK = false;
		
		String prodRisk = (String)prodDTL.get("RISKCATE_ID"); 
		if(!prodRisk.matches("P1|P2|P3|P4")) {
			outputVO.put("PROD_ID", prodDTL.get("PRD_ID"));
			outputVO.put("FundErrId", "ehl_01_SOT702_015");
			outputVO.put("MessageType", "error");
			outputVO.put("Params", prodDTL.get("PRD_ID"));//param1^param2
			outputVO = messageAddFundErrId(outputVO);
			 
			Exception errProdRisk = new Exception(String.format("SOT702 FitnessFund015 商品主檔錯誤 RISKCATE_ID:%s", prodRisk));
			logger.error("SOT702 FitnessFund015 Error", errProdRisk);
			return outputVO;
		}

		//若商品風險等級與客戶風險等級不適配		
		if (!custAndProdRiskFitOK) {
			isFitnessOK = false;
		} else {
			isFitnessOK = true;
		}
		
		if (!isFitnessOK) {
			outputVO.put("PROD_ID", prodDTL.get("PRD_ID"));
			outputVO.put("FundErrId", "ehl_01_SOT702_015");
			outputVO.put("MessageType", "error");
			outputVO.put("Params", prodDTL.get("PRD_ID"));//param1^param2
			outputVO = messageAddFundErrId(outputVO);
			logger.error("SOT702 FitnessFund015商品風險等級與客戶風險等級不適配");
		}

		return outputVO;
	}
	
	/*
	 * FitnessFund016 商品建議售出註記檢核
	 *  ehl_01_SOT702_016  (XXXX)為本行建議售出之標的，請提醒客戶並確認其申購意願，謝謝！
	 */
	private Map<String, Object> FitnessFund016 (Map<String, Object> outputVO, 
												Map<String, Object> custDTL, 
												Map<String, Object> prodDTL, 
												String tradeType, 
												Date tradeDate,
												Map<String, Object> SOT701) {

		boolean isFitnessOK = false;

		if (StringUtils.equals("Y", (String) prodDTL.get("SELLING"))) {
			isFitnessOK = false;
		} else {
			isFitnessOK = true;
		}

		if (!isFitnessOK) {
			outputVO.put("PROD_ID", prodDTL.get("PRD_ID"));
			outputVO.put("FundErrId", "ehl_01_SOT702_016");
			outputVO.put("MessageType", "warning");
			outputVO.put("Params", prodDTL.get("PRD_ID"));//param1^param2
			outputVO = messageAddFundErrId(outputVO);
		}

		return outputVO;
	} 
	 
	/**
	 * FitnessFund017 商品未核備檢核: OBU 可以買未核備, DBU不能買未核備商品
	 *  ehl_01_SOT702_01˙  (XXXX)FUS40 Y:未核備商品！
	 */
	private Map<String, Object> FitnessFund017 (Map<String, Object> outputVO, 
												Map<String, Object> custDTL, 
												Map<String, Object> prodDTL, 
												String tradeType, 
												Date tradeDate,
												Map<String, Object> SOT701) {

		boolean isFitnessOK = false;

		//DBU不能買未核備商品
		if (!StringUtils.equals("Y", (String) SOT701.get("obuFlag")) && StringUtils.equals("Y", (String) prodDTL.get("FUS40"))) {
			isFitnessOK = false;
		} else { //OBU 可以買未核備
			isFitnessOK = true;
		}

		if (!isFitnessOK) {
			outputVO.put("PROD_ID", prodDTL.get("PRD_ID"));
			outputVO.put("FundErrId", "DBU不能買未核備商品ehl_01_SOT702_017");
			outputVO.put("MessageType", "error");
			//outputVO.put("Params", prodDTL.get("PRD_ID"));//param1^param2
			outputVO = messageAddFundErrId(outputVO);
		}

		return outputVO;
	}
	/**
	 * 將上一個Message和目前FundErrId用逗號合成新的Message: FundErrId1,FundErrId2,FundErrId3
	 * ;給 FitnessMessageDialog顯示多筆warning訊息
	 * @param outputVO
	 * @return
	 */
	private Map<String, Object> messageAddFundErrId(Map<String, Object> outputVO) {
		String befMessage = (outputVO.get("Message") == null || StringUtils
				.isBlank(outputVO.get("Message").toString())) ? "" : outputVO.get("Message") + ",";
		
		//ehl_01_SOT702_#param1^param2
		String aftMessage = (String)outputVO.get("FundErrId");
		if(outputVO.get("Params")!=null && StringUtils.isNotBlank((String)outputVO.get("Params"))) {
			String Params = (String)outputVO.get("Params");
			aftMessage += "#" + Params;
		}
		outputVO.put("Message", befMessage + aftMessage);
		return outputVO;
	}
	
	
}
