package com.systex.jbranch.app.server.fps.pms496;

import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

import com.ibm.icu.math.BigDecimal;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.jlb.DataFormat;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("pms496")
@Scope("prototype")
public class PMS496 extends FubonWmsBizLogic {

	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

	DataAccessManager dam = null;
	
	public void queryData(Object body, IPrimitiveMap header) throws Exception {
		
		PMS496OutputVO outputVO = new PMS496OutputVO();
		outputVO = this.queryData(body);

		sendRtnObject(outputVO);
	}
	
	// 查詢
	public PMS496OutputVO queryData(Object body) throws JBranchException {
		
		initUUID();
		SimpleDateFormat sdfYYYYMM = new SimpleDateFormat("yyyyMM");

		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> fcMap = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2); // 理專
		Map<String, String> psopMap = xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2); // OP
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行人員
		Map<String, String> armgrMap   = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2);	//處長

		PMS496InputVO inputVO = (PMS496InputVO) body;
		PMS496OutputVO outputVO = new PMS496OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		String loginRoleID = null != inputVO.getSelectRoleID() ? inputVO.getSelectRoleID() : (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);

		sb.append("SELECT ROWNUM, BASE.* ");
		sb.append("FROM ( ");
		sb.append("  SELECT T.* ");
		sb.append("  FROM ( ");
		sb.append("    SELECT CASE WHEN A.RM_FLAG = 'U' THEN 'Y' ELSE 'N' END AS RM_FLAG, ");
		sb.append("           A.YEARMON, ");
		sb.append("           'BOND' AS PROD_TYPE, ");
		sb.append("           CASE WHEN TRUNC(A.CREATETIME) <= TRUNC(TO_DATE('20230630', 'YYYYMMDD')) THEN 'N' ELSE 'Y' END AS RECORD_YN, ");
		sb.append("           A.BRANCH_NBR, ");
		sb.append("           BRH.BRANCH_NAME, ");
		sb.append("           BRH.BRANCH_AREA_NAME, ");
		sb.append("           BRH.BRANCH_AREA_ID, ");
		sb.append("           BRH.REGION_CENTER_NAME, ");
		sb.append("           BRH.REGION_CENTER_ID, ");
		sb.append("           A.INV_DATE, ");
		sb.append("           TO_CHAR(A.INV_DATE, 'YYYY/MM/DD') AS RPT_INV_DATE, ");
		sb.append("           A.REF_DATE, ");
		sb.append("           TO_CHAR(A.REF_DATE, 'YYYY/MM/DD') AS RPT_REF_DATE, ");
		sb.append("           TO_CHAR(A.REF_DATE, 'YYYYMMDD') AS KEY_REF_DATE, ");
		sb.append("           A.CERT_NBR, ");
		sb.append("           A.BOND_NBR AS PRD_ID, ");
		sb.append("           BOND.BOND_CNAME_A AS PRD_NAME, ");
		sb.append("           A.CUR_COD, ");
		sb.append("           A.REF_AMT, ");
		sb.append("           A.INV_AMT, ");
		sb.append("           A.MANAGE_FEE, ");
		sb.append("           A.INV_FEE, ");
		sb.append("           A.CHANNEL_FEE, ");
		sb.append("           (A.DIV_AMT - A.INV_FRONT_FEE + A.REF_FRONT_FEE) AS DIV_AMT, ");
		sb.append("           CASE WHEN A.INV_AMT <> 0 THEN (ROUND((A.REF_AMT + A.DIV_AMT - A.INV_FRONT_FEE + A.REF_FRONT_FEE - A.INV_AMT - A.MANAGE_FEE - A.INV_FEE) / A.INV_AMT * 100, 2)) ELSE 0 END AS PROFIT_RATE, ");
		sb.append("           CASE WHEN A.TRAN_SRC = 'WEB' THEN '網銀' WHEN A.TRAN_SRC = 'MOB' THEN '行銀' ELSE '臨櫃' END AS TRAN_SRC, ");
		sb.append("           A.CUST_ID, ");
		sb.append("           PAR.PARAM_NAME AS CUST_ATT, ");
		sb.append("           CUS.CUST_NAME, ");
		sb.append("           A.EMP_ID, ");
		sb.append("           MEM.EMP_NAME, ");
		sb.append("           A.AO_CODE, ");
		sb.append("           A.CHANNEL_FEE_TWD, ");
		sb.append("           A.INV_FEE_TWD, ");
		sb.append("           A.NOTE, ");
		sb.append("           A.NOTE2, ");
		sb.append("           A.HR_ATTR, ");
		sb.append("           A.FIRSTUPDATE, ");
		sb.append("           A.MODIFIER, ");
		sb.append("           A.LASTUPDATE, ");
		sb.append("           CASE WHEN A.BUY_TRAN_SRC = 'WEB' THEN '網銀' WHEN A.BUY_TRAN_SRC = 'MOB' THEN '行銀' ELSE '臨櫃' END AS BUY_TRAN_SRC, ");
		sb.append("           A.NOTE_TYPE, ");
		sb.append("           A.RECORD_SEQ, ");
		sb.append("           NULL AS TRANS_DAYS, ");
