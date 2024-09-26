package com.systex.jbranch.app.server.fps.iot110;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.sql.Blob;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ibm.icu.text.DecimalFormat;
import com.systex.jbranch.app.common.fps.table.TBIOT_FUND_LINKVO;
import com.systex.jbranch.app.common.fps.table.TBIOT_PREMATCHVO;
import com.systex.jbranch.app.common.fps.table.TBIOT_PREMATCH_PDFVO;
import com.systex.jbranch.app.server.fps.iot920.FirstBuyDataVO;
import com.systex.jbranch.app.server.fps.iot920.FirstBuyInputVO;
import com.systex.jbranch.app.server.fps.iot920.IOT920;
import com.systex.jbranch.app.server.fps.iot920.IOT920InputVO;
import com.systex.jbranch.app.server.fps.iot920.chk_AbInputVO;
import com.systex.jbranch.app.server.fps.iot920.chk_AbOutputVO;
import com.systex.jbranch.app.server.fps.sot110.SOT110;
import com.systex.jbranch.app.server.fps.sot701.CustHighNetWorthDataVO;
import com.systex.jbranch.app.server.fps.sot701.CustKYCDataVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701;
import com.systex.jbranch.app.server.fps.sot701.SOT701InputVO;
import com.systex.jbranch.app.server.fps.sot714.SOT714;
import com.systex.jbranch.app.server.fps.sot714.SOT714InputVO;
import com.systex.jbranch.app.server.fps.sot714.WMSHAIADataVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.PdfConfigVO;
import com.systex.jbranch.fubon.commons.PdfUtil;
import com.systex.jbranch.fubon.commons.seniorValidation.SeniorValidation;
import com.systex.jbranch.fubon.commons.seniorValidation.SeniorValidationInputVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.SerialNumberUtil;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.ReportIF;
import com.systex.jbranch.platform.common.report.factory.ReportFactory;
import com.systex.jbranch.platform.common.report.generator.ReportGeneratorIF;
import com.systex.jbranch.platform.common.report.reportdata.ReportData;
import com.systex.jbranch.platform.common.report.reportdata.ReportDataIF;
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * IOT110
 *
 * @author Joe
 * @date 2016/08/23
 * @spec null
 */
@Component("iot110")
@Scope("request")
public class IOT110 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(IOT110.class);

	/***
	 * 依購買檢核編號取得資料
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void queryData(Object body, IPrimitiveMap header) throws Exception {
		IOT110InputVO inputVO = (IOT110InputVO) body;
		IOT110OutputVO return_VO = new IOT110OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		if (StringUtils.isNotBlank(inputVO.getPREMATCH_SEQ())) {
			sql.append(" SELECT P.*, ");
			sql.append(" CASE WHEN P.FB_COM_YN = 'N' THEN J.PRODUCTNAME ELSE M.INSPRD_NAME END AS INSPRD_NAME, ");
			sql.append(" CASE WHEN P.FB_COM_YN = 'N' THEN (CASE WHEN TRIM(J.PRODUCTTYPE1) <> '投資型' THEN '1' ELSE '2' END) ELSE M.INSPRD_TYPE END AS PRODUCT_TYPE, ");
	        sql.append(" M.INSPRD_ANNUAL, M.PAY_TYPE, M.CURR_CD, M.PRD_RATE, M.CNR_RATE, NVL(D.BUY_RATE, 1) AS PROD_CURR_RATE, ");
	        sql.append(" M.EXCH_RATE, M.AB_EXCH_RATE, C.CUST_RISK_ATR, COM.CNAME AS INS_COM_NAME, ");
			sql.append(" CASE WHEN EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO WHERE EMP_ID = P.CREATOR) THEN 'Y' ");
			sql.append(" 	  WHEN EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO WHERE EMP_ID = P.MODIFIER) THEN 'Y' ");
			sql.append(" 	  WHEN EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO WHERE EMP_ID = P.RECRUIT_ID) THEN 'Y' ");
			sql.append(" ELSE 'N' END AS UHRM_CASE, "); //UHRM案件//UHRM人員鍵機或UHRM為招攬人員的案件
			sql.append(" CASE WHEN NVL(E.STATUS, '@') IN ('2', '3', '4', '5', '6', '8') THEN 'Y' ELSE 'N' END AS CALLOUT_YN ");
			sql.append(" FROM TBIOT_PREMATCH P ");
			sql.append(" LEFT OUTER JOIN TBPRD_INS_MAIN M ON M.INSPRD_KEYNO = P.INSPRD_KEYNO "); //富壽商品
			sql.append(" LEFT OUTER JOIN TBJSB_INS_PROD_MAIN J ON J.PRODUCTSERIALNUM = P.INSPRD_KEYNO "); //非富壽商品
			sql.append(" LEFT OUTER JOIN TBJSB_INS_PROD_COMPANY COM ON COM.SERIALNUM = P.COMPANY_NUM "); //保險公司
			sql.append(" LEFT OUTER JOIN TBCRM_CUST_MAST C ON P.CUST_ID = C.CUST_ID ");
			sql.append(" LEFT OUTER JOIN TBPMS_IQ053 D ON D.CUR_COD = M.CURR_CD AND D.MTN_DATE = (SELECT MAX(MTN_DATE) FROM TBPMS_IQ053) ");
			sql.append(" LEFT OUTER JOIN TBIOT_CALLOUT E ON E.PREMATCH_SEQ = P.PREMATCH_SEQ ");
			sql.append(" WHERE P.PREMATCH_SEQ = :prematchSeq ");

			queryCondition.setObject("prematchSeq", inputVO.getPREMATCH_SEQ());
			queryCondition.setQueryString(sql.toString());

			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			return_VO.setResultList(list);
			
			if(CollectionUtils.isNotEmpty(list) && StringUtils.equals("1", ObjectUtils.toString(list.get(0).get("REG_TYPE")))) { //只有新契約需取得要保人高資產註記
				//取得要保人高資產註記
				SOT714 sot714 = (SOT714) PlatformContext.getBean("sot714");
				CustHighNetWorthDataVO hnwcData = sot714.getHNWCData(ObjectUtils.toString(list.get(0).get("CUST_ID")));
				return_VO.setHnwcData(hnwcData);
			}
			
			// 投資型連結標的清單
			dam = this.getDataAccessManager();
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append(" select b.TARGET_ID,b.LINKED_NAME,a.LINK_PCT as ALLOCATION_RATIO,b.PRD_RISK,a.INS_KEYNO,a.PRD_LK_KEYNO, b.TARGET_CURR, b.COM_PRD_RISK ");
			sql.append(" from TBIOT_FUND_LINK a, TBPRD_INS_LINKING b ");
			sql.append(" where a.PRD_LK_KEYNO = b.KEY_NO and a.PREMATCH_SEQ = :prematchSeq ");
			queryCondition.setObject("prematchSeq", inputVO.getPREMATCH_SEQ());
			queryCondition.setQueryString(sql.toString());
			return_VO.setINVESTList(dam.exeQuery(queryCondition));
		}

		this.sendRtnObject(return_VO);
	}

	public void checkLicenses(Object body, IPrimitiveMap header)
			throws JBranchException {
		IOT110InputVO inputVO = (IOT110InputVO) body;
		IOT110OutputVO return_VO = new IOT110OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition();
		StringBuffer sql = new StringBuffer();
		if (inputVO.getEmpId() != null) {
			sql.append(" SELECT  'X' ");
			sql.append("	FROM  TBORG_MEMBER_CERT ");
			sql.append(" WHERE  CERTIFICATE_CODE  =  '01' ");
			sql.append(" AND EMP_ID = '" + inputVO.getEmpId() + "' ");
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			return_VO.setResultList(list);
			this.sendRtnObject(return_VO);
		}
	}

	public void checkLicensesValid(Object body, IPrimitiveMap header)
			throws JBranchException {
		IOT110InputVO inputVO = (IOT110InputVO) body;
		IOT110OutputVO return_VO = new IOT110OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition();
		StringBuffer sql = new StringBuffer();
		if (inputVO.getEmpId() != null) {
			sql.append(" SELECT  CASE	WHEN	CERTIFICATE_EX_DATE	<	SYSDATE	THEN	'Y' END	AS	EXPIRED	,	");
			sql.append("					CASE	WHEN	UNREG_DATE	IS NOT NULL	THEN	'Y'	END	AS	UNREG	");
			sql.append("	FROM  TBORG_MEMBER_CERT ");
			sql.append("	 WHERE  CERTIFICATE_CODE  =  '01' ");
			sql.append(" 	AND EMP_ID = '" + inputVO.getEmpId() + "' ");
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			return_VO.setResultList(list);
			this.sendRtnObject(return_VO);
		}
	}

	public void checkCustData(Object body, IPrimitiveMap header) throws JBranchException {
		IOT110InputVO inputVO = (IOT110InputVO) body;
		IOT110OutputVO return_VO = new IOT110OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition();

		StringBuffer sql2 = new StringBuffer();
		sql2.append("SELECT 'X' AS FATCA FROM TBCRM_NPOLM_SG ");
		sql2.append("WHERE POLICY_STATUS IN ('01','04','02','16','05') ");
		sql2.append("AND APPL_ID = '" + inputVO.getCUST_ID().toUpperCase() + "' ");
		queryCondition.setQueryString(sql2.toString());
		List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
		String fatca;
		if (CollectionUtils.isEmpty(list2)) {
			fatca = "";
		} else {
			fatca = (String) list2.get(0).get("FATCA");
		}
		return_VO.setFatca(fatca);
		this.sendRtnObject(return_VO);
	}

	public void getReferralData(Object body, IPrimitiveMap header)
			throws JBranchException {
		IOT110InputVO inputVO = (IOT110InputVO) body;
		IOT110OutputVO return_VO = new IOT110OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition();

		StringBuffer sql = new StringBuffer();
		sql.append("SELECT SEQ, SALES_PERSON, SALES_NAME ");
		sql.append("FROM TBCAM_LOAN_SALEREC ");
		sql.append("WHERE REF_PROD = '5' ");
		sql.append("AND CUST_ID = '" + inputVO.getCUST_ID().toUpperCase() + "' ");
		sql.append("AND CONT_RSLT IS NULL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}

	// KYC檢核
	private List<Map<String, Object>> checkKYC(IOT110InputVO inputVO, CustKYCDataVO custKYCDataVO) throws Exception {
		IOT110OutputVO return_VO = new IOT110OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		String errorMsg = "";
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		Date now = df.parse(df.format(new Date()));
		Date kycDueDate = df.parse(df.format(custKYCDataVO.getKycDueDate().getTime()));

		if(custKYCDataVO.getKycLevel() != null){
			if(kycDueDate.compareTo(now) >= 0){
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sql2 = new StringBuffer();
				sql2.append("SELECT TARGET_ID, LINKED_NAME, PRD_RISK, TARGET_CURR, COM_PRD_RISK ");
				sql2.append(" FROM TBPRD_INS_LINKING ");
				sql2.append("WHERE INSPRD_ID = :INSPRD_ID ");
				sql2.append("AND substrb(PRD_RISK, 2, 1) <= substrb(:risk, 2, 1)");
				//要保人高齡限制P值
				if(StringUtils.isNotBlank(inputVO.getC_SENIOR_PVAL())) {
					sql2.append("AND substrb(PRD_RISK, 2, 1) <= substrb(:seniorPVal, 2, 1)");
					queryCondition.setObject("seniorPVal", inputVO.getC_SENIOR_PVAL());
				}
				queryCondition.setObject("risk", custKYCDataVO.getKycLevel());
				queryCondition.setObject("INSPRD_ID", inputVO.getINSPRD_ID().toString());
				queryCondition.setQueryString(sql2.toString());

				List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
				for(int i = 0 ; i < list2.size() ; i++){
					list2.get(i).put("CUST_RISK_AFR", custKYCDataVO.getKycLevel());
				}
				return_VO.setKYCList(list2);
				return return_VO.getKYCList();
			}else{
				errorMsg = "KYC截止日到期";
				throw new JBranchException(errorMsg);
			}
		}else{
			return_VO.setKYCList(new ArrayList<Map<String,Object>>());
			return return_VO.getKYCList();
		}

	}

	// 非投資型但須適配商品KYC檢核
	private void checkKYCNotInv(IOT110InputVO inputVO, CustKYCDataVO custKYCDataVO) throws Exception {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		Date now = df.parse(df.format(new Date()));
		Date kycDueDate = df.parse(df.format(custKYCDataVO.getKycDueDate().getTime()));
		String kycLevel = custKYCDataVO.getKycLevel();
		Boolean isAbleOverPval = false; //是否可越級
		inputVO.setNeedCalHnwcRiskValue(false); //是否需要做風險權值檢核
		
		//新契約且非高齡(70歲以上)且非C4等級且為高資產客戶且非高資產特定客戶，可越級
		if(StringUtils.equals("1", inputVO.getREG_TYPE()) && !StringUtils.equals("Y", inputVO.getCUST_OVER_70()) && !StringUtils.equals("C4", custKYCDataVO.getKycLevel())) {
			//取得要保人高資產註記
			SOT714 sot714 = (SOT714) PlatformContext.getBean("sot714");
			CustHighNetWorthDataVO hnwcData = sot714.getHNWCData(inputVO.getCUST_ID().toUpperCase());
			//為高資產客戶且非高資產特定客戶
			if(hnwcData != null && StringUtils.equals("Y", hnwcData.getValidHnwcYN()) && 
					StringUtils.equals("Y", hnwcData.getHnwcService()) && !StringUtils.equals("Y", hnwcData.getSpFlag())) {
				//可越級
				kycLevel = "C" + ObjectUtils.toString((Integer.parseInt(kycLevel.substring(1)) + 1));
				//計算可越級投資比例用
				inputVO.setSENIOR_OVER_PVAL(kycLevel);
				inputVO.setHnwcDataVO(hnwcData);
				isAbleOverPval = true; //可越級
			}
		}
		
		if(custKYCDataVO.getKycLevel() != null){
			if(kycDueDate.compareTo(now) >= 0){
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sql2 = new StringBuffer();
				sql2.append("SELECT PRD_RISK, CURR_CD FROM TBPRD_INS_MAIN ");
				sql2.append("WHERE INSPRD_KEYNO = :INSPRD_KEYNO ");
				sql2.append("AND substrb(PRD_RISK, 2, 1) <= substrb(:risk, 2, 1)");
				queryCondition.setObject("risk", kycLevel);
				queryCondition.setObject("INSPRD_KEYNO", inputVO.getINSPRD_KEYNO().toString());
				queryCondition.setQueryString(sql2.toString());

				List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
				if(CollectionUtils.isEmpty(list2)) {
					throw new JBranchException("商品風險等級與客戶風險等級不符");
				} else if(isAbleOverPval) {
					//可越級，則看是否有越級，有越級才做風險權值檢核
					String prdRisk = ObjectUtils.toString(list2.get(0).get("PRD_RISK"));
					if(StringUtils.isNotBlank(prdRisk) && Integer.parseInt(prdRisk.substring(1)) >= Integer.parseInt(kycLevel.substring(1))) {
						inputVO.setNeedCalHnwcRiskValue(true);
					}
				}
			} else {
				throw new JBranchException("KYC截止日到期");
			}
		} else {
			throw new JBranchException("需先執行KYC鍵機");
		}

	}
	
	/**
	 * 檢核要保人高齡非專投，是否可買P3商品 (原為P2,名稱就不改了)
	 * 符合下面條件，可買P3商品
	 * a.	該ID有購買過投資型商品(保單主檔item_remrk='U'或'F')
	 * b.	該ID 保單契約狀態(CNCT_STAT)需為'01'(有效)、'02'(催告)、'10'(停效)、'21'(解約)，依要保書申請日排序取小值
	 * c.	新單要保書申請日-庫存保單要保書申請日需>180天
	 * @param inputVO
	 * @param custKYCDataVO
	 * @return true:可買P2商品 false:不可買P3商品
	 * @throws Exception
	 */
	public boolean validSeniorInvP2(IOT110InputVO inputVO, CustKYCDataVO custKYCDataVO) throws Exception {
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		
		//富壽保單
		sb.append(" SELECT 1 ");
		sb.append(" FROM TBCRM_NPOLM A ");
		sb.append(" LEFT JOIN ( ");
		sb.append(" 	SELECT T.*, ROW_NUMBER()OVER (PARTITION BY POLICY_NO, POLICY_SEQ, ID_DUP, ITEM, ID_NO ORDER BY CREATETIME DESC) RN  "); 
		sb.append(" 	FROM TBCRM_FBRNY8A0 T ) FBRNY8A0  ");
		sb.append(" 	ON  A.POLICY_NO = FBRNY8A0.POLICY_NO ");   
		sb.append(" 	AND A.POLICY_SEQ = FBRNY8A0.POLICY_SEQ ");
		sb.append(" 	AND A.PRD_ID = FBRNY8A0.ITEM ");
		sb.append(" 	AND A.ID_DUP = FBRNY8A0.ID_DUP ");
		sb.append(" 	AND FBRNY8A0.RN = 1 ");
		sb.append(" WHERE A.APPL_ID = :CUST_ID ");
		sb.append(" AND NVL(A.POLICY_STATUS,'01') in ('01','02','04','05','06','07','10','11','12','16','20','21','22','25','27','28') ");
		sb.append(" AND A.ITEM_REMRK in ('U','F') ");
		sb.append(" AND FBRNY8A0.CNCT_STAT in ('01', '02', '10', '21') ");
		sb.append(" AND (TRUNC(:applyDate) - TRUNC(A.APPL_DATE)) > 180 ");
		sb.append(" UNION ");
		//日盛保單
		sb.append(" SELECT 1 ");
		sb.append(" FROM TBJSB_AST_INS_MAST ");
		sb.append(" WHERE CUST_ID = :CUST_ID ");
		sb.append(" AND REPLACE(TRIM(CONTRACT_TEXT), ' ', '') IN (SELECT PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'IOT.JSB_CONTRACT_TEXT_CHK') ");
		sb.append(" AND SP_POLICY_NOTE = 'U' ");
		sb.append(" AND (TRUNC(:applyDate) - TRUNC(APPL_DATE)) > 180 ");
		qc.setObject("CUST_ID", inputVO.getCUST_ID());
		//新契約：日期為要保書申請日；基金標的異動：日期為系統日
		qc.setObject("applyDate", StringUtils.equals("1", inputVO.getREG_TYPE()) ? inputVO.getAPPLY_DATE() : new Timestamp(System.currentTimeMillis()));
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(qc);
		
		return CollectionUtils.isNotEmpty(list) ? true : false;
	}
	
	//購買投資型商品取得高齡可購買P值
	public void getSeniorPVal(Object body, IPrimitiveMap header) throws Exception {
		IOT110InputVO inputVO = (IOT110InputVO) body;
		IOT110OutputVO outputVO = new IOT110OutputVO();
		
		//先取得客戶KYC資料
		CustKYCDataVO custKYCDataVO = new CustKYCDataVO();
		if(inputVO.getREG_TYPE().matches("1|2") && !"1".equals(inputVO.getPRODUCT_TYPE()) || StringUtils.equals("Y", inputVO.getNEED_MATCH())) {			
			SOT701InputVO sot701inputVO = new SOT701InputVO();
			sot701inputVO.setCustID(inputVO.getCUST_ID().toUpperCase());

			SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
			custKYCDataVO = sot701.getCustKycData(sot701inputVO);
		}
		
		/**要保人為高齡客戶(年齡>=64.5歲)購買投資型商品，檢核KYC**/
		if(inputVO.getREG_TYPE().matches("1|2") && StringUtils.equals("Y", inputVO.getC_SALE_SENIOR_YN()) && !"1".equals(inputVO.getPRODUCT_TYPE())) {
			inputVO.setC_SENIOR_PVAL("P3"); //高齡預設可買P3商品
			if(!StringUtils.equals("Y", inputVO.getPRO_YN())) {
				//要保人為高齡非專投，需檢核是否可買投資P3商品
				inputVO.setC_SENIOR_PVAL(validSeniorInvP2(inputVO, custKYCDataVO) ? "P3" : "P2");
			}			
		}
		outputVO.setC_SENIOR_PVAL(inputVO.getC_SENIOR_PVAL());
		
		this.sendRtnObject(outputVO);
	}
	
	// 儲存保單
	public void saveData(Object body, IPrimitiveMap header) throws Exception {

		initUUID();
		
		List<Map<String, Object>> checkKYCList = new ArrayList<Map<String,Object>>();
		IOT110InputVO inputVO = (IOT110InputVO) body;
		IOT110OutputVO outputVO = new IOT110OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2); //UHRM
		
		String ErrorMsg = "";
