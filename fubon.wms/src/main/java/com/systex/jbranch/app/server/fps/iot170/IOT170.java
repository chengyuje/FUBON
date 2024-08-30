package com.systex.jbranch.app.server.fps.iot170;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.reportdata.ConfigAdapterIF;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import com.systex.jbranch.app.server.fps.pms304.PMS304InputVO;
import com.systex.jbranch.app.server.fps.pms304.PMS304OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
/**
 * MENU
 *
 * @author Frank
 * @date 2016/09/23
 * @spec null
 */
@Component("iot170")
@Scope("request")
public class IOT170 extends FubonWmsBizLogic{

	private DataAccessManager dam;

	/**匯率查詢**/
	public void queryExRate(Object body, IPrimitiveMap header) throws JBranchException {

		IOT170OutputVO outputVO = new IOT170OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);   //匯率查詢
		//-----匯率查詢-------
		StringBuffer sql = new StringBuffer();
		sql.append(" WITH EX_RATE AS( ");
		sql.append(" SELECT TO_CHAR(MTN_DATE, 'YYYY-MM-DD') AS MTN_DATE, CREATETIME, ");
		//#6097 原為買賣匯率的均值，改為只需要買匯
		sql.append(" 		CUR_COD, BUY_RATE, BUY_RATE as SEL_RATE  , MAX(TO_CHAR(MTN_DATE,'YYYY-MM-DD')) OVER (PARTITION BY TO_CHAR(MTN_DATE,'YYYYMM')) MAX_DATE ");
		sql.append(" FROM TBPMS_IQ053 ");
		sql.append(" WHERE (TRUNC(MTN_DATE) - TRUNC(MTN_DATE, 'MM')) <= 24 ");
		//20171115 add
		sql.append("   AND TRUNC(MTN_DATE) < TRUNC(	CASE WHEN TO_CHAR(SYSDATE, 'DD') < 25 THEN last_day(ADD_MONTHS(SYSDATE, -1))+1 ");
		sql.append(" 									 ELSE last_day(SYSDATE)+1 END ) ");
		sql.append(" )");
		sql.append(" SELECT * FROM ( ");
		sql.append("    SELECT * FROM EX_RATE WHERE MTN_DATE = MAX_DATE ");
		sql.append(" ) PIVOT ( ");
		sql.append(" SUM( (BUY_RATE + SEL_RATE)/2 ) ");
		sql.append(" FOR CUR_COD IN ('USD' AS USD, 'AUD' AS AUD, 'CNY' AS CNY) ) ");
		sql.append("order by mtn_date desc ");

		condition.setQueryString(sql.toString());

		List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
		resultList = dam.exeQuery(condition);
		outputVO.setResultList(resultList);