//		sb.append("           A.RM_FLAG, ");
		sb.append("           CASE WHEN A.CUST_AGE >= 65 THEN A.CUST_AGE ELSE '' END AS CUST_AGE ");
		sb.append("    FROM TBPMS_SHORT_BOND A ");
		sb.append("    LEFT JOIN TBPRD_BOND BOND ON A.BOND_NBR = BOND.PRD_ID ");
		sb.append("    LEFT JOIN VWORG_DEFN_INFO BRH ON A.BRANCH_NBR = BRH.BRANCH_NBR ");
		sb.append("    LEFT JOIN TBCRM_CUST_MAST CUS ON A.CUST_ID = CUS.CUST_ID ");
		sb.append("    LEFT JOIN TBORG_MEMBER MEM ON A.EMP_ID = MEM.EMP_ID ");
		sb.append("    LEFT JOIN TBSYSPARAMETER PAR ON CUS.VIP_DEGREE = PAR.PARAM_CODE	AND	PAR.PARAM_TYPE = 'CRM.VIP_DEGREE' ");
		sb.append("    WHERE 1 = 1 ");
				       
		sb.append("    UNION ");
				     
		sb.append("    SELECT CASE WHEN A.RM_FLAG = 'U' THEN 'Y' ELSE 'N' END AS RM_FLAG, ");
		sb.append("           A.YEARMON, ");
		sb.append("           'FUND' AS PROD_TYPE, ");
		sb.append("           CASE WHEN TRUNC(A.CREATETIME) <= TRUNC(TO_DATE('20230630', 'YYYYMMDD')) THEN 'N' ELSE 'Y' END AS RECORD_YN, ");
		sb.append("           A.BRANCH_NBR, ");
		sb.append("           A.BRANCH_NAME, ");
		sb.append("           A.BRANCH_AREA_NAME, ");
		sb.append("           A.BRANCH_AREA_ID, ");
		sb.append("           A.REGION_CENTER_NAME,  ");
		sb.append("           A.REGION_CENTER_ID, ");
		sb.append("           TO_DATE(SUBSTR(A.PRCH_DATE, 1, 4) || '/' || SUBSTR(A.PRCH_DATE, 5, 2) || '/' || SUBSTR(A.PRCH_DATE, 7, 2), 'YYYY/MM/DD') AS INV_DATE, ");
		sb.append("           SUBSTR(A.PRCH_DATE, 1, 4) || '/' || SUBSTR(A.PRCH_DATE, 5, 2) || '/' || SUBSTR(A.PRCH_DATE, 7, 2) AS RPT_INV_DATE, ");
		sb.append("           TO_DATE(SUBSTR(A.RDMP_DATE, 1, 4) || '/' || SUBSTR(A.RDMP_DATE, 5, 2) || '/' || SUBSTR(A.RDMP_DATE, 7, 2), 'YYYY/MM/DD') AS REF_DATE, ");
		sb.append("           SUBSTR(A.RDMP_DATE, 1, 4) || '/' || SUBSTR(A.RDMP_DATE, 5, 2) || '/' || SUBSTR(A.RDMP_DATE, 7, 2) AS RPT_REF_DATE, ");
		sb.append("           A.RDMP_DATE AS KEY_REF_DATE, ");
		sb.append("           A.CERT_NBR, ");
		sb.append("           A.PRD_ID, ");
		sb.append("           A.PRD_NAME, ");
		sb.append("           A.CRCY_TYPE, ");
		sb.append("           A.RDMP_AMT AS REF_AMT, ");
		sb.append("           A.ACT_AMT AS INV_AMT, ");
		sb.append("           NULL AS MANAGE_FEE, ");
		sb.append("           A.FEE AS INV_FEE, ");
		sb.append("           0 AS CHANNEL_FEE, ");
		sb.append("           A.INT_AMT AS DIV_AMT, ");
		sb.append("           A.ROR AS PROFIT_RATE, ");
		sb.append("           B.PARAM_NAME AS TRADE_SRC, ");
		sb.append("           A.CUST_ID, ");
		sb.append("           A.CUST_ATTR, ");
		sb.append("           A.CUST_NAME, ");
		sb.append("           A.EMP_ID, ");
		sb.append("           A.EMP_NAME, ");
		sb.append("           A.AO_CODE, ");
		sb.append("           NULL AS CHANNEL_FEE_TWD, ");
		sb.append("           NULL AS INV_FEE_TWD, ");
		sb.append("           A.NOTE, ");
		sb.append("           A.NOTE2, ");
		sb.append("           A.HR_ATTR, ");
		sb.append("           A.FIRSTUPDATE, ");
		sb.append("           A.MODIFIER, ");
		sb.append("           A.LASTUPDATE, ");
		sb.append("           '' AS BUY_TRAN_SRC, ");
		sb.append("           A.NOTE_TYPE, ");
		sb.append("           A.RECORD_SEQ, ");
		sb.append("           A.TRANS_DAYS, ");
