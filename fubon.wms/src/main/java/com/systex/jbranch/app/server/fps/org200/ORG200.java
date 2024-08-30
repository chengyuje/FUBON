package com.systex.jbranch.app.server.fps.org200;

import java.util.List;
import java.util.Map;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBORG_DEFNVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("org200")
@Scope("request")
public class ORG200 extends FubonWmsBizLogic {
	public DataAccessManager dam = null;
	
	public void getTopMostDept(Object body, IPrimitiveMap header) throws JBranchException {
		
		ORG200OutputVO outputVO = new ORG200OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT DEPT_ID, DEPT_NAME, ORG_TYPE, PARENT_DEPT_ID, DEPT_DEGREE, DEPT_GROUP ");
		sb.append("FROM TBORG_DEFN ");
		sb.append("WHERE ORG_TYPE = '00'");
		condition.setQueryString(sb.toString());

		outputVO.setParentLst(dam.exeQuery(condition));
		sendRtnObject(outputVO);
	}
	
	
	public void getDeptDetail(Object body, IPrimitiveMap header) throws JBranchException {
		
		ORG200InputVO inputVO = (ORG200InputVO) body;
		ORG200OutputVO outputVO = new ORG200OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT DEFN.DEPT_ID, DEFN.DEPT_NAME, DEFN.ORG_TYPE, ");
		sb.append("       DEFN.PARENT_DEPT_ID, (SELECT TMP.ORG_TYPE FROM TBORG_DEFN TMP WHERE TMP.DEPT_ID = DEFN.PARENT_DEPT_ID) AS PARENT_ORG_TYPE, ");
		sb.append("       DEFN.DEPT_DEGREE, DEFN.DEPT_GROUP ");
		sb.append("FROM TBORG_DEFN DEFN ");
		sb.append("WHERE DEPT_ID = :deptId ");
		sb.append("ORDER BY DEPT_ID, DEPT_NAME ");
		
		condition.setQueryString(sb.toString());
		condition.setObject("deptId", inputVO.getDEPT_ID());
		
		outputVO.setDeptDetail(dam.exeQuery(condition));
		sendRtnObject(outputVO);
	}
	
	public void getSubDeptLst(Object body, IPrimitiveMap header) throws JBranchException {
		
		ORG200InputVO inputVO = (ORG200InputVO) body;
		ORG200OutputVO outputVO = new ORG200OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT DEPT_ID, DEPT_NAME, ORG_TYPE, PARENT_DEPT_ID, DEPT_DEGREE, DEPT_GROUP ");
		sb.append("FROM TBORG_DEFN ");
		sb.append("WHERE PARENT_DEPT_ID = :deptId ");
		sb.append("ORDER BY DEPT_ID, DEPT_NAME ");
		condition.setQueryString(sb.toString());
		condition.setObject("deptId", inputVO.getDEPT_ID());

		outputVO.setSubDeptLst(dam.exeQuery(condition));
		sendRtnObject(outputVO);
	}

	public void getParentLst(Object body, IPrimitiveMap header) throws JBranchException {
		
		ORG200InputVO inputVO = (ORG200InputVO) body;
		ORG200OutputVO outputVO = new ORG200OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		if ("".equals(inputVO.getORG_TYPE().trim())) {
			sb.append("SELECT DEPT_ID, DEPT_NAME, ORG_TYPE, PARENT_DEPT_ID, DEPT_DEGREE, DEPT_GROUP ");
			sb.append("FROM TBORG_DEFN ");
			sb.append("WHERE ORG_TYPE < (SELECT ORG_TYPE FROM TBORG_DEFN WHERE DEPT_ID = :deptId) ");
			sb.append("ORDER BY DEPT_ID, DEPT_NAME ");;
			condition.setQueryString(sb.toString());
			condition.setObject("deptId", inputVO.getDEPT_ID());
		} else {
			sb.append("SELECT DEPT_ID, DEPT_NAME, ORG_TYPE, PARENT_DEPT_ID, DEPT_DEGREE, DEPT_GROUP ");
			sb.append("FROM TBORG_DEFN ");
			sb.append("WHERE ORG_TYPE < :orgType ");
			sb.append("ORDER BY DEPT_ID, DEPT_NAME ");
			condition.setQueryString(sb.toString());
			condition.setObject("orgType", inputVO.getORG_TYPE());
		}
		
		outputVO.setParentLst(dam.exeQuery(condition));
		sendRtnObject(outputVO);
	}
	
