package com.systex.jbranch.app.server.fps.cam160;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.jlb.CAM999;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author Ocean
 * @date 2016/05/17
 * 
 */
@Component("cam160")
@Scope("request")
public class CAM160 extends FubonWmsBizLogic {
	
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CAM160.class);

	/**
	 *
	 * @param body
	 * @param header
	 * @throws JBranchException
	 *
	 * @version 2016/12/05; Sebastian 	1.日期條件起訖日拆分開判斷與帶值
	 * 									2.加入REMOVE_FLAG之條件,排除已移除之案件
     */
	public void queryData(Object body, IPrimitiveMap header) throws JBranchException {
		
		WorkStation ws = DataManager.getWorkStation(uuid);
		CAM160InputVO inputVO = (CAM160InputVO) body;
		CAM160OutputVO outputVO = new CAM160OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		// 取得活動名單移除及修改
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT PA.SFA_PARA_ID, IMP.SEQNO, IMP.REL_EMP_ID, MEM.EMP_NAME, IMP.REL_DATETIME, ");
		sb.append("       CAMP.STEP_ID, IMP.CREATETIME, CAMP.CAMPAIGN_ID, CAMP.CAMPAIGN_NAME, CAMP.CAMPAIGN_DESC, ");
		sb.append("       CAMP.START_DATE, CAMP.END_DATE, ");
		sb.append("       IMP.IM_TOTAL_CNT, CNT.REMOVEABLE, CNT.PROCESSED, IMP.RV_LE_CNT, IMP.RE_STATUS, IMP.RV_REASON, ");
		sb.append("       CASE WHEN PA.CREATOR = :loginID THEN 'Y' WHEN (SELECT REPLACE(ROL.SYS_ROLE, '_ROLE', '') AS LOGIN_ROLE FROM TBORG_ROLE ROL WHERE ROLE_ID = :loginRole) = 'HEADMGR' THEN 'Y' ELSE 'N' END AS PA_MODIFY_YN, ");
		sb.append("       CASE WHEN IMP.CREATOR = :loginID THEN 'Y' WHEN (SELECT REPLACE(ROL.SYS_ROLE, '_ROLE', '') AS LOGIN_ROLE FROM TBORG_ROLE ROL WHERE ROLE_ID = :loginRole) = 'HEADMGR' THEN 'Y' ELSE 'N' END AS MODIFY_YN ");
		sb.append("FROM TBCAM_SFA_CAMPAIGN CAMP ");
		sb.append("LEFT JOIN TBCAM_SFA_PARAMETER PA ON CAMP.CAMPAIGN_ID = PA.CAMPAIGN_ID ");
		sb.append("LEFT JOIN TBCAM_SFA_LEADS_IMP IMP ON IMP.CAMPAIGN_ID = CAMP.CAMPAIGN_ID AND IMP.STEP_ID = CAMP.STEP_ID ");
		sb.append("LEFT JOIN TBORG_MEMBER MEM on IMP.REL_EMP_ID = MEM.EMP_ID ");
		sb.append("LEFT JOIN (SELECT CAMPAIGN_ID, STEP_ID, ");
		sb.append("SUM(CASE WHEN LEAD_STATUS NOT LIKE '03%' THEN 1 ELSE 0 END) REMOVEABLE, ");
		sb.append("SUM(CASE WHEN LEAD_STATUS LIKE '03%' THEN 1 ELSE 0 END) PROCESSED ");
		sb.append("FROM TBCAM_SFA_LEADS ");
		sb.append("GROUP BY CAMPAIGN_ID, STEP_ID) CNT ON IMP.CAMPAIGN_ID = CNT.CAMPAIGN_ID AND IMP.STEP_ID = CNT.STEP_ID ");
		sb.append("WHERE 1 = 1 ");
		
		queryCondition.setObject("loginID", ws.getUser().getUserID());
		queryCondition.setObject("loginRole", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		
		if (!StringUtils.isBlank(inputVO.getCampID())) { // 行銷活動代碼
			sb.append("AND IMP.CAMPAIGN_ID like :campID "); 
			queryCondition.setObject("campID", "%" + inputVO.getCampID() + "%");
		}
			
		if (!StringUtils.isBlank(inputVO.getCampName())) { // 行銷活動名稱
			sb.append("AND IMP.CAMPAIGN_NAME like :campName "); 
			queryCondition.setObject("campName", "%" + inputVO.getCampName() + "%");
		}
			
		if (null != inputVO.getImportSDate()) {
			sb.append("AND TO_CHAR(IMP.CREATETIME, 'yyyyMMdd') >= TO_CHAR(:importSDate, 'yyyyMMdd') ");
			queryCondition.setObject("importSDate", inputVO.getImportSDate());
		}
			
		if( null != inputVO.getImportEDate()) {
			sb.append("AND TO_CHAR(IMP.CREATETIME, 'yyyyMMdd') <= TO_CHAR(:importEDate, 'yyyyMMdd') ");
			queryCondition.setObject("importEDate", inputVO.getImportEDate());
		}
		
		if (null != inputVO.getsDate() && null != inputVO.getsDate2()) {// 活動起始日-起迄 
			sb.append("AND TO_CHAR(CAMP.START_DATE, 'yyyyMMdd') >= TO_CHAR(:sDate, 'yyyyMMdd') AND TO_CHAR(CAMP.START_DATE, 'yyyyMMdd') <= TO_CHAR(:sDate2, 'yyyyMMdd') ");
			queryCondition.setObject("sDate", inputVO.getsDate());
			queryCondition.setObject("sDate2", inputVO.getsDate2());
		}
		
		if ((null != inputVO.getsDate() && null == inputVO.getsDate2()) || 
			(null == inputVO.getsDate() && null != inputVO.getsDate2())) {// 活動起始日 
			sb.append("AND TO_CHAR(CAMP.START_DATE, 'yyyyMMdd') >= TO_CHAR(:sDate, 'yyyyMMdd') ");
			queryCondition.setObject("sDate", (null != inputVO.getsDate() && null == inputVO.getsDate2()) ? inputVO.getsDate() : inputVO.getsDate2());
		}
		
		if (null != inputVO.geteDate() && null != inputVO.geteDate2()) {// 活動終止日-起迄
			sb.append("AND TO_CHAR(CAMP.END_DATE, 'yyyyMMdd') >= TO_CHAR(:eDate, 'yyyyMMdd') AND TO_CHAR(CAMP.END_DATE, 'yyyyMMdd') <= TO_CHAR(:eDate2, 'yyyyMMdd') ");
			queryCondition.setObject("eDate", inputVO.geteDate());
			queryCondition.setObject("eDate2", inputVO.geteDate2());
		}
		
		if ((null != inputVO.geteDate() && null == inputVO.geteDate2()) || 
			(null == inputVO.geteDate() && null != inputVO.geteDate2())) {// 活動終止日 
			sb.append("AND TO_CHAR(CAMP.END_DATE, 'yyyyMMdd') >= TO_CHAR(:eDate, 'yyyyMMdd') ");
			queryCondition.setObject("eDate", (null != inputVO.geteDate() && null == inputVO.geteDate2()) ? inputVO.geteDate() : inputVO.geteDate2());
		}

		sb.append("AND REMOVE_FLAG <> 'Y' "); //排除已刪除案件
		sb.append("ORDER BY IMP.CREATETIME DESC ");
		queryCondition.setQueryString(sb.toString());
				
		ResultIF list = dam.executePaging(queryCondition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		int totalPage_i = list.getTotalPage(); // 分頁用
		int totalRecord_i = list.getTotalRecord(); // 分頁用
		outputVO.setResultList(list); // data
		outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
		outputVO.setTotalPage(totalPage_i);// 總頁次
		outputVO.setTotalRecord(totalRecord_i);// 總筆數
		
		this.sendRtnObject(outputVO);
	}
	
	public void queryHisData(Object body, IPrimitiveMap header) throws JBranchException {
		
		CAM160InputVO inputVO = (CAM160InputVO) body;
		CAM160OutputVO outputVO = new CAM160OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		// 取得名單移除及修改歷史紀錄
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT IMP.MODIFIER, mem.EMP_NAME, IMP.LASTUPDATE, IMP.CREATETIME, ");
		sb.append("IMP.CAMPAIGN_ID, IMP.CAMPAIGN_NAME, IMP.CAMPAIGN_DESC, ");
		sb.append("IMP.RE_STATUS, IMP.RV_REASON, IMP.START_DATE, IMP.END_DATE, ");
		sb.append("IMP.IM_TOTAL_CNT, IMP.RV_LE_CNT, (IMP.IM_TOTAL_CNT-IMP.RV_LE_CNT) ALIVE_CNT, ");
		sb.append("IMP.STEP_ID ");
		sb.append("FROM TBCAM_SFA_LEADS_IMP IMP ");
		sb.append("left join TBORG_MEMBER mem on IMP.MODIFIER = mem.EMP_ID ");
		sb.append("INNER JOIN TBCAM_SFA_CAMPAIGN CAMP ON IMP.CAMPAIGN_ID = CAMP.CAMPAIGN_ID AND IMP.STEP_ID = CAMP.STEP_ID ");
		sb.append("WHERE IMP.RE_STATUS IS NOT NULL ");

		if (!StringUtils.isBlank(inputVO.getHis_campID())) { // 行銷活動代碼
			sb.append("AND IMP.CAMPAIGN_ID like :campID "); 
			queryCondition.setObject("campID", "%" + inputVO.getHis_campID()+ "%");
		}
			
		if (!StringUtils.isBlank(inputVO.getHis_campName())) { // 行銷活動名稱
			sb.append("AND IMP.CAMPAIGN_NAME like :campName "); 
			queryCondition.setObject("campName", "%" + inputVO.getHis_campName() + "%");
		}
			
		if (!StringUtils.isBlank(inputVO.getHis_modifier())) { // 修改人員
			sb.append("AND IMP.MODIFIER like :modifier "); 
			queryCondition.setObject("modifier", "%" + inputVO.getHis_modifier() + "%");
		}
			
		if (null != inputVO.getHis_delSDate()) {
			sb.append("AND TO_CHAR(IMP.LASTUPDATE, 'yyyyMMdd') >= TO_CHAR(:his_delSDate, 'yyyyMMdd') "); // 修改期間
			queryCondition.setObject("his_delSDate", inputVO.getHis_delSDate());
		}
			
		if (null != inputVO.getHis_delEDate()) {
			sb.append("AND TO_CHAR(IMP.LASTUPDATE, 'yyyyMMdd') <= TO_CHAR(:his_delEDate, 'yyyyMMdd') ");
			queryCondition.setObject("his_delEDate", inputVO.getHis_delEDate());
		}
		
		if (null != inputVO.getHis_sDate() && null != inputVO.getHis_sDate2()) {// 活動起始日-起迄 
			sb.append("AND TO_CHAR(imp.START_DATE, 'yyyyMMdd') >= TO_CHAR(:his_sDate, 'yyyyMMdd') AND TO_CHAR(imp.START_DATE, 'yyyyMMdd') <= TO_CHAR(:his_sDate2, 'yyyyMMdd') ");
			queryCondition.setObject("his_sDate", inputVO.getHis_sDate());
			queryCondition.setObject("his_sDate2", inputVO.getHis_sDate2());
		}
		
		if ((null != inputVO.getHis_sDate() && null == inputVO.getHis_sDate2()) || 
			(null == inputVO.getHis_sDate() && null != inputVO.getHis_sDate2())) {// 活動起始日 
			sb.append("AND TO_CHAR(imp.START_DATE, 'yyyyMMdd') >= TO_CHAR(:his_sDate, 'yyyyMMdd') ");
			queryCondition.setObject("his_sDate", (null != inputVO.getHis_sDate() && null == inputVO.getHis_sDate2()) ? inputVO.getHis_sDate() : inputVO.getHis_sDate2());
		}
		
		if (null != inputVO.getHis_eDate() && null != inputVO.getHis_eDate2()) {// 活動終止日-起迄
			sb.append("AND TO_CHAR(imp.END_DATE, 'yyyyMMdd') >= TO_CHAR(:his_eDate, 'yyyyMMdd') AND TO_CHAR(imp.END_DATE, 'yyyyMMdd') <= TO_CHAR(:his_eDate2, 'yyyyMMdd') ");
			queryCondition.setObject("his_eDate", inputVO.getHis_eDate());
			queryCondition.setObject("his_eDate2", inputVO.getHis_eDate2());
		}
		
		if ((null != inputVO.getHis_eDate() && null == inputVO.getHis_eDate2()) || 
			(null == inputVO.getHis_eDate() && null != inputVO.getHis_eDate2())) {// 活動起始日 
			sb.append("AND TO_CHAR(imp.END_DATE, 'yyyyMMdd') >= TO_CHAR(:his_eDate, 'yyyyMMdd') ");
			queryCondition.setObject("his_eDate", (null != inputVO.getHis_eDate() && null == inputVO.getHis_eDate2()) ? inputVO.getHis_eDate() : inputVO.getHis_eDate2());
		}
		queryCondition.setQueryString(sb.toString());

		ResultIF list = dam.executePaging(queryCondition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		int totalPage_i = list.getTotalPage(); // 分頁用
		int totalRecord_i = list.getTotalRecord(); // 分頁用
		outputVO.setResultList_his(list);; // data
		outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
		outputVO.setTotalPage(totalPage_i);// 總頁次
		outputVO.setTotalRecord(totalRecord_i);// 總筆數
		
		this.sendRtnObject(outputVO);
	}
	
	public void removeLead(Object body, IPrimitiveMap header) throws JBranchException {
		
		dam = this.getDataAccessManager();
		CAM160InputVO inputVO = (CAM160InputVO) body;
		
		CAM999 cam999 = new CAM999();
		cam999.removeLead(dam, inputVO.getType(), inputVO.getCampID(), inputVO.getStepID(), inputVO.getRvReason());

		this.sendRtnObject(null);
	}
	
	public void getLeadsList(Object body, IPrimitiveMap header) throws JBranchException {
		
		CAM160InputVO inputVO = (CAM160InputVO) body;
		CAM160OutputVO outputVO = new CAM160OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT LEADS.CUST_ID, CUST.CUST_NAME ");
		sb.append("FROM TBCAM_SFA_LEADS LEADS ");
		sb.append("LEFT JOIN TBCRM_CUST_MAST CUST ON CUST.CUST_ID = LEADS.CUST_ID ");
		sb.append("WHERE LEADS.LEAD_STATUS <> 'TR' ");
		sb.append("AND LEADS.CAMPAIGN_ID = :campID ");
		sb.append("AND LEADS.STEP_ID = :stepID ");
		
		queryCondition.setQueryString(sb.toString());
		
		queryCondition.setObject("campID", inputVO.getHis_campID());
		queryCondition.setObject("stepID", inputVO.getHis_stepID());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		outputVO.setLeadsList(list);
		
		this.sendRtnObject(outputVO);
	}
}
