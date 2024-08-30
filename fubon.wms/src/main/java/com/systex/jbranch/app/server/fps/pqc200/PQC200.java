package com.systex.jbranch.app.server.fps.pqc200;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("pqc200")
@Scope("request")
public class PQC200 extends FubonWmsBizLogic {

	private DataAccessManager dam = null;
	
	// 查詢
	public void query(Object body, IPrimitiveMap header) throws JBranchException {
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行人員
		
		PQC200InputVO inputVO = (PQC200InputVO) body;
		PQC200OutputVO outputVO = new PQC200OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		sb.append("SELECT AM.SEQNO, ");
		sb.append("       DEFN.REGION_CENTER_ID, ");
		sb.append("       DEFN.REGION_CENTER_NAME, ");
		sb.append("       DEFN.BRANCH_AREA_ID, ");
		sb.append("       DEFN.BRANCH_AREA_NAME, ");
		sb.append("       DEFN.BRANCH_NBR, ");
		sb.append("       DEFN.BRANCH_NAME, ");
		sb.append("       AM.APPLY_EMP_ID, ");
		sb.append("       APPLY_MEM.EMP_NAME AS APPLY_EMP_NAME, ");
		sb.append("       AM.APPLY_CUST_ID, ");
		sb.append("       AM.APPLY_CUST_NAME, ");
		sb.append("       AM.APPLY_PRD_S_DATE, ");
		sb.append("       AM.APPLY_PRD_E_DATE, ");
		sb.append("       TO_DATE(AM.APPLY_PRD_S_DATE, 'yyyyMMdd') AS PRD_S_DATE, ");
		sb.append("       TO_DATE(AM.APPLY_PRD_E_DATE, 'yyyyMMdd') AS PRD_E_DATE, ");
		sb.append("       AM.APPLY_PRD_TYPE, ");
		sb.append("       AM.APPLY_PRD_ID, ");
		sb.append("       PRD.PRD_NAME, ");
		sb.append("       PRD.CURRENCY, ");
		sb.append("       AM.APPLY_QUOTA, ");
		sb.append("       AM.APPLY_SEQ, ");
		sb.append("       AM.CREATOR, ");
		sb.append("       CREATE_MEM.EMP_NAME AS CREATE_EMP_NAME, ");
		sb.append("       AM.CREATETIME, ");
		sb.append("       AM.MODIFIER, ");
		sb.append("       MODIFY_MEM.EMP_NAME AS MODIFY_EMP_NAME, ");
		sb.append("       AM.LASTUPDATE ");
		sb.append("FROM TBPQC_QUOTA_APPLY_M AM ");
		sb.append("LEFT JOIN VWORG_DEFN_INFO DEFN ON AM.BRANCH_NBR = DEFN.BRANCH_NBR ");
		sb.append("LEFT JOIN TBORG_MEMBER APPLY_MEM ON AM.APPLY_EMP_ID = APPLY_MEM.EMP_ID ");
		sb.append("LEFT JOIN TBPQC_QUOTA_PRD PRD ON AM.APPLY_PRD_S_DATE = PRD.START_DATE AND AM.APPLY_PRD_E_DATE = PRD.END_DATE AND AM.APPLY_PRD_TYPE = PRD.PRD_TYPE AND AM.APPLY_PRD_ID = PRD.PRD_ID ");
		sb.append("LEFT JOIN TBORG_MEMBER CREATE_MEM ON CREATE_MEM.EMP_ID = AM.CREATOR ");
		sb.append("LEFT JOIN TBORG_MEMBER MODIFY_MEM ON MODIFY_MEM.EMP_ID = AM.MODIFIER ");
		sb.append("WHERE 1 = 1 ");

		if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") < 0 ||
			StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).equals("uhrm")) { 
			if (!StringUtils.equals(StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)), "uhrm")) {
				if (StringUtils.isNotEmpty(inputVO.getBranch_nbr())) {
					sb.append("AND DEFN.BRANCH_NBR = :branchNbr ");
					queryCondition.setObject("branchNbr", inputVO.getBranch_nbr());
				} else if (StringUtils.isNotEmpty(inputVO.getBranch_area_id())) {
					sb.append("AND DEFN.BRANCH_AREA_ID = :branchAreaID ");
					queryCondition.setObject("branchAreaID", inputVO.getBranch_area_id());
				} else if (StringUtils.isNotEmpty(inputVO.getRegion_center_id())) {
					sb.append("AND DEFN.REGION_CENTER_ID = :regionCenterID ");
					queryCondition.setObject("regionCenterID", inputVO.getRegion_center_id());
				} 
				
				if (StringUtils.isNotEmpty(inputVO.getEmp_id())) {
					sb.append("AND AM.APPLY_EMP_ID LIKE :empID ");
					queryCondition.setObject("empID", "%" + inputVO.getEmp_id().toUpperCase() + "%");
				}
				
				if (!headmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE)) &&
					!StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).equals("uhrm")) {
					// 非總人行員 或 非 為031之兼任FC，僅可視轄下
					sb.append("AND NOT EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO U WHERE AM.APPLY_EMP_ID = U.EMP_ID) ");
				}
			}
		} else {
			if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") >= 0 &&
				!StringUtils.equals(StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)), "uhrm")) {
				if (StringUtils.isNotEmpty(inputVO.getU_emp_Id())) {
					sb.append("AND AM.APPLY_EMP_ID LIKE :empID ");
					queryCondition.setObject("empID", "%" + inputVO.getU_emp_Id().toUpperCase() + "%");
				}
				
				sb.append("AND EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO U WHERE AM.APPLY_EMP_ID= U.EMP_ID) ");
			}
		}
		
		if (StringUtils.isNotEmpty(inputVO.getPrdType())) {
			sb.append("AND PRD.PRD_TYPE = :prdType ");
			queryCondition.setObject("prdType", inputVO.getPrdType());
		}
		
		if (StringUtils.isNotEmpty(inputVO.getPrdID())) {
			sb.append("AND PRD.PRD_ID = :prdID ");
			queryCondition.setObject("prdID", inputVO.getPrdID());
		}
		
		if (StringUtils.isNotEmpty(inputVO.getReportType())) {
			switch (inputVO.getReportType()) {
				case "O":
					sb.append("AND TO_DATE(PRD.START_DATE, 'yyyyMMdd') <= TRUNC(SYSDATE) ");
					sb.append("AND TO_DATE(PRD.END_DATE, 'yyyyMMdd') >= TRUNC(SYSDATE) ");
					break;
				case "C":
					sb.append("AND TO_DATE(PRD.END_DATE, 'yyyyMMdd') < TRUNC(SYSDATE) ");
					break;
			}
		}

		queryCondition.setQueryString(sb.toString());

		outputVO.setResultList(dam.exeQuery(queryCondition));

		this.sendRtnObject(outputVO);
	}
	
	// 匯出
	public void report(Object body, IPrimitiveMap header) throws JBranchException {
		
		PQC200OutputVO outputVO = (PQC200OutputVO) body;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		List<Map<String, Object>> list = outputVO.getResultList();
		
		String[] csvHeader = { "業務處", "營運區", "分行代碼", "分行名稱", 
							   "行員ID", "行員姓名", 
							   "客戶ID", "客戶姓名", 
							   "產品種類", "產品代號", "產品名稱", "幣別", 
							   "已成功申請額度", "取號號碼", 
							   "建立人", "建立時間", "修改人", "最新修改時間"};
		String[] csvMain   = { "REGION_CENTER_NAME", "BRANCH_AREA_NAME", "BRANCH_NBR", "BRANCH_NAME",
							   "APPLY_EMP_ID", "APPLY_EMP_NAME",
							   "APPLY_CUST_ID", "APPLY_CUST_NAME",
							   "APPLY_PRD_TYPE", "APPLY_PRD_ID", "PRD_NAME", "CURRENCY",
							   "APPLY_QUOTA", "APPLY_SEQ",
							   "CREATOR", "CREATETIME", "MODIFIER", "LASTUPDATE"};

		String fileName = "已申請商品額度清單_" + sdf.format(new Date()) + ".csv";

		List listCSV = new ArrayList();
		for (Map<String, Object> map : list) {
			String[] records = new String[csvHeader.length];
			
			for (int i = 0; i < csvHeader.length; i++) {
				switch (csvMain[i]) {
					case "APPLY_EMP_ID":
						records[i] = "=\"" + checkIsNull(map, csvMain[i]) + "\"";
						break;
					case "CREATOR":
						records[i] = checkIsNull(map, csvMain[i]) + "-" + checkIsNull(map, "CREATE_EMP_NAME");
						break;
					case "MODIFIER":
						records[i] = checkIsNull(map, csvMain[i]) + "-" + checkIsNull(map, "MODIFY_EMP_NAME");
						break;
					case "APPLY_QUOTA":
						records[i] = currencyFormat(map, csvMain[i]);
						break;
					default:
						records[i] = checkIsNull(map, csvMain[i]);
						break;
				}
			}
			
			listCSV.add(records);
		}
		
		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		csv.addRecordList(listCSV);
		
		notifyClientToDownloadFile(csv.generateCSV(), fileName);
		
		this.sendRtnObject(null);
	}
	
	// 剩餘額度共用SQL
	public String getCheckSQL () {
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT DISTINCT ");
		sb.append("       START_DATE, ");
		sb.append("       END_DATE, ");
		sb.append("       TO_DATE(START_DATE, 'yyyyMMdd') AS D_START_DATE, ");
		sb.append("       TO_DATE(END_DATE, 'yyyyMMdd') AS D_END_DATE, ");
		sb.append("       PRD_TYPE, ");
		sb.append("       PRD_ID, ");
		sb.append("       PRD_QUOTA, ");
		sb.append("       EMP_ID, ");
		sb.append("       PRIVILEGEID, ");
		sb.append("       MIN_QUOTA, ");
		sb.append("       MAX_QUOTA, ");
		sb.append("       TOTAL_QUOTA_TYPE, ");
		sb.append("       TOTAL_QUOTA, ");
		sb.append("       SUM_APPLY_QUOTA, ");
		sb.append("       TOTAL_QUOTA - SUM_APPLY_QUOTA AS LAVE_QUOTA ");
		sb.append("FROM ( ");
		sb.append("  SELECT QCO.START_DATE, QCO.END_DATE, QCO.PRD_TYPE, QCO.PRD_ID, ");
		sb.append("         QCO.PRD_QUOTA, ");
		sb.append("         ORG.EMP_ID, EMP.PRIVILEGEID, ");
		sb.append("         CASE WHEN QCO.ALL_QUOTA > 0 THEN 'ALL_QUOTA' ELSE ORG.ORG_TYPE END AS TOTAL_QUOTA_TYPE, ");
		sb.append("         CASE WHEN QCO.ALL_QUOTA > 0 THEN NVL(QCO.ALL_QUOTA, 0) ");
		sb.append("              WHEN ORG.ORG_TYPE = 'RC_APPLY_1' THEN NVL(QCO.RC_QUOTA_1, 0) ");
		sb.append("              WHEN ORG.ORG_TYPE = 'RC_APPLY_2' THEN NVL(QCO.RC_QUOTA_2, 0) ");
		sb.append("              WHEN ORG.ORG_TYPE = 'RC_APPLY_3' THEN NVL(QCO.RC_QUOTA_3, 0) ");
		sb.append("              WHEN ORG.ORG_TYPE = 'RC_APPLY_4' THEN NVL(QCO.RC_QUOTA_4, 0) ");
		sb.append("              WHEN ORG.ORG_TYPE = 'RC_APPLY_5' THEN NVL(QCO.RC_QUOTA_5, 0) ");
		sb.append("              WHEN ORG.ORG_TYPE = 'RC_APPLY_6' THEN NVL(QCO.RC_QUOTA_6, 0) ");
		sb.append("              WHEN ORG.ORG_TYPE = 'RC_APPLY_UHRM' THEN NVL(QCO.RC_QUOTA_UHRM, 0) ");
		sb.append("         ELSE 0 END AS TOTAL_QUOTA, ");
		sb.append("         CASE WHEN QCO.ALL_QUOTA > 0 THEN NVL(QA.SUM_APPLY_QUOTA, 0) ");
		sb.append("              WHEN ORG.ORG_TYPE IN ('RC_APPLY_1', 'RC_APPLY_2', 'RC_APPLY_3', 'RC_APPLY_4', 'RC_APPLY_5', 'RC_APPLY_6', 'RC_APPLY_UHRM') THEN NVL(RA.SUM_APPLY_QUOTA, 0) ");
		sb.append("         ELSE 0 END AS SUM_APPLY_QUOTA, ");
		sb.append("         BASIC.MIN_QUOTA, ");
		sb.append("         BASIC.MAX_QUOTA ");
		sb.append("  FROM ( ");
		sb.append("    SELECT MEM.EMP_ID, ");
		sb.append("           CASE WHEN MEM.DEPT_ID = '031' THEN 'RC_APPLY_UHRM' ");
		sb.append("                WHEN INSTR(DEFN.REGION_CENTER_NAME, '一') > 0 THEN 'RC_APPLY_1' ");
		sb.append("                WHEN INSTR(DEFN.REGION_CENTER_NAME, '二') > 0 THEN 'RC_APPLY_2' ");
		sb.append("                WHEN INSTR(DEFN.REGION_CENTER_NAME, '三') > 0 THEN 'RC_APPLY_3' ");
		sb.append("                WHEN INSTR(DEFN.REGION_CENTER_NAME, '四') > 0 THEN 'RC_APPLY_4' ");
		sb.append("                WHEN INSTR(DEFN.REGION_CENTER_NAME, '五') > 0 THEN 'RC_APPLY_5' ");
		sb.append("                WHEN INSTR(DEFN.REGION_CENTER_NAME, '六') > 0 THEN 'RC_APPLY_6' ");
		sb.append("            ELSE NULL END AS ORG_TYPE ");
		sb.append("    FROM TBORG_MEMBER MEM ");
		sb.append("    LEFT JOIN VWORG_DEFN_INFO DEFN ON MEM.DEPT_ID = DEFN.BRANCH_NBR ");
		sb.append("    WHERE 1 = 1 ");
		sb.append("    AND CASE WHEN MEM.DEPT_ID = '031' THEN 'RC_APPLY_UHRM' ");
		sb.append("             WHEN INSTR(DEFN.REGION_CENTER_NAME, '一') > 0 THEN 'RC_APPLY_1' ");
		sb.append("             WHEN INSTR(DEFN.REGION_CENTER_NAME, '二') > 0 THEN 'RC_APPLY_2' ");
		sb.append("             WHEN INSTR(DEFN.REGION_CENTER_NAME, '三') > 0 THEN 'RC_APPLY_3' ");
		sb.append("             WHEN INSTR(DEFN.REGION_CENTER_NAME, '四') > 0 THEN 'RC_APPLY_4' ");
		sb.append("             WHEN INSTR(DEFN.REGION_CENTER_NAME, '五') > 0 THEN 'RC_APPLY_5' ");
		sb.append("             WHEN INSTR(DEFN.REGION_CENTER_NAME, '六') > 0 THEN 'RC_APPLY_6' ");
		sb.append("        ELSE NULL END IS NOT NULL ");
		sb.append("  ) ORG ");
		sb.append("  LEFT JOIN TBPQC_QUOTA_CONTROL_ORG QCO ON 1 = 1 ");
		sb.append("  LEFT JOIN VWORG_EMP_INFO EMP ON ORG.EMP_ID = EMP.EMP_ID ");
		sb.append("  LEFT JOIN ( ");
		sb.append("    SELECT PRD.START_DATE, PRD.END_DATE, PRD.PRD_TYPE, PRD.PRD_ID, NVL(SUM(APPLY_QUOTA), 0) AS SUM_APPLY_QUOTA ");
		sb.append("    FROM TBPQC_QUOTA_PRD PRD ");
		sb.append("    LEFT JOIN TBPQC_QUOTA_APPLY_M AM ON PRD.START_DATE = AM.APPLY_PRD_S_DATE AND PRD.END_DATE = AM.APPLY_PRD_E_DATE AND PRD.PRD_TYPE = AM.APPLY_PRD_TYPE AND PRD.PRD_ID = AM.APPLY_PRD_ID ");
		sb.append("    GROUP BY PRD.START_DATE, PRD.END_DATE, PRD.PRD_TYPE, PRD.PRD_ID ");
		sb.append("  ) QA ON QA.START_DATE = QCO.START_DATE AND QA.END_DATE = QCO.END_DATE AND QA.PRD_TYPE = QCO.PRD_TYPE AND QA.PRD_ID = QCO.PRD_ID ");
		sb.append("  LEFT JOIN ( ");
		sb.append("    SELECT BASE.START_DATE, BASE.END_DATE, BASE.PRD_TYPE, BASE.PRD_ID, ");
		sb.append("           CASE WHEN BASE.REGION_CENTER_ID = '031' THEN 'RC_APPLY_UHRM' ");
		sb.append("                WHEN BASE.REGION_CENTER_ID = '171' THEN 'RC_APPLY_1' ");
		sb.append("                WHEN BASE.REGION_CENTER_ID = '172' THEN 'RC_APPLY_2' ");
		sb.append("                WHEN BASE.REGION_CENTER_ID = '174' THEN 'RC_APPLY_3' ");
		sb.append("                WHEN BASE.REGION_CENTER_ID = '142' THEN 'RC_APPLY_4' ");
		sb.append("                WHEN BASE.REGION_CENTER_ID = '145' THEN 'RC_APPLY_5' ");
		sb.append("                WHEN BASE.REGION_CENTER_ID = '146' THEN 'RC_APPLY_6' ");
		sb.append("            ELSE NULL END AS ORG_TYPE, ");
		sb.append("            NVL(APPLY_Q.SUM_APPLY_QUOTA, 0) AS SUM_APPLY_QUOTA ");
		sb.append("    FROM ( ");
		sb.append("      SELECT DEFN_BASE.REGION_CENTER_ID, PRD.START_DATE, PRD.END_DATE, PRD.PRD_TYPE, PRD.PRD_ID ");
		sb.append("      FROM TBPQC_QUOTA_PRD PRD ");
		sb.append("      LEFT JOIN ( ");
		sb.append("        SELECT DISTINCT REGION_CENTER_ID FROM VWORG_DEFN_INFO ");
		sb.append("        UNION ");
		sb.append("        SELECT '031' AS REGION_CENTER_ID FROM DUAL ");
		sb.append("      ) DEFN_BASE ON 1 = 1 ");
		sb.append("    ) BASE ");
		sb.append("    LEFT JOIN ( ");
		sb.append("      SELECT CASE WHEN MEM.DEPT_ID = '031' THEN '031' ELSE DEFN_APPLY.REGION_CENTER_ID END AS REGION_CENTER_ID, ");
		sb.append("             A.APPLY_PRD_S_DATE, A.APPLY_PRD_E_DATE, A.APPLY_PRD_TYPE, A.APPLY_PRD_ID, SUM(A.APPLY_QUOTA) AS SUM_APPLY_QUOTA ");
		sb.append("      FROM TBPQC_QUOTA_APPLY_M A ");
		sb.append("      LEFT JOIN TBORG_MEMBER MEM ON A.APPLY_EMP_ID = MEM.EMP_ID ");
		sb.append("      LEFT JOIN VWORG_DEFN_INFO DEFN_APPLY ON MEM.DEPT_ID = DEFN_APPLY.BRANCH_NBR ");
		sb.append("      GROUP BY CASE WHEN MEM.DEPT_ID = '031' THEN '031' ELSE DEFN_APPLY.REGION_CENTER_ID END, A.APPLY_PRD_S_DATE, A.APPLY_PRD_E_DATE, A.APPLY_PRD_TYPE, A.APPLY_PRD_ID ");
		sb.append("    ) APPLY_Q ON APPLY_Q.APPLY_PRD_S_DATE = BASE.START_DATE AND APPLY_Q.APPLY_PRD_E_DATE = BASE.END_DATE AND APPLY_Q.APPLY_PRD_TYPE = BASE.PRD_TYPE AND APPLY_Q.APPLY_PRD_ID = BASE.PRD_ID AND APPLY_Q.REGION_CENTER_ID = BASE.REGION_CENTER_ID ");
		sb.append("  ) RA ON RA.START_DATE = QCO.START_DATE AND RA.END_DATE = QCO.END_DATE AND RA.PRD_TYPE = QCO.PRD_TYPE AND RA.PRD_ID = QCO.PRD_ID AND ORG.ORG_TYPE = RA.ORG_TYPE ");
		sb.append("  INNER JOIN TBPQC_QUOTA_CONTROL_BASIC BASIC ON EMP.PRIVILEGEID = BASIC.PRIVILEGEID ");
		sb.append("  WHERE 1 = 1 ");

		sb.append("  AND TO_DATE(QCO.START_DATE, 'yyyyMMdd') <= TRUNC(SYSDATE) ");
		sb.append("  AND TO_DATE(QCO.END_DATE, 'yyyyMMdd') >= TRUNC(SYSDATE) ");
		
		sb.append("  AND EMP.EMP_ID = :empID ");
		sb.append("  AND QCO.PRD_TYPE = :prdType ");
		sb.append("  AND QCO.PRD_ID = :prdID ");
		sb.append(") BASE ");
		
		return sb.toString();
	}
	
	// 申請
	public void add(Object body, IPrimitiveMap header) throws JBranchException {
		
		PQC200InputVO inputVO = (PQC200InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		// 1. 再次確認剩餘餘額是否足夠
		sb.append(getCheckSQL());
		
		queryCondition.setObject("empID", getUserVariable(FubonSystemVariableConsts.LOGINID));
		queryCondition.setObject("prdType", inputVO.getPrdType());		
		queryCondition.setObject("prdID", inputVO.getPrdID());
		
		sb.append("WHERE 1 = 1 ");
		
		if (StringUtils.isNotEmpty(inputVO.getAddStartDate())) {
			sb.append("AND BASE.START_DATE = :startDate ");
			queryCondition.setObject("startDate", inputVO.getAddStartDate());
		}
		
		if (StringUtils.isNotEmpty(inputVO.getAddEndDate())) {
			sb.append("AND BASE.END_DATE = :endDate ");
			queryCondition.setObject("endDate", inputVO.getAddEndDate());
		}
		
		queryCondition.setQueryString(sb.toString());

		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		if (list.size() > 0) {
			// 重覆確認申請之額度小於剩餘額度
			if ((inputVO.getApplyQuota()).compareTo((BigDecimal) list.get(0).get("LAVE_QUOTA")) == -1) {
				// 取得本日編號
				String applySEQ = "";
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuffer();
				
				sb.append("SELECT P.PARAM_CODE AS PRD_TYPE, TO_CHAR(SYSDATE, 'yyyyMMdd') AS APPLY_DATE, COUNT(M.SEQNO) + 1 AS COUNTS ");
				sb.append("FROM TBSYSPARAMETER P ");
				sb.append("LEFT JOIN TBPQC_QUOTA_APPLY_M M ON P.PARAM_CODE = M.APPLY_PRD_TYPE AND TRUNC(APPLY_DATE) = TRUNC(SYSDATE) ");
				sb.append("WHERE 1 = 1 ");
				sb.append("AND P.PARAM_TYPE = 'PQC.PRD_TYPE' ");
				sb.append("AND P.PARAM_CODE = :prdType ");
				sb.append("GROUP BY P.PARAM_CODE, TO_CHAR(SYSDATE, 'yyyyMMdd') ");

				queryCondition.setObject("prdType", inputVO.getPrdType());
				
				queryCondition.setQueryString(sb.toString());

				List<Map<String, Object>> applySEQList = dam.exeQuery(queryCondition);
				
				applySEQ = (String) applySEQList.get(0).get("PRD_TYPE") + (String) applySEQList.get(0).get("APPLY_DATE") + addZeroForNum(String.valueOf(applySEQList.get(0).get("COUNTS")), 4);
				
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuffer();
				sb.append("INSERT INTO TBPQC_QUOTA_APPLY_M ( ");
				sb.append("  SEQNO, ");
				sb.append("  BRANCH_NBR, ");
				sb.append("  APPLY_EMP_ID, ");
				sb.append("  APPLY_CUST_ID, ");
				sb.append("  APPLY_CUST_NAME, ");
				sb.append("  APPLY_PRD_S_DATE, ");
				sb.append("  APPLY_PRD_E_DATE, ");
				sb.append("  APPLY_DATE, ");
				sb.append("  APPLY_PRD_TYPE, ");
				sb.append("  APPLY_PRD_ID, ");
				sb.append("  APPLY_QUOTA, ");
				sb.append("  APPLY_SEQ, ");
				sb.append("  VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE ");
				sb.append(") ");
				sb.append("VALUES ( ");
				sb.append("  TBPQC_QUOTA_APPLY_M_SEQ.NEXTVAL, ");
				sb.append("  :branchNbr, ");
				sb.append("  :empID, ");
				sb.append("  :custID, ");
				sb.append("  :custName, ");
				sb.append("  :prdStartDate, ");
				sb.append("  :prdEndDate, ");
				sb.append("  SYSDATE, ");
				sb.append("  :prdType, ");
				sb.append("  :prdID, ");
				sb.append("  :applyQuota, ");
				sb.append("  :applySEQ, ");
				sb.append("  0, SYSDATE, :empID, :empID, SYSDATE ");
				sb.append(") ");
				
				// condition
				queryCondition.setObject("branchNbr", getUserVariable(FubonSystemVariableConsts.LOGINBRH));
				queryCondition.setObject("empID", getUserVariable(FubonSystemVariableConsts.LOGINID));
				queryCondition.setObject("custID", inputVO.getCustId());
				queryCondition.setObject("custName", inputVO.getCustName());
				queryCondition.setObject("prdStartDate", inputVO.getAddStartDate());
				queryCondition.setObject("prdEndDate", inputVO.getAddEndDate());
				queryCondition.setObject("prdType", inputVO.getPrdType());
				queryCondition.setObject("prdID", inputVO.getPrdID());
				queryCondition.setObject("applyQuota", inputVO.getApplyQuota());
				queryCondition.setObject("applySEQ", applySEQ);

				queryCondition.setQueryString(sb.toString());
				
				dam.exeUpdate(queryCondition);
			} else {
				throw new JBranchException("額度不足，請刷新畫面重新申請");
			}
		} else {
			throw new JBranchException("查無該商品餘額資訊");
		}
		
		this.sendRtnObject(null);
	}
	
	// 修改
	public void upd(Object body, IPrimitiveMap header) throws JBranchException {
		
		PQC200InputVO inputVO = (PQC200InputVO) body;
		PQC200OutputVO outputVO = new PQC200OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		sb.append("UPDATE TBPQC_QUOTA_APPLY_M ");
		sb.append("SET APPLY_QUOTA = :applyQuota, ");
		sb.append("    VERSION = VERSION + 1, ");
		sb.append("    MODIFIER = :empID, ");
		sb.append("    LASTUPDATE = SYSDATE ");
		sb.append("WHERE SEQNO = :seqNO ");
		
		// PK
		queryCondition.setObject("seqNO", inputVO.getSEQNO());
		
		// condition
		queryCondition.setObject("applyQuota", inputVO.getApplyQuota());
		queryCondition.setObject("empID", getUserVariable(FubonSystemVariableConsts.LOGINID));

		queryCondition.setQueryString(sb.toString());
		
		dam.exeUpdate(queryCondition);
		
		this.sendRtnObject(outputVO);
	}

	// 刪除
	public void del(Object body, IPrimitiveMap header) throws JBranchException {
		
		PQC200InputVO inputVO = (PQC200InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		sb.append("DELETE FROM TBPQC_QUOTA_APPLY_M WHERE SEQNO = :seqNO ");
		
		queryCondition.setObject("seqNO", inputVO.getSEQNO());
		
		queryCondition.setQueryString(sb.toString());
		
		dam.exeUpdate(queryCondition);
		
		this.sendRtnObject(null);
	}

	// 取得剩餘額度
	public void getLaveQuota(Object body, IPrimitiveMap header) throws JBranchException {
		
		PQC200InputVO inputVO = (PQC200InputVO) body;
		PQC200OutputVO outputVO = new PQC200OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		sb.append(getCheckSQL());
		
		queryCondition.setObject("empID", getUserVariable(FubonSystemVariableConsts.LOGINID));
		queryCondition.setObject("prdType", inputVO.getSearchPrdType());		
		queryCondition.setObject("prdID", inputVO.getSearchPrdID());
		
		queryCondition.setQueryString(sb.toString());

		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		if (list.size() > 0) {
			outputVO.setShowStartDate((Date) list.get(0).get("D_START_DATE"));			// 回報起日
			outputVO.setShowEndDate((Date) list.get(0).get("D_END_DATE"));				// 回報迄日
			
			outputVO.setMIN_QUOTA((BigDecimal) list.get(0).get("MIN_QUOTA"));			// 最低申購金額
			outputVO.setMAX_QUOTA((BigDecimal) list.get(0).get("MAX_QUOTA"));			// 最高申購金額
			outputVO.setLAVE_QUOTA((BigDecimal) list.get(0).get("LAVE_QUOTA"));  		// 剩餘額度
			outputVO.setSTART_DATE((String) list.get(0).get("START_DATE"));				// 回報起日
			outputVO.setEND_DATE((String) list.get(0).get("END_DATE"));					// 回報迄日
			outputVO.setTOTAL_QUOTA_TYPE((String) list.get(0).get("TOTAL_QUOTA_TYPE")); // 全行/各處 之總額度類別
			outputVO.setTOTAL_QUOTA((BigDecimal) list.get(0).get("TOTAL_QUOTA"));      	// 全行/各處 之總額度
		}

		this.sendRtnObject(outputVO);
	}

	// 左補0
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
	
	// 檢查Map取出欄位是否為Null
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
			DecimalFormat df = new DecimalFormat("#,##0.00");
			return df.format(map.get(key));
		} else
			return "0.00";
	}
}