//		XmlInfo xmlInfo = new XmlInfo();

		//若為視訊投保，送出覆核及核可時，需檢查是否影像品質檢核項目皆有通過
		if(StringUtils.equals("Y", inputVO.getMAPPVIDEO_YN()) && inputVO.getSTATUS().matches("2|3")) {
			IOT920 iot920 = (IOT920) PlatformContext.getBean("iot920");
			IOT920InputVO inputVO920 = new IOT920InputVO();
			inputVO920.setPREMATCH_SEQ(inputVO.getPREMATCH_SEQ());
			inputVO920.setMAPP_CHKLIST_TYPE(inputVO.getMAPP_CHKLIST_TYPE());
			if(!iot920.validMAPPVideoCheck(inputVO920)) {
				if(StringUtils.equals("2", inputVO.getSTATUS()))
					throw new APException("視訊簽單影像未完成確認請勾選，或若有項目為不通過應填寫不通過原因。");
				else
					throw new APException("視訊簽單影像未完成確認請勾選，或若有項目為不通過應填寫不通過原因。");
			}
		}
		
		//送出覆核時，檢核投資屬性問卷分數
		//1. 不可以點選：投資屬性問卷同一要保書申請日分數不同，請修改
		//2. 可以點選，顯示訊息：近期送件，投資屬性問卷分數不同，請說明
		//其他，可以點選通過
//		if(!StringUtils.equals("1", inputVO.getPRODUCT_TYPE()) && StringUtils.isNotBlank(inputVO.getINV_SCORE())) {
//			String invScoreChk = checkInvScore(inputVO);
//			inputVO.setINV_SCORE_CHK(invScoreChk);
//			outputVO.setInvScoreChk(invScoreChk);
//			
//			if(StringUtils.equals("2", inputVO.getSTATUS())) { //送出覆核時
//				if(StringUtils.equals("1", invScoreChk)) {
//					throw new APException("投資屬性問卷同一要保書申請日分數不同，請修改");
//				} else if(StringUtils.equals("2", invScoreChk) && StringUtils.isBlank(inputVO.getLOAN_SOURCE_REMARK())) {
//					//近期有投資型屬性問卷分數不同，要檢查客戶保費來源/投資屬性說明不能為空白，若是空白無法送出覆核
//					throw new APException("近期有投資型屬性問卷分數不同，客戶保費來源/投資屬性說明不能為空白");
//				}
//			}
//			
//		}
		
		//若需適配，先取得客戶KYC資料
		CustKYCDataVO custKYCDataVO = new CustKYCDataVO();
		if(inputVO.getREG_TYPE().matches("1|2") && !"1".equals(inputVO.getPRODUCT_TYPE()) || StringUtils.equals("Y", inputVO.getNEED_MATCH())) {			
			SOT701InputVO sot701inputVO = new SOT701InputVO();
			sot701inputVO.setCustID(inputVO.getCUST_ID().toUpperCase());

			SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
			custKYCDataVO = sot701.getCustKycData(sot701inputVO);
		}
		
		/**要保人為高齡客戶(年齡>=64.5歲)購買投資型商品，檢核KYC**/
		//選擇基金標的的時候已先取得PVAL，這裡不用再做
