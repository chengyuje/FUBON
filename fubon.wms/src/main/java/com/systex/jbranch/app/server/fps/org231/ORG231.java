package com.systex.jbranch.app.server.fps.org231;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

import de.schlichtherle.io.FileInputStream;

@Component("org231")
@Scope("request")
public class ORG231 extends FubonWmsBizLogic {
	
	public DataAccessManager dam = null;
	
	public void getList(Object body, IPrimitiveMap header) throws Exception {

		ORG231OutputVO outputVO = new ORG231OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT RM1_CNT, RM2_CNT, SRM_CNT, MODIFIER, LASTUPDATE ");
		sb.append("FROM TBORG_UHRM_MBR_QUOTA ");
		
		queryCondition.setQueryString(sb.toString());
		
		outputVO.setResultList(dam.exeQuery(queryCondition));
		
		sendRtnObject(outputVO);
	}
	
	public void save(Object body, IPrimitiveMap header) throws Exception {
		
		ORG231InputVO inputVO = (ORG231InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
			StringBuffer sb = new StringBuffer();
						
			sb = new StringBuffer();
			sb.append("UPDATE TBORG_UHRM_MBR_QUOTA ");
			sb.append("SET RM1_CNT = :RM1_CNT, RM2_CNT = :RM2_CNT, SRM_CNT = :SRM_CNT, "); 
			sb.append("    VERSION = VERSION + 1, MODIFIER = :empId, LASTUPDATE = sysdate ");
			
			queryCondition.setObject("RM1_CNT", StringUtils.isNotEmpty(inputVO.getRM1_CNT()) ? new BigDecimal(inputVO.getRM1_CNT()) : BigDecimal.ZERO);
			queryCondition.setObject("RM2_CNT", StringUtils.isNotEmpty(inputVO.getRM2_CNT()) ? new BigDecimal(inputVO.getRM2_CNT()) : BigDecimal.ZERO);
			queryCondition.setObject("SRM_CNT", StringUtils.isNotEmpty(inputVO.getSRM_CNT()) ? new BigDecimal(inputVO.getSRM_CNT()) : BigDecimal.ZERO);
			queryCondition.setObject("empId", getUserVariable(FubonSystemVariableConsts.LOGINID));
			
			queryCondition.setQueryString(sb.toString());
			
			dam.exeUpdate(queryCondition);

		sendRtnObject(null);
	}
	
}
