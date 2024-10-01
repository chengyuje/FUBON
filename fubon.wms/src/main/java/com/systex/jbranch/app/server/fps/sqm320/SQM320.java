package com.systex.jbranch.app.server.fps.sqm320;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBSQM_RSA_MASTPK;
import com.systex.jbranch.app.common.fps.table.TBSQM_RSA_MASTVO;
import com.systex.jbranch.app.common.fps.table.TBSQM_RSA_RC_BRPK;
import com.systex.jbranch.app.common.fps.table.TBSQM_RSA_RC_BRVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.jlb.DataFormat;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.reportdata.ConfigAdapterIF;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @Description 客戶服務定期查核
 * @Author Willis
 * @date 20180510
 * @Author Eli
 * @date 20190801 匯出報表客戶 ID 欄位遮蔽處理
 *
 * @NOTE BY OCEAN
 * 1. 若增/刪/修資料表欄位，請同步更新PABTH_BTSQM904
 */

@Component("sqm320")
@Scope("request")
public class SQM320 extends FubonWmsBizLogic {

	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(SQM320.class);
	public final String SPECIAL_CUST = "1";
	public final String SC_CUST = "2";
	public final String KYC_UP_CUST = "3";

	SimpleDateFormat sdfYYYYMMDD  = new SimpleDateFormat("yyyy-MM-dd");
	
