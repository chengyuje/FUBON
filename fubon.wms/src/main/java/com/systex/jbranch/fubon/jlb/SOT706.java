package com.systex.jbranch.fubon.jlb;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.systex.jbranch.app.common.fps.table.TBPRD_FUNDVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.info.XmlInfo;

public class SOT706 extends BizLogic {
	private Logger logger = LoggerFactory.getLogger(SOT706.class);
	/*
	 * prodID : 海外債、SI=SD、SN 商品代號
	 * SOT701 : 客少基本資料與帳號
	 */
	public Map<String, Object> checkFitness (DataAccessManager dam, String prodID, Map<String, Object> SOT701, String pType) throws JBranchException {
		
		Map<String, Object> outputVO = new HashMap<String, Object>();
		
		try {
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT MAST.CUST_ID, MAST.AGE, NOTE.CURT_FATCA_ID, MAST.BIRTH_DATE ");
			sb.append("FROM TBCRM_CUST_MAST MAST ");
			sb.append("LEFT JOIN TBCRM_CUST_NOTE NOTE ON MAST.CUST_ID = NOTE.CUST_ID ");
			sb.append("WHERE MAST.CUST_ID = :custID ");
			queryCondition.setQueryString(sb.toString());
			queryCondition.setObject("custID", ((String) SOT701.get("CUST_ID")).toUpperCase());
			List<Map<String, Object>> custDTLList = dam.exeQuery(queryCondition);
			if (StringUtils.equals("BND", pType)) {
				pType = "BOND";
			}
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			sb.append("SELECT PROD.PRD_ID, ");
			sb.append("'").append(pType).append("' AS PTYPE, ");
			sb.append("PROD.DATE_OF_MATURITY, ");
			sb.append("PROD.RISKCATE_ID, ");
			sb.append((StringUtils.equals("SI", pType) || StringUtils.equals("SN", pType) ? "PROD.GLCODE, " : "'' AS GLCODE, "));
			sb.append((StringUtils.equals("SI", pType) ? "PROD.STAKEHOLDER,  " : "'' AS STAKEHOLDER, "));
			sb.append((StringUtils.equals("SI", pType) ? "PROD.PI_BUY,  " : "'' AS PI_BUY, "));//商品是否限專業投資人
			
			sb.append(" VO.COUNTRY_ID "); //SOT702.FitnessBond011_013FATCA客戶註記檢核  use COUNTRY_ID 
			sb.append("FROM TBPRD_").append(pType).append(" PROD ");
			sb.append(" LEFT JOIN TBPRD_NATIONALITY VO ON PROD.PRD_ID = VO.PROD_ID ");
			sb.append("WHERE PROD.PRD_ID = :prodID ");
			queryCondition.setQueryString(sb.toString());
			queryCondition.setObject("prodID", prodID);
			List<Map<String, Object>> prodDTLList = dam.exeQuery(queryCondition);
			
			
			
			
			outputVO.put("PROD_ID", "");
			outputVO.put("FundErrId", "");
			outputVO.put("MessageType", "");
			outputVO.put("Message", "");
			SOT702 sot702 = new SOT702();
			outputVO = sot702.FitnessFund011_013(outputVO, SOT701, prodDTLList.get(0));//FATCA客戶註記檢核
			String msgType = (String) outputVO.get("MessageType");
			if (StringUtils.isNotBlank(msgType) && StringUtils.equals("error", msgType)) {
				throw new APException((String) outputVO.get("FundErrId"));
			}
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
				
				
				logger.info(String.format("SOT706.checkFitness() custDTLList: %s \n prodDTLList:%s \n SOT701: %s \n", custDTLList.get(0), prodDTLList.get(0), SOT701));
//				if (!StringUtils.equals("error", (String) outputVO.get("MessageType"))) {
//					//FitnessBond000  SD SN不能為空的欄位: 商品主檔_到期日期、產品Q值、利害關係人是否可申購、產品國籍檢核檔_國別代碼
//					outputVO = FitnessBond000(outputVO, custDTLList.get(0), prodDTLList.get(0), SOT701);
//				}
				if (!StringUtils.equals("error", (String) outputVO.get("MessageType"))) {
					//債80、70年期檢核 (SI、SN)
					outputVO = FitnessBond001_002(outputVO, custDTLList.get(0), prodDTLList.get(0), SOT701);
				}

				if (!StringUtils.equals("error", (String) outputVO.get("MessageType"))) {
					//債券年限檢核
					outputVO = FitnessBond006_008(dam, outputVO, custDTLList.get(0), prodDTLList.get(0), SOT701);
				}

				if (!StringUtils.equals("error", (String) outputVO.get("MessageType"))) {
					//SD、SN商品適格等級
					outputVO = FitnessBond003(outputVO, custDTLList.get(0), prodDTLList.get(0), SOT701);
				}

				if (!StringUtils.equals("error", (String) outputVO.get("MessageType"))) {
					//SD商品類別 利害關係人交易
					outputVO = FitnessBond004_005(outputVO, custDTLList.get(0), prodDTLList.get(0), SOT701);
				}
				
				if (!StringUtils.equals("error", (String) outputVO.get("MessageType"))) {
					//ehl_01_SOT704_002 非專業投資人不得購買此商品  同ETF檢核 (PTYPE=SI商品也要檢核)
					outputVO = this.FitnessETF704_002 (outputVO, prodDTLList.get(0), SOT701);
				}
				
				/**請參考SOT310 :  SOT701.put("kycLV", inputVO.getKycLV());**/
				if (!StringUtils.equals("error", (String) outputVO.get("MessageType"))) {
					//客戶風險屬性檢核
					outputVO = sot702.FitnessFund009(outputVO, prodDTLList.get(0), SOT701);
				}
				if (!StringUtils.equals("error", (String) outputVO.get("MessageType"))) {
					//FitnessFund015 客戶風險屬性檢核
					outputVO = sot702.FitnessFund015(outputVO, prodDTLList.get(0), SOT701 , custAndProdRiskFitOK);
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
				logger.error(String.format("錯誤,SOT706適配檢核結果  cust %s , prod %s , errId %s , msgType %s , msg %s",
								SOT701.get("CUST_ID"), prodID, outputVO.get("FundErrId"), outputVO.get("MessageType"), outputVO.get("Message")));
			}
		} catch (Exception e) {
			logger.error(String.format("SOT706.checkFitness() prodID:%s； pType:%s ； SOT701:%s  error:%s", prodID, pType, SOT701.toString(), e.getMessage()), e);
			throw new JBranchException(String.format("適配異常 商品代號:%s error:%s ", prodID, e.getMessage()));
		}
		return outputVO;
	}
	
//	/*
//	 * FitnessBond011_013 FATCA客戶註記檢核(電文fc032275 & PS_FP_NFNJNBBMP0.NBB03)
//	 */
//	public Map<String, Object> FitnessBond011_013 (Map<String, Object> outputVO, 
//												   Map<String, Object> custDTL, 
//												   Map<String, Object> prodDTL, 
//												   Map<String, Object> SOT701) {
//			
//		boolean isFitnessOK = false;
//		
//		if (!StringUtils.equals("", (String) SOT701.get("fatcaType"))) {
//			isFitnessOK = false;
//		} else {
//			isFitnessOK = true;
//		}
//		
//		if (!isFitnessOK) {
//			outputVO.put("PROD_ID", prodDTL.get("PRD_ID"));
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
//
//		}
//		
//		return outputVO;
//	}

