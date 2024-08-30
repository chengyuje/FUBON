package com.systex.jbranch.app.server.fps.org330;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBORG_ROLEVO;
import com.systex.jbranch.app.common.fps.table.TBORG_ROLE_REVIEWVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSPARAMETERPK;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSPARAMETERVO;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSSECUROLEVO;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("org330")
@Scope("request")
public class ORG330 extends FubonWmsBizLogic {
	
	public DataAccessManager dam = null;
	
	public void getRoleLst(Object body, IPrimitiveMap header) throws JBranchException {

		WorkStation ws = DataManager.getWorkStation(uuid);
		ORG330OutputVO outputVO = new ORG330OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		//=== 資訊服務營管部資訊營管科長 (顯示需覆核)
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT PRIVILEGEID ");
		sb.append("FROM TBSYSSECUROLPRIASS ");
		sb.append("WHERE PRIVILEGEID IN (SELECT PRIVILEGEID FROM TBSYSSECUPRIFUNMAP WHERE ITEMID = 'ORG330' AND FUNCTIONID = 'confirm') "); 
		sb.append("AND ROLEID = :roleID ");
		
		queryCondition.setQueryString(sb.toString());
		
		queryCondition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		//===
		
		List<Map<String, Object>> privilege = dam.exeQuery(queryCondition);
		
		sb = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb.append("SELECT ROLE_ID, ROLE_NAME, JOB_TITLE_NAME, IS_AO, AGENT_ROLE, ACT_TYPE, REVIEW_STATUS, SYS_ROLE, SEQNO ");
		sb.append("FROM ( ");
		if (privilege.size() > 0) { //登入角色為資訊服務營管部資訊營管科長 ((BigDecimal) privilegeCount.get(0).get("COUNTS")).compareTo(new BigDecimal(0)) > 0
			sb.append("SELECT ROLE_ID, ROLE_NAME, JOB_TITLE_NAME, IS_AO, AGENT_ROLE, ACT_TYPE, REVIEW_STATUS, SYS_ROLE, SEQNO ");
			sb.append("FROM TBORG_ROLE_REVIEW ");
			sb.append("WHERE REVIEW_STATUS = 'W' ");
			sb.append("UNION ");
			sb.append("SELECT ROLE_ID, ROLE_NAME, JOB_TITLE_NAME, IS_AO, AGENT_ROLE, ACT_TYPE, REVIEW_STATUS, SYS_ROLE, null AS SEQNO ");
			sb.append("FROM TBORG_ROLE ");
			sb.append("WHERE REVIEW_STATUS = 'Y' ");
			sb.append("AND ROLE_ID NOT IN (SELECT ROLE_ID FROM TBORG_ROLE_REVIEW WHERE REVIEW_STATUS = 'W') ");
		} else {
			sb.append("SELECT ROLE_ID, ROLE_NAME, JOB_TITLE_NAME, IS_AO, AGENT_ROLE, ACT_TYPE, REVIEW_STATUS, SYS_ROLE, 0 AS SEQNO ");
			sb.append("FROM TBORG_ROLE_REVIEW ");
			sb.append("WHERE REVIEW_STATUS = 'W' ");
			sb.append("UNION ");
			sb.append("SELECT ROLE_ID, ROLE_NAME, JOB_TITLE_NAME, IS_AO, AGENT_ROLE, ACT_TYPE, REVIEW_STATUS, SYS_ROLE, null AS SEQNO ");
			sb.append("FROM TBORG_ROLE ");
			sb.append("WHERE REVIEW_STATUS = 'Y' ");
			sb.append("AND ROLE_ID NOT IN (SELECT ROLE_ID FROM TBORG_ROLE_REVIEW WHERE REVIEW_STATUS = 'W' AND CREATOR = :creator) ");
			queryCondition.setObject("creator", ws.getUser().getUserID());
		}
		sb.append(") ORDER BY CASE WHEN REVIEW_STATUS = 'W' THEN 0 WHEN REVIEW_STATUS = 'Y' THEN 1 ELSE 2 END ASC, ROLE_NAME, JOB_TITLE_NAME, ROLE_ID ");
		
		queryCondition.setQueryString(sb.toString());
				
		outputVO.setRoleLst(dam.exeQuery(queryCondition));
		
		sendRtnObject(outputVO);
	}
	
