package com.systex.jbranch.app.server.fps.crm998;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_MASTVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.SeriaNextSequence;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.errHandle.NotFoundException;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 客戶等級例外處理功能
 * 
 * @author Eli
 * @date 20180202
 * 
 */
@Component("crm998")
@Scope("request")
@SuppressWarnings({ "rawtypes", "unchecked" })
public class CRM998 extends FubonWmsBizLogic {
	@Autowired
	private CRM998InputVO inputVO;
	@Autowired
	private CRM998OutputVO returnVO;
	@Autowired
	private SeriaNextSequence seriaNextSequence;

	private DataAccessManager dam;
	private QueryConditionIF condition;

	/** 列舉覆核狀態 */
	private final String ALLOW = "A";
	private final String REJECT = "R";
	private final String WAIT = "W";

	/** 取得 {@link DataAccessManager} 物件 */
	private void getDam() {
		dam = this.getDataAccessManager();
	}

	/** 取得 {@link QueryConditionIF} 物件 */
	private void getCondition() throws DAOException, JBranchException {
		condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
	}

	/**
	 * 取得該模組PriId作為UI判斷用
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void getPriId(Object body, IPrimitiveMap<?> header) throws JBranchException {
		getDam();
		getCondition();
		condition.setQueryString("select PRIVILEGEID from TBSYSSECUPRIFUNMAP where ITEMID = :itemId and FUNCTIONID = 'confirm' ");
		condition.setObject("itemId", this.getClass().getSimpleName());
		returnVO.setResultList(dam.exeQuery(condition));
		this.sendRtnObject(returnVO);
	}
	
	/**
	 * 查詢客戶資料
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void inquireCustData(Object body, IPrimitiveMap<?> header) throws JBranchException {
		getDam();
		getCondition();
		condition.setQueryString(
				new StringBuffer()
					.append("select M.CUST_NAME, M.BRA_NBR, M.AO_CODE, A.EMP_NAME, A.EMP_ID, ")
					.append("       M.VIP_DEGREE ORG_DEGREE, M.AUM_AMT, ")
					.append("       (select distinct APPR_STATUS from TBCRM_CUST_VIP_DEGREE_APPROVAL ")
					.append("		 where CUST_ID = :custId and APPR_STATUS = 'W') WAITING ")
					.append("from TBCRM_CUST_MAST M ")
					.append("left join VWORG_AO_INFO A ")
					.append("on M.AO_CODE = A.AO_CODE ")
					.append("where M.CUST_ID = :custId ")
					.toString());
		condition.setObject("custId", ((CRM998InputVO) body).getCustId());
		returnVO.setResultList(dam.exeQuery(condition));
		this.sendRtnObject(returnVO);
	}

	/**
	 * 將客戶等級例外的清單儲存至DB
	 * 
	 * @param body : Array
	 * @param header
	 * @throws DAOException
	 * @throws JBranchException
	 */
	public void add(Object body, IPrimitiveMap<?> header) throws DAOException, JBranchException {
		for (Object approval : (ArrayList) body) {
			exeUpdateForMap(getAddApprovalSql(), addExtraParam((Map)approval));
		}
	}
	