	public void modDeptDetail(Object body, IPrimitiveMap header) throws JBranchException {
		
		ORG200InputVO inputVO = (ORG200InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		TBORG_DEFNVO defnVO = (TBORG_DEFNVO) dam.findByPKey(TBORG_DEFNVO.TABLE_UID, inputVO.getOLD_DEPT_ID());
		if (!inputVO.getDEPT_ID().equals(inputVO.getOLD_DEPT_ID())) {
			defnVO.setDEPT_ID(inputVO.getDEPT_ID());
		}
		defnVO.setDEPT_NAME(inputVO.getDEPT_NAME());
		defnVO.setORG_TYPE(inputVO.getORG_TYPE());
		defnVO.setPARENT_DEPT_ID(inputVO.getPARENT_DEPT_ID());
		dam.update(defnVO);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT DEPT_ID ");
		sb.append("FROM TBORG_DEFN ");
		sb.append("WHERE PARENT_DEPT_ID = :deptId");
		condition.setQueryString(sb.toString());
		condition.setObject("deptId", inputVO.getDEPT_ID());
		List childrenLst = dam.exeQuery(condition);
		
		for(int i = 0; i < childrenLst.size(); i++) {
			dam = this.getDataAccessManager();
			TBORG_DEFNVO child = (TBORG_DEFNVO) dam.findByPKey(TBORG_DEFNVO.TABLE_UID, (String) ((Map) childrenLst.get(i)).get("DEPT_ID"));
			child.setPARENT_DEPT_ID(inputVO.getDEPT_ID());
			dam.update(child);
		}
		
		// 行動載具組織檔
		exeUpdateForQcf(genDefaultQueryConditionIF()
				.setQueryString(new StringBuilder()
				.append("UPDATE TBMAO_DEV_MAST A ")
				.append("SET DC_NBR = (SELECT REGION_CENTER_ID ")
				.append("              FROM VWORG_DEFN_INFO B ")
				.append("              WHERE B.BRANCH_NBR = A.BRA_NBR), ")
				.append("    OP_NBR = (SELECT BRANCH_AREA_ID ")
				.append("              FROM VWORG_DEFN_INFO B ")
				.append("              WHERE B.BRANCH_NBR = A.BRA_NBR) ").toString()));
		
		// 回報計畫組織歷史
		exeUpdateForQcf(genDefaultQueryConditionIF()
				.setQueryString(new StringBuilder()
				.append("UPDATE TBCAM_IVG_PLAN_MAIN_ORG A ")
				.append("SET A.BRANCH_AREA_ID = (SELECT BRANCH_AREA_ID ")
				.append("                        FROM VWORG_DEFN_INFO INFO ")
				.append("                        WHERE INFO.BRANCH_NBR = A.BRANCH_NBR), ")
				.append("    A.REGION_CENTER_ID = (SELECT REGION_CENTER_ID ")
				.append("                          FROM VWORG_DEFN_INFO INFO ")
				.append("                          WHERE INFO.BRANCH_NBR = A.BRANCH_NBR) ")
				.append("WHERE EXISTS (SELECT B.IVG_PLAN_SEQ ")
				.append("              FROM TBCAM_IVG_PLAN_MAIN B ")
				.append("              WHERE TRUNC(B.IVG_END_DATE) >= TRUNC(SYSDATE) ") //--尚未到期
				.append("              AND A.IVG_PLAN_SEQ = B.IVG_PLAN_SEQ) ").toString()));
		
		// 回報計畫回報內容
		exeUpdateForQcf(genDefaultQueryConditionIF()
				.setQueryString(new StringBuilder()
				.append("UPDATE TBCAM_IVG_PLAN_CONTENT A ")
				.append("SET A.BRANCH_AREA_ID = (SELECT BRANCH_AREA_ID ")
				.append("                        FROM VWORG_DEFN_INFO INFO ")
				.append("                        WHERE INFO.BRANCH_NBR = A.BRANCH_NBR), ")
				.append("    A.REGION_CENTER_ID = (SELECT REGION_CENTER_ID ")
				.append("                          FROM VWORG_DEFN_INFO INFO ")
				.append("                          WHERE INFO.BRANCH_NBR = A.BRANCH_NBR) ")
				.append("WHERE EXISTS (SELECT B.IVG_PLAN_SEQ ")
				.append("              FROM TBCAM_IVG_PLAN_MAIN B ")
				.append("              WHERE TRUNC(B.IVG_END_DATE) >= TRUNC(SYSDATE) ") //--尚未到期
				.append("              AND A.IVG_PLAN_SEQ = B.IVG_PLAN_SEQ) ").toString()));
		
		sendRtnObject(null);
	}
	
	public void delDept(Object body, IPrimitiveMap header) throws JBranchException {
		
		ORG200InputVO inputVO = (ORG200InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		TBORG_DEFNVO defnVO = (TBORG_DEFNVO) dam.findByPKey(TBORG_DEFNVO.TABLE_UID, inputVO.getDEPT_ID());
		dam.delete(defnVO);
				
		StringBuffer sql = new StringBuffer();
		sql.append(" UPDATE TBORG_MEMBER SET DEPT_ID = '' WHERE DEPT_ID = :DEPT_ID ");
		condition.setQueryString(sql.toString());
		condition.setObject("DEPT_ID", inputVO.getDEPT_ID());
		dam.exeUpdate(condition);
		
		sendRtnObject(null);
	}
	
	public void addDeptDetail(Object body, IPrimitiveMap header) {
		
		ORG200InputVO inputVO = (ORG200InputVO) body;
		dam = this.getDataAccessManager();
		
		TBORG_DEFNVO defnVO = new TBORG_DEFNVO();
		defnVO.setDEPT_ID(inputVO.getDEPT_ID());
		defnVO.setDEPT_NAME(inputVO.getDEPT_NAME());
		defnVO.setORG_TYPE(inputVO.getORG_TYPE());
		defnVO.setPARENT_DEPT_ID(inputVO.getPARENT_DEPT_ID());
		try {
			dam.create(defnVO);
			sendRtnObject(null);
		} catch (JBranchException jex) {
			if (jex.getException() instanceof ConstraintViolationException) {
				sendErrMsg("新增失敗，請確認組織代碼" + inputVO.getDEPT_ID() + "是否重覆");
			}
		}
	}
	
	public void getOrgType(Object body, IPrimitiveMap header) throws JBranchException {
		
		ORG200InputVO inputVO = (ORG200InputVO) body;
		ORG200OutputVO outputVO = new ORG200OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT PARAM_CODE AS ORG_TYPE ");
		sb.append("FROM TBSYSPARAMETER ");
		sb.append("WHERE PARAM_TYPE = 'ORG.TYPE' ");
		sb.append("AND PARAM_CODE > (SELECT ORG_TYPE FROM TBORG_DEFN WHERE DEPT_ID = :parentDeptId) ");
		sb.append("AND ROWNUM < 2 ");
		sb.append("ORDER BY PARAM_CODE ASC");
		condition.setQueryString(sb.toString());
		condition.setObject("parentDeptId", inputVO.getPARENT_DEPT_ID());

		outputVO.setDeptDetail(dam.exeQuery(condition));
		sendRtnObject(outputVO);
	}
}
