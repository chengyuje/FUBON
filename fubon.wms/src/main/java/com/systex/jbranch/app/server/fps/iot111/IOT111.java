package com.systex.jbranch.app.server.fps.iot111;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * IOT111
 * 
 */
@Component("iot111")
@Scope("request")
public class IOT111 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;

	/***
	 * 取得要保人購買檢核查詢資料
	 * @param body
	 * @param header
	 * @throws Exception 
	 */
	public void queryData(Object body, IPrimitiveMap header) throws Exception {
		
		IOT111InputVO inputVO = (IOT111InputVO) body;
		IOT111OutputVO return_VO = new IOT111OutputVO();
		
		XmlInfo xmlInfo = new XmlInfo();
		String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行人員
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append(" SELECT DISTINCT A.*, ");
		sql.append("        B.EMP_NAME as RECRUIT_NAME, ");
		sql.append("        C.EMP_NAME AS SIGNOFF_NAME, ");
		sql.append("        CASE WHEN A.FB_COM_YN = 'N' THEN J.PRODUCTNAME ELSE M.INSPRD_NAME END AS INSPRD_NAME, ");
		sql.append("        CASE WHEN A.FB_COM_YN = 'N' THEN J.CURRENCYRATE ELSE M.EXCH_RATE END AS EXCH_RATE, ");
		sql.append("        M.AB_EXCH_RATE, ");
		sql.append("        M.INSPRD_ANNUAL, ");
		sql.append("        M.PAY_TYPE, ");
		sql.append("        M.PRD_RATE, ");
		sql.append("        M.CNR_RATE, ");
		sql.append("        E.CUST_RISK_ATR, ");
		sql.append("        (CASE WHEN D.PREMATCH_SEQ IS NULL THEN 'N' ELSE 'Y' END) AS IS_PRINT_YN, ");
		sql.append("        I.STATUS AS CALLOUT_STATUS, ");
		sql.append("        TO_CHAR(I.APPLY_DATE, 'YYYY/MM/DD') AS CALL_DATE, ");
		sql.append("        B.DEPT_ID, ");
		sql.append("        K.DEPT_NAME, ");
		sql.append("        M.CURR_CD, ");
		sql.append("        M.INSPRD_TYPE, ");
		sql.append("        I.C_CALL_TYPE, ");
		sql.append("        I.I_CALL_TYPE, ");
		sql.append("        I.P_CALL_TYPE, ");
		sql.append("        I.CALL_PERSON, ");
		sql.append("        I.C_PREMIUM_TRANSSEQ, ");
		sql.append("        I.I_PREMIUM_TRANSSEQ, ");
		sql.append("        I.P_PREMIUM_TRANSSEQ, ");
		sql.append("        I.TOT_CALL_CNT, ");
		sql.append("        I.TODAY_CALL_CNT, ");
		sql.append("        I.FAIL_CALL_CNT, ");
		sql.append("        I.FAIL_REASON, ");
		sql.append("        I.FAIL_TYPE1, ");
		sql.append("        I.FAIL_TYPE2, ");
		sql.append("        I.CALL_MEMO ");
		sql.append(" FROM TBIOT_PREMATCH A ");
		sql.append(" LEFT OUTER JOIN TBORG_MEMBER B ON B.EMP_ID = A.RECRUIT_ID ");
		sql.append(" LEFT OUTER JOIN TBORG_MEMBER C ON C.EMP_ID = A.SIGNOFF_ID ");
		sql.append(" LEFT OUTER JOIN TBIOT_PREMATCH_PDF D ON D.PREMATCH_SEQ = A.PREMATCH_SEQ ");
		sql.append(" LEFT OUTER JOIN TBPRD_INS_MAIN M ON M.INSPRD_KEYNO = A.INSPRD_KEYNO ");			// 富壽商品
		sql.append(" LEFT OUTER JOIN TBJSB_INS_PROD_MAIN J ON J.PRODUCTSERIALNUM = A.INSPRD_KEYNO ");	// 非富壽商品
		sql.append(" LEFT OUTER JOIN TBCRM_CUST_MAST E ON A.CUST_ID = E.CUST_ID ");
		sql.append(" LEFT OUTER JOIN (SELECT DISTINCT EMP_ID, DEPT_ID FROM VWORG_EMP_UHRM_INFO) F ON F.EMP_ID = A.CREATOR "); //UHRM人員鍵機或UHRM為招攬人員的案件
		sql.append(" LEFT OUTER JOIN (SELECT DISTINCT EMP_ID, DEPT_ID FROM VWORG_EMP_UHRM_INFO) G ON G.EMP_ID = A.MODIFIER "); //UHRM人員鍵機或UHRM為招攬人員的案件
		sql.append(" LEFT OUTER JOIN (SELECT DISTINCT EMP_ID, DEPT_ID FROM VWORG_EMP_UHRM_INFO) H ON H.EMP_ID = A.RECRUIT_ID "); //UHRM人員鍵機或UHRM為招攬人員的案件
		sql.append(" LEFT JOIN TBIOT_CALLOUT I ON A.PREMATCH_SEQ = I.PREMATCH_SEQ ");
		sql.append(" LEFT JOIN TBORG_DEFN K ON B.DEPT_ID = K.DEPT_ID ");
		sql.append(" WHERE 1 = 1 ");
		
		switch (getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG).toString()) {
			case "UHRM":
			case "uhrmMGR":
				//有UHRM權限人員只能查詢UHRM人員鍵機或UHRM為招攬人員的案件
				sql.append("AND ( ");
				sql.append("     (F.EMP_ID IS NOT NULL AND EXISTS (SELECT 1 FROM TBORG_MEMBER MT WHERE F.DEPT_ID = MT.DEPT_ID AND MT.EMP_ID = :loginID)) ");
				sql.append("  OR (G.EMP_ID IS NOT NULL AND EXISTS (SELECT 1 FROM TBORG_MEMBER MT WHERE G.DEPT_ID = MT.DEPT_ID AND MT.EMP_ID = :loginID)) ");
				sql.append("  OR (H.EMP_ID IS NOT NULL AND EXISTS (SELECT 1 FROM TBORG_MEMBER MT WHERE H.DEPT_ID = MT.DEPT_ID AND MT.EMP_ID = :loginID)) ");
				sql.append(") ");
				
				queryCondition.setObject("loginID", (String) SysInfo.getInfoValue(SystemVariableConsts.LOGINID));
				
//				sql.append(" AND (F.EMP_ID IS NOT NULL OR G.EMP_ID IS NOT NULL OR H.EMP_ID IS NOT NULL) ");
				break;
			default:
				//非總行人員需檢查可視分行，只能查詢有鍵機行權限的資料，且非UHRM人員鍵機或UHRM為招攬人員的案件
				if(!headmgrMap.containsKey(roleID)) {
					sql.append(" AND A.BRANCH_NBR IN ( :branchList ) AND F.EMP_ID IS NULL AND G.EMP_ID IS NULL AND H.EMP_ID IS NULL ");
					queryCondition.setObject("branchList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
				}
				break;
		}
		
		if (StringUtils.isNotBlank(inputVO.getREG_TYPE())) {
			sql.append(" AND A.REG_TYPE = :regType "); 
			queryCondition.setObject("regType", inputVO.getREG_TYPE());
		}
		
		if (StringUtils.isNotBlank(inputVO.getCUST_ID())) {
			if (StringUtils.equals("1",inputVO.getREG_TYPE())) {
				sql.append(" AND A.CUST_ID = :custId "); 
				queryCondition.setObject("custId", inputVO.getCUST_ID().toUpperCase());
			} else if(StringUtils.equals("3",inputVO.getREG_TYPE())) {
				sql.append(" AND A.CHG_CUST_ID = :custId "); 
				queryCondition.setObject("custId", inputVO.getCUST_ID().toUpperCase());
			} else {
				sql.append(" AND (A.CUST_ID = :custId OR A.CHG_CUST_ID = :custId) "); 
				queryCondition.setObject("custId", inputVO.getCUST_ID().toUpperCase());
			}
		}

		if (StringUtils.isNotBlank(inputVO.getSTATUS())) {
			sql.append(" AND A.STATUS = :status "); 
			queryCondition.setObject("status", inputVO.getSTATUS());
		}
		
		if (StringUtils.isNotBlank(inputVO.getPREMATCH_SEQ())) {
			sql.append(" AND A.PREMATCH_SEQ = :prematchSeq "); 
			queryCondition.setObject("prematchSeq", inputVO.getPREMATCH_SEQ());
		}
		
		if (StringUtils.isNotBlank(inputVO.getRECRUIT_ID())) {
			sql.append(" AND A.RECRUIT_ID = :recruitId "); 
			queryCondition.setObject("recruitId", inputVO.getRECRUIT_ID());
		}
		
		if (StringUtils.isNotBlank(inputVO.getRECRUIT_ID())) {
			sql.append(" AND A.RECRUIT_ID = :recruitId "); 
			queryCondition.setObject("recruitId", inputVO.getRECRUIT_ID());
		}
		
		if (StringUtils.isNotBlank(inputVO.getINSPRD_ID())) {
			sql.append(" AND A.INSPRD_ID = :insPrdId "); 
			queryCondition.setObject("insPrdId", inputVO.getINSPRD_ID().toUpperCase());
		}
		
		if (StringUtils.isNotBlank(inputVO.getCASE_ID())) {
			sql.append(" AND A.CASE_ID = :caseId "); 
			queryCondition.setObject("caseId", inputVO.getCASE_ID().toUpperCase());
		}
		
		if (inputVO.getAPPLY_DATE_F() != null) {
			sql.append(" AND TRUNC(A.APPLY_DATE) >= TRUNC(:applyDateF) "); 
			queryCondition.setObject("applyDateF", inputVO.getAPPLY_DATE_F());
		}
		
		if (inputVO.getAPPLY_DATE_E() != null) {
			sql.append(" AND TRUNC(A.APPLY_DATE) <= TRUNC(:applyDateE) "); 
			queryCondition.setObject("applyDateE", inputVO.getAPPLY_DATE_E());
		}
		
		sql.append(" ORDER BY A.PREMATCH_SEQ, A.CUST_ID, A.APPLY_DATE "); 
		
		queryCondition.setQueryString(sql.toString());
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);	
		
		String callout_yn = null;
		for(Map<String, Object> map : list) {
			callout_yn = "N";
			// C_PREMIUM_TRANSSEQ_YN, I_PREMIUM_TRANSSEQ_YN, P_PREMIUM_TRANSSEQ_YN
			if ((map.get("C_PREMIUM_TRANSSEQ_YN") != null && map.get("C_PREMIUM_TRANSSEQ_YN").toString().equals("Y")) ||
				(map.get("I_PREMIUM_TRANSSEQ_YN") != null && map.get("I_PREMIUM_TRANSSEQ_YN").toString().equals("Y")) ||
				(map.get("P_PREMIUM_TRANSSEQ_YN") != null && map.get("P_PREMIUM_TRANSSEQ_YN").toString().equals("Y"))) {
				callout_yn = "Y";
			}
			map.put("CALLOUT_YN", callout_yn);
		}
		
		return_VO.setPrematchList(list);
		
		this.sendRtnObject(return_VO);
	}
	
	/* === 產出Excel==== */
	public void export(Object body, IPrimitiveMap header) throws JBranchException {
		IOT111InputVO inputVO = (IOT111InputVO) body;
		List<Map<String, Object>> list = inputVO.getPrematchList();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String fileName = "要保人保險購買檢核_" + sdf.format(new Date()) + ".csv";
		List listCSV = new ArrayList();
		if (list.size() == 0) {
			String[] records = new String[2];
			records[0] = "查無資料";
			listCSV.add(records);
		} else {
			XmlInfo xmlInfo = new XmlInfo();
			String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
			// REG_TYPE				保險交易項目
			Map<String, String> regTypeMap = xmlInfo.doGetVariable("IOT.TRADE_TYPE", FormatHelper.FORMAT_3);
			// OTH_TYPE				要保書類型
			Map<String, String> othTypeMap = xmlInfo.doGetVariable("IOT.OTH_TYPE_110", FormatHelper.FORMAT_3);
			// RLT_BT_PROPAY		繳款人與要保人關係
			Map<String, String> relationMap = xmlInfo.doGetVariable("IOT.PAYER_REL_PROPOSER", FormatHelper.FORMAT_3);
			// PAY_TYPE				躉繳/分期繳
			Map<String, String> payTypeMap = xmlInfo.doGetVariable("IOT.PAY_TYPE", FormatHelper.FORMAT_3);
			// MOP2					分期繳別
			Map<String, String> mop2Map = xmlInfo.doGetVariable("IOT.MOP2", FormatHelper.FORMAT_3);
			// LOAN_SOURCE_YN		業報書保費來源為貸款/保單借款
			// LOAN_SOURCE2_YN		業報書保費來源為解約
			// C_LOAN_CHK1_YN		要保人保單貸款檢核(透過本行送件)
			// C_LOAN_CHK2_YN		要保人行內貸款檢核
			// C_LOAN_CHK3_YN		要保人行內保單解約檢核
			// C_CD_CHK_YN			要保人定存單不打折
			// I_LOAN_CHK1_YN		被保險人保單貸款檢核(透過本行送件)
			// I_LOAN_CHK2_YN		被保險人行內貸款檢核
			// I_LOAN_CHK3_YN		被保險人行內保單解約檢核
			// I_CD_CHK_YN			被保險人定存單不打折
			// LOAN_CHK1_YN			繳款人保單貸款檢核(透過本行送件)
			// LOAN_CHK2_YN			繳款人行內貸款檢核
			// LOAN_CHK3_YN			繳款人行內保單解約檢核
			// CD_CHK_YN			繳款人定存單不打折
			Map<String, String> YNMap = xmlInfo.doGetVariable("COMMON.YES_NO", FormatHelper.FORMAT_3);
			// CONTRACT_END_YN		業報書投保前三個月內有辦理解約
			// S_INFITEM_LOAN_YN	業報書投保前三個月內有辦理貸款或保險單借款
			Map<String, String> oneTwoMap = xmlInfo.doGetVariable("COMMON.YES_NO_12", FormatHelper.FORMAT_3);
			// PROPOSER_CM_FLAG		要保人戶況檢核
			// INSURED_CM_FLAG		被保險人戶況檢核
			// PAYER_CM_FLAG		繳款人戶況檢核
			Map<String, String> cmFlagMap = xmlInfo.doGetVariable("IOT.CM_FLAG", FormatHelper.FORMAT_3);
			// SENIOR_AUTH_OPT		高齡銷售過程錄音覆核邏頊
			Map<String, String> optMap = xmlInfo.doGetVariable("IOT.SENIOR_AUTH_OPT", FormatHelper.FORMAT_3);
			// STATUS				狀態
			Map<String, String> statusMap = xmlInfo.doGetVariable("IOT.PREMATCH_STATUS", FormatHelper.FORMAT_3);
			// 電訪種類
			Map<String, String> callTypeMap = xmlInfo.doGetVariable("CALLOUT.CALL_TYPE", FormatHelper.FORMAT_3);
			// 未成功原因
			Map<String, String> failReasonMap = xmlInfo.doGetVariable("CALLOUT.FAIL_REASON", FormatHelper.FORMAT_3);
			// 失敗異常分類
			Map<String, String> failTypeMap = xmlInfo.doGetVariable("CALLOUT.FAIL_TYPE", FormatHelper.FORMAT_3);
			
			for (Map<String, Object> map : list) {
				String[] records = new String[96];
				int i = 0;
				String regType = regTypeMap.get(checkIsNull(map, "REG_TYPE"));
				records[i]	 = regType == null ? "" : regType;				// 保險交易項目
				records[++i] = checkIsNull(map, "PREMATCH_SEQ");			// 適合度檢核編碼
				String othType = othTypeMap.get(checkIsNull(map, "OTH_TYPE"));
				records[++i] = othType == null ? "" : othType;				// 要保書類型
				records[++i] = checkIsNull(map, "CASE_ID");					// 案件編號
				records[++i] = checkIsNull(map, "CUST_ID");					// 要保人ID
				records[++i] = checkIsNull(map, "PROPOSER_NAME");			// 要保人姓名
				String proposerBirth = this.onlyDate(checkIsNull(map, "PROPOSER_BIRTH"));
				records[++i] = proposerBirth;								// 要保人生日
				records[++i] = checkIsNull(map, "INSURED_ID");				// 被保險人ID
				records[++i] = checkIsNull(map, "INSURED_NAME");			// 被保險人姓名
				String insuredBirth = this.onlyDate(checkIsNull(map, "INSURED_BIRTH"));
				records[++i] = insuredBirth;								// 被保險人生日
				String rltBtPropay = relationMap.get(checkIsNull(map, "RLT_BT_PROPAY"));
				records[++i] = rltBtPropay == null ? "" : rltBtPropay;		// 繳款人與要保人關係
				records[++i] = checkIsNull(map, "PAYER_ID");				// 繳款人ID
				records[++i] = checkIsNull(map, "PAYER_NAME");				// 繳款人姓名
				String payerBirth = this.onlyDate(checkIsNull(map, "PAYER_BIRTH"));
				records[++i] = payerBirth;									// 繳款人生日
				records[++i] = checkIsNull(map, "C_SALE_SENIOR_TRANSSEQ");	// 要保人高齡銷售過程錄音序號
				records[++i] = checkIsNull(map, "I_SALE_SENIOR_TRANSSEQ");	// 被保人高齡銷售過程錄音序號
				records[++i] = checkIsNull(map, "P_SALE_SENIOR_TRANSSEQ");	// 繳款人高齡銷售過程錄音序號
				records[++i] = checkIsNull(map, "INSPRD_ID");				// 險種代號
				records[++i] = checkIsNull(map, "INSPRD_NAME");				// 險種名稱
				records[++i] = checkIsNull(map, "INSPRD_ANNUAL");			// 繳費年期
				records[++i] = currencyFormat(map, "REAL_PREMIUM");			// 實收保費(原幣) 
				records[++i] = currencyFormat(map, "BASE_PREMIUM");			// 基本保費(原幣)
				String payType = payTypeMap.get(checkIsNull(map, "PAY_TYPE"));
				records[++i] = payType == null ? "" : payType;				// 躉/分期繳
				String mop2 = mop2Map.get(checkIsNull(map, "MOP2"));
				records[++i] = mop2 == null ? "" : mop2;					// 分期繳别
				records[++i] = checkIsNull(map, "PRD_RATE");				// 實際收益率(臺幣/非年化)
				records[++i] = checkIsNull(map, "CNR_RATE");				// CNR收益率(臺幣/非年化)
				records[++i] = checkIsNull(map, "EXCH_RATE");				// 參考匯率
				records[++i] = checkIsNull(map, "AB_EXCH_RATE");			// 非常態交易參考匯率
				String applyDate = this.onlyDate(checkIsNull(map, "APPLY_DATE"));
				records[++i] = applyDate;									// 要保書申請日
				String YN1 = YNMap.get(checkIsNull(map, "LOAN_SOURCE_YN"));
				records[++i] = YN1 == null ? "" : YN1;						// 業報書保費來源為貸款/保單借款
				String YN2 = YNMap.get(checkIsNull(map, "LOAN_SOURCE2_YN"));
				records[++i] = YN2 == null ? "" : YN2;						// 業報書保費來源為解約（Y/N）
				String oneTwo1 = oneTwoMap.get(checkIsNull(map, "CONTRACT_END_YN"));
				records[++i] = oneTwo1 == null ? "" : oneTwo1;				// 業報書投保前三個月内有辦理解約（1.是 2.否）
				String oneTwo2 = oneTwoMap.get(checkIsNull(map, "S_INFITEM_LOAN_YN"));
				records[++i] = oneTwo2 == null ? "" : oneTwo2;				// 業報書投保前三個月內有辦理貸款或保險單借款（1.是 2.否）
				String matchDate = this.onlyDate(checkIsNull(map, "MATCH_DATE"));
				records[++i] = matchDate;									// 檢核試算日
				records[++i] = "=\""+checkIsNull(map, "RECRUIT_ID")+"\"";	// 招攬人員編
				String cmFlag1 = cmFlagMap.get(checkIsNull(map, "PROPOSER_CM_FLAG"));
				records[++i] = cmFlag1 == null ? "" : cmFlag1;				// 要保人戶況檢核
				
				String riskAtr = checkIsNull(map, "CUST_RISK_ATR");
				String riskDue = this.onlyDate(checkIsNull(map, "CUST_RISK_DUE"));
				records[++i] = riskAtr + " " + riskDue;						// 要保人KYC等級/效期
				
				records[++i] = checkIsNull(map, "PROPOSER_WORK");			// 要保人工作內容
				records[++i] = checkIsNull(map, "AML");						// 要保人AML風險等級
				records[++i] = checkIsNull(map, "PRECHECK");				// 要保人Pre-check結果
				String cPremDate = this.onlyDate(checkIsNull(map, "C_PREM_DATE"));
				records[++i] = cPremDate;									// 提領保額/保價日
				String cLoanApplyDate = this.onlyDate(checkIsNull(map, "C_LOAN_APPLY_DATE"));
				records[++i] = cLoanApplyDate;								// 要保人行内貸款申請日
				records[++i] = currencyFormat(map, "PROPOSER_INCOME1");		// 要保人業報書年收入(工作+其他)
				records[++i] = checkIsNull(map, "C_KYC_INCOME");			// 要保人行內KYC收入
				records[++i] = checkIsNull(map, "PROPOSER_INCOME3");		// 要保人授信收入
				String cLoanChk2Date = this.onlyDate(checkIsNull(map, "C_LOAN_CHK2_DATE"));
				records[++i] = cLoanChk2Date;								// 要保人最近撥貸日
				String YN3 = YNMap.get(checkIsNull(map, "C_LOAN_CHK1_YN"));
				String YN4 = YNMap.get(checkIsNull(map, "C_LOAN_CHK2_YN"));
				String YN5 = YNMap.get(checkIsNull(map, "C_LOAN_CHK3_YN"));
				String YN6 = YNMap.get(checkIsNull(map, "C_CD_CHK_YN"));
				records[++i] = YN3 == null ? "" : YN3;						// 要保人保單貸款檢核(透過本行送件)
				records[++i] = YN4 == null ? "" : YN4;						// 要保人行內貸款檢核
				records[++i] = YN5 == null ? "" : YN5;						// 要保人行內保單解約檢核
				records[++i] = YN6 == null ? "" : YN6;						// 要保人定存單不打折
				String cCDDueDate = this.onlyDate(checkIsNull(map, "C_CD_DUE_DATE"));// 要保人定存單解約/到期日
				records[++i] = cCDDueDate;
				String cmFlag2 = cmFlagMap.get(checkIsNull(map, "INSURED_CM_FLAG"));
				records[++i] = cmFlag2 == null ? "" : cmFlag2;				// 被保險人戶況檢核
				String iLoanApplyDate = this.onlyDate(checkIsNull(map, "I_LOAN_APPLY_DATE"));
				records[++i] = iLoanApplyDate;								// 被保險人行内貸款申請日
				String iLoanChk2Date = this.onlyDate(checkIsNull(map, "I_LOAN_CHK2_DATE"));
				records[++i] = iLoanChk2Date;								// 被保人最近撥貸日
				records[++i] = checkIsNull(map, "INSURED_WORK");			// 被保險人工作内容
				records[++i] = currencyFormat(map, "INSURED_INCOME1");		// 被保險人業報書年收入(工作+其他)
				records[++i] = checkIsNull(map, "I_KYC_INCOME");			// 被保險人行内KYC收入
				records[++i] = checkIsNull(map, "INSURED_INCOME3");			// 被保險人授信收入
				String YN7 = YNMap.get(checkIsNull(map, "I_LOAN_CHK1_YN"));
				String YN8 = YNMap.get(checkIsNull(map, "I_LOAN_CHK2_YN"));
				String YN9 = YNMap.get(checkIsNull(map, "I_LOAN_CHK3_YN"));
				String YN10 = YNMap.get(checkIsNull(map, "I_CD_CHK_YN"));
				records[++i] = YN7 == null ? "" : YN7;						// 被保險人保單貸款檢核(透過本行送件)
				records[++i] = YN8 == null ? "" : YN8;						// 被保險人行內貸款檢核
				records[++i] = YN9 == null ? "" : YN9;						// 被保險人行內保單解約檢核
				records[++i] = YN10 == null ? "" : YN10;					// 被保險人定存單不打折
				String iCDDueDate = this.onlyDate(checkIsNull(map, "I_CD_DUE_DATE"));// 被保人定存單解約/到期日
				records[++i] = iCDDueDate;
				String cmFlag3 = cmFlagMap.get(checkIsNull(map, "PAYER_CM_FLAG"));
				records[++i] = cmFlag3 == null ? "" : cmFlag3;				// 繳款人戶況檢核
				String pLoanApplyDate = this.onlyDate(checkIsNull(map, "P_LOAN_APPLY_DATE"));
				records[++i] = pLoanApplyDate;								// 繳款人行內貸款申請日
				String loanChk2Date = this.onlyDate(checkIsNull(map, "LOAN_CHK2_DATE"));
				records[++i] = loanChk2Date;								// 繳款人最近撥貸日
				String YN11 = YNMap.get(checkIsNull(map, "LOAN_CHK1_YN"));
				String YN12 = YNMap.get(checkIsNull(map, "LOAN_CHK2_YN"));
				String YN13 = YNMap.get(checkIsNull(map, "LOAN_CHK3_YN"));
				String YN14 = YNMap.get(checkIsNull(map, "CD_CHK_YN"));
				records[++i] = YN11 == null ? "" : YN11;					// 繳款人保單貸款檢核(透過本行送件)
				records[++i] = YN12 == null ? "" : YN12;					// 繳款人行內貸款檢核
				records[++i] = YN13 == null ? "" : YN13;					// 繳款人行內保單解約檢核
				records[++i] = YN14 == null ? "" : YN14;					// 繳款人定存單不打折
				String pCDDueDate = this.onlyDate(checkIsNull(map, "P_CD_DUE_DATE"));// 繳款人定存單解約/到期日
				records[++i] = pCDDueDate;
				records[++i] = checkIsNull(map, "CUST_DEBIT");				// 要保人行內負債(不含信用卡)
				records[++i] = checkIsNull(map, "INSURED_DEBIT");			// 被保險人行內負債(不含信用卡)
				String opt = optMap.get(checkIsNull(map, "SENIOR_AUTH_OPT"));
				records[++i] = opt == null ? "" : opt;						// 高齡銷售過程錄音覆核邏頊（1:驗證錄音無誤 2:驗證錄音有誤）
				String status = statusMap.get(checkIsNull(map, "STATUS"));
				records[++i] = status == null ? "" : status;				// 狀態（1:暫存 2:覆核中 3:核可 4:退件）
				records[++i] = "=\""+checkIsNull(map, "SIGNOFF_ID")+"\"";	// 覆核人員員編
				String lastupdate = this.onlyDate(checkIsNull(map, "LASTUPDATE"));
				records[++i] = lastupdate;									// 最後維護日期
				
				// 2024.06.05 新增匯出欄位*20
				records[++i] = checkIsNull(map, "CALL_DATE");								// 電訪申請日期
				records[++i] = checkIsNull(map, "DEPT_ID");									// 分行代號
				records[++i] = checkIsNull(map, "DEPT_NAME");								// 分行中文
				records[++i] = checkIsNull(map, "CURR_CD");									// 商品幣別
//				records[++i] = checkIsNull(map, "INSPRD_NAME");								// 險種中文
				records[++i] = checkIsNull(map, "INSPRD_TYPE").equals("1") ? "N" : "Y";		// 投資型註記
				
				// 錄音樣態
				String c_call_type = callTypeMap.get(checkIsNull(map, "C_CALL_TYPE"));
				records[++i] = c_call_type == null ? "" : c_call_type;
				// 被保險人電訪種類
				String i_call_type = callTypeMap.get(checkIsNull(map, "I_CALL_TYPE"));
				records[++i] = i_call_type == null ? "" : i_call_type;
				// 繳款人電訪種類
				String p_call_type = callTypeMap.get(checkIsNull(map, "P_CALL_TYPE"));
				records[++i] = p_call_type == null ? "" : p_call_type;
				
				records[++i] = checkIsNull(map, "CALL_PERSON");			// 電訪人員
				records[++i] = checkIsNull(map, "C_PREMIUM_TRANSSEQ");	// 要保人錄音序號
				records[++i] = checkIsNull(map, "I_PREMIUM_TRANSSEQ");	// 被保險人錄音序號
				records[++i] = checkIsNull(map, "P_PREMIUM_TRANSSEQ");	// 繳款人錄音序號
				records[++i] = checkIsNull(map, "TOT_CALL_CNT");		// 總撥打次數
				records[++i] = checkIsNull(map, "TODAY_CALL_CNT");		// 今日撥打次數
				records[++i] = checkIsNull(map, "FAIL_CALL_CNT");		// 失敗次數
				
				// 未成功原因
				String fail_reason = failReasonMap.get(checkIsNull(map, "FAIL_REASON"));
				records[++i] = fail_reason == null ? "" : fail_reason;
				// 失敗異常分類1
				String fail_type1 = failTypeMap.get(checkIsNull(map, "FAIL_TYPE1"));
				records[++i] = fail_type1 == null ? "" : fail_type1;
				// 失敗異常分類2
				String fail_type2 = failTypeMap.get(checkIsNull(map, "FAIL_TYPE2"));
				records[++i] = fail_type2 == null ? "" : fail_type2;
				
				records[++i] = checkIsNull(map, "CALL_MEMO");			// 備註（蓋掉異常分類前，請把前次失敗原因打進備註）

				listCSV.add(records);
			}
		}

		// header
		String[] csvHeader = new String[96];
		int j = 0;
		csvHeader[j]   = "保險交易項目";
		csvHeader[++j] = "適合度檢核編碼";
		csvHeader[++j] = "要保書類型";
		csvHeader[++j] = "案件編號";
		csvHeader[++j] = "要保人ID";
		csvHeader[++j] = "要保人姓名";
		csvHeader[++j] = "要保人生日";
		csvHeader[++j] = "被保險人ID";
		csvHeader[++j] = "被保險人姓名";
		csvHeader[++j] = "被保險人生日";
		csvHeader[++j] = "繳款人與要保人關係";
		csvHeader[++j] = "繳款人ID";
		csvHeader[++j] = "繳款人姓名";
		csvHeader[++j] = "繳款人生日";
		csvHeader[++j] = "要保人高齡銷售過程錄音序號";
		csvHeader[++j] = "被保人高齡銷售過程錄音序號";
		csvHeader[++j] = "繳款人高齡銷售過程錄音序號";
		csvHeader[++j] = "險種代號";
		csvHeader[++j] = "險種名稱";
		csvHeader[++j] = "繳費年期";
		csvHeader[++j] = "實收保費(原幣)";
		csvHeader[++j] = "基本保費(原幣)";
		csvHeader[++j] = "躉/分期繳";
		csvHeader[++j] = "分期繳別";
		csvHeader[++j] = "實際收益率(臺幣/非年化)";
		csvHeader[++j] = "CNR收益率(臺幣/非年化)";
		csvHeader[++j] = "參考匯率";
		csvHeader[++j] = "非常態交易參考匯率";
		csvHeader[++j] = "要保書申請日";
		csvHeader[++j] = "業報書保費來源為貸款/保單借款";
		csvHeader[++j] = "業報書保費來源為解約";
		csvHeader[++j] = "業報書投保前三個月內有辦理解約";
		csvHeader[++j] = "業報書投保前三個月內有辦理貸款或保險單借款";
		csvHeader[++j] = "檢核試算日";
		csvHeader[++j] = "招攬人員編";
		csvHeader[++j] = "要保人戶況檢核";
		csvHeader[++j] = "要保人KYC等級/效期";
		csvHeader[++j] = "要保人工作內容";
		csvHeader[++j] = "要保人AML風險等級";
		csvHeader[++j] = "要保人Pre-check結果";
		csvHeader[++j] = "要保人舊保單提領保額/保價日";
		csvHeader[++j] = "要保人行內貸款申請日";
		csvHeader[++j] = "要保人業報書年收入(工作+其他)";
		csvHeader[++j] = "要保人行內KYC收入";
		csvHeader[++j] = "要保人授信收入";
		csvHeader[++j] = "要保人最近撥貸日";
		csvHeader[++j] = "要保人保單貸款檢核(透過本行送件)";
		csvHeader[++j] = "要保人行內貸款檢核";
		csvHeader[++j] = "要保人行內保單解約檢核";
		csvHeader[++j] = "要保人定存單不打折";
		csvHeader[++j] = "要保人定存單解約/到期日";
		csvHeader[++j] = "被保險人戶況檢核";
		csvHeader[++j] = "被保險人行內貸款申請日";
		csvHeader[++j] = "被保人最近撥貸日";
		csvHeader[++j] = "被保險人工作內容";
		csvHeader[++j] = "被保險人業報書年收入(工作+其他)";
		csvHeader[++j] = "被保險人行內KYC收入";
		csvHeader[++j] = "被保險人授信收入";
		csvHeader[++j] = "被保險人保單貸款檢核(透過本行送件)";
		csvHeader[++j] = "被保險人行內貸款檢核";
		csvHeader[++j] = "被保險人行內保單解約檢核";
		csvHeader[++j] = "被保險人定存單不打折";
		csvHeader[++j] = "被保人定存單解約/到期日";
		csvHeader[++j] = "繳款人戶況檢核";
		csvHeader[++j] = "繳款人行內貸款申請日";
		csvHeader[++j] = "繳款人最近撥貸日";
		csvHeader[++j] = "繳款人保單貸款檢核(透過本行送件)";
		csvHeader[++j] = "繳款人行內貸款檢核";
		csvHeader[++j] = "繳款人行內保單解約檢核";
		csvHeader[++j] = "繳款人定存單不打折";
		csvHeader[++j] = "繳款人定存單解約/到期日";
		csvHeader[++j] = "要保人行內負債(不含信用卡)";
		csvHeader[++j] = "被保險人行內負債(不含信用卡)";
		csvHeader[++j] = "高齡銷售過程錄音覆核選項";
		csvHeader[++j] = "狀態";
		csvHeader[++j] = "覆核人員員編";
		csvHeader[++j] = "最後維護日期";
		
		// 2024.06.05 新增匯出欄位*20 
		csvHeader[++j] = "電訪申請日期";
		csvHeader[++j] = "分行代號";
		csvHeader[++j] = "分行中文";
		csvHeader[++j] = "商品幣別";
//		csvHeader[++j] = "險種中文";
		csvHeader[++j] = "投資型註記";
		csvHeader[++j] = "錄音樣態";
		csvHeader[++j] = "被保險人電訪種類";
		csvHeader[++j] = "繳款人電訪種類";
		csvHeader[++j] = "電訪人員";
		csvHeader[++j] = "要保人錄音序號";
		csvHeader[++j] = "被保險人錄音序號";
		csvHeader[++j] = "繳款人錄音序號";
		csvHeader[++j] = "總撥打次數";
		csvHeader[++j] = "今日撥打次數";
		csvHeader[++j] = "失敗次數";
		csvHeader[++j] = "未成功原因";
		csvHeader[++j] = "失敗異常分類1";
		csvHeader[++j] = "失敗異常分類2";
		csvHeader[++j] = "備註（蓋掉異常分類前，請把前次失敗原因打進備註）";


		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		csv.addRecordList(listCSV);
		String url = csv.generateCSV();
		notifyClientToDownloadFile(url, fileName);
		this.sendRtnObject(null);
	}
	
	// 下載範例
	public void downLoad(Object body, IPrimitiveMap header) throws Exception {
		notifyClientToDownloadFile("doc//IOT//IOT111_EXAMPLE.xlsx", "保險資金錄音序號_上傳範例.xlsx");
		
		this.sendRtnObject(null);
	}
	
	public void upload(Object body, IPrimitiveMap header) throws Exception {
		IOT111InputVO inputVO = (IOT111InputVO) body;
		IOT111OutputVO outputVO = new IOT111OutputVO();
		List<Map<String, Object>> resultList = new ArrayList<>();
		File dataFile = null;
		
		if (!StringUtils.isBlank(inputVO.getFileName())) {
			dataFile = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName());
			String fileName = dataFile.getName();
			List<Map<String, String>> list = null;
			
			if (fileName.toUpperCase().endsWith("CSV") || fileName.toUpperCase().endsWith("TXT")) {
				String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
				List<String[]> dataCsv = CSVUtil.getBig5CSVFile(tempPath, inputVO.getFileName());
				list = this.readCSV(dataCsv);
				
			} else if (fileName.toUpperCase().endsWith("XLS")||fileName.toUpperCase().endsWith("XLSX")) {
				list = this.readXLS(dataFile);
				
			} else {
				throw new APException("上傳的檔案格式必須是CSV或Excel");
			}
			
			dam = this.getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			
			String prematch_seq = null;
			String premium_transseq = null;
			String check_result_1 = null;
			String i_premium_transseq = null;
			String check_result_2 = null;
			String p_premium_transseq = null;
			String check_result_3 = null;
			for (Map<String, String> map : list) {
				Map<String, Object> resultMap = new HashMap<>();
				
				prematch_seq = map.get("1");
				premium_transseq = map.get("2");
				i_premium_transseq = map.get("3");
				p_premium_transseq = map.get("4");
				// 檢核結果：上傳值須為12碼，且第7、8碼須為73，若不符合則檢核結果為N
				check_result_1 = this.checkTransSeq(premium_transseq);
				check_result_2 = this.checkTransSeq(i_premium_transseq);
				check_result_3 = this.checkTransSeq(p_premium_transseq);
				
				// 取得 CUST_ID
				qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuffer();
				sb.append(" SELECT CUST_ID, INSURED_ID, PAYER_ID FROM TBIOT_PREMATCH WHERE PREMATCH_SEQ = :prematch_seq ");
				
				qc.setObject("prematch_seq", prematch_seq);
				qc.setQueryString(sb.toString());
				List<Map<String, Object>> custList = dam.exeQuery(qc);
				String cust_id = "";		// 要保人ID
				String insured_id = "";		// 被保人ID
				String payer_id = "";		// 繳款人ID
				if (null != custList && custList.size() > 0) {
					if (custList.get(0).get("CUST_ID") != null && StringUtils.isNotBlank(custList.get(0).get("CUST_ID").toString().trim())) {
						cust_id = custList.get(0).get("CUST_ID").toString();
					} 
					if (custList.get(0).get("INSURED_ID") != null && StringUtils.isNotBlank(custList.get(0).get("INSURED_ID").toString().trim())) {
						insured_id = custList.get(0).get("INSURED_ID").toString();
					} 
					if (custList.get(0).get("PAYER_ID") != null && StringUtils.isNotBlank(custList.get(0).get("PAYER_ID").toString().trim())) {
						payer_id = custList.get(0).get("PAYER_ID").toString();	
					}
					if (StringUtils.isBlank(cust_id) && StringUtils.isBlank(insured_id) && StringUtils.isBlank(payer_id)) {
						// 沒有客戶ID的話，就不寫入訪談記錄
						continue;
					}
				} else {
					// 沒有客戶ID的話，就不寫入訪談記錄
					continue;
				}
				
				resultMap.put("PREMATCH_SEQ"		, prematch_seq);
				resultMap.put("PREMIUM_TRANSSEQ"	, premium_transseq);
				resultMap.put("CHECK_RESULT_1"		, check_result_1);
				resultMap.put("I_PREMIUM_TRANSSEQ"	, i_premium_transseq);
				resultMap.put("CHECK_RESULT_2"		, check_result_2);
				resultMap.put("P_PREMIUM_TRANSSEQ"	, p_premium_transseq);
				resultMap.put("CHECK_RESULT_3"		, check_result_3);
				resultMap.put("CUST_ID"				, cust_id);
				resultMap.put("INSURED_ID"			, insured_id);
				resultMap.put("PAYER_ID"			, payer_id);
				
				resultList.add(resultMap);
			}
			outputVO.setResultList(resultList);
			
		} else {
			throw new APException("上傳檔案欄位與定義不符");
		}
		this.sendRtnObject(outputVO);
	}
	
	public void confirm(Object body, IPrimitiveMap header) throws Exception {
		IOT111InputVO inputVO = (IOT111InputVO) body;
		List<Map<String, Object>> list = inputVO.getResultList();
		String loginID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);
		
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
//		TBCRM_CUST_VISIT_RECORDVO vo = null;
		for (Map<String, Object> map : list) {
			String prematch_seq		  = map.get("PREMATCH_SEQ").toString();
			String premium_transseq   = map.get("PREMIUM_TRANSSEQ").toString();
			String check_result_1	  = map.get("CHECK_RESULT_1").toString();
			String i_premium_transseq = map.get("I_PREMIUM_TRANSSEQ").toString();
			String check_result_2 	  = map.get("CHECK_RESULT_2").toString();
			String p_premium_transseq = map.get("P_PREMIUM_TRANSSEQ").toString();
			String check_result_3 	  = map.get("CHECK_RESULT_3").toString();
			
			// 新增/更新至 TBIOT_TRANSSEQ
			qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			
			sb.append(" SELECT * FROM TBIOT_TRANSSEQ WHERE PREMATCH_SEQ = :prematch_seq ");
			qc.setObject("prematch_seq", prematch_seq);
			qc.setQueryString(sb.toString());
			List<Map<String, Object>> seqList = dam.exeQuery(qc);
			
			qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			if (seqList != null && seqList.size() > 0) {
				// 更新
				sb.append(" UPDATE TBIOT_TRANSSEQ ");
				sb.append(" SET PREMIUM_TRANSSEQ = :premium_transseq, ");
				sb.append(" CHECK_RESULT_1 = :check_result_1, ");
				sb.append(" I_PREMIUM_TRANSSEQ = :i_premium_transseq, ");
				sb.append(" CHECK_RESULT_2 = :check_result_2, ");
				sb.append(" P_PREMIUM_TRANSSEQ = :p_premium_transseq, ");
				sb.append(" CHECK_RESULT_3 = :check_result_3, ");
				sb.append(" UPDATE_STATUS = 'UP', ");
				sb.append(" VERSION = VERSION+1, ");
				sb.append(" MODIFIER = :loginID, ");
				sb.append(" LASTUPDATE = SYSDATE ");
				sb.append(" WHERE PREMATCH_SEQ = :prematch_seq ");
				
			} else {
				// 新增
				sb.append(" INSERT INTO TBIOT_TRANSSEQ ( ");
				sb.append(" PREMATCH_SEQ, PREMIUM_TRANSSEQ, CHECK_RESULT_1, ");
				sb.append(" I_PREMIUM_TRANSSEQ, CHECK_RESULT_2, ");
				sb.append(" P_PREMIUM_TRANSSEQ, CHECK_RESULT_3, UPDATE_STATUS, ");
				sb.append(" VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE ");
				sb.append(" ) VALUES ( ");
				sb.append(" :prematch_seq, :premium_transseq, :check_result_1, ");
				sb.append(" :i_premium_transseq, :check_result_2, ");
				sb.append(" :p_premium_transseq, :check_result_3, 'UP', ");
				sb.append(" 0, SYSDATE, :loginID, :loginID, SYSDATE) ");
			}
			
			qc.setObject("prematch_seq", prematch_seq);
			qc.setObject("premium_transseq", premium_transseq);
			qc.setObject("check_result_1", check_result_1);
			qc.setObject("i_premium_transseq", i_premium_transseq);
			qc.setObject("check_result_2", check_result_2);
			qc.setObject("p_premium_transseq", p_premium_transseq);
			qc.setObject("check_result_3", check_result_3);
			qc.setObject("loginID", loginID);
			qc.setQueryString(sb.toString());
			dam.exeUpdate(qc);
			
			Timestamp today = new Timestamp(new Date().getTime());
			String visitor_role = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
			String visit_memo = "通知客戶內容：適合度編碼" + map.get("PREMATCH_SEQ").toString() + "，客戶進行銀行端高齡/保費來源錄音保險電訪。";
			String visit_creply = "客戶回應內容：電訪完成，要保人錄音序號為" + map.get("PREMIUM_TRANSSEQ").toString() + 
								  "、被保人錄音序號為" + map.get("I_PREMIUM_TRANSSEQ").toString() + 
								  "、繳款人錄音序號為" + map.get("P_PREMIUM_TRANSSEQ").toString();
			
			// 要保人(CUST_ID)、被保人(INSURED_ID)、繳款人(PAYER_ID)分別寫入, 但同一人只需寫一筆
			String cust_id = map.get("CUST_ID") != null ? map.get("CUST_ID").toString() : "";
			this.saveVisit(visitor_role, cust_id, visit_memo, today, visit_creply, loginID);
			
			String insured_id = map.get("INSURED_ID") != null ? map.get("INSURED_ID").toString() : "";
			if (!cust_id.equals(insured_id)) {
				this.saveVisit(visitor_role, insured_id, visit_memo, today, visit_creply, loginID);
			}
			
			String payer_id = map.get("PAYER_ID") != null ? map.get("PAYER_ID").toString() : "";
			if (!cust_id.equals(payer_id) && !insured_id.equals(payer_id)) {
				this.saveVisit(visitor_role, payer_id, visit_memo, today, visit_creply, loginID);
			}
		}
		this.sendRtnObject(null);
	}
	
	public void saveVisit(String visitor_role, String cust_id, String visit_memo, Timestamp today, String visit_creply, String loginID) throws JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		String visitSEQ = addZeroForNum(this.getSN(), 10);
		
		sb.append("INSERT INTO TBCRM_CUST_VISIT_RECORD ( ");
		sb.append("  VISIT_SEQ, ");
		sb.append("  VISITOR_ROLE, ");
		sb.append("  CUST_ID, ");
		sb.append("  CMU_TYPE,");
		sb.append("  VISIT_MEMO, ");
		sb.append("  VISIT_DT, ");
		sb.append("  VISIT_CREPLY,");
		sb.append("  VERSION, ");
		sb.append("  CREATETIME, ");
		sb.append("  CREATOR, ");
		sb.append("  MODIFIER, ");
		sb.append("  LASTUPDATE ");
		sb.append(") VALUES ( ");
		sb.append("  :visit_seq, ");
		sb.append("  :visitor_role, ");
		sb.append("  :cust_id, ");
		sb.append("  'P',");
		sb.append("  :visit_memo, ");
		sb.append("  :visit_dt, ");
		sb.append("  :visit_creply,");
		sb.append("  0, ");
		sb.append("  SYSDATE, ");
		sb.append("  :loginID, ");
		sb.append("  :loginID, ");
		sb.append("  SYSDATE ");
		sb.append(" ) ");
		
		queryCondition.setObject("visit_seq", visitSEQ);
		queryCondition.setObject("visitor_role", visitor_role);
		queryCondition.setObject("cust_id", cust_id);
		queryCondition.setObject("visit_memo", visit_memo);
		queryCondition.setObject("visit_dt", today);
		queryCondition.setObject("visit_creply", visit_creply);
		queryCondition.setObject("loginID", loginID);
		
		queryCondition.setQueryString(sb.toString());
		
		dam.exeUpdate(queryCondition);
		
		// #0001931_WMS-CR-20240226-03_為提升系統效能擬優化業管系統相關功能_名單訪談記錄
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		
		sb.append("INSERT INTO TBCRM_CUST_VISIT_RECORD_NEW ( ");
		sb.append("  VISIT_SEQ, ");
		sb.append("  VISITOR_ROLE, ");
		sb.append("  CUST_ID, ");
		sb.append("  CMU_TYPE, ");
		sb.append("  VISIT_MEMO, ");
		sb.append("  VERSION, ");
		sb.append("  CREATOR, ");
		sb.append("  CREATETIME, ");
		sb.append("  MODIFIER, ");
		sb.append("  LASTUPDATE, ");
		sb.append("  VISIT_DT, ");
		sb.append("  VISIT_CREPLY ");
		sb.append(") ");
		sb.append("VALUES ( ");
		sb.append("  :VISIT_SEQ, ");
		sb.append("  :VISITOR_ROLE, ");
		sb.append("  :CUST_ID, ");
		sb.append("  'P', ");
		sb.append("  :VISIT_MEMO, ");
		sb.append("  0, ");
		sb.append("  :loginID, ");
		sb.append("  SYSDATE, ");
		sb.append("  :loginID, ");
		sb.append("  SYSDATE, ");
		sb.append("  :VISIT_DT, ");
		sb.append("  :VISIT_CREPLY ");
		sb.append(") ");
		
		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("VISIT_SEQ", visitSEQ);
		queryCondition.setObject("VISITOR_ROLE", visitor_role);
		queryCondition.setObject("CUST_ID", cust_id);
		queryCondition.setObject("VISIT_MEMO", visit_memo);
		queryCondition.setObject("loginID", loginID);
		queryCondition.setObject("VISIT_DT", today);
		queryCondition.setObject("VISIT_CREPLY", visit_creply);
		
		dam.exeUpdate(queryCondition);
	}
	
	private String getSN() throws JBranchException {
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT TO_CHAR(SQ_TBCRM_CUST_VISIT_RECORD.nextval) AS SEQNO FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);

		return ObjectUtils.toString(SEQLIST.get(0).get("SEQNO"));
	}
	
	private String addZeroForNum(String str, int strLength) {
		int strLen = str.length();
		if (strLen < strLength) {
			while (strLen < strLength) {
				StringBuffer sb = new StringBuffer();
				sb.append("0").append(str);// 左補0
				// sb.append(str).append("0");//右補0
				str = sb.toString();
				strLen = str.length();
			}
		}
		return str;
	}
	
	// 檢核結果：上傳值須為12碼，且第7、8碼須為73，若不符合則檢核結果為N
	private String checkTransSeq(String transseq) {
		String result = "N";		
		if (StringUtils.isNotBlank(transseq)) {
			if (transseq.length() == 12 && "73".equals(transseq.subSequence(6, 8))) {
				result = "Y";				
			}
		} else {
			result = "";	// *如果沒上傳錄音序號者不檢核
		}
		return result;
	}
	
	private String onlyDate(String DateTimes) {
		String date = "";
		if (StringUtils.isNotBlank(DateTimes)) {
			String[] dateParts = DateTimes.split(" ");
			date = dateParts[0];
		}
		return date;
	}
	
	/**
	 * 檢查Map取出欄位是否為Null
	 * 
	 * @param map
	 * @return String
	 */
	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}
	
	// 處理貨幣格式
	private String currencyFormat(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
			DecimalFormat df = new DecimalFormat("#,##0");
			return df.format(map.get(key));
		} else
			return "0";
	}
	
	/**
	 * 讀取CSV檔案格式
	 * 
	 * @param file
	 * @return List<Map<Integer,String>>
	 * @throws JBranchException
	 */
	public List<Map<String,String>> readCSV(List<String[]> dataCsv) throws JBranchException{
		List<Map<String,String>> resultList = new ArrayList<>();
		Map<String,String> map = null;
		boolean errFlag = false;
		if(!dataCsv.isEmpty()) {
			for (int i = 1; i < dataCsv.size(); i++) {
				map = new HashMap<String,String>();
				String[] str = dataCsv.get(i);				
				for (int v = 0; v < str.length; v++) {
					if (v == 0 && StringUtils.isBlank(str[v])) {
						errFlag = true;
						break;	// 若適合度檢核編碼為空值，則後面都不讀取
					}
					if (!errFlag) {
						map.put(v + 1 + "", str[v]);						
					}
				}
				if (!errFlag) {
					resultList.add(map);					
				} else {
					break;
				}
			}
		}
		return resultList;
	}
	
	/**
	 * 讀取Excel檔案格式
	 * 
	 * @param file
	 * @return List<Map<Integer,String>>
	 * @throws JBranchException
	 * @throws IOException 
	 * @throws InvalidFormatException 
	 * @throws EncryptedDocumentException 
	 * @throws BiffException 
	 * @throws  
	 */
	public List<Map<String,String>> readXLS(File file) throws JBranchException, IOException, EncryptedDocumentException, InvalidFormatException{
		List<Map<String,String>> resultList = new ArrayList<>();
		Workbook wb = WorkbookFactory.create(file);
		Sheet sheet = wb.getSheetAt(0);
		Iterator<Row> rowIterator = sheet.iterator();
		Map<String,String> map = null;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		int cellNum = 0;
		int i = 0;
		boolean errFlag = false;
		while (rowIterator.hasNext()) {
			map = new HashMap<String,String>();
			Row row = rowIterator.next();
			if (i == 0) {
				cellNum = row.getLastCellNum();
			} else {
				for (int j = 0; j < cellNum; j++) {
					if (j == 0 && StringUtils.isBlank(getValue(row.getCell(j), j, wb, df, false))) {
						errFlag = true;
						break;	// 若適合度檢核編碼為空值，則後面都不讀取
					}
					if (!errFlag) {
						map.put(j + 1 + "", getValue(row.getCell(j), j, wb, df, false));						
					}
				}
				if (!errFlag) {
					resultList.add(map);									
				} else {
					break;
				}
			}
			i++;
		}
		return resultList;
		
	}
	
	private String getValue(Cell cell, int index, Workbook book, SimpleDateFormat df, boolean isKey) throws IOException{
        // 空白或空
        if (cell == null || cell.getCellType()==Cell.CELL_TYPE_BLANK ) {
            if (isKey) {
            } else {
                return "";
            }
        }

        // 0. 數字 型別
        if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            if (HSSFDateUtil.isCellDateFormatted(cell)) {
                Date date = cell.getDateCellValue();
                return df.format(date);
            }
            String val = cell.getNumericCellValue()+"";
            val = val.toUpperCase();
            if (val.contains("E")) {
            	BigDecimal nbr = new BigDecimal(val);
            	val = nbr.toPlainString();
            }
            return val;
        }

        // 1. String型別
        if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
            String val = cell.getStringCellValue();
            if (val == null || val.trim().length()==0) {
                if (book != null) {
//                    book.close();
                }
                return "";
            }
            return val.trim();
        }

        // 2. 公式 CELL_TYPE_FORMULA
        if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
            return cell.getStringCellValue();
        }

        // 4. 布林值 CELL_TYPE_BOOLEAN
        if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
            return cell.getBooleanCellValue()+"";
        }

        // 5.	錯誤 CELL_TYPE_ERROR
        return "";
    }
	
	public void getCallOut(Object body, IPrimitiveMap header) throws Exception {
		IOT111InputVO inputVO = (IOT111InputVO) body;
		IOT111OutputVO outputVO = new IOT111OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT S05.* FROM TBIOT_CALLOUT S05 ");
		sb.append(" LEFT JOIN TBIOT_PREMATCH S01 ON S01.PREMATCH_SEQ = S05.PREMATCH_SEQ ");
		sb.append(" WHERE S01.INS_ID = :ins_id ");
		qc.setObject("ins_id", inputVO.getINS_ID());
		qc.setQueryString(sb.toString());	
		outputVO.setResultList(dam.exeQuery(qc));
		this.sendRtnObject(outputVO);
	}
	
}