//		inputVO.setC_SENIOR_PVAL("");
//		if(inputVO.getREG_TYPE().matches("1|2") && StringUtils.equals("Y", inputVO.getC_SALE_SENIOR_YN()) && !"1".equals(inputVO.getPRODUCT_TYPE())) {
//			inputVO.setC_SENIOR_PVAL("P2"); //高齡預設可買P2商品
//			if(!StringUtils.equals("Y", inputVO.getPRO_YN())) {
//				//要保人為高齡非專投，需檢核是否可買投資P2商品
//				inputVO.setC_SENIOR_PVAL(validSeniorInvP2(inputVO, custKYCDataVO) ? "P2" : "P1");
//			}			
//		}
//		outputVO.setC_SENIOR_PVAL(inputVO.getC_SENIOR_PVAL());
				
		/**如果是投資型商品需要檢核KYC**/
		if(inputVO.getREG_TYPE().matches("1|2") && !"1".equals(inputVO.getPRODUCT_TYPE())){
			checkKYCList = checkKYC(inputVO, custKYCDataVO);
			
			if(CollectionUtils.isEmpty(checkKYCList)) {
				if(StringUtils.equals("Y", inputVO.getC_SALE_SENIOR_YN())) { //高齡投資
					if(StringUtils.equals("Y", inputVO.getPRO_YN())) {
						throw new APException("高齡專投客戶只能申購風險值為P3(含)以下之標的。"); //高齡專投
					} else {
						throw new APException("高齡客戶無相關投資經驗只能申購風險值為P2之標的。"); //高齡非專投
					}
				}
				
				ErrorMsg = "ehl_01_iot110_005";
				throw new APException(ErrorMsg);
			}
		}
		/**非投資型但須適配商品**/
		if(inputVO.getREG_TYPE().matches("1|2") && StringUtils.equals("1", inputVO.getPRODUCT_TYPE()) && StringUtils.equals("Y", inputVO.getNEED_MATCH())) {
			checkKYCNotInv(inputVO, custKYCDataVO);
			//若可越級適配，高資產客戶投組風險權值檢核
			if(inputVO.isNeedCalHnwcRiskValue()) {
				//高資產客戶投組風險權值檢核
				if(!validHnwcRiskValue(inputVO)) {
					throw new APException("客戶加計此筆投資之越級比率已超標");
				}
			}
		}

		String chk_Ab = "N";
		if("1".equals(inputVO.getREG_TYPE())){
			//非常態錄音判斷
			if(StringUtils.equals("N", inputVO.getC_SALE_SENIOR_YN()) && //要保人若已有高齡銷售錄音註記則不需再做非常態錄音檢核
					(!"1".equals(inputVO.getPRODUCT_TYPE()) || ("1".equals(inputVO.getPRODUCT_TYPE()) && !"TWD".equals(inputVO.getCURR_CD())))) { 
				String First_buy = "";
				IOT920 iot920 = (IOT920) PlatformContext.getBean("iot920");
				FirstBuyInputVO iot920_FBInputVO = new FirstBuyInputVO();
				FirstBuyDataVO iot920_FBOutputVO = new FirstBuyDataVO();

				iot920_FBInputVO.setINSPRD_TYPE(inputVO.getPRODUCT_TYPE());
				iot920_FBInputVO.setCURR_CD(inputVO.getCURR_CD());
				iot920_FBInputVO.setCUST_ID(inputVO.getCUST_ID().toUpperCase());
				//判斷首購
				iot920_FBOutputVO = iot920.chk_FirstBuy(iot920_FBInputVO);
				First_buy = iot920_FBOutputVO.getFirstBuy_YN();
				//非常態錄音電訪判斷
				chk_AbInputVO iot920_AbinputVO = new chk_AbInputVO();
				chk_AbOutputVO iot920_AbOutputVO = new chk_AbOutputVO();
				iot920_AbinputVO.setREAL_PREMIUM(ObjectUtils.toString(inputVO.getREAL_PREMIUM().intValue()));
				iot920_AbinputVO.setPAY_TYPE(inputVO.getPAY_TYPE());
				iot920_AbinputVO.setMOP2(inputVO.getMOP2());
				iot920_AbinputVO.setAB_EXCH_RATE(inputVO.getAB_EXCH_RATE());
				iot920_AbinputVO.setUNDER_YN(inputVO.getUNDER_YN());
				iot920_AbinputVO.setPRO_YN(inputVO.getPRO_YN());
				iot920_AbinputVO.setFirstBuy_YN(First_buy);
				iot920_AbOutputVO = iot920.chk_Ab(iot920_AbinputVO);
				chk_Ab = iot920_AbOutputVO.getAbTranSEQ_YN();
				if("Y".equals(chk_Ab)){
					outputVO.setPrintAb(true);
				}else{
					outputVO.setPrintAb(false);
				}
			}
		}
		inputVO.setAB_YN(chk_Ab);
		
		//取得適合度檢核編號
		if(StringUtils.isBlank(inputVO.getPREMATCH_SEQ())) {
			inputVO.setPREMATCH_SEQ(getPrematchSeq());
		}

		//資料寫入購買檢核檔
		TBIOT_PREMATCHVO tpvo = (TBIOT_PREMATCHVO) dam.findByPKey(TBIOT_PREMATCHVO.TABLE_UID, inputVO.getPREMATCH_SEQ());
		if(tpvo == null) {
			//適合度檢核
			tpvo = new TBIOT_PREMATCHVO();
			putTBIOT_PREMATCH(tpvo, inputVO);
			//適配日只記錄第一次適配日期時間
			tpvo.setMATCH_DATE(inputVO.getMATCH_DATE());
			
			if (uhrmMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sb = new StringBuffer();
				
				sb.append("SELECT BRANCH_NBR ");
				sb.append("FROM TBORG_UHRM_BRH ");
				sb.append("WHERE EMP_ID = :loginID ");

				queryCondition.setObject("loginID", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
				queryCondition.setQueryString(sb.toString());

				List<Map<String, Object>> loginBreach = dam.exeQuery(queryCondition);
				
				if (loginBreach.size() > 0) {
					tpvo.setBRANCH_NBR((String) loginBreach.get(0).get("BRANCH_NBR"));
				} else {
					throw new APException("人員無有效分行"); //顯示錯誤訊息
				}
			} else {
				//分行只記錄建立者分行
				tpvo.setBRANCH_NBR(ObjectUtils.toString(SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINBRH)));
			}
			
			//TBIOT_PREMATCH 新增
			dam.create(tpvo);
			
			//寫入基金適配標的清單
			putTBIOT_FUND_LINK(inputVO);
		} else {
			//核可或退回，寫入覆核ID & 時間
			if(inputVO.getSTATUS().matches("3|4")) {
				//核可且有高齡客戶，須檢核關懷主管員編角色，但契撤不須檢核
				if(inputVO.getSTATUS().matches("3") && !StringUtils.equals("Y", inputVO.getCANCEL_CONTRACT_YN()) &&
						(StringUtils.equals("Y", inputVO.getC_SALE_SENIOR_YN()) || 
							StringUtils.equals("Y", inputVO.getI_SALE_SENIOR_YN()) || 
							StringUtils.equals("Y", inputVO.getP_SALE_SENIOR_YN()))) {
					if(!validSeniorAuthRole(inputVO.getSENIOR_AUTH_ID())) {
						throw new APException("關懷主管不符合規範");
					}
				}
				tpvo.setSIGNOFF_ID(ObjectUtils.toString(SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID)));
				tpvo.setSIGNOFF_DATE(new Timestamp(new Date().getTime()));
				tpvo.setSENIOR_AUTH_OPT(inputVO.getSENIOR_AUTH_OPT());
				tpvo.setSENIOR_AUTH_OPT2(inputVO.getSENIOR_AUTH_OPT2());
				tpvo.setSENIOR_AUTH_OPT3(inputVO.getSENIOR_AUTH_OPT3());
				tpvo.setSENIOR_AUTH_OPT4(inputVO.getSENIOR_AUTH_OPT4());
				tpvo.setSTATUS(inputVO.getSTATUS());
				tpvo.setSENIOR_AUTH_REMARKS(inputVO.getSENIOR_AUTH_REMARKS());
				tpvo.setSENIOR_AUTH_ID(inputVO.getSENIOR_AUTH_ID());
				
				//TBIOT_PREMATCH 更新
				dam.update(tpvo);
			} else {
				//適合度檢核
				putTBIOT_PREMATCH(tpvo, inputVO);
				//TBIOT_PREMATCH 更新
				dam.update(tpvo);
				
				//寫入基金適配標的清單
				putTBIOT_FUND_LINK(inputVO);
			}			
		}

		//新契約或要保人變更且適合度檢核/送出覆核，需要寫入"客戶保險購買檢核表PDF資料檔"
		if((StringUtils.equals("1", inputVO.getREG_TYPE()) && inputVO.getSTATUS().matches("1|2"))
				|| (StringUtils.equals("3", inputVO.getREG_TYPE()) && inputVO.getSTATUS().matches("1|2|3"))) {
			String pdfURL = "";
			if(StringUtils.equals("1", inputVO.getREG_TYPE())) {
				//新契約
				pdfURL = genPrematchPdfURL(inputVO);
			} else if(StringUtils.equals("3", inputVO.getREG_TYPE())) {
				//要保人變更
				pdfURL = genChgCustPdfURL(inputVO);
			}

			String serverPath = (String) SysInfo.getInfoValue(SystemVariableConsts.SERVER_PATH);
			byte[] reportData = Files.readAllBytes(new File(serverPath, pdfURL).toPath());

			TBIOT_PREMATCH_PDFVO pvo = (TBIOT_PREMATCH_PDFVO) dam.findByPKey(TBIOT_PREMATCH_PDFVO.TABLE_UID, inputVO.getPREMATCH_SEQ());
			if(pvo == null) {
				pvo = new TBIOT_PREMATCH_PDFVO();
				pvo.setPREMATCH_SEQ(inputVO.getPREMATCH_SEQ());
				pvo.setPDF_FILE(ObjectUtil.byteArrToBlob(reportData));

				dam.create(pvo);
			} else {
				pvo.setPREMATCH_SEQ(inputVO.getPREMATCH_SEQ());
				pvo.setPDF_FILE(ObjectUtil.byteArrToBlob(reportData));

				dam.update(pvo);
			}
		}

		outputVO.setSavepass(true);
		outputVO.setPrematchSeq(inputVO.getPREMATCH_SEQ());
		outputVO.setKYCList(checkKYCList);
		outputVO.setCALLOUT_YN(getCallOutYN(inputVO.getPREMATCH_SEQ())); //是否有電訪預約紀錄
		
		this.sendRtnObject(outputVO);
	}

	/***
	 * 是否有電訪預約紀錄，且非為"未申請"以及"取消電訪"狀態
	 * 1.未申請 2.電訪預約中 3.電訪處理中 4.電訪成功 5.電訪未成功 6.電訪疑義 7.取消電訪 8.退件處理-契撤
	 * @param prematchSeq
	 * @return Y:是 N:否
	 * @throws JBranchException 
	 * @throws DAOException 
	 */
	private String getCallOutYN(String prematchSeq) throws DAOException, JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		
		sb.append("SELECT 1 FROM TBIOT_CALLOUT ");
		sb.append(" WHERE PREMATCH_SEQ = :prematchSeq ");
		sb.append(" AND STATUS IN ('2', '3', '4', '5', '6', '8') ");
		qc.setObject("prematchSeq", prematchSeq);
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> chkList = dam.exeQuery(qc);

		return (CollectionUtils.isEmpty(chkList) ? "N" : "Y");
	}
	
	private void putTBIOT_PREMATCH(TBIOT_PREMATCHVO tpvo, IOT110InputVO inputVO) throws JBranchException {
		tpvo.setPREMATCH_SEQ(inputVO.getPREMATCH_SEQ());
		tpvo.setINS_KIND("1");
		tpvo.setINS_ID(inputVO.getINS_ID());
		tpvo.setCASE_ID(inputVO.getCASE_ID());
		tpvo.setPOLICY_NO1(StringUtils.isNotBlank(inputVO.getPOLICY_NO1()) ? inputVO.getPOLICY_NO1().toUpperCase() : null);
		tpvo.setPOLICY_NO2(inputVO.getPOLICY_NO2());
		tpvo.setPOLICY_NO3(inputVO.getPOLICY_NO3());
		tpvo.setREG_TYPE(inputVO.getREG_TYPE());
		tpvo.setOTH_TYPE(inputVO.getOTH_TYPE());		
		tpvo.setCUST_ID(inputVO.getCUST_ID());
		tpvo.setCUST_RISK(inputVO.getCUST_RISK());
		tpvo.setCUST_RISK_DUE(inputVO.getCUST_RISK_DUE());
		tpvo.setAO_CODE(inputVO.getAO_CODE());
		tpvo.setPROPOSER_NAME(inputVO.getPROPOSER_NAME());
		tpvo.setPROPOSER_BIRTH(inputVO.getPROPOSER_BIRTH());
		tpvo.setINSURED_ID(inputVO.getINSURED_ID());
		tpvo.setINSURED_NAME(inputVO.getINSURED_NAME());
		tpvo.setREPRESET_ID(inputVO.getREPRESET_ID());
		tpvo.setREPRESET_NAME(inputVO.getREPRESET_NAME());
		tpvo.setREPRESET_CM_FLAG(inputVO.getREPRESET_CM_FLAG());
		tpvo.setRLT_BT_PROREP(inputVO.getRLT_BT_PROREP());
		tpvo.setPAYER_ID(inputVO.getPAYER_ID());
		tpvo.setPAYER_NAME(inputVO.getPAYER_NAME());
		tpvo.setRLT_BT_PROPAY(inputVO.getRLT_BT_PROPAY());
		tpvo.setINSPRD_KEYNO(inputVO.getINSPRD_KEYNO());
		tpvo.setINSPRD_ID(inputVO.getINSPRD_ID());
		tpvo.setREAL_PREMIUM(inputVO.getREAL_PREMIUM());
		tpvo.setBASE_PREMIUM(inputVO.getBASE_PREMIUM());
		tpvo.setMOP2(inputVO.getMOP2());
		tpvo.setLOAN_SOURCE_YN(inputVO.getLOAN_SOURCE_YN());
		tpvo.setLOAN_SOURCE2_YN(inputVO.getLOAN_SOURCE2_YN());
		tpvo.setAPPLY_DATE(inputVO.getAPPLY_DATE());
		tpvo.setRECRUIT_ID(inputVO.getRECRUIT_ID());
		tpvo.setPROPOSER_CM_FLAG(inputVO.getPROPOSER_CM_FLAG());
		tpvo.setINSURED_CM_FLAG(inputVO.getINSURED_CM_FLAG());
		tpvo.setPAYER_CM_FLAG(inputVO.getPAYER_CM_FLAG());
		tpvo.setAML(inputVO.getAML());
		tpvo.setPRECHECK(inputVO.getPRECHECK());
		tpvo.setPROPOSER_INCOME1(inputVO.getPROPOSER_INCOME1());
		tpvo.setPROPOSER_INCOME2(inputVO.getPROPOSER_INCOME2());
		tpvo.setPROPOSER_INCOME3(inputVO.getPROPOSER_INCOME3());
		tpvo.setINSURED_INCOME1(inputVO.getINSURED_INCOME1());
		tpvo.setINSURED_INCOME2(inputVO.getINSURED_INCOME2());
		tpvo.setINSURED_INCOME3(inputVO.getINSURED_INCOME3());
		tpvo.setLOAN_CHK1_YN(inputVO.getLOAN_CHK1_YN());
		tpvo.setLOAN_CHK2_YN(inputVO.getLOAN_CHK2_YN());
		tpvo.setLOAN_CHK3_YN(inputVO.getLOAN_CHK3_YN());
		tpvo.setLOAN_CHK4_YN(inputVO.getLOAN_CHK4_YN());
		tpvo.setCD_CHK_YN(inputVO.getCD_CHK_YN());
		tpvo.setC_LOAN_CHK1_YN(inputVO.getC_LOAN_CHK1_YN());
		tpvo.setC_LOAN_CHK2_YN(inputVO.getC_LOAN_CHK2_YN());
		tpvo.setC_LOAN_CHK3_YN(inputVO.getC_LOAN_CHK3_YN());
		tpvo.setC_LOAN_CHK4_YN(inputVO.getC_LOAN_CHK4_YN());
		tpvo.setC_CD_CHK_YN(inputVO.getC_CD_CHK_YN());
		tpvo.setI_LOAN_CHK1_YN(inputVO.getI_LOAN_CHK1_YN());
		tpvo.setI_LOAN_CHK2_YN(inputVO.getI_LOAN_CHK2_YN());
		tpvo.setI_LOAN_CHK3_YN(inputVO.getI_LOAN_CHK3_YN());
		tpvo.setI_LOAN_CHK4_YN(inputVO.getI_LOAN_CHK4_YN());
		tpvo.setI_CD_CHK_YN(inputVO.getI_CD_CHK_YN());
		tpvo.setINCOME_REMARK(inputVO.getINCOME_REMARK());
		tpvo.setLOAN_SOURCE_REMARK(inputVO.getLOAN_SOURCE_REMARK());
		tpvo.setSTATUS(inputVO.getSTATUS());
		tpvo.setCHG_CUST_ID(inputVO.getCHG_CUST_ID());
		tpvo.setCHG_PROPOSER_NAME(inputVO.getCHG_PROPOSER_NAME());
		tpvo.setCHG_PROPOSER_BIRTH(inputVO.getCHG_PROPOSER_BIRTH());
		tpvo.setCONTRACT_END_YN(inputVO.getCONTRACT_END_YN());
		tpvo.setS_INFITEM_LOAN_YN(inputVO.getS_INFITEM_LOAN_YN());
		tpvo.setPROPOSER_WORK(inputVO.getPROPOSER_WORK());
		tpvo.setINSURED_WORK(inputVO.getINSURED_WORK());
		tpvo.setC_LOAN_APPLY_DATE(inputVO.getC_LOAN_APPLY_DATE());
		tpvo.setC_PREM_DATE(inputVO.getC_PREM_DATE());
		tpvo.setI_LOAN_APPLY_DATE(inputVO.getI_LOAN_APPLY_DATE());
		tpvo.setP_LOAN_APPLY_DATE(inputVO.getP_LOAN_APPLY_DATE());
		tpvo.setC_LOAN_APPLY_YN(inputVO.getC_LOAN_APPLY_YN());
		tpvo.setI_LOAN_APPLY_YN(inputVO.getI_LOAN_APPLY_YN());
		tpvo.setP_LOAN_APPLY_YN(inputVO.getP_LOAN_APPLY_YN());
		tpvo.setAB_SENIOR_YN(inputVO.getAB_SENIOR_YN());
		tpvo.setC_SALE_SENIOR_YN(inputVO.getC_SALE_SENIOR_YN());
		tpvo.setI_SALE_SENIOR_YN(inputVO.getI_SALE_SENIOR_YN());
		tpvo.setP_SALE_SENIOR_YN(inputVO.getP_SALE_SENIOR_YN());
		tpvo.setC_LOAN_CHK2_DATE(inputVO.getC_LOAN_CHK2_DATE());
		tpvo.setI_LOAN_CHK2_DATE(inputVO.getI_LOAN_CHK2_DATE());
		tpvo.setLOAN_CHK2_DATE(inputVO.getLOAN_CHK2_DATE());
		tpvo.setMAPPVIDEO_YN(inputVO.getMAPPVIDEO_YN());
		tpvo.setAGE_UNDER20_YN(inputVO.getAGE_UNDER20_YN());
		tpvo.setINSURED_BIRTH(inputVO.getINSURED_BIRTH());
		tpvo.setPAYER_BIRTH(inputVO.getPAYER_BIRTH());
		tpvo.setINV_SCORE(inputVO.getINV_SCORE());
		tpvo.setC_KYC_INCOME(inputVO.getC_KYC_INCOME());
		tpvo.setI_KYC_INCOME(inputVO.getI_KYC_INCOME());
		tpvo.setSENIOR_AUTH_OPT(inputVO.getSENIOR_AUTH_OPT());
		tpvo.setSENIOR_AUTH_OPT2(inputVO.getSENIOR_AUTH_OPT2());
		tpvo.setSENIOR_AUTH_OPT3(inputVO.getSENIOR_AUTH_OPT3());
		tpvo.setSENIOR_AUTH_OPT4(inputVO.getSENIOR_AUTH_OPT4());
		tpvo.setC_SALE_SENIOR_TRANSSEQ(inputVO.getC_SALE_SENIOR_TRANSSEQ());
		tpvo.setI_SALE_SENIOR_TRANSSEQ(inputVO.getI_SALE_SENIOR_TRANSSEQ());
		tpvo.setP_SALE_SENIOR_TRANSSEQ(inputVO.getP_SALE_SENIOR_TRANSSEQ());
		tpvo.setINV_SCORE_CHK(inputVO.getINV_SCORE_CHK());
		tpvo.setCUST_DEBIT(inputVO.getCUST_DEBIT());
		tpvo.setINSURED_DEBIT(inputVO.getINSURED_DEBIT());
		tpvo.setMAPPVIDEO_AGENTMEMO(inputVO.getMAPPVIDEO_AGENTMEMO());
		tpvo.setC_SENIOR_PVAL(inputVO.getC_SENIOR_PVAL());
		tpvo.setDIGITAL_AGREESIGN_YN(inputVO.getDIGITAL_AGREESIGN_YN());
		tpvo.setFB_COM_YN(inputVO.getFB_COM_YN());
		tpvo.setCOMPANY_NUM(inputVO.getCOMPANY_NUM());
		tpvo.setAB_YN(inputVO.getAB_YN());
		tpvo.setC_CD_DUE_DATE(inputVO.getC_CD_DUE_DATE());
		tpvo.setI_CD_DUE_DATE(inputVO.getI_CD_DUE_DATE());
		tpvo.setP_CD_DUE_DATE(inputVO.getP_CD_DUE_DATE());
		tpvo.setCANCEL_CONTRACT_YN(StringUtils.isEmpty(inputVO.getCANCEL_CONTRACT_YN()) ? "N" : inputVO.getCANCEL_CONTRACT_YN());
		tpvo.setDATA_SHR_YN(inputVO.getDATA_SHR_YN());
		tpvo.setSENIOR_OVER_PVAL(inputVO.getSENIOR_OVER_PVAL());
		tpvo.setWMSHAIA_SEQ(inputVO.getWMSHAIA_SEQ());
		tpvo.setOVER_PVAL_AMT(inputVO.getOVER_PVAL_AMT());
		tpvo.setOVER_PVAL_MAX_AMT(inputVO.getOVER_PVAL_MAX_AMT());
		tpvo.setSENIOR_AUTH_REMARKS(inputVO.getSENIOR_AUTH_REMARKS());
		tpvo.setSENIOR_AUTH_ID(inputVO.getSENIOR_AUTH_ID());
		tpvo.setC_PREMIUM_TRANSSEQ_YN(inputVO.getC_PREMIUM_TRANSSEQ_YN());
		tpvo.setI_PREMIUM_TRANSSEQ_YN(inputVO.getI_PREMIUM_TRANSSEQ_YN());
		tpvo.setP_PREMIUM_TRANSSEQ_YN(inputVO.getP_PREMIUM_TRANSSEQ_YN());
		tpvo.setC_REVOLVING_LOAN_YN(inputVO.getC_REVOLVING_LOAN_YN());
		tpvo.setI_REVOLVING_LOAN_YN(inputVO.getI_REVOLVING_LOAN_YN());
		tpvo.setP_REVOLVING_LOAN_YN(inputVO.getP_REVOLVING_LOAN_YN());
	}

	//寫入基金適配標的清單
	private void putTBIOT_FUND_LINK(IOT110InputVO inputVO) throws JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		if (inputVO.getINVESTList().size() > 0) {
			//將原本資料刪除
			sql.append(" DELETE FROM TBIOT_FUND_LINK WHERE PREMATCH_SEQ = :prematchSeq ");
			queryCondition.setObject("prematchSeq", inputVO.getPREMATCH_SEQ());
			queryCondition.setQueryString(sql.toString());
			dam.exeUpdate(queryCondition);
			
			//重新寫入標的資料
			TBIOT_FUND_LINKVO fLinkVO = new TBIOT_FUND_LINKVO();
			for (Map<String, Object> invest : inputVO.getINVESTList()) {
				fLinkVO = new TBIOT_FUND_LINKVO();
				BigDecimal seqKeyNo = getFundLinkKeyNo();
				
				fLinkVO.setSEQ_KEY_NO(seqKeyNo);
				fLinkVO.setPRD_LK_KEYNO(new BigDecimal(invest.get("PRD_LK_KEYNO").toString()));
				fLinkVO.setPREMATCH_SEQ(inputVO.getPREMATCH_SEQ());
				fLinkVO.setINSPRD_ID(inputVO.getINSPRD_ID());
				if (invest.get("ALLOCATION_RATIO") != null) {
					try {
						BigDecimal link_pct =new BigDecimal(invest.get("ALLOCATION_RATIO").toString());
						fLinkVO.setLINK_PCT(link_pct);
					} catch (Exception e) {
						fLinkVO.setLINK_PCT(new BigDecimal(0));
					}
				}
				dam.create(fLinkVO);
			}
		}
	}
	
	/***
	 * 取得購買檢核編碼
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void getPrematchSeq(Object body, IPrimitiveMap header) throws JBranchException {
		IOT110OutputVO outputVO = new IOT110OutputVO();

		outputVO.setPrematchSeq(getPrematchSeq());
		this.sendRtnObject(outputVO);
	}

	/***
	 * 取得購買檢核編碼
	 * @return
	 * @throws JBranchException
	 */
	public String getPrematchSeq() throws JBranchException {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		String year = df.format(new Date()).substring(2, 8);
		String prematchSeq = year.concat(getSEQ());

		//檢查是否有重複的PREMATCH_SEQ
		dam = this.getDataAccessManager();
		TBIOT_PREMATCHVO tpvo = (TBIOT_PREMATCHVO) dam.findByPKey(TBIOT_PREMATCHVO.TABLE_UID, prematchSeq);

		if(tpvo == null) {
			return prematchSeq;
		} else {
			//若有重複則繼續抓下一筆
			return getPrematchSeq();
		}
	}

	private String getSEQ() throws JBranchException {
		SerialNumberUtil sn = new SerialNumberUtil();
		String seqNum = "";
		try {
			seqNum = sn.getNextSerialNumber("IOT110_PREMATCH_SEQ");
		} catch (Exception e) {
			sn.createNewSerial("IOT110_PREMATCH_SEQ", "0000", 1, "d", null, 1, new Long("9999"), "y", new Long("0"), null);
			seqNum = sn.getNextSerialNumber("IOT110_PREMATCH_SEQ");
		}
		return seqNum;
	}

	// 修改保單
