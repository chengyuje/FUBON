package com.systex.jbranch.app.server.fps.cam200;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBCAM_EXAMRECORDVO;
import com.systex.jbranch.app.common.fps.table.TBCAM_EXAMRECORD_DETAILPK;
import com.systex.jbranch.app.common.fps.table.TBCAM_EXAMRECORD_DETAILVO;
import com.systex.jbranch.app.common.fps.table.TBCAM_SFA_LEADSVO;
import com.systex.jbranch.app.common.fps.table.TBCAM_SFA_LEADS_RESVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_NOTEVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_VISIT_RECORDVO;
import com.systex.jbranch.app.common.fps.table.TBSYS_QST_ANSWERPK;
import com.systex.jbranch.app.common.fps.table.TBSYS_QST_ANSWERVO;
import com.systex.jbranch.app.server.fps.crm131.CRM131;
import com.systex.jbranch.app.server.fps.pms110.PMS110;
import com.systex.jbranch.app.server.fps.pms110.PMS110InputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author Ocean
 * @date 2016/05/19
 * 
 */
@Component("cam200")
@Scope("request")
public class CAM200 extends FubonWmsBizLogic {
	
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CAM200.class);

	SimpleDateFormat sdfYmd = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sdfHms = new SimpleDateFormat("HH:mm:ss");
    SimpleDateFormat sdfDms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
	private StringBuffer getListSQL (String type) throws JBranchException {
		
		StringBuffer sb = new StringBuffer();
		sb.append("WITH CUS_VICO AS ( ");
		sb.append("  SELECT VIP_DEGREE, CON_DEGREE FROM TBCRM_CUST_MAST WHERE CUST_ID = :custID ");
		sb.append("), CUS_KYC_HIS AS ( ");
		sb.append("  SELECT COUNT(1) AS COUNTS, :custID AS CUST_ID FROM TBCRM_CUST_DKYC_DATA WHERE CUST_ID = :custID ");
		sb.append("), CAMP_RESPONSE AS ( ");
		sb.append("	 SELECT CAMPAIGN_ID, LEAD_STATUS, RESPONSE_NAME FROM TBCAM_SFA_CAMP_RESPONSE ");
		sb.append(") ");
		sb.append("SELECT INFO.EMP_ID, INFO.EMP_NAME, CAMP.CAMPAIGN_ID, CAMP.STEP_ID, CAMP.CAMPAIGN_NAME, CAMP.CAMPAIGN_DESC, CAMP.SALES_PITCH, CAMP.LEAD_PARA1, CAMP.LEAD_PARA2, CAMP.EXAM_ID, CAMP.LEAD_RESPONSE_CODE, ");
		sb.append("       CASE WHEN CAMP.LEAD_PARA1 = 'C' AND KYC.KYC_COUNTS > 0 THEN 'Y' ELSE 'N' END AS KYC, ");
		sb.append("       CASE WHEN CAMP.LEAD_PARA1 = 'C' AND CKH.COUNTS = KYC.KYC_COUNTS THEN 'N' ELSE 'Y' END AS KYC_LENGTH_OPEN, ");
		sb.append("       CASE WHEN CAMP.EXAM_ID IS NULL THEN 'N' ELSE (SELECT CASE WHEN COUNT(1) > 0 THEN 'N' ELSE 'Y' END AS LEGTH FROM TBCAM_EXAMRECORD Q WHERE Q.EXAM_VERSION = CAMP.EXAM_ID AND Q.CUST_ID = LEAD.CUST_ID) END AS EXAMRECORD, ");
		sb.append("       CASE WHEN CAMP.EXAM_ID IS NULL THEN 'Y' ELSE (SELECT CASE WHEN COUNT(1) > 0 THEN 'Y' ELSE 'N' END AS LEGTH FROM TBSYS_QUESTIONNAIRE QN LEFT JOIN TBSYS_QST_QUESTION Q ON Q.QUESTION_VERSION = QN.QUESTION_VERSION LEFT JOIN TBCAM_EXAMRECORD CQ ON CQ.EXAM_VERSION = QN.EXAM_VERSION WHERE QN.EXAM_VERSION = CAMP.EXAM_ID AND CQ.CUST_ID = LEAD.CUST_ID) END AS EXAMRECORD_STATUS, ");
		sb.append("       CASE WHEN INSTR(LEAD.SFA_LEAD_ID, '_S', -1) > 0 THEN 'N' ELSE 'Y' END AS QUS_WRITE_OPEN, ");
		sb.append("       LEAD.END_DATE, LEAD.LEAD_STATUS, CR.RESPONSE_NAME, LEAD.LEAD_TYPE, LEAD.SFA_LEAD_ID, ");
		sb.append("       CASE WHEN (SELECT COUNT(1) FROM TBCAM_SFA_LEADS_VAR WHERE SFA_LEAD_ID = LEAD.SFA_LEAD_ID) > 0 THEN 'Y' ELSE 'N' END AS INFO, ");
		sb.append("       VAR_FIELD_LABEL1, VAR_FIELD_VALUE1, VAR_FIELD_LABEL2, VAR_FIELD_VALUE2, ");
		sb.append("       VAR_FIELD_LABEL3, VAR_FIELD_VALUE3, VAR_FIELD_LABEL4, VAR_FIELD_VALUE4, ");
		sb.append("       VAR_FIELD_LABEL5, VAR_FIELD_VALUE5, VAR_FIELD_LABEL6, VAR_FIELD_VALUE6, ");
		sb.append("       VAR_FIELD_LABEL7, VAR_FIELD_VALUE7, VAR_FIELD_LABEL8, VAR_FIELD_VALUE8, ");
		sb.append("       VAR_FIELD_LABEL9, VAR_FIELD_VALUE9, VAR_FIELD_LABEL10, VAR_FIELD_VALUE10, ");
		sb.append("       VAR_FIELD_LABEL11, VAR_FIELD_VALUE11, VAR_FIELD_LABEL12, VAR_FIELD_VALUE12, ");
		sb.append("       VAR_FIELD_LABEL13, VAR_FIELD_VALUE13, VAR_FIELD_LABEL14, VAR_FIELD_VALUE14, ");
		sb.append("       VAR_FIELD_LABEL15, VAR_FIELD_VALUE15, VAR_FIELD_LABEL16, VAR_FIELD_VALUE16, ");
		sb.append("       VAR_FIELD_LABEL17, VAR_FIELD_VALUE17, VAR_FIELD_LABEL18, VAR_FIELD_VALUE18, ");
		sb.append("       VAR_FIELD_LABEL19, VAR_FIELD_VALUE19, VAR_FIELD_LABEL20, VAR_FIELD_VALUE20, ");
		sb.append("       VISIT_SEQ, VISITOR_ROLE, CMU_TYPE, VISIT_MEMO, VISIT_DT AS VISIT_DATE, VISIT_DT AS VISIT_TIME, VISIT_CREPLY, ");
		
		//== 20201127 ADD BY OCEAN => 0000415: WMS-CR-20200908-01_自動產製線上留資名單報表及留資客戶承作消金商品導入消金Pipeline功能
		sb.append("       LEAD.PLAN_SEQ, LEAD.PLAN_YEARMON, LEAD.PLAN_CENTER_ID, LEAD.PLAN_AREA_ID, LEAD.EST_PRD, LEAD.EST_AMT, LEAD.PLAN_EMP_ID || '-' || PTE.EMP_NAME || '(' || PTE.ROLE_NAME || ')' AS PLAN_EMP_ID ");

		sb.append("FROM TBCAM_SFA_LEADS LEAD ");
		sb.append("INNER JOIN TBCAM_SFA_CAMPAIGN CAMP ON LEAD.CAMPAIGN_ID = CAMP.CAMPAIGN_ID AND LEAD.STEP_ID = CAMP.STEP_ID ");
		sb.append("LEFT JOIN VWORG_DEFN_INFO DEFN ON LEAD.BRANCH_ID = DEFN.BRANCH_NBR ");
		sb.append("LEFT JOIN TBCAM_SFA_LEADS_VAR VAR ON LEAD.SFA_LEAD_ID = VAR.SFA_LEAD_ID ");
		sb.append("LEFT JOIN TBCRM_CUST_VISIT_RECORD REC ON LEAD.CUST_MEMO_SEQ = REC.VISIT_SEQ AND LEAD.CUST_ID = REC.CUST_ID ");
		sb.append("LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO INFO ON LEAD.EMP_ID = INFO.EMP_ID ");
		sb.append("LEFT JOIN TBSYSSECUROLPRIASS IASS ON INFO.ROLE_ID = IASS.ROLEID ");
		sb.append("LEFT JOIN (SELECT COUNT(1) AS KYC_COUNTS, :custID AS CUST_ID FROM (SELECT A.QSTN_ID FROM TBCRM_DKYC_QSTN_SET A LEFT JOIN TBCRM_DKYC_ANS_SET B ON A.QSTN_ID = B.QSTN_ID WHERE SYSDATE BETWEEN A.VALID_BGN_DATE AND A.VALID_END_DATE AND (INSTR(A.VIP_DEGREE, (SELECT VIP_DEGREE FROM CUS_VICO)) > 0  AND INSTR(A.AUM_DEGREE, (SELECT CON_DEGREE FROM CUS_VICO)) > 0) GROUP BY A.QSTN_ID)) KYC ON LEAD.CUST_ID = KYC.CUST_ID ");
		sb.append("LEFT JOIN CUS_KYC_HIS CKH ON LEAD.CUST_ID = CKH.CUST_ID ");
		sb.append("LEFT JOIN CAMP_RESPONSE CR ON (CAMP.CAMPAIGN_ID = CR.CAMPAIGN_ID OR CAMP.LEAD_RESPONSE_CODE = CR.CAMPAIGN_ID) AND LEAD.LEAD_STATUS = CR.LEAD_STATUS ");
		sb.append("LEFT JOIN ( ");
		sb.append("  SELECT MEM.EMP_ID, MEM.EMP_NAME, LISTAGG(R.ROLE_NAME, '/') WITHIN GROUP (ORDER BY PRIV.ROLEID DESC) AS ROLE_NAME ");
		sb.append("  FROM TBORG_MEMBER MEM ");
		sb.append("  INNER JOIN TBORG_MEMBER_ROLE MR ON MEM.EMP_ID = MR.EMP_ID ");
		sb.append("  INNER JOIN TBORG_ROLE R ON MR.ROLE_ID = R.ROLE_ID ");
		sb.append("  INNER JOIN TBSYSSECUROLPRIASS PRIV ON R.ROLE_ID = PRIV.ROLEID ");
		sb.append("  GROUP BY MEM.EMP_ID, MEM.EMP_NAME ");
		sb.append(") PTE ON LEAD.PLAN_EMP_ID = PTE.EMP_ID ");
		sb.append("WHERE LEAD.CUST_ID = :custID ");
		
		// 依系統角色決定下拉選單可視範圍
		XmlInfo xmlInfo = new XmlInfo();
		if (xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) { // 業務處主管
			sb.append("AND DEFN.REGION_CENTER_ID IN (:rcIdList) ");
		} else if (xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) { // 營運督導
			sb.append("AND DEFN.BRANCH_AREA_ID IN (:opIdList) ");
		} else if (xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) { // 分行主管
			sb.append("AND DEFN.BRANCH_NBR IN (:brIdList) ");
		} else if (xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) { // 理專
			sb.append("AND DEFN.BRANCH_NBR IN (:brIdList) ");
			sb.append("AND (");
			sb.append("     LEAD.EMP_ID = :empID ");
			sb.append("  OR IASS.PRIVILEGEID = '004'");
			
			// 20210629 add by Ocean => #0662: WMS-CR-20210624-01_配合DiamondTeam專案調整系統模組功能_組織+客管
			sb.append("  OR EXISTS ( ");
			sb.append("    SELECT AO.AO_CODE, DT_A.EMP_ID, DT_A.BRANCH_NBR ");
			sb.append("    FROM TBORG_DIAMOND_TEAM DT_A ");
			sb.append("    INNER JOIN TBORG_SALES_AOCODE AO ON DT_A.EMP_ID = AO.EMP_ID ");
			sb.append("    WHERE EXISTS (SELECT 1 FROM TBORG_DIAMOND_TEAM DT_B WHERE DT_A.BRANCH_NBR = DT_B.BRANCH_NBR AND DT_A.TEAM_TYPE = DT_B.TEAM_TYPE AND EMP_ID = :empID) ");
			sb.append("    AND LEAD.EMP_ID = DT_A.EMP_ID ");
			sb.append("  ) ");
			
			sb.append(") ");
		} else if (xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE)) || 
				   xmlInfo.doGetVariable("FUBONSYS.FAIA_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE)) || 
				   xmlInfo.doGetVariable("FUBONSYS.PAO_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) { // PSOP/faia
			sb.append("AND DEFN.BRANCH_NBR IN (:brIdList) ");
			sb.append("AND LEAD.EMP_ID = :empID ");
		} else if (xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) { // UHRM
			sb.append("AND LEAD.EMP_ID = :empID ");
		} else { // 總行人員
			
		}
		
		sb.append("AND LEAD.LEAD_STATUS <> 'TR' ");
		
		sb.append("AND TRUNC(LEAD.START_DATE) <= TRUNC(SYSDATE) ");
		
		if ("getBeContactList".equals(type)) {
			sb.append("AND ( ");
			sb.append("      LEAD.LEAD_STATUS < '03' ");
			sb.append("  AND (");
			sb.append("       TO_CHAR(LEAD.END_DATE, 'yyyyMMdd') >= TO_CHAR(SYSDATE, 'yyyyMMdd') ");
			sb.append("    OR TO_CHAR(LEAD.END_DATE, 'yyyyMMdd') >= TO_CHAR(SYSDATE-10, 'yyyyMMdd')");
			sb.append("  ) "); 
			sb.append(") ");
			sb.append("ORDER BY LEAD.END_DATE DESC ");
		} else if ("getExpiredList".equals(type)) {
			sb.append("AND ( ");
			sb.append("  LEAD.LEAD_STATUS < '03' ");
			sb.append("  AND TO_CHAR(LEAD.END_DATE, 'yyyyMMdd') < TO_CHAR(SYSDATE-10, 'yyyyMMdd') ");
			sb.append(") ");
			sb.append("ORDER BY LEAD.END_DATE DESC ");
		} else if ("getClosedList".equals(type)) {
			sb.append("AND LEAD.LEAD_STATUS >= '03' ");
			sb.append("ORDER BY (CASE WHEN LEAD.LEAD_STATUS = '03C' THEN 1 ELSE 3 END), LEAD.END_DATE DESC ");
		}

		return sb;
	}
	
	public void getBeContaceListByCRM631 (Object body, IPrimitiveMap header) throws JBranchException {
		
		CAM200InputVO inputVO = (CAM200InputVO) body;
		CAM200OutputVO outputVO = new CAM200OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT DISTINCT CAMP.CAMPAIGN_ID, CAMP.STEP_ID, CAMP.CAMPAIGN_NAME, CAMP.LEAD_TYPE, LEAD.END_DATE ");
		sb.append("FROM TBCAM_SFA_LEADS LEAD ");
		sb.append("INNER JOIN TBCAM_SFA_CAMPAIGN CAMP ON LEAD.CAMPAIGN_ID = CAMP.CAMPAIGN_ID AND LEAD.STEP_ID = CAMP.STEP_ID ");
		sb.append("LEFT JOIN VWORG_DEFN_INFO DEFN ON LEAD.BRANCH_ID = DEFN.BRANCH_NBR ");
		sb.append("LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO INFO ON LEAD.EMP_ID = INFO.EMP_ID ");
		sb.append("LEFT JOIN TBSYSSECUROLPRIASS IASS ON INFO.ROLE_ID = IASS.ROLEID ");
		sb.append("WHERE LEAD.CUST_ID = :custID ");
		sb.append("AND ((LEAD.LEAD_TYPE <> '04' AND LEAD.LEAD_STATUS < '03' AND (TO_CHAR(LEAD.END_DATE, 'yyyyMMdd') >= TO_CHAR(SYSDATE, 'yyyyMMdd') OR TO_CHAR(LEAD.END_DATE, 'yyyyMMdd') >= TO_CHAR(SYSDATE-10, 'yyyyMMdd'))) ");
		sb.append("     OR (LEAD.LEAD_TYPE = '04' AND (TO_CHAR(LEAD.END_DATE, 'yyyyMMdd') >= TO_CHAR(SYSDATE, 'yyyyMMdd') OR TO_CHAR(LEAD.END_DATE, 'yyyyMMdd') >= TO_CHAR(SYSDATE-10, 'yyyyMMdd')))) ");		
		
		sb.append("AND TRUNC(LEAD.START_DATE) <= TRUNC(SYSDATE) ");
		
		// 依系統角色決定下拉選單可視範圍
		XmlInfo xmlInfo = new XmlInfo();
		if (xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) { // 業務處主管
			sb.append("AND DEFN.REGION_CENTER_ID IN (:rcIdList) ");
		} else if (xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) { // 營運督導
			sb.append("AND DEFN.BRANCH_AREA_ID IN (:opIdList) ");
		} else if (xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) { // 分行主管
			sb.append("AND DEFN.BRANCH_NBR IN (:brIdList) ");
		} else if (xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) { // 理專
			sb.append("AND DEFN.BRANCH_NBR IN (:brIdList) ");
			sb.append("AND (LEAD.EMP_ID = :empID OR IASS.PRIVILEGEID = '004') ");
		} else if (xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE)) || 
				   xmlInfo.doGetVariable("FUBONSYS.FAIA_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE)) || 
				   xmlInfo.doGetVariable("FUBONSYS.PAO_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) { // PSOP/faia
			sb.append("AND DEFN.BRANCH_NBR IN (:brIdList) ");
			sb.append("AND LEAD.EMP_ID = :empID ");
		} else if (xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) { // UHRM
			sb.append("AND LEAD.EMP_ID = :empID ");
		} else { // 總行人員
			
		}
		
		sb.append("AND LEAD.LEAD_STATUS <> 'TR' ");
		sb.append("ORDER BY LEAD.END_DATE DESC ");
		queryCondition.setObject("custID", inputVO.getCustID());
		queryCondition.setQueryString(sb.toString());
		
		WorkStation ws = DataManager.getWorkStation(uuid);
		// 依系統角色決定下拉選單可視範圍
		if (xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) { // 業務處主管
			queryCondition.setObject("rcIdList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
		} else if (xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) { // 營運督導
			queryCondition.setObject("opIdList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
		} else if (xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) { // 分行主管
			queryCondition.setObject("brIdList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		} else if (xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE)) || 
				   xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE)) ||
				   xmlInfo.doGetVariable("FUBONSYS.FAIA_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE)) || 
				   xmlInfo.doGetVariable("FUBONSYS.PAO_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) { // 理專/PSOP/faia
			queryCondition.setObject("brIdList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			queryCondition.setObject("empID", ws.getUser().getUserID());
		} else if (xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) { // UHRM
			queryCondition.setObject("empID", ws.getUser().getUserID());
		} else { // 總行人員
			
		}
		
		outputVO.setBeContactList(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	}
		
	public void getBeContactList (Object body, IPrimitiveMap header) throws JBranchException {
		
		WorkStation ws = DataManager.getWorkStation(uuid);
		CAM200InputVO inputVO = (CAM200InputVO) body;
		CAM200OutputVO outputVO = new CAM200OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		queryCondition.setQueryString(getListSQL("getBeContactList").toString());
		queryCondition.setObject("custID", inputVO.getCustID());
		
		// 依系統角色決定下拉選單可視範圍
		XmlInfo xmlInfo = new XmlInfo();
		if (xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) { // 業務處主管
			queryCondition.setObject("rcIdList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
		} else if (xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) { // 營運督導
			queryCondition.setObject("opIdList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
		} else if (xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) { // 分行主管
			queryCondition.setObject("brIdList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		} else if (xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE)) || 
				   xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE)) ||
				   xmlInfo.doGetVariable("FUBONSYS.FAIA_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE)) || 
				   xmlInfo.doGetVariable("FUBONSYS.PAO_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) { // 理專/PSOP/faia
			queryCondition.setObject("brIdList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			queryCondition.setObject("empID", ws.getUser().getUserID());
		} else if (xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) { // UHRM
			queryCondition.setObject("empID", ws.getUser().getUserID());
		} else { // 總行人員
			
		}

		outputVO.setBeContactList(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	}
	
	public void getExpiredList (Object body, IPrimitiveMap header) throws JBranchException {
		
		WorkStation ws = DataManager.getWorkStation(uuid);
		CAM200InputVO inputVO = (CAM200InputVO) body;
		CAM200OutputVO outputVO = new CAM200OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		queryCondition.setQueryString(getListSQL("getExpiredList").toString());
		queryCondition.setObject("custID", inputVO.getCustID());
		
		// 依系統角色決定下拉選單可視範圍
		XmlInfo xmlInfo = new XmlInfo();
		if (xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) { // 業務處主管
			queryCondition.setObject("rcIdList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
		} else if (xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) { // 營運督導
			queryCondition.setObject("opIdList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
		} else if (xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) { // 分行主管
			queryCondition.setObject("brIdList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		} else if (xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE)) || 
				   xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE)) ||
				   xmlInfo.doGetVariable("FUBONSYS.FAIA_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE)) || 
				   xmlInfo.doGetVariable("FUBONSYS.PAO_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) { // 理專/PSOP/faia
			queryCondition.setObject("brIdList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			queryCondition.setObject("empID", ws.getUser().getUserID());
		} else if (xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) { // UHRM
			queryCondition.setObject("empID", ws.getUser().getUserID());
		} else { // 總行人員
			
		}
		
//		outputVO.setExpiredList(dam.exeQuery(queryCondition));
		ResultIF list = dam.executePaging(queryCondition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		int totalPage_i = list.getTotalPage(); // 分頁用
		int totalRecord_i = list.getTotalRecord(); // 分頁用
		outputVO.setExpiredList(list); // data
		outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
		outputVO.setTotalPage(totalPage_i);// 總頁次
		outputVO.setTotalRecord(totalRecord_i);// 總筆數
		
		this.sendRtnObject(outputVO);
	}
	
	public void getClosedList (Object body, IPrimitiveMap header) throws JBranchException {
	
		WorkStation ws = DataManager.getWorkStation(uuid);
		CAM200InputVO inputVO = (CAM200InputVO) body;
		CAM200OutputVO outputVO = new CAM200OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		queryCondition.setQueryString(getListSQL("getClosedList").toString());
		queryCondition.setObject("custID", inputVO.getCustID());
		// 依系統角色決定下拉選單可視範圍
		XmlInfo xmlInfo = new XmlInfo();
		if (xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) { // 業務處主管
			queryCondition.setObject("rcIdList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
		} else if (xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) { // 營運督導
			queryCondition.setObject("opIdList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
		} else if (xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) { // 分行主管
			queryCondition.setObject("brIdList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		} else if (xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE)) || 
				   xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE)) ||
				   xmlInfo.doGetVariable("FUBONSYS.FAIA_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE)) || 
				   xmlInfo.doGetVariable("FUBONSYS.PAO_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) { // 理專/PSOP/faia
			queryCondition.setObject("brIdList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			queryCondition.setObject("empID", ws.getUser().getUserID());
		} else if (xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) { // UHRM
			queryCondition.setObject("empID", ws.getUser().getUserID());
		} else { // 總行人員
			
		}
		
//		outputVO.setClosedList(dam.exeQuery(queryCondition));
		ResultIF list = dam.executePaging(queryCondition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		int totalPage_i = list.getTotalPage(); // 分頁用
		int totalRecord_i = list.getTotalRecord(); // 分頁用
		outputVO.setClosedList(list); // data
		outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
		outputVO.setTotalPage(totalPage_i);// 總頁次
		outputVO.setTotalRecord(totalRecord_i);// 總筆數
		
		this.sendRtnObject(outputVO);
	}
	
 	public void getResList (Object body, IPrimitiveMap header) throws JBranchException {
		
		CAM200InputVO inputVO = (CAM200InputVO) body;
		CAM200OutputVO outputVO = new CAM200OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT LEAD_STATUS, RESPONSE_NAME ");
		sb.append("FROM TBCAM_SFA_CAMP_RESPONSE ");
		sb.append("WHERE CAMPAIGN_ID IN (:responseCode, :campID) ");
		sb.append("AND RESPONSE_ENABLE = 'Y' ");
		
		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("responseCode", inputVO.getResponseCode());
		queryCondition.setObject("campID", inputVO.getCampID());
		
		outputVO.setResultList(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	}
	
	public String getFileSQL(String action) {
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT MAPP.CAMPAIGN_ID, MAPP.STEP_ID, MAPP.SFA_DOC_ID, FMAIN.DOC_NAME, DTL.DOC_FILE ");
		sb.append("FROM TBCAM_SFA_CAMP_DOC_MAPP MAPP ");
		sb.append("LEFT JOIN TBSYS_FILE_MAIN FMAIN ON MAPP.SFA_DOC_ID = FMAIN.DOC_ID ");
		sb.append("LEFT JOIN TBSYS_FILE_DETAIL DTL ON DTL.DOC_ID = FMAIN.DOC_ID ");
		sb.append("WHERE MAPP.CAMPAIGN_ID = :campID ");
		sb.append("AND MAPP.STEP_ID = :stepID ");
		sb.append("ORDER BY SFA_DOC_ID ");
		
		return sb.toString();
	}

	public void getFileList (Object body, IPrimitiveMap header) throws JBranchException, SQLException {
		
		CAM200InputVO inputVO = (CAM200InputVO) body;
		CAM200OutputVO outputVO = new CAM200OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT MAPP.CAMPAIGN_ID, MAPP.STEP_ID, MAPP.SFA_DOC_ID, FMAIN.DOC_NAME, DTL.DOC_FILE, DTL.DOC_FILE_TYPE, DTL.DOC_URL ");
		sb.append("FROM TBCAM_SFA_CAMP_DOC_MAPP MAPP ");
		sb.append("LEFT JOIN TBSYS_FILE_MAIN FMAIN ON MAPP.SFA_DOC_ID = FMAIN.DOC_ID ");
		sb.append("LEFT JOIN TBSYS_FILE_DETAIL DTL ON DTL.DOC_ID = FMAIN.DOC_ID ");
		sb.append("WHERE 1 = 1 ");
		sb.append("AND MAPP.CAMPAIGN_ID = :campID ");
		sb.append("AND MAPP.STEP_ID = :stepID ");
		sb.append("ORDER BY SFA_DOC_ID ");
		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("campID", inputVO.getCampID());
		queryCondition.setObject("stepID", inputVO.getStepID());
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		for(Map<String, Object> map : list) {
			if (map.get("DOC_FILE") != null) {
				Blob blob = (Blob) map.get("DOC_FILE");
				int blobLength = (int) blob.length();  
				byte[] blobAsBytes = blob.getBytes(1, blobLength);
				map.put("DOC_FILE", blobAsBytes);
				blob.free();
			}
		}
		outputVO.setFileList(list);
		
		this.sendRtnObject(outputVO);
	}
	
	public void getQuestionList (Object body, IPrimitiveMap header) throws JBranchException {
		
		CAM200InputVO inputVO = (CAM200InputVO) body;
		CAM200OutputVO outputVO = new CAM200OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT QN.EXAM_VERSION, QN.QUESTION_VERSION, QN.QST_NO, Q.QUESTION_DESC, Q.QUESTION_TYPE, Q.ANS_OTHER_FLAG, Q.ANS_MEMO_FLAG ");
		sb.append("FROM TBSYS_QUESTIONNAIRE QN ");
		sb.append("LEFT JOIN TBSYS_QST_QUESTION Q ON Q.QUESTION_VERSION = QN.QUESTION_VERSION ");
		sb.append("LEFT JOIN TBCAM_EXAMRECORD CQ ON CQ.EXAM_VERSION = QN.EXAM_VERSION AND CQ.CUST_ID = :custID ");
		sb.append("WHERE QN.EXAM_VERSION = :examVersion ");
		sb.append("ORDER BY QN.QST_NO ");
		
		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("examVersion", inputVO.getExamVersion());
		queryCondition.setObject("custID", inputVO.getCustID());
		
		outputVO.setQuestionList(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	}
	
	public void getAnswerList (Object body, IPrimitiveMap header) throws JBranchException {
		
		CAM200InputVO inputVO = (CAM200InputVO) body;
		CAM200OutputVO outputVO = new CAM200OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT ANS.QUESTION_VERSION, ANS.ANSWER_SEQ, ANS.ANSWER_DESC, ");
		sb.append("CASE WHEN DTL.RECORD_SEQ IS NULL THEN 'N' ELSE 'Y' END AS EXIST, DTL.REMARK ");
		sb.append("FROM TBSYS_QST_ANSWER ANS ");
		sb.append("LEFT JOIN (SELECT RECORD_SEQ, QUESTION_VERSION, ANSWER_SEQ, REMARK FROM TBCAM_EXAMRECORD_DETAIL WHERE RECORD_SEQ = (SELECT RECORD_SEQ FROM TBCAM_EXAMRECORD WHERE EXAM_VERSION = :examVersion AND CUST_ID = :custID)) DTL ");
		sb.append("ON ANS.QUESTION_VERSION = DTL.QUESTION_VERSION AND ANS.ANSWER_SEQ = DTL.ANSWER_SEQ ");
		sb.append("WHERE ANS.QUESTION_VERSION = :questionVersion ");
		sb.append("ORDER BY ANS.QUESTION_VERSION, ANS.ANSWER_SEQ ");
		
		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("questionVersion",inputVO.getQuestionVersion());
		queryCondition.setObject("examVersion",inputVO.getExamVersion());
		queryCondition.setObject("custID",inputVO.getCustID());

		outputVO.setAnswerList(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	}
	
	public void saveQuestionnaire (Object body, IPrimitiveMap header) throws JBranchException {
		WorkStation ws = DataManager.getWorkStation(uuid);
		CAM200InputVO inputVO = (CAM200InputVO) body;
		CAM200OutputVO outputVO = new CAM200OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		String recordSEQ = "";

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT SEQ, RECORD_SEQ ");
		sb.append("FROM TBCAM_EXAMRECORD ");
		sb.append("WHERE EXAM_VERSION = :examVersion ");
		sb.append("AND CUST_ID = :custID ");
		
		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("examVersion", inputVO.getExamVersion());
		queryCondition.setObject("custID", inputVO.getCustID());
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() > 0) {
			recordSEQ = (String) list.get(0).get("RECORD_SEQ");
			
			TBCAM_EXAMRECORDVO vo = new TBCAM_EXAMRECORDVO();
			vo = (TBCAM_EXAMRECORDVO) dam.findByPKey(TBCAM_EXAMRECORDVO.TABLE_UID, (String) list.get(0).get("SEQ"));
			vo.setModifier(ws.getUser().getUserID());
			vo.setLastupdate(new Timestamp(System.currentTimeMillis()));
			
			dam.update(vo);
		} else {
			recordSEQ = getSeqNum("TBCAM_EXAMRECORD_DETAIL");
			
			String pk = getSeqNum("TBCAM_EXAMRECORD");
			while(checkID(pk)){
				pk = getSeqNum("TBCAM_EXAMRECORD");
			}
			
			TBCAM_EXAMRECORDVO vo = new TBCAM_EXAMRECORDVO();
			vo.setSEQ(pk);
			vo.setEXAM_VERSION(inputVO.getExamVersion());
			vo.setCUST_ID(inputVO.getCustID());
			vo.setCUST_NAME(inputVO.getCustName());
			vo.setRECORD_SEQ(recordSEQ);
			
			dam.create(vo);
		}
		
		// DELETE ===
		sb = new StringBuffer();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb.append("DELETE FROM TBCAM_EXAMRECORD_DETAIL WHERE RECORD_SEQ = :recordSEQ ");
		
		condition.setQueryString(sb.toString());
		condition.setObject("recordSEQ", recordSEQ);
		dam.exeUpdate(condition);
		// ===
		
		for (Map<String, String> map : inputVO.getQuestionnaireList()) {
			TBSYS_QST_ANSWERVO aVO = new TBSYS_QST_ANSWERVO();
			TBSYS_QST_ANSWERPK aPK = new TBSYS_QST_ANSWERPK();
			aPK.setQUESTION_VERSION(map.get("questionVersion"));
			aPK.setANSWER_SEQ(new BigDecimal(map.get("custAnswer")));
			aVO.setcomp_id(aPK);
			aVO = (TBSYS_QST_ANSWERVO) dam.findByPKey(TBSYS_QST_ANSWERVO.TABLE_UID, aVO.getcomp_id());
			
			TBCAM_EXAMRECORD_DETAILVO vo = new TBCAM_EXAMRECORD_DETAILVO();
			TBCAM_EXAMRECORD_DETAILPK pk = new TBCAM_EXAMRECORD_DETAILPK();
			pk.setRECORD_SEQ(recordSEQ);
			pk.setQUESTION_VERSION(map.get("questionVersion"));
			pk.setANSWER_SEQ(new BigDecimal(map.get("custAnswer")));
			vo.setcomp_id(pk);
			if ((null == aVO.getANSWER_DESC()) || ("其他".equals(aVO.getANSWER_DESC()))) {
				vo.setREMARK(map.get("custAnswerDesc"));
			} else {
				vo.setREMARK(null);
			}
			
			dam.create(vo);
		}
		
		this.sendRtnObject(null);
	}
	
	public void downloadFile (Object body, IPrimitiveMap header) throws JBranchException {
		
		try {
			CAM200InputVO inputVO = (CAM200InputVO) body;
			String filePath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
			byte[] byteFile = inputVO.getAttach();
			String fileName = inputVO.getOnefileName();
			String uuid = UUID.randomUUID().toString();
			
			File targetFile = new File(filePath, uuid);
			FileOutputStream fos = new FileOutputStream(targetFile);
		    fos.write(byteFile);
		    fos.close();
		    
		    notifyClientToDownloadFile("temp//"+uuid, fileName);
		    
		    this.sendRtnObject(null);
		} catch(Exception e){
			logger.debug(e.getMessage(),e);
		}
	}
	
	public void updDTL (Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		CAM200InputVO inputVO = (CAM200InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		for (int i = 0; i < inputVO.getSfaLeadIDList().length; i++) {			
			TBCAM_SFA_LEADSVO vo = new TBCAM_SFA_LEADSVO();
			vo = (TBCAM_SFA_LEADSVO) dam.findByPKey(TBCAM_SFA_LEADSVO.TABLE_UID, inputVO.getSfaLeadIDList()[i]);
			vo.setLEAD_STATUS(inputVO.getLeadStatusList()[i]);
			dam.update(vo);
			
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT CAMP.LEAD_RESPONSE_CODE, CAMP.LEAD_SOURCE_ID ");
			sb.append("FROM TBCAM_SFA_CAMPAIGN CAMP, TBCAM_SFA_LEADS LEADS ");
			sb.append("WHERE CAMP.CAMPAIGN_ID = LEADS.CAMPAIGN_ID ");
			sb.append("AND CAMP.STEP_ID = LEADS.STEP_ID ");
			sb.append("AND LEADS.SFA_LEAD_ID = :sfaLeadID ");
			queryCondition.setQueryString(sb.toString());
			queryCondition.setObject("sfaLeadID", inputVO.getSfaLeadIDList()[i]);

			List<Map<String, Object>> dtlList = dam.exeQuery(queryCondition);
			
			if (dtlList.size() > 0 && "01".equals(dtlList.get(0).get("LEAD_SOURCE_ID"))) {
				insertRES(dam, inputVO, dtlList.get(0), vo, i);
			}
			
			TBCRM_CUST_VISIT_RECORDVO rVO = new TBCRM_CUST_VISIT_RECORDVO();
			rVO = (TBCRM_CUST_VISIT_RECORDVO) dam.findByPKey(TBCRM_CUST_VISIT_RECORDVO.TABLE_UID, vo.getCUST_MEMO_SEQ());
			rVO.setCMU_TYPE(inputVO.getClosedCmuType());
			rVO.setVISIT_MEMO(inputVO.getClosedVisitMemo());
			rVO.setVISIT_DT(new Timestamp(sdfDms.parse((sdfYmd.format(inputVO.getClosedVisitDate()) + " " + sdfHms.format(inputVO.getClosedVisitTime()))).getTime()));
			rVO.setVISIT_CREPLY(inputVO.getClosedVisitCreply());

			dam.update(rVO);
			
			// #0001931_WMS-CR-20240226-03_為提升系統效能擬優化業管系統相關功能_名單訪談記錄
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			
			sb.append("UPDATE TBCRM_CUST_VISIT_RECORD_NEW ");
			sb.append("SET CMU_TYPE = :CMU_TYPE, ");
			sb.append("    VISIT_MEMO = :VISIT_MEMO, ");
			sb.append("    VISIT_DT = :VISIT_DT, ");
			sb.append("    VISIT_CREPLY = :VISIT_CREPLY, ");
			sb.append("    MODIFIER = :MODIFIER, ");
			sb.append("    LASTUPDATE = :LASTUPDATE ");
			sb.append("WHERE VISIT_SEQ = :VISIT_SEQ ");
			
			queryCondition.setQueryString(sb.toString());
			queryCondition.setObject("CMU_TYPE", rVO.getCMU_TYPE());
			queryCondition.setObject("VISIT_MEMO", rVO.getVISIT_MEMO());
			queryCondition.setObject("VISIT_DT", rVO.getVISIT_DT());
			queryCondition.setObject("VISIT_CREPLY", rVO.getVISIT_CREPLY());
			queryCondition.setObject("MODIFIER", rVO.getModifier());
			queryCondition.setObject("LASTUPDATE", rVO.getLastupdate());
			queryCondition.setObject("VISIT_SEQ", rVO.getVISIT_SEQ());

			dam.exeUpdate(queryCondition);
		}
		
		this.sendRtnObject(null);
	}
	
	public void saveDTL (Object body, IPrimitiveMap header) throws JBranchException, ParseException, Exception {
		
		CAM200InputVO inputVO = (CAM200InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT TO_CHAR(SQ_TBCRM_CUST_VISIT_RECORD.nextval) AS SEQNO FROM DUAL");
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		String seqNo = addZeroForNum((String) list.get(0).get("SEQNO"), 10);

		for (int i = 0; i < inputVO.getSfaLeadIDList().length; i++) {
			TBCAM_SFA_LEADSVO vo = new TBCAM_SFA_LEADSVO();
			
			//=== 更新名單狀態
			vo = (TBCAM_SFA_LEADSVO) dam.findByPKey(TBCAM_SFA_LEADSVO.TABLE_UID, inputVO.getSfaLeadIDList()[i]);
			vo.setCUST_MEMO_SEQ(seqNo);
			vo.setLEAD_STATUS(inputVO.getLeadStatusList()[i]);
			// 2017/9/7 mantis 3547
			List<String> curr_ao_list = (ArrayList<String>) getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST);
			if(curr_ao_list.size() > 0)
				vo.setAO_CODE(curr_ao_list.get(0));
			else
				vo.setAO_CODE(ObjectUtils.toString(getUserVariable(FubonSystemVariableConsts.LOGINID)));
			vo.setEMP_ID(ObjectUtils.toString(getUserVariable(FubonSystemVariableConsts.LOGINID)));
			dam.update(vo);
			
			//=== 寫入名單回應檔
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			sb.append("SELECT CAMP.LEAD_RESPONSE_CODE, CAMP.LEAD_SOURCE_ID ");
			sb.append("FROM TBCAM_SFA_CAMPAIGN CAMP, TBCAM_SFA_LEADS LEADS ");
			sb.append("WHERE CAMP.CAMPAIGN_ID = LEADS.CAMPAIGN_ID ");
			sb.append("AND CAMP.STEP_ID = LEADS.STEP_ID ");
			sb.append("AND LEADS.SFA_LEAD_ID = :sfaLeadID ");
			queryCondition.setQueryString(sb.toString());
			queryCondition.setObject("sfaLeadID", inputVO.getSfaLeadIDList()[i]);
			List<Map<String, Object>> dtlList = dam.exeQuery(queryCondition);
			
			if (dtlList.size() > 0 && "01".equals(dtlList.get(0).get("LEAD_SOURCE_ID"))) {
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				insertRES(dam, inputVO, dtlList.get(0), vo, i);
			}
			
			//== 導入pipeline
			/*
			 * 名單類型：留資-房/留資-信/留資-客自房/留資-客共房/電銷留資
			 * 名單處理狀態：03B-約訪收件
			 * 預計承作商品/預計申請金額/接手業務人員 需填寫

			 */
			if ((StringUtils.equals("05", vo.getLEAD_TYPE()) || StringUtils.equals("06", vo.getLEAD_TYPE()) || StringUtils.equals("H1", vo.getLEAD_TYPE()) || StringUtils.equals("H2", vo.getLEAD_TYPE()) || StringUtils.equals("UX", vo.getLEAD_TYPE())) && 
				StringUtils.equals("03B", vo.getLEAD_STATUS())) {
				
				if (null != inputVO.getEstPrd() && null != inputVO.getEstAmt() && null != inputVO.getPlanEmpID()) {
					//== 取得相關資訊
					sb = new StringBuffer();
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb.append("SELECT REGION_CENTER_ID, BRANCH_AREA_ID FROM VWORG_DEFN_INFO WHERE BRANCH_NBR = :branchNbr ");
					queryCondition.setObject("branchNbr", vo.getBRANCH_ID());
					
					queryCondition.setQueryString(sb.toString());
					
					List<Map<String, Object>> defnList = dam.exeQuery(queryCondition);
					
					//== 將導入PK寫入LEADS中
					PMS110 pms110 = (PMS110) PlatformContext.getBean("pms110");
					String planSEQ = getSeqNum("TBPMS_PIPELINE_SEQ");
					sb = new StringBuffer();
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb.append("UPDATE TBCAM_SFA_LEADS ");
					sb.append("SET PLAN_SEQ = :planSEQ, ");
					sb.append("    PLAN_YEARMON = :planYYYYMM, ");
					sb.append("    PLAN_CENTER_ID = :planCenterID, ");
					sb.append("    PLAN_AREA_ID = :planAreaID, ");
					
					sb.append("    EST_PRD = :estPrd, ");
					sb.append("    EST_AMT = :estAmt, ");
					sb.append("    PLAN_EMP_ID = :planEmpID, ");
					
					sb.append("    MODIFIER = :modifier, ");
					sb.append("    LASTUPDATE = SYSDATE ");
					
					sb.append("WHERE SFA_LEAD_ID = :sfaLeadID ");
					
					queryCondition.setObject("sfaLeadID", inputVO.getSfaLeadIDList()[i]);
					// is key
					queryCondition.setObject("planSEQ", planSEQ);
					queryCondition.setObject("planYYYYMM", new SimpleDateFormat("yyyyMM").format(new Date().getTime()));
					queryCondition.setObject("planCenterID", (defnList.size() > 0 ? (String) defnList.get(0).get("REGION_CENTER_ID") : null));
					queryCondition.setObject("planAreaID", (defnList.size() > 0 ? (String) defnList.get(0).get("BRANCH_AREA_ID") : null));
					
					// not key
					queryCondition.setObject("estPrd", inputVO.getEstPrd());
					queryCondition.setObject("estAmt", inputVO.getEstAmt());
					queryCondition.setObject("planEmpID", inputVO.getPlanEmpID());
					
					// other
					queryCondition.setObject("modifier", getUserVariable(FubonSystemVariableConsts.LOGINID));
					
					queryCondition.setQueryString(sb.toString());
					
					dam.exeUpdate(queryCondition);
					
					//== 導入Pipeline
					// 客戶姓名
					List<Map<String, Object>> varCustDTLList = getVarField(inputVO, new String[] {"%客戶姓名%", "%姓氏%"}, "CUST_NAME");
					
					// 備註
					List<Map<String, Object>> varMemoDTLList = getVarField(inputVO, new String[] {"%留言內容%", "%備註%"}, "MEMO");
					
					// 行動電話
					List<Map<String, Object>> varPhoneDTLList = getVarField(inputVO, new String[] {"%行動電話%"}, "PHONE");
					
					// 方便聯絡時間
					List<Map<String, Object>> varContentDTLList = getVarField(inputVO, new String[] {"%方便聯絡時間%"}, "CONTENT");
					
					// 客服專員編號
					List<Map<String, Object>> varCustServiceIDList = getVarField(inputVO, new String[] {"%客服專員編號%"}, "CUST_SERVICE");
					
					// 客服專員姓名
					List<Map<String, Object>> varCustServiceNameList = getVarField(inputVO, new String[] {"%客服專員姓名%"}, "CUST_SERVICE_NAME");

					PMS110InputVO pms110InputVO = new PMS110InputVO();
					// is key
					pms110InputVO.setPlanSeq(planSEQ.toString());
					pms110InputVO.setPlanYearmon(new SimpleDateFormat("yyyyMM").format(new Date().getTime()));
					pms110InputVO.setRegion_center_id((defnList.size() > 0 ? (String) defnList.get(0).get("REGION_CENTER_ID") : null));
					pms110InputVO.setBranch_area_id((defnList.size() > 0 ? (String) defnList.get(0).get("BRANCH_AREA_ID") : null));
					
					// not key
					pms110InputVO.setBranch_nbr(vo.getBRANCH_ID());
					pms110InputVO.setEmp_id(inputVO.getPlanEmpID());
					pms110InputVO.setCustId(vo.getCUST_ID());
					pms110InputVO.setMeetingDate(null);
					pms110InputVO.setMeetingResult(null);
					pms110InputVO.setEstPrd(inputVO.getEstPrd());
					pms110InputVO.setEstPrdItem(null);
					pms110InputVO.setEstAmt(((inputVO.getEstAmt()).multiply(new BigDecimal("1000000"))).toString());
					pms110InputVO.setEstDrawDate(null);
					pms110InputVO.setCaseSource(null); 	// 06：其他通路
					pms110InputVO.setCustSource("L"); 	// L：留資名單訪談
					pms110InputVO.setCase_num(null);
					pms110InputVO.setLoanCustID(null);
					pms110InputVO.setLoanCustName(null);
					pms110InputVO.setLoanAmt(null);
					
					// VAR TO PIPELINE
					pms110InputVO.setCustName((varCustDTLList.size() > 0 ? (String) varCustDTLList.get(0).get("LABEL_VALUE") : null));
					pms110InputVO.setMemo((varMemoDTLList.size() > 0 ? (String) varMemoDTLList.get(0).get("LABEL_VALUE") : null));
					pms110InputVO.setLeadVarPhone((varPhoneDTLList.size() > 0 ? (String) varPhoneDTLList.get(0).get("LABEL_VALUE") : null));
					pms110InputVO.setLeadVarCTime((varContentDTLList.size() > 0 ? (String) varContentDTLList.get(0).get("LABEL_VALUE") : "詳見備註"));
					pms110InputVO.setLeadVarRtnEName((varCustServiceNameList.size() > 0 ? (String) varCustServiceNameList.get(0).get("LABEL_VALUE") : null));
					pms110InputVO.setLeadVarRtnEID((varCustServiceIDList.size() > 0 ? (String) varCustServiceIDList.get(0).get("LABEL_VALUE") : null));
					
					pms110.insert(pms110InputVO);
				} else {
					throw new APException("預計承作商品、預計申請金額、接手業務人員為必填欄位");
				}
			}
		}
		
		//== 寫入訪談記錄
		TBCRM_CUST_VISIT_RECORDVO vo = new TBCRM_CUST_VISIT_RECORDVO();
		vo.setVISIT_SEQ(seqNo);
		vo.setVISITOR_ROLE((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		vo.setCUST_ID(inputVO.getCustID());
		vo.setCMU_TYPE(inputVO.getCmuType());
		vo.setVISIT_MEMO(inputVO.getVisitMemo());
		vo.setVISIT_DT(new Timestamp(sdfDms.parse((sdfYmd.format(inputVO.getVisitDate()) + " " + sdfHms.format(inputVO.getVisitTime()))).getTime()));
		vo.setVISIT_CREPLY(inputVO.getVisitCreply());
		dam.create(vo);
		
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
		sb.append("  :CMU_TYPE, ");
		sb.append("  :VISIT_MEMO, ");
		sb.append("  :VERSION, ");
		sb.append("  :CREATOR, ");
		sb.append("  :CREATETIME, ");
		sb.append("  :MODIFIER, ");
		sb.append("  :LASTUPDATE, ");
		sb.append("  :VISIT_DT, ");
		sb.append("  :VISIT_CREPLY ");
		sb.append(") ");
		
		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("VISIT_SEQ", vo.getVISIT_SEQ());
		queryCondition.setObject("VISITOR_ROLE", vo.getVISITOR_ROLE());
		queryCondition.setObject("CUST_ID", vo.getCUST_ID());
		queryCondition.setObject("CMU_TYPE", vo.getCMU_TYPE());
		queryCondition.setObject("VISIT_MEMO", vo.getVISIT_MEMO());
		queryCondition.setObject("VERSION", vo.getVersion());
		queryCondition.setObject("CREATOR", vo.getCreator());
		queryCondition.setObject("CREATETIME", vo.getCreatetime());
		queryCondition.setObject("MODIFIER", vo.getModifier());
		queryCondition.setObject("LASTUPDATE", vo.getLastupdate());
		queryCondition.setObject("VISIT_DT", vo.getVISIT_DT());
		queryCondition.setObject("VISIT_CREPLY", vo.getVISIT_CREPLY());
		
		dam.exeUpdate(queryCondition);
		
		//2018-12-14 by Jacky WMS-CR-20181025-02_新增客戶聯繫頻率管理報表
		//排除當日連繫客戶
		CRM131 crm131 = (CRM131) PlatformContext.getBean("crm131");
		crm131.updateUnderservCust(inputVO.getCustID());
		
		this.sendRtnObject(null);
	}
	
	public void insertRES(DataAccessManager dam, CAM200InputVO inputVO, Map<String, Object> campMap, TBCAM_SFA_LEADSVO vo, Integer i) throws JBranchException {
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		// ===UNICA名單回應檔		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT CAMPAIGN_ID, LEAD_STATUS, RESPONSE_NAME, RESPONSE_MEAN, RESPONSE_ENABLE ");
		sb.append("FROM TBCAM_SFA_CAMP_RESPONSE ");
		sb.append("WHERE CAMPAIGN_ID IN (:responseCode, :campaignID) ");
		sb.append("AND LEAD_STATUS = :leadStatus ");
		
		queryCondition.setQueryString(sb.toString());
		
		queryCondition.setObject("responseCode", campMap.get("LEAD_RESPONSE_CODE"));
		queryCondition.setObject("campaignID", vo.getCAMPAIGN_ID());
		queryCondition.setObject("leadStatus", vo.getLEAD_STATUS());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		BigDecimal seqNo = new BigDecimal(getSeqNum("TBCAM_SFA_LEADS_RES"));
	
		TBCAM_SFA_LEADS_RESVO resFVO = new TBCAM_SFA_LEADS_RESVO();
		resFVO.setSEQNO(seqNo);
		resFVO.setSFA_LEAD_ID(vo.getSFA_LEAD_ID());
		resFVO.setCUST_ID(vo.getCUST_ID());
		resFVO.setCAMP_RESP_CODE((String) list.get(0).get("RESPONSE_MEAN"));
		resFVO.setSFA_RESP_CODE(inputVO.getLeadStatusList()[i]);
		resFVO.setLEAD_TYPE((String) vo.getLEAD_TYPE());
		resFVO.setRESP_DATE(new Timestamp(System.currentTimeMillis()));
		dam.create(resFVO);
	}
	
	public void getPSAOSalesList (Object body, IPrimitiveMap header) throws JBranchException {
		
		CAM200InputVO inputVO = (CAM200InputVO) body;
		CAM200OutputVO outputVO = new CAM200OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		if (inputVO.getSfaLeadIDList().length > 0) {
			List<Map<String, Object>> branchList = getVarField(inputVO, new String[] {"%分行%"}, "BRANCH");
			
			// === 取得該行之消金PS及個金AO人員清單
			if (branchList.size() > 0) {
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuffer();
				
				sb.append("SELECT PAO.EMP_ID AS DATA, PAO.EMP_ID || '-' || MEM.EMP_NAME || '(' || R.ROLE_NAME || ')' AS LABEL ");
				sb.append("FROM TBORG_PAO PAO ");
				sb.append("LEFT JOIN VWORG_DEFN_INFO DEFN ON PAO.BRANCH_NBR = DEFN.BRANCH_NBR ");
				sb.append("LEFT JOIN TBORG_MEMBER MEM ON PAO.EMP_ID = MEM.EMP_ID ");
				sb.append("LEFT JOIN TBORG_MEMBER_ROLE MR ON PAO.EMP_ID = MR.EMP_ID ");
				sb.append("INNER JOIN TBORG_ROLE R ON MR.ROLE_ID = R.ROLE_ID AND R.JOB_TITLE_NAME IS NOT NULL ");
				sb.append("LEFT JOIN TBSYSSECUROLPRIASS PRI ON PRI.ROLEID = MR.ROLE_ID ");
				sb.append("WHERE 1 = 1 ");
				sb.append("AND PAO.BRANCH_NBR = :branchID ");
				sb.append("UNION ");
				sb.append("SELECT INFO.EMP_ID AS DATA, INFO.EMP_ID || '-' || INFO.EMP_NAME || '(' || INFO.ROLE_NAME || ')' AS LABEL ");
				sb.append("FROM VWORG_BRANCH_EMP_DETAIL_INFO INFO ");
				sb.append("WHERE INFO.BRANCH_NBR = :branchID ");
				sb.append("AND INFO.ROLE_ID IN (SELECT ROLEID FROM TBSYSSECUROLPRIASS WHERE PRIVILEGEID = '004') ");
				sb.append("AND NOT EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE INFO.EMP_ID = UHRM.EMP_ID) ");
				sb.append("UNION ALL ");
				sb.append("SELECT PT_INFO.EMP_ID AS DATA, PT_INFO.EMP_ID || '-' || PT_INFO.EMP_NAME || '(' || PT_INFO.ROLE_NAME || ')' AS LABEL ");
				sb.append("FROM VWORG_EMP_PLURALISM_INFO PT_INFO ");
				sb.append("WHERE PT_INFO.BRANCH_NBR = :branchID ");
				sb.append("AND PT_INFO.ROLE_ID IN (SELECT ROLEID FROM TBSYSSECUROLPRIASS WHERE PRIVILEGEID = '004') ");
				sb.append("AND NOT EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE PT_INFO.EMP_ID = UHRM.EMP_ID) ");
				
				queryCondition.setQueryString(sb.toString());
				
				queryCondition.setObject("branchID", branchList.get(0).get("LABEL_VALUE"));

				outputVO.setPsaoSalesList(dam.exeQuery(queryCondition));
			}
		}
		
		this.sendRtnObject(outputVO);
	}
	
	public String addZeroForNum(String str, int strLength) {
		
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
	
	private String getSeqNum(String TXN_ID) throws JBranchException {
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		switch(TXN_ID) {
			case "TBCAM_EXAMRECORD_DETAIL":
				sql.append("SELECT SQ_TBCAM_EXAMRECORD_DETAIL.nextval AS SEQ FROM DUAL ");
				break;
			case "TBCAM_EXAMRECORD":
				sql.append("SELECT SQ_TBCAM_EXAMRECORD.nextval AS SEQ FROM DUAL ");
				break;
			case "TBCAM_SFA_LEADS_RES":
				sql.append("SELECT SQ_TBCAM_SFA_LEADS_RES.nextval AS SEQ FROM DUAL ");
				break;
			case "TBPMS_PIPELINE_SEQ":
				sql.append("SELECT TBPMS_PIPELINE_SEQ.nextval AS SEQ FROM DUAL ");
				break;
		}
		
		queryCondition.setQueryString(sql.toString());
		
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		
		return ObjectUtils.toString(SEQLIST.get(0).get("SEQ"));
	}
	
	/** 取得參考資訊中特定資訊 **/
	private List<Map<String, Object>> getVarField (CAM200InputVO inputVO, String[] condition, String searchKey) throws JBranchException {

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		// === 取得參考資訊中的分行代號
		sb.append("SELECT FL.SFA_LEAD_ID, FL.VAR_FIELD_TYPE, FL.RESULT AS LABEL_TITLE, FV.RESULT AS LABEL_VALUE ");
		sb.append("FROM ( ");
		sb.append("  SELECT SFA_LEAD_ID, VAR_FIELD_LABEL AS VAR_FIELD_TYPE, RESULT ");
		sb.append("  FROM TBCAM_SFA_LEADS_VAR ");
		sb.append("  UNPIVOT (RESULT FOR VAR_FIELD_LABEL IN (VAR_FIELD_LABEL1, VAR_FIELD_LABEL2, VAR_FIELD_LABEL3, VAR_FIELD_LABEL4, VAR_FIELD_LABEL5, VAR_FIELD_LABEL6, VAR_FIELD_LABEL7, VAR_FIELD_LABEL8, VAR_FIELD_LABEL9, VAR_FIELD_LABEL10, VAR_FIELD_LABEL11, VAR_FIELD_LABEL12, VAR_FIELD_LABEL13, VAR_FIELD_LABEL14, VAR_FIELD_LABEL15, VAR_FIELD_LABEL16, VAR_FIELD_LABEL17, VAR_FIELD_LABEL18, VAR_FIELD_LABEL19, VAR_FIELD_LABEL20)) ");
		sb.append(") FL ");
		sb.append("LEFT JOIN ( ");
		sb.append("  SELECT SFA_LEAD_ID, REPLACE(VAR_FIELD_VALUE, 'VALUE', 'LABEL') AS VAR_FIELD_TYPE, RESULT ");
		sb.append("  FROM TBCAM_SFA_LEADS_VAR ");
		sb.append("  UNPIVOT (RESULT FOR VAR_FIELD_VALUE IN (VAR_FIELD_VALUE1, VAR_FIELD_VALUE2, VAR_FIELD_VALUE3, VAR_FIELD_VALUE4, VAR_FIELD_VALUE5, VAR_FIELD_VALUE6, VAR_FIELD_VALUE7, VAR_FIELD_VALUE8, VAR_FIELD_VALUE9, VAR_FIELD_VALUE10, VAR_FIELD_VALUE11, VAR_FIELD_VALUE12, VAR_FIELD_VALUE13, VAR_FIELD_VALUE14, VAR_FIELD_VALUE15, VAR_FIELD_VALUE16, VAR_FIELD_VALUE17, VAR_FIELD_VALUE18, VAR_FIELD_VALUE19, VAR_FIELD_VALUE20)) ");
		sb.append(") FV ON FL.SFA_LEAD_ID = FV.SFA_LEAD_ID AND FL.VAR_FIELD_TYPE = FV.VAR_FIELD_TYPE ");
		sb.append("WHERE FL.SFA_LEAD_ID = :sfaLeadID ");
		
		if (condition.length > 0) {
			sb.append("AND ( ");
			for (int i = 0; i < condition.length; i++) {
				sb.append("FL.RESULT LIKE '").append(condition[i]).append("' ");
				if (i + 1 < condition.length) {
					sb.append(" OR ");
				}
			}
			sb.append(") ");
		}
		sb.append("ORDER BY FL.SFA_LEAD_ID, FL.VAR_FIELD_TYPE ");		
		sb.append("FETCH FIRST 1 ROWS ONLY ");
		
		queryCondition.setQueryString(sb.toString());
		
		queryCondition.setObject("sfaLeadID", inputVO.getSfaLeadIDList()[0]);

		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		if (list.size() <= 0) {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			
			switch (searchKey) {
				case "BRANCH":
					// === 取得名單中的分行代號
					sb.append("SELECT BRANCH_ID AS LABEL_VALUE ");
					sb.append("FROM TBCAM_SFA_LEADS ");
					sb.append("WHERE SFA_LEAD_ID = :sfaLeadID ");
					
					queryCondition.setObject("sfaLeadID", inputVO.getSfaLeadIDList()[0]);

					break;
				case "CUST_NAME":
					sb.append("SELECT CUST_NAME AS LABEL_VALUE ");
					sb.append("FROM TBCRM_CUST_MAST CM ");
					sb.append("WHERE EXISTS (SELECT 1 FROM TBCAM_SFA_LEADS LEADS WHERE LEADS.SFA_LEAD_ID = :sfaLeadID AND CM.CUST_ID = LEADS.CUST_ID) ");
					
					queryCondition.setObject("sfaLeadID", inputVO.getSfaLeadIDList()[0]);

					break;
				case "MEMO":
				case "PHONE":
				case "CONTENT":
				case "CUST_SERVICE":
				case "CUST_SERVICE_NAME":
					break;
			}
			
			if (sb.length() > 0) {
				queryCondition.setQueryString(sb.toString());
				
				list = dam.exeQuery(queryCondition);
			}
		}
		
		return list;
	}
	
	private Boolean checkID(String pk) throws JBranchException {
		Boolean ans = false;
		
		TBCAM_EXAMRECORDVO vo = new TBCAM_EXAMRECORDVO();
		vo = (TBCAM_EXAMRECORDVO) dam.findByPKey(TBCAM_EXAMRECORDVO.TABLE_UID, pk);
		if (vo != null)
			ans = true;
		else
			ans = false;
		return ans;
	}
	
}