	/** 查詢資料 **/
	public void queryData(Object body, IPrimitiveMap header) throws JBranchException, ParseException {

		SQM320InputVO inputVO = (SQM320InputVO) body;
		SQM320OutputVO outputVO = new SQM320OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
		String loginID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);
		String loginBRH = (String) getUserVariable(FubonSystemVariableConsts.LOGINBRH);
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); // 總行人員
		Map<String, String> mbrmgrMap = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2); // 區督導
		Map<String, String> bmmgrMap = xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2); // 分行人員

		boolean headFlag = headmgrMap.containsKey(roleID);
		boolean mbrFlag = mbrmgrMap.containsKey(roleID);
		boolean bmFlag = bmmgrMap.containsKey(roleID);

		sql.append("SELECT * ");
		sql.append("FROM ( ");
		sql.append("  SELECT DISTINCT ");
		sql.append("         CM.CON_DEGREE, ");
		sql.append("         NVL(ROUND(MONTHS_BETWEEN(LAST_DAY(TO_DATE(:yearQtr || '01','YYYYMMDD')), CM.BIRTH_DATE) / 12, 0), 0) AS CUST_YEAR, ");
		sql.append("         CASE WHEN NVL(ROUND(MONTHS_BETWEEN(LAST_DAY(TO_DATE(:yearQtr || '01','YYYYMMDD')), CM.BIRTH_DATE) / 12, 0), 0) >= 65 THEN 'Y' ELSE 'N' END AS HIGH_YEAR_YN, ");
		sql.append("         EMP.EMP_NAME, ");
		sql.append("         EMP_AUDITOR.EMP_NAME AS EMP_AUDIT_NAME, ");
		sql.append("         EMP_REVIEW.EMP_NAME  AS EMP_REVIEW_NAME, ");
		sql.append("         ORG.BRANCH_NAME, ");
		sql.append("         MAST.*, ");
		sql.append("         CASE WHEN MAST.AUDITED = 'Y' AND MAST.REVIEW_DATE IS NOT NULL THEN 'N' ");
		sql.append("              WHEN MAST.AUDITED = 'Y' AND MAST.REVIEW_DATE IS NULL THEN 'Y' ");
		sql.append("         ELSE 'X' END AS REVIEW_YN, ");
		sql.append("         CASE WHEN NVL(CMU_P.P_COUNTS, 0) > 0 THEN 'Y' ELSE 'N' END AS CMU_P_YN, ");
		sql.append("         CASE WHEN MAST.REVIEWER_EMP_ID = :loginID THEN 'Y' ELSE 'N' END REVIEWER_FLAG, ");
		sql.append("         CASE WHEN MAST.BRANCH_NBR = :loginBRH THEN 'Y' ELSE 'N' END AUDITOR_FLAG ");
		sql.append("  FROM TBSQM_RSA_MAST MAST ");
		sql.append("  LEFT JOIN TBPMS_ORG_REC_N      ORG         ON ORG.DEPT_ID = MAST.BRANCH_NBR AND TRUNC(SYSDATE) BETWEEN ORG.START_TIME AND ORG.END_TIME ");
		sql.append("  LEFT JOIN TBPMS_EMPLOYEE_REC_N EMP         ON MAST.EMP_ID = EMP.EMP_ID AND LAST_DAY(TO_DATE(:yearQtr || '01', 'YYYYMMDD')) BETWEEN EMP.START_TIME AND EMP.END_TIME ");
		sql.append("  LEFT JOIN TBPMS_EMPLOYEE_REC_N EMP_REVIEW  ON EMP_REVIEW.EMP_ID = MAST.REVIEWER_EMP_ID AND TRUNC(SYSDATE) BETWEEN EMP_REVIEW.START_TIME AND EMP_REVIEW.END_TIME ");
		sql.append("  LEFT JOIN TBPMS_EMPLOYEE_REC_N EMP_AUDITOR ON EMP_AUDITOR.EMP_ID = MAST.AUDITOR AND TRUNC(SYSDATE) BETWEEN EMP_AUDITOR.START_TIME AND EMP_AUDITOR.END_TIME ");
		sql.append("  LEFT JOIN TBCRM_CUST_MAST CM ON MAST.CUST_ID = CM.CUST_ID ");
		sql.append("  LEFT JOIN ( ");
		sql.append("    SELECT CUST_ID, COUNT(CMU_TYPE) AS P_COUNTS ");
		sql.append("    FROM TBSQM_RSA_DTL ");
		sql.append("    WHERE CMU_TYPE = 'P' ");
		sql.append("    AND YEARQTR = :yearQtr ");
		sql.append("    GROUP BY CUST_ID ");
		sql.append("  ) CMU_P ON MAST.CUST_ID = CMU_P.CUST_ID ");
		sql.append("  WHERE MAST.YEARQTR = :yearQtr ");
		sql.append("  AND MAST.AUDIT_TYPE = '1' ");
		sql.append("  AND NVL(MAST.AUDITED, ' ') <> 'D' ");
		
		/*********** 歷史組織查詢條件篩選-START by willis ***********/
		// UHRM 或 其他FLAG人員(不含UHRM%主管)
		if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("uhrm") < 0) {
			if (StringUtils.isNotBlank(inputVO.getBranch_nbr()) && (bmFlag || headFlag)) {
				sql.append("  AND ORG.BRANCH_NBR = :BRNCH_NBRR ");
				condition.setObject("BRNCH_NBRR", inputVO.getBranch_nbr());
			} else if (StringUtils.isNotBlank(inputVO.getBranch_area_id()) && (mbrFlag || headFlag)) {
				sql.append("  AND ORG.BRANCH_AREA_ID = :BRANCH_AREA_IDD ");
				condition.setObject("BRANCH_AREA_IDD", inputVO.getBranch_area_id());

				//區域主管有選分行需加上區域+分行，避免看到歷史其他區的紀錄
				if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
					sql.append("  AND ORG.BRANCH_NBR = :BRNCH_NBRR ");
					condition.setObject("BRNCH_NBRR", inputVO.getBranch_nbr());
				}
			} else {
				if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
					sql.append("  AND ORG.REGION_CENTER_ID = :REGION_CENTER_IDD ");
					condition.setObject("REGION_CENTER_IDD", inputVO.getRegion_center_id());

					if (inputVO.getReview_status().equals("")) {
						sql.append("  AND MAST.AUDITED = 'Y' ");
					}

					//業務處主管有選分行需加上業務處+分行，避免看到歷史其他業務處分行的紀錄
					if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
						sql.append("  AND ORG.BRANCH_NBR = :BRNCH_NBRR ");
						condition.setObject("BRNCH_NBRR", inputVO.getBranch_nbr());
					}
					
					//業務處主管有選區需加上業務處+區，避免看到歷史其他業務處區的紀錄
					else if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
						sql.append("  AND ORG.BRANCH_AREA_ID = :BRANCH_AREA_IDD ");
						condition.setObject("BRANCH_AREA_IDD", inputVO.getBranch_area_id());
					}
				}
			}
			
			// 非總行 且 非UHRM，則排除UHRM
			if (!headFlag) {
				sql.append("  AND NOT EXISTS ( ");
				sql.append("    SELECT 1 ");
				sql.append("    FROM TBPMS_EMPLOYEE_REC_N UCN ");
				sql.append("    WHERE UCN.DEPT_ID <> UCN.E_DEPT_ID  ");
				sql.append("    AND LAST_DAY(TO_DATE(:yearQtr || '01', 'YYYYMMDD')) BETWEEN UCN.START_TIME AND UCN.END_TIME ");
				sql.append("    AND UCN.EMP_ID = MAST.EMP_ID ");
				sql.append("  ) ");
			}
		} else {
			if (StringUtils.isNotBlank(inputVO.getUhrmOP())) {
				sql.append("  AND (");
				sql.append("       EXISTS ( SELECT 1 FROM TBORG_MEMBER MT WHERE MAST.EMP_ID = MT.EMP_ID AND MT.DEPT_ID = :uhrmOP ) ");
				sql.append("    OR EMP.E_DEPT_ID = :uhrmOP ");
				sql.append("  )");
				condition.setObject("uhrmOP", inputVO.getUhrmOP());
			}
			
			sql.append("  AND EXISTS ( ");
			sql.append("    SELECT 1 ");
			sql.append("    FROM TBPMS_EMPLOYEE_REC_N UCN ");
			sql.append("    WHERE UCN.DEPT_ID <> UCN.E_DEPT_ID  ");
			sql.append("    AND LAST_DAY(TO_DATE(:yearQtr || '01', 'YYYYMMDD')) BETWEEN UCN.START_TIME AND UCN.END_TIME ");
			sql.append("    AND UCN.EMP_ID = MAST.EMP_ID ");
			sql.append("  ) ");
		}

		if (StringUtils.isNotBlank(inputVO.getEmp_id())) {
			sql.append("and EMP.EMP_ID = :emp_id ");
			condition.setObject("emp_id", inputVO.getEmp_id());
		}

		// 訪查狀態
		if (StringUtils.isNotBlank(inputVO.getReview_status())) {
			switch (inputVO.getReview_status()) {
				case "01": // 未訪查
					sql.append("  AND MAST.AUDITED = 'N' ");
					
					break;
				case "02": // 已訪查
					sql.append("  AND MAST.AUDITED = 'Y' ");
	
					//單號: 5478 -- 已訪查多增加 REVIEW_DATE == null
					sql.append("  AND MAST.REVIEW_DATE is null ");
					
					break;
				case "03": // 已覆核
					sql.append("  AND MAST.REVIEW_DATE is not null ");
					break;
			}
		}
		
		// 身份別
		if (StringUtils.isNotBlank(inputVO.getCust_status())) {
			switch (inputVO.getCust_status()) {
				case "01": // 一般客戶
					sql.append("  AND MAST.SP_CUST_YN = 'N' ");
					
					break; 
				case "02": // 特定客戶
					sql.append("  AND MAST.SP_CUST_YN = 'Y' ");
					
					break;
			}
		}
		sql.append(") ");
		/*********** 歷史組織查詢條件篩選-END ***********/

		sql.append("ORDER BY BRANCH_NBR, AO_CODE, CUST_YEAR DESC, CUST_ID ");

		condition.setObject("yearQtr", inputVO.getYearQtr());
		condition.setObject("loginID", loginID);
		condition.setObject("loginBRH", loginBRH);

		condition.setQueryString(sql.toString());
		
		outputVO.setResultList(dam.exeQuery(condition));
		
		sendRtnObject(outputVO);
	}

	/** 查詢訪查明細資料 **/
	public void getDtlData(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		SQM320InputVO inputVO = (SQM320InputVO) body;
		SQM320OutputVO outputVO = new SQM320OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT CASE WHEN (SELECT COUNT(MEET_RULE) AS CHK_MEET_RULE FROM TBSQM_RSA_DTL WHERE YEARQTR = :yearQtr AND CUST_ID = :cust_id AND AUDIT_TYPE = :audit_type AND MEET_RULE = 'Y' GROUP BY YEARQTR, CUST_ID, AUDIT_TYPE) = 1 AND DTL.MEET_RULE = 'Y' THEN 'OPEN' ");
		sql.append("            WHEN (SELECT COUNT(MEET_RULE) AS CHK_MEET_RULE FROM TBSQM_RSA_DTL WHERE YEARQTR = :yearQtr AND CUST_ID = :cust_id AND AUDIT_TYPE = :audit_type AND MEET_RULE = 'Y' GROUP BY YEARQTR, CUST_ID, AUDIT_TYPE) = 1 AND DTL.MEET_RULE IS NULL THEN 'CLOSE' ");
		sql.append("       ELSE 'OPEN' END AS CHK_DISABLE, ");
		sql.append("       DTL.*, ");
//		sql.append("       CASE WHEN DTL.DOC_ID IS NOT NULL THEN 'Y' ELSE 'N' END AS FILE_FLAG, ");
//		sql.append("       FM.DOC_NAME, ");
		sql.append("       CUST.CUST_NAME, ");
		sql.append("       CUST.AO_CODE, ");
		sql.append("       CASE WHEN CUST.GENDER ='1' THEN '男' ");
		sql.append("            WHEN CUST.GENDER ='2' THEN '女' ");
		sql.append("       END AS GENDER, ");
		sql.append("       TO_CHAR(CUST.BIRTH_DATE,'YYYY/MM/DD') AS BIRTH_DATE, ");
		sql.append("       MAST.UP_KYC_YN, ");
		sql.append("       MAST.EBILL_PICK_YN, ");
		sql.append("       MAST.CUST_E_NOREC_FLAG, ");
		sql.append("       MAST.AST_LOSS_NOREC_FLAG, ");
		sql.append("       MAST.NOTE_SP_CUST_YN AS SP_CUST_YN, ");
		sql.append("       MAST.NOTE_REC_YN AS REC_YN, ");
		sql.append("       MAST.NOTE_BASE_FLAG AS COMM_RS_YN, ");
		sql.append("       NOTE.SECRET_YN, ");
		sql.append("       NOTE.PROF_INVESTOR_YN, ");
		sql.append("       NOTE.COMPLAIN_YN, ");
		sql.append("       AO.ROLE_NAME, ");
		sql.append("       AO.EMP_NAME ");
		sql.append("FROM TBSQM_RSA_DTL DTL ");
		sql.append("LEFT JOIN TBCRM_CUST_MAST CUST ON CUST.CUST_ID = DTL.CUST_ID ");
		sql.append("LEFT JOIN TBSQM_RSA_MAST MAST ON MAST.CUST_ID = DTL.CUST_ID AND MAST.YEARQTR = DTL.YEARQTR ");
		sql.append("LEFT JOIN TBCRM_CUST_NOTE NOTE ON NOTE.CUST_ID = DTL.CUST_ID ");
		sql.append("LEFT JOIN VWORG_AO_INFO AO ON MAST.AO_CODE = AO.AO_CODE ");
//		sql.append("LEFT JOIN TBSYS_FILE_MAIN FM ON DTL.DOC_ID = FM.DOC_ID ");
		sql.append("WHERE DTL.YEARQTR = :yearQtr ");
		sql.append("AND DTL.CUST_ID = :cust_id ");
		sql.append("AND DTL.AUDIT_TYPE =:audit_type ");

		condition.setObject("cust_id", inputVO.getCust_id());
		condition.setObject("yearQtr", inputVO.getYearQtr());
		condition.setObject("audit_type", inputVO.getAudit_type());
		
		condition.setQueryString(sql.toString());

		outputVO.setResultList(dam.exeQuery(condition));
		
		sendRtnObject(outputVO);
	}

	/** 查詢分行各職級查戶數-前端入口 **/
	public void getVisitTotal(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		SQM320InputVO inputVO = (SQM320InputVO) body;
		SQM320OutputVO outputVO = new SQM320OutputVO();

		List<Map<String, Object>> list = retrieveVisitTotalList(inputVO, "vistit", false);
		outputVO.setResultList(list);

		sendRtnObject(outputVO);
	}

	/** 查詢分行各職級查戶數-後端入口 **/
	private List<Map<String, Object>> retrieveVisitTotalList(SQM320InputVO inputVO, String type, Boolean exportAll) throws DAOException, JBranchException {
		
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT DISTINCT ");
		sql.append("       RTN_DAT.EMP_ID, ");
		sql.append("       RTN_DAT.EMP_NAME, ");
		sql.append("       RTN_DAT.BRANCH_NBR, ");
		sql.append("       RTN_DAT.ROLE_NAME, ");
		sql.append("       RTN_DAT.SC_CNT_TOT, "); 																//總查核戶數(一般+特殊)
		sql.append("       RTN_DAT.SC_CNT_L, "); 																//總應查戶教(一般+特殊) 
		sql.append("       NVL(OP_CNT.SC_CNT_AUDITED, 0) AS SC_CNT_AUDITED, "); 								//總已查戶數(一般+特殊) 
		sql.append("       RTN_DAT.SP_CUST_TOT, "); 															//總應查戶教(特殊) 
		sql.append("       NVL(OP_CNT.SP_CNT_AUDITED, 0) AS SP_CNT_AUDITED, "); 								//總已查戶數(特殊) 
		sql.append("       NVL(TM_DAT.TM_CNT, 0) AS TM_CNT, "); 												//符合電話行銷筆數 
		sql.append("       CASE WHEN NVL(TM_DAT.TM_CNT, 0) > 0 THEN '已查核' ELSE '未查核' END AS TM_FLAG, "); 		//是否符合電話行銷註記
		sql.append("       NVL(EB_DAT.EBPICK_ALL_CNT, 0) AS EBPICK_ALL_CNT,  "); 								//自取對帳單客戶名單所有筆數
		sql.append("       NVL(EB_DAT.EBPICK_AUDIT_CNT, 0) AS EBPICK_AUDIT_CNT,  "); 							//自取對帳單客戶名單已訪查筆數   
		sql.append("       CASE WHEN NVL(EB_DAT.EBPICK_ALL_CNT, 0)        = 0 THEN '不需查核' ");
		sql.append("            WHEN     NVL(EB_DAT.EBPICK_ALL_CNT, 0)   >= 2 ");
		sql.append("                 AND NVL(EB_DAT.EBPICK_AUDIT_CNT, 0) >= 2 THEN '已查核' ");
		sql.append("            WHEN     NVL(EB_DAT.EBPICK_ALL_CNT, 0)    = 1 ");
		sql.append("                 AND NVL(EB_DAT.EBPICK_AUDIT_CNT, 0)  = 1 THEN '已查核' ");
		sql.append("       ELSE '未查核' END AS EBILL_PICK_FLAG, "); 												//是否符合自取對帳單客戶名單訪查筆數規則註記
		sql.append("       NVL(CUSTE_DAT.CUST_E_NOREC_ALL_CNT, 0) AS CUST_E_NOREC_ALL_CNT, "); 					//符合當季底E級客戶訪查名單的筆數所有筆數
		sql.append("       NVL(CUSTE_DAT.CUST_E_NOREC_AUDIT_CNT, 0) AS CUST_E_NOREC_AUDIT_CNT, ");				//符合當季底E級客戶訪查名單的筆數已訪查筆數               
		sql.append("       CASE WHEN NVL(CUSTE_DAT.CUST_E_NOREC_ALL_CNT, 0)        = 0 THEN '不需查核' ");
		sql.append("            WHEN     NVL(CUSTE_DAT.CUST_E_NOREC_ALL_CNT, 0)   >= 1 ");
		sql.append("                 AND NVL(CUSTE_DAT.CUST_E_NOREC_AUDIT_CNT, 0) >= 1 THEN '已查核' ");
		sql.append("       ELSE '未查核' END AS CUST_E_NOREC_FLAG,  ");											//是否符合當季底E級客戶訪查名單筆數規則註記
		sql.append("       NVL(ASTLOSS_DAT.AST_LOSS_NOREC_ALL_CNT, 0) AS AST_LOSS_NOREC_ALL_CNT, "); 			//季底庫存資產減損客戶名單所有筆數
		sql.append("       NVL(ASTLOSS_DAT.AST_LOSS_NOREC_AUDIT_CNT, 0) AS AST_LOSS_NOREC_AUDIT_CNT,   ");		//季底庫存資產減損客戶名單已訪查筆數               
		sql.append("       CASE WHEN NVL(ASTLOSS_DAT.AST_LOSS_NOREC_ALL_CNT, 0)        = 0 THEN '不需查核' ");
		sql.append("            WHEN     NVL(ASTLOSS_DAT.AST_LOSS_NOREC_ALL_CNT, 0)   >= 1 ");
		sql.append("                 AND NVL(ASTLOSS_DAT.AST_LOSS_NOREC_AUDIT_CNT, 0) >= 1 THEN '已查核' ");
		sql.append("       ELSE '未查核' END AS AST_LOSS_NOREC_FLAG, "); 											//是否符合季底庫存資產減損客戶名單筆數規則註記
		sql.append("       NVL(OP_CNT.Q_CNT_TOT, 0) AS Q_CNT_TOT, "); 											//該季總抽查戶數 
		sql.append("       CASE WHEN     NVL(OP_CNT.SC_CNT_AUDITED, 0) >= RTN_DAT.SC_CNT_L "); 					//總已查戶數(一般+特殊) >= 總應查戶教(一般+特殊) 
		sql.append("                 AND NVL(OP_CNT.SP_CNT_AUDITED, 0) >= RTN_DAT.SP_CUST_TOT "); 				//總已查戶數(特殊)     >= 總應查戶教(特殊) 
		sql.append("                 AND (CASE WHEN NVL(TM_DAT.TM_CNT, 0) > 0 THEN 'Y' ELSE 'N' END) = 'Y' "); 	//至少需有一筆符合電話行銷
		sql.append("                 AND (CASE WHEN NVL(CUSTE_DAT.CUST_E_NOREC_ALL_CNT, 0)        = 0 THEN 'Y' ");
		sql.append("                           WHEN     NVL(CUSTE_DAT.CUST_E_NOREC_ALL_CNT, 0)   >= 1 ");
		sql.append("                                AND NVL(CUSTE_DAT.CUST_E_NOREC_AUDIT_CNT, 0) >= 1 THEN 'Y' ");
		sql.append("                      ELSE 'N' END ) = 'Y' "); 												//當季底E級客戶名單符合條件.
		sql.append("                 AND (CASE WHEN NVL(ASTLOSS_DAT.AST_LOSS_NOREC_ALL_CNT, 0)        = 0 THEN 'Y' ");
		sql.append("                           WHEN     NVL(ASTLOSS_DAT.AST_LOSS_NOREC_ALL_CNT, 0)   >= 1 ");
		sql.append("                                AND NVL(ASTLOSS_DAT.AST_LOSS_NOREC_AUDIT_CNT, 0) >= 1 THEN 'Y' ");
		sql.append("                      ELSE 'N' END ) = 'Y' "); 												//季底庫存資產減損客戶名單符合條件.
		sql.append("                 AND (CASE WHEN NVL(EB_DAT.EBPICK_ALL_CNT, 0)        = 0 THEN 'Y' ");
		sql.append("                           WHEN     NVL(EB_DAT.EBPICK_ALL_CNT, 0)   >= 2 ");
		sql.append("                                AND NVL(EB_DAT.EBPICK_AUDIT_CNT, 0) >= 2 THEN 'Y' ");
		sql.append("                           WHEN     NVL(EB_DAT.EBPICK_ALL_CNT, 0)    = 1 ");
		sql.append("                                AND NVL(EB_DAT.EBPICK_AUDIT_CNT, 0)  = 1 THEN 'Y' ");
		sql.append("                      ELSE 'N' END ) = 'Y' THEN 'Y' "); 									//自取對帳單客戶名單符合條件.
		sql.append("            WHEN     NVL(OP_CNT.SC_CNT_AUDITED, 0) = NVL(OP_CNT.Q_CNT_TOT, 0) "); 			//提供總抽查戶數是否小於應查戶數時(如已查戶數=總抽查戶數時)，始可送出覆核，排除刪除客戶. 
		sql.append("                 AND (CASE WHEN NVL(TM_DAT.TM_CNT, 0) > 0 THEN 'Y' ELSE 'N' END) = 'Y' "); 	//至少需有一筆符合電話行銷
		sql.append("                 AND (CASE WHEN NVL(CUSTE_DAT.CUST_E_NOREC_ALL_CNT, 0)        = 0 THEN 'Y' ");
		sql.append("                           WHEN     NVL(CUSTE_DAT.CUST_E_NOREC_ALL_CNT, 0)   >= 1 ");
		sql.append("                                AND NVL(CUSTE_DAT.CUST_E_NOREC_AUDIT_CNT, 0) >= 1 THEN 'Y' ");
		sql.append("                      ELSE 'N' END ) = 'Y' "); 												//當季底E級客戶名單符合條件.
		sql.append("                 AND (CASE WHEN NVL(ASTLOSS_DAT.AST_LOSS_NOREC_ALL_CNT, 0)        = 0 THEN 'Y' ");
		sql.append("                           WHEN     NVL(ASTLOSS_DAT.AST_LOSS_NOREC_ALL_CNT, 0)   >= 1 ");
		sql.append("                                AND NVL(ASTLOSS_DAT.AST_LOSS_NOREC_AUDIT_CNT, 0) >= 1 THEN 'Y' ");
		sql.append("                      ELSE 'N' END ) = 'Y' "); 												//季底庫存資產減損客戶名單符合條件.
		sql.append("                 AND (CASE WHEN NVL(EB_DAT.EBPICK_ALL_CNT, 0)        = 0 THEN 'Y' ");
		sql.append("                           WHEN     NVL(EB_DAT.EBPICK_ALL_CNT, 0)   >= 2 ");
		sql.append("                                AND NVL(EB_DAT.EBPICK_AUDIT_CNT, 0) >= 2 THEN 'Y' ");
		sql.append("                           WHEN     NVL(EB_DAT.EBPICK_ALL_CNT, 0)    = 1 ");
		sql.append("                                AND NVL(EB_DAT.EBPICK_AUDIT_CNT, 0)  = 1 THEN 'Y' ");
		sql.append("                       ELSE 'N' END ) = 'Y' THEN 'Y' "); 									//自取對帳單客戶名單符合條件.
		sql.append("       ELSE 'N' END AS REVIEW_FLAG, ");
		sql.append("       CASE WHEN     NVL(OP_CNT.SC_CNT_AUDITED, 0) > 0 ");
		sql.append("                 AND NVL(OP_CNT.REVIEW_CNT, 0) = NVL(OP_CNT.SC_CNT_AUDITED, 0) THEN 'Y' "); //已訪查數大於0且覆核戶數=總已查戶數(一般+特殊)
		sql.append("       ELSE 'N' END AS REVIEW_STATUS, "); 													//主管覆核狀態
		sql.append("       (SELECT DISTINCT TMP.REVIEWER_EMP_ID FROM TBSQM_RSA_MAST TMP WHERE TMP.YEARQTR = :yearQtr AND TMP.AUDIT_TYPE = '1' and TMP.AUDITED = 'Y' AND TMP.EMP_ID = RTN_DAT.EMP_ID AND TMP.REVIEWER_EMP_ID IS NOT NULL) AS REVIEWER_EMP_ID, ");
		sql.append("       (SELECT DISTINCT TMP.REVIEWER_EMP_ID FROM TBSQM_RSA_MAST TMP WHERE TMP.YEARQTR = :yearQtr AND TMP.AUDIT_TYPE = '1' and TMP.AUDITED = 'Y' AND TMP.EMP_ID = RTN_DAT.EMP_ID AND TMP.REVIEWER_EMP_ID IS NOT NULL) AS REVIEW_DATE ");

		// **** 列出該分行所有理專總/應查核戶數
		sql.append("FROM ( ");
		sql.append("  SELECT A.EMP_ID, ");
		sql.append("         E.EMP_NAME || CASE WHEN U.EMP_ID IS NOT NULL THEN '(私銀)' ELSE '' END AS EMP_NAME, ");
		sql.append("         A.BRANCH_NBR, ");
		sql.append("         R.ROLE_NAME, ");
		sql.append("         A.SC_CNT_TOT, ");
		sql.append("         A.SC_CNT_L, ");
		sql.append("         A.SP_CUST_TOT ");
		sql.append("  FROM TBSQM_RSA_RC_BR A ");
		sql.append("  LEFT JOIN TBORG_ROLE R ON R.ROLE_ID = A.ROLE_ID  ");
		sql.append("  LEFT JOIN TBPMS_EMPLOYEE_REC_N E ON A.EMP_ID = E.EMP_ID AND LAST_DAY(TO_DATE(A.YEARQTR || '01', 'YYYYMMDD')) BETWEEN E.START_TIME AND E.END_TIME ");
		sql.append("  LEFT JOIN VWORG_EMP_UHRM_INFO U ON A.EMP_ID = U.EMP_ID ");
		sql.append("  WHERE A.YEARQTR = :yearQtr ");
		
		// #0544:WMS-CR-20210303-01_客戶服務定期查核新增高端二階主管放行權限
		if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("uhrm") >= 0) {
			if (StringUtils.isNotBlank(inputVO.getUhrmOP())) {
				sql.append("  AND (");
				sql.append("       EXISTS ( SELECT 1 FROM TBORG_MEMBER MT WHERE A.EMP_ID = MT.EMP_ID AND MT.DEPT_ID = :uhrmOP ) ");
				sql.append("    OR E.E_DEPT_ID = :uhrmOP ");
				sql.append("  )");
				condition.setObject("uhrmOP", inputVO.getUhrmOP());
			}
			
			sql.append("  AND EXISTS ( ");
			sql.append("    SELECT 1 ");
			sql.append("    FROM TBPMS_EMPLOYEE_REC_N UCN ");
			sql.append("    WHERE UCN.DEPT_ID <> UCN.E_DEPT_ID  ");
			sql.append("    AND LAST_DAY(TO_DATE(:yearQtr || '01', 'YYYYMMDD')) BETWEEN UCN.START_TIME AND UCN.END_TIME ");
			sql.append("    AND UCN.EMP_ID = A.EMP_ID ");
			sql.append("  ) ");
		} else if (!exportAll) {
			sql.append("  AND A.BRANCH_NBR = :BRANCH_NBR ");
			
			// 分行權限者不可視UHRM相關資料
			if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("brh") >= 0) {
				sql.append("  AND NOT EXISTS ( ");
				sql.append("    SELECT 1 ");
				sql.append("    FROM TBPMS_EMPLOYEE_REC_N UCN ");
				sql.append("    WHERE UCN.DEPT_ID <> UCN.E_DEPT_ID  ");
				sql.append("    AND LAST_DAY(TO_DATE(:yearQtr || '01', 'YYYYMMDD')) BETWEEN UCN.START_TIME AND UCN.END_TIME ");
				sql.append("    AND UCN.EMP_ID = A.EMP_ID ");
				sql.append("  ) ");
			}
		}
		
		sql.append(") RTN_DAT ");
		
		// **** 已訪查尚未覆核筆數(一般+特殊)及已訪查尚未覆核筆數(特殊)及該季總客戶數(一般+特殊)，不含刪除 
		sql.append("LEFT JOIN (  ");
		sql.append("  SELECT A.EMP_ID, ");
		sql.append("         SUM( CASE WHEN NVL(A.AUDITED, ' ') = 'Y' THEN 1 ELSE 0 END ) AS SC_CNT_AUDITED ,  "); //已訪查筆數(一般+特殊) 
		sql.append("         SUM( CASE WHEN NVL(A.AUDITED, ' ') = 'Y' AND A.SP_CUST_YN = 'Y' THEN 1 ELSE 0 END ) AS SP_CNT_AUDITED ,  "); //已訪查筆數(特殊) 
		sql.append("         SUM( CASE WHEN NVL(A.AUDITED, ' ') <>'D' THEN 1 ELSE 0 END ) AS Q_CNT_TOT,  "); //該季總客戶數(一般+特殊)，不含刪除
		sql.append("         SUM( CASE WHEN NVL(A.AUDITED, ' ') = 'Y' AND A.REVIEW_DATE IS NOT NULL THEN 1 ELSE 0 END ) AS REVIEW_CNT "); //已覆核客戶數
		sql.append("  FROM TBSQM_RSA_MAST A ");
		sql.append("  LEFT JOIN TBPMS_EMPLOYEE_REC_N E ON A.EMP_ID = E.EMP_ID AND LAST_DAY(TO_DATE(A.YEARQTR || '01', 'YYYYMMDD')) BETWEEN E.START_TIME AND E.END_TIME ");
		sql.append("  WHERE A.YEARQTR = :yearQtr ");
		sql.append("  AND A.AUDIT_TYPE = '1' ");
		
		// #0544:WMS-CR-20210303-01_客戶服務定期查核新增高端二階主管放行權限
		if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("uhrm") >= 0) {
			if (StringUtils.isNotBlank(inputVO.getUhrmOP())) {
				sql.append("  AND (");
				sql.append("       EXISTS ( SELECT 1 FROM TBORG_MEMBER MT WHERE A.EMP_ID = MT.EMP_ID AND MT.DEPT_ID = :uhrmOP ) ");
				sql.append("    OR E.E_DEPT_ID = :uhrmOP ");
				sql.append("  )");
				condition.setObject("uhrmOP", inputVO.getUhrmOP());
			}
			
			sql.append("  AND EXISTS ( ");
			sql.append("    SELECT 1 ");
			sql.append("    FROM TBPMS_EMPLOYEE_REC_N UCN ");
			sql.append("    WHERE UCN.DEPT_ID <> UCN.E_DEPT_ID  ");
			sql.append("    AND LAST_DAY(TO_DATE(:yearQtr || '01', 'YYYYMMDD')) BETWEEN UCN.START_TIME AND UCN.END_TIME ");
			sql.append("    AND UCN.EMP_ID = A.EMP_ID ");
			sql.append("  ) ");
		} else if (!exportAll) {
			sql.append("  AND A.BRANCH_NBR = :BRANCH_NBR ");
			
			// 分行權限者不可視UHRM相關資料
			if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("brh") >= 0) {
				sql.append("  AND NOT EXISTS ( ");
				sql.append("    SELECT 1 ");
				sql.append("    FROM TBPMS_EMPLOYEE_REC_N UCN ");
				sql.append("    WHERE UCN.DEPT_ID <> UCN.E_DEPT_ID  ");
				sql.append("    AND LAST_DAY(TO_DATE(:yearQtr || '01', 'YYYYMMDD')) BETWEEN UCN.START_TIME AND UCN.END_TIME ");
				sql.append("    AND UCN.EMP_ID = A.EMP_ID ");
				sql.append("  ) ");
			}
		}
		
		sql.append("  GROUP BY A.EMP_ID ");
		sql.append(") OP_CNT ON RTN_DAT.EMP_ID = OP_CNT.EMP_ID ");
		
		// **** 符合電話行銷筆數
		sql.append("LEFT JOIN ( ");
		sql.append("  SELECT MAST_A.EMP_ID, COUNT(*) AS TM_CNT ");
		sql.append("  FROM ( ");
		sql.append("    SELECT DISTINCT A.EMP_ID, A.CUST_ID ");
		sql.append("    FROM TBSQM_RSA_MAST A ");
		sql.append("    LEFT JOIN TBPMS_EMPLOYEE_REC_N E ON A.EMP_ID = E.EMP_ID AND LAST_DAY(TO_DATE(A.YEARQTR || '01', 'YYYYMMDD')) BETWEEN E.START_TIME AND E.END_TIME ");
		sql.append("    WHERE A.YEARQTR = :yearQtr ");
		sql.append("    AND A.AUDIT_TYPE = '1' ");
		
		// #0544:WMS-CR-20210303-01_客戶服務定期查核新增高端二階主管放行權限
		if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("uhrm") >= 0) {
			if (StringUtils.isNotBlank(inputVO.getUhrmOP())) {
				sql.append("  AND (");
				sql.append("       EXISTS ( SELECT 1 FROM TBORG_MEMBER MT WHERE A.EMP_ID = MT.EMP_ID AND MT.DEPT_ID = :uhrmOP ) ");
				sql.append("    OR E.E_DEPT_ID = :uhrmOP ");
				sql.append("  )");
				condition.setObject("uhrmOP", inputVO.getUhrmOP());
			}
			
			sql.append("  AND EXISTS ( ");
			sql.append("    SELECT 1 ");
			sql.append("    FROM TBPMS_EMPLOYEE_REC_N UCN ");
			sql.append("    WHERE UCN.DEPT_ID <> UCN.E_DEPT_ID  ");
			sql.append("    AND LAST_DAY(TO_DATE(:yearQtr || '01', 'YYYYMMDD')) BETWEEN UCN.START_TIME AND UCN.END_TIME ");
			sql.append("    AND UCN.EMP_ID = A.EMP_ID ");
			sql.append("  ) ");
		} else if (!exportAll) {
			sql.append("  AND A.BRANCH_NBR = :BRANCH_NBR ");
			
			// 分行權限者不可視UHRM相關資料
			if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("brh") >= 0) {
				sql.append("  AND NOT EXISTS ( ");
				sql.append("    SELECT 1 ");
				sql.append("    FROM TBPMS_EMPLOYEE_REC_N UCN ");
				sql.append("    WHERE UCN.DEPT_ID <> UCN.E_DEPT_ID  ");
				sql.append("    AND LAST_DAY(TO_DATE(:yearQtr || '01', 'YYYYMMDD')) BETWEEN UCN.START_TIME AND UCN.END_TIME ");
				sql.append("    AND UCN.EMP_ID = A.EMP_ID ");
				sql.append("  ) ");
			}
		}
		
		sql.append("    AND NVL(A.AUDITED, ' ') = 'Y' ");
		sql.append("  ) MAST_A ");
		sql.append("  INNER JOIN ( ");
		sql.append("    SELECT DISTINCT CUST_ID ");
		sql.append("    FROM TBSQM_RSA_DTL ");
		sql.append("    WHERE YEARQTR = :yearQtr ");
		sql.append("    AND MEET_TM_RULE='Y' ");
		sql.append("  ) DTL_B ON MAST_A.CUST_ID = DTL_B.CUST_ID ");
		sql.append("  GROUP BY MAST_A.EMP_ID ");
		sql.append(") TM_DAT ON RTN_DAT.EMP_ID = TM_DAT.EMP_ID ");
		
		// **** 自取對帳單客戶名單
		sql.append("LEFT JOIN (  ");
		sql.append("  SELECT A.EMP_ID, COUNT(*) AS EBPICK_ALL_CNT,  "); //自取對帳單客戶名單所有筆數
		sql.append("         SUM( CASE WHEN A.AUDITED = 'Y' THEN 1 ELSE 0 END) AS EBPICK_AUDIT_CNT  "); //自取對帳單客戶名單已訪查筆數
		sql.append("  FROM TBSQM_RSA_MAST A ");
		sql.append("  LEFT JOIN TBPMS_EMPLOYEE_REC_N E ON A.EMP_ID = E.EMP_ID AND LAST_DAY(TO_DATE(A.YEARQTR || '01', 'YYYYMMDD')) BETWEEN E.START_TIME AND E.END_TIME ");
		sql.append("  WHERE A.YEARQTR = :yearQtr ");
		sql.append("  AND A.AUDIT_TYPE = '1' ");
		
		// #0544:WMS-CR-20210303-01_客戶服務定期查核新增高端二階主管放行權限
		if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("uhrm") >= 0) {
			if (StringUtils.isNotBlank(inputVO.getUhrmOP())) {
				sql.append("  AND (");
				sql.append("       EXISTS ( SELECT 1 FROM TBORG_MEMBER MT WHERE A.EMP_ID = MT.EMP_ID AND MT.DEPT_ID = :uhrmOP ) ");
				sql.append("    OR E.E_DEPT_ID = :uhrmOP ");
				sql.append("  )");
				condition.setObject("uhrmOP", inputVO.getUhrmOP());
			}
			
			sql.append("  AND EXISTS ( ");
			sql.append("    SELECT 1 ");
			sql.append("    FROM TBPMS_EMPLOYEE_REC_N UCN ");
			sql.append("    WHERE UCN.DEPT_ID <> UCN.E_DEPT_ID  ");
			sql.append("    AND LAST_DAY(TO_DATE(:yearQtr || '01', 'YYYYMMDD')) BETWEEN UCN.START_TIME AND UCN.END_TIME ");
			sql.append("    AND UCN.EMP_ID = A.EMP_ID ");
			sql.append("  ) ");
		} else if (!exportAll) {
			sql.append("  AND A.BRANCH_NBR = :BRANCH_NBR ");
			
			// 分行權限者不可視UHRM相關資料
			if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("brh") >= 0) {
				sql.append("  AND NOT EXISTS ( ");
				sql.append("    SELECT 1 ");
				sql.append("    FROM TBPMS_EMPLOYEE_REC_N UCN ");
				sql.append("    WHERE UCN.DEPT_ID <> UCN.E_DEPT_ID  ");
				sql.append("    AND LAST_DAY(TO_DATE(:yearQtr || '01', 'YYYYMMDD')) BETWEEN UCN.START_TIME AND UCN.END_TIME ");
				sql.append("    AND UCN.EMP_ID = A.EMP_ID ");
				sql.append("  ) ");
			}
		}
		
		sql.append("  AND A.AUDITED <>'D' ");
		sql.append("  AND A.EBILL_PICK_YN = 'Y' ");
		sql.append("  GROUP BY A.EMP_ID ");
		sql.append(") EB_DAT ON RTN_DAT.EMP_ID = EB_DAT.EMP_ID ");
		
		// 當季底E級客戶訪查名單(當季無訪訪記錄者)
		sql.append("LEFT JOIN ( ");
		sql.append("  SELECT A.EMP_ID, COUNT(*) AS CUST_E_NOREC_ALL_CNT, "); //符合當季底E級客戶訪查名單的筆數
		sql.append("         SUM( CASE WHEN A.AUDITED = 'Y' THEN 1 ELSE 0 END) AS CUST_E_NOREC_AUDIT_CNT "); //已訪查當季底E級客戶訪查名單的筆數
		sql.append("  FROM TBSQM_RSA_MAST A ");
		sql.append("  LEFT JOIN TBPMS_EMPLOYEE_REC_N E ON A.EMP_ID = E.EMP_ID AND LAST_DAY(TO_DATE(A.YEARQTR || '01', 'YYYYMMDD')) BETWEEN E.START_TIME AND E.END_TIME ");
		sql.append("  WHERE A.YEARQTR = :yearQtr ");
		sql.append("  AND A.AUDIT_TYPE = '1' ");
		
		// #0544:WMS-CR-20210303-01_客戶服務定期查核新增高端二階主管放行權限
		if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("uhrm") >= 0) {
			if (StringUtils.isNotBlank(inputVO.getUhrmOP())) {
				sql.append("  AND (");
				sql.append("       EXISTS ( SELECT 1 FROM TBORG_MEMBER MT WHERE A.EMP_ID = MT.EMP_ID AND MT.DEPT_ID = :uhrmOP ) ");
				sql.append("    OR E.E_DEPT_ID = :uhrmOP ");
				sql.append("  )");
				condition.setObject("uhrmOP", inputVO.getUhrmOP());
			}
			
			sql.append("  AND EXISTS ( ");
			sql.append("    SELECT 1 ");
			sql.append("    FROM TBPMS_EMPLOYEE_REC_N UCN ");
			sql.append("    WHERE UCN.DEPT_ID <> UCN.E_DEPT_ID  ");
			sql.append("    AND LAST_DAY(TO_DATE(:yearQtr || '01', 'YYYYMMDD')) BETWEEN UCN.START_TIME AND UCN.END_TIME ");
			sql.append("    AND UCN.EMP_ID = A.EMP_ID ");
			sql.append("  ) ");
		} else if (!exportAll) {
			sql.append("  AND A.BRANCH_NBR = :BRANCH_NBR ");
			
			// 分行權限者不可視UHRM相關資料
			if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("brh") >= 0) {
				sql.append("  AND NOT EXISTS ( ");
				sql.append("    SELECT 1 ");
				sql.append("    FROM TBPMS_EMPLOYEE_REC_N UCN ");
				sql.append("    WHERE UCN.DEPT_ID <> UCN.E_DEPT_ID  ");
				sql.append("    AND LAST_DAY(TO_DATE(:yearQtr || '01', 'YYYYMMDD')) BETWEEN UCN.START_TIME AND UCN.END_TIME ");
				sql.append("    AND UCN.EMP_ID = A.EMP_ID ");
				sql.append("  ) ");
			}
		}
		
		sql.append("  AND A.AUDITED <> 'D' ");
		sql.append("  AND A.CUST_E_NOREC_FLAG = 'Y' ");
		sql.append("  GROUP BY A.EMP_ID ");
		sql.append(") CUSTE_DAT ON RTN_DAT.EMP_ID = CUSTE_DAT.EMP_ID ");
		
		//新增季底庫存資產減損>=等值新台幣100萬元之客戶訪查名單(當季無訪訪記錄者)
		sql.append("LEFT JOIN ( ");
		sql.append("  SELECT A.EMP_ID, COUNT(*) AS AST_LOSS_NOREC_ALL_CNT ,  "); //符合當季底E級客戶訪查名單的筆數
		sql.append("         SUM( CASE WHEN A.AUDITED = 'Y' THEN 1 ELSE 0 END) AS AST_LOSS_NOREC_AUDIT_CNT  "); //已訪查當季底E級客戶訪查名單的筆數
		sql.append("  FROM TBSQM_RSA_MAST A ");
		sql.append("  LEFT JOIN TBPMS_EMPLOYEE_REC_N E ON A.EMP_ID = E.EMP_ID AND LAST_DAY(TO_DATE(A.YEARQTR || '01', 'YYYYMMDD')) BETWEEN E.START_TIME AND E.END_TIME ");
		sql.append("  WHERE A.YEARQTR = :yearQtr ");
		sql.append("  AND A.AUDIT_TYPE = '1' ");
		
		// #0544:WMS-CR-20210303-01_客戶服務定期查核新增高端二階主管放行權限
		if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("uhrm") >= 0) {
			if (StringUtils.isNotBlank(inputVO.getUhrmOP())) {
				sql.append("  AND (");
				sql.append("       EXISTS ( SELECT 1 FROM TBORG_MEMBER MT WHERE A.EMP_ID = MT.EMP_ID AND MT.DEPT_ID = :uhrmOP ) ");
				sql.append("    OR E.E_DEPT_ID = :uhrmOP ");
				sql.append("  )");
				condition.setObject("uhrmOP", inputVO.getUhrmOP());
			}
			
			sql.append("  AND EXISTS ( ");
			sql.append("    SELECT 1 ");
			sql.append("    FROM TBPMS_EMPLOYEE_REC_N UCN ");
			sql.append("    WHERE UCN.DEPT_ID <> UCN.E_DEPT_ID  ");
			sql.append("    AND LAST_DAY(TO_DATE(:yearQtr || '01', 'YYYYMMDD')) BETWEEN UCN.START_TIME AND UCN.END_TIME ");
			sql.append("    AND UCN.EMP_ID = A.EMP_ID ");
			sql.append("  ) ");
		} else if (!exportAll) {
			sql.append("  AND A.BRANCH_NBR = :BRANCH_NBR ");
			
			// 分行權限者不可視UHRM相關資料
			if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("brh") >= 0) {
				sql.append("  AND NOT EXISTS ( ");
				sql.append("    SELECT 1 ");
				sql.append("    FROM TBPMS_EMPLOYEE_REC_N UCN ");
				sql.append("    WHERE UCN.DEPT_ID <> UCN.E_DEPT_ID  ");
				sql.append("    AND LAST_DAY(TO_DATE(:yearQtr || '01', 'YYYYMMDD')) BETWEEN UCN.START_TIME AND UCN.END_TIME ");
				sql.append("    AND UCN.EMP_ID = A.EMP_ID ");
				sql.append("  ) ");
			}
		}
		
		sql.append("  AND A.AUDITED <> 'D' ");
		sql.append("  AND A.AST_LOSS_NOREC_FLAG = 'Y' ");
		sql.append("  GROUP BY A.EMP_ID ");
		sql.append(") ASTLOSS_DAT ON RTN_DAT.EMP_ID = ASTLOSS_DAT.EMP_ID ");

		// #1460_WMS-CR-20230417-03_為提升定期查核作業效率擬新增修改部份系統功能(其它) : 若當季個金客戶關係經理名下無客戶可查，則不會出現在「各職級查核戶數全行版」報表中
		sql.append("INNER JOIN ( ");
		sql.append("  SELECT DISTINCT MAST.EMP_ID ");
		sql.append("  FROM TBSQM_RSA_MAST MAST ");
		sql.append("  LEFT JOIN TBSQM_RSA_DTL DTL ON  MAST.CUST_ID = DTL.CUST_ID AND MAST.AUDIT_TYPE = DTL.AUDIT_TYPE AND MAST.YEARQTR = DTL.YEARQTR ");
		sql.append("  WHERE DTL.YEARQTR = :yearQtr ");
		sql.append(") DTL_T ON RTN_DAT.EMP_ID = DTL_T.EMP_ID ");
				
		if ("save".equals(type)) {
			sql.append("where RTN_DAT.EMP_ID = :emp_id ");
			condition.setObject("emp_id", inputVO.getEmp_id());
		}

		if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("uhrm") >= 0) {
			sql.append("ORDER BY RTN_DAT.EMP_ID ");
		} else if (!exportAll) {
			condition.setObject("BRANCH_NBR", inputVO.getBranch_nbr());
		} else {
			sql.append("ORDER BY RTN_DAT.BRANCH_NBR ");
		}
		
		condition.setObject("yearQtr", inputVO.getYearQtr());
		
		condition.setQueryString(sql.toString());

		return dam.exeQuery(condition);
	}

	private List<Map<String, Object>> totalListDownload(SQM320InputVO inputVO, String type, Boolean exportAll) throws DAOException, JBranchException {

		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT DISTINCT ");
		sql.append("       RTN_DAT.REGION_CENTER_ID, ");
		sql.append("       RTN_DAT.REGION_CENTER_NAME, ");
		sql.append("       RTN_DAT.BRANCH_NBR, ");
		sql.append("       RTN_DAT.BRANCH_NAME, ");
		sql.append("       RTN_DAT.EMP_ID, ");
		sql.append("       RTN_DAT.EMP_NAME, ");
		sql.append("       CASE WHEN NVL(OP_CNT.SC_CNT_AUDITED, 0) >= RTN_DAT.SC_CNT_L "); 						//--總已查戶數(一般+特殊) >= 總應查戶教(一般+特殊)
		sql.append("                 AND NVL(OP_CNT.SP_CNT_AUDITED, 0) >= RTN_DAT.SP_CUST_TOT "); 				//--總已查戶數(特殊) >= 總應查戶教(特殊) 
		sql.append("                 AND (CASE WHEN NVL(TM_DAT.TM_CNT, 0) > 0 THEN 'Y' ELSE 'N' END) = 'Y' "); 	//--至少需有一筆符合電話行銷
		sql.append("                 AND NVL(CUSTE_DAT.CUST_E_STATUS, 'Y') = 'Y' "); 										//--當季底E級客戶名單符合條件
		sql.append("                 AND NVL(ASTLOSS_DAT.ASTLOSS_STATUS, 'Y') = 'Y' "); 									//--季底庫存資產減損客戶名單符合條件
		sql.append("                 AND NVL(EB_DAT.EB_STATUS, 'Y') = 'Y' THEN 'Y' "); 									//--自取對帳單客戶名單符合條件
		sql.append("             WHEN NVL(OP_CNT.SC_CNT_AUDITED, 0) = NVL(OP_CNT.Q_CNT_TOT, 0) "); 				//--提供總抽查戶數是否小於應查戶數時(如已查戶數=總抽查戶數時)，始可送出覆核，排除刪除客戶.    
		sql.append("                 AND (CASE WHEN NVL(TM_DAT.TM_CNT, 0) > 0 THEN 'Y' ELSE 'N' END) = 'Y' "); 	//--至少需有一筆符合電話行銷
		sql.append("                 AND NVL(CUSTE_DAT.CUST_E_STATUS, 'Y') = 'Y' "); 										//--當季底E級客戶名單符合條件
		sql.append("                 AND NVL(ASTLOSS_DAT.ASTLOSS_STATUS, 'Y') = 'Y' "); 									//--季底庫存資產減損客戶名單符合條件
		sql.append("                 AND NVL(EB_DAT.EB_STATUS, 'Y') = 'Y' THEN 'Y' "); 									//--自取對帳單客戶名單符合條件
		sql.append("       ELSE 'N' END AS REVIEW_FLAG, ");
		sql.append("       OP_CNT.REVIEW_STATUS ");

		//-- 列出該分行所有理專總/應查核戶數
		sql.append("FROM ( ");
		sql.append("  SELECT A.EMP_ID, ");
		sql.append("         E.EMP_NAME || CASE WHEN U.EMP_ID IS NOT NULL THEN '(私銀)' ELSE '' END AS EMP_NAME, ");
		sql.append("         DEFN.REGION_CENTER_ID, ");
		sql.append("         DEFN.REGION_CENTER_NAME, ");
		sql.append("         A.BRANCH_NBR, ");
		sql.append("         DEFN.BRANCH_NAME, ");
		sql.append("         R.ROLE_NAME, ");
		sql.append("         A.SC_CNT_TOT, ");
		sql.append("         A.SC_CNT_L, ");
		sql.append("         A.SP_CUST_TOT ");
		sql.append("  FROM TBSQM_RSA_RC_BR A ");
		sql.append("  LEFT JOIN VWORG_DEFN_INFO DEFN ON A.BRANCH_NBR = DEFN.BRANCH_NBR ");
		sql.append("  LEFT JOIN TBORG_MEMBER E ON A.EMP_ID = E.EMP_ID ");
		sql.append("  LEFT JOIN TBORG_ROLE R ON R.ROLE_ID = A.ROLE_ID ");
		sql.append("  LEFT JOIN VWORG_EMP_UHRM_INFO U ON A.EMP_ID = U.EMP_ID ");
		sql.append("  WHERE YEARQTR = :yearQtr ");
		sql.append(") RTN_DAT ");
             
		//-- 已訪查尚未覆核筆數(一般+特殊)及已訪查尚未覆核筆數(特殊)及該季總客戶數(一般+特殊)，不含刪除 
		sql.append("LEFT JOIN ( ");
		sql.append("  SELECT EMP_ID, SC_CNT_AUDITED, SP_CNT_AUDITED, Q_CNT_TOT, REVIEW_CNT, ");
		sql.append("        CASE WHEN NVL(SC_CNT_AUDITED, 0) > 0 AND NVL(REVIEW_CNT, 0) = NVL(SC_CNT_AUDISZTED, 0) THEN 'Y' ELSE 'N' END AS REVIEW_STATUS ");
		sql.append("  FROM ( ");
		sql.append("    SELECT EMP_ID, ");
		sql.append("           SUM( CASE WHEN NVL(AUDITED, ' ') = 'Y' THEN 1 ELSE 0 END ) AS SC_CNT_AUDITED, "); //--已訪查筆數(一般+特殊) 
		sql.append("           SUM( CASE WHEN NVL(AUDITED, ' ') = 'Y' AND SP_CUST_YN = 'Y' THEN 1 ELSE 0 END ) AS SP_CNT_AUDITED, "); //--已訪查筆數(特殊) 
		sql.append("           SUM( CASE WHEN NVL(AUDITED, ' ') <>'D' THEN 1 ELSE 0 END ) AS Q_CNT_TOT, "); //--該季總客戶數(一般+特殊)，不含刪除
		sql.append("           SUM( CASE WHEN NVL(AUDITED, ' ') = 'Y' AND REVIEW_DATE IS NOT NULL THEN 1 ELSE 0 END ) AS REVIEW_CNT "); //--已覆核客戶數
		sql.append("    FROM TBSQM_RSA_MAST A ");
		sql.append("    WHERE YEARQTR = :yearQtr ");
		sql.append("    AND AUDIT_TYPE = '1'");
		sql.append("    GROUP BY EMP_ID ");
		sql.append("  ) ");
		sql.append(") OP_CNT ON RTN_DAT.EMP_ID = OP_CNT.EMP_ID ");

		//-- 符合電話行銷筆數
		sql.append("LEFT JOIN ( ");
		sql.append("  SELECT MAST_A.EMP_ID, COUNT(*) AS TM_CNT ");
		sql.append("  FROM ( ");
		sql.append("    SELECT EMP_ID, CUST_ID, ROW_NUMBER() OVER (PARTITION BY EMP_ID, CUST_ID ORDER BY EMP_ID) AS ROW_SU ");
		sql.append("    FROM TBSQM_RSA_MAST A ");
		sql.append("    WHERE YEARQTR = :yearQtr ");
		sql.append("    AND AUDIT_TYPE = '1' ");
		sql.append("    AND NVL(AUDITED, ' ') = 'Y' ");
		sql.append("  ) MAST_A ");
		sql.append("  WHERE 1 = 1 ");
		sql.append("  AND ROW_SU = 1 ");
		sql.append("  AND EXISTS (SELECT 1 FROM TBSQM_RSA_DTL TMP WHERE TMP.YEARQTR = :yearQtr AND TMP.MEET_TM_RULE = 'Y' AND MAST_A.CUST_ID = TMP.CUST_ID ) ");
		sql.append("  GROUP BY MAST_A.EMP_ID ");
		sql.append(") TM_DAT ON RTN_DAT.EMP_ID = TM_DAT.EMP_ID ");
             
		//-- 自取對帳單客戶名單
		sql.append("LEFT JOIN ( ");
		sql.append("  SELECT EMP_ID, ");
		sql.append("         CASE WHEN NVL(EBPICK_ALL_CNT, 0) = 0 THEN 'Y' ");
		sql.append("              WHEN NVL(EBPICK_ALL_CNT, 0) >= 2 AND NVL(EBPICK_AUDIT_CNT, 0) >= 2 THEN 'Y' ");
		sql.append("              WHEN NVL(EBPICK_ALL_CNT, 0) = 1 AND NVL(EBPICK_AUDIT_CNT, 0) = 1 THEN 'Y' ");
		sql.append("         ELSE 'N' END AS EB_STATUS ");
		sql.append("  FROM ( ");
		sql.append("    SELECT EMP_ID, ");
		sql.append("           COUNT(*) AS EBPICK_ALL_CNT, "); //--自取對帳單客戶名單所有筆數
		sql.append("           SUM( CASE WHEN AUDITED = 'Y' THEN 1 ELSE 0 END) AS EBPICK_AUDIT_CNT"); //--自取對帳單客戶名單已訪查筆數
		sql.append("    FROM TBSQM_RSA_MAST A ");
		sql.append("    WHERE YEARQTR = :yearQtr ");
		sql.append("    AND AUDIT_TYPE = '1' ");
		sql.append("    AND AUDITED <> 'D' ");
		sql.append("    AND EBILL_PICK_YN = 'Y' ");
		sql.append("    GROUP BY EMP_ID ");
		sql.append("  ) ");
		sql.append(") EB_DAT ON RTN_DAT.EMP_ID = EB_DAT.EMP_ID ");
             
		//-- 當季底E級客戶訪查名單(當季無訪訪記錄者)
		sql.append("LEFT JOIN ( ");
		sql.append("  SELECT EMP_ID, ");
		sql.append("         CASE WHEN NVL(CUST_E_NOREC_ALL_CNT, 0) = 0 THEN 'Y' ");
		sql.append("              WHEN NVL(CUST_E_NOREC_ALL_CNT, 0) >= 1 AND NVL(CUST_E_NOREC_AUDIT_CNT, 0) >= 1 THEN 'Y' ");
		sql.append("         ELSE 'N' END AS CUST_E_STATUS ");
		sql.append("  FROM ( ");
		sql.append("    SELECT EMP_ID, ");
		sql.append("           COUNT(*) AS CUST_E_NOREC_ALL_CNT, "); //--符合當季底E級客戶訪查名單的筆數
		sql.append("           SUM( CASE WHEN AUDITED = 'Y' THEN 1 ELSE 0 END) AS CUST_E_NOREC_AUDIT_CNT"); //--已訪查當季底E級客戶訪查名單的筆數
		sql.append("   FROM TBSQM_RSA_MAST A ");
		sql.append("   WHERE YEARQTR = :yearQtr ");
		sql.append("   AND AUDIT_TYPE = '1' ");
		sql.append("   AND AUDITED <> 'D' ");
		sql.append("   AND CUST_E_NOREC_FLAG = 'Y' ");
		sql.append("   GROUP BY EMP_ID ");
		sql.append("  ) ");
		sql.append(") CUSTE_DAT ON RTN_DAT.EMP_ID = CUSTE_DAT.EMP_ID ");
             
		//-- 新增季底庫存資產減損>=等值新台幣100萬元之客戶訪查名單(當季無訪訪記錄者)
		sql.append("LEFT JOIN ( ");
		sql.append("  SELECT EMP_ID, ");
		sql.append("         CASE WHEN NVL(AST_LOSS_NOREC_ALL_CNT, 0) = 0 THEN 'Y' ");
		sql.append("              WHEN NVL(AST_LOSS_NOREC_ALL_CNT, 0) >= 1 AND NVL(AST_LOSS_NOREC_AUDIT_CNT, 0) >= 1 THEN 'Y' ");
		sql.append("         ELSE 'N' END AS ASTLOSS_STATUS ");
		sql.append("  FROM ( ");
		sql.append("    SELECT EMP_ID, ");
		sql.append("           COUNT(*) AS AST_LOSS_NOREC_ALL_CNT, "); //--符合當季底E級客戶訪查名單的筆數
		sql.append("           SUM( CASE WHEN AUDITED = 'Y' THEN 1 ELSE 0 END) AS AST_LOSS_NOREC_AUDIT_CNT"); //--已訪查當季底E級客戶訪查名單的筆數
		sql.append("    FROM TBSQM_RSA_MAST A ");
		sql.append("    WHERE YEARQTR = :yearQtr ");
		sql.append("    AND AUDIT_TYPE = '1' ");
		sql.append("    AND AUDITED <> 'D' ");
		sql.append("    AND AST_LOSS_NOREC_FLAG = 'Y' ");
		sql.append("    GROUP BY EMP_ID ");
		sql.append("  ) ");
		sql.append(") ASTLOSS_DAT ON RTN_DAT.EMP_ID = ASTLOSS_DAT.EMP_ID ");

		//-- #1460_WMS-CR-20230417-03_為提升定期查核作業效率擬新增修改部份系統功能(其它) : 若當季個金客戶關係經理名下無客戶可查，則不會出現在「各職級查核戶數全行版」報表中
		sql.append("INNER JOIN ( ");
		sql.append("  SELECT DISTINCT MAST.EMP_ID ");
		sql.append("  FROM TBSQM_RSA_MAST MAST ");
		sql.append("  LEFT JOIN TBSQM_RSA_DTL DTL ON MAST.CUST_ID = DTL.CUST_ID AND MAST.AUDIT_TYPE = DTL.AUDIT_TYPE AND MAST.YEARQTR = DTL.YEARQTR ");
		sql.append("  WHERE DTL.YEARQTR = :yearQtr ");
		sql.append(") DTL_T ON RTN_DAT.EMP_ID = DTL_T.EMP_ID ");

		sql.append("ORDER BY RTN_DAT.REGION_CENTER_ID, RTN_DAT.BRANCH_NBR ");

		condition.setObject("yearQtr", inputVO.getYearQtr());

		condition.setQueryString(sql.toString());
		
		return dam.exeQuery(condition);
	}

	/** 全行下載 **/
	public void exportAll(Object body, IPrimitiveMap header) throws JBranchException, ParseException, FileNotFoundException, IOException {
		
		SQM320InputVO inputVO = (SQM320InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		String fileName = "各職級查核戶數全行版";

		String uuid = UUID.randomUUID().toString();
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		String filePath = Path + uuid;

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet(fileName);
		sheet.setDefaultColumnWidth(30);
		sheet.setDefaultRowHeightInPoints(30);

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

		String[] headerLine1 = { "業務處", "分行代號", "分行名稱", "員編", "個金RM", "查核狀態", "已覆核" };

		String[] mainLine = { "REGION_CENTER_NAME", "BRANCH_NBR", "BRANCH_NAME", "EMP_ID", "EMP_NAME", "REVIEW_FLAG", "REVIEW_STATUS" };

		Integer index = 0; // first row
		Integer startFlag = 0;
		Integer endFlag = 0;
		ArrayList<String> tempList = new ArrayList<String>(); //比對用

		XSSFRow row = sheet.createRow(index);
		for (int i = 0; i < headerLine1.length; i++) {
			String headerLine = headerLine1[i];
			if (tempList.indexOf(headerLine) < 0) {
				tempList.add(headerLine);
				XSSFCell cell = row.createCell(i);
				cell.setCellStyle(headingStyle);
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
		
		if (endFlag != 0) { //最後的CELL若需要合併儲存格，則在這裡做
			sheet.addMergedRegion(new CellRangeAddress(0, 0, startFlag, endFlag)); // firstRow, endRow, firstColumn, endColumn
		}
		
		index++;

		// 資料 CELL型式
		XSSFCellStyle mainStyle = workbook.createCellStyle();
		mainStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		mainStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		mainStyle.setBorderBottom((short) 1);
		mainStyle.setBorderTop((short) 1);
		mainStyle.setBorderLeft((short) 1);
		mainStyle.setBorderRight((short) 1);

		List<Map<String, Object>> list = totalListDownload(inputVO, "vistit", true);

		for (Map<String, Object> map : list) {
			row = sheet.createRow(index);

			if (map.size() > 0) {
				for (int j = 0; j < mainLine.length; j++) {
					XSSFCell cell = row.createCell(j);
					cell.setCellStyle(mainStyle);
					
					switch (mainLine[j]) {
						case "REVIEW_FLAG":
							String reviewFlag = (map.get(mainLine[j]) != null && "Y".equals(map.get(mainLine[j]).toString().trim())) ? "已完成" : "未完成";
							cell.setCellValue(reviewFlag);
							
							break;
						default:
							cell.setCellValue(checkIsNull(map, mainLine[j]));
							break;
					}
				}
				
				index++;
			}
		}
		
		workbook.write(new FileOutputStream(filePath));
		
		this.notifyClientToDownloadFile(DataManager.getSystem().getPath().get("temp").toString() + uuid, fileName + ".xlsx");

		sendRtnObject(null);
	}

	/** 確認欄位是否空值 **/
	private String checkIsNull(Map map, String key) {
		if (null != map && null != map.get(key))
			return String.valueOf(map.get(key));
		else
			return "";
	}

	/** 資料儲存 **/
	public void save(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		SQM320InputVO inputVO = (SQM320InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
		String loginID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);
		
		try {
			String meetRule = "", meetRuleCust1 = "", meetRuleCust2 = "", meetRuleCust3 = "", meetRuleCust4 = "";

			/**** 明細檔儲存 Start ****/
			for (Map<String, Object> map : inputVO.getDtlList()) {
				
//				String SN = "";
//				// file
//				if (!StringUtils.isBlank(inputVO.getFileRealName()) && !StringUtils.equals(inputVO.getFileRealName(), (String) map.get("DOC_NAME"))) {
//					condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//					sb = new StringBuffer();
//					sb.append("SELECT SQ_TBSYS_FILE_MAIN.nextval AS SEQ FROM DUAL ");
//					condition.setQueryString(sb.toString());
//					
//					List<Map<String, Object>> SEQLIST = dam.exeQuery(condition);
//					
//					SN = ObjectUtils.toString(SEQLIST.get(0).get("SEQ"));
//				
//				
//					String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
//					byte[] data = Files.readAllBytes(new File(tempPath, inputVO.getFileName()).toPath());
//
//					TBSYS_FILE_MAINVO fvo = new TBSYS_FILE_MAINVO();
//					fvo.setDOC_ID(SN);
//					fvo.setDOC_NAME(inputVO.getFileRealName());
//					fvo.setSUBSYSTEM_TYPE("SQM");
//					fvo.setDOC_TYPE("01");
//					dam.create(fvo);
//
//					TBSYS_FILE_DETAILVO dvo = new TBSYS_FILE_DETAILVO();
//					dvo.setDOC_ID(SN);
//					dvo.setDOC_VERSION_STATUS("2");
//					dvo.setDOC_FILE_TYPE("D");
//					dvo.setDOC_FILE(ObjectUtil.byteArrToBlob(data));
//					dam.create(dvo);
//				}
				
				meetRule = map.get("MEET_RULE").toString();
				meetRuleCust1 = map.get("MEET_RULE_CUST1").toString();
				meetRuleCust2 = map.get("MEET_RULE_CUST2").toString();
				meetRuleCust3 = map.get("MEET_RULE_CUST3").toString();
				meetRuleCust4 = map.get("MEET_RULE_CUST4").toString();
				
				sb = new StringBuffer();
				condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

				sb.append("UPDATE TBSQM_RSA_DTL ");
				sb.append("SET ");
				sb.append("  MEET_RULE = :MEET_RULE, ");
				sb.append("  MEET_RULE_CUST1 = :MEET_RULE_CUST1, ");
				sb.append("  MEET_RULE_CUST2 = :MEET_RULE_CUST2, ");
				sb.append("  MEET_RULE_CUST3 = :MEET_RULE_CUST3, ");
				sb.append("  MEET_RULE_CUST4 = :MEET_RULE_CUST4, ");
				sb.append("  AUDIT_REMARK = :AUDIT_REMARK, ");

				condition.setObject("MEET_RULE", map.get("MEET_RULE").toString());
				condition.setObject("MEET_RULE_CUST1", map.get("MEET_RULE_CUST1").toString());
				condition.setObject("MEET_RULE_CUST2", map.get("MEET_RULE_CUST2").toString());
				condition.setObject("MEET_RULE_CUST3", map.get("MEET_RULE_CUST3").toString());
				condition.setObject("MEET_RULE_CUST4", map.get("MEET_RULE_CUST4").toString());
				condition.setObject("AUDIT_REMARK", map.get("AUDIT_REMARK").toString());

				if (map.get("MEET_TM_RULE") != null) {
					sb.append("  MEET_TM_RULE = :MEET_TM_RULE, ");
					condition.setObject("MEET_TM_RULE", map.get("MEET_TM_RULE").toString());
				}
				
				if (map.get("CMU_TYPE") != null) {
					sb.append("  CMU_TYPE = :CMU_TYPE, ");
					condition.setObject("CMU_TYPE", map.get("CMU_TYPE").toString());
				}
				
				if (StringUtils.equals("P", map.get("MEET_TYPE").toString()) && map.get("RECORD_SEQ") != null) {
					sb.append("  RECORD_SEQ = :RECORD_SEQ, ");
					condition.setObject("RECORD_SEQ", map.get("RECORD_SEQ").toString());
				} else {
					sb.append("  RECORD_SEQ = NULL, ");
				}
				
//				if (!StringUtils.equals(inputVO.getFileRealName(), (String) map.get("DOC_NAME"))) {
//					sb.append("  DOC_ID = :DOC_ID, ");
//					condition.setObject("DOC_ID", StringUtils.isNotEmpty(SN) ? SN : "");
//				}
				
				sb.append("  MEET_DATE = :MEET_DATE, ");
				sb.append("  MEET_TYPE = :MEET_TYPE ");

				condition.setObject("MEET_DATE", sdfYYYYMMDD.parse(sdfYYYYMMDD.format(map.get("MEET_DATE"))));
				condition.setObject("MEET_TYPE", map.get("MEET_TYPE").toString());
				
				sb.append("WHERE YEARQTR = :YEARQTR ");
				sb.append("AND CUST_ID = :CUST_ID ");
				sb.append("AND AUDIT_TYPE = :AUDIT_TYPE ");
				sb.append("AND CUST_MEMO_SEQ = :CUST_MEMO_SEQ ");
				sb.append("AND STEP_ID = :STEP_ID ");
				sb.append("AND CAMPAIGN_ID = :CAMPAIGN_ID ");

				condition.setObject("YEARQTR", map.get("YEARQTR").toString());
				condition.setObject("CUST_ID", map.get("CUST_ID").toString());
				condition.setObject("AUDIT_TYPE", map.get("AUDIT_TYPE").toString());
				condition.setObject("CUST_MEMO_SEQ", map.get("CUST_MEMO_SEQ").toString());
				condition.setObject("STEP_ID", map.get("STEP_ID").toString());
				condition.setObject("CAMPAIGN_ID", map.get("CAMPAIGN_ID").toString());

				condition.setQueryString(sb.toString());

				dam.exeUpdate(condition);
			}
			/**** END ****/

			/**** 主檔儲存 Start ****/
			if (inputVO.getSave_type().equals("save")) {
				TBSQM_RSA_MASTPK mastPk = new TBSQM_RSA_MASTPK();
				mastPk.setYEARQTR(inputVO.getYearQtr());
				mastPk.setCUST_ID(inputVO.getCust_id());
				mastPk.setAUDIT_TYPE(inputVO.getAudit_type());

				TBSQM_RSA_MASTVO mastVO = (TBSQM_RSA_MASTVO) dam.findByPKey(TBSQM_RSA_MASTVO.TABLE_UID, mastPk);
				mastVO.setAUDITED("Y");
				mastVO.setAUDIT_DATE(new Timestamp(System.currentTimeMillis()));
				mastVO.setAUDIT_ROLE_ID(roleID);
				mastVO.setAUDITOR(loginID);

				//系統查核或電話查核如有一個不符合, 即不通過
				if (StringUtils.equals(meetRule, "Y")) {
					if (StringUtils.equals(meetRuleCust1, "Y") || StringUtils.equals(meetRuleCust2, "Y") || StringUtils.equals(meetRuleCust3, "Y") || StringUtils.equals(meetRuleCust4, "Y")) {
						mastVO.setMEET_RULE("Y");
					} else {
						mastVO.setMEET_RULE("N"); //空值或有電話確認符合都是通過
					}
				} else {
					mastVO.setMEET_RULE("N");
				}

				dam.update(mastVO);

//				// 加總已查比數
				TBSQM_RSA_RC_BRPK rcBrPk = new TBSQM_RSA_RC_BRPK();
				rcBrPk.setYEARQTR(inputVO.getYearQtr());
				rcBrPk.setBRANCH_NBR(inputVO.getBranch_nbr());
				rcBrPk.setEMP_ID(inputVO.getEmp_id());
				
				TBSQM_RSA_RC_BRVO rcBrVO = (TBSQM_RSA_RC_BRVO) dam.findByPKey(TBSQM_RSA_RC_BRVO.TABLE_UID, rcBrPk);

				if (StringUtils.isNotBlank(inputVO.getSp_cust_yn())) {
					// 加總已查客戶數 (計算總抽查戶數)
					// 撈出  AUDITED = 'Y'(已查) 的客戶
					if ("Y".equals(inputVO.getSp_cust_yn())) {
						rcBrVO.setSP_CUST_CNT(totalAuditedCnt(inputVO.getYearQtr(), inputVO.getEmp_id(), SPECIAL_CUST));
						rcBrVO.setSC_CNT(totalAuditedCnt(inputVO.getYearQtr(), inputVO.getEmp_id(), SC_CUST));
					}

					else if ("N".equals(inputVO.getSp_cust_yn())) {
						rcBrVO.setSC_CNT(totalAuditedCnt(inputVO.getYearQtr(), inputVO.getEmp_id(), SC_CUST));
					}

					dam.update(rcBrVO);
				}
			}
			
			sendRtnObject(null);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	public void deleteData(Object body, IPrimitiveMap header) throws JBranchException {

		SQM320InputVO inputVO = (SQM320InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
		String loginID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);

		switch (inputVO.getDelType()) {
			case "D":
				List<Map<String, Object>> visitorList = retrieveVisitTotalList(inputVO, "save", false);
	
				if (StringUtils.equals("Y", inputVO.getUp_kyc_yn()) && ((BigDecimal) visitorList.get(0).get("KYC_ALL_CNT")).compareTo(BigDecimal.ONE) <= 0) {
					throw new APException("「提高KYC承作商品者」剩餘1戶，無法刪除。");
				} else {
					try {
						Map<String, Object> map = inputVO.getDeleteList().get(0);
	
						TBSQM_RSA_MASTPK mastPk = new TBSQM_RSA_MASTPK();
						mastPk.setYEARQTR(map.get("YEARQTR").toString());
						mastPk.setCUST_ID(map.get("CUST_ID").toString());
						mastPk.setAUDIT_TYPE(map.get("AUDIT_TYPE").toString());
	
						TBSQM_RSA_MASTVO mastVO = (TBSQM_RSA_MASTVO) dam.findByPKey(TBSQM_RSA_MASTVO.TABLE_UID, mastPk);
						mastVO.setAUDITED("D");
						mastVO.setAUDIT_DATE(new Timestamp(System.currentTimeMillis()));
						mastVO.setAUDIT_ROLE_ID(roleID);
						mastVO.setAUDITOR(loginID);
	
						if (mastVO != null) {
							dam.update(mastVO);
						}
	
						TBSQM_RSA_RC_BRPK rcBrPk = new TBSQM_RSA_RC_BRPK();
						rcBrPk.setYEARQTR(map.get("YEARQTR").toString());
						rcBrPk.setBRANCH_NBR(map.get("BRANCH_NBR").toString());
						rcBrPk.setEMP_ID(map.get("EMP_ID").toString());
	
						TBSQM_RSA_RC_BRVO rcBrVO = (TBSQM_RSA_RC_BRVO) dam.findByPKey(TBSQM_RSA_RC_BRVO.TABLE_UID, rcBrPk);
	
						if (StringUtils.isNotBlank(map.get("SP_CUST_YN").toString())) {
							if ("Y".equals(map.get("SP_CUST_YN").toString())) {
								rcBrVO.setSP_CUST_CNT(totalAuditedCnt(map.get("YEARQTR").toString(), map.get("EMP_ID").toString(), SPECIAL_CUST));
								rcBrVO.setSC_CNT(totalAuditedCnt(map.get("YEARQTR").toString(), map.get("EMP_ID").toString(), SC_CUST));
							}
	
							else if ("N".equals(map.get("SP_CUST_YN").toString())) {
								rcBrVO.setSC_CNT(totalAuditedCnt(map.get("YEARQTR").toString(), map.get("EMP_ID").toString(), SC_CUST));
							}
	
							dam.update(rcBrVO);
						}
	
						//檢查查核狀態若有已覆核，需清掉。
						String checkKycFlag = "";
						visitorList = retrieveVisitTotalList(inputVO, "save", false);
	
						if (CollectionUtils.isNotEmpty(visitorList) && visitorList.size() > 0) {
							checkKycFlag = visitorList.get(0).get("REVIEW_FLAG").toString();
	
							if ("N".equals(checkKycFlag)) {
								sb = new StringBuffer();
								condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
	
								sb.append("update TBSQM_RSA_MAST ");
								sb.append("set REVIEWER_EMP_ID = null, ");
								sb.append("    REVIEWER_ROLE_ID = null ");
								sb.append("where YEARQTR = :yearQtr ");
								sb.append("and EMP_ID = :emp_id ");
								sb.append("and AUDIT_TYPE = '1' ");
								sb.append("and AUDITED = 'Y' ");
								sb.append("and REVIEWER_EMP_ID is not null ");
	
								condition.setObject("yearQtr", inputVO.getYearQtr());
								condition.setObject("emp_id", inputVO.getEmp_id());
	
								condition.setQueryString(sb.toString());
								dam.exeUpdate(condition);
							}
						}
	
						sendRtnObject(null);
					} catch (Exception e) {
						logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
						throw new APException("系統發生錯誤請洽系統管理員");
					}
				}
				break;
			case "R":
				try {
					Map<String, Object> map = inputVO.getDeleteList().get(0);
					
					// 恢復明細檔
					sb = new StringBuffer();
					condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
	
					sb.append("UPDATE TBSQM_RSA_DTL ");
					sb.append("SET ");
					sb.append("  MEET_RULE = NULL, ");
					sb.append("  MEET_RULE_CUST1 = NULL, ");
					sb.append("  MEET_RULE_CUST2 = NULL, ");
					sb.append("  MEET_RULE_CUST3 = NULL, ");
					sb.append("  MEET_RULE_CUST4 = NULL, ");
					sb.append("  AUDIT_REMARK = NULL, ");
					sb.append("  MEET_TM_RULE = NULL, ");
					sb.append("  RECORD_SEQ = NULL, ");				
					sb.append("  MEET_DATE = NULL, ");
					sb.append("  MEET_TYPE = NULL ");
	
					sb.append("WHERE YEARQTR = :YEARQTR ");
					sb.append("AND CUST_ID = :CUST_ID ");
					
					condition.setObject("YEARQTR", map.get("YEARQTR").toString());
					condition.setObject("CUST_ID", map.get("CUST_ID").toString());
					
					condition.setQueryString(sb.toString());
					
					dam.exeUpdate(condition);
					
					// 恢復主檔
					sb = new StringBuffer();
					condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
	
					sb.append("UPDATE TBSQM_RSA_MAST ");
					sb.append("SET ");
					sb.append("  AUDITED = 'N', ");
					sb.append("  AUDIT_DATE = NULL, ");
					sb.append("  AUDIT_ROLE_ID = NULL, ");
					sb.append("  AUDITOR = NULL, ");
					sb.append("  MEET_RULE = NULL ");
					
					sb.append("WHERE YEARQTR = :YEARQTR ");
					sb.append("AND CUST_ID = :CUST_ID ");
					
					condition.setObject("YEARQTR", map.get("YEARQTR").toString());
					condition.setObject("CUST_ID", map.get("CUST_ID").toString());
					
					condition.setQueryString(sb.toString());

					dam.exeUpdate(condition);
					
					// 加總已查客戶數(計算總抽查戶數)
					TBSQM_RSA_RC_BRPK rcBrPk = new TBSQM_RSA_RC_BRPK();
					rcBrPk.setYEARQTR(map.get("YEARQTR").toString());
					rcBrPk.setBRANCH_NBR(map.get("BRANCH_NBR").toString());
					rcBrPk.setEMP_ID(map.get("EMP_ID").toString());
					
					TBSQM_RSA_RC_BRVO rcBrVO = (TBSQM_RSA_RC_BRVO) dam.findByPKey(TBSQM_RSA_RC_BRVO.TABLE_UID, rcBrPk);
					
					rcBrVO.setSP_CUST_CNT(totalAuditedCnt(inputVO.getYearQtr(), inputVO.getEmp_id(), SPECIAL_CUST));
					rcBrVO.setSC_CNT(totalAuditedCnt(inputVO.getYearQtr(), inputVO.getEmp_id(), SC_CUST));
					
					dam.update(rcBrVO);
				} catch (Exception e) {
					logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
					throw new APException("系統發生錯誤請洽系統管理員");
				}
				
				break;
		}
		
		sendRtnObject(null);
	}

	/** 資料覆核 **/
	public void reviewData(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		SQM320InputVO inputVO = (SQM320InputVO) body;
		dam = this.getDataAccessManager();
		
		String loginID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);

		try {
			for (Map<String, Object> map : inputVO.getReviewList()) {
				//主檔儲存
				TBSQM_RSA_MASTPK mastPk = new TBSQM_RSA_MASTPK();
				mastPk.setYEARQTR(map.get("YEARQTR").toString());
				mastPk.setCUST_ID(map.get("CUST_ID").toString());
				mastPk.setAUDIT_TYPE(map.get("AUDIT_TYPE").toString());

				TBSQM_RSA_MASTVO mastVO = (TBSQM_RSA_MASTVO) dam.findByPKey(TBSQM_RSA_MASTVO.TABLE_UID, mastPk);

				if (loginID.equals(mastVO.getREVIEWER_EMP_ID()))
					mastVO.setREVIEW_DATE(new Timestamp(System.currentTimeMillis()));
				
				dam.update(mastVO);
			}

			sendRtnObject(null);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	/** 手動資料覆核 - 選擇簽核人員 **/
	public void review(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		SQM320InputVO inputVO = (SQM320InputVO) body;
		SQM320OutputVO outputVO = new SQM320OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		try {
			if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("uhrm") >= 0) {
				sql.append("SELECT EMP_ID, EMP_NAME, ROLE_ID ");
				sql.append("FROM VWORG_EMP_UHRM_INFO ");
				sql.append("WHERE PRIVILEGEID = 'UHRM013' ");
				
				condition.setQueryString(sql.toString());
				
				outputVO.setReviewerList(dam.exeQuery(condition));
			} else {
				sql.append("WITH BASE AS ( ");
				//  取所有訪查者的PRIV 
				sql.append("  SELECT DISTINCT MAST.BRANCH_NBR, MAST.AUDITOR, MAST.AUDIT_ROLE_ID, INFO.PRIVILEGEID AS EMP_MAX_PRIV ");
				sql.append("  FROM TBSQM_RSA_MAST MAST ");
				sql.append("  INNER JOIN ( ");
				sql.append("    SELECT RO.EMP_ID, RO.ROLE_ID, ASS.PRIVILEGEID ");
				sql.append("    FROM TBORG_MEMBER_ROLE RO ");
				sql.append("    INNER JOIN TBSYSSECUROLPRIASS ASS ON RO.ROLE_ID = ASS.ROLEID ");
				sql.append("  ) INFO ON INFO.EMP_ID = MAST.AUDITOR ");
				sql.append("  WHERE MAST.YEARQTR = :yq ");
				sql.append("  AND MAST.EMP_ID = :emp_id ");
				sql.append("  AND MAST.AUDIT_TYPE = '1' ");
				sql.append("  AND MAST.REVIEWER_EMP_ID IS NULL ");
				sql.append("  AND MAST.AUDITED = 'Y' ");
				sql.append(")");
				
				//  排序該訪查者最大角色 
				sql.append(", FIND_MAX_ROLE AS ( ");
				sql.append("  SELECT ROW_NUMBER() OVER (PARTITION BY A.AUDITOR ORDER BY A.EMP_MAX_PRIV DESC) AS SN, A.* ");
				sql.append("  FROM BASE A WHERE A.EMP_MAX_PRIV NOT LIKE '%UHRM%'");
				sql.append(") ");
				
				//  取出該訪查者最大角色 
				sql.append(", GET_MAX_ROLE AS ( ");
				sql.append("  SELECT * FROM FIND_MAX_ROLE WHERE SN = 1 ");
				sql.append(") ");
				
				//  設定訪查角色的下一關 
				sql.append(", GET_NEXT_ROLE AS ( ");
				sql.append("  SELECT '009' AS NOW_PRIV_ID, '012' AS NEXT_PRIV_ID FROM DUAL ");
				sql.append("  UNION ");
				sql.append("  SELECT '010', '012' FROM DUAL ");
				sql.append("  UNION ");
				sql.append("  SELECT '011', '012' FROM DUAL ");
				sql.append("  UNION ");
				sql.append("  SELECT '012', '013' FROM DUAL ");
				sql.append("  UNION ");
				sql.append("  SELECT '013', 'XXX' FROM DUAL ");
				sql.append(") ");
				sql.append("SELECT A.BRANCH_NBR, B.NEXT_PRIV_ID  ");
				sql.append("FROM GET_MAX_ROLE A ");
				sql.append("INNER JOIN GET_NEXT_ROLE B ON A.EMP_MAX_PRIV = B.NOW_PRIV_ID ");
				sql.append("ORDER BY A.EMP_MAX_PRIV DESC ");
				sql.append("FETCH FIRST 1 ROWS ONLY ");

				condition.setObject("yq", inputVO.getYearQtr());
				condition.setObject("emp_id", inputVO.getEmp_id());
				condition.setQueryString(sql.toString());
				List<Map<String, Object>> list = dam.exeQuery(condition);

				if (CollectionUtils.isNotEmpty(list) && list.size() > 0) {
					String pri = list.get(0).get("NEXT_PRIV_ID").toString();
					String branch = list.get(0).get("BRANCH_NBR").toString();
					
					sql = new StringBuffer();
					condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					if (pri.equals("012")) {
						//priv=012 => 區督導
						sql.append("SELECT EMP_ID, EMP_NAME, ROLE_ID ");
						sql.append("FROM ( ");
						sql.append("  SELECT EMP.EMP_ID, EMP.EMP_NAME, EMP.ROLE_ID ");
						sql.append("  FROM VWORG_EMP_INFO EMP ");
						sql.append("  WHERE EMP.PRIVILEGEID = '012' ");
						sql.append("  AND EXISTS (SELECT DEFN.BRANCH_AREA_ID FROM VWORG_DEFN_INFO DEFN WHERE DEFN.BRANCH_AREA_ID = EMP.BRANCH_AREA_ID AND DEFN.BRANCH_NBR = :branch_nbr) ");
						sql.append(") EMP ");

						condition.setObject("branch_nbr", branch);
						condition.setQueryString(sql.toString());
						outputVO.setReviewerList(dam.exeQuery(condition));
					} else if (pri.equals("013")) {
						//priv=013 => 處主管覆核
						sql.append("SELECT DISTINCT EMP_ID, EMP_NAME, ROLE_ID ");
						sql.append("FROM ( ");
						sql.append("  SELECT EMP.EMP_ID, EMP.EMP_NAME, EMP.ROLE_ID ");
						sql.append("  FROM VWORG_EMP_INFO EMP ");
						sql.append("  WHERE EMP.PRIVILEGEID = '013' ");
						sql.append("  AND EMP.JOB_TITLE_NAME IN ('處主管','處副主管') ");
						sql.append("  AND EXISTS ( ");
						sql.append("    SELECT DEFN.REGION_CENTER_ID ");
						sql.append("    FROM VWORG_DEFN_INFO DEFN ");
						sql.append("    WHERE DEFN.REGION_CENTER_ID = EMP.REGION_CENTER_ID ");
						sql.append("    AND DEFN.BRANCH_NBR = :branch_nbr");
						sql.append("  ) ");
						sql.append(") EMP ");

						condition.setObject("branch_nbr", branch);
						
						condition.setQueryString(sql.toString());
						
						outputVO.setReviewerList(dam.exeQuery(condition));
					}
				}
			}
			
			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	/** 手動覆核 - 確認簽核人員 **/
	public void saveReview(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		SQM320InputVO inputVO = (SQM320InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		sb.append("UPDATE TBSQM_RSA_MAST ");
		sb.append("SET REVIEWER_EMP_ID = :reviewer_emp_id, ");
		sb.append("    REVIEWER_ROLE_ID = :reviewer_role_id ");
		sb.append("WHERE YEARQTR = :yearQtr ");
		sb.append("AND EMP_ID = :emp_id ");
		sb.append("AND AUDIT_TYPE = '1' ");
		sb.append("AND AUDITED = 'Y' ");
		sb.append("AND REVIEWER_EMP_ID is null ");

		condition.setObject("yearQtr", inputVO.getYearQtr());
		condition.setObject("emp_id", inputVO.getEmp_id());
		condition.setObject("reviewer_emp_id", inputVO.getReviewerList().get(0).get("EMP_ID").toString());
		condition.setObject("reviewer_role_id", inputVO.getReviewerList().get(0).get("ROLE_ID").toString());

		condition.setQueryString(sb.toString());
		
		dam.exeUpdate(condition);
		
		this.sendRtnObject(null);
	}

	/*** 手動清除覆核 ***/
	public void reset(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		SQM320InputVO inputVO = (SQM320InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		sb.append("update TBSQM_RSA_MAST ");
		sb.append("set REVIEWER_EMP_ID = null, ");
		sb.append("    REVIEWER_ROLE_ID = null, ");
		sb.append("    REVIEW_DATE = NULL ");
		sb.append("where YEARQTR = :yearQtr ");
		sb.append("and EMP_ID = :emp_id ");
		sb.append("and AUDIT_TYPE = '1' ");
		sb.append("and AUDITED = 'Y' ");
		sb.append("and REVIEWER_EMP_ID is not null ");

		condition.setObject("yearQtr", inputVO.getYearQtr());
		condition.setObject("emp_id", inputVO.getEmp_id());

		condition.setQueryString(sb.toString());
		
		dam.exeUpdate(condition);

		this.sendRtnObject(null);
	}

	public Map<String, Object> getReviewerIDMap(String id, String roleID, String branch_nbr) throws DAOException, JBranchException {

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		// get EMP privilegeid === add by ocean
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT PRIVILEGEID, ROLEID AS ROLE_ID ");
		sb.append("FROM TBSYSSECUROLPRIASS ");
		sb.append("WHERE ROLEID = :roleID ");
		
		queryCondition.setObject("roleID", roleID);
		queryCondition.setQueryString(sb.toString());

		List<Map<String, Object>> privilegeList = dam.exeQuery(queryCondition);
		String privilegeid = "";
		if (privilegeList.size() > 0) {
			privilegeid = (String) privilegeList.get(0).get("PRIVILEGEID");
		}
		// ===

		List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();

		try {
			// 訪查人為分行主管 or 分行主管以下(非uhrm且<11皆為分行主管以下人員)
			if (privilegeid.equals("011") || (privilegeid.indexOf("UHRM") == -1 && new BigDecimal(privilegeid.replace("UHRM", "")).compareTo(new BigDecimal("11")) < 0)) {
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuffer();

				//問題單5684:業務主管完成訪查，覆核者跳到上二階主管，另營運督導加入 A301 角色, 以 A146 優先取出.
				sb.append("SELECT EMP_ID, ROLE_ID ");
				sb.append("FROM ( ");
				sb.append("  SELECT EMP.EMP_ID, EMP.ROLE_ID ");
				sb.append("  FROM VWORG_EMP_INFO EMP ");
				sb.append("  WHERE EMP.PRIVILEGEID = '012' ");
				sb.append("  AND EXISTS (SELECT DEFN.BRANCH_AREA_ID FROM VWORG_DEFN_INFO DEFN WHERE DEFN.BRANCH_AREA_ID = EMP.BRANCH_AREA_ID AND DEFN.BRANCH_NBR = :branch_nbr) ");
				sb.append("  AND ROWNUM = 1 ");
				sb.append("  ORDER BY CASE WHEN EMP.ROLE_ID = 'A146' THEN 1 WHEN EMP.ROLE_ID = 'A301' THEN 2 ELSE 3 END ");
				sb.append(") EMP ");

				queryCondition.setObject("branch_nbr", branch_nbr);
				queryCondition.setQueryString(sb.toString());

				List<Map<String, Object>> mbr_list = dam.exeQuery(queryCondition);

				// 單號：5478→如分行主管的下一層區督導為同一人時, 則在找下一層
				if (mbr_list.size() > 0 && ((String) mbr_list.get(0).get("EMP_ID")).equals(id)) {
					List<Map<String, Object>> recur_list = getRecursiveSup_Id(branch_nbr);
					if (recur_list.size() > 0) {
						returnList = recur_list;
					}
				} else {
					returnList = mbr_list;
				}
			}

			//訪查人為區督導、副處主管
			if (privilegeid.equals("012") || returnList.size() == 0) {
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuffer();

				List<Map<String, Object>> recur_list = getRecursiveSup_Id(branch_nbr);
				if (recur_list.size() > 0) {
					returnList = recur_list;
				}
			}
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}

		return returnList.get(0);
	}

	public List<Map<String, Object>> getRecursiveSup_Id(String branch_nbr) throws DAOException, JBranchException {
		
		StringBuffer recur_sql = new StringBuffer();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		// 20190510 modify by ocean
		recur_sql.append("SELECT DISTINCT EMP_ID, ROLE_ID ");
		recur_sql.append("FROM ( ");
		recur_sql.append("  SELECT EMP.EMP_ID, EMP.ROLE_ID ");
		recur_sql.append("  FROM VWORG_EMP_INFO EMP ");
		recur_sql.append("  WHERE EMP.PRIVILEGEID = '013' ");
		recur_sql.append("  AND EMP.JOB_TITLE_NAME IN ('處主管','處副主管') ");
		recur_sql.append("  AND EXISTS (SELECT DEFN.REGION_CENTER_ID FROM VWORG_DEFN_INFO DEFN WHERE DEFN.REGION_CENTER_ID = EMP.REGION_CENTER_ID AND DEFN.BRANCH_NBR = :branch_nbr) ");
		recur_sql.append("  ORDER BY CASE WHEN EMP.ROLE_TYPE = 'Y' THEN 1 ELSE 2 END ");
		recur_sql.append(") EMP ");

		condition.setObject("branch_nbr", branch_nbr);
		
		condition.setQueryString(recur_sql.toString());

		return dam.exeQuery(condition);

	}

	public String getYearQtr (String yearQtr) {
		
		String qtr = yearQtr.substring(0, 4);
		switch (yearQtr.substring(4, 6)) {
			case "03":
				qtr += "Q1";
				break;
			case "06":
				qtr += "Q2";
				break;
			case "09":
				qtr += "Q3";
				break;
			case "12":
				qtr += "Q4";
				break;
		}
		
		return qtr;
	}
	
	/** 匯出舊報表EXCEL檔 **/
	public void exportRPT(Object body, IPrimitiveMap header) throws JBranchException, Exception {
		
		SQM320InputVO inputVO = (SQM320InputVO) body;
		SQM320OutputVO outputVO = new SQM320OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		ConfigAdapterIF config = (ConfigAdapterIF) PlatformContext.getBean("configAdapter");
		String yearQtr = getYearQtr(inputVO.getYearQtr());

		// #0544:WMS-CR-20210303-01_客戶服務定期查核新增高端二階主管放行權限
		String fileName  = "客戶服務定期查核報告(舊報表)_" + yearQtr + "_" + getUserVariable(FubonSystemVariableConsts.LOGINID) + ".xlsx";
		String csvheader = yearQtr.substring(0, 4) + "年" + yearQtr.substring(4, 6) + "季";
		if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("uhrm") >= 0) {
			csvheader = csvheader + "           ";
		} else {
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("SELECT BRANCH_NBR, BRANCH_NAME ");
			sql.append("FROM VWORG_DEFN_INFO ");
			sql.append("WHERE BRANCH_NBR = :bra_nbr  ");

			condition.setObject("bra_nbr", inputVO.getBranch_nbr());
			
			condition.setQueryString(sql.toString());
			
			List<Map<String, Object>> BR_list = dam.exeQuery(condition);

			csvheader = csvheader + "           " + ObjectUtils.toString(BR_list.get(0).get("BRANCH_NAME")) + "           ";
		}
		
		csvheader = csvheader + "客戶服務定期查核報告";
		// ====
		
		XSSFWorkbook wb = new XSSFWorkbook();

		//設定SHEET名稱
		XSSFSheet sheet = wb.createSheet("客戶服務定期查核報告");
		sheet.setDefaultColumnWidth(25);
		sheet.setDefaultRowHeightInPoints(25);

		// 資料 CELL型式
		XSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		style.setBorderBottom((short) 1);
		style.setBorderTop((short) 1);
		style.setBorderLeft((short) 1);
		style.setBorderRight((short) 1);

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

		List<String> heading = new ArrayList<String>();
		heading.add(csvheader);

		String[] headerLineTop = {"專員姓名/員編", "回覆日期", "查核對象", "", 
								  "", "", "", "", "", 
								  "", 
								  "查核項目", 
								  "", 
								  "符合電話行銷規範", 
								  "查核意見", 
								  "訪查者", "覆核者", "覆核日期"};
		String[] headerLineSec = {"", "", "客戶ID", "客戶姓名", 
							  	  "特定客戶", "提高KYC承作商品客戶", "自取對帳單客戶", "符合推介", "共同行銷", 
							  	  "訪談紀錄上所載時間", 
							  	  "系統查核", 
							  	  "查核項目-與客戶確認(電訪或面訪等方式)包含但不限於:\n1.確認客戶清楚投資損益狀況及理財往來情形\n2.個金RM無銷售非屬本行核准之金融商品\n3.個金RM無自行製作並提供對帳單\n4.「自取對帳單客戶名單」目前資產餘額確認", 
							  	  "", 
							  	  "", 
							  	  "", "", ""};
		

		Integer index = 0; // 行數	
		XSSFRow row = sheet.createRow(index); // Heading

		for (int i = 0; i < heading.size(); i++) {
			XSSFCell cell = row.createCell(i);
			cell.setCellStyle(headingStyle);
			cell.setCellValue(heading.get(i));
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 16));
		}

		index++;
		row = sheet.createRow(index);
		row.setHeightInPoints(25);

		for (int i = 0; i < headerLineTop.length; i++) {
			XSSFCell cell = row.createCell(i);
			cell.setCellStyle(headingStyle);
			cell.setCellValue(headerLineTop[i]);
			
			switch (i) {
				case 2:
					sheet.addMergedRegion(new CellRangeAddress(1, 1, 2, 9)); // firstRow, endRow, firstColumn, endColumn
					break;
				case 10:
					sheet.addMergedRegion(new CellRangeAddress(1, 1, 10, 11));
					
					break;
				default:
					if (i < 2 || i >= 12) {
						sheet.addMergedRegion(new CellRangeAddress(1, 2, i, i));
					}
					
					break;
			}
		}
		
		index++;
		row = sheet.createRow(index);

		for (int i = 0; i < headerLineSec.length; i++) {
			XSSFCell cell = row.createCell(i);
			cell.setCellStyle(headingStyle);
			cell.setCellValue(headerLineSec[i]);
		}

		condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT DISTINCT MAST.EMP_ID, ");
		sql.append("       MAST.EMP_ID || '-' || AO.EMP_NAME AS AO_NAME, ");
		sql.append("       TO_CHAR(MAST.AUDIT_DATE, 'YYYY/MM/DD') AS AUDIT_DATE, ");
		sql.append("       MAST.CUST_ID, ");
		sql.append("       CUST.CUST_NAME, ");
		sql.append("       MAST.NOTE_SP_CUST_YN AS SP_CUST_YN, ");
		sql.append("       MAST.UP_KYC_YN, ");
		sql.append("       MAST.EBILL_PICK_YN, ");
		sql.append("       MAST.NOTE_REC_YN AS REC_YN, ");
		sql.append("       MAST.NOTE_BASE_FLAG AS COMM_RS_YN, ");
		sql.append("       DTL.LEAD_TYPE, ");
		sql.append("       DTL.AUDIT_REMARK, ");
		sql.append("       AO_CNT.CNT, ");
		sql.append("       TO_CHAR(DTL.CONTACT_DATE, 'YYYY/MM/DD') AS CONTACT_DATE, ");
		sql.append("       DTL.MEET_RULE, ");
		sql.append("       DTL.MEET_RULE_CUST, ");
		sql.append("       CASE WHEN DTL.MEET_TM_RULE = 'Y' THEN DTL.MEET_TM_RULE ELSE ' ' END AS MEET_TM_RULE, ");
		sql.append("       MAST.AUDITOR || '-' || EMP_AUDITOR.EMP_NAME AS AUDITOR, ");
		sql.append("       MAST.REVIEWER_EMP_ID ||'-'|| EMP_REVIEW.EMP_NAME AS REVIEWER_EMP_ID, ");
		sql.append("       TO_CHAR(MAST.REVIEW_DATE, 'YYYY/MM/DD') AS REVIEW_DATE ");
		sql.append("FROM TBSQM_RSA_MAST MAST ");
		sql.append("LEFT JOIN TBSQM_RSA_DTL DTL ON MAST.CUST_ID = DTL.CUST_ID AND MAST.AUDIT_TYPE = DTL.AUDIT_TYPE AND MAST.YEARQTR = DTL.YEARQTR ");
		sql.append("LEFT JOIN TBCRM_CUST_MAST CUST ON MAST.CUST_ID = CUST.CUST_ID ");
		sql.append("LEFT JOIN VWORG_AO_INFO AO ON MAST.AO_CODE = AO.AO_CODE ");
		sql.append("LEFT JOIN ( ");
		sql.append("  SELECT MAST.EMP_ID, COUNT(*) AS CNT ");
		sql.append("  FROM TBSQM_RSA_MAST MAST ");
		sql.append("  LEFT JOIN TBPMS_EMPLOYEE_REC_N E ON A.ROLE_ID = E.ROLE_ID AND A.EMP_ID = E.EMP_ID AND LAST_DAY(TO_DATE(A.YEARQTR || '01', 'YYYYMMDD')) BETWEEN E.START_TIME AND E.END_TIME ");
		sql.append("  LEFT JOIN TBSQM_RSA_DTL DTL ON  MAST.CUST_ID = DTL.CUST_ID AND MAST.AUDIT_TYPE = DTL.AUDIT_TYPE AND MAST.YEARQTR = DTL.YEARQTR ");
		sql.append("  WHERE 1 = 1 ");
		
		// #0544:WMS-CR-20210303-01_客戶服務定期查核新增高端二階主管放行權限
		if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("uhrm") >= 0) {
			if (StringUtils.isNotBlank(inputVO.getUhrmOP())) {
				sql.append("  AND (");
				sql.append("       EXISTS ( SELECT 1 FROM TBORG_MEMBER MT WHERE MAST.EMP_ID = MT.EMP_ID AND MT.DEPT_ID = :uhrmOP ) ");
				sql.append("    OR E.E_DEPT_ID = :uhrmOP ");
				sql.append("  )");
				condition.setObject("uhrmOP", inputVO.getUhrmOP());
			}
			
			sql.append("  AND EXISTS ( ");
			sql.append("    SELECT 1 ");
			sql.append("    FROM TBPMS_EMPLOYEE_REC_N UCN ");
			sql.append("    WHERE UCN.DEPT_ID <> UCN.E_DEPT_ID  ");
			sql.append("    AND LAST_DAY(TO_DATE(:yearQtr || '01', 'YYYYMMDD')) BETWEEN UCN.START_TIME AND UCN.END_TIME ");
			sql.append("    AND UCN.EMP_ID = MAST.EMP_ID ");
			sql.append("  ) ");
		} else {
			sql.append("  AND MAST.BRANCH_NBR = :bra_nbr ");
			
			// 分行權限者不可視UHRM相關資料
			if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("brh") >= 0) {
				// 分行權限者不可視UHRM相關資料
				if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("brh") >= 0) {
					sql.append("  AND NOT EXISTS ( ");
					sql.append("    SELECT 1 ");
					sql.append("    FROM TBPMS_EMPLOYEE_REC_N UCN ");
					sql.append("    WHERE UCN.DEPT_ID <> UCN.E_DEPT_ID  ");
					sql.append("    AND LAST_DAY(TO_DATE(:yearQtr || '01', 'YYYYMMDD')) BETWEEN UCN.START_TIME AND UCN.END_TIME ");
					sql.append("    AND UCN.EMP_ID = MAST.EMP_ID ");
					sql.append("  ) ");
				}
			}
			
			condition.setObject("bra_nbr", inputVO.getBranch_nbr());
		}
		
		sql.append("  AND MAST.YEARQTR = :yearQtr ");
		sql.append("  AND DTL.MEET_RULE IS NOT NULL ");
		sql.append("  AND MAST.AUDITED = 'Y' ");
		sql.append("  GROUP BY MAST.EMP_ID ");
		sql.append(") AO_CNT ON AO_CNT.EMP_ID = MAST.EMP_ID ");
		sql.append("LEFT JOIN TBPMS_EMPLOYEE_REC_N EMP_REVIEW ON EMP_REVIEW.EMP_ID = MAST.REVIEWER_EMP_ID AND LAST_DAY(TO_DATE(:yearQtr||'01','YYYYMMDD')) BETWEEN EMP_REVIEW.START_TIME AND EMP_REVIEW.END_TIME ");
		sql.append("LEFT JOIN TBPMS_EMPLOYEE_REC_N EMP_AUDITOR ON EMP_AUDITOR.EMP_ID = MAST.AUDITOR AND LAST_DAY(TO_DATE(:yearQtr||'01','YYYYMMDD')) BETWEEN EMP_AUDITOR.START_TIME AND EMP_AUDITOR.END_TIME ");
		sql.append("LEFT JOIN TBPMS_EMPLOYEE_REC_N E ON MAST.EMP_ID = E.EMP_ID AND LAST_DAY(TO_DATE(MAST.YEARQTR || '01', 'YYYYMMDD')) BETWEEN E.START_TIME AND E.END_TIME ");
		sql.append("WHERE  1 = 1 ");
		
		// #0544:WMS-CR-20210303-01_客戶服務定期查核新增高端二階主管放行權限
		if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("uhrm") >= 0) {
			if (StringUtils.isNotBlank(inputVO.getUhrmOP())) {
				sql.append("  AND (");
				sql.append("       EXISTS ( SELECT 1 FROM TBORG_MEMBER MT WHERE MAST.EMP_ID = MT.EMP_ID AND MT.DEPT_ID = :uhrmOP ) ");
				sql.append("    OR E.E_DEPT_ID = :uhrmOP ");
				sql.append("  )");
				condition.setObject("uhrmOP", inputVO.getUhrmOP());
			}
			
			sql.append("  AND EXISTS ( ");
			sql.append("    SELECT 1 ");
			sql.append("    FROM TBPMS_EMPLOYEE_REC_N UCN ");
			sql.append("    WHERE UCN.DEPT_ID <> UCN.E_DEPT_ID  ");
			sql.append("    AND LAST_DAY(TO_DATE(:yearQtr || '01', 'YYYYMMDD')) BETWEEN UCN.START_TIME AND UCN.END_TIME ");
			sql.append("    AND UCN.EMP_ID = MAST.EMP_ID ");
			sql.append("  ) ");
		} else {
			sql.append("AND MAST.BRANCH_NBR = :bra_nbr ");
			
			// 分行權限者不可視UHRM相關資料
			if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("brh") >= 0) {
				if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("brh") >= 0) {
					sql.append("  AND NOT EXISTS ( ");
					sql.append("    SELECT 1 ");
					sql.append("    FROM TBPMS_EMPLOYEE_REC_N UCN ");
					sql.append("    WHERE UCN.DEPT_ID <> UCN.E_DEPT_ID  ");
					sql.append("    AND LAST_DAY(TO_DATE(:yearQtr || '01', 'YYYYMMDD')) BETWEEN UCN.START_TIME AND UCN.END_TIME ");
					sql.append("    AND UCN.EMP_ID = MAST.EMP_ID ");
					sql.append("  ) ");
				}
			}
			
			condition.setObject("bra_nbr", inputVO.getBranch_nbr());
		}
		
		sql.append("AND MAST.YEARQTR = :yearQtr ");
		sql.append("AND DTL.MEET_RULE IS NOT NULL AND MAST.AUDITED ='Y' ");
		sql.append("ORDER BY MAST.EMP_ID, TO_CHAR(MAST.AUDIT_DATE,'YYYY/MM/DD'), MAST.CUST_ID ");

		condition.setObject("yearQtr", inputVO.getYearQtr());
		condition.setQueryString(sql.toString());

		List<Map<String, Object>> data_list = dam.exeQuery(condition);

		if (data_list.isEmpty()) {
			this.sendRtnObject(outputVO);
			
			return;
		}

		String[] columnStr = {"AO_NAME", "AUDIT_DATE", "CUST_ID", "CUST_NAME", 
							  "SP_CUST_YN", "UP_KYC_YN", "EBILL_PICK_YN", "REC_YN", "COMM_RS_YN", 
							  "CONTACT_DATE", 
							  "MEET_RULE", 
							  "MEET_RULE_CUST", 
							  "MEET_TM_RULE", 
							  "AUDIT_REMARK", 
							  "AUDITOR", "REVIEWER_EMP_ID", "REVIEW_DATE"};
		
		index++;
		int AoCnt = 0;
		int detail = index;

		for (Map<String, Object> dataMap : data_list) {
			row = sheet.createRow(detail++);

			AoCnt = Integer.parseInt(dataMap.get("CNT").toString());

			for (int j = 0; j < columnStr.length; j++) {
				XSSFCell cell = row.createCell(j);
				cell.setCellStyle(style);
				
				switch (columnStr[j]) {
					case "CUST_ID":
						cell.setCellValue(DataFormat.getCustIdMaskForHighRisk(ObjectUtils.toString(dataMap.get("CUST_ID"))));
						break;
					default:
						cell.setCellValue((String) dataMap.get(columnStr[j]));
						
						break;
				}
			}

			if (detail == (index + AoCnt) && AoCnt > 1) { //小於一筆不需要合併儲存格
				sheet.addMergedRegion(new CellRangeAddress(index, detail - 1, 0, 0));
				index = detail;
			} else if (new BigDecimal(String.valueOf(AoCnt)).compareTo(BigDecimal.ONE) == 0) {
				index = detail;
			}
		}

		index++;

		String tempName = UUID.randomUUID().toString();
		
		wb.write(new FileOutputStream(new File(config.getServerHome(), config.getReportTemp() + tempName)));

		notifyClientToDownloadFile(config.getReportTemp().substring(1) + tempName, fileName);

		this.sendRtnObject(null);
	}

	/** 匯出EXCEL檔 **/
	public void exportRPTNew(Object body, IPrimitiveMap header) throws JBranchException, Exception {
		
		SQM320InputVO inputVO = (SQM320InputVO) body;
		SQM320OutputVO outputVO = new SQM320OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		ConfigAdapterIF config = (ConfigAdapterIF) PlatformContext.getBean("configAdapter");
		String yearQtr = getYearQtr(inputVO.getYearQtr());

		// #0544:WMS-CR-20210303-01_客戶服務定期查核新增高端二階主管放行權限
		String fileName  = "客戶服務定期查核報告(新報表)_" + yearQtr + "_" + getUserVariable(FubonSystemVariableConsts.LOGINID) + ".xlsx";
		String csvheader = yearQtr.substring(0, 4) + "年" + yearQtr.substring(4, 6) + "季";
		if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("uhrm") >= 0) {
			csvheader = csvheader + "           ";
		} else {
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			
			sql.append("SELECT BRANCH_NBR, BRANCH_NAME ");
			sql.append("FROM VWORG_DEFN_INFO ");
			sql.append("WHERE BRANCH_NBR = :bra_nbr  ");

			condition.setObject("bra_nbr", inputVO.getBranch_nbr());
			
			condition.setQueryString(sql.toString());
			
			List<Map<String, Object>> BR_list = dam.exeQuery(condition);

			csvheader = csvheader + "           " + ObjectUtils.toString(BR_list.get(0).get("BRANCH_NAME")) + "           ";
		}
		
		csvheader = csvheader + "客戶服務定期查核報告";
		// ====
		
		XSSFWorkbook wb = new XSSFWorkbook();
		
		//設定SHEET名稱
		XSSFSheet sheet = wb.createSheet("客戶服務定期查核報告");
		sheet.setDefaultColumnWidth(25);
		sheet.setDefaultRowHeightInPoints(25);

		// 資料 CELL型式
		XSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		style.setBorderBottom((short) 1);
		style.setBorderTop((short) 1);
		style.setBorderLeft((short) 1);
		style.setBorderRight((short) 1);

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

		String[] headerLineTop = {"專員姓名/員編", 
								  "回覆日期", 
								  "查核對象", 
								  "查核對象", 
								  "查核對象",
								  "查核對象",
								  "查核對象", 
								  "查核對象", 
								  "查核對象", 
								  "查核對象", 
								  "查核對象", 
								  "查核項目", 
								  "查核項目", 
								  "查核項目", 
								  "查核項目", 
								  "查核項目", 
								  "符合電話行銷規範", 
								  "查核日期", 
								  "查核方式", 
								  "電訪錄音序號",
								  "查核意見", 
								  "訪查者", 
								  "覆核者", 
								  "覆核日期"};
		
		String[] headerLineSec = {"專員姓名/員編", 
								  "回覆日期", 
								  "客戶ID", 
								  "客戶姓名", 
								  "特殊客戶",
								  "自取對帳單客戶", 
								  "E級客戶", 
								  "季底庫存資產減損客戶", 
								  "符合推介", 
								  "共同行銷", 
								  "訪談紀錄上所載時間", 
								  "訪談記錄內容查核", 
								  "\n1.確認客戶清楚投資損益狀況及理財往來情形", 
								  "\n2.個金RM無銷售非屬本行核准之金融商品", 
								  "\n3.個金RM無自行製作並提供對帳單", 
								  "\n4.「自取對帳單客戶名單」目前資產餘額確認", 
								  "符合電話行銷規範", 
								  "查核日期", 
								  "查核方式", 
								  "電訪錄音序號", 
								  "查核意見", 
								  "訪查者", 
								  "覆核者", 
								  "覆核日期"};

		String[] columnStr = {"AO_NAME", 
				              "AUDIT_DATE", 
				              "CUST_ID", 
				              "CUST_NAME", 
				              "MAST_SP_CUST_YN",
							  "EBILL_PICK_YN", 
							  "CUST_E_NOREC_FLAG", 
							  "AST_LOSS_NOREC_FLAG", 
							  "REC_YN", 
							  "COMM_RS_YN", 
							  "CONTACT_DATE", 
							  "MEET_RULE", 
							  "MEET_RULE_CUST1", 
							  "MEET_RULE_CUST2", 
							  "MEET_RULE_CUST3", 
							  "MEET_RULE_CUST4", 
							  "MEET_TM_RULE", 
							  "MEET_DATE", 
							  "MEET_TYPE", 
							  "RECORD_SEQ", 
							  "AUDIT_REMARK", 
							  "AUDITOR", 
							  "REVIEWER_EMP_ID", 
							  "REVIEW_DATE"};


		String[] headerLineLOOP = {csvheader};
		
		Integer index = 0; // 行數	
		XSSFRow row = sheet.createRow(index); // Heading

		/*** 標題 1 : "yyyy年Qx季          (分行)           客戶服務定期查核報告" START ***/
		for (int i = 0; i < headerLineTop.length; i++) {
			XSSFCell cell = row.createCell(i);
			cell.setCellStyle(headingStyle);
			cell.setCellValue(headerLineLOOP[0]);
		}
		
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, columnStr.length - 1));
		/*** 標題 1 END ***/
		
		index++;
		
		/*** 標題 2 START ***/
		Integer startFlag = 0;
		Integer endFlag = 0;
		ArrayList<String> tempList = new ArrayList<String>(); //比對用
		
		row = sheet.createRow(index);
		row.setHeightInPoints(25);
		for (int i = 0; i < headerLineTop.length; i++) {
			String headerLine = headerLineTop[i];
			if (tempList.indexOf(headerLine) < 0) {
				tempList.add(headerLine);
				XSSFCell cell = row.createCell(i);
				cell.setCellStyle(headingStyle);
				cell.setCellValue(headerLineTop[i]);

				if (endFlag != 0) {
					sheet.addMergedRegion(new CellRangeAddress(1, 1, startFlag, endFlag)); // firstRow, endRow, firstColumn, endColumn
				} 
				
				startFlag = i;
				endFlag = 0;
			} else {
				endFlag = i;
			}
		}
		/*** 標題 2 END ***/
		
		index++;
		
		/*** 標題 3 START ***/
		row = sheet.createRow(index);
		for (int i = 0; i < headerLineSec.length; i++) {			
			XSSFCell cell = row.createCell(i);
			cell.setCellStyle(headingStyle);
			cell.setCellValue(headerLineSec[i]);
			
			if (StringUtils.equals(headerLineSec[i], headerLineTop[i])) {
				sheet.addMergedRegion(new CellRangeAddress(1, 2, i, i)); // firstRow, endRow, firstColumn, endColumn
			}
				
		}
		/*** 標題 3 END ***/

		// Data row
		condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT DISTINCT MAST.EMP_ID, ");
		sql.append("       MAST.EMP_ID || '-' || AO.EMP_NAME AS AO_NAME, ");
		sql.append("       TO_CHAR(MAST.AUDIT_DATE, 'YYYY/MM/DD') AS AUDIT_DATE, ");
		sql.append("       MAST.CUST_ID, ");
		sql.append("       CUST.CUST_NAME, ");
		sql.append("       MAST.NOTE_SP_CUST_YN AS SP_CUST_YN, ");
		sql.append("       MAST.SP_CUST_YN AS MAST_SP_CUST_YN, ");
		sql.append("       MAST.UP_KYC_YN, ");
		sql.append("       MAST.EBILL_PICK_YN, ");
		sql.append("       MAST.CUST_E_NOREC_FLAG, ");
		sql.append("       MAST.AST_LOSS_NOREC_FLAG, ");
		sql.append("       MAST.NOTE_REC_YN AS REC_YN, ");
		sql.append("       MAST.NOTE_BASE_FLAG AS COMM_RS_YN, ");
		sql.append("       DTL.LEAD_TYPE, ");
		sql.append("       DTL.AUDIT_REMARK, ");
		sql.append("       AO_CNT.CNT, ");
		sql.append("       TO_CHAR(DTL.CONTACT_DATE, 'YYYY/MM/DD') AS CONTACT_DATE, ");
		sql.append("       DTL.MEET_RULE, ");
		sql.append("       DTL.MEET_RULE_CUST1, ");
		sql.append("       DTL.MEET_RULE_CUST2, ");
		sql.append("       DTL.MEET_RULE_CUST3, ");
		sql.append("       DTL.MEET_RULE_CUST4, ");
		sql.append("       CASE WHEN DTL.MEET_TM_RULE = 'Y' THEN DTL.MEET_TM_RULE ELSE ' ' END AS MEET_TM_RULE, ");
		sql.append("       DTL.CMU_TYPE, ");
		sql.append("       MAST.AUDITOR || '-' || EMP_AUDITOR.EMP_NAME AS AUDITOR, ");
		sql.append("       MAST.REVIEWER_EMP_ID ||'-'|| EMP_REVIEW.EMP_NAME AS REVIEWER_EMP_ID, ");
		sql.append("       TO_CHAR(MAST.REVIEW_DATE, 'YYYY/MM/DD') AS REVIEW_DATE, ");
		sql.append("       TO_CHAR(DTL.MEET_DATE, 'YYYY/MM/DD') AS MEET_DATE, ");
		sql.append("       DTL.MEET_TYPE, ");
		sql.append("       DTL.RECORD_SEQ ");
		sql.append("FROM TBSQM_RSA_MAST MAST ");
		sql.append("LEFT JOIN TBSQM_RSA_DTL DTL ON MAST.CUST_ID = DTL.CUST_ID AND MAST.AUDIT_TYPE = DTL.AUDIT_TYPE AND MAST.YEARQTR = DTL.YEARQTR ");
		sql.append("LEFT JOIN TBCRM_CUST_MAST CUST ON MAST.CUST_ID = CUST.CUST_ID ");
		sql.append("LEFT JOIN VWORG_AO_INFO AO ON MAST.AO_CODE = AO.AO_CODE ");
		sql.append("LEFT JOIN ( ");
		sql.append("  SELECT MAST.EMP_ID, COUNT(*) AS CNT ");
		sql.append("  FROM TBSQM_RSA_MAST MAST ");
		sql.append("  LEFT JOIN TBPMS_EMPLOYEE_REC_N E ON MAST.EMP_ID = E.EMP_ID AND LAST_DAY(TO_DATE(MAST.YEARQTR || '01', 'YYYYMMDD')) BETWEEN E.START_TIME AND E.END_TIME ");
		sql.append("  LEFT JOIN TBSQM_RSA_DTL DTL ON MAST.CUST_ID = DTL.CUST_ID AND MAST.AUDIT_TYPE = DTL.AUDIT_TYPE AND MAST.YEARQTR = DTL.YEARQTR ");
		sql.append("  WHERE 1 = 1 ");
		
		// #0544:WMS-CR-20210303-01_客戶服務定期查核新增高端二階主管放行權限
		if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("uhrm") >= 0) {
			if (StringUtils.isNotBlank(inputVO.getUhrmOP())) {
				sql.append("  AND (");
				sql.append("       EXISTS ( SELECT 1 FROM TBORG_MEMBER MT WHERE MAST.EMP_ID = MT.EMP_ID AND MT.DEPT_ID = :uhrmOP ) ");
				sql.append("    OR E.E_DEPT_ID = :uhrmOP ");
				sql.append("  )");
				condition.setObject("uhrmOP", inputVO.getUhrmOP());
			}
			
			sql.append("  AND EXISTS ( ");
			sql.append("    SELECT 1 ");
			sql.append("    FROM TBPMS_EMPLOYEE_REC_N UCN ");
			sql.append("    WHERE UCN.DEPT_ID <> UCN.E_DEPT_ID  ");
			sql.append("    AND LAST_DAY(TO_DATE(:yearQtr || '01', 'YYYYMMDD')) BETWEEN UCN.START_TIME AND UCN.END_TIME ");
			sql.append("    AND UCN.EMP_ID = MAST.EMP_ID ");
			sql.append("  ) ");
		} else {
			sql.append("  AND MAST.BRANCH_NBR = :bra_nbr ");
			
			// 分行權限者不可視UHRM相關資料
			if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("brh") >= 0) {
				// 分行權限者不可視UHRM相關資料
				if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("brh") >= 0) {
					sql.append("  AND NOT EXISTS ( ");
					sql.append("    SELECT 1 ");
					sql.append("    FROM TBPMS_EMPLOYEE_REC_N UCN ");
					sql.append("    WHERE UCN.DEPT_ID <> UCN.E_DEPT_ID  ");
					sql.append("    AND LAST_DAY(TO_DATE(:yearQtr || '01', 'YYYYMMDD')) BETWEEN UCN.START_TIME AND UCN.END_TIME ");
					sql.append("    AND UCN.EMP_ID = MAST.EMP_ID ");
					sql.append("  ) ");
				}
			}
			
			condition.setObject("bra_nbr", inputVO.getBranch_nbr());
		}
		
		sql.append("  AND MAST.YEARQTR = :yearQtr ");
		sql.append("  AND DTL.MEET_RULE IS NOT NULL ");
		sql.append("  AND MAST.AUDITED = 'Y' ");
		sql.append("  GROUP BY MAST.EMP_ID ");
		sql.append(") AO_CNT ON AO_CNT.EMP_ID = MAST.EMP_ID ");
		sql.append("LEFT JOIN TBPMS_EMPLOYEE_REC_N EMP_REVIEW ON EMP_REVIEW.EMP_ID = MAST.REVIEWER_EMP_ID AND LAST_DAY(TO_DATE(:yearQtr||'01','YYYYMMDD')) BETWEEN EMP_REVIEW.START_TIME AND EMP_REVIEW.END_TIME ");
		sql.append("LEFT JOIN TBPMS_EMPLOYEE_REC_N EMP_AUDITOR ON EMP_AUDITOR.EMP_ID = MAST.AUDITOR AND LAST_DAY(TO_DATE(:yearQtr||'01','YYYYMMDD')) BETWEEN EMP_AUDITOR.START_TIME AND EMP_AUDITOR.END_TIME ");
		sql.append("LEFT JOIN TBPMS_EMPLOYEE_REC_N E ON MAST.EMP_ID = E.EMP_ID AND LAST_DAY(TO_DATE(MAST.YEARQTR || '01', 'YYYYMMDD')) BETWEEN E.START_TIME AND E.END_TIME ");
		sql.append("WHERE 1 = 1 ");
		
		// #0544:WMS-CR-20210303-01_客戶服務定期查核新增高端二階主管放行權限
		if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("uhrm") >= 0) {
			if (StringUtils.isNotBlank(inputVO.getUhrmOP())) {
				sql.append("  AND (");
				sql.append("       EXISTS ( SELECT 1 FROM TBORG_MEMBER MT WHERE MAST.EMP_ID = MT.EMP_ID AND MT.DEPT_ID = :uhrmOP ) ");
				sql.append("    OR E.E_DEPT_ID = :uhrmOP ");
				sql.append("  )");
				condition.setObject("uhrmOP", inputVO.getUhrmOP());
			}
			
			sql.append("  AND EXISTS ( ");
			sql.append("    SELECT 1 ");
			sql.append("    FROM TBPMS_EMPLOYEE_REC_N UCN ");
			sql.append("    WHERE UCN.DEPT_ID <> UCN.E_DEPT_ID  ");
			sql.append("    AND LAST_DAY(TO_DATE(:yearQtr || '01', 'YYYYMMDD')) BETWEEN UCN.START_TIME AND UCN.END_TIME ");
			sql.append("    AND UCN.EMP_ID = MAST.EMP_ID ");
			sql.append("  ) ");
		} else {
			sql.append("  AND MAST.BRANCH_NBR = :bra_nbr ");
			
			// 分行權限者不可視UHRM相關資料
			if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("brh") >= 0) {
				if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("brh") >= 0) {
					sql.append("  AND NOT EXISTS ( ");
					sql.append("    SELECT 1 ");
					sql.append("    FROM TBPMS_EMPLOYEE_REC_N UCN ");
					sql.append("    WHERE UCN.DEPT_ID <> UCN.E_DEPT_ID  ");
					sql.append("    AND LAST_DAY(TO_DATE(:yearQtr || '01', 'YYYYMMDD')) BETWEEN UCN.START_TIME AND UCN.END_TIME ");
					sql.append("    AND UCN.EMP_ID = MAST.EMP_ID ");
					sql.append("  ) ");
				}
			}
			
			condition.setObject("bra_nbr", inputVO.getBranch_nbr());
		}
		
		sql.append("AND MAST.YEARQTR = :yearQtr ");
		sql.append("AND DTL.MEET_RULE IS NOT NULL ");
		sql.append("AND MAST.AUDITED ='Y' ");
		sql.append("ORDER BY MAST.EMP_ID, TO_CHAR(MAST.AUDIT_DATE, 'YYYY/MM/DD'), MAST.CUST_ID ");

		condition.setObject("yearQtr", inputVO.getYearQtr());
		condition.setQueryString(sql.toString());

		List<Map<String, Object>> data_list = dam.exeQuery(condition);

		if (data_list.isEmpty()) {
			this.sendRtnObject(outputVO);
			return;
		}
		
		//資料開始
		index++;
		int AoCnt = 0;
		int detail = index;
		
		XmlInfo xmlInfo = new XmlInfo();

		for (Map<String, Object> dataMap : data_list) {
			row = sheet.createRow(detail++);

			AoCnt = Integer.parseInt(dataMap.get("CNT").toString());

			for (int j = 0; j < columnStr.length; j++) {
				XSSFCell cell = row.createCell(j);
				cell.setCellStyle(style);
				
				switch (columnStr[j]) {
					case "CUST_ID":
						cell.setCellValue(DataFormat.getCustIdMaskForHighRisk(ObjectUtils.toString(dataMap.get("CUST_ID"))));
						break;
					case "MEET_TYPE":
						cell.setCellValue((String) xmlInfo.getVariable("SQM.MEET_TYPE", (String) dataMap.get("MEET_TYPE"), "F3"));
						break;
					default:
						cell.setCellValue((String) dataMap.get(columnStr[j]));
						break;
				}
			}

			if (detail == (index + AoCnt) && AoCnt > 1) { //小於一筆不需要合併儲存格
				sheet.addMergedRegion(new CellRangeAddress(index, detail - 1, 0, 0));
				index = detail;
			} else if (new BigDecimal(String.valueOf(AoCnt)).compareTo(BigDecimal.ONE) == 0) {
				index = detail;
			}
		}

		index++;

		String tempName = UUID.randomUUID().toString();

		wb.write(new FileOutputStream(new File(config.getServerHome(), config.getReportTemp() + tempName)));

		notifyClientToDownloadFile(config.getReportTemp().substring(1) + tempName, fileName);

		this.sendRtnObject(null);
	}

	public BigDecimal totalAuditedCnt(String yearQtr, String empID, String type) throws DAOException, JBranchException {

		BigDecimal cnt = new BigDecimal(0);

		DataAccessManager dam = this.getDataAccessManager();
		StringBuffer sql = new StringBuffer();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		sql.append("SELECT COUNT(*) AS CNT ");
		sql.append("FROM TBSQM_RSA_MAST ");
		sql.append("WHERE YEARQTR = :yearQtr ");
		sql.append("AND AUDIT_TYPE = '1' ");
		sql.append("AND EMP_ID  = :emp_id ");
		sql.append("AND AUDITED = 'Y' ");

		if (type.equals(SPECIAL_CUST)) {
			sql.append("AND SP_CUST_YN = 'Y' ");
		}

		condition.setObject("yearQtr", yearQtr);
		condition.setObject("emp_id", empID);
		condition.setQueryString(sql.toString());

		List<Map<String, Object>> list = dam.exeQuery(condition);

		if (CollectionUtils.isNotEmpty(list) && list.size() > 0) {
			cnt = (BigDecimal) list.get(0).get("CNT");
		}

		return cnt;
	}

	/** 查詢待處理件數 => 查無功能使用 **/
	public void getOwnerFlag(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		SQM320InputVO inputVO = (SQM320InputVO) body;
		SQM320OutputVO outputVO = new SQM320OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		String ID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);
		
		sql.append("select case when count(*) > 0 then 'Y' else 'N' end ownerFlag  ");
		sql.append("from TBSQM_RSA_MAST ");
		sql.append("where REVIEWER_EMP_ID = :emp_id ");
		sql.append("and YEARQTR =:yearQtr ");

		condition.setObject("emp_id", ID);
		condition.setObject("yearQtr", inputVO.getYearQtr());

		condition.setQueryString(sql.toString());
		
		List<Map<String, Object>> list = dam.exeQuery(condition);
		outputVO.setOwnerFlag(ObjectUtils.toString(list.get(0).get("OWNERFLAG")));

		sendRtnObject(outputVO);
	}
}