//	public void editData(Object body, IPrimitiveMap header)
//			throws JBranchException {
//		BigDecimal keyNo = getKeyNo();
//		IOT110InputVO inputVO = (IOT110InputVO) body;
//		dam = this.getDataAccessManager();
//		QueryConditionIF queryCondition = dam.getQueryCondition();
//		StringBuffer sql = new StringBuffer();
//
//		sql.append("INSERT  INTO  TBIOT_PREMATCH ");
//		sql.append("(INS_KEYNO, INS_KIND, OTH_TYPE, CUST_ID, INSPRD_ID, MATCH_DATE, CREATOR, CREATETIME ) ");
//		sql.append("VALUES (" + keyNo + ", " + " '1' " + ", '" + "5"
//				+ inputVO.getCUST_ID() + "', " + "'" + 1234 + "', " + "SYSDATE"
//				+ ", '" + inputVO.getEmpId() + "', SYSDATE" + ")");
//
//		queryCondition.setQueryString(sql.toString());
//		dam.exeUpdate(queryCondition);
//
//	}

	// 取得保險進件主檔主鍵
	public BigDecimal getKeyNo() throws JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT TBIOT_MAIN_SEQ.NEXTVAL  FROM  DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		BigDecimal keyNo = (BigDecimal) list.get(0).get("NEXTVAL");
		return keyNo;
	}

	// 取得保險商品基金標的配置檔主鍵
	public BigDecimal getFundLinkKeyNo() throws JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT TBIOT_FUND_LINK_SEQ.NEXTVAL FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		BigDecimal keyNo = (BigDecimal) list.get(0).get("NEXTVAL");
		return keyNo;
	}
		
	// 保險重要權益宣告書
	public void printInsVitIntDecBook(Object body, IPrimitiveMap header)
			throws JBranchException, FileNotFoundException, IOException {
		String url = null;
		String txnCode = "IOT110";
		String reportID = "R1";
		ReportIF report = null;
		IOT110OutputVO return_VO = (IOT110OutputVO) body;

		List<Map<String, Object>> list = return_VO.getList();
		List<Map<String, Object>> cust_list = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> insured_list = new ArrayList<Map<String,Object>>();
		//要保人題目
		dam = getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT (CASE WHEN Q_ID = 'Q07' THEN REPLACE(Q_NAME, '[:1]', INSPRD_ANNUAL) ELSE Q_NAME END) AS Q_NAME ")
			.append(" ,TEXT_STYLE_B, TEXT_STYLE_I, TEXT_STYLE_U, TEXT_STYLE_A ")	//宣告題目
				.append(" FROM VWPRD_INS_ANCDOC ")
				.append(" WHERE INSPRD_ID = :INSPRD_ID ")	//保險代號
				.append(" AND INSPRD_ANNUAL = :INSPRD_ANNUAL ")//繳費年期
				.append(" AND Q_TYPE = '1' ")	//要保人
				.append(" AND EFFECT_DATE <= SYSDATE AND EXPIRY_DATE >= SYSDATE ");
		qc.setObject("INSPRD_ID", list.get(0).get("INSPRD_ID"));
		qc.setObject("INSPRD_ANNUAL", list.get(0).get("INSPRD_ANNUAL"));
		qc.setQueryString(sb.toString());
		cust_list = dam.exeQuery(qc);

		//被保人題目
		dam = getDataAccessManager();
		qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append(" SELECT (CASE WHEN Q_ID = 'Q07' THEN REPLACE(Q_NAME, '[:1]', INSPRD_ANNUAL) ELSE Q_NAME END) AS Q_NAME ")
			.append(" ,TEXT_STYLE_B, TEXT_STYLE_I, TEXT_STYLE_U, TEXT_STYLE_A ")	//宣告題目
				.append(" FROM VWPRD_INS_ANCDOC ")
				.append(" WHERE INSPRD_ID = :INSPRD_ID ")	//保險代號
				.append(" AND INSPRD_ANNUAL = :INSPRD_ANNUAL ")//繳費年期
				.append(" AND Q_TYPE = '2' ")	//被保人
				.append(" AND EFFECT_DATE <= SYSDATE AND EXPIRY_DATE >= SYSDATE ");
		qc.setObject("INSPRD_ID", list.get(0).get("INSPRD_ID"));
		qc.setObject("INSPRD_ANNUAL", list.get(0).get("INSPRD_ANNUAL"));
		qc.setQueryString(sb.toString());
		insured_list = dam.exeQuery(qc);

		ReportFactory factory = new ReportFactory();
		ReportDataIF data = new ReportData();
		ReportGeneratorIF gen = factory.getGenerator();

		int i = 0;

		data.addParameter("CUST_ID", list.get(0).get("CUST_ID"));
		// 西元轉民國
		String year = "";
		String mon = "";
		String day = "";
		if(list.get(0).get("DATE") != null){
//			int keyin_date_year = (Integer.parseInt(list.get(0).get("DATE").toString().substring(0, 4)))-1911;//西元年換算民國年
//			year = String.valueOf(keyin_date_year);
//
//			mon = list.get(0).get("DATE").toString().substring(4, 7).replace("-", "/");
//			day = list.get(0).get("DATE").toString().substring(7, 10).replace("-", "/");

			String dayStr = list.get(0).get("DATE").toString();//  "2017-10-31 00:00:00.0";
			List<String> list2 = new ArrayList<String>();
			Pattern pattern = Pattern.compile("(\\d+)");

			Matcher mat = pattern.matcher(dayStr);

			while(mat.find()){
				list2.add(mat.group());
			}
			year = String.valueOf((Integer.parseInt(list2.get(0)) - 1911));
			mon = list2.get(1);
			day = list2.get(2);

		}else{
			Calendar cal = Calendar.getInstance();
			year = cal.get(Calendar.YEAR) - 1911 + "";
			mon = cal.get(Calendar.MONTH) + 1 + "";
			if(mon.length()<2){
				mon = "0"+mon;
			}
			day = cal.get(Calendar.DAY_OF_MONTH) + "";
			if(day.length()<2){
				day = "0"+day;
			}
			data.addParameter("DATE", year + mon + day);
		}
		data.addParameter("DATE", year + mon + day);

//		data.addParameter("DATE", list.get(0).get("DATE"));

		data.addRecordList("Script Mult Data Set", list);
		data.addRecordList("CUST", cust_list);
		data.addRecordList("INSURED", insured_list);
		report = gen.generateReport(txnCode, reportID, data);
		url = report.getLocation();
		String encryptUrl = PdfUtil.process(new PdfConfigVO(this.getDataAccessManager(), url));
		notifyClientToDownloadFile(encryptUrl, "保險重要權益宣告書.pdf");
	}

	// 投資型商品基金適配清單
	public void printInvOriAdaFunList(Object body, IPrimitiveMap header) throws JBranchException, FileNotFoundException, IOException {
		IOT110OutputVO return_VO = (IOT110OutputVO) body;		
		List<Map<String, Object>> list = return_VO.getList();
		
		//新契約高資產越級適配
		if("1".equals(list.get(0).get("INSTYPE")) && StringUtils.isNotBlank(ObjectUtils.toString(list.get(0).get("SENIOR_OVER_PVAL")))) {
			printInvOriAdaFunList(list);
			return;
		}
		
		String url = null;
		String txnCode = "IOT110";
		String reportID = "R2";
		ReportIF report = null;
		
		ReportFactory factory = new ReportFactory();
		ReportDataIF data = new ReportData();
		ReportGeneratorIF gen = factory.getGenerator();
		if("1".equals(list.get(0).get("INSTYPE")))
			data.addParameter("INSTYPE", "新契約");
		if("2".equals(list.get(0).get("INSTYPE")))
			data.addParameter("INSTYPE", "契約變更-基金標的異動");
		data.addParameter("CUST_ID", list.get(0).get("CUST_ID"));
		data.addParameter("CUST_NAME", list.get(0).get("CUST_NAME"));
		data.addParameter("EMP_ID", list.get(0).get("EMP_ID"));
		data.addParameter("INSPRD_ID", list.get(0).get("INSPRD_ID").toString().toUpperCase());
		data.addParameter("INSPRD_NAME", list.get(0).get("INSPRD_NAME"));
		data.addParameter("POLICY_NO1", list.get(0).get("POLICY_NO1")!=null?list.get(0).get("POLICY_NO1"):"");
		data.addParameter("POLICY_NO2", list.get(0).get("POLICY_NO2")!=null?list.get(0).get("POLICY_NO2"):"");
		data.addParameter("POLICY_NO3", list.get(0).get("POLICY_NO3")!=null?list.get(0).get("POLICY_NO3"):"");
		data.addParameter("DATE", list.get(0).get("DATE"));
		data.addParameter("CUST_RISK", ObjectUtils.toString(list.get(0).get("CUST_RISK")));
		data.addParameter("CUST_RISK_DUE", ObjectUtils.toString(list.get(0).get("CUST_RISK_DUE")));
		data.addParameter("INV_SCORE", ObjectUtils.toString(list.get(0).get("INV_SCORE")));
		
		//高齡警語
		String proYN = ObjectUtils.toString(list.get(0).get("PRO_YN"));
		String cSeniorPVAL = ObjectUtils.toString(list.get(0).get("C_SENIOR_PVAL"));
		String seniorRmk = "";
		if(StringUtils.isNotBlank(cSeniorPVAL)) {
			String custRisk = ObjectUtils.toString(list.get(0).get("CUST_RISK"));
			if(StringUtils.equals("Y", proYN) && Integer.parseInt(custRisk.substring(1)) > 3) {
				seniorRmk = "高齡專投客戶只能申購風險值為P3以下(含)之標的";
			} else if(StringUtils.equals("P2", cSeniorPVAL) && Integer.parseInt(custRisk.substring(1)) > 2) {
				seniorRmk = "高齡客戶無相關投資經驗只能申購風險值為P2之標的";
			}
		}		
		data.addParameter("SENIOR_RMK", seniorRmk);
		
		//適配說明
		String regType = ObjectUtils.toString(list.get(0).get("REG_TYPE"));
		String rmk1 = "";
		String rmk2 = "";
		if(StringUtils.equals("1", regType) || StringUtils.equals("2", regType)) {
			rmk1 = "1. 請注意險種代號與要保書是否一致";
		}
//		} else if(StringUtils.equals("2", regType)) {	
//			rmk1 = "1. 請注意投資屬性問卷分數應>=標的投資問卷分數限制";
//			rmk2 = "2. 請注意險種代號與要保書是否一致";
//		}
		data.addParameter("RMK1", rmk1);
		data.addParameter("RMK2", rmk2);
		
		List<Map<String, Object>> kycList = (List) list.get(0).get("KYCList");
		List<Map<String, Object>> prtList = new ArrayList<Map<String,Object>>();
		for(Map<String, Object> map : kycList) {
			try {
				BigDecimal percentage = map.get("ALLOCATION_RATIO") != null ? new BigDecimal(ObjectUtils.toString(map.get("ALLOCATION_RATIO"))) : BigDecimal.ZERO;
				if(percentage.compareTo(BigDecimal.ZERO) == 1) {
					//有輸入比例標的才需要於表單顯示
					map.put("CHECKED", "V");
					prtList.add(map);
				}
			} catch(Exception e) {}
		}
		data.addRecordList("Script Mult Data Set", prtList);
		report = gen.generateReport(txnCode, reportID, data);
		url = report.getLocation();
		String encryptUrl = PdfUtil.process(new PdfConfigVO(this.getDataAccessManager(), url));
		notifyClientToDownloadFile(encryptUrl, "投資型商品基金適配清單.pdf");
	}

	private void printInvOriAdaFunList(List<Map<String, Object>> list) throws JBranchException {
		String url = null;
		String txnCode = "IOT110";
		String reportID = "R8";
		ReportIF report = null;
		DecimalFormat df = new DecimalFormat("#,###");	
		ReportFactory factory = new ReportFactory();
		ReportDataIF data = new ReportData();
		ReportGeneratorIF gen = factory.getGenerator();
		
		data.addParameter("INSTYPE", "新契約");
		data.addParameter("CUST_ID", list.get(0).get("CUST_ID"));
		data.addParameter("CUST_NAME", list.get(0).get("CUST_NAME"));
		data.addParameter("EMP_ID", list.get(0).get("EMP_ID"));
		data.addParameter("INSPRD_ID", list.get(0).get("INSPRD_ID").toString().toUpperCase());
		data.addParameter("INSPRD_NAME", list.get(0).get("INSPRD_NAME"));
		data.addParameter("POLICY_NO1", list.get(0).get("POLICY_NO1")!=null?list.get(0).get("POLICY_NO1"):"");
		data.addParameter("POLICY_NO2", list.get(0).get("POLICY_NO2")!=null?list.get(0).get("POLICY_NO2"):"");
		data.addParameter("POLICY_NO3", list.get(0).get("POLICY_NO3")!=null?list.get(0).get("POLICY_NO3"):"");
		data.addParameter("DATE", list.get(0).get("DATE"));
		data.addParameter("CUST_RISK", ObjectUtils.toString(list.get(0).get("CUST_RISK")));
		data.addParameter("CUST_RISK_DUE", ObjectUtils.toString(list.get(0).get("CUST_RISK_DUE")));
		data.addParameter("INV_SCORE", ObjectUtils.toString(list.get(0).get("INV_SCORE")));
		data.addParameter("SENIOR_OVER_PVAL", ObjectUtils.toString(list.get(0).get("SENIOR_OVER_PVAL")));
		data.addParameter("validHnwcYN", ObjectUtils.toString(list.get(0).get("validHnwcYN")));
		data.addParameter("hnwcService", ObjectUtils.toString(list.get(0).get("hnwcService")));
		data.addParameter("hnwcDueDate", ObjectUtils.toString(list.get(0).get("hnwcDueDate")).substring(0, 10).replaceAll("-", "/"));
		data.addParameter("REAL_PREMIUM", list.get(0).get("REAL_PREMIUM") == null ? "" : df.format(new BigDecimal(list.get(0).get("REAL_PREMIUM").toString())));
		data.addParameter("CURR_CD", ObjectUtils.toString(list.get(0).get("CURR_CD")));
		data.addParameter("PROD_CURR_RATE", ObjectUtils.toString(list.get(0).get("PROD_CURR_RATE")));
		data.addParameter("CUST_REMARKS", ObjectUtils.toString(list.get(0).get("CUST_REMARKS")));
		data.addParameter("OVER_PVAL_AMT", list.get(0).get("OVER_PVAL_AMT") == null ? "" : df.format(new BigDecimal(list.get(0).get("OVER_PVAL_AMT").toString())));
		data.addParameter("OVER_PVAL_MAX_AMT",list.get(0).get("OVER_PVAL_MAX_AMT") == null ? "" :  df.format(new BigDecimal(list.get(0).get("OVER_PVAL_MAX_AMT").toString())));
		
		//高齡警語
		data.addParameter("SENIOR_RMK", "");
		
		//適配說明
		String regType = ObjectUtils.toString(list.get(0).get("REG_TYPE"));
		String rmk1 = "";
		String rmk2 = "";
		if(StringUtils.equals("1", regType) || StringUtils.equals("2", regType)) {
			rmk1 = "1. 請注意險種代號與要保書是否一致";
		}
		data.addParameter("RMK1", rmk1);
		data.addParameter("RMK2", rmk2);
		
		BigDecimal realPremium = list.get(0).get("REAL_PREMIUM") == null ? BigDecimal.ZERO : new BigDecimal(list.get(0).get("REAL_PREMIUM").toString());
		BigDecimal prodCurrRate = list.get(0).get("PROD_CURR_RATE") == null ? new BigDecimal(1) : new BigDecimal(list.get(0).get("PROD_CURR_RATE").toString());
		List<Map<String, Object>> kycList = (List) list.get(0).get("KYCList");
		List<Map<String, Object>> prtList = new ArrayList<Map<String,Object>>();
		for(Map<String, Object> map : kycList) {
			try {
				BigDecimal percentage = map.get("ALLOCATION_RATIO") != null ? new BigDecimal(ObjectUtils.toString(map.get("ALLOCATION_RATIO"))) : BigDecimal.ZERO;
				if(percentage.compareTo(BigDecimal.ZERO) == 1) {
					BigDecimal oriAmt = realPremium.multiply(percentage).divide(new BigDecimal(100)); //原幣金額
					BigDecimal twdAmt = oriAmt.multiply(prodCurrRate); //折台金額
					map.put("ORI_AMT", df.format(oriAmt));
					map.put("TWD_AMT", df.format(twdAmt));
					prtList.add(map);
				}
			} catch(Exception e) {}
		}
		data.addRecordList("Script Mult Data Set", prtList);
		report = gen.generateReport(txnCode, reportID, data);
		url = report.getLocation();
		String encryptUrl = PdfUtil.process(new PdfConfigVO(this.getDataAccessManager(), url));
		notifyClientToDownloadFile(encryptUrl, "投資型商品基金適配清單.pdf");
	}
	
	
	// 非常態交易錄音電訪單
	public void printUnuTraRecTeleIntList(Object body, IPrimitiveMap header)
			throws JBranchException, FileNotFoundException, IOException {
		String url = null;
		String txnCode = "IOT110";
		String reportID = "R3";
		ReportIF report = null;
		IOT110OutputVO return_VO = (IOT110OutputVO) body;

		List<Map<String, Object>> list = return_VO.getList();
		ReportFactory factory = new ReportFactory();
		ReportDataIF data = new ReportData();
		ReportGeneratorIF gen = factory.getGenerator();

		String year = "";
		String mon = "";
		String day = "";
		if(list.get(0).get("DATE") != null){
//			int keyin_date_year = (Integer.parseInt(list.get(0).get("DATE").toString().substring(0, 4)))-1911;//西元年換算民國年
//			year = String.valueOf(keyin_date_year);
//
//			mon = list.get(0).get("DATE").toString().substring(4, 7);
//			day = list.get(0).get("DATE").toString().substring(7);
			String dayStr = list.get(0).get("DATE").toString();//  "2017-10-31 00:00:00.0";
			List<String> list2 = new ArrayList<String>();
			Pattern pattern = Pattern.compile("(\\d+)");

			Matcher mat = pattern.matcher(dayStr);

			while(mat.find()){
				list2.add(mat.group());
			}
			year = String.valueOf((Integer.parseInt(list2.get(0)) - 1911));
			mon = list2.get(1);
			day = list2.get(2);
		}else{
			Calendar cal = Calendar.getInstance();
			year = cal.get(Calendar.YEAR) - 1911 + "";
			mon = cal.get(Calendar.MONTH) + 1 + "";
			if(mon.length()<2){
				mon = "0"+mon;
			}
			day = cal.get(Calendar.DAY_OF_MONTH) + "";
			if(day.length()<2){
				day = "0"+day;
			}
			data.addParameter("DATE", year + mon + day);
		}

		data.addParameter("DATE", year + mon + day);
		data.addParameter("CUST_ID", list.get(0).get("CUST_ID"));
//		data.addParameter("DATE", list.get(0).get("DATE"));
		data.addParameter("PRODUCT_TYPE", list.get(0).get("PRODUCT_TYPE"));

		data.addRecordList("Script Mult Data Set", list);
		report = gen.generateReport(txnCode, reportID, data);
		url = report.getLocation();
		String encryptUrl = PdfUtil.process(new PdfConfigVO(this.getDataAccessManager(), url));
		notifyClientToDownloadFile(encryptUrl, "非常態交易錄音電訪單.pdf");
	}

	// 轉介清單列印
	public void printRefList(Object body, IPrimitiveMap header)
			throws JBranchException, FileNotFoundException, IOException {
		String url = null;
		String txnCode = "IOT110";
		String reportID = "R4";
		ReportIF report = null;
		IOT110OutputVO return_VO = (IOT110OutputVO) body;

		List<Map<String, Object>> list = return_VO.getList();
		ReportFactory factory = new ReportFactory();
		ReportDataIF data = new ReportData();
		ReportGeneratorIF gen = factory.getGenerator();

		data.addRecordList("Script Mult Data Set", list);
		report = gen.generateReport(txnCode, reportID, data);
		url = report.getLocation();
		String encryptUrl = PdfUtil.process(new PdfConfigVO(this.getDataAccessManager(), url));
		notifyClientToDownloadFile(encryptUrl, "轉介清單列印.pdf");
	}

	/***
	 * 產生新契約客戶保險購買檢核表，並回傳URL
	 * @param inputVO
	 * @return
	 * @throws JBranchException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 */
	private String genPrematchPdfURL(IOT110InputVO inputVO) throws JBranchException, FileNotFoundException, IOException, ParseException {
		String url = null;
		String txnCode = "IOT110";
		String reportID = "R5";
		ReportIF report = null;

		dam = getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT A.*,TO_CHAR(MATCH_DATE,'YYYY-MM-DD') MATCH_DATE_STR  ");
		sb.append(" ,TO_CHAR(PROPOSER_BIRTH,'YYYY-MM-DD') PROPOSER_BIRTH_STR ");
		sb.append(" ,TO_CHAR(INSURED_BIRTH,'YYYY-MM-DD') INSURED_BIRTH_STR ");
		sb.append(" ,TO_CHAR(PAYER_BIRTH,'YYYY-MM-DD') PAYER_BIRTH_STR ");
		sb.append(" ,TO_CHAR(APPLY_DATE,'YYYY-MM-DD') APPLY_DATE_STR ");
		sb.append(" ,TO_CHAR(CUST_RISK_DUE,'YYYY-MM-DD') CUST_RISK_DUE_STR ");
		sb.append(" ,SUBSTR(A.PREMATCH_SEQ, 1, 6) AS PREMATCH_SEQ_1, SUBSTR(A.PREMATCH_SEQ, 7, 4) AS PREMATCH_SEQ_2 ");
		sb.append(" ,TO_CHAR(C_LOAN_APPLY_DATE,'YYYY-MM-DD') C_LOAN_APPLY_DATE_STR ");
		sb.append(" ,TO_CHAR(C_PREM_DATE,'YYYY-MM-DD') C_PREM_DATE_STR ");
		sb.append(" ,TO_CHAR(I_LOAN_APPLY_DATE,'YYYY-MM-DD') I_LOAN_APPLY_DATE_STR ");
		sb.append(" ,TO_CHAR(P_LOAN_APPLY_DATE,'YYYY-MM-DD') P_LOAN_APPLY_DATE_STR ");
		sb.append(" ,CASE WHEN B.PARAM_CODE IS NULL THEN 'N' ELSE 'Y' END AS NO_CHK_PRD ");
		sb.append(" FROM TBIOT_PREMATCH A ");
		sb.append(" LEFT JOIN TBSYSPARAMETER B ON B.PARAM_TYPE = 'IOT.NO_CHK_LOAN_INSPRD' AND B.PARAM_CODE = A.INSPRD_ID ");
		sb.append(" WHERE PREMATCH_SEQ = :prematch_seq");
		qc.setObject("prematch_seq", inputVO.getPREMATCH_SEQ());
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(qc);

		XmlInfo xmlinfo = new XmlInfo();

		Map<String,Object> tradeTypeMap = xmlinfo.getVariable("IOT.TRADE_TYPE", FormatHelper.FORMAT_3);//交易類型
		Map<String,Object> relatedMap = xmlinfo.getVariable("IOT.RELATED_WITH_PROPOSER", FormatHelper.FORMAT_3);//與要保人關係
		Map<String,Object> payRelMap = xmlinfo.getVariable("IOT.PAYER_REL_PROPOSER", FormatHelper.FORMAT_3);//與繳款人關係
		Map<String,Object> cmFlagMap = xmlinfo.getVariable("IOT.CM_FLAG", FormatHelper.FORMAT_3);//戶況檢核
		Map<String,Object> othTypeMap = xmlinfo.getVariable("IOT.OTH_TYPE_110", FormatHelper.FORMAT_3);//要保書類型 1.非手寫電子要保書 2.手寫要保書
		Map<String,Object> payTypeMap = xmlinfo.getVariable("IOT.PAY_TYPE", FormatHelper.FORMAT_3);//繳別
//		Map<String,Object> yesNoMap = xmlinfo.getVariable("COMMON.YES_NO", FormatHelper.FORMAT_3);//是/否
//		Map<String,Object> yesNo12Map = xmlinfo.getVariable("COMMON.YES_NO_12", FormatHelper.FORMAT_3);//是/否
		Map<String,String> prdMap = this.getInsPrd(ObjectUtils.toString(list.get(0).get("INSPRD_KEYNO"))); //保險產品主檔

		ReportFactory factory = new ReportFactory();
		ReportDataIF data = new ReportData();
		ReportGeneratorIF gen = factory.getGenerator();
		data.addParameter("REG_TYPE",tradeTypeMap.get(ObjectUtils.toString(list.get(0).get("REG_TYPE"))));         		//保險交易項目 1.壽險新契約 2.契約變更-基金標的異動 3-契約變更-要保人變更
		data.addParameter("OTH_TYPE",othTypeMap.get(ObjectUtils.toString(list.get(0).get("OTH_TYPE"))));				//要保書類型 1.非手寫電子要保書 2.手寫要保書
		data.addParameter("CASE_ID",ObjectUtils.toString(list.get(0).get("CASE_ID")));	        		//案件編號
		data.addParameter("PREMATCH_SEQ",ObjectUtils.toString(list.get(0).get("PREMATCH_SEQ")));     	//適合度檢核編號
		data.addParameter("PREMATCH_SEQ_1",ObjectUtils.toString(list.get(0).get("PREMATCH_SEQ_1")));     	//適合度檢核編號
		data.addParameter("PREMATCH_SEQ_2",ObjectUtils.toString(list.get(0).get("PREMATCH_SEQ_2")));     	//適合度檢核編號
		data.addParameter("CUST_ID",ObjectUtils.toString(list.get(0).get("CUST_ID")));					//客戶ID
		data.addParameter("PROPOSER_NAME",ObjectUtils.toString(list.get(0).get("PROPOSER_NAME")));    	//要保人NAME
		data.addParameter("PROPOSER_BIRTH",ObjectUtils.toString(list.get(0).get("PROPOSER_BIRTH_STR")));   	//要保人生日
		data.addParameter("INSURED_BIRTH",ObjectUtils.toString(list.get(0).get("INSURED_BIRTH_STR")));   	//被保人生日
		data.addParameter("PAYER_BIRTH",ObjectUtils.toString(list.get(0).get("PAYER_BIRTH_STR")));   	//繳款人生日
		data.addParameter("REPRESET_ID",ObjectUtils.toString(list.get(0).get("REPRESET_ID")));     		//法定代理人ID
		data.addParameter("REPRESET_NAME",ObjectUtils.toString(list.get(0).get("REPRESET_NAME")));    	//法定代理人NAME
		data.addParameter("RLT_BT_PROREP",relatedMap.get(ObjectUtils.toString(list.get(0).get("RLT_BT_PROREP"))));    	//法定代理人與要保人關係
		data.addParameter("INSURED_ID",ObjectUtils.toString(list.get(0).get("INSURED_ID")));       		//被保人ID
		data.addParameter("INSURED_NAME",ObjectUtils.toString(list.get(0).get("INSURED_NAME")));		//被保人NAME
		data.addParameter("PAYER_ID",ObjectUtils.toString(list.get(0).get("PAYER_ID")));				//繳款人ID
		data.addParameter("PAYER_NAME",ObjectUtils.toString(list.get(0).get("PAYER_NAME")));			//繳款人NAME
		data.addParameter("RLT_BT_PROPAY",payRelMap.get(ObjectUtils.toString(list.get(0).get("RLT_BT_PROPAY"))));		//繳款人與要保人關係
		data.addParameter("INSPRD_ID",ObjectUtils.toString(list.get(0).get("INSPRD_ID")));				//主約險種代碼
		data.addParameter("INSPRD_NAME",prdMap.get("INSPRD_NAME"));										//險種名稱
		data.addParameter("PAY_TYPE",payTypeMap.get(prdMap.get("PAY_TYPE")));							//繳別
		data.addParameter("INSPRD_ANNUAL",prdMap.get("INSPRD_ANNUAL"));									//繳費年期
		data.addParameter("REAL_PREMIUM",ObjectUtils.toString(list.get(0).get("REAL_PREMIUM")));		//實收保費(原幣)
		data.addParameter("BASE_PREMIUM",ObjectUtils.toString(list.get(0).get("BASE_PREMIUM")));		//基本保費(原幣)
		data.addParameter("APPLY_DATE",ObjectUtils.toString(list.get(0).get("APPLY_DATE_STR")));			//要保書填寫申請日
		data.addParameter("LOAN_SOURCE_YN",ObjectUtils.toString(list.get(0).get("LOAN_SOURCE_YN")));	//保費來源是否為貸款(Y/N)
		data.addParameter("LOAN_SOURCE2_YN",ObjectUtils.toString(list.get(0).get("LOAN_SOURCE2_YN")));//保費來源是否為解約(Y/N)
		data.addParameter("MATCH_DATE",ObjectUtils.toString(list.get(0).get("MATCH_DATE_STR")));			//檢核試算日
		data.addParameter("RECRUIT_ID",ObjectUtils.toString(list.get(0).get("RECRUIT_ID")));			//招攬人員編
		data.addParameter("PROPOSER_CM_FLAG",cmFlagMap.get(ObjectUtils.toString(list.get(0).get("PROPOSER_CM_FLAG")))); //要保人戶況
		data.addParameter("CUST_RISK",ObjectUtils.toString(list.get(0).get("CUST_RISK")));				//要保人風險屬性
		data.addParameter("KYC_DUE_DATE",ObjectUtils.toString(list.get(0).get("CUST_RISK_DUE_STR")));	//KYC有效日
		data.addParameter("AML",ObjectUtils.toString(list.get(0).get("AML")));							//要保人AML風險等級
		data.addParameter("PRECHECK",ObjectUtils.toString(list.get(0).get("PRECHECK")));				//要保人Pre-check結果(洗錢防制法)
		data.addParameter("PROPOSER_INCOME1",ObjectUtils.toString(list.get(0).get("PROPOSER_INCOME1"))); //要保人工作年收入(業報)
		data.addParameter("PROPOSER_INCOME2",ObjectUtils.toString(list.get(0).get("PROPOSER_INCOME2"))); //要保人工作年收入(財告)
		data.addParameter("PROPOSER_INCOME3",ObjectUtils.toString(list.get(0).get("PROPOSER_INCOME3"))); //要保人工作年收入(授信)
		data.addParameter("INSURED_CM_FLAG",cmFlagMap.get(ObjectUtils.toString(list.get(0).get("INSURED_CM_FLAG"))));	//被保人戶況
		data.addParameter("INSURED_INCOME1",ObjectUtils.toString(list.get(0).get("INSURED_INCOME1")));	//被保人工作年收入(業報)
		data.addParameter("INSURED_INCOME2",ObjectUtils.toString(list.get(0).get("INSURED_INCOME2")));	//被保人工作年收入(財告)
		data.addParameter("INSURED_INCOME3",ObjectUtils.toString(list.get(0).get("INSURED_INCOME3")));	//被保人工作年收入(授信)
		data.addParameter("PAYER_CM_FLAG",cmFlagMap.get(ObjectUtils.toString(list.get(0).get("PAYER_CM_FLAG"))));		//繳款人戶況
		data.addParameter("LOAN_CHK1_YN",ObjectUtils.toString(list.get(0).get("LOAN_CHK1_YN")));		//繳款人保單貸款檢核Y/N(透過本行送件)
		data.addParameter("LOAN_CHK2_YN",ObjectUtils.toString(list.get(0).get("LOAN_CHK2_YN")));		//繳款人行內貸款檢核(Y/N)
		data.addParameter("LOAN_CHK3_YN",ObjectUtils.toString(list.get(0).get("LOAN_CHK3_YN")));		//繳款人行保單解約檢核(Y/N)
//		data.addParameter("LOAN_CHK4_YN",ObjectUtils.toString(list.get(0).get("LOAN_CHK4_YN")));		//繳款人是否有額度式貸款檢核(Y/N)
		data.addParameter("CD_CHK_YN",ObjectUtils.toString(list.get(0).get("CD_CHK_YN")));				//繳款人定存中解不打折檢核檢核(Y/N)
		data.addParameter("C_LOAN_CHK1_YN",ObjectUtils.toString(list.get(0).get("C_LOAN_CHK1_YN")));		//要保人保單貸款檢核Y/N(透過本行送件)
		data.addParameter("C_LOAN_CHK2_YN",ObjectUtils.toString(list.get(0).get("C_LOAN_CHK2_YN")));		//要保人行內貸款檢核(Y/N)
		data.addParameter("C_LOAN_CHK3_YN",ObjectUtils.toString(list.get(0).get("C_LOAN_CHK3_YN")));		//要保人行保單解約檢核(Y/N)
//		data.addParameter("C_LOAN_CHK4_YN",ObjectUtils.toString(list.get(0).get("C_LOAN_CHK4_YN")));		//要保人是否有額度式貸款檢核(Y/N)
		data.addParameter("C_CD_CHK_YN",ObjectUtils.toString(list.get(0).get("C_CD_CHK_YN")));			//要保人定存中解不打折檢核檢核(Y/N)
		data.addParameter("I_LOAN_CHK1_YN",ObjectUtils.toString(list.get(0).get("I_LOAN_CHK1_YN")));		//被保人保單貸款檢核Y/N(透過本行送件)
		data.addParameter("I_LOAN_CHK2_YN",ObjectUtils.toString(list.get(0).get("I_LOAN_CHK2_YN")));		//被保人行內貸款檢核(Y/N)
		data.addParameter("I_LOAN_CHK3_YN",ObjectUtils.toString(list.get(0).get("I_LOAN_CHK3_YN")));		//被保人行保單解約檢核(Y/N)
//		data.addParameter("I_LOAN_CHK4_YN",ObjectUtils.toString(list.get(0).get("I_LOAN_CHK4_YN")));		//被保人是否有額度式貸款檢核(Y/N)
		data.addParameter("I_CD_CHK_YN",ObjectUtils.toString(list.get(0).get("I_CD_CHK_YN")));				//被保人定存中解不打折檢核檢核(Y/N)
		data.addParameter("INCOME_REMARK",ObjectUtils.toString(list.get(0).get("INCOME_REMARK")));		//工作年收入與授信審表不一致說明
		data.addParameter("LOAN_SOURCE_REMARK",ObjectUtils.toString(list.get(0).get("LOAN_SOURCE_REMARK")));//客戶保費來源說明
		data.addParameter("CONTRACT_END_YN",StringUtils.equals("1", ObjectUtils.toString(list.get(0).get("CONTRACT_END_YN"))) ? "Y" : "N");//業報書投保前三個月內有辦理解約
		data.addParameter("S_INFITEM_LOAN_YN",StringUtils.equals("1", ObjectUtils.toString(list.get(0).get("S_INFITEM_LOAN_YN"))) ? "Y" : "N");//業報書投保前三個月內有辦理貸款或保險單借款
		data.addParameter("PROPOSER_WORK",ObjectUtils.toString(list.get(0).get("PROPOSER_WORK")));//要保人工作內容
		data.addParameter("INSURED_WORK",ObjectUtils.toString(list.get(0).get("INSURED_WORK")));//被保人工作內容
		data.addParameter("C_LOAN_APPLY_DATE",ObjectUtils.toString(list.get(0).get("C_LOAN_APPLY_DATE_STR")));//要保人行內貸款申請日
		data.addParameter("C_PREM_DATE",ObjectUtils.toString(list.get(0).get("C_PREM_DATE_STR")));//要保人舊保單提領保額/保價日
		data.addParameter("I_LOAN_APPLY_DATE",ObjectUtils.toString(list.get(0).get("I_LOAN_APPLY_DATE_STR")));//被保人行內貸款申請日
		data.addParameter("P_LOAN_APPLY_DATE",ObjectUtils.toString(list.get(0).get("P_LOAN_APPLY_DATE_STR")));//繳款人行內貸款申請日
		data.addParameter("INV_SCORE",ObjectUtils.toString(list.get(0).get("INV_SCORE")));//投資屬性問卷分數
		data.addParameter("C_KYC_INCOME",ObjectUtils.toString(list.get(0).get("C_KYC_INCOME")));//要保人行內KYC收入
		data.addParameter("I_KYC_INCOME",ObjectUtils.toString(list.get(0).get("I_KYC_INCOME")));//被保人行內KYC收入
		data.addParameter("C_SALE_SENIOR_TRANSSEQ",ObjectUtils.toString(list.get(0).get("C_SALE_SENIOR_TRANSSEQ")));//要保人高齡銷售過程錄音序號
		data.addParameter("CUST_DEBIT",ObjectUtils.toString(list.get(0).get("CUST_DEBIT")));//要保人行內負債(不含信用卡)
		data.addParameter("INSURED_DEBIT",ObjectUtils.toString(list.get(0).get("INSURED_DEBIT")));//被保人行內負債(不含信用卡)
		data.addParameter("C_CD_DUE_DATE",ObjectUtils.toString(list.get(0).get("C_CD_DUE_DATE")));//要保人定存單解約/到期日
		data.addParameter("I_CD_DUE_DATE",ObjectUtils.toString(list.get(0).get("I_CD_DUE_DATE")));//被保人定存單解約/到期日
		data.addParameter("P_CD_DUE_DATE",ObjectUtils.toString(list.get(0).get("P_CD_DUE_DATE")));//繳款人定存單解約/到期日
		data.addParameter("CANCEL_CONTRACT_YN",ObjectUtils.toString(list.get(0).get("CANCEL_CONTRACT_YN")));//契撤案件
		
		//警語區
		StringBuilder remarks = new StringBuilder();
		
		//非常態錄音
		if(StringUtils.equals("Y", ObjectUtils.toString(list.get(0).get("AB_YN")))) {
			remarks.append("**該案件需進行非常態錄音。\n");
		}
		
		//高齡銷售過程錄音		
		if(StringUtils.equals("Y", ObjectUtils.toString(list.get(0).get("C_SALE_SENIOR_YN"))) ||
				StringUtils.equals("Y", ObjectUtils.toString(list.get(0).get("I_SALE_SENIOR_YN"))) ||
				StringUtils.equals("Y", ObjectUtils.toString(list.get(0).get("P_SALE_SENIOR_YN")))) {
			//要保人或被保人或繳款人只要其中一人為高齡，且商品代號不在不須檢核貸款險種參數檔中，須執行高齡銷售過程錄音
			if(StringUtils.equals("N", ObjectUtils.toString(list.get(0).get("NO_CHK_PRD")))) {				
				remarks.append("**要保人及達65歲之被保人或繳款人需進行高齡銷售過程錄音。\n");
			}
			
			//若要保人或被保人或繳款人保險年齡>=65歲
			remarks.append("**高齡客戶應檢附行內『高齡客戶資訊觀察表』，並請留意業務人員報告書附表_高齡客戶投保評估量表是否具有合理性。\n");
		}
		
		//若有產生"保費來源確認及高齡客戶關懷錄音電訪單"
		if(StringUtils.equals("Y", ObjectUtils.toString(inputVO.getPrintSourceRecListYN()))) {			
			remarks.append("**需要進行高齡/保險資金來源錄音。\n");
		}
		
		//若被保人、繳款人為高齡未開戶警語
		String seniorNonOpen = "";
		if(StringUtils.equals("Y", ObjectUtils.toString(list.get(0).get("I_SALE_SENIOR_YN"))) && 
				StringUtils.equals("10", ObjectUtils.toString(list.get(0).get("INSURED_CM_FLAG")))) seniorNonOpen = "被保人"; //被保人高齡未開戶
		if(StringUtils.equals("Y", ObjectUtils.toString(list.get(0).get("P_SALE_SENIOR_YN"))) && 
				StringUtils.equals("10", ObjectUtils.toString(list.get(0).get("PAYER_CM_FLAG")))) seniorNonOpen += (StringUtils.isBlank(seniorNonOpen) ? "" : "、") + "繳款人";
		remarks.append(StringUtils.isBlank(seniorNonOpen) ? "" : "**" + seniorNonOpen + "高齡且非本行客戶，請填寫本行紙本高齡評估量表，並分行歸檔留存。\n");
		
		//是否有額度是貸款
//		String loan4Name = "";
//		if(StringUtils.equals("Y", ObjectUtils.toString(list.get(0).get("C_LOAN_CHK4_YN")))) loan4Name = "要保人";
//		if(StringUtils.equals("Y", ObjectUtils.toString(list.get(0).get("I_LOAN_CHK4_YN")))) loan4Name += (StringUtils.isBlank(loan4Name) ? "" : "、") + "被保人";
//		if(StringUtils.equals("Y", ObjectUtils.toString(list.get(0).get("LOAN_CHK4_YN")))) loan4Name += (StringUtils.isBlank(loan4Name) ? "" : "、") + "繳款人";
//		remarks.append(StringUtils.isBlank(loan4Name) ? "" : "**" + loan4Name + "有申請額度式房貸，請檢視客戶保費動用是否與該貸款相關。\n");
		
		if(list.get(0).get("C_LOAN_CHK2_DATE") != null || list.get(0).get("I_LOAN_CHK2_DATE") != null) {
			remarks.append("**客戶近一年有貸款撥貸，請檢視授信收入與業報書收入(工作收入+其他收入)是否一致，不一致必須於業報書補充說明欄詳細說明原因。\n");
		}

		if(StringUtils.equals("Y", ObjectUtils.toString(list.get(0).get("AGE_UNDER20_YN")))) {
			remarks.append("**要保人未成年請注意投資型屬性問卷須以要保人角度填寫，投保目的勾選必須具合理性。\n");
		}
		
		if(StringUtils.equals("2", ObjectUtils.toString(list.get(0).get("INV_SCORE_CHK")))) {
			remarks.append("**近期送件，投資屬性問卷分數不同，請說明。\n");
		}
		
		//有定存單到期或解約
		String dueDateName = "";
		if(list.get(0).get("C_CD_DUE_DATE") != null) dueDateName = "要保人";
		if(list.get(0).get("I_CD_DUE_DATE") != null) dueDateName += (StringUtils.isBlank(dueDateName) ? "" : "、") + "被保人";
		if(list.get(0).get("P_CD_DUE_DATE") != null) dueDateName += (StringUtils.isBlank(dueDateName) ? "" : "、") + "繳款人";
		remarks.append(StringUtils.isBlank(dueDateName) ? "" : "**" + dueDateName + "於購買保險前有定存單到期或解約，請留意保費來源是否與定存單到期或解約有關。\n");
		
		//契撤案件
		if(StringUtils.equals("Y", ObjectUtils.toString(list.get(0).get("CANCEL_CONTRACT_YN")))) {
			remarks.append("**契撤案件，請檢附契撤申請書。\n");
		}
		
		//循環型貸款
		if(StringUtils.equals("Y", ObjectUtils.toString(list.get(0).get("C_REVOLVING_LOAN_YN"))) ||
				StringUtils.equals("Y", ObjectUtils.toString(list.get(0).get("I_REVOLVING_LOAN_YN"))) ||
				StringUtils.equals("Y", ObjectUtils.toString(list.get(0).get("P_REVOLVING_LOAN_YN")))) {
			String loan4Name = "";
			if(StringUtils.equals("Y", ObjectUtils.toString(list.get(0).get("C_REVOLVING_LOAN_YN")))) loan4Name = "要保人";
			if(StringUtils.equals("Y", ObjectUtils.toString(list.get(0).get("I_REVOLVING_LOAN_YN")))) loan4Name += (StringUtils.isBlank(loan4Name) ? "" : "、") + "被保人";
			if(StringUtils.equals("Y", ObjectUtils.toString(list.get(0).get("P_REVOLVING_LOAN_YN")))) loan4Name += (StringUtils.isBlank(loan4Name) ? "" : "、") + "繳款人";
			//要保人或被保人或繳款人任一人有申辦循環型貸款
			remarks.append("**" + loan4Name + "有申請額度式房貸，請檢視保費動用是否與該貸款相關，已建議客戶匯款繳交保費。 □是  □否  (□已告知客戶以貸款申購保險之相關風險。將持續追蹤保費扣款金流，確認不會動用透支額度。)\n");
		}
		
		data.addParameter("QUOTA_LOAN_CHK", remarks.toString());
				
		report = gen.generateReport(txnCode, reportID, data);
		return report.getLocation();
	}

	// 客戶保險購買檢核表列印
	public void printCheckList(Object body, IPrimitiveMap header) throws JBranchException, FileNotFoundException, IOException, ParseException {
		IOT110InputVO inputVO = (IOT110InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		sb.append("SELECT * FROM TBIOT_PREMATCH_PDF WHERE PREMATCH_SEQ = :prematchSeq");
		queryCondition.setObject("prematchSeq", inputVO.getPREMATCH_SEQ());
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		String errorMag = null;

		try {
			if (list.size() > 0 && list.get(0).get("PDF_FILE") != null) {
				String filePath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
				String uuid = UUID.randomUUID().toString();
				String fileName = String.format("%s.pdf", uuid);
				Blob blob = (Blob) list.get(0).get("PDF_FILE");
				int blobLength = (int) blob.length();
				byte[] blobAsBytes = blob.getBytes(1, blobLength);

				File targetFile = new File(filePath, fileName);
				FileOutputStream fos = new FileOutputStream(targetFile);
				fos.write(blobAsBytes);
				fos.close();
				// 下載時 PDF 時，進行加密
				notifyClientToDownloadFile(PdfUtil.process(new PdfConfigVO(this.getDataAccessManager(), "temp//" + fileName))
						, "客戶保險購買檢核表.pdf");
//				notifyClientViewDoc("temp//" + fileName, "pdf");
			} else {
				errorMag = "查無此資料請洽系統管理員";
			}
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}

		if (StringUtils.isNotBlank(errorMag)) {
			throw new APException(errorMag);
		}

		this.sendRtnObject(null);
	}

	/***
	 * 產生要保人變更保險購買檢核表，並回傳URL
	 * @param inputVO
	 * @return
	 * @throws JBranchException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 */
	private String genChgCustPdfURL(IOT110InputVO inputVO) throws JBranchException, FileNotFoundException, IOException, ParseException {
		String url = null;
		String txnCode = "IOT110";
		String reportID = "R7";
		ReportIF report = null;

		dam = getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT A.* ");
		sb.append(" , TO_CHAR(CHG_PROPOSER_BIRTH,'YYYY-MM-DD') CHG_PROPOSER_BIRTH_STR  ");
		sb.append(" , SUBSTR(A.PREMATCH_SEQ, 1, 6) AS PREMATCH_SEQ_1 ");
		sb.append(" , SUBSTR(A.PREMATCH_SEQ, 7, 4) AS PREMATCH_SEQ_2 ");
		sb.append(" , (POLICY_NO1 || ' - ' || NVL(POLICY_NO2, '  ') || ' - ' || NVL(POLICY_NO3, '  ')) AS POLICY_NBR ");
		sb.append("FROM TBIOT_PREMATCH A ");
		sb.append("WHERE A.PREMATCH_SEQ = :prematch_seq");
		qc.setObject("prematch_seq", inputVO.getPREMATCH_SEQ());
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(qc);

		XmlInfo xmlinfo = new XmlInfo();

		Map<String,Object> tradeTypeMap = xmlinfo.getVariable("IOT.TRADE_TYPE", FormatHelper.FORMAT_3);//交易類型

		ReportFactory factory = new ReportFactory();
		ReportDataIF data = new ReportData();
		ReportGeneratorIF gen = factory.getGenerator();
		data.addParameter("REG_TYPE",tradeTypeMap.get(ObjectUtils.toString(list.get(0).get("REG_TYPE"))));         		//保險交易項目 1.壽險新契約 2.契約變更-基金標的異動 3-契約變更-要保人變更
		data.addParameter("CASE_ID",ObjectUtils.toString(list.get(0).get("CASE_ID")));	        		//案件編號
		data.addParameter("PREMATCH_SEQ",ObjectUtils.toString(list.get(0).get("PREMATCH_SEQ")));     	//適合度檢核編號
		data.addParameter("PREMATCH_SEQ_1",ObjectUtils.toString(list.get(0).get("PREMATCH_SEQ_1")));     	//適合度檢核編號
		data.addParameter("PREMATCH_SEQ_2",ObjectUtils.toString(list.get(0).get("PREMATCH_SEQ_2")));     	//適合度檢核編號
		data.addParameter("CHG_CUST_ID",ObjectUtils.toString(list.get(0).get("CHG_CUST_ID")));					//變更後客戶ID
		data.addParameter("CHG_PROPOSER_NAME",ObjectUtils.toString(list.get(0).get("CHG_PROPOSER_NAME")));    	//變更後要保人NAME
		data.addParameter("CHG_PROPOSER_BIRTH",ObjectUtils.toString(list.get(0).get("CHG_PROPOSER_BIRTH_STR")));   	//變更後要保人生日
		data.addParameter("INSURED_ID",ObjectUtils.toString(list.get(0).get("INSURED_ID")));       		//被保人ID
		data.addParameter("INSURED_NAME",ObjectUtils.toString(list.get(0).get("INSURED_NAME")));		//被保人NAME
		data.addParameter("AML",ObjectUtils.toString(list.get(0).get("AML")));							//要保人AML風險等級
		data.addParameter("PRECHECK",ObjectUtils.toString(list.get(0).get("PRECHECK")));				//要保人Pre-check結果(洗錢防制法)
		data.addParameter("POLICY_NBR",ObjectUtils.toString(list.get(0).get("POLICY_NBR"))); //要保人工作年收入(業報)

		report = gen.generateReport(txnCode, reportID, data);
		return report.getLocation();
	}

	// 客戶保險購買檢核表列印
	public void printChgCustCheckList(Object body, IPrimitiveMap header) throws JBranchException, FileNotFoundException, IOException, ParseException {
		IOT110InputVO inputVO = (IOT110InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		sb.append("SELECT * FROM TBIOT_PREMATCH_PDF WHERE PREMATCH_SEQ = :prematchSeq");
		queryCondition.setObject("prematchSeq", inputVO.getPREMATCH_SEQ());
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		String errorMag = null;

		try {
			if (list.size() > 0 && list.get(0).get("PDF_FILE") != null) {
				String filePath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
				String uuid = UUID.randomUUID().toString();
				String fileName = String.format("%s.pdf", uuid);
				Blob blob = (Blob) list.get(0).get("PDF_FILE");
				int blobLength = (int) blob.length();
				byte[] blobAsBytes = blob.getBytes(1, blobLength);

				File targetFile = new File(filePath, fileName);
				FileOutputStream fos = new FileOutputStream(targetFile);
				fos.write(blobAsBytes);
				fos.close();

				// 下載時 PDF 時，進行加密
				notifyClientToDownloadFile(PdfUtil.process(new PdfConfigVO(this.getDataAccessManager(), "temp//" + fileName))
						, "客戶保險購買檢核表.pdf");
//				notifyClientViewDoc("temp//" + fileName, "pdf");
			} else {
				errorMag = "查無此資料請洽系統管理員";
			}
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}

		if (StringUtils.isNotBlank(errorMag)) {
			throw new APException(errorMag);
		}

		this.sendRtnObject(null);
	}

	/**
	 *
	 * @param prdKeyNo
	 * @return 險種名稱
	 * @throws JBranchExcpetion
	 */
	private Map<String,String> getInsPrd(String prdKeyNo) throws JBranchException{
		Map<String, String> prdMap = new HashMap<String, String>();

		dam = getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT * FROM TBPRD_INS_MAIN ");
		sb.append("WHERE INSPRD_KEYNO = :prd_keyno");
		qc.setObject("prd_keyno", prdKeyNo);
		qc.setQueryString(sb.toString());

		List<Map<String, Object>> list = dam.exeQuery(qc);

		if(!list.isEmpty()){
			prdMap.put("INSPRD_NAME", ObjectUtils.toString(list.get(0).get("INSPRD_NAME")));
			prdMap.put("PAY_TYPE", ObjectUtils.toString(list.get(0).get("PAY_TYPE")));
			prdMap.put("INSPRD_ANNUAL", ObjectUtils.toString(list.get(0).get("INSPRD_ANNUAL")));
		}
		return prdMap;
	}
	
	// 保費來源確認及高齡客戶關懷錄音電訪單
	public void printSourceRecList(Object body, IPrimitiveMap header) throws JBranchException, FileNotFoundException, IOException {
		String url = null;
		String txnCode = "IOT110";
		String reportID = "R6";
		ReportIF report = null;
		IOT110OutputVO return_VO = (IOT110OutputVO) body;

		List<Map<String, Object>> list = return_VO.getList();
		List<Map<String, Object>> printList = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		ReportFactory factory = new ReportFactory();
		ReportDataIF data = new ReportData();
		ReportGeneratorIF gen = factory.getGenerator();

		map.put("CUST_ID", ObjectUtils.toString(list.get(0).get("CUST_ID")));
		map.put("PROPOSER_NAME", ObjectUtils.toString(list.get(0).get("PROPOSER_NAME")));
		
		map.put("INSURED_ID", ObjectUtils.toString(list.get(0).get("INSURED_ID")));
		map.put("INSURED_NAME", ObjectUtils.toString(list.get(0).get("INSURED_NAME")));
		
		map.put("PAYER_ID", ObjectUtils.toString(list.get(0).get("PAYER_ID")));
		map.put("PAYER_NAME", ObjectUtils.toString(list.get(0).get("PAYER_NAME")));
		
		map.put("PROD_ID", ObjectUtils.toString(list.get(0).get("PROD_ID")));
		map.put("PROD_NAME", ObjectUtils.toString(list.get(0).get("PROD_NAME")));

		map.put("REC_TYPE", ObjectUtils.toString(list.get(0).get("REC_TYPE")));
		map.put("PROD_CURR", ObjectUtils.toString(list.get(0).get("PROD_CURR")));
		
		printList.add(map);
		
		data.addRecordList("Script Mult Data Set", printList);
		report = gen.generateReport(txnCode, reportID, data);
		url = report.getLocation();
		String encryptUrl = PdfUtil.process(new PdfConfigVO(this.getDataAccessManager(), url));
		notifyClientToDownloadFile(encryptUrl, "保費來源確認及高齡客戶關懷錄音電訪單.pdf");
	}


	/***
	 * 取得欲複製說明的保險文件編號資料(改為輸入購買檢核編號，複製說明)
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @throws DAOException
	 */
	public void getCopyInsData(Object body, IPrimitiveMap header) throws DAOException, JBranchException {
		IOT110InputVO inputVO = (IOT110InputVO) body;
		IOT110OutputVO outputVO = new IOT110OutputVO();

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		if (StringUtils.isNotBlank(inputVO.getCopyInsId())) {
			sql.append(" SELECT * FROM TBIOT_PREMATCH ");
			sql.append(" WHERE PREMATCH_SEQ = :insId ");
			queryCondition.setObject("insId", inputVO.getCopyInsId());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);

			if(CollectionUtils.isNotEmpty(list)) {
				if(StringUtils.isBlank(ObjectUtils.toString(list.get(0).get("INCOME_REMARK")))
						&& StringUtils.isBlank(ObjectUtils.toString(list.get(0).get("INCOME_REMARK")))) {
					throw new APException("ehl_01_iot110_007");		//欲複製說明為空白無法複製
				}

				if(!StringUtils.equals(inputVO.getCUST_ID(), ObjectUtils.toString(list.get(0).get("CUST_ID")))
						|| !StringUtils.equals(inputVO.getINSURED_ID(), ObjectUtils.toString(list.get(0).get("INSURED_ID")))
						|| !StringUtils.equals(inputVO.getPAYER_ID(), ObjectUtils.toString(list.get(0).get("PAYER_ID")))) {
					throw new APException("ehl_01_iot110_008");		//欲複製說明之要保人、被保人、繳款人不一致，無法進行複製
				}
			} else {
				throw new APException("ehl_01_iot110_009");		//查無欲複製說明之保險文件編號資料
			}

			outputVO.setResultList(list);
		}

		this.sendRtnObject(outputVO);
	}

	//送出覆核時，檢核投資屬性問卷分數
	//1. 不可以點選：投資屬性問卷同一要保書申請日分數不同，請修改
	//2. 可以點選，顯示訊息：近期送件，投資屬性問卷分數不同，請說明
	//其他，可以點選通過
	private String checkInvScore(IOT110InputVO inputVO) throws DAOException, JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT 1 FROM TBIOT_PREMATCH P ");
		sql.append(" LEFT OUTER JOIN TBPRD_INS_MAIN M ON M.INSPRD_KEYNO = P.INSPRD_KEYNO ");
		sql.append(" WHERE P.CUST_ID = :custId AND P.INV_SCORE IS NOT NULL AND M.INSPRD_TYPE <> '1' ");
		sql.append(" AND P.INV_SCORE <> :invScore AND TRUNC(P.APPLY_DATE) = TRUNC(:applyDate) ");
		if(StringUtils.isNotBlank(inputVO.getPREMATCH_SEQ())) {
			sql.append(" AND P.PREMATCH_SEQ <> :prematchSeq ");
			queryCondition.setObject("prematchSeq", inputVO.getPREMATCH_SEQ());
		}
		queryCondition.setObject("invScore",inputVO.getINV_SCORE());
		queryCondition.setObject("custId", inputVO.getCUST_ID());
		queryCondition.setObject("applyDate", inputVO.getAPPLY_DATE());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if(CollectionUtils.isNotEmpty(list)) {
			return "1"; //投資屬性問卷同一要保書申請日分數不同，請修改
		}
		
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT 1 FROM TBIOT_PREMATCH P ");
		sql.append(" LEFT OUTER JOIN TBPRD_INS_MAIN M ON M.INSPRD_KEYNO = P.INSPRD_KEYNO ");
		sql.append(" WHERE P.CUST_ID = :custId AND P.INV_SCORE IS NOT NULL AND M.INSPRD_TYPE <> '1' "); //投資型商品才比對
		sql.append(" AND P.INV_SCORE <> :invScore AND TRUNC(P.APPLY_DATE) <> TRUNC(:applyDate) AND (TRUNC(P.APPLY_DATE) - TRUNC(:applyDate)) > -93 AND (TRUNC(P.APPLY_DATE) - TRUNC(:applyDate)) < 93 ");
		if(StringUtils.isNotBlank(inputVO.getPREMATCH_SEQ())) {
			sql.append(" AND P.PREMATCH_SEQ <> :prematchSeq ");
			queryCondition.setObject("prematchSeq", inputVO.getPREMATCH_SEQ());
		}
		queryCondition.setObject("invScore",inputVO.getINV_SCORE());
		queryCondition.setObject("custId", inputVO.getCUST_ID());
		queryCondition.setObject("applyDate", inputVO.getAPPLY_DATE());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
		if(CollectionUtils.isNotEmpty(list2)) {
			return "2"; //近期送件，投資屬性問卷分數不同，請說明
		}
		
		return "";
	}
	
	/***
	 * 要保人或被保人或繳款人為高齡，本行的高齡評估作業異動日期是否介於要保書申請日-3個工作天～要保書申請日區間
	 * 理專鍵機檢核
	 * @param body
	 * @param header
	 * @throws Exception 
	 */
	public void validSeniorCustEvlRM(Object body, IPrimitiveMap header) throws Exception {
		IOT110InputVO inputVO = (IOT110InputVO) body;
		IOT110OutputVO outputVO = new IOT110OutputVO();
		
		//要保人或被保人或繳款人為高齡，本行的高齡評估作業異動日期是否介於要保書申請日-3個工作天～要保書申請日區間
		String invalidCustEvlRMYN = invalidSeniorCustEvlRM(inputVO);
		outputVO.setInvalidSeniorCustEvlRM(invalidCustEvlRMYN);
		//高齡觀察表：金融認知結果&能力表現填答
		if(!StringUtils.equals("Y", invalidCustEvlRMYN)) { //通過評估日檢核才做題目檢核
			IOT110OutputVO outputVOBoss = validSeniorCustEvlBoss(inputVO);
			outputVO.setInvalidSeniorCustEvlBossB(outputVOBoss.getInvalidSeniorCustEvlBossB());
			outputVO.setInvalidSeniorCustEvlBossC(outputVOBoss.getInvalidSeniorCustEvlBossC());
			outputVO.setInvalidSeniorCustEvlBossD(outputVOBoss.getInvalidSeniorCustEvlBossD());
			outputVO.setInvalidSeniorCustEvlBossE(outputVOBoss.getInvalidSeniorCustEvlBossE());
		}
		
		this.sendRtnObject(outputVO);
	}
	
	/***
	 * 要保人或被保人或繳款人為高齡，本行的高齡評估作業異動日期是否介於要保書申請日-3個工作天～要保書申請日區間
	 * 被保人/繳款人，非本行客戶不檢核
	 * @param inputVO
	 * @return "Y":invalid "N":valid
	 * @throws Exception
	 */
	public String invalidSeniorCustEvlRM(IOT110InputVO inputVO) throws Exception {
		String invalidYN = "N";
		
		if((StringUtils.equals("Y", inputVO.getC_SALE_SENIOR_YN()) || //要保人高齡
				(!StringUtils.equals("10", inputVO.getINSURED_CM_FLAG()) && StringUtils.equals("Y", inputVO.getI_SALE_SENIOR_YN())) ||	//被保人為本行客戶且高齡
				(!StringUtils.equals("10", inputVO.getPAYER_CM_FLAG()) && StringUtils.equals("Y", inputVO.getP_SALE_SENIOR_YN())))) {	//繳款人為本行客戶且高齡
			SeniorValidation svalid = (SeniorValidation) PlatformContext.getBean("SeniorValidation");
			SeniorValidationInputVO sInputVO = new SeniorValidationInputVO();
			sInputVO.setCustID(inputVO.getCUST_ID());
			sInputVO.setInsuredID(inputVO.getINSURED_ID());
			sInputVO.setPayerID(inputVO.getPAYER_ID());
			sInputVO.setIOTApplyDate(inputVO.getAPPLY_DATE());
			sInputVO.setIOTApplyDate3(getBeforeApplyDate3(inputVO.getAPPLY_DATE())); //要保書申請日-3個工作天
			
			List<Map<String, String>> sResult = svalid.validSeniorCustEvalIOTRM(sInputVO);
			if(CollectionUtils.isNotEmpty(sResult) && StringUtils.equals("A", sResult.get(0).get("invalidCode").toString())) {
				invalidYN = "Y";
			}
		}
		
		return invalidYN;
	}
	
	//取得要保書申請日-3個工作天
	public Date getBeforeApplyDate3(Date applyDate) throws DAOException, JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT PABTH_UTIL.FC_getBusiDay(:applyDate , 'TWD', -3) AS ALLPYDATE3 FROM DUAL ");

		qc.setObject("applyDate", applyDate);
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> dateList = dam.exeQuery(qc);
		
		return CollectionUtils.isNotEmpty(dateList) ? (Date) dateList.get(0).get("ALLPYDATE3") : new Date();
	}
		
	/***
	 * 要保人或被保人或繳款人為高齡
	 * 主管覆核要檢核：
	 * 1. 本行的高齡評估作業異動日期是否介於要保書申請日-3個工作天～要保書申請日區間
	 * 2. 檢核客戶資訊觀察表高齡客戶能力表現問項為8.無上述情形且金融認知問項為4.無上述情形之選項
	 * @param body
	 * @param header
	 * @throws Exception 
	 */
	public void validSeniorCustEvlBoss(Object body, IPrimitiveMap header) throws Exception {
		IOT110InputVO inputVO = (IOT110InputVO) body;
		
		IOT110OutputVO outputVO = validSeniorCustEvlBoss(inputVO);		
		outputVO.setInvalidSeniorCustEvlRM(invalidSeniorCustEvlRM(inputVO));
		
		this.sendRtnObject(outputVO);
	}
	
	/***
	 * 檢核客戶資訊觀察表高齡客戶能力表現問項為8.無上述情形且金融認知問項為4.無上述情形之選項
	 * 被保人/繳款人，非本行客戶不檢核
	 * @param inputVO
	 * @return
	 * invalidSeniorCustEvlBossB: Y:invalid 金融認知結果<>4沒有上述情形
	 * invalidSeniorCustEvlBossC: Y:invalid 能力表現填答<>8.無上述情形
	 * invalidSeniorCustEvlBossD: Y:invalid 金融認知填答第一選項
	 * invalidSeniorCustEvlBossE: Y:invalid 金融認知填答第二或第三個選項
	 * @throws Exception
	 */
	public IOT110OutputVO validSeniorCustEvlBoss(IOT110InputVO inputVO) throws Exception {
		IOT110OutputVO outputVO = new IOT110OutputVO();
		String invalidB = "N";
		String invalidC = "N";
		String invalidD = "N";
		String invalidE = "N";
		
		if(StringUtils.equals("Y", inputVO.getC_SALE_SENIOR_YN())) { //要保人高齡
			List<Map<String, String>> pList = invalidSeniorCustEvlBoss(inputVO.getCUST_ID());
			for(Map<String, String> map : pList) {
				if(StringUtils.equals("B", map.get("invalidCode"))) invalidB = "Y";
				if(StringUtils.equals("C", map.get("invalidCode"))) invalidC = "Y";
				if(StringUtils.equals("D", map.get("invalidCode"))) invalidD = "Y";
				if(StringUtils.equals("E", map.get("invalidCode"))) invalidE = "Y";
			}
		}
		if(!StringUtils.equals("10", inputVO.getINSURED_CM_FLAG()) && StringUtils.equals("Y", inputVO.getI_SALE_SENIOR_YN())) { //本行客戶、被保人高齡
			List<Map<String, String>> pList = invalidSeniorCustEvlBoss(inputVO.getINSURED_ID());
			for(Map<String, String> map : pList) {
				if(StringUtils.equals("B", map.get("invalidCode"))) invalidB = "Y";
				if(StringUtils.equals("C", map.get("invalidCode"))) invalidC = "Y";
				if(StringUtils.equals("D", map.get("invalidCode"))) invalidD = "Y";
				if(StringUtils.equals("E", map.get("invalidCode"))) invalidE = "Y";
			}
		}
		if(!StringUtils.equals("10", inputVO.getPAYER_CM_FLAG()) && StringUtils.equals("Y", inputVO.getP_SALE_SENIOR_YN())) { //本行客戶、繳款人高齡
			List<Map<String, String>> pList = invalidSeniorCustEvlBoss(inputVO.getPAYER_ID());
			for(Map<String, String> map : pList) {
				if(StringUtils.equals("B", map.get("invalidCode"))) invalidB = "Y";
				if(StringUtils.equals("C", map.get("invalidCode"))) invalidC = "Y";
				if(StringUtils.equals("D", map.get("invalidCode"))) invalidD = "Y";
				if(StringUtils.equals("E", map.get("invalidCode"))) invalidE = "Y";
			}
		}
		
		outputVO.setInvalidSeniorCustEvlBossB(invalidB);
		outputVO.setInvalidSeniorCustEvlBossC(invalidC);
		outputVO.setInvalidSeniorCustEvlBossD(invalidD);
		outputVO.setInvalidSeniorCustEvlBossE(invalidE);
		
		return outputVO;
	}
	
	/***
	 * 要保人或被保人或繳款人，年齡>=64.5歲(保險年齡=65歲以上)
	 * B：高齡客戶資訊觀察表金融認知結果是否填答<>4沒有上述情形
	 * C：高齡客戶資訊觀察表取得能力表現是否填答<>8.無上述情形
	 * @param custId
	 * @param applyDate
	 * @return true:檢核不通過  false:檢核通過 
	 * @throws Exception
	 */
	private List<Map<String, String>> invalidSeniorCustEvlBoss(String custId) throws Exception {
		SeniorValidation svalid = (SeniorValidation) PlatformContext.getBean("SeniorValidation");
		SeniorValidationInputVO sInputVO = new SeniorValidationInputVO();
		sInputVO.setCustID(custId);
		
		return svalid.validSeniorCustEvalIOTBOSS(custId);
	}
	
	private boolean validHnwcRiskValue(IOT110InputVO inputVO) throws Exception {
		//買匯匯率
		SOT110 sot110 = (SOT110) PlatformContext.getBean("sot110");
		BigDecimal rate = sot110.getBuyRate(inputVO.getCURR_CD());
		
		//高資產客戶投組風險權值檢核
		SOT714InputVO inputVO714 = new SOT714InputVO();
		inputVO714.setCustID(inputVO.getCUST_ID());
		inputVO714.setCUST_KYC(inputVO.getCUST_RISK()); //客戶C值
		inputVO714.setSP_YN(inputVO.getHnwcDataVO().getSpFlag()); //是否為特定客戶
		inputVO714.setPROD_RISK(inputVO.getPRD_RISK()); //商品風險檢核值
		BigDecimal amtBuy = inputVO.getREAL_PREMIUM().multiply(rate);
		switch(inputVO.getPRD_RISK()) {
			case "P1":
				inputVO714.setAMT_BUY_1(amtBuy);
				break;
			case "P2":
				inputVO714.setAMT_BUY_2(amtBuy);
				break;
			case "P3":
				inputVO714.setAMT_BUY_3(amtBuy);
				break;
			case "P4":
				inputVO714.setAMT_BUY_4(amtBuy);
				break;
		}
		//查詢客戶風險檢核值資料
		SOT714 sot714 = (SOT714) PlatformContext.getBean("sot714");
		WMSHAIADataVO riskValData = sot714.getByPassRiskData(inputVO714);
		
		return StringUtils.equals("Y", riskValData.getVALIDATE_YN()) ? true : false;
	}
	
	/***
	 * 高齡關懷主管員編須檢核角色是否在參數設定中
	 * @param sAuthRole
	 * @return true:是，通過檢核 false:否，不通過檢核 
	 * @throws JBranchException 
	 * @throws DAOException 
	 */
	private boolean validSeniorAuthRole(String sAuthId) throws DAOException, JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT 1 ");
		sb.append(" FROM TBSYSSECUROLPRIASS A ");
		sb.append(" LEFT JOIN TBSYSSECUROLE B ON B.ROLEID = A.ROLEID ");
		sb.append(" LEFT JOIN TBORG_MEMBER_ROLE C ON C.ROLE_ID = B.ROLEID ");
		sb.append(" WHERE A.PRIVILEGEID IN (SELECT PARAM_CODE FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'IOT.SENIOR_AUTH_ROLE') ");
		sb.append(" AND C.EMP_ID = :authId ");
		qc.setObject("authId", sAuthId);
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(qc);
		
		return CollectionUtils.isNotEmpty(list) ? true : false;
	}
}
