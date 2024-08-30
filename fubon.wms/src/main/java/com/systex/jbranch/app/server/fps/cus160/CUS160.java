package com.systex.jbranch.app.server.fps.cus160;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBCAM_CAL_SALES_TASKVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author Ocean
 * @date 2016/06/6
 * 
 */
@Component("cus160")
@Scope("request")
public class CUS160 extends FubonWmsBizLogic {
	
	private DataAccessManager dam = null;
	
	public void save (Object body, IPrimitiveMap header) throws JBranchException {

		WorkStation ws = DataManager.getWorkStation(uuid);
		CUS160InputVO inputVO = (CUS160InputVO) body;
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT SQ_TBCAM_CAL_SALES_TASK.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQ");
		
		TBCAM_CAL_SALES_TASKVO vo = new TBCAM_CAL_SALES_TASKVO();
		vo.setSEQ_NO(seqNo);
		vo.setEMP_ID(StringUtils.isNotBlank(inputVO.getEmpID()) ? inputVO.getEmpID() : ws.getUser().getUserID());
		vo.setCUST_ID(inputVO.getCustID());
		vo.setTASK_DATE(inputVO.getsDate());
		vo.setTASK_STIME(inputVO.getsHour() + inputVO.getsMinute());
		vo.setTASK_ETIME(inputVO.geteHour() + inputVO.geteMinute());
		vo.setTASK_TITLE(inputVO.getTaskTitle());
		vo.setTASK_MEMO(inputVO.getTaskMemo());
		vo.setTASK_SOURCE(inputVO.getTaskSource());
		vo.setTASK_STATUS("I");
		dam.create(vo);
		
		this.sendRtnObject(null);
	}
	
