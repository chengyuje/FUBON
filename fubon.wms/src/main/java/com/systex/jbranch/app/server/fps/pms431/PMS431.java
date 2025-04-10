package com.systex.jbranch.app.server.fps.pms431;

import static com.systex.jbranch.fubon.jlb.DataFormat.getCustIdMaskForHighRisk;
import static java.lang.String.format;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.defaultString;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("pms431")
@Scope("prototype")
public class PMS431 extends FubonWmsBizLogic {

	private DataAccessManager dam = null;

	public void chkMaintenance (Object body, IPrimitiveMap header) throws JBranchException {
		
		initUUID();

		PMS431OutputVO outputVO = new PMS431OutputVO();
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		sb.append("SELECT PRIVILEGEID ");
		sb.append("FROM TBSYSSECUROLPRIASS ");
		sb.append("WHERE PRIVILEGEID IN (SELECT PRIVILEGEID FROM TBSYSSECUPRIFUNMAP WHERE ITEMID = 'PMS431' AND FUNCTIONID = 'maintenance') "); 
		sb.append("AND ROLEID = :roleID ");
		
		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("roleID", (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		
		List<Map<String, Object>> priList = dam.exeQuery(queryCondition);
		if (priList.size() > 0) {
			outputVO.setIsMaintenancePRI("Y");
		} else {
			outputVO.setIsMaintenancePRI("N");
		}
		
		sendRtnObject(outputVO);
	}
	
	public void queryData(Object body, IPrimitiveMap header) throws JBranchException, ParseException {

		PMS431OutputVO outputVO = new PMS431OutputVO();
	    outputVO = this.queryData(body);

	    sendRtnObject(outputVO);
	}

	public PMS431OutputVO queryData(Object body) throws JBranchException {

		initUUID();
		SimpleDateFormat sdfYYYYMM = new SimpleDateFormat("yyyyMM");

		PMS431InputVO inputVO = (PMS431InputVO) body;
		PMS431OutputVO outputVO = new PMS431OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		String loginRoleID = null != inputVO.getSelectRoleID() ? inputVO.getSelectRoleID() : (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);

		XmlInfo xmlInfo = new XmlInfo();
		boolean isHANDMGR = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2).containsKey(loginRoleID);
		boolean isARMGR = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2).containsKey(loginRoleID);

		sb.append("SELECT * ");
		sb.append("FROM ( ");
		sb.append("  SELECT CASE WHEN ( ");
		sb.append("           SELECT COUNT(1) ");
		sb.append("           FROM TBPMS_HIGH_RISK_INV_D_REP D ");
		sb.append("           WHERE RPT.SNAP_YYYYMM = D.SNAP_YYYYMM ");
		sb.append("           AND RPT.AO_BRANCH_NBR = D.AO_BRANCH_NBR ");
		sb.append("           AND RPT.AO_EMP_CID = D.AO_EMP_ID ");
		sb.append("           AND RPT.KIND_TYPE = D.KIND_TYPE ");
		sb.append("           AND D.RM_FLAG = 'U' ");
		sb.append("         ) > 0 THEN 'Y' ELSE 'N' END AS RM_FLAG, ");
		
		sb.append("         RPT.YYYYMM, ");
		sb.append("         TO_CHAR(RPT.CREATETIME, 'YYYY/MM/DD') AS CREATETIME, ");
		sb.append("         NVL(RPT.AO_BRANCH_NBR, ' ') AS BRANCH_NBR, ");
		sb.append("         NVL(ORG.BRANCH_NAME, ' ') AS BRANCH_NAME, ");
		sb.append("         RPT.SNAP_YYYYMM, ");
		sb.append("         RPT.AO_EMP_ID, ");
		sb.append("         RPT.AO_EMP_NAME, ");
		sb.append("         RPT.AO_EMP_CID, ");
		sb.append("         RPT.KIND_TYPE, ");
		sb.append("         RPT.TX_AMT_TWD, ");
		sb.append("         ROUND(RPT.BAL_TWD_ME, 0) AS BAL_TWD_ME, ");
		sb.append("         NVL((SELECT COUNT(*) AS COUNTS ");
		sb.append("              FROM TBPMS_HIGH_RISK_INV_D_REP D ");
		sb.append("              WHERE D.FIRSTUPDATE IS NOT NULL ");
		sb.append("              AND RPT.SNAP_YYYYMM = D.SNAP_YYYYMM ");
		sb.append("              AND RPT.AO_BRANCH_NBR = D.AO_BRANCH_NBR ");
		sb.append("              AND RPT.AO_EMP_CID = D.AO_EMP_ID ");
		sb.append("              AND RPT.KIND_TYPE = D.KIND_TYPE ");
		sb.append("              GROUP BY D.SNAP_YYYYMM, D.AO_BRANCH_NBR, D.AO_EMP_ID), 0) AS RESPONDING, ");
		sb.append("         NVL((SELECT COUNT(*) AS COUNTS ");
		sb.append("              FROM TBPMS_HIGH_RISK_INV_D_REP D ");
		sb.append("              WHERE D.FIRSTUPDATE IS NULL ");
		sb.append("              AND RPT.SNAP_YYYYMM = D.SNAP_YYYYMM ");
		sb.append("              AND RPT.AO_BRANCH_NBR = D.AO_BRANCH_NBR ");
		sb.append("              AND RPT.AO_EMP_CID = D.AO_EMP_ID ");
		sb.append("              AND RPT.KIND_TYPE = D.KIND_TYPE ");
		sb.append("              GROUP BY D.SNAP_YYYYMM, D.AO_BRANCH_NBR, D.AO_EMP_ID), 0) AS NOT_RESPONDING ");
		sb.append("  FROM TBPMS_HIGH_RISK_INV_M_REP RPT ");
		sb.append("  LEFT JOIN VWORG_DEFN_INFO DEFN ON RPT.AO_BRANCH_NBR = DEFN.BRANCH_NBR ");
		sb.append("  LEFT JOIN TBPMS_ORG_REC_N ORG ON RPT.AO_BRANCH_NBR = ORG.DEPT_ID AND TO_DATE(RPT.SNAP_YYYYMM || '01', 'YYYYMMDD') BETWEEN ORG.START_TIME AND ORG.END_TIME ");
		sb.append("  LEFT JOIN TBPMS_EMPLOYEE_REC_N MEM ON RPT.AO_EMP_ID = MEM.EMP_ID AND RPT.AO_BRANCH_NBR = MEM.DEPT_ID AND TO_DATE(RPT.YYYYMM || '01', 'YYYYMMDD') BETWEEN MEM.START_TIME AND MEM.END_TIME ");

		sb.append("  WHERE 1 = 1 ");
		
		if (StringUtils.equals("Y", inputVO.getNeedConfirmYN())) {
			if (StringUtils.isNotBlank(inputVO.getsCreDate())) {
    			sb.append("  AND RPT.YYYYMM >= :yearMonS ");
    			queryCondition.setObject("yearMonS", inputVO.getsCreDate());
    		}
			
			if (StringUtils.isNotBlank(inputVO.geteCreDate())) {
    			sb.append("  AND RPT.YYYYMM <= :yearMonE ");
    			queryCondition.setObject("yearMonE", inputVO.geteCreDate());
    		}
        } else {
        	//資料月份
    		if (StringUtils.isNotBlank(inputVO.getsCreDate())) {
    			sb.append("  AND RPT.YYYYMM >= :yearMonS ");
    			queryCondition.setObject("yearMonS", sdfYYYYMM.format(new Date(Long.parseLong(inputVO.getsCreDate()))));
    		}
    		
    		if (StringUtils.isNotBlank(inputVO.geteCreDate())) {
    			sb.append("  AND RPT.YYYYMM <= :yearMonE ");
    			queryCondition.setObject("yearMonE", sdfYYYYMM.format(new Date(Long.parseLong(inputVO.geteCreDate()))));
    		}
        }
		

		if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("uhrm") < 0) { 		
			if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
				sb.append("  AND RPT.AO_BRANCH_NBR = :branch ");
				sb.append("  AND CASE WHEN ( ");
				sb.append("    SELECT COUNT(1) ");
				sb.append("    FROM TBPMS_HIGH_RISK_INV_D_REP D ");
				sb.append("    WHERE RPT.SNAP_YYYYMM = D.SNAP_YYYYMM ");
				sb.append("    AND RPT.AO_BRANCH_NBR = D.AO_BRANCH_NBR ");
				sb.append("    AND RPT.AO_EMP_CID = D.AO_EMP_ID ");
				sb.append("    AND RPT.KIND_TYPE = D.KIND_TYPE ");
				sb.append("    AND D.RM_FLAG = 'U' ");
				sb.append("  ) > 0 THEN 'Y' ELSE 'N' END = 'N' ");
				
				queryCondition.setObject("branch", inputVO.getBranch_nbr());
			} else if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {	
				sb.append("  AND ( ");
				sb.append("    (");
				sb.append("     CASE WHEN ( ");
				sb.append("         SELECT COUNT(1) ");
				sb.append("         FROM TBPMS_HIGH_RISK_INV_D_REP D ");
				sb.append("         WHERE RPT.SNAP_YYYYMM = D.SNAP_YYYYMM ");
				sb.append("         AND RPT.AO_BRANCH_NBR = D.AO_BRANCH_NBR ");
				sb.append("         AND RPT.AO_EMP_CID = D.AO_EMP_ID ");
				sb.append("         AND RPT.KIND_TYPE = D.KIND_TYPE ");
				sb.append("         AND D.RM_FLAG = 'B' ");
				sb.append("     ) > 0 THEN 'Y' ELSE 'N' END = 'Y' ");
				sb.append("     AND DEFN.BRANCH_AREA_ID = :BRANCH_AREA_IDD ");
				sb.append("    ) ");
				
				if (isHANDMGR || isARMGR) {
					sb.append("  OR ( ");
					sb.append("    CASE WHEN ( ");
					sb.append("      SELECT COUNT(1) ");
					sb.append("      FROM TBPMS_HIGH_RISK_INV_D_REP D ");
					sb.append("      WHERE RPT.SNAP_YYYYMM = D.SNAP_YYYYMM ");
					sb.append("      AND RPT.AO_BRANCH_NBR = D.AO_BRANCH_NBR ");
					sb.append("      AND RPT.AO_EMP_CID = D.AO_EMP_ID ");
					sb.append("      AND RPT.KIND_TYPE = D.KIND_TYPE ");
					sb.append("      AND D.RM_FLAG = 'U' ");
					sb.append("    ) > 0 THEN 'Y' ELSE 'N' END = 'Y' ");
					sb.append("    AND EXISTS ( SELECT 1 FROM TBORG_MEMBER MT WHERE RPT.AO_EMP_ID = MT.EMP_ID AND MT.DEPT_ID = :BRANCH_AREA_IDD) ");
					sb.append("  ) ");
				}
			
				sb.append("  ) ");
				queryCondition.setObject("BRANCH_AREA_IDD", inputVO.getBranch_area_id());
			} else if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
				sb.append("  AND DEFN.REGION_CENTER_ID = :REGION_CENTER_IDD ");
				queryCondition.setObject("REGION_CENTER_IDD", inputVO.getRegion_center_id());
			}
			
			
			if (!isHANDMGR && !isARMGR) {
				sb.append("  AND CASE WHEN ( ");
				sb.append("    SELECT COUNT(1) ");
				sb.append("    FROM TBPMS_HIGH_RISK_INV_D_REP D ");
				sb.append("    WHERE RPT.SNAP_YYYYMM = D.SNAP_YYYYMM ");
				sb.append("    AND RPT.AO_BRANCH_NBR = D.AO_BRANCH_NBR ");
				sb.append("    AND RPT.AO_EMP_CID = D.AO_EMP_ID ");
				sb.append("    AND RPT.KIND_TYPE = D.KIND_TYPE ");
				sb.append("    AND D.RM_FLAG = 'B' ");
				sb.append("  ) > 0 THEN 'Y' ELSE 'N' END = 'Y' ");
			} 
		} else {
			if (StringUtils.isNotBlank(inputVO.getUhrmOP())) {
				sb.append("  AND (");
				sb.append("       EXISTS ( SELECT 1 FROM TBORG_MEMBER MT WHERE RPT.AO_EMP_ID = MT.EMP_ID AND MT.DEPT_ID = :uhrmOP ) ");
				sb.append("    OR MEM.E_DEPT_ID = :uhrmOP ");
				sb.append("  )");
				queryCondition.setObject("uhrmOP", inputVO.getUhrmOP());
			}
			
			sb.append("  AND CASE WHEN ( ");
			sb.append("    SELECT COUNT(1) ");
			sb.append("    FROM TBPMS_HIGH_RISK_INV_D_REP D ");
			sb.append("    WHERE RPT.SNAP_YYYYMM = D.SNAP_YYYYMM ");
			sb.append("    AND RPT.AO_BRANCH_NBR = D.AO_BRANCH_NBR ");
			sb.append("    AND RPT.AO_EMP_CID = D.AO_EMP_ID ");
			sb.append("    AND RPT.KIND_TYPE = D.KIND_TYPE ");
			sb.append("    AND D.RM_FLAG = 'U' ");
			sb.append("  ) > 0 THEN 'Y' ELSE 'N' END = 'Y' ");
		}
		
		// 行員ID
		if (StringUtils.isNotEmpty(inputVO.getAoEmpCID())) {
			sb.append("  AND RPT.AO_EMP_CID = :aoEmpCID ");
			queryCondition.setObject("aoEmpCID", inputVO.getAoEmpCID());
		}
		
		// 類別
		if (StringUtils.isNotEmpty(inputVO.getHighRiskInvType())) {
			sb.append("  AND RPT.KIND_TYPE = :kindType ");
			queryCondition.setObject("kindType", inputVO.getHighRiskInvType());
		}
		
		sb.append(") BASE ");

		sb.append("WHERE 1 = 1 ");

		// 已回覆/未回覆
		if (StringUtils.isNotEmpty(inputVO.getNoteStatus())) {
			switch (inputVO.getNoteStatus()) {
				case "01":
					sb.append("AND BASE.RESPONDING > 0 ");
					break;
				case "02":
					sb.append("AND BASE.NOT_RESPONDING > 0 ");
					break;
			}
		}

		//由工作首頁 CRM181 過來，只須查詢主管尚未確認資料
		if (StringUtils.equals("Y", inputVO.getNeedConfirmYN())) {
			sb.append("  AND BASE.NOT_RESPONDING > 0 ");
		}

		sb.append("ORDER BY BASE.CREATETIME DESC, BASE.BRANCH_NBR, BASE.AO_EMP_ID, BASE.KIND_TYPE ");

		queryCondition.setQueryString(sb.toString());

		ResultIF list = dam.executePaging(queryCondition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());

		outputVO.setTotalPage(list.getTotalPage());
		outputVO.setResultList(list);
		outputVO.setTotalRecord(list.getTotalRecord());
		outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());

		outputVO.setReportList(dam.exeQuery(queryCondition));


		return outputVO;
	}

	public void queryDetail(Object body, IPrimitiveMap header) throws JBranchException {

		PMS431InputVO inputVO = (PMS431InputVO) body;
		PMS431OutputVO outputVO = new PMS431OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		sb.append("SELECT D.YYYYMM, ");

		sb.append("       D.SNAP_YYYYMM, ");
		sb.append("       TO_CHAR(D.TX_DATE, 'YYYY/MM/DD') AS TX_DATE, ");    
		sb.append("       NVL(D.AO_BRANCH_NBR, ' ') AS BRANCH_NBR, ");
		sb.append("       NVL(ORG.BRANCH_NAME, ' ') AS BRANCH_NAME, ");
		sb.append("       D.AO_NAME, ");
		sb.append("       D.AO_EMP_ID AS AO_EMP_CID, ");
		sb.append("       D.ACCT_NBR_ORI, ");
		sb.append("       D.DRCR, ");
		sb.append("       D.KIND_TYPE, ");
		sb.append("       D.STCK_CSTDY_CD, ");
		sb.append("       D.CUR, ");         
		sb.append("       D.TX_AMT_TWD, ");
		sb.append("       D.JRNL_NO, ");
		sb.append("       D.REMK, ");
		sb.append("       D.MEMO, ");
		
		sb.append("       M.AO_EMP_ID, ");
		
		sb.append("       D.NOTE, ");
		sb.append("       D.NOTE2, ");
		sb.append("       D.NOTE3, ");
		sb.append("       D.HR_ATTR, ");
		sb.append("       D.FIRSTUPDATE, ");
		sb.append("       D.VERSION, ");
		sb.append("       TO_CHAR(D.CREATETIME, 'YYYY/MM/DD') AS CREATETIME, ");
		sb.append("       D.CREATOR, ");
		sb.append("       D.MODIFIER, ");
		sb.append("       D.LASTUPDATE ");
		sb.append("FROM TBPMS_HIGH_RISK_INV_D_REP D ");
		sb.append("LEFT JOIN TBPMS_HIGH_RISK_INV_M_REP M ON D.SNAP_YYYYMM = M.SNAP_YYYYMM AND D.AO_BRANCH_NBR = M.AO_BRANCH_NBR AND D.AO_EMP_ID = M.AO_EMP_CID AND D.KIND_TYPE = M.KIND_TYPE ");
		sb.append("LEFT JOIN TBPMS_ORG_REC_N ORG ON M.AO_BRANCH_NBR = ORG.DEPT_ID AND TO_DATE(M.SNAP_YYYYMM || '01', 'YYYYMMDD') BETWEEN ORG.START_TIME AND ORG.END_TIME ");
		sb.append("WHERE 1 = 1 ");
		sb.append("AND D.SNAP_YYYYMM = :SNAP_YYYYMM ");
		sb.append("AND D.AO_BRANCH_NBR = :AO_BRANCH_NBR ");
		sb.append("AND D.AO_EMP_ID = :AO_EMP_ID ");
		sb.append("AND D.KIND_TYPE = :KIND_TYPE ");
		
		sb.append("ORDER BY D.TX_DATE ");

		queryCondition.setObject("SNAP_YYYYMM", inputVO.getSnapYYYYMM());
		queryCondition.setObject("AO_BRANCH_NBR", inputVO.getAoBranchNBR());
		queryCondition.setObject("AO_EMP_ID", inputVO.getAoEmpID());
		queryCondition.setObject("KIND_TYPE", inputVO.getKindType());

		queryCondition.setQueryString(sb.toString());

		outputVO.setDtlList(dam.exeQuery(queryCondition));

		sendRtnObject(outputVO);
	}

	public void save(Object body, IPrimitiveMap header) throws JBranchException {

		PMS431InputVO inputVO = (PMS431InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		for (Map<String, Object> map : inputVO.getEditedList()) {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();

			sb.append("UPDATE TBPMS_HIGH_RISK_INV_D_REP ");
			sb.append("SET HR_ATTR = :hrAttr, ");
			sb.append("    NOTE = :note, ");
			sb.append("    MODIFIER = :modifier, ");
			sb.append("    LASTUPDATE = sysdate ");

			if (null == map.get("FIRSTUPDATE")) {
				sb.append("    ,  ");
				sb.append("    FIRSTUPDATE = sysdate ");
			}

			sb.append("WHERE JRNL_NO = :jrnlNO ");

			// KEY
			queryCondition.setObject("jrnlNO", map.get("JRNL_NO"));

			// CONTENT
			queryCondition.setObject("note", map.get("NOTE"));
			queryCondition.setObject("hrAttr", map.get("HR_ATTR"));
			queryCondition.setObject("modifier", DataManager.getWorkStation(uuid).getUser().getCurrentUserId());

			queryCondition.setQueryString(sb.toString());

			dam.exeUpdate(queryCondition);
		}

		sendRtnObject(null);
	}

	// 匯出CSV
	public void export(Object body, IPrimitiveMap header) throws JBranchException, ParseException, FileNotFoundException, IOException {

		PMS431InputVO inputVO = (PMS431InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行人員
		Map<String, String> armgrMap   = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2);	//處長

		sb.append("SELECT * ");
		sb.append("FROM ( ");
		sb.append("  SELECT CASE WHEN D.RM_FLAG = 'U' THEN 'Y' ELSE 'N' END AS RM_FLAG, ");
		sb.append("         M.CREATETIME AS M_CREATETIME, ");
		sb.append("         M.SNAP_YYYYMM AS M_SNAP_YYYYMM, ");
		sb.append("         NVL(M.AO_BRANCH_NBR, ' ') AS M_BRANCH_NBR, ");
		sb.append("         NVL(ORG.BRANCH_NAME, ' ') AS M_BRANCH_NAME, ");
		sb.append("         M.AO_EMP_NAME, ");
		sb.append("         M.AO_EMP_CID AS M_AO_EMP_CID, ");
		sb.append("         M.KIND_TYPE, ");
		sb.append("         P_1.PARAM_NAME AS M_KIND_NAME, ");
		sb.append("         TRUNC(M.TX_AMT_TWD) AS M_TX_AMT_TWD, ");
		sb.append("         ROUND(M.BAL_TWD_ME, 0) AS BAL_TWD_ME, ");
		sb.append("         NVL((SELECT COUNT(*) AS COUNTS ");
		sb.append("              FROM TBPMS_HIGH_RISK_INV_D_REP D ");
		sb.append("              WHERE D.FIRSTUPDATE IS NOT NULL ");
		sb.append("              AND M.SNAP_YYYYMM = D.SNAP_YYYYMM ");
		sb.append("              AND M.AO_BRANCH_NBR = D.AO_BRANCH_NBR ");
		sb.append("              AND M.AO_EMP_CID = D.AO_EMP_ID ");
		sb.append("              AND M.KIND_TYPE = D.KIND_TYPE ");
		sb.append("              GROUP BY D.SNAP_YYYYMM, D.AO_BRANCH_NBR, D.AO_EMP_ID), 0) AS RESPONDING, ");
		sb.append("         NVL((SELECT COUNT(*) AS COUNTS ");
		sb.append("              FROM TBPMS_HIGH_RISK_INV_D_REP D ");
		sb.append("              WHERE D.FIRSTUPDATE IS NULL ");
		sb.append("              AND M.SNAP_YYYYMM = D.SNAP_YYYYMM ");
		sb.append("              AND M.AO_BRANCH_NBR = D.AO_BRANCH_NBR ");
		sb.append("              AND M.AO_EMP_CID = D.AO_EMP_ID ");
		sb.append("              AND M.KIND_TYPE = D.KIND_TYPE ");
		sb.append("              GROUP BY D.SNAP_YYYYMM, D.AO_BRANCH_NBR, D.AO_EMP_ID), 0) AS NOT_RESPONDING, ");
		sb.append("          D.SNAP_YYYYMM AS D_SNAP_YYYYMM, ");
		sb.append("          TO_CHAR(D.TX_DATE, 'YYYY/MM/DD') AS TX_DATE, ");
		sb.append("          NVL(M.AO_BRANCH_NBR, ' ') AS D_BRANCH_NBR, ");
		sb.append("          NVL(ORG.BRANCH_NAME, ' ') AS D_BRANCH_NAME, ");
		sb.append("          D.AO_NAME, ");
		sb.append("          M.AO_EMP_CID AS D_AO_EMP_CID, ");
		sb.append("          M.AO_EMP_ID, ");
		sb.append("          D.ACCT_NBR_ORI, ");
		sb.append("          P_2.PARAM_NAME AS DRCR_NAME, ");
		sb.append("          P_1.PARAM_NAME AS D_KIND_NAME, ");
		sb.append("          D.STCK_CSTDY_CD, ");
		sb.append("          D.CUR, ");
		sb.append("          TRUNC(D.TX_AMT_TWD) AS D_TX_AMT_TWD, ");
		sb.append("          D.JRNL_NO, ");
		sb.append("          D.REMK, ");
		sb.append("          D.MEMO, ");
		sb.append("          D.NOTE, ");
		sb.append("          D.HR_ATTR, ");
		sb.append("          D.FIRSTUPDATE, ");
		sb.append("          D.MODIFIER, ");
		sb.append("          D.LASTUPDATE ");
		sb.append("  FROM TBPMS_HIGH_RISK_INV_D_REP D ");
		sb.append("  LEFT JOIN TBPMS_HIGH_RISK_INV_M_REP M ON D.SNAP_YYYYMM = M.SNAP_YYYYMM AND D.AO_BRANCH_NBR = M.AO_BRANCH_NBR AND D.AO_EMP_ID = M.AO_EMP_CID AND D.KIND_TYPE = M.KIND_TYPE ");
		sb.append("  LEFT JOIN TBPMS_ORG_REC_N ORG ON M.AO_BRANCH_NBR = ORG.DEPT_ID AND TO_DATE(M.SNAP_YYYYMM || '01', 'YYYYMMDD') BETWEEN ORG.START_TIME AND ORG.END_TIME ");
		sb.append("  LEFT JOIN TBSYSPARAMETER P_1 ON P_1.PARAM_TYPE = 'PMS.HIGH_RISK_INV_TYPE' AND P_1.PARAM_CODE = M.KIND_TYPE ");
		sb.append("  LEFT JOIN TBSYSPARAMETER P_2 ON P_2.PARAM_TYPE = 'PMS.HIGH_RISK_INV_DRCR' AND P_2.PARAM_CODE = D.DRCR ");
		sb.append("  LEFT JOIN TBPMS_EMPLOYEE_REC_N MEM ON M.AO_EMP_ID = MEM.EMP_ID AND M.AO_BRANCH_NBR = MEM.DEPT_ID AND TO_DATE(M.SNAP_YYYYMM || '01', 'YYYYMMDD') BETWEEN MEM.START_TIME AND MEM.END_TIME ");

		sb.append("  WHERE 1 = 1 ");
		
		// 統計月份
		if (StringUtils.isNotEmpty(inputVO.getsCreDate())) {
			sb.append("  AND M.YYYYMM = :sDate ");
			queryCondition.setObject("sDate", inputVO.getsCreDate());
		}

		if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("uhrm") < 0) { 		
			if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
				sb.append("  AND M.AO_BRANCH_NBR = :branch ");
				queryCondition.setObject("branch", inputVO.getBranch_nbr());
			} else if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
				sb.append("  AND M.AO_BRANCH_NBR IN (SELECT BRANCH_NBR FROM VWORG_DEFN_BRH WHERE DEPT_ID = :area) ");
				queryCondition.setObject("area", inputVO.getBranch_area_id());
			} else if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
				sb.append("  AND M.AO_BRANCH_NBR IN (SELECT BRANCH_NBR FROM VWORG_DEFN_BRH WHERE DEPT_ID = :region) ");
				queryCondition.setObject("region", inputVO.getRegion_center_id());
			}
			
			if (!headmgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE)) || 
				!armgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
				sb.append("AND D.RM_FLAG = 'B' ");
			}
		} else {
			if (StringUtils.isNotBlank(inputVO.getUhrmOP())) {
				sb.append("AND (");
				sb.append("     EXISTS ( SELECT 1 FROM TBORG_MEMBER MT WHERE M.AO_EMP_ID = MT.EMP_ID AND MT.DEPT_ID = :uhrmOP ) ");
				sb.append("  OR MEM.E_DEPT_ID = :uhrmOP ");
				sb.append(")");
				queryCondition.setObject("uhrmOP", inputVO.getUhrmOP());
			}
			
			sb.append("AND D.RM_FLAG = 'U' ");
		}
		
		// 行員ID
		if (StringUtils.isNotEmpty(inputVO.getAoEmpCID())) {
			sb.append("  AND M.AO_EMP_CID = :aoEmpCID ");
			queryCondition.setObject("aoEmpCID", inputVO.getAoEmpCID());
		}
		
		// 類別
		if (StringUtils.isNotEmpty(inputVO.getHighRiskInvType())) {
			sb.append("  AND M.KIND_TYPE = :kindType ");
			queryCondition.setObject("kindType", inputVO.getHighRiskInvType());
		}
		
		sb.append(") BASE ");
		
		// 已回覆/未回覆
		if (StringUtils.isNotEmpty(inputVO.getNoteStatus())) {
			switch (inputVO.getNoteStatus()) {
				case "01":
					sb.append("AND BASE.RESPONDING > 0 ");
					break;
				case "02":
					sb.append("AND BASE.NOT_RESPONDING > 0 ");
					break;
			}
		}
		
		sb.append("ORDER BY BASE.M_CREATETIME DESC, NVL(BASE.M_BRANCH_NBR, ' '), BASE.AO_EMP_ID, BASE.KIND_TYPE, BASE.TX_DATE ");

		queryCondition.setQueryString(sb.toString());

		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		// === start ===
		SimpleDateFormat sdfYYYYMMDDHHMM = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		SimpleDateFormat sdfYYYYMM = new SimpleDateFormat("yyyyMM");

		String reportName = "客戶經理高風險月報";
		String fileName = reportName + "_" + sdfYYYYMM.format(new Date(Long.parseLong(inputVO.getsCreDate()))) + ".xlsx";
		String uuid = UUID.randomUUID().toString();
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);

		String filePath = Path + uuid;

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet(reportName);
		sheet.setDefaultColumnWidth(20);
		sheet.setDefaultRowHeightInPoints(20);

		// 表頭 CELL型式
		XSSFCellStyle headingStyle = workbook.createCellStyle();
		headingStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		headingStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		headingStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);// 填滿顏色
		headingStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		headingStyle.setBorderBottom((short) 1);
		headingStyle.setBorderTop((short) 1);
		headingStyle.setBorderLeft((short) 1);
		headingStyle.setBorderRight((short) 1);
		headingStyle.setWrapText(true);

		// 資料 CELL型式
		XSSFCellStyle mainStyle = workbook.createCellStyle();
		mainStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		mainStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		mainStyle.setBorderBottom((short) 1);
		mainStyle.setBorderTop((short) 1);
		mainStyle.setBorderLeft((short) 1);
		mainStyle.setBorderRight((short) 1);
		
		// 資料 CELL型式
		XSSFCellStyle titleStyle = workbook.createCellStyle();
		titleStyle.setAlignment(XSSFCellStyle.ALIGN_LEFT);
		titleStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		titleStyle.setBorderBottom((short) 1);
		titleStyle.setBorderTop((short) 1);
		titleStyle.setBorderLeft((short) 1);
		titleStyle.setBorderRight((short) 1);
		
		String[] headerLine = { "私銀註記", 
								"交易統計年月", 
								"行員歸屬行", 
								"行員姓名", 
								"行員ID", 
								"類別", 
								"累積異動金額", 
								"已回覆筆數", 
								"未回覆筆數",
								"資料日期(系統日期)",
								"交易日期", 
								"行員歸屬行", 
								"行員姓名", 
								"行員ID", 
								"行員員編", 
								"帳號",
								"轉入/轉出",
								"類別", 
								"幣別", 
								"交易金額", 
								"交易序號", 
								"備註", 
								"初判異常轉法遵部調查", 
								"備註(如有異常必填入說明)", 
								"首次建立時間",
								"最新異動人員",
								"最新異動日期"};
		
		String[] mainLine   = { "RM_FLAG", 
								"M_SNAP_YYYYMM", 
								"M_BRANCH_NBR", 
								"AO_EMP_NAME", 
								"M_AO_EMP_CID", 
								"M_KIND_NAME", 
								"M_TX_AMT_TWD", 
								"RESPONDING", 
								"NOT_RESPONDING",
								"D_SNAP_YYYYMM",
								"TX_DATE", 
								"D_BRANCH_NBR", 
								"AO_NAME", 
								"D_AO_EMP_CID", 
								"AO_EMP_ID",
								"ACCT_NBR_ORI", 
								"DRCR_NAME",
								"D_KIND_NAME", 
								"CUR", 
								"D_TX_AMT_TWD", 
								"JRNL_NO", 
								"REMK",
								"HR_ATTR", 
								"NOTE", 
								"FIRSTUPDATE", 
								"MODIFIER", 
								"LASTUPDATE"};

		Integer index = 0;

		XSSFRow row = sheet.createRow(index);
		for (int i = 0; i < 1; i++) {
			XSSFCell cell = row.createCell(i);
			cell.setCellStyle(titleStyle);
			cell.setCellValue(reportName);
		}
		
		index++;
		
		row = sheet.createRow(index);
		for (int i = 0; i < headerLine.length; i++) {
			XSSFCell cell = row.createCell(i);
			cell.setCellStyle(headingStyle);
			cell.setCellValue(headerLine[i]);
		}

		index++;
		
		for (Map<String, Object> map : list) {
			row = sheet.createRow(index);

			for (int i = 0; i < mainLine.length; i++) {
				XSSFCell cell = row.createCell(i);
				cell.setCellStyle(mainStyle);

				switch (mainLine[i]) {
					case "FIRSTUPDATE":	
					case "LASTUPDATE":
						cell.setCellValue(formatDate(map.get(mainLine[i]), sdfYYYYMMDDHHMM));
						break;
					case "D_BRANCH_NBR":
					case "M_BRANCH_NBR":
						cell.setCellValue(format("%s-%s", defaultString((String) map.get(mainLine[i])), defaultString((String) map.get("M_BRANCH_NAME"))));
						break;
					case "M_AO_EMP_CID":	// 身分證號（高風險隱蔽資料）
					case "D_AO_EMP_CID":
						cell.setCellValue(getCustIdMaskForHighRisk(defaultString((String) map.get(mainLine[i]))));
						break;
					case "M_TX_AMT_TWD":
					case "BAL_TWD_ME":
					case "D_TX_AMT_TWD":
						if (null != map.get(mainLine[i])) {
							cell.setCellValue(new DecimalFormat("#,##0.##").format(map.get(mainLine[i])));
						} else {
							cell.setCellValue("0");
						}

						break;
					case "RESPONDING":
					case "NOT_RESPONDING":
						if (null != map.get(mainLine[i])) {
							cell.setCellValue(new DecimalFormat("#,##0").format(map.get(mainLine[i])));
						} else {
							cell.setCellValue("0");
						}

						break;
					case "HR_ATTR":		// 初判異常轉法遵部調查 Y: 是、N: 否
						cell.setCellValue(formatFlag(defaultString((String) map.get(mainLine[i])))); // 初判異常轉法遵部調查 Y: 是、N: 否
						break;
					case "RECORD_SEQ":
						cell.setCellValue(checkIsNull(map, mainLine[i]) + "");
						break;
					default:
						if (null != map.get(mainLine[i])) {
							cell.setCellValue(defaultString(map.get(mainLine[i]).toString()));
						} else {
							cell.setCellValue("");
						}
						break;
				}
			}
			
			index++;
		}

		workbook.write(new FileOutputStream(filePath));

		notifyClientToDownloadFile(DataManager.getSystem().getPath().get("temp").toString() + uuid, fileName);

		sendRtnObject(null);
	}

	// 格式時間
	private String formatDate(Object date, SimpleDateFormat sdf) {

		if (date != null)
			return sdf.format(date);
		else
			return "";
	}

	// Flag Y="是"，N="否"
	private String formatFlag(String flag) {

		return "Y".equals(flag) ? "是" : "N".equals(flag) ? "否" : "";
	}
	
	private String checkIsNull(Map map, String key) {

		if (StringUtils.isNotBlank(ObjectUtils.toString(map.get(key)))) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}
}