	/** 加入待覆核名單額外參數 */
	private Map addExtraParam(Map map) throws JBranchException {
		map.put("SEQ", seriaNextSequence.nextSeq("TBCRM_VIP_DEG_APPR_SEQ"));
		map.put("CREATOR", (String) SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID));
		return map;
	}

	/** 返回插入 TBCRM_CUST_VIP_DEGREE_APPROVAL SQL  */
	private String getAddApprovalSql() {
		return new StringBuffer()
			.append("insert into TBCRM_CUST_VIP_DEGREE_APPROVAL ")
			.append("(SEQ, BRA_NBR, CUST_ID, CUST_NAME, AUM_AMT, ORG_DEGREE, NEW_DEGREE, ")
			.append(" APPL_EMP_ID, APPL_EMP_NAME, APPL_DATE, APPR_STATUS, version, CREATETIME, CREATOR ) ")
			.append("values (:SEQ, :BRA_NBR, :CUST_ID, :CUST_NAME, :AUM_AMT, :ORG_DEGREE, :NEW_DEGREE, ")
 			.append(":APPL_EMP_ID, :APPL_EMP_NAME, sysdate, :APPR_STATUS, 0, sysdate, :CREATOR ) ")
 			.toString();
	}

	/**
	 * 查詢例外客戶覆核清單
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @throws DAOException
	 */
	public void inquireApprovalData(Object body, IPrimitiveMap<?> header) throws DAOException, JBranchException {
		getDam();
		getCondition();
		setCRM998InputVO(body);
		condition.setQueryString(getInquireApprovalDataSQL());
		returnVO.setResultList(dam.exeQuery(condition));
		this.sendRtnObject(returnVO);
	}

	/**
	 * 取得查詢覆核清單的SQL
	 * 
	 * @throws JBranchException
	 */
	private String getInquireApprovalDataSQL() throws JBranchException {
		StringBuffer buffer = new StringBuffer();
		buffer.append("select * from TBCRM_CUST_VIP_DEGREE_APPROVAL ").append("where 1=1 ");
		// 客戶ID
		if (StringUtils.isNotBlank(inputVO.getCustId())) {
			buffer.append("and CUST_ID = :custId ");
			condition.setObject("custId", inputVO.getCustId());
		}
		// 處、區、分行 (控管可視範圍)
		if (StringUtils.isNotBlank(inputVO.getBraNbr())) {
			buffer.append("and BRA_NBR = :braNbr ");
			condition.setObject("braNbr", inputVO.getBraNbr());
		} else if (StringUtils.isNotBlank(inputVO.getAreaId())) {
			buffer.append("and BRA_NBR in (select BRANCH_NBR from VWORG_DEFN_INFO where BRANCH_AREA_ID = :areaId) ");
			condition.setObject("areaId", inputVO.getAreaId());
		} else if (StringUtils.isNotBlank(inputVO.getCenterId())) {
			buffer.append("and BRA_NBR in (select BRANCH_NBR from VWORG_DEFN_INFO where REGION_CENTER_ID = :centerId) ");
			condition.setObject("centerId", inputVO.getCenterId());
		} else { //BRA_NBR為空者，具有覆核權限者均可以看到
			buffer.append("and (BRA_NBR in (:braNbrList) or BRA_NBR is null) ");
			condition.setObject("braNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		}
		// 覆核狀態
		if (StringUtils.isNotBlank(inputVO.getApprStatus())) {
			buffer.append("and APPR_STATUS = :apprStatus ");
			condition.setObject("apprStatus", inputVO.getApprStatus());
		}
		// AO_CODE
		if (StringUtils.isNotBlank(inputVO.getAoCode())) {
			buffer.append("and APPL_EMP_ID = (select EMP_ID from VWORG_AO_INFO where AO_CODE = :aoCode ) ");
			condition.setObject("aoCode", inputVO.getAoCode());
		}
		// 申請日(起)
		if (inputVO.getApplDateStart() != null) {
			buffer.append("and TRUNC(APPL_DATE) >= :applDateStart ");
			condition.setObject("applDateStart", inputVO.getApplDateStart());
		}
		// 申請日(迄)
		if (inputVO.getApplDateEnd() != null) {
			buffer.append("and TRUNC(APPL_DATE) <= :applDateEnd ");
			condition.setObject("applDateEnd", inputVO.getApplDateEnd());
		}
		
		return buffer.toString();
	}

	/**
	 * 放行例外客戶
	 * 
	 * @param body
	 * @param header
	 * @throws DAOException
	 * @throws JBranchException
	 */
	public void approval(Object body, IPrimitiveMap<?> header) throws DAOException, JBranchException {
		getDam();
		getCondition();
		checkStep(updateApproval((Map)((ArrayList)body).get(0)));
		this.sendRtnObject(null);
	}

	/** 設定inputVO */
	private void setCRM998InputVO(Object body) {
		inputVO = (CRM998InputVO) body;
	}

	/**
	 * 呼叫這個method時，此刻的覆核狀態只會有允許 {@link CRM998#ALLOW} 、 與 拒絕{@link CRM998#REJECT}
	 * 如果覆核狀態為 {@link CRM998#REJECT} (拒絕)。 就不繼續往下執行
	 * 
	 * @param vo
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private void checkStep(Map map) throws DAOException, JBranchException {
		if (map.get("APPR_STATUS").equals(REJECT)) return;
		
		insertChgLog(map);
		updateCustDegree(map);
	}

	/** 更新客戶主檔 TBCRM_CUST_MAST中的客戶等級 VIP_DEGREE */
	private void updateCustDegree(Map map) throws JBranchException {
		exeUpdateForMap(getUpdateCustDegreeSql(), getUpdateCustDegreeParamMap(map));
	}

	/**取得更新客戶主檔等級的Param Map */
	private Map getUpdateCustDegreeParamMap(Map map) {
		HashMap param = new HashMap();
		param.put("CUST_ID", (String)map.get("CUST_ID"));
		param.put("NEW_DEGREE", (String)map.get("NEW_DEGREE"));
		return param;
	}

	/**取得更新客戶主檔等級Sql*/
	private String getUpdateCustDegreeSql() {
		return "update TBCRM_CUST_MAST set VIP_DEGREE = :NEW_DEGREE where CUST_ID = :CUST_ID ";
	}

	/** insert TBCRM_CUST_VIP_DEGREE_CHGLOG table */
	private void insertChgLog(Map map) throws DAOException, JBranchException {
		exeUpdateForMap(getInsertChgLogSql(), getInsertChgLogParamMap(map));
	}

	/** 取得插入TBCRM_CUST_VIP_DEGREE_CHGLOG 的param map */
	private Map getInsertChgLogParamMap(Map map) throws JBranchException {
		HashMap param = new HashMap();
		param.put("SEQ", seriaNextSequence.nextSeq("TBCRM_CUST_VIP_DEGREE_SEQ"));
		param.put("CUST_ID", map.get("CUST_ID"));
		param.put("ORG_DEGREE", map.get("ORG_DEGREE"));
		param.put("ORG_DEGREE_NAME", map.get("ORG_DEGREE_NAME"));
		param.put("NEW_DEGREE", map.get("NEW_DEGREE"));
		param.put("NEW_NOTE", map.get("NEW_DEGREE"));
		param.put("NEW_DEGREE_NAME", map.get("NEW_DEGREE_NAME"));
		param.put("DUE_DATE", map.get("REVIEW_DATE"));
		param.put("APPL_EMP_ID", map.get("APPL_EMP_ID"));
		param.put("APPL_DATE", map.get("APPL_DATE"));
		param.put("CHG_DATE", map.get("REVIEW_DATE"));
		param.put("REVIEW_EMP_ID", map.get("REVIEW_EMP_ID"));
		param.put("REVIEW_DATE", map.get("REVIEW_DATE"));
		
		String id = (String) SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID);
		param.put("CREATOR", id);
		param.put("MODIFIER", id);
		
		return param;
	}

	/** 取得插入TBCRM_CUST_VIP_DEGREE_CHGLOG 的SQL */
	private String getInsertChgLogSql() {
		return new StringBuffer()
			.append("insert into TBCRM_CUST_VIP_DEGREE_CHGLOG ")
			.append("(CUST_ID, CHG_TYPE, ORG_DEGREE, ORG_DEGREE_NAME, NEW_DEGREE, NEW_NOTE, ")
			.append(" NEW_DEGREE_NAME, UPGRADE_EXP_NOTE, DUE_DATE, APPL_EMP_ID, APPL_DATE, ")
			.append(" SEQ, CHG_DATE, RETURN_400_YN, REVIEW_EMP_ID, REVIEW_DATE, ")
			.append(" version, CREATOR, CREATETIME, MODIFIER, LASTUPDATE) ")
			.append("values ")
			.append("(:CUST_ID, '1', :ORG_DEGREE, :ORG_DEGREE_NAME, :NEW_DEGREE, :NEW_NOTE, ")
			.append(" :NEW_DEGREE_NAME, 'EA', TO_DATE(:DUE_DATE, 'YYYY-MM-DD HH24:MI:SS '), :APPL_EMP_ID, ")
			.append(" TO_DATE(:APPL_DATE, 'YYYY-MM-DD HH24:MI:SS'), :SEQ, TO_DATE(:CHG_DATE, 'YYYY-MM-DD HH24:MI:SS'), 'U',")
			.append(" :REVIEW_EMP_ID, TO_DATE(:REVIEW_DATE, 'YYYY-MM-DD HH24:MI:SS'), 0, :CREATOR, sysdate, :MODIFIER, sysdate) ")
			.toString();
	}

	/** 更新 TBCRM_CUST_VIP_DEGREE_APPROVAL table */ 
	private Map updateApproval(Map map) throws JBranchException {
		Map extraMap;
		exeUpdateForMap(getUpdateApprovalSql(), (extraMap = getUpdateApprovalParamMap(map)));
		map.putAll(extraMap);
		return map;
	}

	/** 取得更新 TBCRM_CUST_VIP_DEGREE_APPROVAL 的param map  */
	private Map getUpdateApprovalParamMap(Map map) throws JBranchException {
		HashMap param = new HashMap();
		String id = (String) SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID);
		String name = (String) SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINNAME);
		param.put("REVIEW_EMP_ID", id);
		param.put("REVIEW_EMP_NAME", name);
		param.put("REVIEW_DATE", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		param.put("APPR_STATUS", map.get("APPR_STATUS"));
		param.put("MODIFIER", id);
		param.put("SEQ", map.get("SEQ"));
		return param;
	}

	/** 取得更新 TBCRM_CUST_VIP_DEGREE_APPROVAL 的SQL  */
	private String getUpdateApprovalSql() {
		return new StringBuffer()
			.append("update TBCRM_CUST_VIP_DEGREE_APPROVAL ")
			.append("set REVIEW_EMP_ID = :REVIEW_EMP_ID, ")
			.append("	 REVIEW_EMP_NAME = :REVIEW_EMP_NAME, ")
			.append("	 REVIEW_DATE = TO_DATE(:REVIEW_DATE, 'YYYY-MM-DD HH24:MI:SS'), ")
			.append("	 APPR_STATUS = :APPR_STATUS, ")
			.append("    MODIFIER = :MODIFIER, ")
			.append("    LASTUPDATE = sysdate ")
			.append("where SEQ = :SEQ ")
			.toString();
	}
}