	public void review (Object body, IPrimitiveMap header) throws JBranchException {
		
		ORG330InputVO inputVO = (ORG330InputVO) body;
		dam = this.getDataAccessManager();
		
		TBORG_ROLE_REVIEWVO rewVO = (TBORG_ROLE_REVIEWVO) dam.findByPKey(TBORG_ROLE_REVIEWVO.TABLE_UID, new BigDecimal(inputVO.getSEQNO()));
		rewVO.setREVIEW_STATUS(inputVO.getREVIEW_STATUS());
		dam.update(rewVO);
				
		if ("A".equals(rewVO.getACT_TYPE()) && "Y".equals(rewVO.getREVIEW_STATUS())) { // 新增
			TBORG_ROLEVO roleVO = new TBORG_ROLEVO();
			roleVO.setROLE_ID(rewVO.getROLE_ID()); // 角色代號
			roleVO.setROLE_NAME(rewVO.getROLE_NAME()); // 角色名稱
			roleVO.setJOB_TITLE_NAME(rewVO.getJOB_TITLE_NAME()); // 人資職稱
			roleVO.setIS_AO(rewVO.getIS_AO()); // 是否為理專
			roleVO.setAGENT_ROLE(rewVO.getAGENT_ROLE());
			roleVO.setSYS_ROLE(rewVO.getSYS_ROLE());
			roleVO.setACT_TYPE(rewVO.getACT_TYPE()); // 異動註記
			roleVO.setREVIEW_STATUS(rewVO.getREVIEW_STATUS()); // 覆核狀態
			dam.create(roleVO);
			
			// 系統角色表同時新增
			TBSYSSECUROLEVO secRoleVO = new TBSYSSECUROLEVO();
			secRoleVO.setROLEID(rewVO.getROLE_ID());
			secRoleVO.setNAME(rewVO.getROLE_NAME());
			secRoleVO.setDESCRIPTION(rewVO.getJOB_TITLE_NAME());
			dam.create(secRoleVO);
			
			// 可視範圍同時新增
			TBSYSPARAMETERVO parameterVO = new TBSYSPARAMETERVO();
			TBSYSPARAMETERPK parameterPK = new TBSYSPARAMETERPK();
			parameterPK.setPARAM_TYPE("FUBONSYS." + rewVO.getSYS_ROLE());
			parameterPK.setPARAM_CODE(rewVO.getROLE_ID());
			parameterVO.setcomp_id(parameterPK);
			parameterVO.setPARAM_ORDER(9);
			parameterVO.setPARAM_NAME(rewVO.getROLE_NAME());
			parameterVO.setPARAM_NAME_EDIT(rewVO.getROLE_NAME());
			parameterVO.setPARAM_DESC(rewVO.getJOB_TITLE_NAME());
			parameterVO.setPARAM_STATUS("0");
			dam.create(parameterVO);
		} else if ("M".equals(rewVO.getACT_TYPE()) && "Y".equals(rewVO.getREVIEW_STATUS())) { // 修改
			
			TBORG_ROLEVO roleVO = (TBORG_ROLEVO) dam.findByPKey(TBORG_ROLEVO.TABLE_UID, rewVO.getROLE_ID());
			
			// 可視範圍同時修改(先刪除後新增)			
			TBSYSPARAMETERPK parameterPK = new TBSYSPARAMETERPK();
			TBSYSPARAMETERVO parameterVO = new TBSYSPARAMETERVO();
			parameterPK.setPARAM_TYPE("FUBONSYS." + roleVO.getSYS_ROLE());
			parameterPK.setPARAM_CODE(rewVO.getROLE_ID());
			parameterVO.setcomp_id(parameterPK);
			parameterVO = (TBSYSPARAMETERVO) dam.findByPKey(TBSYSPARAMETERVO.TABLE_UID, parameterVO.getcomp_id());
			if (parameterVO != null) {
				dam.delete(parameterVO);
			}
			
			parameterVO = new TBSYSPARAMETERVO();
			parameterPK = new TBSYSPARAMETERPK();
			parameterPK.setPARAM_TYPE("FUBONSYS." + rewVO.getSYS_ROLE());
			parameterPK.setPARAM_CODE(rewVO.getROLE_ID());
			parameterVO.setcomp_id(parameterPK);
			parameterVO.setPARAM_ORDER(9);
			parameterVO.setPARAM_NAME(rewVO.getROLE_NAME());
			parameterVO.setPARAM_NAME_EDIT(rewVO.getROLE_NAME());
			parameterVO.setPARAM_DESC(rewVO.getJOB_TITLE_NAME());
			parameterVO.setPARAM_STATUS("0");
			dam.create(parameterVO);
			
			if (roleVO != null) {
				roleVO.setROLE_NAME(rewVO.getROLE_NAME());
				roleVO.setJOB_TITLE_NAME(rewVO.getJOB_TITLE_NAME());
				roleVO.setIS_AO(rewVO.getIS_AO());
				roleVO.setAGENT_ROLE(rewVO.getAGENT_ROLE());
				roleVO.setSYS_ROLE(rewVO.getSYS_ROLE());
				roleVO.setACT_TYPE(rewVO.getACT_TYPE());
				roleVO.setREVIEW_STATUS(rewVO.getREVIEW_STATUS());
				dam.update(roleVO);
			}
			
			// 系統角色表同時修改
			TBSYSSECUROLEVO secRoleVO = (TBSYSSECUROLEVO) dam.findByPKey(TBSYSSECUROLEVO.TABLE_UID, rewVO.getROLE_ID());
			if (secRoleVO != null) {
				secRoleVO.setNAME(rewVO.getROLE_NAME());
				secRoleVO.setDESCRIPTION(rewVO.getJOB_TITLE_NAME());
				dam.update(secRoleVO);
			}
		} else if ("D".equals(rewVO.getACT_TYPE()) && "Y".equals(rewVO.getREVIEW_STATUS())) { // 刪除
			TBORG_ROLEVO roleVO = (TBORG_ROLEVO) dam.findByPKey(TBORG_ROLEVO.TABLE_UID, rewVO.getROLE_ID());
			if (roleVO != null) {
				dam.delete(roleVO);
			}
			
			// 系統角色表同時刪除
			TBSYSSECUROLEVO secRoleVO = (TBSYSSECUROLEVO) dam.findByPKey(TBSYSSECUROLEVO.TABLE_UID, rewVO.getROLE_ID());
			if (secRoleVO != null) {
				dam.delete(secRoleVO);
			}
			
			// 可視範圍同時刪除
			TBSYSPARAMETERPK parameterPK = new TBSYSPARAMETERPK();
			TBSYSPARAMETERVO parameterVO = new TBSYSPARAMETERVO();
			parameterPK.setPARAM_TYPE("FUBONSYS." + rewVO.getSYS_ROLE());
			parameterPK.setPARAM_CODE(rewVO.getROLE_ID());
			parameterVO.setcomp_id(parameterPK);
			parameterVO = (TBSYSPARAMETERVO) dam.findByPKey(TBSYSPARAMETERVO.TABLE_UID, parameterVO.getcomp_id());
			if (parameterVO != null) {
				dam.delete(parameterVO);
			}
		}
		
		sendRtnObject(null);
	}
	
