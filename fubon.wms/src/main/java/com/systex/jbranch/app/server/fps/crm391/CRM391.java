package com.systex.jbranch.app.server.fps.crm391;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * crm511
 * 
 * @author moron
 * @date 2016/05/27
 * @spec null
 */
@Component("crm391")
@Scope("request")
public class CRM391 extends FubonWmsBizLogic {
	
	private DataAccessManager dam = null;
	
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		CRM391InputVO inputVO = (CRM391InputVO) body;
		CRM391OutputVO return_VO = new CRM391OutputVO();
		dam = this.getDataAccessManager();
		
		// 依系統角色決定下拉選單可視範圍
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行
		Map<String, String> armgrMap   = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2);	//業務處
		Map<String, String> mbrmgrMap  = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2);	//營運區
		Map<String, String> psopMap    = xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2);	//OP
		Map<String, String> fcMap      = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2);		//理專
		Map<String, String> fchMap     = xmlInfo.doGetVariable("FUBONSYS.FCH_ROLE", FormatHelper.FORMAT_2);		//FCH理專
		Map<String, String> paoMap     = xmlInfo.doGetVariable("FUBONSYS.PAO_ROLE", FormatHelper.FORMAT_2);		//OP
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT TTAP.CUST_ID, TTAP.NEW_AO_BRH, a2.BRANCH_NAME AS NEW_BRANCH_NAME, TTAP.ORG_AO_BRH, a1.BRANCH_NAME, ");
		sql.append("       TTAP.ORG_AO_CODE, TTAP.NEW_AO_CODE, TTAP.APL_EMP_ID, TTAP.APL_EMP_ROLE, TTAP.APL_DATETIME, TTAP.APL_BRH_MGR_RPL_DATETIME, TTAP.CALL_REVIEW_STATUS, ");
		sql.append("       TTAP.APL_BRH_MGR, TTAP.OP_MGR_RPL_DATETIME, TTAP.DC_MGR_RPL_DATETIME, TTAP.DC_MGR, TTAP.HQ_MGR_RPL_DATETIME, TTAP.HQ_MGR, ");
		sql.append("       CASE WHEN TTAP.APL_REASON = '13' THEN TTAP.APL_OTH_REASON ELSE TTAP.APL_REASON END as APL_REASON, TTAP.CALL_REVIEW_NOTE, TTAP.TRS_TYPE, ");
		sql.append("       (CASE WHEN TTAP.PROCESS_STATUS = 'S' THEN '已派' ELSE '未派' END) AS PROCESS, TTAP.PROCESS_STATUS, ");
		sql.append("       DECODE(a.EMP_NAME , NULL, TTAP.ORG_AO_CODE ,TTAP.ORG_AO_CODE || '-' || a.EMP_NAME) as ORG_AO_NAME, ");
		sql.append("       DECODE(b.EMP_NAME , NULL, TTAP.NEW_AO_CODE ,TTAP.NEW_AO_CODE || '-' || b.EMP_NAME) as NEW_AO_NAME, ");
		sql.append("       DECODE(c.EMP_NAME , NULL, TTAP.APL_EMP_ID ,TTAP.APL_EMP_ID || '-' || c.EMP_NAME) as NAPL_EMP_ID, ");
		sql.append("       DECODE(d.EMP_NAME , NULL, TTAP.APL_BRH_MGR , ");
		sql.append("       TTAP.APL_BRH_MGR || '-' || d.EMP_NAME) as NAPL_BRH_MGR, ");
		sql.append("       DECODE(e.EMP_NAME , NULL, TTAP.OP_MGR , TTAP.OP_MGR || '-' || e.EMP_NAME) as NOP_MGR, ");
		sql.append("       DECODE(f.EMP_NAME , NULL, TTAP.DC_MGR ,TTAP.DC_MGR || '-' || f.EMP_NAME) as NDC_MGR, ");
		sql.append("       DECODE(g.EMP_NAME , NULL, TTAP.HQ_MGR ,TTAP.HQ_MGR || '-' || g.EMP_NAME) as NHQ_MGR, ");
		sql.append("       DECODE(h.ROLE_NAME , NULL, TTAP.APL_EMP_ROLE ,TTAP.APL_EMP_ROLE || '-' || h.ROLE_NAME) as ROLE_NAME, ");
		sql.append("       TTAP.ACT_DATE, TTAP.AGMT_SEQ, TTAP.TRS_SEQ, TTAP.AGMT_FILE ");
		//sql.append("FROM TBCRM_TRS_AOCHG_PLIST TTAP ");
		sql.append("FROM ( ");
		sql.append("  SELECT CUST_ID, ORG_AO_BRH, NEW_AO_BRH, ORG_AO_CODE, NEW_AO_CODE, APL_EMP_ID, APL_EMP_ROLE, APL_DATETIME, APL_BRH_MGR_RPL_DATETIME, "); 
		sql.append("         CALL_REVIEW_STATUS, APL_BRH_MGR, OP_MGR, OP_MGR_RPL_DATETIME, ");
		sql.append("         DC_MGR_RPL_DATETIME, DC_MGR, HQ_MGR_RPL_DATETIME, HQ_MGR, APL_REASON, APL_OTH_REASON, "); 
		sql.append("         PROCESS_STATUS, CALL_REVIEW_NOTE, TRS_TYPE, ACT_DATE, AGMT_SEQ, SEQ AS TRS_SEQ, (CASE WHEN AGMT_FILE IS NULL THEN null ELSE 'Y' END) AS AGMT_FILE ");
		sql.append("  FROM TBCRM_TRS_AOCHG_PLIST ");
		sql.append("  UNION ALL ");
		/*
		 * added by Oliver 2017/08/28 
		 * 舊系統資料部分因為不會出現在移轉流程資料(TBCRM_TRS_AOCHG_PLIST)裡，但又要可以查詢舊移轉紀錄
		 * 只能固定抓2017/08/28前已匯入系統的TBCRM_CUST_AOCODE_CHGLOG，之後移轉成功資料一樣可以看TBCRM_TRS_AOCHG_PLIST or TBCRM_CUST_AOCODE_CHGLOG
		 */
		sql.append("  SELECT CUST_ID, ORG_AO_BRH, NEW_AO_BRH, ORG_AO_CODE, NEW_AO_CODE, REG_AOCODE_EMP_ID AS APL_EMP_ID, R.ROLE_ID AS APL_EMP_ROLE, REG_AOCODE_SUB_DATETIME AS APL_DATETIME, NULL AS APL_BRH_MGR_RPL_DATETIME, "); 
		sql.append("         '' AS CALL_REVIEW_STATUS, '' AS APL_BRH_MGR, '' AS OP_MGR, NULL AS OP_MGR_RPL_DATETIME, ");
		sql.append("         NULL AS DC_MGR_RPL_DATETIME, '' AS DC_MGR, NULL AS HQ_MGR_RPL_DATETIME, '' AS HQ_MGR, APL_REASON, APL_OTH_REASON, ");
		sql.append("         'S' AS PROCESS_STATUS, '' AS CALL_REVIEW_NOTE, '' AS TRS_TYPE, LETGO_DATETIME AS ACT_DATE, '' AS AGMT_SEQ, null AS TRS_SEQ, null AS AGMT_FILE ");
		sql.append("  FROM TBCRM_CUST_AOCODE_CHGLOG L LEFT JOIN TBORG_MEMBER_ROLE R ON L.REG_AOCODE_EMP_ID = R.EMP_ID ");
		sql.append("  WHERE 1=1  ");
		sql.append("  AND ((LETGO_DATETIME < TO_DATE('20170828','YYYYMMDD')) OR (CHG_DATE < TO_DATE('20170828','YYYYMMDD'))) ");
		sql.append(") TTAP ");
		sql.append("left join TBCRM_CUST_MAST TCM ON TTAP.CUST_ID = TCM.CUST_ID ");
		sql.append("left join VWORG_DEFN_INFO a1 on TTAP.ORG_AO_BRH = a1.BRANCH_NBR ");
		sql.append("left join VWORG_DEFN_INFO a2 on TTAP.NEW_AO_BRH = a2.BRANCH_NBR ");
		sql.append("left join VWORG_AO_INFO a on TTAP.ORG_AO_CODE = a.AO_CODE ");
		sql.append("left join VWORG_AO_INFO b on TTAP.NEW_AO_CODE = b.AO_CODE ");
		sql.append("left join TBORG_MEMBER c on TTAP.APL_EMP_ID = c.EMP_ID ");
		sql.append("left join TBORG_MEMBER d on TTAP.APL_BRH_MGR = d.EMP_ID ");
		sql.append("left join TBORG_MEMBER e on TTAP.OP_MGR = e.EMP_ID ");
		sql.append("left join TBORG_MEMBER f on TTAP.DC_MGR = f.EMP_ID ");
		sql.append("left join TBORG_MEMBER g on TTAP.HQ_MGR = g.EMP_ID ");
		sql.append("left join TBORG_ROLE h on TTAP.APL_EMP_ROLE = h.ROLE_ID WHERE 1=1 ");
		
		// #0191:WMS-CR-20200214-01_新增理專輪調暨客戶換手經營資料產出及後續控管 => 2020-8-17 十保客戶一定要指派,應該可以查詢流程中案件 
		// 調離職客戶移轉(G)
	 	sql.append("AND ((TTAP.TRS_TYPE IN ('7','8','9') AND TTAP.NEW_AO_CODE IS NOT NULL) OR TTAP.TRS_TYPE IN ('1','2','3','4','5','6','A','C','D','E','F','G','H') ");
		// 2018/01/17 調整 ISU 4072 添加 OR 條件  7-換手經營 8-理財會員空Code認養 9-高貢獻空Code認養 且為 "退件" 並且為分行主管進行的退件方須認可
		sql.append("  OR (TTAP.TRS_TYPE IN ('7','8','9') AND PROCESS_STATUS = 'F' AND APL_BRH_MGR IS NOT NULL)");
		sql.append(") ");
		
		// where
		if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") >= 0 &&
			!StringUtils.equals(StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)), "uhrm")) {
			sql.append("AND EXISTS ( ");
			sql.append("  SELECT 1 ");
			sql.append("  FROM TBCRM_CUST_MAST CM ");
			sql.append("  INNER JOIN VWORG_EMP_UHRM_INFO U ON CM.AO_CODE = U.UHRM_CODE ");
			sql.append("  WHERE TCM.CUST_ID = CM.CUST_ID ");
			sql.append(") ");	
			
			// ORG_AO_CODE
			if (!StringUtils.isBlank(inputVO.getOrg_uEmpID())) {
				sql.append("AND TTAP.ORG_AO_CODE = :org_uEmpID ");
				queryCondition.setObject("org_uEmpID", inputVO.getOrg_uEmpID());
			}
					
			// ORG_AO_CODE
			if (!StringUtils.isBlank(inputVO.getNew_uEmpID())) {
				sql.append("AND TTAP.NEW_AO_CODE = :new_uEmpID ");
				queryCondition.setObject("new_uEmpID", inputVO.getNew_uEmpID());
			}
		} else {
			if (headmgrMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
				sql.append("AND (");
				sql.append("  ((a.CENTER_ID IN (:rcIdList) OR a.CENTER_ID IS NULL) ");
				sql.append("   AND (a.AREA_ID IN (:opIdList) OR a.AREA_ID IS NULL) ");
				sql.append("   AND (a.BRA_NBR IN (:brNbrList) OR a.BRA_NBR IS NULL)) ");
				sql.append("  or ");
				sql.append("  ((b.CENTER_ID IN (:rcIdList) OR b.CENTER_ID IS NULL) ");
				sql.append("   AND (b.AREA_ID IN (:opIdList) OR b.AREA_ID IS NULL) ");
				sql.append("   AND (b.BRA_NBR IN (:brNbrList) OR b.BRA_NBR IS NULL))");
				sql.append(") ");
			} else if (armgrMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
				sql.append("AND (");
				sql.append("  ((a.CENTER_ID IN (:rcIdList)) ");
				sql.append("   AND (a.AREA_ID IN (:opIdList) OR a.AREA_ID IS NULL) ");
				sql.append("   AND (a.BRA_NBR IN (:brNbrList) OR a.BRA_NBR IS NULL)) ");
				sql.append("  or ");
				sql.append("  ((b.CENTER_ID IN (:rcIdList)) ");
				sql.append("   AND (b.AREA_ID IN (:opIdList) OR b.AREA_ID IS NULL) ");
				sql.append("   AND (b.BRA_NBR IN (:brNbrList) OR b.BRA_NBR IS NULL))");
				sql.append(") ");
			} else if (mbrmgrMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
				sql.append("AND (");
				sql.append("  ((a.CENTER_ID IN (:rcIdList)) ");
				sql.append("   AND (a.AREA_ID IN (:opIdList)) ");
				sql.append("   AND (a.BRA_NBR IN (:brNbrList) OR a.BRA_NBR IS NULL)) ");
				sql.append("  or ");
				sql.append("  ((b.CENTER_ID IN (:rcIdList)) ");
				sql.append("   AND (b.AREA_ID IN (:opIdList)) ");
				sql.append("   AND (b.BRA_NBR IN (:brNbrList) OR b.BRA_NBR IS NULL))");
				sql.append(") ");
			} else if (psopMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE)) || 
					   fcMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE)) || 
					   fchMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE)) ||
					   paoMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
				sql.append("AND (");
				sql.append("  ((a.CENTER_ID IN (:rcIdList)) ");
				sql.append("   AND (a.AREA_ID IN (:opIdList)) ");
				sql.append("   AND (a.BRA_NBR IN (:brNbrList) OR a.BRA_NBR IS NULL)) ");
				sql.append("  or ");
				sql.append("  ((b.CENTER_ID IN (:rcIdList)) ");
				sql.append("   AND (b.AREA_ID IN (:opIdList)) ");
				sql.append("   AND (b.BRA_NBR IN (:brNbrList) OR b.BRA_NBR IS NULL))");
				sql.append(") ");
				sql.append("AND ((TTAP.ORG_AO_CODE in (:ao_list)) or (TTAP.NEW_AO_CODE in (:ao_list))) ");
				queryCondition.setObject("ao_list", getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST));
			} else {
				sql.append("AND (");
				sql.append("  ((a.CENTER_ID IN (:rcIdList)) ");
				sql.append("   AND (a.AREA_ID IN (:opIdList)) ");
				sql.append("   AND (a.BRA_NBR IN (:brNbrList))) ");
				sql.append("  or ");
				sql.append("  ((b.CENTER_ID IN (:rcIdList)) ");
				sql.append("   AND (b.AREA_ID IN (:opIdList)) ");
				sql.append("   AND (b.BRA_NBR IN (:brNbrList)))");
				sql.append(") ");
			}
			
			// ORG_AO_CODE
			if (!StringUtils.isBlank(inputVO.getOrg_ao_code())) {
				sql.append("AND TTAP.ORG_AO_CODE = :org_ao_code ");
				queryCondition.setObject("org_ao_code", inputVO.getOrg_ao_code());
			}
					
			// ORG_AO_CODE
			if (!StringUtils.isBlank(inputVO.getNew_ao_code())) {
				sql.append("AND TTAP.NEW_AO_CODE = :new_ao_code ");
				queryCondition.setObject("new_ao_code", inputVO.getNew_ao_code());
			}
			queryCondition.setObject("rcIdList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
			queryCondition.setObject("opIdList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
			queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		}
		
		//
		if (!StringUtils.isBlank(inputVO.getCust_id())) {
			sql.append("AND TTAP.CUST_ID = :cust_id ");
			queryCondition.setObject("cust_id", inputVO.getCust_id());
		}
		
		if (inputVO.getApl_sDate() != null) {
			sql.append("and TRUNC(TTAP.APL_DATETIME) >= :apl_start ");
			queryCondition.setObject("apl_start", inputVO.getApl_sDate());
		}
		
		if (inputVO.getApl_eDate() != null) {
			sql.append("and TRUNC(TTAP.APL_DATETIME) <= :apl_end ");
			queryCondition.setObject("apl_end", inputVO.getApl_eDate());
		}
		
		// PROCESS_STATUS
		if (!StringUtils.isBlank(inputVO.getProcess())) {
			if("1".equals(inputVO.getProcess()))
				sql.append("AND TTAP.PROCESS_STATUS = 'S' ");
			else
				sql.append("AND TTAP.PROCESS_STATUS != 'S' ");
		}
		
		if (inputVO.getApl_mgr_sDate() != null) {
			sql.append("and TRUNC(TTAP.APL_BRH_MGR_RPL_DATETIME) >= :apl_mgr_start ");
			queryCondition.setObject("apl_mgr_start", inputVO.getApl_mgr_sDate());
		}
		
		if (inputVO.getApl_mgr_eDate() != null) {
			sql.append("and TRUNC(TTAP.APL_BRH_MGR_RPL_DATETIME) <= :apl_mgr_end ");
			queryCondition.setObject("apl_mgr_end", inputVO.getApl_mgr_eDate());
		}
		
		// ORG_AO_BRH
		if (!StringUtils.isBlank(inputVO.getOrg_ao_brh())) {
			sql.append("AND TTAP.ORG_AO_BRH = :org_ao_brh ");
			queryCondition.setObject("org_ao_brh", inputVO.getOrg_ao_brh());
		}
		
		// NEW_AO_BRH
		if (!StringUtils.isBlank(inputVO.getNew_ao_brh())) {
			sql.append("AND TTAP.NEW_AO_BRH = :new_ao_brh ");
			queryCondition.setObject("new_ao_brh", inputVO.getNew_ao_brh());
		}
		
		if (inputVO.getOp_mgr_sDate() != null) {
			sql.append("and TRUNC(TTAP.OP_MGR_RPL_DATETIME) >= :op_mgr_start ");
			queryCondition.setObject("op_mgr_start", inputVO.getOp_mgr_sDate());
		}
		
		if (inputVO.getOp_mgr_eDate() != null) {
			sql.append("and TRUNC(TTAP.OP_MGR_RPL_DATETIME) <= :op_mgr_end ");
			queryCondition.setObject("op_mgr_end", inputVO.getOp_mgr_eDate());
		}
		
		if (inputVO.getDc_mgr_sDate() != null) {
			sql.append("and TRUNC(TTAP.DC_MGR_RPL_DATETIME) >= :dc_mgr_start ");
			queryCondition.setObject("dc_mgr_start", inputVO.getDc_mgr_sDate());
		}
		
		if (inputVO.getDc_mgr_eDate() != null) {
			sql.append("and TRUNC(TTAP.DC_MGR_RPL_DATETIME) <= :dc_mgr_end ");
			queryCondition.setObject("dc_mgr_end", inputVO.getDc_mgr_eDate());
		}
		
		if (inputVO.getHq_mgr_sDate() != null) {
			sql.append("and TRUNC(TTAP.HQ_MGR_RPL_DATETIME) >= :hq_mgr_start ");
			queryCondition.setObject("hq_mgr_start", inputVO.getHq_mgr_sDate());
		}
		
		if (inputVO.getHq_mgr_eDate() != null) {
			sql.append("and TRUNC(TTAP.HQ_MGR_RPL_DATETIME) <= :hq_mgr_end ");
			queryCondition.setObject("hq_mgr_end", inputVO.getHq_mgr_eDate());
		}
		
		// #0191:WMS-CR-20200214-01_新增理專輪調暨客戶換手經營資料產出及後續控管 => 2020-8-17 十保客戶一定要指派,應該可以查詢流程中案件 
		if (StringUtils.isNotBlank(inputVO.getTrs_type())){
		 	sql.append("and TTAP.TRS_TYPE = :trs_type ");
		 	queryCondition.setObject("trs_type", inputVO.getTrs_type());
		}
		
		sql.append("ORDER BY ACT_DATE DESC ");
		sql.append("FETCH FIRST ").append((Integer) SysInfo.getInfoValue(FubonSystemVariableConsts.QRY_MAX_RESULTS)).append(" ROWS ONLY ");
		
		queryCondition.setQueryString(sql.toString());
		ResultIF list = dam.executePaging(queryCondition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		int totalPage_i = list.getTotalPage();
		int totalRecord_i = list.getTotalRecord();
		return_VO.setResultList(list);
		return_VO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
		return_VO.setTotalPage(totalPage_i);// 總頁次
		return_VO.setTotalRecord(totalRecord_i);// 總筆數
		this.sendRtnObject(return_VO);
	}
	
	
}