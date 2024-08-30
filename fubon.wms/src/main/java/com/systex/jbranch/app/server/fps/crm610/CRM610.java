package com.systex.jbranch.app.server.fps.crm610;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_MASTVO;
import com.systex.jbranch.app.common.fps.table.TBORG_SALES_AOCODEPK;
import com.systex.jbranch.app.common.fps.table.TBORG_SALES_AOCODEVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author Stella
 * @date 2016/11/10
 * 
 */ 
@Component("crm610")
@Scope("request")
public class CRM610 extends FubonWmsBizLogic{
	
	private DataAccessManager dam;
	
	public void initial(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM610InputVO inputVO = (CRM610InputVO) body;
		CRM610OutputVO return_VO = new CRM610OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT A.CUST_ID, ");
		sql.append("       A.CUST_NAME, ");
		sql.append("       A.VIP_DEGREE, ");
		sql.append("       A.FAMILY_DEGREE, ");
		sql.append("       A.AO_CODE, ");
		
		sql.append("	   CASE WHEN C.TYPE = '1' THEN '(主)' "); 		// AO CODE類型：1主 2副 3維護
		sql.append("	        WHEN C.TYPE = '2' THEN '(副)' "); 										
		sql.append("	        WHEN C.TYPE = '3' THEN '(維護)'"); 										
		sql.append("	   ELSE '' END AS C_TYPE_NAME , ");
		
		sql.append("       C.EMP_ID, ");
		sql.append("       C.EMP_NAME, ");
		sql.append("       B.BRANCH_NAME, ");
		sql.append("       UHRM.EMP_ID AS UEMP_ID, ");
		sql.append("       UHRM.EMP_NAME || CASE WHEN UHRM.CODE_TYPE = '1' THEN '(計績)' WHEN UHRM.CODE_TYPE = '3' THEN '(維護)' ELSE '' END AS UEMP_NAME ");
		sql.append("FROM TBCRM_CUST_MAST A ");
		sql.append("LEFT JOIN VWORG_EMP_UHRM_INFO UHRM ON UHRM.UHRM_CODE = A.AO_CODE ");
		sql.append("left outer join VWORG_DEFN_INFO B on B.BRANCH_NBR = A.BRA_NBR ");
		sql.append("left outer join VWORG_AO_INFO C on C.AO_CODE = A.AO_CODE ");
		sql.append("left outer join TBORG_MEMBER M on A.UEMP_ID = M.EMP_ID ");
		sql.append("where A.CUST_ID = :cust_id ");
		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("cust_id", inputVO.getCust_id());
		
		List list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		
		//家庭會員Tab顯示Title
		QueryConditionIF queryCondition2 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append(" SELECT PARAM.PARAM_NAME AS REL_TYPE_DESC ");
		sql.append(" FROM (SELECT DISTINCT (CASE WHEN P.FAMILY_DEGREE IS NOT NULL THEN P.FAMILY_DEGREE  ");
		sql.append(" 					  		 WHEN A.VIP_DEGREE='K' OR A.VIP_DEGREE='T' THEN A.VIP_DEGREE  ");
//		sql.append(" 					  		 WHEN A.VIP_DEGREE='A' OR A.VIP_DEGREE='V' THEN A.VIP_DEGREE  ");
		sql.append(" 						ELSE '' END) AS FAMILY_DEGREE  ");
		sql.append(" 		FROM TBCRM_CUST_MAST A  ");
		sql.append(" 		left outer join TBCRM_CUST_PRV P ON CUST_ID_M = A.CUST_ID OR CUST_ID_S = A.CUST_ID   ");   
		sql.append(" 		where A.CUST_ID = :cust_id) F ");
		sql.append(" LEFT OUTER JOIN TBSYSPARAMETER PARAM ON PARAM.PARAM_CODE = F.FAMILY_DEGREE AND PARAM.PARAM_TYPE = 'CRM.VIP_DEGREE'  ");
		queryCondition2.setQueryString(sql.toString());
		queryCondition2.setObject("cust_id", inputVO.getCust_id());
		
		List list2 = dam.exeQuery(queryCondition2);
		return_VO.setResultList2(list2);
		
		sendRtnObject(return_VO);
	}