	/**
	 * FitnessBond001_002債80、70年期檢核  (SI、SN)
	 */
	private Map<String, Object> FitnessBond001_002 (Map<String, Object> outputVO, 
												    Map<String, Object> custDTL, 
												    Map<String, Object> prodDTL, 
												    Map<String, Object> SOT701) throws Exception{
		
		try {
			boolean isFitnessOK = false;
			boolean isErrorA = false;
			boolean isErrorB = false;
			
			boolean isSDSN = (StringUtils.equals("SN", (String) prodDTL.get("PTYPE")) || StringUtils.equals("SI", (String) prodDTL.get("PTYPE")) ? true : false);
			if (isSDSN) {
				BigDecimal custAge = (BigDecimal) custDTL.get("AGE");
				Date dateOfMaturity = (Timestamp) prodDTL.get("DATE_OF_MATURITY");
				//		BigDecimal totalYearToMaturity = (new BigDecimal(dateOfMaturity.getTime()).subtract(new BigDecimal((new Date()).getTime()))).divide(new BigDecimal("365"),1,BigDecimal.ROUND_HALF_UP);
				BigDecimal totalYearToMaturity = (new BigDecimal(dateOfMaturity.getTime()).subtract(new BigDecimal((new Date()).getTime()))).divide(new BigDecimal(3600*24*1000),1,BigDecimal.ROUND_HALF_UP).divide(new BigDecimal("365"),1,BigDecimal.ROUND_HALF_UP);
				
				/*
				TOTALYEARTOMATURITY  等同剩餘年限?之前伯瑞是說是用 到期日期計算，公式為：到期日(DateOfMaturity) - 系統日 = 天數，再除以365 = XX年。
				*/
				logger.debug("custAge:"+custAge);
				logger.debug("dateOfMaturity:"+dateOfMaturity);
				logger.debug("FitnessBond001_002  totalYearToMaturity:"+totalYearToMaturity);
				
				String residual = totalYearToMaturity.compareTo(new BigDecimal("1")) >= 0 ? "Y" : "N";
				BigDecimal custRole = checkCustRole(SOT701); 
				
				if (custRole.compareTo(new BigDecimal("2")) == 1 && 
					isSDSN && 
					custAge.compareTo(new BigDecimal("70")) >= 0 && 
					StringUtils.equals("Y", (String) SOT701.get("custProFlag")) && 
					custAge.compareTo(new BigDecimal("80")) >= 0 && 
					StringUtils.equals("Y", residual)) {
					isErrorA = true;
					isFitnessOK = false;
				} else if (custRole.compareTo(new BigDecimal("2")) == 1 && 
						   isSDSN && 
						   custAge.compareTo(new BigDecimal("70")) >= 0 && 
						   !StringUtils.equals("Y", (String) SOT701.get("custProFlag")) && 
						   StringUtils.equals("Y", residual)) {
					isErrorB = true;
					isFitnessOK = false;
				} else {
					isFitnessOK = true;
				}
			} else {
				isFitnessOK = true;
			}
			if (!isFitnessOK) {
				outputVO.put("PROD_ID", prodDTL.get("PRD_ID"));
				outputVO.put("MessageType", "error");
				
				if (isErrorA) {
					outputVO.put("FundErrId", "ehl_01_SOT706_001");
					outputVO.put("Message", "ehl_01_SOT706_001");
				} else if (isErrorB) {
					outputVO.put("FundErrId", "ehl_01_SOT706_002");
					outputVO.put("Message", "ehl_01_SOT706_002");
				}
			}
			
			return outputVO;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/** 
	 * 
	 * FitnessBond003 SD、SN商品適格等級
	 */
	private Map<String, Object> FitnessBond003 (Map<String, Object> outputVO, 
											    Map<String, Object> custDTL, 
											    Map<String, Object> prodDTL, 
											    Map<String, Object> SOT701) {
		
		boolean isFitnessOK = false;
		boolean isSISD = StringUtils.equals("SI", (String) prodDTL.get("PTYPE")) ||  StringUtils.equals("SN", (String) prodDTL.get("PTYPE")) ? true : false;
		
		if (isSISD) {
			BigDecimal prodQ = new BigDecimal("0");
			if (StringUtils.equals("SI", (String) prodDTL.get("PTYPE"))) {
				if( prodDTL.get("GLCODE") != null && StringUtils.isNotBlank((String)prodDTL.get("GLCODE"))){
					prodQ =  new BigDecimal(StringUtils.substring((String) prodDTL.get("GLCODE"), 1));
				}
			} else if (StringUtils.equals("SN", (String) prodDTL.get("PTYPE"))) {
				if( prodDTL.get("GLCODE")  != null && StringUtils.isNotBlank((String)prodDTL.get("GLCODE"))){
					prodQ = new BigDecimal(StringUtils.substring((String) prodDTL.get("GLCODE"), 1));
				}
			}
			
			BigDecimal custQ = null;
			if(SOT701.get("custQValue") == null){
				logger.error(String.format("custQValue is NULL(check SOT701InputVO.prodType): %s", SOT701.get("CUST_ID")));
				isFitnessOK = false;
			}else{
				custQ = new BigDecimal(StringUtils.substring((String) SOT701.get("custQValue"), 1));
				if (!StringUtils.equals("Y", (String) SOT701.get("custProFlag")) && prodQ.compareTo(custQ) > 0) {
					isFitnessOK = false;
				} else {
					isFitnessOK = true;
				}
			}
			

			if (!isFitnessOK) {
				outputVO.put("PROD_ID", prodDTL.get("PRD_ID"));
				outputVO.put("MessageType", "error");
				outputVO.put("FundErrId", "ehl_01_SOT706_003");
				outputVO.put("Message", "ehl_01_SOT706_003");
			}
		}
		
		return outputVO;
	}
	
	/**
	 * 
	 * FitnessBond004_005 SD商品類別 利害關係人交易
	 */
	private Map<String, Object> FitnessBond004_005 (Map<String, Object> outputVO, 
												    Map<String, Object> custDTL, 
												    Map<String, Object> prodDTL, 
												    Map<String, Object> SOT701) {

		boolean isFitnessOK = false;
		boolean isErrorA = false;
		boolean isErrorB = false;
		boolean isSD = StringUtils.equals("SI", (String) prodDTL.get("PTYPE")) ? true : false;
		
		if(!isSD){
			isFitnessOK = true;
		}else if(isSD){
			boolean isStakeholder = (boolean) SOT701.get("isCustStakeholder"); 
			if (isSD && isStakeholder && (checkCustRole(SOT701)).compareTo(new BigDecimal("2")) == 1 && !StringUtils.equals("Y", (String) prodDTL.get("STAKEHOLDER"))) {
				isFitnessOK = false;
				isErrorA = true;
			} else if (isSD && isStakeholder && (checkCustRole(SOT701)).compareTo(new BigDecimal("3")) == -1) {
				isFitnessOK = false;
				isErrorB = true;
			} else {
				isFitnessOK = true;
			}
		}
		if (!isFitnessOK) {
			outputVO.put("PROD_ID", prodDTL.get("PRD_ID"));
			outputVO.put("MessageType", "error");
			
			if (isErrorA) {
				outputVO.put("FundErrId", "ehl_01_SOT706_004");
				outputVO.put("Message", "ehl_01_SOT706_004");
			} else if (isErrorB) {
				outputVO.put("FundErrId", "ehl_01_SOT706_005");
				outputVO.put("Message", "ehl_01_SOT706_005");
			}
		}
		
		return outputVO;
	}
	
	/**
	 * FitnessBond006_008債券年限檢核
	 */
	private Map<String, Object> FitnessBond006_008 (DataAccessManager dam,
													Map<String, Object> outputVO, 
												    Map<String, Object> custDTL, 
												    Map<String, Object> prodDTL, 
												    Map<String, Object> SOT701) throws JBranchException {

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		boolean isFitnessOK = false;
		boolean isErrorA = false;
		boolean isErrorB = false;
		
		String pType = (String) prodDTL.get("PTYPE");
		BigDecimal custAge = (BigDecimal) custDTL.get("AGE");
		Date birthDate = (Timestamp) custDTL.get("BIRTH_DATE");
		Date dateOfMaturity = (Timestamp) prodDTL.get("DATE_OF_MATURITY");
		BigDecimal prodAGE = new BigDecimal("0");
		boolean isBondProdType = (StringUtils.equals("SN", pType)) ? true : false;
		boolean isFProdType = StringUtils.equals("F", pType) ? true : false;
		boolean isWmdcdWmdci = (StringUtils.equals("WMDCD", (String) prodDTL.get("PROD_ID")) || StringUtils.equals("WMDCI", (String) prodDTL.get("PROD_ID"))) ? true : false;
		
		if (null != dateOfMaturity && null != birthDate) {
			  String maturityMMDD = sdf.format(dateOfMaturity).substring(4, 6) + sdf.format(dateOfMaturity).substring(6);
			  String birthdayMMDD = sdf.format(birthDate).substring(4, 6) + sdf.format(birthDate).substring(6);
			  prodAGE = new BigDecimal(maturityMMDD.substring(0, 4)).subtract(new BigDecimal(birthdayMMDD.substring(0, 4)));
			  if (maturityMMDD.compareTo(birthdayMMDD) == -1) { //不足年要減1
				  prodAGE = prodAGE.subtract(new BigDecimal("1"));
			  }
		}
		
		BigDecimal limitValue = new BigDecimal("70");
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT PARAM_CODE AS LIMIT_VALUE ");
		sb.append("FROM TBSYSPARAMETER ");
		sb.append("WHERE PARAM_TYPE = 'SOT.BN_AGE_LIMIT' ");
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() > 0) {
			limitValue = new BigDecimal((String) list.get(0).get("LIMIT_VALUE"));
		}
		
		if (isBondProdType && 
			isWmdcdWmdci && 
			StringUtils.equals("Y", (String) SOT701.get("custProFlag")) && 
			custAge.compareTo(limitValue) >= 0 && 
			custAge.compareTo(new BigDecimal("80")) == -1) {
			isFitnessOK = false;
			isErrorA = true;
		} else if (isBondProdType && 
				   null != dateOfMaturity && 
				   prodAGE.compareTo(limitValue) >= 0 && 
				   (prodAGE.subtract(custAge)).compareTo(new BigDecimal("0")) >= 0) {
			isFitnessOK = false;
			isErrorA = true;
		} else if (isBondProdType && 
				   null == dateOfMaturity) {
			isFitnessOK = false;
			isErrorB = true;
		} else {
			isFitnessOK = true;
		}
		
		if (!isFitnessOK) { 
			outputVO.put("PROD_ID", prodDTL.get("PRD_ID"));
			outputVO.put("MessageType", "error");
			
			if (isErrorA) {
				outputVO.put("FundErrId", "ehl_01_SOT706_006");
				outputVO.put("Message", "ehl_01_SOT706_006");
			} else if (isErrorB) {
				outputVO.put("FundErrId", "ehl_01_SOT706_007");
				outputVO.put("Message", "ehl_01_SOT706_007");
			}
		}
		
		return outputVO;
	}
	
	/** 
	 * ehl_01_SOT704_002 非專業投資人不得購買此商品  (PTYPE=SI商品也要檢核)
	 * @param prodDTL
	 * @param SOT701
	 * @return
	 */
	private Map<String, Object> FitnessETF704_002 (Map<String, Object> outputVO,Map<String, Object> prodDTL, 
												   	   Map<String, Object> SOT701) { 
		boolean isFitnessOK = false; 
		boolean isErrorB = false;
		
		boolean isSI = (StringUtils.equals("SI", (String) prodDTL.get("PTYPE")) ? true : false);
		 
		if (isSI && StringUtils.equals("Y", (String) prodDTL.get("PI_BUY")) && 
				   !StringUtils.equals("Y", (String) SOT701.get("custProFlag"))) {
			isErrorB = true;
			isFitnessOK = false;
		} else {
			isFitnessOK = true;
		}

		if (!isFitnessOK) {
			outputVO.put("PROD_ID", prodDTL.get("PRD_ID"));
			outputVO.put("MessageType", "error"); 
			if (isErrorB) {
				logger.debug("ehl_01_SOT704_002 非專業投資人不得購買此商品  (SI債卷商品也要)");
				outputVO.put("FundErrId", "ehl_01_SOT704_002");
				outputVO.put("Message", "ehl_01_SOT704_002");
			}
		}

		return outputVO;
	}

	private BigDecimal checkCustRole (Map<String, Object> SOT701) {
		
		if (((String) SOT701.get("CUST_ID")).length() >= 10) {
			if (StringUtils.equalsIgnoreCase("Y", ((String) SOT701.get("obuFlag")))) {
				return new BigDecimal("4"); //OBU: 外國自然人
			} else {
				return new BigDecimal("3"); //DBU: 本國自然人
			}
		} else {
			if (StringUtils.equalsIgnoreCase("Y", ((String) SOT701.get("obuFlag")))) {
				return new BigDecimal("2"); //OBU: 外國法人
			} else {
				return new BigDecimal("1"); //DBU: 本國法人
			}
		}
	}
	
	/**
	 * 檢查欄位是否為空
	 * @param prodDTL
	 * @param column
	 * @return
	 */
	private boolean isEmpty(Map<String, Object> prodDTL, String column) {
		if (prodDTL.get(column) == null
				|| StringUtils.isBlank(prodDTL.get(column).toString()))
			return true;
		else
			return false;
	}
}