	// 新增角色
	public void addRole (Object body, IPrimitiveMap header) throws JBranchException {
		ORG330InputVO inputVO = (ORG330InputVO) body;
		dam = this.getDataAccessManager();
		
		BigDecimal seqNo = new BigDecimal(getSeqNum());

		TBORG_ROLE_REVIEWVO vo = new TBORG_ROLE_REVIEWVO();
		vo.setSEQNO(seqNo);
		vo.setROLE_ID(inputVO.getROLE_ID()); // 角色代號
		vo.setROLE_NAME(inputVO.getROLE_NAME()); // 角色名稱
		vo.setJOB_TITLE_NAME(inputVO.getJOB_TITLE_NAME()); // 人資職稱
		vo.setIS_AO(inputVO.getIS_AO()); // 是否為理專
		vo.setAGENT_ROLE(inputVO.getAGENT_ROLE()); // 可代理該角色的角色
		vo.setSYS_ROLE(inputVO.getSYS_ROLE()); // 可視範圍
		vo.setACT_TYPE("A"); // A-新增 M-修改 D-刪除
		vo.setREVIEW_STATUS("W"); //Y-已覆核 W-待覆核 N-退回
		dam.create(vo);
		
		sendRtnObject(null);
	}
	
	public void delRole(Object body, IPrimitiveMap header) throws JBranchException {
		ORG330InputVO inputVO = (ORG330InputVO) body;
		dam = this.getDataAccessManager();
		
		TBORG_ROLEVO roleVO = (TBORG_ROLEVO) dam.findByPKey(TBORG_ROLEVO.TABLE_UID, inputVO.getROLE_ID());
				
		BigDecimal seqNo = new BigDecimal(getSeqNum());

		TBORG_ROLE_REVIEWVO vo = new TBORG_ROLE_REVIEWVO();
		vo.setSEQNO(seqNo);
		vo.setROLE_ID(roleVO.getROLE_ID()); // 角色代號
		vo.setROLE_NAME(roleVO.getROLE_NAME()); // 角色名稱
		vo.setJOB_TITLE_NAME(roleVO.getJOB_TITLE_NAME()); // 人資職稱
		vo.setIS_AO(roleVO.getIS_AO()); // 是否為理專
		vo.setAGENT_ROLE(inputVO.getAGENT_ROLE()); // 可代理該角色的角色
		vo.setSYS_ROLE(inputVO.getSYS_ROLE()); // 可視範圍
		vo.setACT_TYPE("D"); // A-新增 M-修改 D-刪除
		vo.setREVIEW_STATUS("W"); //Y-已覆核 W-待覆核 N-退回
		dam.create(vo);
		
		sendRtnObject(null);
	}
	