	public void addInquireLog(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM610InputVO inputVO = (CRM610InputVO) body;
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		// === 取得員工資料START ===
		sb.append("SELECT MEM.EMP_ID, MEM.EMP_NAME, MR.ROLE_ID, RO.ROLE_NAME ");
		sb.append("FROM TBORG_MEMBER MEM ");
		sb.append("LEFT JOIN TBORG_MEMBER_ROLE MR ON MEM.EMP_ID = MR.EMP_ID ");
		sb.append("LEFT JOIN TBORG_ROLE RO ON MR.ROLE_ID = RO.ROLE_ID ");
		sb.append("WHERE MEM.EMP_ID = :loginID "); 
		sb.append("AND MR.ROLE_ID = :loginEmpRoleID ");
		
		queryCondition.setQueryString(sb.toString());

		queryCondition.setObject("loginID", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
		queryCondition.setObject("loginEmpRoleID", (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		
		List<Map<String, Object>> empList = dam.exeQuery(queryCondition);
		// === 取得員工資料END ===
		
		// === 取得客戶資料START ===
		TBCRM_CUST_MASTVO crmVO = (TBCRM_CUST_MASTVO) dam.findByPKey(TBCRM_CUST_MASTVO.TABLE_UID, inputVO.getCust_id());
		// === 取得客戶資料END ===
		
		// === 比對客戶理專與登入理專 START ===
		TBORG_SALES_AOCODEVO aoVO = new TBORG_SALES_AOCODEVO();
		if (StringUtils.isNotEmpty(crmVO.getAO_CODE())) {
			TBORG_SALES_AOCODEPK aoPK = new TBORG_SALES_AOCODEPK();
			aoPK.setAO_CODE(crmVO.getAO_CODE());
			aoPK.setEMP_ID((String) empList.get(0).get("EMP_ID"));
			
			aoVO = (TBORG_SALES_AOCODEVO) dam.findByPKey(TBORG_SALES_AOCODEVO.TABLE_UID, aoPK);
		} else {
			aoVO = null;
		}
		// === 比對客戶理專與登入理專 END ===
		
		// === 記錄START ===
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append("INSERT INTO TBPMS_INQUIRE_CUST_DTL ( ");
		sb.append("  SEQNO, ");
		sb.append("  INQ_DATE_TIME, ");
		sb.append("  BRANCH_NBR, ");
		sb.append("  INQ_EMP_ID, ");
		sb.append("  INQ_EMP_NAME, ");
		sb.append("  INQ_EMP_ROLE_ID, ");
		sb.append("  INQ_EMP_ROLE_NAME, ");
		sb.append("  CUST_ID, ");
		sb.append("  CUST_NAME, ");
		sb.append("  CUST_AO, ");
		sb.append("  VERSION, ");
		sb.append("  CREATETIME, ");
		sb.append("  CREATOR, ");
		sb.append("  MODIFIER, ");
		sb.append("  LASTUPDATE ");
		sb.append(") VALUES ( ");
		sb.append("  SQ_TBPMS_INQUIRE_CUST_DTL.NEXTVAL, ");
		sb.append("  SYSDATE, ");
		sb.append("  :BRANCH_NBR, ");
		sb.append("  :INQ_EMP_ID, ");
		sb.append("  :INQ_EMP_NAME, ");
		sb.append("  :INQ_EMP_ROLE_ID, ");
		sb.append("  :INQ_EMP_ROLE_NAME, ");
		sb.append("  :CUST_ID, ");
		sb.append("  :CUST_NAME, ");
		sb.append("  :CUST_AO, ");
		sb.append("  1, ");
		sb.append("  SYSDATE, ");
		sb.append("  :loginID, ");
		sb.append("  :loginID, ");
		sb.append("  SYSDATE ");
		sb.append(")");
		
		queryCondition.setQueryString(sb.toString());
		
		queryCondition.setObject("BRANCH_NBR", (String) getUserVariable(FubonSystemVariableConsts.LOGINBRH));
		queryCondition.setObject("INQ_EMP_ID", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
		queryCondition.setObject("INQ_EMP_NAME", (empList.size() > 0 ? empList.get(0).get("EMP_NAME") : ""));
		queryCondition.setObject("INQ_EMP_ROLE_ID", (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		queryCondition.setObject("INQ_EMP_ROLE_NAME", (empList.size() > 0 ? empList.get(0).get("ROLE_NAME") : ""));
		queryCondition.setObject("CUST_ID", inputVO.getCust_id());
		queryCondition.setObject("CUST_NAME", inputVO.getCust_name());
		queryCondition.setObject("CUST_AO", crmVO.getAO_CODE());
		queryCondition.setObject("loginID", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
		
		if (aoVO == null) {
			dam.exeUpdate(queryCondition);
		}
		
		// === 記錄END ===
	}
}