//		sb.append("           A.RM_FLAG, ");
		sb.append("           CASE WHEN A.CUST_AGE >= 65 THEN A.CUST_AGE ELSE '' END AS CUST_AGE ");
		sb.append("    FROM TBPMS_SHORT_TRAN A ");
		sb.append("    LEFT JOIN TBSYSPARAMETER B ON PARAM_TYPE = 'PMS.SHORT_TRADE_TRANS_SRC' AND B.PARAM_CODE = A.TRADE_SRC ");
		sb.append("    WHERE 1 = 1 ");
		sb.append("    AND A.DATA_DATE IS NOT NULL ");
		sb.append("  ) T ");
		sb.append("  LEFT JOIN TBPMS_EMPLOYEE_REC_N MEM ON T.EMP_ID = MEM.EMP_ID AND T.BRANCH_NBR = MEM.DEPT_ID AND TO_DATE(T.YEARMON || '01', 'YYYYMMDD') BETWEEN MEM.START_TIME AND MEM.END_TIME ");
		sb.append("  WHERE 1 = 1 ");
		
		// 由工作首頁 CRM181 過來，只須查詢主管尚未確認資料 & 報酬率為負值
		if (StringUtils.equals("Y", inputVO.getFrom181())) {
			if (StringUtils.isNotBlank(inputVO.getImportSDate())) {
    			sb.append("  AND T.YEARMON >= :yearMonS ");
    			queryCondition.setObject("yearMonS", inputVO.getImportSDate());
    		}
			
			if (StringUtils.isNotBlank(inputVO.getImportEDate())) {
    			sb.append("  AND T.YEARMON <= :yearMonE ");
    			queryCondition.setObject("yearMonE", inputVO.getImportEDate());
    		}
        } else {
    		//資料月份
    		if (StringUtils.isNotBlank(inputVO.getImportSDate())) {
    			sb.append("  AND T.YEARMON >= :yearMonS ");
    			queryCondition.setObject("yearMonS", sdfYYYYMM.format(new Date(Long.parseLong(inputVO.getImportSDate()))));
    		}
    		
    		if (StringUtils.isNotBlank(inputVO.getImportEDate())) {
    			sb.append("  AND T.YEARMON <= :yearMonE ");
    			queryCondition.setObject("yearMonE", sdfYYYYMM.format(new Date(Long.parseLong(inputVO.getImportEDate()))));
    		}
        }


		if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("uhrm") < 0) {
			// 登入為銷售人員強制加AO_CODE
			if (fcMap.containsKey(loginRoleID) || psopMap.containsKey(loginRoleID)) {
				sb.append("  AND T.AO_CODE IN :aoCodeList ");
				queryCondition.setObject("aoCodeList", getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST));
			}
			
			if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
				sb.append("  AND T.BRANCH_NBR = :branchNbr ");
				queryCondition.setObject("branchNbr", inputVO.getBranch_nbr());
			} else if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {	
				sb.append("  AND ( ");
				sb.append("    (T.RM_FLAG = 'B' AND T.BRANCH_AREA_ID = :branchAreaID) ");
				
				if (headmgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE)) ||
					armgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
					sb.append("    OR (T.RM_FLAG = 'U' AND EXISTS ( SELECT 1 FROM TBORG_MEMBER MT WHERE T.EMP_ID = MT.EMP_ID AND MT.DEPT_ID = :branchAreaID )) ");
				}
			
				sb.append("  ) ");
				queryCondition.setObject("branchAreaID", inputVO.getBranch_area_id());
			} else if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
				sb.append("  AND T.REGION_CENTER_ID = :regionCenterID ");
				queryCondition.setObject("regionCenterID", inputVO.getRegion_center_id());
			}
			
			if (!headmgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE)) && 
				!armgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
				sb.append("  AND T.RM_FLAG = 'N' ");
			}
		} else {
			if (StringUtils.isNotBlank(inputVO.getUhrmOP())) {
				sb.append("  AND (");
				sb.append("       EXISTS ( SELECT 1 FROM TBORG_MEMBER MT WHERE T.EMP_ID = MT.EMP_ID AND MT.DEPT_ID = :uhrmOP ) ");
				sb.append("    OR MEM.E_DEPT_ID = :uhrmOP ");
				sb.append("  )");
				queryCondition.setObject("uhrmOP", inputVO.getUhrmOP());
			}
			
			sb.append("  AND T.RM_FLAG = 'Y' ");
		}
		
		// 理專
		if (inputVO.getAoCode() != null && !"".equals(inputVO.getAoCode())) {
			sb.append("  AND AO_CODE = :aoCode ");
			queryCondition.setObject("aoCode", inputVO.getAoCode());
		}
		
		sb.append("  ORDER BY T.YEARMON, ");
		sb.append("           CASE WHEN T.PROFIT_RATE < 0 THEN 0 ELSE 1 END ASC, ");
		sb.append("           T.BRANCH_NBR, ");
		sb.append("           T.CUST_ID, ");
		sb.append("           T.PROFIT_RATE ASC, ");
		sb.append("           T.INV_DATE ");
		sb.append(") BASE ");

		sb.append("WHERE 1 = 1 ");
		
		// 由工作首頁 CRM181 過來，只須查詢主管尚未確認資料 & 報酬率為負值
		if (StringUtils.equals("Y", inputVO.getFrom181())) {
			sb.append("AND FIRSTUPDATE IS NULL ");
			sb.append("AND PROFIT_RATE < 0 ");
        }
		
		// #0001590_WMS-CR-20230807-01_擬優化各項內控報表相關功能
 		if (StringUtils.isNotEmpty(inputVO.getNoteStatus())) {
 			switch (inputVO.getNoteStatus()) {
 				case "01":
 					sb.append("AND FIRSTUPDATE IS NOT NULL ");
 					break;
 				case "02":
 					sb.append("AND FIRSTUPDATE IS NULL ");
 					break;
 			}
 		}
 		
 		if (StringUtils.isNotEmpty(inputVO.getCustID())) {
			sb.append("AND CUST_ID LIKE :CUST_ID ");
			queryCondition.setObject("CUST_ID", "%" + inputVO.getCustID().toUpperCase() + "%");
		}
		
		if (StringUtils.isNotEmpty(inputVO.getPRD_TYPE())) {
			sb.append("AND PROD_TYPE = :PRD_TYPE ");
			queryCondition.setObject("PRD_TYPE", inputVO.getPRD_TYPE());
		}
		
		// 進出頻率
		if (StringUtils.isNotEmpty(inputVO.getPRD_TYPE())) {
			switch (inputVO.getPRD_TYPE()) {
				case "FUND":
					if (StringUtils.isNotEmpty(inputVO.getFreqType())) {
						switch (inputVO.getFreqType()) {
							case "1":
								sb.append("  AND TRANS_DAYS <= 7 ");
								break;
							case "2":
								sb.append("  AND TRANS_DAYS >= 8 ");
								sb.append("  AND TRANS_DAYS <= 30 ");
								break;
						}
					}
					break;
				case "BOND":
					if (StringUtils.isNotEmpty(inputVO.getFreqType())) {
						switch (inputVO.getFreqType()) {
							case "1":
								sb.append("AND INV_DATE >= ADD_MONTHS(REF_DATE, -3) ");
								break;
							case "2":
								sb.append("AND INV_DATE >= ADD_MONTHS(REF_DATE, -6) AND INV_DATE < ADD_MONTHS(REF_DATE, -3) ");
								break;
						}
					}
					break;
			}
		}

		// 海外債預設
		sb.append("AND INV_DATE >= ADD_MONTHS(REF_DATE, -6) ");

		queryCondition.setQueryString(sb.toString());

		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		outputVO.setTotalList(list);
		outputVO.setResultList(list);
		
		return outputVO;
	}

	// 更新資料，在前端篩選編輯過的資料。
	public void save(Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS496InputVO inputVO = (PMS496InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		for (Map<String, Object> map : inputVO.getList()) {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			
			switch (map.get("PROD_TYPE").toString()) {
				case "FUND":
					sb.append("UPDATE TBPMS_SHORT_TRAN ");
					sb.append("SET HR_ATTR = :hrAttr, ");
					sb.append("    NOTE_TYPE = :noteType, ");
					sb.append("    NOTE = :note, ");
					sb.append("    NOTE2 = :note2, ");
					sb.append("    RECORD_SEQ = :recordSEQ, ");
					sb.append("    MODIFIER = :modifier, ");
					sb.append("    LASTUPDATE = sysdate ");
					
					if (null == map.get("FIRSTUPDATE")) {
						sb.append("    , FIRSTUPDATE = sysdate ");
					}
					
					sb.append("WHERE YEARMON = :dataDate ");
					sb.append("AND BRANCH_NBR = :branchNBR ");
					sb.append("AND CERT_NBR = :certNBR ");
					
					// KEY
					queryCondition.setObject("dataDate", map.get("YEARMON"));
					queryCondition.setObject("branchNBR", map.get("BRANCH_NBR"));
					queryCondition.setObject("certNBR", map.get("CERT_NBR"));
					
					// CONTENT
					queryCondition.setObject("hrAttr", map.get("HR_ATTR"));
					queryCondition.setObject("noteType", map.get("NOTE_TYPE"));
					queryCondition.setObject("note", checkIsNull(map, "NOTE")); //map.get("NOTE"));
					queryCondition.setObject("note2", checkIsNull(map, "NOTE2")); //map.get("NOTE2"));
					queryCondition.setObject("recordSEQ", checkIsNull(map, "RECORD_SEQ")); //map.get("RECORD_SEQ"));
					queryCondition.setObject("modifier", DataManager.getWorkStation(uuid).getUser().getCurrentUserId());
					
					queryCondition.setQueryString(sb.toString());

					dam.exeUpdate(queryCondition);
					
					break;
				case "BOND":
					sb.append("UPDATE TBPMS_SHORT_BOND ");
					sb.append("SET HR_ATTR = :hrAttr, ");
					sb.append("    NOTE_TYPE = :noteType, ");
					sb.append("    NOTE = :note, ");
					sb.append("    NOTE2 = :note2, ");
					sb.append("    RECORD_SEQ = :recordSEQ, ");
					sb.append("    MODIFIER = :modifier, ");
					sb.append("    LASTUPDATE = sysdate ");
					
					if (null == map.get("FIRSTUPDATE")) {
						sb.append("    , FIRSTUPDATE = sysdate ");
					}
					
					sb.append("WHERE CERT_NBR = :certNBR ");
					sb.append("AND CUST_ID = :custID ");
					sb.append("AND TO_CHAR(REF_DATE, 'YYYYMMDD') = :refDate ");
					sb.append("AND YEARMON = :yearMon ");
	
					// KEY
					queryCondition.setObject("certNBR", map.get("CERT_NBR"));
					queryCondition.setObject("custID", map.get("CUST_ID"));
					queryCondition.setObject("refDate", map.get("KEY_REF_DATE"));
					queryCondition.setObject("yearMon", map.get("YEARMON"));
					
					// CONTENT
					queryCondition.setObject("hrAttr", map.get("HR_ATTR"));
					queryCondition.setObject("noteType", map.get("NOTE_TYPE"));
					queryCondition.setObject("note", checkIsNull(map, "NOTE")); //map.get("NOTE"));
					queryCondition.setObject("note2", checkIsNull(map, "NOTE2")); //map.get("NOTE2"));
					queryCondition.setObject("recordSEQ", checkIsNull(map, "RECORD_SEQ")); //map.get("RECORD_SEQ"));
					queryCondition.setObject("modifier", DataManager.getWorkStation(uuid).getUser().getCurrentUserId());
					
					queryCondition.setQueryString(sb.toString());

					dam.exeUpdate(queryCondition);
					
					break;
			}
		}
		
		sendRtnObject(null);
	}
	
	// 匯出
	public void export(Object body, IPrimitiveMap header) throws Exception {

		XmlInfo xmlInfo = new XmlInfo();
		
		PMS496InputVO inputVO = (PMS496InputVO) body;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String fileName = "基金與海外債短線交易進出報表_" + sdf.format(new Date()) + ".xlsx";
		String uuid = UUID.randomUUID().toString();
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);

		String filePath = Path + uuid;

		List<Map<String, String>> reportList = inputVO.getExportList();

		String[] headerLine = { "序號", "私銀註記", "資料月份", 
				  				"分行代號", "分行名稱", 
				  				"交易類別",
				                "申購日期", "贖回日期", "憑證號碼", "產品代號", "商品名稱", 
				                "計價幣別", "參考贖回金額", "相關配息", "投資金額", 
				                "相關手續費(e.g 申購手續費、信管費、短線費)", 
				                "通路服務費", "報酬率", "客戶ID", "客戶姓名", "高齡客戶",
				                "員工編號", "員工姓名", "AO CODE", 
	                            "專員是否勸誘客戶短線進出", "查證方式", "電訪錄音編號", "具體原因", "首次建立時間", "最新異動人員", "最新異動日期"};
		String[] mainLine   = { "ROWNUM", "RM_FLAG", "YEARMON", 
								"BRANCH_NBR", "BRANCH_NAME", 
								"PROD_TYPE",
				                "RPT_INV_DATE", "RPT_REF_DATE", "CERT_NBR", "PRD_ID", "PRD_NAME", 
				                "CUR_COD", "REF_AMT", "DIV_AMT", "INV_AMT", 
				                "INV_FEE", 
				                "CHANNEL_FEE", "PROFIT_RATE", "CUST_ID", "CUST_NAME", "CUST_AGE",
				                "EMP_ID", "EMP_NAME", "AO_CODE", 
	                            "HR_ATTR", "NOTE", "RECORD_SEQ", "NOTE2", "FIRSTUPDATE", "MODIFIER", "LASTUPDATE"};

		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet("基金與海外債短線交易進出報表_" + sdf.format(new Date()));
		sheet.setDefaultColumnWidth(20);
		sheet.setDefaultRowHeightInPoints(20);

		// 表頭 CELL型式
		XSSFCellStyle headingStyle = wb.createCellStyle();
		headingStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		headingStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		headingStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);// 填滿顏色
		headingStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		headingStyle.setBorderBottom((short) 1);
		headingStyle.setBorderTop((short) 1);
		headingStyle.setBorderLeft((short) 1);
		headingStyle.setBorderRight((short) 1);
		headingStyle.setWrapText(true);

		Integer index = 0; // first row

		// Heading
		XSSFRow row = sheet.createRow(index);

		row = sheet.createRow(index);
		row.setHeightInPoints(30);
		for (int i = 0; i < headerLine.length; i++) {
			XSSFCell cell = row.createCell(i);
			cell.setCellStyle(headingStyle);
			cell.setCellValue(headerLine[i]);
		}

		index++;

		// 資料 CELL型式
		XSSFCellStyle mainStyle = wb.createCellStyle();
		mainStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		mainStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		mainStyle.setBorderBottom((short) 1);
		mainStyle.setBorderTop((short) 1);
		mainStyle.setBorderLeft((short) 1);
		mainStyle.setBorderRight((short) 1);
		mainStyle.setWrapText(true);

		for (Map<String, String> map : reportList) {
			row = sheet.createRow(index);

			SimpleDateFormat timeSdf = new SimpleDateFormat("yyyy/MM/dd");
			for (int j = 0; j < mainLine.length; j++) {
				XSSFCell cell = row.createCell(j);
				cell.setCellStyle(mainStyle);
				
				String codeList = checkIsNull(map, mainLine[j]).replaceAll("\n|\r", "/");
				codeList = (codeList.indexOf("/") > -1) ? (codeList.substring(0, codeList.length() - 1)).replaceAll("/", "\n") : codeList;
				
				switch (mainLine[j]) {
					case "RPT_INV_DATE":
					case "RPT_REF_DATE":
						cell.setCellValue(checkIsNull(map, mainLine[j]));
						break;
					case "ROWNUM" :
						cell.setCellValue(((int) Double.parseDouble(checkIsNull(map, mainLine[j]).toString())) + ""); //序號
						break;
					case "NOTE":
						String note = (String) xmlInfo.getVariable("PMS.CHECK_TYPE", (String) map.get("NOTE_TYPE"), "F3");
						
						if (null != map.get("NOTE_TYPE") && StringUtils.equals("O", (String) map.get("NOTE_TYPE"))) {
							note = note + "：" + StringUtils.defaultString((String) map.get(mainLine[j]));
						}
						
						cell.setCellValue(note);
						break;
					case "PROD_TYPE":
						cell.setCellValue((String) xmlInfo.getVariable("PMS.SHORT_PRD_TYPE", (String) map.get(mainLine[j]), "F3"));
						break;
					case "INV_FEE":
						DecimalFormat df = new DecimalFormat("#,##0.00");
						BigDecimal fee = new BigDecimal(checkIsNull(map, "INV_FEE") + 0).add(new BigDecimal(checkIsNull(map, "MANAGE_FEE") + 0));
						cell.setCellValue(df.format(fee));
						break;
					case "REF_AMT":
					case "DIV_AMT":
					case "INV_AMT":
					case "CHANNEL_FEE":
						cell.setCellValue(currencyFormat(map, mainLine[j]));
						break;
					case "PROFIT_RATE":
						cell.setCellValue(currencyFormat(map, mainLine[j]) + "%");
						break;
					default :
						cell.setCellValue(checkIsNull(map, mainLine[j]));
						break;
				}
				
			}

			index++;
		}

		wb.write(new FileOutputStream(filePath));
		notifyClientToDownloadFile(DataManager.getSystem().getPath().get("temp").toString() + uuid, fileName);

		sendRtnObject(null);
	}

	// 檢查Map取出欄位是否為Null
	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
			if ("CUST_ID".equals(key)) {
				return DataFormat.getCustIdMaskForHighRisk(String.valueOf(map.get(key)));
			} else {
				return String.valueOf(map.get(key));
			}
		} else {
			return "";
		}
	}

	// 處理貨幣格式
	private String currencyFormat(Map map, String key) {
		
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
			DecimalFormat df = new DecimalFormat("#,##0.00");
			return df.format(new BigDecimal((String) map.get(key)));
		} else
			return "0.00";
	}
	
	// 格式時間
	private String formatDate(Object date, SimpleDateFormat sdf) {
		
		if (date != null)
			return sdf.format(date);
		else
			return "";
	}
}