		this.sendRtnObject(outputVO);
	}

	/** 鍵機資訊, 品質查詢 **/
	public void queryData(Object body, IPrimitiveMap header) throws JBranchException {

		IOT170InputVO inputVO = (IOT170InputVO) body;
		IOT170OutputVO outputVO = new IOT170OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();

		if("2".equals(inputVO.getReportType())){
			//品質檢核查詢
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT DISTINCT IOT.INS_KEYNO, IOT.KEYIN_DATE, ");
			sql.append("		TO_CHAR(IOT.APPLY_DATE, 'YYYY-MM-DD') AS APPLY_DATE, IOT.OP_BATCH_NO, ");
			sql.append(" 		IOT.INS_ID, IOT.POLICY_NO, IOT.AO_CODE, ");
			sql.append(" 		IOT.BRANCH_NBR, IOT.BRANCH_NAME, IOT.RECRUIT_ID, ");
			sql.append(" 		MEM.CUST_ID AS M_CUST_ID, ");
			sql.append(" 		MEM.EMP_NAME AS M_EMP_NAME, IOT.INSURED_NAME, ");
			sql.append(" 		IOT.INSURED_ID, IOT.PROPOSER_NAME, IOT.CUST_ID, ");
			sql.append(" 		IOT.INSPRD_ID, IOT.INSPRD_NAME, TO_CHAR(IOT.STATUS) AS STATUS, ");
			sql.append(" 		IOT.INSPRD_TYPE, IOT.INSPRD_ANNUAL, IOT.PAY_TYPE, ");
			sql.append(" 		IOT.MOP2, IOT.SPECIAL_CONDITION, IOT.CURR_CD, ");
			sql.append(" 		IOT.EXCH_RATE, IOT.REAL_PREMIUM, IOT.BASE_PREMIUM, ");
			sql.append(" 		IOT.MATCH_DATE, IOT.REF_CON_ID, ");
			sql.append(" 		SAL.SALES_PERSON, SAL.SALES_NAME, IOT.REG_TYPE, ");
			sql.append(" 		IOT.WRITE_REASON, IOT.WRITE_REASON_OTH, ");
			sql.append(" 		IOT.QC_ANC_DOC, IOT.INS_RCV_DATE, IOT.INS_RCV_OPRID, IOT.REMARK_BANK, ");
			sql.append(" 		IOT.DELETE_DATE, IOT.DELETE_OPRID ,IOT.FIRST_PAY_WAY, ");
			sql.append(" 		IOT.QC_ERASER, IOT.OP_DATE, IOT.CREATOR, IOT.QC_STAMP, ");
			sql.append(" 		IOT.AB_TRANSSEQ, IOT.PROPOSER_TRANSSEQ, IOT.INSURED_TRANSSEQ, P.C_SALE_SENIOR_TRANSSEQ, P.I_SALE_SENIOR_TRANSSEQ, P.P_SALE_SENIOR_TRANSSEQ, ");
			sql.append(" 		IOT.PROPOSER_CM_FLAG, IOT.CUST_RISK, TO_CHAR(IOT.CUST_RISK_DUE, 'YYYY-MM-DD') AS CUST_RISK_DUE, ");
			sql.append(" 		IOT.AML, IOT.PRECHECK, IOT.PROPOSER_INCOME1,IOT.PROPOSER_INCOME2, IOT.PROPOSER_INCOME3, ");
			sql.append(" 		IOT.INSURED_INCOME1, IOT.INSURED_INCOME2, IOT.INSURED_INCOME3, ");
			sql.append(" 		IOT.PAYER_ID, IOT.RLT_BT_PROPAY, IOT.LOAN_CHK1_YN,IOT.LOAN_CHK2_YN, ");
			sql.append(" 		IOT.CD_CHK_YN, IOT.INCOME_REMARK, IOT.LOAN_SOURCE_REMARK, IOT.SIGNOFF_DATE, ");
			sql.append(" 		IOT.QC_IMMI, IOT.LOAN_SOURCE_YN, IOT.PREMIUM_TRANSSEQ, IOT.I_PREMIUM_TRANSSEQ, IOT.P_PREMIUM_TRANSSEQ ");
			sql.append("       ,P.LOAN_SOURCE2_YN, P.CONTRACT_END_YN, P.S_INFITEM_LOAN_YN, P.C_LOAN_CHK1_YN, P.C_LOAN_CHK2_YN ");
			sql.append("       ,P.C_CD_CHK_YN, P.C_LOAN_CHK3_YN, P.I_LOAN_CHK1_YN, P.I_LOAN_CHK2_YN, P.I_CD_CHK_YN, P.I_LOAN_CHK3_YN, P.LOAN_CHK3_YN ");
			sql.append("       ,TO_CHAR(P.C_LOAN_APPLY_DATE, 'YYYY-MM-DD') AS C_LOAN_APPLY_DATE, TO_CHAR(P.I_LOAN_APPLY_DATE, 'YYYY-MM-DD') AS I_LOAN_APPLY_DATE ");
			sql.append("	   ,TO_CHAR(P.P_LOAN_APPLY_DATE, 'YYYY-MM-DD') AS P_LOAN_APPLY_DATE, TO_CHAR(P.C_PREM_DATE, 'YYYY-MM-DD') AS C_PREM_DATE ");
			sql.append(" 	   ,TO_CHAR(P.PROPOSER_BIRTH, 'YYYY-MM-DD') AS PROPOSER_BIRTH, TO_CHAR(P.INSURED_BIRTH, 'YYYY-MM-DD') AS INSURED_BIRTH ");
			sql.append(" 	   ,TO_CHAR(P.PAYER_BIRTH, 'YYYY-MM-DD') AS PAYER_BIRTH, P.C_KYC_INCOME, P.I_KYC_INCOME ");
			sql.append(" 	   ,IOT.QC_APEC, IOT.QC_LOAN_CHK, IOT.QC_SIGNATURE, IOT.PREMATCH_SEQ, IOT.CASE_ID ");
			sql.append(" 	   ,P.CANCEL_CONTRACT_YN, P.SENIOR_AUTH_REMARKS, P.SENIOR_AUTH_ID ");
			sql.append("	   ,CASE WHEN IOT.PREMIUM_TRANSSEQ IS NOT NULL OR IOT.I_PREMIUM_TRANSSEQ IS NOT NULL OR IOT.P_PREMIUM_TRANSSEQ IS NOT NULL THEN 'Y' ELSE 'N' END AS RECORD_YN ");
			sql.append(" FROM VWIOT_MAIN IOT ");
			sql.append(" LEFT JOIN TBIOT_PREMATCH P ON P.PREMATCH_SEQ = IOT.PREMATCH_SEQ ");
			sql.append(" LEFT JOIN TBORG_MEMBER MEM ON MEM.EMP_ID = IOT.RECRUIT_ID ");
			sql.append(" LEFT JOIN TBCAM_LOAN_SALEREC SAL ON SAL.REF_CON_ID = IOT.REF_CON_ID ");
			sql.append(" WHERE IOT.REG_TYPE IN ('1','2') ");

			//業務處
			if(StringUtils.isNotBlank(inputVO.getRegion_center_id())){
				sql.append(" AND IOT.REGION_CENTER_ID = :region ");
				condition.setObject("region",inputVO.getRegion_center_id());
			}
			//營運區
			if(StringUtils.isNotBlank(inputVO.getBranch_area_id())){
				sql.append(" AND IOT.BRANCH_AREA_ID = :branchArea ");
				condition.setObject("branchArea",inputVO.getBranch_area_id());
			}
			//分行
			if(StringUtils.isNotBlank(inputVO.getBranch_nbr())){
				sql.append(" AND IOT.BRANCH_NBR = :branchNbr ");
				condition.setObject("branchNbr",inputVO.getBranch_nbr());
			}
			//理專
			if(StringUtils.isNotBlank(inputVO.getEmp_id())){
				sql.append(" AND MEM.EMP_ID = :empid ");
				condition.setObject("empid",inputVO.getEmp_id());
			}
			//保險文件編號
			if(StringUtils.isNotBlank(inputVO.getInsID())){
				sql.append(" AND IOT.INS_ID = :insID ");
				condition.setObject("insID",inputVO.getInsID());
			}
			//保險產品類型
			if(StringUtils.isNotBlank(inputVO.getInsPrdType())){
				sql.append(" AND IOT.INSPRD_TYPE = :INSPRD_TYPE ");
				condition.setObject("INSPRD_TYPE",inputVO.getInsPrdType());
			}
			//保單號碼
			if(StringUtils.isNotBlank(inputVO.getPolicyNo1())){
				sql.append(" AND IOT.POLICY_NO1 = :POLICY_NO1 ");
				condition.setObject("POLICY_NO1",inputVO.getPolicyNo1());
			}
			if(StringUtils.isNotBlank(inputVO.getPolicyNo2())){
				sql.append(" AND IOT.POLICY_NO2 = :POLICY_NO2 ");
				condition.setObject("POLICY_NO2",inputVO.getPolicyNo2());
			}
			if(StringUtils.isNotBlank(inputVO.getPolicyNo3())){
				sql.append(" AND IOT.POLICY_NO3 = :POLICY_NO3 ");
				condition.setObject("POLICY_NO3",inputVO.getPolicyNo3());
			}
			//文件簽收狀態
			if(StringUtils.isNotBlank(inputVO.getDocStatus())){
				sql.append(" AND IOT.STATUS = :STATUS ");
				condition.setObject("STATUS",inputVO.getDocStatus());
			}
			//險種代碼
			if(StringUtils.isNotBlank(inputVO.getInsPrdID())){
				sql.append(" AND IOT.INSPRD_ID = :INSPRD_ID ");
				condition.setObject("INSPRD_ID",inputVO.getInsPrdID());
			}
			//招攬人員編
			if(StringUtils.isNotBlank(inputVO.getRecruitID())){
				sql.append(" AND IOT.RECRUIT_ID = :RECRUIT_ID ");
				condition.setObject("RECRUIT_ID",inputVO.getRecruitID());
			}
			//要保人ID
			if(StringUtils.isNotBlank(inputVO.getCustID())){
				sql.append(" AND IOT.CUST_ID = :CUST_ID ");
				condition.setObject("CUST_ID",inputVO.getCustID());
			}
			//被保人ID
			if(StringUtils.isNotBlank(inputVO.getInsuredID())){
				sql.append(" AND IOT.INSURED_ID = :INSURED_ID ");
				condition.setObject("INSURED_ID",inputVO.getInsuredID());
			}
			//首期繳費方式
			if(StringUtils.isNotBlank(inputVO.getFstPayWay())){
				sql.append(" AND IOT.FIRST_PAY_WAY = :FIRST_PAY_WAY ");
				condition.setObject("FIRST_PAY_WAY",inputVO.getFstPayWay());
			}
			//鍵機日起日
			if(inputVO.getsCreDate() != null){
				sql.append(" AND TRUNC(IOT.KEYIN_DATE) >= TRUNC(TO_DATE( :sDATE, 'YYYY-MM-DD')) ");
				condition.setObject("sDATE",sdf.format(inputVO.getsCreDate()));
			}
			//鍵機日迄日
			if(inputVO.geteCreDate() != null){
				sql.append(" AND TRUNC(IOT.KEYIN_DATE) <= TRUNC(TO_DATE( :eDATE, 'YYYY-MM-DD')) ");
				condition.setObject("eDATE",sdf.format(inputVO.geteCreDate()));
			}
			//投資型保單標的代號
			if(StringUtils.isNotBlank(inputVO.getInvInsId())){
				sql.append(" AND EXISTS (SELECT 'X' FROM VWIOT_FUND_LINK ");
				sql.append("			 WHERE INS_KEYNO = IOT.INS_KEYNO ");
				sql.append("			   AND TARGET_ID = :TARGET_ID ) ");
				condition.setObject("TARGET_ID",inputVO.getInvInsId());
			}
			sql.append(" ORDER BY IOT.KEYIN_DATE DESC ");
			condition.setQueryString(sql.toString());
			resultList = dam.exeQuery(condition);
			outputVO.setExport1ResultList(resultList);

		}else{
			//鍵機資訊查詢
			QueryConditionIF condition_1 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);	//匯出用
			List<Map<String, Object>> excelList = new ArrayList<Map<String,Object>>();
			
			//查詢SQL
			String qSQL = getType1SQL().toString();
			String qSQLWhere = getType1WhereSQL(inputVO, condition).toString();
			//匯出SQL
			String eSQL1 = getType1SQLExport("1").toString();
			String eSQL2 = getType1SQLExport("2").toString();
			String eSQLWhere = getType1WhereSQL(inputVO, condition_1).toString();
			String eSQL = "SELECT * FROM (".concat(eSQL1).concat(eSQLWhere).concat(" UNION ").concat(eSQL2).concat(eSQLWhere).concat(")");
			//排序SQL
			String orderSQL = " ORDER BY KEYIN_DATE DESC ";
			
			//查詢SQL
			condition.setQueryString(qSQL.concat(qSQLWhere).concat(orderSQL));
			//匯出SQL
			condition_1.setQueryString(eSQL.concat(orderSQL));

			resultList = dam.exeQuery(condition);
			excelList = dam.exeQuery(condition_1);

			outputVO.setExport1ResultList(excelList);
		}


		outputVO.setResultList(resultList);

		this.sendRtnObject(outputVO);
	}

	/**受理查詢報表**/
	public void queryStatus(Object body, IPrimitiveMap header) throws JBranchException {

		IOT170InputVO inputVO = (IOT170InputVO) body;
		IOT170OutputVO outputVO = new IOT170OutputVO();
		DataAccessManager dam = this.getDataAccessManager();

		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

		sql.append("SELECT DISTINCT IOT.INS_ID, FBK.POLICY_STATUS, VAIM.INS_NAME, ");
		sql.append("       IOT.PROPOSER_NAME, FBK.CUST_ID, ");
		sql.append("       IOT.KEYIN_DATE, ");
		sql.append("       TO_CHAR(IOT.APPLY_DATE, 'YYYY-MM-DD') AS APPLY_DATE, ");
		sql.append("       FBK.POLICY_NO1 ||'-'|| FBK.POLICY_NO2 ||'-'|| FBK.POLICY_NO3 as POLICY_NO, ");
		sql.append("       FBK.INS_NO, FBK.INS_ITEM, FBK.PREM_YEAR_STR, ");
		sql.append("       VAIM.POLICY_ASSURE_AMT||UNIT INS_YEAR_STR, FBK.PAY_TYPE, VMCL2.EMPY_YN, ");
		sql.append("       FBK.DEPT_CODE, FBK.NB_NOTE_DT, FBK.NB_CONTENT, ");
		sql.append("       FBK.RN_NOTE_DT, FBK.RM_CONTENT, ");
		sql.append("       FBK.INSPECT_DATE, FBK.PREM_PAYABLE, FBK.APPL_DATE, ");
		sql.append("       FBK.DOWN_PAY_TYPE, FBK.SEND_DATE, FBK.SEND_TYPE, ");
		sql.append("       FBK.SIGN_DATE, FBK.REGISTER_NO, FBK.POLI_YEAR, ");
		sql.append("       VMCL2.POLI_PERD, FBK.POLICY_CUR, FBK.BRANCH_NBR, ");
		sql.append("       FBK.ITEM_REMRK, VMCL2.COMU_RATE, FBK.POLICY_EXCH, ");
		sql.append("       FBK.POLICY_EXCH_DATE, VMCL2.GEN_Y_M_D GET_YYMMDD, ");
		sql.append("       VDCL.COMU, VDCL.PREM, VDCL.COMIS, TXN.ANNU_ACT_FEE, VDCL.DECL_ECHG, FBK.REAL_COMU_DATE, ");
		sql.append("       VMCL.PREM REAL_PREM, VMCL.COMIS REAL_COMIS, VMCL.DECL_ECHG REAL_DECL_ECHG, IOT.CREATETIME,IOT.INSURED_NAME,IOT.INSURED_ID, ");
		sql.append("       TXN.RECRUIT_ID, TXN.RECRUIT_NAME ");
		sql.append("FROM TBIOT_FEEDBACK FBK ");
		sql.append("LEFT JOIN VWIOT_MAIN IOT ON IOT.INS_KEYNO = FBK.INS_KEYNO ");
		sql.append("LEFT JOIN TBPMS_INS_TXN TXN ON TXN.INS_ID = IOT.INS_ID AND TXN.TX_TYPE = 'B' ");

		// 保額來自於客管VWCRM_AST_INS_MAST
		// 2020/11/12 效能問題不串VIEW BY OCEAN
//		sql.append("LEFT JOIN VWCRM_AST_INS_MAST VAIM  ON VAIM.POLICY_NBR = FBK.POLICY_NO1 AND VAIM.POLICY_SEQ = FBK.POLICY_NO2 AND VAIM.ID_DUP = FBK.POLICY_NO3 AND VAIM.INS_ID = FBK.INSURED_ID AND VAIM.INSPRD_ID = FBK.INS_ITEM ");
		sql.append("LEFT JOIN ( ");
		sql.append("  SELECT TAIM.POLICY_NBR, TAIM.POLICY_SEQ, TAIM.ID_DUP, TNS.BELONG_ID AS INS_ID, TNS.INS_ITEM AS INSPRD_ID, TNS.INSURED_NAME AS INS_NAME, TNS.EX_INS_AMOUNT AS POLICY_ASSURE_AMT, TNS.EX_UNIT AS UNIT ");
		sql.append("  FROM TBCRM_AST_INS_MAST TAIM ");
		sql.append("  INNER JOIN TBCRM_NPOLD TNS ");
		sql.append("          ON TAIM.POLICY_NBR = TNS.POLICY_NO ");
		sql.append("         AND TAIM.POLICY_SEQ = TNS.POLICY_SEQ ");
		sql.append("         AND TAIM.ID_DUP = TNS.ID_DUP ");
		sql.append("         AND EXISTS ( ");
		sql.append("           SELECT 1 ");
		sql.append("           FROM ( ");
		sql.append("             SELECT DISTINCT TRIM(REGEXP_SUBSTR(STR, '[^,]+', 1, LEVEL)) STR ");
		sql.append("             FROM (SELECT PARAM_CODE AS STR FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'INS.NORMAL_POLICY_STATUS') T ");
		sql.append("             CONNECT BY INSTR(STR, ',', 1, LEVEL - 1) > 0 ");
		sql.append("           ) BASE ");
		sql.append("           WHERE TNS.INS_STATUS = BASE.STR ");
		sql.append("         ) ");
		sql.append(") VAIM ON VAIM.POLICY_NBR = FBK.POLICY_NO1 AND VAIM.POLICY_SEQ = FBK.POLICY_NO2 AND VAIM.ID_DUP = FBK.POLICY_NO3 AND VAIM.INS_ID = FBK.INSURED_ID AND VAIM.INSPRD_ID = FBK.INS_ITEM ");

		// 佣金來自於績效VWPMS_DAY_CLOSE_LATELY
		// 2020/11/12 效能問題不串VIEW BY OCEAN
//		sql.append("LEFT JOIN VWPMS_DAY_CLOSE_LATELY VDCL ON VDCL.POLICY_NO = FBK.POLICY_NO1 AND VDCL.POLICY_SEQ = FBK.POLICY_NO2 AND VDCL.ID_DUP = FBK.POLICY_NO3 AND VDCL.ITEM = FBK.INS_ITEM AND TRIM(TO_CHAR(VDCL.PREM_YEAR, '00')) = TRIM(FBK.PREM_YEAR_STR) ");
		sql.append("LEFT JOIN ( ");
		sql.append("  SELECT DISTINCT COMU_YY || COMU_MM || COMU_DD AS COMU, POLICY_NO, POLICY_SEQ, ID_DUP, ITEM, PREM_YEAR, DECL_ECHG, COMIS, (WORK_YY+1911) AS WORK_YY, WORK_MM, PREM, CREATETIME ");
		sql.append("  FROM TBPMS_DAY_CLOSE DC ");
		sql.append("  WHERE (POLICY_NO, POLICY_SEQ, ID_DUP, CREATETIME) IN ( ");
		sql.append("    SELECT POLICY_NO, POLICY_SEQ, ID_DUP, MAX(CREATETIME) ");
		sql.append("    FROM TBPMS_DAY_CLOSE ");
		sql.append("    GROUP BY POLICY_NO,POLICY_SEQ,ID_DUP ");
		sql.append("  ) ");
		sql.append(") VDCL ON VDCL.POLICY_NO = FBK.POLICY_NO1 AND VDCL.POLICY_SEQ = FBK.POLICY_NO2 AND VDCL.ID_DUP = FBK.POLICY_NO3 AND VDCL.ITEM = FBK.INS_ITEM AND TRIM(TO_CHAR(VDCL.PREM_YEAR, '00')) = TRIM(FBK.PREM_YEAR_STR) ");

		// 實際佣金來自於績效VWPMS_MON_COMIS_LATELY
		// 2020/11/12 效能問題不串VIEW BY OCEAN
//		sql.append("LEFT JOIN VWPMS_MON_COMIS_LATELY VMCL ON VMCL.POLICY_NO = FBK.POLICY_NO1 AND VMCL.POLICY_SEQ = FBK.POLICY_NO2 AND VMCL.ID_DUP = FBK.POLICY_NO3 AND VMCL.ITEM = FBK.INS_ITEM AND TRIM(TO_CHAR(VMCL.PREM_YEAR, '00')) = TRIM(FBK.PREM_YEAR_STR) ");
		sql.append("LEFT JOIN ( ");
		sql.append("  SELECT DISTINCT POLICY_NO, POLICY_SEQ, ID_DUP, ITEM, PREM_YEAR, PREM, COMIS, DECL_ECHG, CREATETIME ");
		sql.append("  FROM TBPMS_MON_COMIS ");
		sql.append("  WHERE (POLICY_NO, POLICY_SEQ, ID_DUP, CREATETIME, ITEM) IN ( ");
		sql.append("    SELECT POLICY_NO, POLICY_SEQ, ID_DUP, MAX(CREATETIME), ITEM ");
		sql.append("    FROM TBPMS_MON_COMIS ");
		sql.append("    GROUP BY POLICY_NO, POLICY_SEQ, ID_DUP, ITEM ");
		sql.append("  ) ");
		sql.append(") VMCL ON VMCL.POLICY_NO = FBK.POLICY_NO1 AND VMCL.POLICY_SEQ = FBK.POLICY_NO2 AND VMCL.ID_DUP = FBK.POLICY_NO3 AND VMCL.ITEM = FBK.INS_ITEM AND TRIM(TO_CHAR(VMCL.PREM_YEAR, '00')) = TRIM(FBK.PREM_YEAR_STR) ");

		// 結案檔
		// 2020/11/12 效能問題不串VIEW BY OCEAN
//		sql.append("LEFT JOIN VWPMS_MON_CLOSE_LATELY VMCL2 ON VMCL2.POLICY_NO = FBK.POLICY_NO1 AND VMCL2.POLICY_SEQ = FBK.POLICY_NO2 AND VMCL2.ID_DUP = FBK.POLICY_NO3 AND VMCL2.ITEM = FBK.INS_ITEM AND TRIM(TO_CHAR(VMCL2.PREM_YEAR, '00')) = TRIM(FBK.PREM_YEAR_STR) ");
		sql.append("LEFT JOIN ( ");
		sql.append("  SELECT DISTINCT GEN_YY || GEN_MM || GEN_DD AS GEN_Y_M_D, COMU_RATE, POLI_PERD, EMPY_YN, POLICY_NO, ID_DUP, POLICY_SEQ, ITEM, PREM_YEAR ");
		sql.append("  FROM TBPMS_MON_CLOSE ");
		sql.append("  WHERE (POLICY_NO, ID_DUP, POLICY_SEQ, CREATETIME) IN ( ");
		sql.append("    SELECT POLICY_NO, ID_DUP, POLICY_SEQ, MAX (CREATETIME) ");
		sql.append("    FROM TBPMS_MON_CLOSE ");
		sql.append("    GROUP BY POLICY_NO, POLICY_SEQ, ID_DUP ");
		sql.append("  ) ");
		sql.append(") VMCL2 ON VMCL2.POLICY_NO = FBK.POLICY_NO1 AND VMCL2.POLICY_SEQ = FBK.POLICY_NO2 AND VMCL2.ID_DUP = FBK.POLICY_NO3 AND VMCL2.ITEM = FBK.INS_ITEM AND TRIM(TO_CHAR(VMCL2.PREM_YEAR, '00')) = TRIM(FBK.PREM_YEAR_STR) ");

		sql.append("LEFT JOIN TBORG_SALES_AOCODE AO ON IOT.AO_CODE = AO.AO_CODE ");
		sql.append("LEFT JOIN TBORG_MEMBER MEM ON MEM.EMP_ID = AO.EMP_ID ");
		sql.append("WHERE 1 = 1 ");

		//業務處
		if(StringUtils.isNotBlank(inputVO.getRegion_center_id())){
			sql.append("AND IOT.REGION_CENTER_ID = :REGION ");
			condition.setObject("REGION",inputVO.getRegion_center_id());
		}

		//營運區
		if(StringUtils.isNotBlank(inputVO.getBranch_area_id())){
			sql.append("AND IOT.BRANCH_AREA_ID = :BRANCH_AREA_ID ");
			condition.setObject("BRANCH_AREA_ID",inputVO.getBranch_area_id());
		}

		//分行
		if(StringUtils.isNotBlank(inputVO.getBranch_nbr())){
			sql.append("AND IOT.BRANCH_NBR = :BRANCH_NBR ");
			condition.setObject("BRANCH_NBR",inputVO.getBranch_nbr());
		}

		//理專
		if(StringUtils.isNotBlank(inputVO.getEmp_id())){
			sql.append("AND MEM.EMP_ID = :EMP_ID ");
			condition.setObject("EMP_ID",inputVO.getEmp_id());
		}

		//保險文件編號
		if(StringUtils.isNotBlank(inputVO.getInsID())){
			sql.append("AND IOT.INS_ID = :INS_ID ");
			condition.setObject("INS_ID",inputVO.getInsID());
		}

		//保險產品類型
		if(StringUtils.isNotBlank(inputVO.getInsPrdType())){
			sql.append("AND IOT.INSPRD_TYPE = :INSPRD_TYPE ");
			condition.setObject("INSPRD_TYPE",inputVO.getInsPrdType());
		}

		//保單號碼
		if(StringUtils.isNotBlank(inputVO.getPolicyNo1())){
			sql.append("AND IOT.POLICY_NO1 = :POLICY_NO1 ");
			condition.setObject("POLICY_NO1",inputVO.getPolicyNo1());
		}

		if(StringUtils.isNotBlank(inputVO.getPolicyNo2())){
			sql.append("AND IOT.POLICY_NO2 = :POLICY_NO2 ");
			condition.setObject("POLICY_NO2",inputVO.getPolicyNo2());
		}

		if(StringUtils.isNotBlank(inputVO.getPolicyNo3())){
			sql.append("AND IOT.POLICY_NO3 = :POLICY_NO3 ");
			condition.setObject("POLICY_NO3",inputVO.getPolicyNo3());
		}

		//文件簽收狀態
		if(StringUtils.isNotBlank(inputVO.getDocStatus())){
			sql.append("AND IOT.STATUS = :STATUS ");
			condition.setObject("STATUS",inputVO.getDocStatus());
		}

		//險種代碼
//		if(StringUtils.isNotBlank(inputVO.getInsPrdID())){
//			sql.append(" AND IOT.INSPRD_ID = :INSPRD_ID ");
//			condition.setObject("INSPRD_ID",inputVO.getInsPrdID());
//		}
		if(StringUtils.isNotBlank(inputVO.getInsPrdID())){         //此為原邏輯
			sql.append("AND FBK.INS_ITEM = :INSPRD_ID ");
			condition.setObject("INSPRD_ID",inputVO.getInsPrdID());
		}

		//招攬人員編
		if(StringUtils.isNotBlank(inputVO.getRecruitID())){
			sql.append("AND IOT.RECRUIT_ID = :RECRUIT_ID ");
			condition.setObject("RECRUIT_ID",inputVO.getRecruitID());
		}

		//要保人ID
		if(StringUtils.isNotBlank(inputVO.getCustID())){
			sql.append("AND IOT.CUST_ID = :CUST_ID ");
			condition.setObject("CUST_ID",inputVO.getCustID());
		}

		//被保人ID
		if(StringUtils.isNotBlank(inputVO.getInsuredID())){
			sql.append("AND IOT.INSURED_ID = :INSURED_ID ");
			condition.setObject("INSURED_ID",inputVO.getInsuredID());
		}

		//首期繳費方式
//		if(StringUtils.isNotBlank(inputVO.getFstPayWay())){
//			sql.append(" AND IOT.FIRST_PAY_WAY = :FIRST_PAY_WAY ");
//			condition.setObject("FIRST_PAY_WAY",inputVO.getFstPayWay());
//		}
		if(StringUtils.isNotBlank(inputVO.getFstPayWay())){       //此為原邏輯
			sql.append("AND FBK.DOWN_PAY_TYPE = :FIRST_PAY_WAY ");
			condition.setObject("FIRST_PAY_WAY",inputVO.getFstPayWay());
		}

		//鍵機日起日
		if(inputVO.getsCreDate() != null){
			sql.append("AND TRUNC(IOT.KEYIN_DATE) >= TRUNC(TO_DATE( :sDATE, 'YYYY-MM-DD')) ");
			condition.setObject("sDATE",sdf.format(inputVO.getsCreDate()));
		}

		//鍵機日迄日
		if(inputVO.geteCreDate() != null){
			sql.append("AND TRUNC(IOT.KEYIN_DATE) <= TRUNC(TO_DATE( :eDATE, 'YYYY-MM-DD')) ");
			condition.setObject("eDATE",sdf.format(inputVO.geteCreDate()));
		}

		//投資型保單標的代號
		if(StringUtils.isNotBlank(inputVO.getInvInsId())){
			sql.append("AND EXISTS ( ");
			sql.append("  SELECT 'X' ");
			sql.append("  FROM VWIOT_FUND_LINK ");
			sql.append("  WHERE INS_KEYNO = IOT.INS_KEYNO ");
			sql.append("  AND TARGET_ID = :TARGET_ID ");
			sql.append(") ");
			condition.setObject("TARGET_ID",inputVO.getInvInsId());
		}

		//保險工作年月
		if(StringUtils.isNotBlank(inputVO.getWorkYear())){
			sql.append("AND VDCL.WORK_YY = :WORK_YEAR ");
			condition.setObject("WORK_YEAR",inputVO.getWorkYear());
		}

		if(StringUtils.isNotBlank(inputVO.getWorkMonth())){
			sql.append("AND VDCL.WORK_MM = :WORK_MONTH ");
			condition.setObject("WORK_MONTH",inputVO.getWorkMonth());
		}

		//人壽受理進度(保單發單狀態)
		if(StringUtils.isNotBlank(inputVO.getPolicyStatus())){
			sql.append("AND FBK.INS_STATUS = :POLICYSTAT ");
			condition.setObject("POLICYSTAT",inputVO.getPolicyStatus());
		}

		//要保書填寫日期
		if(inputVO.getsApplyDate() != null){
			sql.append("AND TRUNC(IOT.APPLY_DATE) >= TRUNC(TO_DATE( :sAPPDATE, 'YYYY-MM-DD')) ");
			condition.setObject("sAPPDATE",sdf.format(inputVO.getsApplyDate()));
		}

		if(inputVO.geteApplyDate() != null){
			sql.append("AND TRUNC(IOT.APPLY_DATE) <= TRUNC(TO_DATE( :eAPPDATE, 'YYYY-MM-DD')) ");
			condition.setObject("eAPPDATE",sdf.format(inputVO.geteApplyDate()));
		}

		sql.append("ORDER BY IOT.KEYIN_DATE DESC ");

		condition.setQueryString(sql.toString().replaceAll("\\s+", " "));

		outputVO.setResultList(dam.exeQuery(condition));

		this.sendRtnObject(outputVO);
	}

	/**核實報表**/
	public void queryReport(Object body, IPrimitiveMap header) throws Exception {
		IOT170InputVO inputVO = (IOT170InputVO) body;
		IOT170OutputVO outputVO = new IOT170OutputVO();

		// 全行合計
		List<Map<String, Object>> totalList = getTotal("total", inputVO);
		// 業務處合計
		List<Map<String, Object>> regionList = getTotal("region", inputVO);
		// 營運區合計
		List<Map<String, Object>> areaList = getTotal("area", inputVO);
		// 總查詢
		List<Map<String, Object>> resultList = getTotal("result", inputVO);

		outputVO.setTotalList(totalList);
		outputVO.setRegionList(regionList);
		outputVO.setAreaList(areaList);
		outputVO.setResultList(resultList);
		this.sendRtnObject(outputVO);
	}

	private List<Map<String, Object>> getTotal(String type, IOT170InputVO inputVO) throws Exception{

		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");

		sql.append("WITH BASE AS ");
		sql.append("        ( SELECT DISTINCT TXN.INS_ID, FBK.PMS_STATUS,TXN.ANNU_ACT_FEE, DECODE(TXN.ANNU_ACT_FEE,0,0,1) AS CNT, COALESCE(COM.COMIS, CLO.COMIS,0) AS COMIS ");
		sql.append("                  ,DECODE(COALESCE(COM.COMIS, CLO.COMIS,0),0,0,1) AS COM_CNT ");
		sql.append("                  ,ORG.REGION_CENTER_ID, ORG.REGION_CENTER_NAME ");
		sql.append("                  ,ORG.BRANCH_AREA_ID, ORG.BRANCH_AREA_NAME ");
		sql.append("                  ,ORG.BRANCH_NBR, ORG.BRANCH_NAME, NVL(ORG.BRANCH_CLS,0) AS BRANCH_CLS ");
		sql.append("          FROM TBPMS_INS_TXN TXN ");
		sql.append("          LEFT JOIN TBPMS_ORG_REC_N ORG ON ORG.BRANCH_NBR = TXN.BRANCH_NBR  AND ORG.ORG_TYPE = '50' AND TXN.TX_DATE BETWEEN TO_CHAR(ORG.START_TIME,'YYYYMMDD') AND TO_CHAR(ORG.END_TIME,'YYYYMMDD')");  //歷史組織檔
		sql.append(" 		  LEFT JOIN (SELECT CESS_NO, SUM(NVL(COMIS, 0)) AS COMIS FROM TBPMS_DAY_CLOSE GROUP BY CESS_NO) CLO ON CLO.CESS_NO = TXN.INS_ID ");  //日佣檔
		sql.append(" 		  LEFT JOIN (SELECT CESS_NO, SUM(NVL(COMIS, 0)) AS COMIS FROM TBPMS_MON_COMIS GROUP BY CESS_NO) COM ON COM.CESS_NO = TXN.INS_ID ");  //核實佣檔
		sql.append("          LEFT JOIN TBIOT_MAIN IOT ON IOT.INS_ID = TXN.INS_ID ");  //保險進件主檔
		sql.append("          LEFT JOIN TBIOT_FEEDBACK FBK ON FBK.INS_KEYNO = IOT.INS_KEYNO ");  //保險進件人壽回饋檔
//		sql.append("          LEFT JOIN TBPMS_IQ053 IQ ON IQ.MTN_DATE = (SELECT MAX(MTN_DATE) FROM TBPMS_IQ053) AND IQ.CUR_COD = TXN.CURR_CD ");  //匯率檔
		sql.append("          LEFT JOIN TBPMS_INS_ACCEPT_TIME ACP ON TXN.TX_DATE BETWEEN TO_CHAR(ACP.INS_PROFIT_S,'YYYYMMDD') AND TO_CHAR(ACP.INS_PROFIT_E,'YYYYMMDD') ");
		sql.append("          LEFT JOIN TBORG_SALES_AOCODE AO ON TXN.AO_CODE = AO.AO_CODE ");
		sql.append("          LEFT JOIN TBORG_MEMBER MEM ON MEM.EMP_ID = AO.EMP_ID ");
		sql.append("          WHERE 1=1 ");

		if("result".equals(type)){
			if(StringUtils.isNotBlank(inputVO.getRegion_center_id())){
				sql.append(" AND ORG.REGION_CENTER_ID = :region ");
				condition.setObject("region",inputVO.getRegion_center_id());
			}
			if(StringUtils.isNotBlank(inputVO.getBranch_area_id())){
				sql.append(" AND ORG.BRANCH_AREA_ID = :branchArea ");
				condition.setObject("branchArea",inputVO.getBranch_area_id());
			}
			if(StringUtils.isNotBlank(inputVO.getBranch_nbr())){
				sql.append(" AND ORG.BRANCH_NBR = :branchNbr ");
				condition.setObject("branchNbr",inputVO.getBranch_nbr());
			}
			if(StringUtils.isNotBlank(inputVO.getEmp_id())){
				sql.append(" AND MEM.EMP_ID = :empid ");
				condition.setObject("empid",inputVO.getEmp_id());
			}
		}else if ("area".equals(type)){
			if(StringUtils.isNotBlank(inputVO.getRegion_center_id())){
				sql.append(" AND ORG.REGION_CENTER_ID = :region ");
				condition.setObject("region",inputVO.getRegion_center_id());
			}
			if(StringUtils.isNotBlank(inputVO.getBranch_area_id())){
				sql.append(" AND ORG.BRANCH_AREA_ID = :branchArea ");
				condition.setObject("branchArea",inputVO.getBranch_area_id());
			}
		}else if ("region".equals(type)){
			if(StringUtils.isNotBlank(inputVO.getRegion_center_id())){
				sql.append(" AND ORG.REGION_CENTER_ID = :region ");
				condition.setObject("region",inputVO.getRegion_center_id());
			}
		}
		//保險工作年月
		if(StringUtils.isNotBlank(inputVO.getWorkYear()) || StringUtils.isNotBlank(inputVO.getWorkMonth()) || inputVO.getsCloseDate() != null || inputVO.geteCloseDate() != null){
			sql.append(" AND TXN.INS_ID IN (SELECT CESS_NO FROM TBPMS_DAY_CLOSE WHERE 1=1 ");

			if(StringUtils.isNotBlank(inputVO.getWorkYear())){
				sql.append(" AND WORK_YY = :WORK_YEAR ");
				condition.setObject("WORK_YEAR", org.apache.commons.lang.ObjectUtils.toString(Integer.parseInt(inputVO.getWorkYear()) - 1911));
			}
			if(StringUtils.isNotBlank(inputVO.getWorkMonth())){
				sql.append(" AND WORK_MM = :WORK_MONTH ");
				condition.setObject("WORK_MONTH",inputVO.getWorkMonth());
			}
			///結案日 (核實佣檔沒有就用日佣檔計算)
			if(inputVO.getsCloseDate() != null){
				sql.append(" AND ( DECODE(END_YY,null,'0', (END_YY + 1911) || DECODE(LENGTH(END_MM),1,'0'||END_MM ,END_MM) || DECODE(LENGTH(END_DD),1,'0'||END_DD,END_DD)) ) >= :sCDATE ");
				condition.setObject("sCDATE",sdf1.format(inputVO.getsCloseDate()));
			}
			if(inputVO.geteCloseDate() != null){
				sql.append(" AND ( DECODE(END_YY,null,'0', (END_YY + 1911) || DECODE(LENGTH(END_MM),1,'0'||END_MM ,END_MM) || DECODE(LENGTH(END_DD),1,'0'||END_DD,END_DD)) ) <= :eCDATE ");
				condition.setObject("eCDATE",sdf1.format(inputVO.geteCloseDate()));
			}

			sql.append(" UNION ");
			sql.append(" SELECT CESS_NO FROM TBPMS_MON_COMIS WHERE 1=1 ");

			if(StringUtils.isNotBlank(inputVO.getWorkYear())){
				sql.append(" AND SUBSTR(WORK_YY, 2, 3) = :WORK_YEAR ");
			}
			if(StringUtils.isNotBlank(inputVO.getWorkMonth())){
				sql.append(" AND WORK_MM = :WORK_MONTH ");
			}
			//結案日 (核實佣檔沒有就用日佣檔計算)
			if(inputVO.getsCloseDate() != null){
				sql.append(" AND ( DECODE(END_YY,null,'0', (END_YY + 1911) || DECODE(LENGTH(END_MM),1,'0'||END_MM ,END_MM) || DECODE(LENGTH(END_DD),1,'0'||END_DD,END_DD)) ) >= :sCDATE ");
			}
			if(inputVO.geteCloseDate() != null){
				sql.append(" AND ( DECODE(END_YY,null,'0', (END_YY + 1911) || DECODE(LENGTH(END_MM),1,'0'||END_MM ,END_MM) || DECODE(LENGTH(END_DD),1,'0'||END_DD,END_DD)) ) <= :eCDATE ");
			}

			sql.append(" ) ");
		}

		//保險文件編號
		if(StringUtils.isNotBlank(inputVO.getInsID())){
			sql.append(" AND TXN.INS_ID = :INS_ID ");
			condition.setObject("INS_ID",inputVO.getInsID());
		}
		//險種代號
		if(StringUtils.isNotBlank(inputVO.getInsPrdID())){
			sql.append("AND TXN.PRD_ID = :INSPRD_ID ");
			condition.setObject("INSPRD_ID",inputVO.getInsPrdID());
		}
		//要保人ID
		if(StringUtils.isNotBlank(inputVO.getCustID())){
			sql.append(" AND TXN.CUST_ID = :CUST_ID ");
			condition.setObject("CUST_ID",inputVO.getCustID());
		}
		//被保人ID
		if(StringUtils.isNotBlank(inputVO.getInsuredID())){
			sql.append(" AND TXN.INSURED_ID = :INSURED_ID ");
			condition.setObject("INSURED_ID",inputVO.getInsuredID());
		}
		//招攬人員編
		if(StringUtils.isNotBlank(inputVO.getRecruitID())){
			sql.append(" AND TXN.RECRUIT_ID = :RECRUIT_ID ");
			condition.setObject("RECRUIT_ID",inputVO.getRecruitID());
		}
		//人壽受理進度(保單發單狀態)
		if(StringUtils.isNotBlank(inputVO.getPolicyStatus())){
			sql.append("AND FBK.INS_STATUS = :POLICYSTAT ");
			condition.setObject("POLICYSTAT",inputVO.getPolicyStatus());
		}
		//保單號碼
		if(StringUtils.isNotBlank(inputVO.getPolicyNo1())){
			sql.append(" AND IOT.POLICY_NO1 = :POLICY_NO1 ");
			condition.setObject("POLICY_NO1",inputVO.getPolicyNo1());
		}
		if(StringUtils.isNotBlank(inputVO.getPolicyNo2())){
			sql.append(" AND IOT.POLICY_NO2 = :POLICY_NO2 ");
			condition.setObject("POLICY_NO2",inputVO.getPolicyNo2());
		}
		if(StringUtils.isNotBlank(inputVO.getPolicyNo3())){
			sql.append(" AND IOT.POLICY_NO3 = :POLICY_NO3 ");
			condition.setObject("POLICY_NO3",inputVO.getPolicyNo3());
		}
		//計績工作年月
		if(StringUtils.isNotBlank(inputVO.getsWorkDate())){
			sql.append(" AND ACP.YEARMON >= TO_CHAR(ADD_MONTHS(SYSDATE, :sPDATE),'YYYYMM') ");
			condition.setObject("sPDATE",Integer.parseInt(inputVO.getsWorkDate()) * (-1) );
		}
		if(StringUtils.isNotBlank(inputVO.geteWorkDate())){
			sql.append(" AND ACP.YEARMON <= TO_CHAR(ADD_MONTHS(SYSDATE, :ePDATE ),'YYYYMM') ");
			condition.setObject("ePDATE",Integer.parseInt(inputVO.geteWorkDate()) * (-1) );
		}
		//鍵機日起日
		if(inputVO.getsCreDate() != null){
			sql.append(" AND TRUNC(TXN.KEYIN_DATE) >= TRUNC(TO_DATE( :sDATE, 'YYYY-MM-DD')) ");
			condition.setObject("sDATE",sdf.format(inputVO.getsCreDate()));
		}
		//鍵機日迄日
		if(inputVO.geteCreDate() != null){
			sql.append(" AND TRUNC(TXN.KEYIN_DATE) <= TRUNC(TO_DATE( :eDATE, 'YYYY-MM-DD')) ");
			condition.setObject("eDATE",sdf.format(inputVO.geteCreDate()));
		}
		//要保書填寫日期
		if(inputVO.getsApplyDate() != null){
			sql.append(" AND TRUNC(TXN.APPLY_DATE) >= TRUNC(TO_DATE( :sAPPDATE, 'YYYY-MM-DD')) ");
			condition.setObject("sAPPDATE",sdf.format(inputVO.getsApplyDate()));
		}
		if(inputVO.geteApplyDate() != null){
			sql.append(" AND TRUNC(TXN.APPLY_DATE) <= TRUNC(TO_DATE( :eAPPDATE, 'YYYY-MM-DD')) ");
			condition.setObject("eAPPDATE",sdf.format(inputVO.geteApplyDate()));
		}

		sql.append("    ), ");
		sql.append("    BASE_A AS  ");
		sql.append("      ( SELECT PMS_STATUS, SUM(ANNU_ACT_FEE) AS ANNU_ACT_FEE,  SUM(CNT) AS CNT, SUM(COMIS) AS COMIS, SUM(COM_CNT) AS COM_CNT,REGION_CENTER_ID,REGION_CENTER_NAME ");
		sql.append("              ,BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, NVL(BRANCH_CLS,0) AS BRANCH_CLS");
		sql.append("        FROM BASE ");
		sql.append("        GROUP BY PMS_STATUS,REGION_CENTER_ID,REGION_CENTER_NAME,BRANCH_AREA_ID, BRANCH_AREA_NAME,BRANCH_NBR, BRANCH_NAME, BRANCH_CLS ");
		sql.append("      ), ");
		sql.append("    BASE_ORG AS  ");
		sql.append("      ( SELECT REGION_CENTER_ID,REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, NVL(BRANCH_CLS,0) AS BRANCH_CLS ");
		sql.append("        FROM BASE_A ");
		sql.append("        WHERE REGION_CENTER_ID IS NOT NULL ");
		sql.append("          AND BRANCH_AREA_ID IS NOT NULL ");
		sql.append("          AND BRANCH_NBR IS NOT NULL ");
		sql.append("        GROUP BY REGION_CENTER_ID,REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, BRANCH_CLS ");
		sql.append("      ) ");

		if(!"result".equals(type)){

			if("total".equals(type)){
				sql.append(" SELECT ");
			}else if("region".equals(type)){
				sql.append(" SELECT REGION_CENTER_ID,REGION_CENTER_NAME, ");
			}else{
				sql.append(" SELECT BRANCH_AREA_ID, BRANCH_AREA_NAME, ");
			}

				sql.append("      	NVL(SUM(TOTAL_CNT),0) AS TOTAL_CNT, NVL(SUM(TOTAL_SUM),0) AS TOTAL_SUM, ");
				sql.append("        NVL(SUM(COM_CNT),0) AS COM_CNT, NVL(SUM(COMIS),0) AS COMIS, DECODE(SUM(COMIS),0,0,SUM(COMIS)/SUM(TOTAL_SUM)) AS COM_RATE, ");
				sql.append("        NVL(SUM(B_CNT),0) AS B_CNT, NVL(SUM(B_SUM),0) AS B_SUM, DECODE(SUM(B_SUM),0,0,SUM(B_SUM)/SUM(TOTAL_SUM)) AS B_RATE, ");
				sql.append("        NVL(SUM(D_CNT),0) AS D_CNT, NVL(SUM(D_SUM),0) AS D_SUM, DECODE(SUM(D_SUM),0,0,SUM(D_SUM)/SUM(TOTAL_SUM)) AS D_RATE, ");
				sql.append("        NVL(SUM(C_CNT),0) AS C_CNT, NVL(SUM(C_SUM),0) AS C_SUM, DECODE(SUM(C_SUM),0,0,SUM(C_SUM)/SUM(TOTAL_SUM)) AS C_RATE, ");
				sql.append("        NVL(SUM(F_CNT),0) AS F_CNT, NVL(SUM(F_SUM),0) AS F_SUM, DECODE(SUM(F_SUM),0,0,SUM(F_SUM)/SUM(TOTAL_SUM)) AS F_RATE, ");
				sql.append("        NVL(SUM(J_CNT),0) AS J_CNT, NVL(SUM(J_SUM),0) AS J_SUM, DECODE(SUM(J_SUM),0,0,SUM(J_SUM)/SUM(TOTAL_SUM)) AS J_RATE ");
				sql.append(" FROM( ");
		}

		sql.append("     SELECT  BA.REGION_CENTER_ID, BA.REGION_CENTER_NAME,BA.BRANCH_AREA_NAME,BA.BRANCH_AREA_ID, ");
		sql.append("             BA.BRANCH_NBR, BA.BRANCH_NAME,BA.BRANCH_CLS, ");
		sql.append("             (A.COM_CNT + NVL(B_CNT,0) + NVL(C_CNT,0) + NVL(D_CNT,0) + NVL(F_CNT,0) + NVL(J_CNT,0)) AS TOTAL_CNT, ");
		sql.append("             (A.COMIS + NVL(B_SUM,0) + NVL(C_SUM,0) + NVL(D_SUM,0) + NVL(F_SUM,0) + NVL(J_SUM,0)) AS TOTAL_SUM, ");
		sql.append("             A.COM_CNT, A.COMIS, ");
		sql.append("             DECODE(A.COMIS,0,0,A.COMIS/(A.COMIS + NVL(B_SUM,0) + NVL(C_SUM,0) + NVL(D_SUM,0) + NVL(F_SUM,0) + NVL(J_SUM,0))) AS COM_RATE, ");
		sql.append("             NVL(B_CNT,0) AS B_CNT, NVL(B_SUM,0) AS B_SUM,  ");
		sql.append("             DECODE(NVL(B_SUM,0),0,0,NVL(B_SUM,0)/(A.COMIS + NVL(B_SUM,0) + NVL(C_SUM,0) + NVL(D_SUM,0) + NVL(F_SUM,0) + NVL(J_SUM,0))) AS B_RATE, ");
		sql.append("             NVL(D_CNT,0) AS D_CNT, NVL(D_SUM,0) AS D_SUM, ");
		sql.append("             DECODE(NVL(D_SUM,0),0,0,NVL(D_SUM,0)/(A.COMIS + NVL(B_SUM,0) + NVL(C_SUM,0) + NVL(D_SUM,0) + NVL(F_SUM,0) + NVL(J_SUM,0))) AS D_RATE, ");
		sql.append("             NVL(C_CNT,0) AS C_CNT, NVL(C_SUM,0) AS C_SUM, ");
		sql.append("             DECODE (NVL(C_SUM,0),0,0,NVL(C_SUM,0)/(A.COMIS + NVL(B_SUM,0) + NVL(C_SUM,0) + NVL(D_SUM,0) + NVL(F_SUM,0) + NVL(J_SUM,0))) AS C_RATE, ");
		sql.append("             NVL(F_CNT,0) AS F_CNT, NVL(F_SUM,0) AS F_SUM, ");
		sql.append("             DECODE(NVL(F_SUM,0),0,0,NVL(F_SUM,0)/(A.COMIS + NVL(B_SUM,0) + NVL(C_SUM,0) + NVL(D_SUM,0) + NVL(F_SUM,0) + NVL(J_SUM,0))) AS F_RATE, ");
		sql.append("             NVL(J_CNT,0) AS J_CNT, NVL(J_SUM,0) AS J_SUM, ");
		sql.append("             DECODE(NVL(J_SUM,0),0,0,NVL(J_SUM,0)/(A.COMIS + NVL(B_SUM,0) + NVL(C_SUM,0) + NVL(D_SUM,0) + NVL(F_SUM,0) + NVL(J_SUM,0))) AS J_RATE ");
		sql.append("     FROM BASE_ORG BA ");
		// 已核實預估佣收
		sql.append("     LEFT JOIN ( SELECT BRANCH_AREA_ID, BRANCH_NBR, BRANCH_CLS, SUM(COMIS) AS COMIS, SUM(COM_CNT) AS COM_CNT ");
		sql.append("                 FROM BASE_A ");
		sql.append("	     		 WHERE PMS_STATUS IS NULL OR PMS_STATUS = 'A' ");
		sql.append("                 GROUP BY BRANCH_AREA_ID, BRANCH_NBR, BRANCH_CLS ");
		sql.append("               )A ON BA.BRANCH_NBR = A.BRANCH_NBR AND BA.BRANCH_CLS = A.BRANCH_CLS AND BA.BRANCH_AREA_ID = A.BRANCH_AREA_ID");
		// 核保通過待結案
		sql.append("     LEFT JOIN ( SELECT BRANCH_AREA_ID, BRANCH_NBR, BRANCH_CLS, SUM(CNT) AS B_CNT, SUM(ANNU_ACT_FEE) AS B_SUM ");
		sql.append("                 FROM BASE_A ");
		sql.append("                 WHERE PMS_STATUS = 'B' ");
		sql.append("                 GROUP BY BRANCH_AREA_ID, BRANCH_NBR, BRANCH_CLS ");
		sql.append("               ) B ON BA.BRANCH_NBR = B.BRANCH_NBR AND BA.BRANCH_CLS = B.BRANCH_CLS AND BA.BRANCH_AREA_ID = B.BRANCH_AREA_ID");
		// 照會中
		sql.append("     LEFT JOIN ( SELECT BRANCH_AREA_ID, BRANCH_NBR, BRANCH_CLS, SUM(CNT) AS D_CNT, SUM(ANNU_ACT_FEE) AS D_SUM ");
		sql.append("                 FROM BASE_A ");
		sql.append("                 WHERE PMS_STATUS IN ('D','E') ");
		sql.append("                 GROUP BY BRANCH_AREA_ID, BRANCH_NBR, BRANCH_CLS ");
		sql.append("               ) D ON BA.BRANCH_NBR = D.BRANCH_NBR AND BA.BRANCH_CLS = D.BRANCH_CLS AND BA.BRANCH_AREA_ID = D.BRANCH_AREA_ID");
		// 核保中
		sql.append("     LEFT JOIN ( SELECT BRANCH_AREA_ID, BRANCH_NBR, BRANCH_CLS, SUM(CNT) AS C_CNT, SUM(ANNU_ACT_FEE) AS C_SUM ");
		sql.append("                 FROM BASE_A ");
		sql.append("                 WHERE PMS_STATUS = 'C' ");
		sql.append("                 GROUP BY BRANCH_AREA_ID, BRANCH_NBR, BRANCH_CLS ");
		sql.append("               ) C ON BA.BRANCH_NBR = C.BRANCH_NBR AND BA.BRANCH_CLS = C.BRANCH_CLS AND BA.BRANCH_AREA_ID = C.BRANCH_AREA_ID");
		// 契撤/取消/拒保/延期
		sql.append("     LEFT JOIN ( SELECT BRANCH_AREA_ID, BRANCH_NBR, BRANCH_CLS, SUM(CNT) AS F_CNT, SUM(ANNU_ACT_FEE) AS F_SUM ");
		sql.append("                 FROM BASE_A ");
		sql.append("                 WHERE PMS_STATUS IN ('F','G','H','I') ");
		sql.append("                 GROUP BY BRANCH_AREA_ID, BRANCH_NBR, BRANCH_CLS ");
		sql.append("               ) F ON BA.BRANCH_NBR = F.BRANCH_NBR AND BA.BRANCH_CLS = F.BRANCH_CLS AND BA.BRANCH_AREA_ID = F.BRANCH_AREA_ID");
		// 待確認
		sql.append("     LEFT JOIN ( SELECT BRANCH_AREA_ID, BRANCH_NBR, BRANCH_CLS, SUM(CNT) AS J_CNT, SUM(ANNU_ACT_FEE) AS J_SUM ");
		sql.append("                 FROM BASE_A ");
		sql.append("                 WHERE PMS_STATUS = 'J' ");
		sql.append("                 GROUP BY BRANCH_AREA_ID, BRANCH_NBR, BRANCH_CLS ");
		sql.append("               ) J ON BA.BRANCH_NBR = J.BRANCH_NBR AND BA.BRANCH_CLS = J.BRANCH_CLS AND BA.BRANCH_AREA_ID = J.BRANCH_AREA_ID");
		sql.append("     WHERE 1 = 1 ");
		//預估收益
		if(StringUtils.isNotBlank(inputVO.getEstIncomeLowLimit())){
			sql.append("   AND (A.COMIS + NVL(B_SUM,0) + NVL(C_SUM,0) + NVL(D_SUM,0) + NVL(F_SUM,0) + NVL(J_SUM,0)) >= :EST_LCOM ");
			condition.setObject("EST_LCOM", inputVO.getEstIncomeLowLimit());
		}
		if(StringUtils.isNotBlank(inputVO.getEstIncomeUpLimit())){
			sql.append("    AND (A.COMIS + NVL(B_SUM,0) + NVL(C_SUM,0) + NVL(D_SUM,0) + NVL(F_SUM,0) + NVL(J_SUM,0)) <= :EST_UCOM ");
			condition.setObject("EST_UCOM", inputVO.getEstIncomeUpLimit());
		}
		//實際收益(核實佣收)
		if(StringUtils.isNotBlank(inputVO.getActIncomeLowLimit())){
			sql.append("    AND A.COMIS >= :VER_LCOM ");
			condition.setObject("VER_LCOM", inputVO.getActIncomeLowLimit());
		}
		if(StringUtils.isNotBlank(inputVO.getActIncomeUpLimit())){
			sql.append("    AND A.COMIS <= :VER_UCOM ");
			condition.setObject("VER_UCOM", inputVO.getActIncomeUpLimit());
		}
		sql.append("   ORDER BY BA.REGION_CENTER_ID, BA.BRANCH_AREA_ID, BA.BRANCH_NBR ");

		if("total".equals(type)){
			sql.append(" ) ");
		}else if ("region".equals(type)){
			sql.append(" ) GROUP BY REGION_CENTER_ID ,REGION_CENTER_NAME ORDER BY REGION_CENTER_ID ");
		}else if ("area".equals(type)){
			sql.append(" ) GROUP BY BRANCH_AREA_ID ,BRANCH_AREA_NAME ORDER BY BRANCH_AREA_ID");
		}

		condition.setQueryString(sql.toString());
		List<Map<String, Object>> resultList = dam.exeQuery(condition);

		return resultList;
	}

	/*** 核實報表明細 ***/
	public void getReportDetail(Object body, IPrimitiveMap header) throws JBranchException {
		IOT170InputVO inputVO = (IOT170InputVO) body;
		IOT170OutputVO outputVO = new IOT170OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");

		sql.append(" SELECT  DISTINCT ACP.YEARMON,TXN.KEYIN_DATE,TXN.INS_ID,TXN.APPLY_DATE,TXN.CUST_ID, TXN.INSURED_ID,TXN.PRD_NAME, ");
		sql.append("         FBK.PMS_STATUS, ");
//		sql.append("		 CASE WHEN PMS_STATUS IS NULL THEN COALESCE(COM.COMIS, CLO.COMIS,0)");
		sql.append("		 CASE WHEN NVL(PMS_STATUS,'A') = 'A' THEN COALESCE(COM.COMIS, CLO.COMIS,0)");
		sql.append("			  ELSE TXN.ANNU_ACT_FEE END AS ANNU_ACT_FEE,");
		sql.append("         MEM.EMP_ID,MEM.EMP_NAME,TXN.AO_CODE,ORG.BRANCH_AREA_ID, ORG.BRANCH_AREA_NAME,ORG.BRANCH_NBR, ORG.BRANCH_NAME ");
		sql.append(" FROM TBPMS_INS_TXN TXN ");
		sql.append(" LEFT JOIN TBPMS_ORG_REC_N ORG ON ORG.BRANCH_NBR = TXN.BRANCH_NBR  AND ORG.ORG_TYPE = '50' AND TXN.TX_DATE BETWEEN TO_CHAR(ORG.START_TIME,'YYYYMMDD') AND TO_CHAR(ORG.END_TIME,'YYYYMMDD')");  //歷史組織檔
		sql.append(" LEFT JOIN (SELECT CESS_NO, SUM(NVL(COMIS, 0)) AS COMIS FROM TBPMS_DAY_CLOSE GROUP BY CESS_NO) CLO ON CLO.CESS_NO = TXN.INS_ID ");  //日佣檔
		sql.append(" LEFT JOIN (SELECT CESS_NO, SUM(NVL(COMIS, 0)) AS COMIS FROM TBPMS_MON_COMIS GROUP BY CESS_NO) COM ON COM.CESS_NO = TXN.INS_ID ");  //核實佣檔
		sql.append(" LEFT JOIN TBIOT_MAIN IOT ON IOT.INS_ID = TXN.INS_ID ");  //保險進件主檔
		sql.append(" LEFT JOIN TBIOT_FEEDBACK FBK ON FBK.INS_KEYNO = IOT.INS_KEYNO ");  //保險進件人壽回饋檔
		sql.append(" LEFT JOIN TBPMS_INS_ACCEPT_TIME ACP ON TXN.TX_DATE BETWEEN TO_CHAR(ACP.INS_PROFIT_S,'YYYYMMDD') AND TO_CHAR(ACP.INS_PROFIT_E,'YYYYMMDD') ");
		sql.append(" LEFT JOIN TBORG_SALES_AOCODE AO ON TXN.AO_CODE = AO.AO_CODE ");
		sql.append(" LEFT JOIN TBORG_MEMBER MEM ON MEM.EMP_ID = AO.EMP_ID ");
		sql.append("     WHERE 1=1 ");
		sql.append(" 	   AND (FBK.PMS_STATUS IS NOT NULL OR COALESCE(COM.COMIS, CLO.COMIS,0) <> 0)");

		if(!"total".equals(inputVO.getDetailType())){
			if("b".equals(inputVO.getDetailType())){
				sql.append(" AND FBK.PMS_STATUS = 'B' ");
			}else if ("d".equals(inputVO.getDetailType())){
				sql.append(" AND FBK.PMS_STATUS IN ( 'D','E') ");
			}else if ("c".equals(inputVO.getDetailType())){
				sql.append(" AND FBK.PMS_STATUS = 'C' ");
			}else if ("f".equals(inputVO.getDetailType())){
				sql.append(" AND FBK.PMS_STATUS IN ( 'F','G','H','I' ) ");
			}else if ("j".equals(inputVO.getDetailType())){
				sql.append(" AND FBK.PMS_STATUS = 'J' ");
			}else if ("com".equals(inputVO.getDetailType())){
				sql.append(" AND (FBK.PMS_STATUS IS NULL OR (FBK.PMS_STATUS = 'A' AND COALESCE(COM.COMIS, CLO.COMIS,0) <> 0))");
			}
		}else{
			sql.append(" AND NOT (FBK.PMS_STATUS = 'A' AND COALESCE(COM.COMIS, CLO.COMIS,0) = 0) ");
		}

		if(StringUtils.isNotBlank(inputVO.getBranch_area_id())){
			sql.append(" AND ORG.BRANCH_AREA_ID = :branchArea ");
			condition.setObject("branchArea",inputVO.getBranch_area_id());
		}
		if(StringUtils.isNotBlank(inputVO.getBranch_nbr())){
			sql.append(" AND ORG.BRANCH_NBR = :branchNbr ");
			condition.setObject("branchNbr",inputVO.getBranch_nbr());
		}
		if(StringUtils.isNotBlank(inputVO.getEmp_id())){
			sql.append(" AND MEM.EMP_ID = :empid ");
			condition.setObject("empid",inputVO.getEmp_id());
		}
		if(StringUtils.isNotBlank(inputVO.getCls())){
			sql.append(" AND NVL(ORG.BRANCH_CLS,0) = :cls ");
			condition.setObject("cls",inputVO.getCls());
		}
		//保險工作年月
		if(StringUtils.isNotBlank(inputVO.getWorkYear()) || StringUtils.isNotBlank(inputVO.getWorkMonth()) || inputVO.getsCloseDate() != null || inputVO.geteCloseDate() != null){
			sql.append(" AND TXN.INS_ID IN (SELECT CESS_NO FROM TBPMS_DAY_CLOSE WHERE 1=1 ");

			if(StringUtils.isNotBlank(inputVO.getWorkYear())){
				sql.append(" AND WORK_YY = :WORK_YEAR ");
				condition.setObject("WORK_YEAR", org.apache.commons.lang.ObjectUtils.toString(Integer.parseInt(inputVO.getWorkYear()) - 1911));
			}
			if(StringUtils.isNotBlank(inputVO.getWorkMonth())){
				sql.append(" AND WORK_MM = :WORK_MONTH ");
				condition.setObject("WORK_MONTH",inputVO.getWorkMonth());
			}
			///結案日 (核實佣檔沒有就用日佣檔計算)
			if(inputVO.getsCloseDate() != null){
				sql.append(" AND ( DECODE(END_YY,null,'0', (END_YY + 1911) || DECODE(LENGTH(END_MM),1,'0'||END_MM ,END_MM) || DECODE(LENGTH(END_DD),1,'0'||END_DD,END_DD)) ) >= :sCDATE ");
				condition.setObject("sCDATE",sdf1.format(inputVO.getsCloseDate()));
			}
			if(inputVO.geteCloseDate() != null){
				sql.append(" AND ( DECODE(END_YY,null,'0', (END_YY + 1911) || DECODE(LENGTH(END_MM),1,'0'||END_MM ,END_MM) || DECODE(LENGTH(END_DD),1,'0'||END_DD,END_DD)) ) <= :eCDATE ");
				condition.setObject("eCDATE",sdf1.format(inputVO.geteCloseDate()));
			}

			sql.append(" UNION ");
			sql.append(" SELECT CESS_NO FROM TBPMS_MON_COMIS WHERE 1=1 ");

			if(StringUtils.isNotBlank(inputVO.getWorkYear())){
				sql.append(" AND SUBSTR(WORK_YY, 2, 3) = :WORK_YEAR ");
			}
			if(StringUtils.isNotBlank(inputVO.getWorkMonth())){
				sql.append(" AND WORK_MM = :WORK_MONTH ");
			}
			//結案日 (核實佣檔沒有就用日佣檔計算)
			if(inputVO.getsCloseDate() != null){
				sql.append(" AND ( DECODE(END_YY,null,'0', (END_YY + 1911) || DECODE(LENGTH(END_MM),1,'0'||END_MM ,END_MM) || DECODE(LENGTH(END_DD),1,'0'||END_DD,END_DD)) ) >= :sCDATE ");
			}
			if(inputVO.geteCloseDate() != null){
				sql.append(" AND ( DECODE(END_YY,null,'0', (END_YY + 1911) || DECODE(LENGTH(END_MM),1,'0'||END_MM ,END_MM) || DECODE(LENGTH(END_DD),1,'0'||END_DD,END_DD)) ) <= :eCDATE ");
			}

			sql.append(" ) ");
		}

		//保險文件編號
		if(StringUtils.isNotBlank(inputVO.getInsID())){
			sql.append(" AND TXN.INS_ID = :INS_ID ");
			condition.setObject("INS_ID",inputVO.getInsID());
		}
		//險種代號
		if(StringUtils.isNotBlank(inputVO.getInsPrdID())){
			sql.append("AND TXN.PRD_ID = :INSPRD_ID ");
			condition.setObject("INSPRD_ID",inputVO.getInsPrdID());
		}
		//要保人ID
		if(StringUtils.isNotBlank(inputVO.getCustID())){
			sql.append(" AND TXN.CUST_ID = :CUST_ID ");
			condition.setObject("CUST_ID",inputVO.getCustID());
		}
		//被保人ID
		if(StringUtils.isNotBlank(inputVO.getInsuredID())){
			sql.append(" AND TXN.INSURED_ID = :INSURED_ID ");
			condition.setObject("INSURED_ID",inputVO.getInsuredID());
		}
		//招攬人員編
		if(StringUtils.isNotBlank(inputVO.getRecruitID())){
			sql.append(" AND TXN.RECRUIT_ID = :RECRUIT_ID ");
			condition.setObject("RECRUIT_ID",inputVO.getRecruitID());
		}
		//人壽受理進度(保單發單狀態)
		if(StringUtils.isNotBlank(inputVO.getPolicyStatus())){
			sql.append("AND FBK.INS_STATUS = :POLICYSTAT ");
			condition.setObject("POLICYSTAT",inputVO.getPolicyStatus());
		}
		//保單號碼
		if(StringUtils.isNotBlank(inputVO.getPolicyNo1())){
			sql.append(" AND IOT.POLICY_NO1 = :POLICY_NO1 ");
			condition.setObject("POLICY_NO1",inputVO.getPolicyNo1());
		}
		if(StringUtils.isNotBlank(inputVO.getPolicyNo2())){
			sql.append(" AND IOT.POLICY_NO2 = :POLICY_NO2 ");
			condition.setObject("POLICY_NO2",inputVO.getPolicyNo2());
		}
		if(StringUtils.isNotBlank(inputVO.getPolicyNo3())){
			sql.append(" AND IOT.POLICY_NO3 = :POLICY_NO3 ");
			condition.setObject("POLICY_NO3",inputVO.getPolicyNo3());
		}
		//計績工作年月
		if(StringUtils.isNotBlank(inputVO.getsWorkDate())){
			sql.append(" AND ACP.YEARMON >= TO_CHAR(ADD_MONTHS(SYSDATE, :sPDATE),'YYYYMM') ");
			condition.setObject("sPDATE",Integer.parseInt(inputVO.getsWorkDate()) * (-1) );
		}
		if(StringUtils.isNotBlank(inputVO.geteWorkDate())){
			sql.append(" AND ACP.YEARMON <= TO_CHAR(ADD_MONTHS(SYSDATE, :ePDATE ),'YYYYMM') ");
			condition.setObject("ePDATE",Integer.parseInt(inputVO.geteWorkDate()) * (-1) );
		}
		//鍵機日起日
		if(inputVO.getsCreDate() != null){
			sql.append(" AND TRUNC(TXN.KEYIN_DATE) >= TRUNC(TO_DATE( :sDATE, 'YYYY-MM-DD')) ");
			condition.setObject("sDATE",sdf.format(inputVO.getsCreDate()));
		}
		//鍵機日迄日
		if(inputVO.geteCreDate() != null){
			sql.append(" AND TRUNC(TXN.KEYIN_DATE) <= TRUNC(TO_DATE( :eDATE, 'YYYY-MM-DD')) ");
			condition.setObject("eDATE",sdf.format(inputVO.geteCreDate()));
		}
		//要保書填寫日期
		if(inputVO.getsApplyDate() != null){
			sql.append(" AND TRUNC(TXN.APPLY_DATE) >= TRUNC(TO_DATE( :sAPPDATE, 'YYYY-MM-DD')) ");
			condition.setObject("sAPPDATE",sdf.format(inputVO.getsApplyDate()));
		}
		if(inputVO.geteApplyDate() != null){
			sql.append(" AND TRUNC(TXN.APPLY_DATE) <= TRUNC(TO_DATE( :eAPPDATE, 'YYYY-MM-DD')) ");
			condition.setObject("eAPPDATE",sdf.format(inputVO.geteApplyDate()));
		}

		sql.append(" ORDER BY TXN.KEYIN_DATE,TXN.APPLY_DATE,FBK.PMS_STATUS");
		condition.setQueryString(sql.toString());
		List<Map<String, Object>> resultList = dam.exeQuery(condition);

		outputVO.setReportDetail(resultList);
		sendRtnObject(outputVO);
	}
	/*** 投資型連結標的明細 ***/
	public void getInvTarget(Object body, IPrimitiveMap header) throws JBranchException {
		IOT170InputVO inputVO = (IOT170InputVO) body;
		IOT170OutputVO outputVO = new IOT170OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		ArrayList<String> sql_list = new ArrayList<String>();
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT * FROM VWIOT_FUND_LINK ");
		sql.append("WHERE INS_KEYNO = :insKeyno OR PREMATCH_SEQ = :prematchSeq ");
		condition.setObject("insKeyno", inputVO.getIns_keyno());
		condition.setObject("prematchSeq", inputVO.getPrematchSeq());
		condition.setQueryString(sql.toString());
		
		outputVO.setInvTargetList(dam.exeQuery(condition));
		
		sendRtnObject(outputVO);
	}

	/*** 附約明細 ***/
	public void getRiderDetail(Object body, IPrimitiveMap header) throws JBranchException {
		IOT170InputVO inputVO = (IOT170InputVO) body;
		IOT170OutputVO outputVO = new IOT170OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition();
		ArrayList<String> sql_list = new ArrayList<String>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT INSPRD_ID, INSURED_NAME, ");
		sql.append("RLT_WITH_INSURED, INSPRD_ANNUAL, PREMIUM ");
		sql.append("FROM TBIOT_RIDER_DTL RIDER, TBPRD_INS_MAIN PRD ");
		sql.append("WHERE RIDER.INSPRD_KEYNO = PRD.INSPRD_KEYNO ");
		sql.append("AND RIDER.INS_KEYNO = ? ");
		sql_list.add(inputVO.getIns_keyno()+"");

		condition.setQueryString(sql.toString());
		for (int sql_i = 0; sql_i < sql_list.size(); sql_i++) {
			condition.setString(sql_i + 1, sql_list.get(sql_i));
		}
//		ResultIF list = dam.executePaging(condition,
//				inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());

//		outputVO.setTotalPage(list.getTotalPage());
		outputVO.setRiderDtlList(dam.exeQuery(condition));
//		outputVO.setTotalRecord(list.getTotalRecord());
//		outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
		sendRtnObject(outputVO);
	}

	/** 取工作日 **/
	public void getBusDate(Object body, IPrimitiveMap header) throws JBranchException {
		IOT170OutputVO outputVO = new IOT170OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition();
		StringBuffer sql = new StringBuffer("select TRUNC(PABTH_UTIL.FC_GETBUSIDAY(SYSDATE, 'TWD', -5)) as BUS_DATE from DUAL ");

		condition.setQueryString(sql.toString());
		outputVO.setBusDate(dam.exeQuery(condition));

		sendRtnObject(outputVO);
	}

	/*  === 產出csv==== */
	public void export(Object body, IPrimitiveMap header) throws JBranchException, IOException {
		IOT170OutputVO outputVO = (IOT170OutputVO) body;

		if("4".equals(outputVO.getReportType())){
			exportReport(outputVO);
			this.sendRtnObject(null);
			return;
		}

		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		list = outputVO.getResultList();

		String rptTitle = "";
		String rptType = outputVO.getReportType();
		switch(outputVO.getReportType()){
		case "1" :
			rptTitle = "鍵機資訊";
			list = outputVO.getExport1ResultList();
			break;
		case "2" :
			rptTitle = "品質檢核";
			break;
		case "3" :
			rptTitle = "受理查詢";
			break;
		}

		boolean flag = false;
		int col = 0;
		XmlInfo xmlInfo = new XmlInfo();

		Map<String, String> send_type = xmlInfo.doGetVariable("IOT.SEND_TYPE", FormatHelper.FORMAT_3);
		Map<String, String> first_pay_way = xmlInfo.doGetVariable("IOT.FIRST_PAY_WAY", FormatHelper.FORMAT_3);
		Map<String, String> write_reason = xmlInfo.doGetVariable("IOT.WRITE_REASON", FormatHelper.FORMAT_3);
		Map<String, String> reg_type12 = xmlInfo.doGetVariable("IOT.REG_TYPE12", FormatHelper.FORMAT_3);
		Map<String, String> pay_type = xmlInfo.doGetVariable("IOT.PAY_TYPE", FormatHelper.FORMAT_3);
		Map<String, String> product_type = xmlInfo.doGetVariable("IOT.PRODUCT_TYPE", FormatHelper.FORMAT_3);
		Map<String, String> main_status = xmlInfo.doGetVariable("IOT.MAIN_STATUS", FormatHelper.FORMAT_3);
		Map<String, String> pay_way = xmlInfo.doGetVariable("CRM.PAY_WAY", FormatHelper.FORMAT_3);
		Map<String, String> contract_status = xmlInfo.doGetVariable("CRM.CRM239_CONTRACT_STATUS", FormatHelper.FORMAT_3);
		Map<String, String> pay_yqd = xmlInfo.doGetVariable("PMS.PAY_YQD", FormatHelper.FORMAT_3);
		Map<String, String> cmFlag = xmlInfo.doGetVariable("IOT.CM_FLAG", FormatHelper.FORMAT_3);
		Map<String, String> payerRelProposer = xmlInfo.doGetVariable("IOT.PAYER_REL_PROPOSER", FormatHelper.FORMAT_3);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String fileName = "壽險新契約送件查詢_總行保險商品PM_"+rptTitle+"_"+ sdf.format(new Date()) + ".csv";
		List listCSV =  new ArrayList();
		String[] records = null;
		for(Map<String, Object> map : list){
			//鍵機資訊
			if("1".equals(rptType)){
				records = new String[48];
				int i = 0;
				records[i]   = checkIsNull(map, "KEYIN_DATE");
				records[++i] = checkIsNull(map, "APPLY_DATE");
				records[++i] = checkIsNull(map, "OP_BATCH_NO");
				records[++i] = '"' + checkIsNull(map, "INS_ID") + '"'; // 保險文件編號
				records[++i] = checkIsNull(map, "POLICY_NO");
				records[++i] = '"' + checkIsNull(map, "BRANCH_NBR") + '"'; // 分行代碼
				records[++i] = checkIsNull(map, "BRANCH_NAME");
				records[++i] = '"' + checkIsNull(map, "RECRUIT_ID") + '"'; // 招攬人員原編
				if(checkIsNull(map, "M_CUST_ID").length() == 10)
					records[++i] = chang_Name(checkIsNull(map, "M_CUST_ID"), 1);
				else
					records[++i] = chang_Name(checkIsNull(map, "M_CUST_ID"), 2);
				records[++i] = checkIsNull(map, "M_EMP_NAME");
				records[++i] = '"' + checkIsNull(map, "AO_CODE") + '"';
				records[++i] = chang_Name(checkIsNull(map, "INSURED_NAME"), 2);
				records[++i] = checkIsNull(map, "INSURED_ID");
				records[++i] = chang_Name(checkIsNull(map, "PROPOSER_NAME"), 2);
				records[++i] = checkIsNull(map, "CUST_ID");
				records[++i] = checkIsNull(map, "INS_SOURCE");
				records[++i] = checkIsNull(map, "INS_COM_NAME");
				records[++i] = checkIsNull(map, "INSPRD_ID");
				records[++i] = checkIsNull(map, "INSPRD_NAME");
				records[++i] = checkIsNull(map, "IOT_PRD_RISK");
//				records[++i] = checkIsNull(map, "STATUS");
				if(!checkIsNull(map, "STATUS").equals("")){
					records[++i] = main_status.get(map.get("STATUS"));
				}else{
					records[++i] = "";
				}
//				records[++i] = checkIsNull(map, "INSPRD_TYPE");
				if(!checkIsNull(map, "INSPRD_TYPE").equals("")){
					records[++i] = product_type.get(map.get("INSPRD_TYPE"));
				}else{
					records[++i] = "";
				}
//				records[++i] = checkIsNull(map, "FIRST_PAY_WAY");		  //首期繳費方式
				if(!checkIsNull(map, "FIRST_PAY_WAY").equals("")){
					records[++i] = first_pay_way.get(map.get("FIRST_PAY_WAY"));
				}else{ records[++i] = ""; }

				records[++i] = checkIsNull(map, "INSPRD_ANNUAL");
//				records[++i] = checkIsNull(map, "PAY_TYPE");
				if(!checkIsNull(map, "PAY_TYPE").equals("")){
					records[++i] = pay_type.get(map.get("PAY_TYPE"));
				}else{ records[++i] = "";}

//				records[++i] = checkIsNull(map, "MOP2");
				if(!checkIsNull(map, "MOP2").equals("")){
					records[++i] = pay_yqd.get(map.get("MOP2"));
				}else{ records[++i] = ""; }

				records[++i] = checkIsNull(map, "SPECIAL_CONDITION");
				records[++i] = checkIsNull(map, "CURR_CD");
				records[++i] = checkIsNull(map, "EXCH_RATE");
				records[++i] = currencyFormat(map, "REAL_PREMIUM");
				records[++i] = currencyFormat(map, "BASE_PREMIUM");
				records[++i] = checkIsNull(map, "MATCH_DATE");
				records[++i] = checkIsNull(map, "REF_CON_ID");
				records[++i] = checkIsNull(map, "SALES_PERSON")==""?"":'"' + checkIsNull(map, "SALES_PERSON") + '"'; // 轉介人員原編
				records[++i] = checkIsNull(map, "SALES_NAME");
//				records[++i] = checkIsNull(map, "REG_TYPE");
				if(!checkIsNull(map, "REG_TYPE").equals("")){
					records[++i] = reg_type12.get(map.get("REG_TYPE"));
				}else{ records[++i] = ""; }

//				records[++i] = checkIsNull(map, "WRITE_REASON");
				if(!checkIsNull(map, "WRITE_REASON").equals("")){
					records[++i] = write_reason.get(map.get("WRITE_REASON"));
				}else{ records[++i] = ""; }

				records[++i] = checkIsNull(map, "WRITE_REASON_OTH");
				records[++i] = checkIsNull(map, "QC_ANC_DOC");
				records[++i] = checkIsNull(map, "INS_RCV_DATE");					//人壽簽收時間
				records[++i] = checkIsNull(map, "INS_RCV_OPRID");					//人壽簽收人
				records[++i] = checkIsNull(map, "REMARK_BANK");					    //備註
				records[++i] = checkIsNull(map, "DELETE_DATE");
				records[++i] = checkIsNull(map, "DELETE_OPRID");
				records[++i] = checkIsNull(map, "TARGET_ID");
				records[++i] = checkIsNull(map, "LINKED_NAME");
				records[++i] = checkIsNull(map, "PRD_RISK");
				records[++i] = checkIsNull(map, "LINK_PCT");
				if(!"".equals(checkIsNull(map, "F_INS_KEYNO"))){
					flag = true;
					col = 4;
				}
				//判斷投資型非投資型
				switch (String.valueOf(map.get("INSPRD_TYPE"))) {
				case "1"://非投資型
					listCSV.add(records);
					break;
				default://投資型 & 判斷比例不為空白的才產生再匯出檔
					if(!"".equals(checkIsNull(map, "LINK_PCT"))){
						listCSV.add(records);
					}
					break;
				}
			}
			//品質檢核
			if("2".equals(rptType)){
				records = new String[70];
				int i = 0;
				records[i]   = checkIsNull(map, "KEYIN_DATE");
				records[++i] = '"' + checkIsNull(map, "INS_ID") + '"'; // 保險文件編號
				records[++i] = '"' + checkIsNull(map, "BRANCH_NBR") + '"'; // 分行代碼
				records[++i] = checkIsNull(map, "BRANCH_NAME");
				records[++i] = chang_Name(checkIsNull(map, "INSURED_NAME"), 2);
				records[++i] = checkIsNull(map, "INSURED_ID");
				records[++i] = checkIsNull(map, "INSURED_BIRTH");
				records[++i] = chang_Name(checkIsNull(map, "PROPOSER_NAME"), 2);
				records[++i] = checkIsNull(map, "CUST_ID");
				records[++i] = checkIsNull(map, "PROPOSER_BIRTH");
				records[++i] = checkIsNull(map, "CREATOR");
				records[++i] = checkIsNull(map, "OP_DATE");
				records[++i] = checkIsNull(map, "QC_ERASER");
				records[++i] = checkIsNull(map, "QC_STAMP");
				records[++i] = checkIsNull(map, "QC_IMMI");
				records[++i] = checkIsNull(map, "QC_APEC");
				records[++i] = checkIsNull(map, "QC_LOAN_CHK");
				records[++i] = checkIsNull(map, "QC_SIGNATURE");
				records[++i] = checkIsNull(map, "LOAN_SOURCE_YN");
				records[++i] = checkIsNull(map, "INSURED_TRANSSEQ");
				records[++i] = checkIsNull(map, "PROPOSER_TRANSSEQ");
				records[++i] = checkIsNull(map, "AB_TRANSSEQ");
				records[++i] = checkIsNull(map, "PREMIUM_TRANSSEQ");
				records[++i] = checkIsNull(map, "I_PREMIUM_TRANSSEQ");
				records[++i] = checkIsNull(map, "P_PREMIUM_TRANSSEQ");
				records[++i] = checkIsNull(map, "C_SALE_SENIOR_TRANSSEQ");
				records[++i] = checkIsNull(map, "I_SALE_SENIOR_TRANSSEQ");
				records[++i] = checkIsNull(map, "P_SALE_SENIOR_TRANSSEQ");
				records[++i] = checkIsNull(map, "SENIOR_AUTH_REMARKS");
				records[++i] = checkIsNull(map, "SENIOR_AUTH_ID");
				if(!checkIsNull(map, "PROPOSER_CM_FLAG").equals("")) {
					records[++i] = cmFlag.get(map.get("PROPOSER_CM_FLAG"));
				} else{ records[++i] = ""; }
				records[++i] = checkIsNull(map, "CUST_RISK");
				records[++i] = checkIsNull(map, "CUST_RISK_DUE");
				records[++i] = checkIsNull(map, "AML");
				records[++i] = checkIsNull(map, "PRECHECK");
				records[++i] = checkIsNull(map, "PROPOSER_INCOME1");
				records[++i] = checkIsNull(map, "C_KYC_INCOME");
				records[++i] = checkIsNull(map, "PROPOSER_INCOME3");
				records[++i] = checkIsNull(map, "INSURED_INCOME1");
				records[++i] = checkIsNull(map, "I_KYC_INCOME");
				records[++i] = checkIsNull(map, "INSURED_INCOME3");
				records[++i] = checkIsNull(map, "PAYER_ID");
				records[++i] = checkIsNull(map, "PAYER_BIRTH");
//				records[++i] = checkIsNull(map, "RLT_BT_PROPAY");
				if(!checkIsNull(map, "RLT_BT_PROPAY").equals("")) {
					records[++i] = payerRelProposer.get(map.get("RLT_BT_PROPAY"));
				} else{ records[++i] = ""; }
				records[++i] = checkIsNull(map, "LOAN_CHK1_YN");
				records[++i] = checkIsNull(map, "LOAN_CHK2_YN");
				records[++i] = checkIsNull(map, "CD_CHK_YN");
				records[++i] = checkIsNull(map, "INCOME_REMARK");
				records[++i] = checkIsNull(map, "LOAN_SOURCE_REMARK");
				records[++i] = checkIsNull(map, "SIGNOFF_DATE");

				records[++i] = checkIsNull(map, "LOAN_SOURCE2_YN");
				records[++i] = StringUtils.equals("1", checkIsNull(map, "CONTRACT_END_YN")) ? "Y" : "N";
				records[++i] = StringUtils.equals("1", checkIsNull(map, "S_INFITEM_LOAN_YN")) ? "Y" : "N";
				records[++i] = checkIsNull(map, "C_LOAN_CHK1_YN");
				records[++i] = checkIsNull(map, "C_LOAN_CHK2_YN");
				records[++i] = checkIsNull(map, "C_CD_CHK_YN");
				records[++i] = checkIsNull(map, "C_LOAN_CHK3_YN");
				records[++i] = checkIsNull(map, "I_LOAN_CHK1_YN");
				records[++i] = checkIsNull(map, "I_LOAN_CHK2_YN");
				records[++i] = checkIsNull(map, "I_CD_CHK_YN");
				records[++i] = checkIsNull(map, "I_LOAN_CHK3_YN");
				records[++i] = checkIsNull(map, "LOAN_CHK3_YN");
				records[++i] = checkIsNull(map, "C_LOAN_APPLY_DATE");
				records[++i] = checkIsNull(map, "I_LOAN_APPLY_DATE");
				records[++i] = checkIsNull(map, "P_LOAN_APPLY_DATE");
				records[++i] = checkIsNull(map, "C_PREM_DATE");
				records[++i] = checkIsNull(map, "PREMATCH_SEQ");
				records[++i] = checkIsNull(map, "CASE_ID");
				records[++i] = checkIsNull(map, "CANCEL_CONTRACT_YN");
				records[++i] = checkIsNull(map, "RECORD_YN");

				listCSV.add(records);
			}
			//受理查詢
			if("3".equals(rptType)){
				records = new String[48];
				int i = 0;
				records[i]   = '"' + checkIsNull(map, "INS_ID") + '"'; // 保險文件編號
//				records[++i] = checkIsNull(map, "POLICY_STATUS");
				if(!checkIsNull(map, "POLICY_STATUS").equals("")){
					records[++i] = contract_status.get(map.get("POLICY_STATUS"));
				}else{
					records[++i] = "";
				}
				records[++i] = chang_Name(checkIsNull(map, "INSURED_NAME"), 2);
				records[++i] = checkIsNull(map, "INSURED_ID");
				records[++i] = chang_Name(checkIsNull(map, "PROPOSER_NAME"), 2);
				records[++i] = checkIsNull(map, "CUST_ID");
				records[++i] = checkIsNull(map, "CREATETIME");
				records[++i] = checkIsNull(map, "APPLY_DATE");
				records[++i] = checkIsNull(map, "POLICY_NO");
//				records[++i] = checkIsNull(map, "INS_NO");
				if(map.get("INS_NO").equals("00")){
					records[++i] = "主約";
				}else{ records[++i] = "附約" ;}

				records[++i] = checkIsNull(map, "INS_ITEM");
				records[++i] = checkIsNull(map, "PREM_YEAR_STR");
				records[++i] = checkIsNull(map, "INS_YEAR_STR");
//				records[++i] = checkIsNull(map, "PAY_TYPE");
				if(!checkIsNull(map, "PAY_TYPE").equals("")){
					records[++i] = pay_yqd.get(map.get("PAY_TYPE"));
				}else{ records[++i] = "";}

				records[++i] = checkIsNull(map, "EMPY_YN");
				records[++i] = checkIsNull(map, "DEPT_CODE");
				records[++i] = checkIsNull(map, "NB_NOTE_DT");
				records[++i] = checkIsNull(map, "NB_CONTENT");
				records[++i] = checkIsNull(map, "RN_NOTE_DT");
				records[++i] = checkIsNull(map, "RM_CONTENT");
				records[++i] = checkIsNull(map, "INSPECT_DATE");
				records[++i] = currencyFormat(map, "PREM_PAYABLE");
				records[++i] = checkIsNull(map, "APPL_DATE");
//				records[++i] = checkIsNull(map, "DOWN_PAY_TYPE");
				if(!checkIsNull(map, "DOWN_PAY_TYPE").equals("")){
					records[++i] = pay_way.get(map.get("DOWN_PAY_TYPE"));
				}else{ records[++i] = "";}

				records[++i] = checkIsNull(map, "SEND_DATE");
//				records[++i] = checkIsNull(map, "SEND_TYPE");
				if(!checkIsNull(map, "SEND_TYPE").equals("")){
					records[++i] = send_type.get(map.get("SEND_TYPE"));
				}else{ records[++i] = "";}

				records[++i] = checkIsNull(map, "SIGN_DATE");
				records[++i] = checkIsNull(map, "REGISTER_NO");
				records[++i] = checkIsNull(map, "POLI_YEAR");
				records[++i] = checkIsNull(map, "POLI_PERD");
				records[++i] = checkIsNull(map, "POLICY_CUR");
				records[++i] = checkIsNull(map, "BRANCH_NBR");
				records[++i] = checkIsNull(map, "ITEM_REMRK");
				records[++i] = checkIsNull(map, "COMU_RATE");
				records[++i] = checkIsNull(map, "POLICY_EXCH");
				records[++i] = checkIsNull(map, "POLICY_EXCH_DATE");
				records[++i] = checkIsNull(map, "GET_YYMMDD");
				records[++i] = checkIsNull(map, "COMU");
				records[++i] = currencyFormat(map, "PREM");
				records[++i] = currencyFormat(map, "COMIS");
				records[++i] = currencyFormat(map, "ANNU_ACT_FEE");
				records[++i] = checkIsNull(map, "DECL_ECHG");
				records[++i] = checkIsNull(map, "REAL_COMU_SATE");
				records[++i] = currencyFormat(map, "REAL_PREM");
				records[++i] = currencyFormat(map, "REAL_COMIS");
				records[++i] = checkIsNull(map, "REAL_DECL_ECHG");
				records[++i] = checkIsNull(map, "RECRUIT_ID");
				records[++i] = checkIsNull(map, "RECRUIT_NAME");
				listCSV.add(records);
			}
		}

		//header
		String [] csvHeader = null;
		//鍵機資訊
		if("1".equals(rptType)){
			csvHeader = new String[44+col];
			int j = 0;
			csvHeader[j]   = "鍵機日";
			csvHeader[++j] = "要保書填寫申請日";
			csvHeader[++j] = "送件批號";
			csvHeader[++j] = "保險文件編號";
			csvHeader[++j] = "保單號碼";
			csvHeader[++j] = "分行代碼";
			csvHeader[++j] = "分行名稱";
			csvHeader[++j] = "招攬人員編";
			csvHeader[++j] = "招攬人ID";
			csvHeader[++j] = "招攬人姓名";
			csvHeader[++j] = "AO CODE";
			csvHeader[++j] = "被保險人姓名";
			csvHeader[++j] = "被保險人ID";
			csvHeader[++j] = "要保人姓名";
			csvHeader[++j] = "要保人ID";
			csvHeader[++j] = "進件來源";
			csvHeader[++j] = "保險公司";
			csvHeader[++j] = "主約險種代碼";
			csvHeader[++j] = "主約險種名稱";
			csvHeader[++j] = "商品風險值";
			csvHeader[++j] = "文件簽收狀態";
			csvHeader[++j] = "保險產品類型";
			csvHeader[++j] = "首期繳費方式";
			csvHeader[++j] = "繳費年期";
			csvHeader[++j] = "躉繳/分期繳";
			csvHeader[++j] = "分期繳別";
			csvHeader[++j] = "特殊條件";
			csvHeader[++j] = "幣別";
			csvHeader[++j] = "參考匯率";
			csvHeader[++j] = "實收保費(原幣)";
			csvHeader[++j] = "基本保費(原幣)";
			csvHeader[++j] = "適配日期";
			csvHeader[++j] = "轉介編號";
			csvHeader[++j] = "轉介人員編";
			csvHeader[++j] = "轉介人姓名";
			csvHeader[++j] = "要保書類型";
			csvHeader[++j] = "使用手寫要保書原因";
			csvHeader[++j] = "原因描述";
			csvHeader[++j] = "要保文件內是否有檢附保險通報單";
			csvHeader[++j] = "人壽簽收時間";
			csvHeader[++j] = "人壽簽收人";
			csvHeader[++j] = "備註";
			csvHeader[++j] = "刪除時間";
			csvHeader[++j] = "刪除人";
			if(flag){
//				csvHeader[++j] = "壽險主檔主鍵";
//				csvHeader[++j] = "基金產品主鍵";
				csvHeader[++j] = "人壽基金標的代號";
				csvHeader[++j] = "基金標的名稱";
				csvHeader[++j] = "銀行風險等級";
				csvHeader[++j] = "配置比例";
			}
		}else if("2".equals(rptType)){             //品質檢核
				csvHeader = new String[70];
				int j = 0;
				csvHeader[j]   = "鍵機日";
				csvHeader[++j] = "保險文件編號";
				csvHeader[++j] = "分行代碼";
				csvHeader[++j] = "分行名稱";
				csvHeader[++j] = "被保險人姓名";
				csvHeader[++j] = "被保險人ID";
				csvHeader[++j] = "被保險人生日";
				csvHeader[++j] = "要保人姓名";
				csvHeader[++j] = "要保人ID";
				csvHeader[++j] = "要保人生日";
				csvHeader[++j] = "檢核人(登錄人員)";
				csvHeader[++j] = "檢核時間";
				csvHeader[++j] = "是否以可擦拭文具書寫";
				csvHeader[++j] = "驗印是否符合";
				csvHeader[++j] = "是否有檢附立即投入批註書";
				csvHeader[++j] = "檢視電子要保書地址、電話、EMAIL、手機與套印要保書是否相符 1.相符 2.不相符，塗改處客戶有簽名 3.不相符，塗改處客戶沒簽名";
				csvHeader[++j] = "檢視業報書前3個月貸款/保單借款、業報書前3個月保單解約之勾選與要保人保險購買檢核報表相符 1.相符 2.不相符";
				csvHeader[++j] = "檢核要保書客戶與業務員之簽名欄位均有簽名 1.有簽名 2.有遺漏";
				csvHeader[++j] = "保費來源是否為貸款";
				csvHeader[++j] = "被保人錄音序號";
				csvHeader[++j] = "要保人錄音序號";
				csvHeader[++j] = "非常態錄音序號";
				csvHeader[++j] = "要保人高齡/保費來源錄音序號";
				csvHeader[++j] = "被保人高齡/保費來源錄音序號";
				csvHeader[++j] = "繳款人高齡/保費來源錄音序號";
				csvHeader[++j] = "要保人銷售過程錄音序號";
				csvHeader[++j] = "被保人銷售過程錄音序號";
				csvHeader[++j] = "繳款人銷售過程錄音序號";
				csvHeader[++j] = "高齡客戶投保關懷說明";
				csvHeader[++j] = "關懷主管員編";
				csvHeader[++j] = "要保人戶況";
				csvHeader[++j] = "要保人KYC等級";
				csvHeader[++j] = "要保人KYC效期";
				csvHeader[++j] = "要保人AML風險等級";
				csvHeader[++j] = "要保人Pre-check結果";
				csvHeader[++j] = "要保人工作年收入(業報)";
				csvHeader[++j] = "要保人行內KYC收入";
				csvHeader[++j] = "要保人工作年收入(授信)";
				csvHeader[++j] = "被保人工作年收入(業報)";
				csvHeader[++j] = "被保人行內KYC收入";
				csvHeader[++j] = "被保人工作年收入(授信)";
				csvHeader[++j] = "繳款人ID";
				csvHeader[++j] = "繳款人生日";
				csvHeader[++j] = "繳款人與要保人關係";
				csvHeader[++j] = "繳款人保單貸款檢核";
				csvHeader[++j] = "繳款人行內貸款檢核";
				csvHeader[++j] = "繳款人定存不打折檢核";
				csvHeader[++j] = "工作年收入與授信審核表不一致說明";
				csvHeader[++j] = "客戶保費來源說明";
				csvHeader[++j] = "覆核日期";
				csvHeader[++j] = "保費來源為解約";
				csvHeader[++j] = "業報投保前三個月有解約";
				csvHeader[++j] = "業報投保前三個月有貸款";
				csvHeader[++j] = "要保人保單貸款檢核";
				csvHeader[++j] = "要保人行內貸款檢核";
				csvHeader[++j] = "要保人定存不打折檢核";
				csvHeader[++j] = "要保人解約檢核";
				csvHeader[++j] = "被保人保單貸款檢核";
				csvHeader[++j] = "被保人行內貸款檢核";
				csvHeader[++j] = "被保人定存不打折檢核";
				csvHeader[++j] = "被保人解約檢核";
				csvHeader[++j] = "繳款人解約檢核";
				csvHeader[++j] = "要保人行內貸款申請日";
				csvHeader[++j] = "被保人行內貸款申請日";
				csvHeader[++j] = "繳款人行內貸款申請日";
				csvHeader[++j] = "要保人舊保單提領保額/保價日";
				csvHeader[++j] = "適合度檢核編碼";
				csvHeader[++j] = "案件編號";
				csvHeader[++j] = "契撤案件";
				csvHeader[++j] = "保險資金電訪欄位註記";
		}else if("3".equals(rptType)){			//受理查詢
				csvHeader = new String[48];
				int j = 0;
				csvHeader[j]   = "保險文件編號";
				csvHeader[++j] = "保單發單狀態";
				csvHeader[++j] = "被保險人姓名";
				csvHeader[++j] = "被保險人ID";
				csvHeader[++j] = "要保人姓名";
				csvHeader[++j] = "要保人ID";
				csvHeader[++j] = "鍵機日";
				csvHeader[++j] = "要保書填寫申請日";
				csvHeader[++j] = "保單號碼";
				csvHeader[++j] = "主附約別";
				csvHeader[++j] = "險種代號";
				csvHeader[++j] = "繳費年期";
				csvHeader[++j] = "保額";
				csvHeader[++j] = "繳別";
				csvHeader[++j] = "員工件";
				csvHeader[++j] = "專案代號";
				csvHeader[++j] = "核保照會日";
				csvHeader[++j] = "核保照會內容";
				csvHeader[++j] = "保費照會日";
				csvHeader[++j] = "保費照會原因";
				csvHeader[++j] = "核保完成日";
				csvHeader[++j] = "保單應繳保費";
				csvHeader[++j] = "保單生效日";
				csvHeader[++j] = "首期繳費方式";
				csvHeader[++j] = "保單發單日";
				csvHeader[++j] = "保單郵寄方式";
				csvHeader[++j] = "保單簽收日";
				csvHeader[++j] = "保單掛號號碼";
				csvHeader[++j] = "保單年度";
				csvHeader[++j] = "保單期數";
				csvHeader[++j] = "保單幣別";
				csvHeader[++j] = "保單招攬單位";
				csvHeader[++j] = "險種性質";
				csvHeader[++j] = "特殊佣金比率";
				csvHeader[++j] = "宣告匯率";
				csvHeader[++j] = "宣告匯率日期";
				csvHeader[++j] = "佣金產生日期";
				csvHeader[++j] = "預計發佣日";
				csvHeader[++j] = "預計核實保費(臺幣)";
				csvHeader[++j] = "預計核實佣金(臺幣)";
				csvHeader[++j] = "預估收益(臺幣)";
				csvHeader[++j] = "預計核實保費/佣金計算匯率";
				csvHeader[++j] = "實際發佣日";
				csvHeader[++j] = "實際核實保費";
				csvHeader[++j] = "實際核實佣金";
				csvHeader[++j] = "實際核實保費/佣金計算匯率";
				csvHeader[++j] = "招攬人員編";
				csvHeader[++j] = "招攬人姓名";

		}

		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		csv.addRecordList(listCSV);

		String url = csv.generateCSV();
		notifyClientToDownloadFile(url, fileName);

		this.sendRtnObject(null);
	}

	/**
	 * 匯出EXCEL(核實報表)
	 */
	private void exportReport(IOT170OutputVO body) throws JBranchException, FileNotFoundException, IOException {
		IOT170OutputVO outputVO = (IOT170OutputVO) body;

		List<Map<String, Object>> list = outputVO.getResultList();
		List<Map<String, Object>> totalList = outputVO.getTotalList();
		List<Map<String, Object>> centerList = outputVO.getRegionList();
		List<Map<String, Object>> areaList = outputVO.getAreaList();

        List<String> item = new ArrayList<String>();
        item.add("TOTAL_CNT");  // 保險戰報預估收益
        item.add("TOTAL_SUM");
        item.add("COM_CNT");	// 已核實預估佣收
        item.add("COMIS");
        item.add("COM_RATE");
        item.add("B_CNT");		// 核保通過待結案
        item.add("B_SUM");
        item.add("B_RATE");
        item.add("D_CNT");	    // 照會中
        item.add("D_SUM");
        item.add("D_RATE");
        item.add("C_CNT");	    // 核保中
        item.add("C_SUM");
        item.add("C_RATE");
        item.add("F_CNT");	    // 契約/取消/拒保/延期
        item.add("F_SUM");
        item.add("F_RATE");
        item.add("J_CNT");	    // 待確認
        item.add("J_SUM");
        item.add("J_RATE");

	    List<String> headerL = new ArrayList<String>();
        headerL.add("業務處");
        headerL.add("營運區");
        headerL.add("分行");
        headerL.add("分行名稱");
        headerL.add("組別");
        headerL.add("保險戰報預估收益");
        headerL.add("保險戰報預估收益");
        headerL.add("已核實預估佣收");
        headerL.add("已核實預估佣收");
        headerL.add("已核實預估佣收");
        headerL.add("核保通過待結案");
        headerL.add("核保通過待結案");
        headerL.add("核保通過待結案");
        headerL.add("照會中");
        headerL.add("照會中");
        headerL.add("照會中");
        headerL.add("核保中");
        headerL.add("核保中");
        headerL.add("核保中");
        headerL.add("契約/取消/拒保/延期");
        headerL.add("契約/取消/拒保/延期");
        headerL.add("契約/取消/拒保/延期");
        headerL.add("待確認");
        headerL.add("待確認");
        headerL.add("待確認");
        String[] headerLine1 = headerL.toArray(new String[25]);

        List<String> headerLine2 = new ArrayList<String>();
    	headerLine2.add("");
    	headerLine2.add("");
    	headerLine2.add("");
    	headerLine2.add("");
    	headerLine2.add("");
        for (int i = 0; i < 7; i++) {
        	if (i == 0) {
        		headerLine2.add("件數");
        		headerLine2.add("預估收益");
        	} else if( i == 1){
        		headerLine2.add("件數");
        		headerLine2.add("核實收益");
        		headerLine2.add("比例");
        	}else{
        		headerLine2.add("件數");
        		headerLine2.add("預估收益");
        		headerLine2.add("比例");
        	}
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		ConfigAdapterIF config = (ConfigAdapterIF) PlatformContext.getBean("configAdapter");
		String fileName = "壽險新契約送件查詢_總行保險商品PM_核實報表_" + sdf.format(new Date())+ "-" + getUserVariable(FubonSystemVariableConsts.LOGINID) + ".xlsx";

			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("壽險新契約送件查詢_總行保險商品PM_核實報表_"+ sdf.format(new Date()));
			sheet.setDefaultColumnWidth(25);

			XSSFCellStyle style = workbook.createCellStyle();
			XSSFCellStyle STYLE = workbook.createCellStyle();

			style.setAlignment(XSSFCellStyle.ALIGN_CENTER); 				// 水平置中
			style.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER); 		// 垂直置中

			STYLE.setAlignment(XSSFCellStyle.ALIGN_CENTER); 				// 水平置中
			STYLE.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER); 		// 垂直置中
			STYLE.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
			STYLE.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			STYLE.setBorderBottom((short) 1);
			STYLE.setBorderTop((short) 1);
			STYLE.setBorderLeft((short) 1);
			STYLE.setBorderRight((short) 1);
			Integer index = 0; // first row
			Integer startFlag = 0;
			Integer endFlag = 0;
			ArrayList<String> tempList = new ArrayList<String>(); // 比對用

			XSSFRow row = sheet.createRow(index);
			for (int i = 0; i < headerLine1.length; i++) {
				String headerLine = headerLine1[i];
				if (tempList.indexOf(headerLine) < 0) {
					tempList.add(headerLine);
					XSSFCell cell = row.createCell(i);
					cell.setCellStyle(STYLE);
					cell.setCellValue(headerLine1[i]);

					if (endFlag != 0) {
						sheet.addMergedRegion(new CellRangeAddress(0, 0, startFlag, endFlag)); // firstRow, endRow, firstColumn, endColumn
					}
					startFlag = i;
					endFlag = 0;
				} else {
					endFlag = i;
				}
			}
			if (endFlag != 0) { // 最後的CELL若需要合併儲存格，則在這裡做
				sheet.addMergedRegion(new CellRangeAddress(0, 0, startFlag, endFlag)); // firstRow, endRow, firstColumn, endColumn
			}

			index++; // next row
			row = sheet.createRow(index);
			for (int i = 0; i < headerLine2.size(); i++) {
				XSSFCell cell = row.createCell(i);
				cell.setCellStyle(STYLE);
				cell.setCellValue(headerLine2.get(i));

				if ("".equals(headerLine2.get(i))) {
					sheet.addMergedRegion(new CellRangeAddress(0, 1, i, i)); // firstRow, endRow, firstColumn, endColumn
				}
			}

			index++;

			String centerName = "";
			String areaName = "";
			String branchName = "";
			String centerID = "";
			String areaID = "";
			String branchNbr = "";
			if (list.size() > 0) {
				centerName = list.get(0).get("REGION_CENTER_NAME").toString();
				areaName = list.get(0).get("BRANCH_AREA_NAME").toString();
				branchName = list.get(0).get("BRANCH_NAME").toString();
				centerID = list.get(0).get("REGION_CENTER_ID").toString();
				areaID = list.get(0).get("BRANCH_AREA_ID").toString();
				branchNbr = list.get(0).get("BRANCH_NBR").toString();
			}


			int listCnt = 0;
			for (Map<String, Object> map : list) {
				listCnt++;

				Boolean changeArea = false;
				if (map.get("BRANCH_AREA_NAME") != null && !areaName.equals(map.get("BRANCH_AREA_NAME").toString())) {
					changeArea = true;
				}

				Boolean changeCenter = false;
				if (map.get("REGION_CENTER_NAME") != null && !centerName.equals(map.get("REGION_CENTER_NAME").toString())) {
					changeCenter = true;
				}

				//營運區變換
				if (changeArea) {
					for (Map<String, Object> areaMap : areaList) {
						if (areaMap.get("BRANCH_AREA_ID").toString().equals(areaID)){
							countAreaTotal(row, sheet, style, index, centerName, areaName, areaMap, item);
						}
					}
					areaName = map.get("BRANCH_AREA_NAME").toString();
					areaID = map.get("BRANCH_AREA_ID").toString();
					index++;
				}

				//業務處變換
				if (changeCenter) {
					for (Map<String, Object> centerMap : centerList) {
						if (centerMap.get("REGION_CENTER_ID").toString().equals(centerID)){
							countCenterTotal(row, sheet, style, index, centerName, centerMap, item);
						}
					}
					centerName = map.get("REGION_CENTER_NAME").toString();
					centerID = map.get("REGION_CENTER_ID").toString();
					index++;
				}

				row = sheet.createRow(index);
				int j = 0;
				XSSFCell cell = row.createCell(j);

				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(checkIsNull(map, "REGION_CENTER_NAME"));
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(checkIsNull(map, "BRANCH_AREA_NAME"));
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(checkIsNull(map, "BRANCH_NBR"));
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(checkIsNull(map, "BRANCH_NAME"));
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(checkIsNull(map, "GROUP_ID"));

				for(String key : item) {
					cell = row.createCell(j++);
					cell.setCellStyle(style);

					String value = "";
					if (map.get(key) != null) {

						if("COM_RATE".equals(key) || "B_RATE".equals(key) || "D_RATE".equals(key) || "C_RATE".equals(key) || "F_RATE".equals(key) || "J_RATE".equals(key)){
							value = checkIsNullRate(map,key);
						}else{
							value = map.get(key).toString();
						}
					}
					cell.setCellValue(value);
				}

				index++;
				//最後一筆
				if (listCnt == list.size()) {

					// 區合計
					for (Map<String, Object> areaMap : areaList) {
						if (areaMap.get("BRANCH_AREA_ID").toString().equals(areaID)){
							countAreaTotal(row, sheet, style, index, centerName, areaName, areaMap, item);
						}
					}
					index++;

					//處合計
					for (Map<String, Object> centerMap : centerList) {
						if (centerMap.get("REGION_CENTER_ID").toString().equals(centerID)){
							countCenterTotal(row, sheet, style, index, centerName, centerMap, item);
						}
					}
					index++;

					// 全行合計
					countCenterTotal(row, sheet, style, index, "全行", totalList.get(0), item);
				}
			}

			String tempName = UUID.randomUUID().toString();
			File f = new File(config.getServerHome(), config.getReportTemp() + tempName);
			workbook.write(new FileOutputStream(f)); //絕對路徑建檔
			notifyClientToDownloadFile(config.getReportTemp().substring(1) + tempName, fileName); //相對路徑取檔

	}

	// FOR 區合計
	private void countAreaTotal(XSSFRow row, XSSFSheet sheet, XSSFCellStyle style, int index, String centerName, String areaName, Map<String, Object> areaMap, List<String> item) {
		row = sheet.createRow(index);
		int i = 0;
		XSSFCell areaTalCell = row.createCell(i);
		areaTalCell = row.createCell(i++);
		areaTalCell.setCellStyle(style);
		areaTalCell.setCellValue(centerName);

		areaTalCell = row.createCell(i++);
		areaTalCell.setCellStyle(style);
		areaTalCell.setCellValue(areaName + " 合計");
		areaTalCell = row.createCell(i++);
		areaTalCell = row.createCell(i++);
		areaTalCell = row.createCell(i++);

		for (String key : item){
			areaTalCell = row.createCell(i++);
			areaTalCell.setCellStyle(style);

			String value = "";
			if (areaMap.get(key) != null) {

				if("COM_RATE".equals(key) || "B_RATE".equals(key) || "D_RATE".equals(key) || "C_RATE".equals(key) || "F_RATE".equals(key) || "J_RATE".equals(key)){
					value = checkIsNullRate(areaMap,key);
				}else{
					value = areaMap.get(key).toString();
				}
			}
			areaTalCell.setCellValue(value);
		}

		sheet.addMergedRegion(new CellRangeAddress(index, index, 1, 4)); // firstRow, endRow, firstColumn, endColumn
	}

	// FOR 處&全行合計
	private void countCenterTotal(XSSFRow row, XSSFSheet sheet, XSSFCellStyle style, int index, String centerName, Map<String, Object> centerMap, List<String> item) {
		row = sheet.createRow(index);
		int i = 0;
		XSSFCell centerTalCell = row.createCell(i);
		centerTalCell = row.createCell(i++);
		centerTalCell.setCellStyle(style);
		centerTalCell.setCellValue(centerName + " 合計");
		centerTalCell = row.createCell(i++);
		centerTalCell = row.createCell(i++);
		centerTalCell = row.createCell(i++);
		centerTalCell = row.createCell(i++);

		for (String key : item){
			centerTalCell = row.createCell(i++);
			centerTalCell.setCellStyle(style);

			String value = "";
			if (centerMap.get(key) != null) {

				if("COM_RATE".equals(key) || "B_RATE".equals(key) || "D_RATE".equals(key) || "C_RATE".equals(key) || "F_RATE".equals(key) || "J_RATE".equals(key)){
					value = checkIsNullRate(centerMap,key);
				}else{
					value = centerMap.get(key).toString();
				}
			}
			centerTalCell.setCellValue(value);
		}

		sheet.addMergedRegion(new CellRangeAddress(index, index, 0, 4)); // firstRow, endRow, firstColumn, endColumn
	}


	//個資遮罩
	public String chang_Name(String Name,int type) throws APException{
		String change_name = "";
		try{
			switch (type) {
			//ID
			case 1:
				if(StringUtils.isNotBlank(Name)){
					change_name = Name.replaceFirst(Name.substring(5, 7), "**");
				}else{
					change_name = Name;
				}
				break;
				//Name
			case 2:
				if(StringUtils.isNotBlank(Name) && Name.length()>1){
					change_name = Name.replaceFirst(Name.substring(1,2), "*");
				}else{
					change_name = Name;
				}
				break;
			default:
				change_name = Name;
				break;
			}
		}catch (Exception e) {
			change_name = "**";
		}
		return change_name;
	}
	/**
	* 檢查Map取出欄位是否為Null
	*
	* @param map
	* @return String
	*/
	private String checkIsNull(Map map, String key) {
		if(StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null){
			return String.valueOf(map.get(key));
		}else{
			return "";
		}
	}

	private String checkIsNullRate(Map map, String key) {
		if(StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null){
			BigDecimal b = new BigDecimal(map.get(key).toString()).multiply(new BigDecimal(100));
			return String.valueOf(b.setScale(2,BigDecimal.ROUND_HALF_UP)) + "%";

		}else{
			return "";
		}
	}

	//處理貨幣格式
	private String currencyFormat(Map map, String key){
		DecimalFormat df = new DecimalFormat("###.##");
		if(StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null){
			return df.format(map.get(key));
		}else
			return df.format(0);
	}
	
	//鍵機資訊查詢SQL
	private StringBuffer getType1SQL() {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT DISTINCT IOT.INS_KEYNO, IOT.KEYIN_DATE, IOT.PREMATCH_SEQ, ");
		sql.append("		TO_CHAR(IOT.APPLY_DATE, 'YYYY-MM-DD') AS APPLY_DATE, IOT.OP_BATCH_NO, ");
		sql.append(" 		IOT.INS_ID, IOT.POLICY_NO, IOT.AO_CODE, ");
		sql.append(" 		IOT.BRANCH_NBR, IOT.BRANCH_NAME, IOT.RECRUIT_ID, ");
		sql.append(" 		MEM.CUST_ID AS M_CUST_ID, ");
		sql.append(" 		MEM.EMP_NAME AS M_EMP_NAME, IOT.INSURED_NAME, ");
		sql.append(" 		IOT.INSURED_ID, IOT.PROPOSER_NAME, IOT.CUST_ID, ");
		sql.append(" 		IOT.INSPRD_ID, IOT.INSPRD_NAME, TO_CHAR(IOT.STATUS) AS STATUS, ");
		sql.append(" 		IOT.INSPRD_TYPE, IOT.INSPRD_ANNUAL, IOT.PAY_TYPE, IOT.PRD_RISK AS IOT_PRD_RISK, ");
		sql.append(" 		IOT.MOP2, IOT.SPECIAL_CONDITION, IOT.CURR_CD, ");
		sql.append(" 		IOT.EXCH_RATE, IOT.REAL_PREMIUM, IOT.BASE_PREMIUM, ");
		sql.append(" 		IOT.MATCH_DATE, IOT.REF_CON_ID, ");
		sql.append(" 		SAL.SALES_PERSON, SAL.SALES_NAME, IOT.REG_TYPE, ");
		sql.append(" 		IOT.WRITE_REASON, IOT.WRITE_REASON_OTH, ");
		sql.append(" 		IOT.QC_ANC_DOC, IOT.INS_RCV_DATE, IOT.INS_RCV_OPRID, IOT.REMARK_BANK, ");
		sql.append(" 		IOT.DELETE_DATE, IOT.DELETE_OPRID ,IOT.FIRST_PAY_WAY, IOT.CREATETIME, ");
		sql.append("		IOT.COMPANY_NUM, E.CNAME AS INS_COM_NAME, CASE WHEN IOT.COMPANY_NUM = 82 THEN '富壽' ELSE '非富壽' END AS INS_SOURCE ");
		sql.append(" FROM VWIOT_MAIN IOT ");
		sql.append(" LEFT JOIN TBORG_MEMBER MEM ON MEM.EMP_ID = IOT.RECRUIT_ID ");
		sql.append(" LEFT JOIN TBCAM_LOAN_SALEREC SAL ON SAL.REF_CON_ID = IOT.REF_CON_ID ");
		sql.append(" LEFT JOIN TBJSB_INS_PROD_COMPANY E ON E.SERIALNUM = IOT.COMPANY_NUM ");
		
		return sql;
	}
	
	//鍵機資訊查詢匯出SQL
	private StringBuffer getType1SQLExport(String type) {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT DISTINCT IOT.INS_KEYNO, IOT.KEYIN_DATE, IOT.PREMATCH_SEQ, ");
		sql.append("		TO_CHAR(IOT.APPLY_DATE, 'YYYY-MM-DD') AS APPLY_DATE, IOT.OP_BATCH_NO, ");
		sql.append(" 		IOT.INS_ID, IOT.POLICY_NO, IOT.AO_CODE, ");
		sql.append(" 		IOT.BRANCH_NBR, IOT.BRANCH_NAME, IOT.RECRUIT_ID, ");
		sql.append(" 		MEM.CUST_ID AS M_CUST_ID, ");
		sql.append(" 		MEM.EMP_NAME AS M_EMP_NAME, IOT.INSURED_NAME, ");
		sql.append(" 		IOT.INSURED_ID, IOT.PROPOSER_NAME, IOT.CUST_ID, ");
		sql.append(" 		IOT.INSPRD_ID, IOT.INSPRD_NAME, TO_CHAR(IOT.STATUS) AS STATUS, ");
		sql.append(" 		IOT.INSPRD_TYPE, IOT.INSPRD_ANNUAL, IOT.PAY_TYPE, IOT.PRD_RISK AS IOT_PRD_RISK, ");
		sql.append(" 		IOT.MOP2, IOT.SPECIAL_CONDITION, IOT.CURR_CD, ");
		sql.append(" 		IOT.EXCH_RATE, IOT.REAL_PREMIUM, IOT.BASE_PREMIUM, ");
		sql.append(" 		IOT.MATCH_DATE, IOT.REF_CON_ID, ");
		sql.append(" 		SAL.SALES_PERSON, SAL.SALES_NAME, IOT.REG_TYPE, ");
		sql.append(" 		IOT.WRITE_REASON, IOT.WRITE_REASON_OTH, ");
		sql.append(" 		IOT.QC_ANC_DOC, IOT.INS_RCV_DATE, IOT.INS_RCV_OPRID, IOT.REMARK_BANK, ");
		sql.append(" 		IOT.DELETE_DATE, IOT.DELETE_OPRID ,IOT.FIRST_PAY_WAY, IOT.CREATETIME, ");
		sql.append("		IOT.COMPANY_NUM, E.CNAME AS INS_COM_NAME, CASE WHEN IOT.COMPANY_NUM = 82 THEN '富壽' ELSE '非富壽' END AS INS_SOURCE ");
		sql.append("		,FND.INS_KEYNO AS F_INS_KEYNO, FND.PRD_KEYNO, FND.TARGET_ID, "); 	//EXCEL帶出明細欄位
		sql.append("		FND.LINKED_NAME, FND.PRD_RISK, FND.LINK_PCT  ");	//EXCEL帶出明細欄位
		sql.append(" FROM VWIOT_MAIN IOT ");
		sql.append(" LEFT JOIN TBORG_MEMBER MEM ON MEM.EMP_ID = IOT.RECRUIT_ID ");
		sql.append(" LEFT JOIN TBCAM_LOAN_SALEREC SAL ON SAL.REF_CON_ID = IOT.REF_CON_ID ");
		sql.append(" LEFT JOIN TBJSB_INS_PROD_COMPANY E ON E.SERIALNUM = IOT.COMPANY_NUM ");
		if(StringUtils.equals("1", type))
			sql.append(" LEFT JOIN VWIOT_FUND_LINK FND ON FND.INS_KEYNO = IOT.INS_KEYNO ");//EXCEL帶出明細欄位
		else
			sql.append(" LEFT JOIN VWIOT_FUND_LINK FND ON FND.PREMATCH_SEQ = IOT.PREMATCH_SEQ ");//EXCEL帶出明細欄位
		
		return sql;
	}
	
	//鍵機資訊查詢WHERE條件SQL
	private StringBuffer getType1WhereSQL(IOT170InputVO inputVO, QueryConditionIF condition) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		StringBuffer sql = new StringBuffer();
		sql.append(" WHERE IOT.REG_TYPE IN ('1','2') ");

		//業務處
		if(StringUtils.isNotBlank(inputVO.getRegion_center_id())){
			sql.append(" AND IOT.REGION_CENTER_ID = :region ");
			condition.setObject("region",inputVO.getRegion_center_id());
		}
		//營運區
		if(StringUtils.isNotBlank(inputVO.getBranch_area_id())){
			sql.append(" AND IOT.BRANCH_AREA_ID = :branchArea ");
			condition.setObject("branchArea",inputVO.getBranch_area_id());
		}
		//分行
		if(StringUtils.isNotBlank(inputVO.getBranch_nbr())){
			sql.append(" AND IOT.BRANCH_NBR = :branchNbr ");
			condition.setObject("branchNbr",inputVO.getBranch_nbr());
		}
		//理專
		if(StringUtils.isNotBlank(inputVO.getEmp_id())){
			sql.append(" AND MEM.EMP_ID = :empid ");
			condition.setObject("empid",inputVO.getEmp_id());
		}
		//保險文件編號
		if(StringUtils.isNotBlank(inputVO.getInsID())){
			sql.append(" AND IOT.INS_ID = :insID ");
			condition.setObject("insID",inputVO.getInsID());
		}
		//保險產品類型
		if(StringUtils.isNotBlank(inputVO.getInsPrdType())){
			sql.append(" AND IOT.INSPRD_TYPE = :INSPRD_TYPE ");
			condition.setObject("INSPRD_TYPE",inputVO.getInsPrdType());
		}
		//保單號碼
		if(StringUtils.isNotBlank(inputVO.getPolicyNo1())){
			sql.append(" AND IOT.POLICY_NO1 = :POLICY_NO1 ");
			condition.setObject("POLICY_NO1",inputVO.getPolicyNo1());
		}
		if(StringUtils.isNotBlank(inputVO.getPolicyNo2())){
			sql.append(" AND IOT.POLICY_NO2 = :POLICY_NO2 ");
			condition.setObject("POLICY_NO2",inputVO.getPolicyNo2());
		}
		if(StringUtils.isNotBlank(inputVO.getPolicyNo3())){
			sql.append(" AND IOT.POLICY_NO3 = :POLICY_NO3 ");
			condition.setObject("POLICY_NO3",inputVO.getPolicyNo3());
		}
		//文件簽收狀態
		if(StringUtils.isNotBlank(inputVO.getDocStatus())){
			sql.append(" AND IOT.STATUS = :STATUS ");
			condition.setObject("STATUS",inputVO.getDocStatus());
		}
		//險種代碼
		if(StringUtils.isNotBlank(inputVO.getInsPrdID())){
			sql.append(" AND IOT.INSPRD_ID = :INSPRD_ID ");
			condition.setObject("INSPRD_ID",inputVO.getInsPrdID());
		}
		//招攬人員編
		if(StringUtils.isNotBlank(inputVO.getRecruitID())){
			sql.append(" AND IOT.RECRUIT_ID = :RECRUIT_ID ");
			condition.setObject("RECRUIT_ID",inputVO.getRecruitID());
		}
		//要保人ID
		if(StringUtils.isNotBlank(inputVO.getCustID())){
			sql.append(" AND IOT.CUST_ID = :CUST_ID ");
			condition.setObject("CUST_ID",inputVO.getCustID());
		}
		//被保人ID
		if(StringUtils.isNotBlank(inputVO.getInsuredID())){
			sql.append(" AND IOT.INSURED_ID = :INSURED_ID ");
			condition.setObject("INSURED_ID",inputVO.getInsuredID());
		}
		//首期繳費方式
		if(StringUtils.isNotBlank(inputVO.getFstPayWay())){
			sql.append(" AND IOT.FIRST_PAY_WAY = :FIRST_PAY_WAY ");
			condition.setObject("FIRST_PAY_WAY",inputVO.getFstPayWay());
		}
		//鍵機日起日
		if(inputVO.getsCreDate() != null){
			sql.append(" AND TRUNC(IOT.KEYIN_DATE) >= TRUNC(TO_DATE( :sDATE, 'YYYY-MM-DD')) ");
			condition.setObject("sDATE",sdf.format(inputVO.getsCreDate()));
		}
		//鍵機日迄日
		if(inputVO.geteCreDate() != null){
			sql.append(" AND TRUNC(IOT.KEYIN_DATE) <= TRUNC(TO_DATE( :eDATE, 'YYYY-MM-DD')) ");
			condition.setObject("eDATE",sdf.format(inputVO.geteCreDate()));
		}
		//投資型保單標的代號
		if(StringUtils.isNotBlank(inputVO.getInvInsId())){
			sql.append(" AND EXISTS (SELECT 'X' FROM VWIOT_FUND_LINK ");
			sql.append("			 WHERE INS_KEYNO = IOT.INS_KEYNO ");
			sql.append("			   AND TARGET_ID = :TARGET_ID AND LINK_PCT IS NOT NULL) ");
			condition.setObject("TARGET_ID",inputVO.getInvInsId());
		}
		//進件來源
		if(StringUtils.isNotBlank(inputVO.getFB_COM_YN())) {
			if(StringUtils.equals("Y", inputVO.getFB_COM_YN())) {
				sql.append(" And IOT.COMPANY_NUM = 82 ");
			} else if(StringUtils.equals("N", inputVO.getFB_COM_YN())) {
				sql.append(" And IOT.COMPANY_NUM <> 82 ");
			}
		}
		//保險公司
		if(StringUtils.isNotBlank(inputVO.getCOMPANY_NUM())) {
			sql.append(" And IOT.COMPANY_NUM = :companyNum ");
			condition.setObject("companyNum", inputVO.getCOMPANY_NUM());
		}
		
		return sql;
	}
}