	public void getAOEmpList (Object body, IPrimitiveMap header) throws JBranchException {
		
		WorkStation ws = DataManager.getWorkStation(uuid);
		CUS160InputVO inputVO = (CUS160InputVO) body;
		CUS160OutputVO outputVO = new CUS160OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT INFO.EMP_ID, INFO.ROLE_NAME, INFO.EMP_NAME, INFO.AO_CODE ");
		sb.append("FROM VWORG_BRANCH_EMP_DETAIL_INFO INFO ");
		sb.append("WHERE INFO.BRANCH_NBR = :branchID ");
		sb.append("AND INFO.AO_CODE IS NOT NULL ");
		sb.append("ORDER BY INFO.ROLE_NAME ");
		queryCondition.setQueryString(sb.toString());
		
		queryCondition.setObject("branchID", (null == inputVO.getBranchID()) ? ws.getBrchID() : inputVO.getBranchID());
		
		outputVO.setAoEmpList(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	}
	
	public void queryData (Object body, IPrimitiveMap header) throws JBranchException {
		
		WorkStation ws = DataManager.getWorkStation(uuid);
		CUS160InputVO inputVO = (CUS160InputVO) body;
		CUS160OutputVO outputVO = new CUS160OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		// 依系統角色決定下拉選單可視範圍
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> fcMap      = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> psopMap    = xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> paoMap     = xmlInfo.doGetVariable("FUBONSYS.PAO_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> bmmgrMap   = xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2);    //個金主管
		Map<String, String> mbrmgrMap  = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2);   //營運督導
		Map<String, String> armgrMap   = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2);   //區域中心主管
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT TASK.EMP_ID, TASK.CUST_ID, CUST.CUST_NAME, TASK.TASK_DATE, TASK.TASK_STIME, TASK.TASK_ETIME, ");
		sb.append("       TASK.TASK_TITLE, TASK.TASK_MEMO, TASK_SOURCE, TASK.TASK_STATUS, ");
		sb.append("       INFO.AO_CODE, INFO.EMP_NAME, TASK.CREATOR ");
		sb.append("FROM TBCAM_CAL_SALES_TASK TASK ");
		sb.append("LEFT JOIN TBCRM_CUST_MAST CUST ON CUST.CUST_ID = TASK.CUST_ID ");
		sb.append("LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO INFO ON INFO.EMP_ID = TASK.EMP_ID ");
		
		sb.append("WHERE 1 = 1 ");
		
		if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") >= 0 &&
			!StringUtils.equals(StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)), "uhrm")) {
		} else {
			if (StringUtils.isNotBlank(inputVO.getBranchID()) && Integer.valueOf(inputVO.getBranchID()) > 0) {
				sb.append("AND INFO.BRANCH_NBR = :branchID "); //分行代碼
				queryCondition.setObject("branchID", inputVO.getBranchID());
			} else {
				sb.append("AND INFO.BRANCH_NBR IN (:branchIDList) ");
				queryCondition.setObject("branchIDList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			}
		}
		
		// 以分行人員/高端人員登入時，應僅含登入者建立的行事曆。
		// 以高端主管/ARM登入時，應僅含高端人員建立的行事曆。
		if (fcMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE)) || 
			psopMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE)) || 
			paoMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE)) || 
			StringUtils.equals(StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)), "uhrm")) {
			sb.append("AND TASK.EMP_ID = :empID ");
			queryCondition.setObject("empID", ws.getUser().getUserID());
		} else if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") >= 0 &&
				   !StringUtils.equals(StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)), "uhrm")) {
			sb.append("AND EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO U WHERE TASK.EMP_ID = U.EMP_ID) ");
		} else if (armgrMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE)) || 
				   mbrmgrMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE)) || 
				   bmmgrMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sb.append("AND NOT EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO U WHERE TASK.EMP_ID = U.EMP_ID) ");
		}
		
		if (inputVO.getsDate() != null) {
			sb.append("AND TO_CHAR(TASK.TASK_DATE, 'yyyyMMdd') >= TO_CHAR(:sDate, 'yyyyMMdd') ");
			queryCondition.setObject("sDate", inputVO.getsDate());
		}
		
		if (inputVO.geteDate() != null) {
			sb.append("AND TO_CHAR(TASK.TASK_DATE, 'yyyyMMdd') <= TO_CHAR(:eDate, 'yyyyMMdd') ");
			queryCondition.setObject("eDate", inputVO.geteDate());
		}
		
		if (StringUtils.isNotBlank(inputVO.getTaskSource())) {
			sb.append("AND TASK.TASK_SOURCE = :taskSource ");
			queryCondition.setObject("taskSource", inputVO.getTaskSource());
		}
				
		if (StringUtils.isNotBlank(inputVO.getAoCode())) {
			sb.append("AND INFO.EMP_ID = (SELECT EMP_ID FROM TBORG_SALES_AOCODE WHERE AO_CODE = :aoCode) ");
			queryCondition.setObject("aoCode", inputVO.getAoCode());
		}
		
		if (StringUtils.isNotBlank(inputVO.getTaskStatus())) {
			sb.append("AND TASK.TASK_STATUS = :taskStatus ");
			queryCondition.setObject("taskStatus", inputVO.getTaskStatus());
		}
		if (StringUtils.isNotBlank(inputVO.getCustID())) {
			sb.append("AND TASK.CUST_ID LIKE :custID ");
			queryCondition.setObject("custID", "%" + inputVO.getCustID() + "%");
		}
		
		if (StringUtils.isNotBlank(inputVO.getCustName())) {
			sb.append("AND CUST.CUST_NAME LIKE :custName ");
			queryCondition.setObject("custName", "%" + inputVO.getCustName() + "%");
		}
		
		if (StringUtils.isNotBlank(inputVO.getStatus()) && "01".equals(inputVO.getStatus())) {
			sb.append("AND (TRUNC(TASK.TASK_DATE) > TRUNC(SYSDATE) OR (TRUNC(TASK.TASK_DATE) = TRUNC(SYSDATE) AND TASK.TASK_ETIME >= TO_CHAR(SYSDATE, 'HH24MI'))) ");
		} else {
			sb.append("AND (TRUNC(TASK.TASK_DATE) < TRUNC(SYSDATE) OR (TRUNC(TASK.TASK_DATE) = TRUNC(SYSDATE) AND TASK.TASK_ETIME <= TO_CHAR(SYSDATE, 'HH24MI'))) ");
		}
			
		sb.append("ORDER BY TASK.TASK_DATE DESC ");

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
	
	//查詢客戶姓名
	public void queryCustData (Object body, IPrimitiveMap header) throws JBranchException {
		CUS160InputVO inputVO = (CUS160InputVO) body;
		CUS160OutputVO outputVO = new CUS160OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT CUST_NAME FROM TBCRM_CUST_MAST WHERE CUST_ID = :custID ");
		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("custID", inputVO.getCustID());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		outputVO.setResultList(list);
		this.sendRtnObject(outputVO);
	}
}