	public void updateRole(Object body, IPrimitiveMap header) throws JBranchException {
		ORG330InputVO inputVO = (ORG330InputVO) body;
		dam = this.getDataAccessManager();
		
		BigDecimal seqNo = new BigDecimal(getSeqNum());

		TBORG_ROLE_REVIEWVO vo = new TBORG_ROLE_REVIEWVO();
		vo.setSEQNO(seqNo);
		vo.setROLE_ID(inputVO.getROLE_ID()); // 角色代號
		vo.setROLE_NAME(inputVO.getROLE_NAME()); // 角色名稱
		vo.setJOB_TITLE_NAME(inputVO.getJOB_TITLE_NAME()); // 人資職稱
		vo.setIS_AO(inputVO.getIS_AO()); // 是否為理專
		vo.setAGENT_ROLE(inputVO.getAGENT_ROLE()); // 可代理該角色的角色
		vo.setSYS_ROLE(inputVO.getSYS_ROLE()); // 可視範圍
		vo.setACT_TYPE("M"); // A-新增 M-修改 D-刪除
		vo.setREVIEW_STATUS("W"); //Y-已覆核 W-待覆核 N-退回
		dam.create(vo);
		
		sendRtnObject(null);
	}
	
	public void getReviewList (Object body, IPrimitiveMap header) throws JBranchException {
		
		ORG330InputVO inputVO = (ORG330InputVO) body;
		ORG330OutputVO outputVO = new ORG330OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer(); //replace(AGENT_ROLE, ',', ' \n') AS 
		sb.append("SELECT ROLE_ID, ROLE_NAME, JOB_TITLE_NAME, IS_AO, AGENT_ROLE, SYS_ROLE, ACT_TYPE, REVIEW_STATUS, CREATETIME, CREATOR ");
		sb.append("FROM TBORG_ROLE_REVIEW ");
		sb.append("WHERE ROLE_ID = :roleID ");
		sb.append("ORDER BY CREATETIME DESC");
		
		queryCondition.setQueryString(sb.toString());
		
		queryCondition.setObject("roleID", inputVO.getROLE_ID());
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		for (Map<String, Object> map : list) {
			String agentRoleName = "";
			if (StringUtils.isNotBlank(String.valueOf(map.get("AGENT_ROLE")))) {
				String[] agentRole = String.valueOf(map.get("AGENT_ROLE")).split(",");
				for (int i = 0; i < agentRole.length; i++) {
					TBORG_ROLEVO vo = new TBORG_ROLEVO();
					vo = (TBORG_ROLEVO) dam.findByPKey(TBORG_ROLEVO.TABLE_UID, agentRole[i]);
					if (null != vo) {
						agentRoleName = agentRoleName + vo.getROLE_NAME() + ((i + 1) == agentRole.length ? "" : ",");
					}
				}
			}
			map.put("AGENT_ROLE", agentRoleName);
		}
		
		outputVO.setReviewList(list);
	
		sendRtnObject(outputVO);
	}
	
	private String getSeqNum() throws JBranchException {
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT SQ_TBORG_ROLE_REVIEW.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		
		return ObjectUtils.toString(SEQLIST.get(0).get("SEQ"));
	}
	
}