package com.systex.jbranch.app.server.fps.mao221;

import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("mao221")
@Scope("request")
public class MAO221 extends FubonWmsBizLogic {
	
	private DataAccessManager dam = null;
	
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		MAO221InputVO inputVO = (MAO221InputVO) body;
		MAO221OutputVO outputVO = new MAO221OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT P.SEQ, E.EMP_NAME, ");
		sql.append("       P.USE_DATE, ");
		sql.append("       P.VISIT_CUST_LIST, ");
		sql.append("       P.USE_PERIOD, ");
		sql.append("       P.DEV_NBR, ");
		sql.append("       P.DEV_STATUS, ");
		sql.append("       P.APL_EMP_ID, ");
		sql.append("       P.DEV_TAKE_EMP_ID, ");
		sql.append("       P.DEV_TAKE_DATETIME, ");
		sql.append("       P.DEV_RETURN_EMP_ID, ");
		sql.append("       P.DEV_RETURN_DATETIME, ");
		sql.append("       SUBSTR(P.USE_PERIOD_S_TIME, 1, 2) || ':00' AS START_TIME, ");
		sql.append("       SUBSTR(P.USE_PERIOD_E_TIME, 1, 2) || ':00' AS END_TIME ");
		sql.append("FROM TBMAO_DEV_APL_PLIST P ");
		sql.append("LEFT JOIN TBORG_MEMBER E ON P.APL_EMP_ID = E.EMP_ID ");
		sql.append("WHERE 1 = 1 ");
        sql.append("AND EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UP WHERE UP.DEPT_ID = E.DEPT_ID AND P.APL_EMP_ID = UP.EMP_ID) ");

		if (!StringUtils.isBlank(inputVO.getEmp_id())) {
			sql.append("AND P.APL_EMP_ID like :emp_id ");
			queryCondition.setObject("emp_id", inputVO.getEmp_id());
		}
			
		if (inputVO.getUse_date_bgn() != null) {
			sql.append("AND TRUNC(P.USE_DATE) >= TRUNC(:start) ");
			queryCondition.setObject("start", new Timestamp(inputVO.getUse_date_bgn().getTime()));
		}
			
		if (inputVO.getUse_date_end() != null) {
			sql.append("AND TRUNC(P.USE_DATE) <= TRUNC(:end) ");
			queryCondition.setObject("end", new Timestamp(inputVO.getUse_date_end().getTime()));
		}
		
		sql.append("ORDER BY P.DEV_TAKE_DATETIME DESC, P.DEV_RETURN_DATETIME DESC ");
		
		queryCondition.setQueryString(sql.toString());
		
		outputVO.setResultList(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	}

	

}