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
		
		sb.append("MERGE INTO TBORG_UHRM_MBR_QUOTA A ");
		sb.append("USING ( ");
		sb.append("  SELECT DEPT_ID ");
		sb.append("  FROM TBORG_DEFN ");
		sb.append("  WHERE DEPT_NAME LIKE '私銀%區' ");
		sb.append(") B ON (A.DEPT_ID = B.DEPT_ID) ");
		sb.append("WHEN NOT MATCHED THEN ");
		sb.append("INSERT ( ");
		sb.append("  VERSION, ");
		sb.append("  CREATETIME, ");
		sb.append("  CREATOR, ");
		sb.append("  MODIFIER, ");
		sb.append("  LASTUPDATE, ");
		sb.append("  SRM1_CNT, ");
		sb.append("  SRM2_CNT, ");
		sb.append("  SRM3_CNT, ");
		sb.append("  OPH_CNT, ");
		sb.append("  OP1_CNT, ");
		sb.append("  OP2_CNT, ");
		sb.append("  DEPT_ID ");
		sb.append(") ");
		sb.append("VALUES ( ");
		sb.append("  0, SYSDATE, 'SYSTEM', 'SYSTEM', SYSDATE, 0, 0, 0, 0, 0, 0, B.DEPT_ID ");
		sb.append(") ");
		
		queryCondition.setQueryString(sb.toString());
		
		dam.exeUpdate(queryCondition);
		
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append("SELECT  Q.DEPT_ID, ");
		sb.append("        D.BRANCH_AREA_NAME, ");
		sb.append("        D.REGION_CENTER_NAME, ");
		sb.append("        Q.SRM1_CNT, ");
		sb.append("        Q.SRM2_CNT, ");
		sb.append("        Q.SRM3_CNT, ");
		sb.append("        Q.OPH_CNT, ");
		sb.append("        Q.OP1_CNT, ");
		sb.append("        Q.OP2_CNT,  ");
		sb.append("        Q.MODIFIER, ");
		sb.append("        Q.LASTUPDATE ");
		sb.append("FROM TBORG_UHRM_MBR_QUOTA Q ");
		sb.append("LEFT JOIN VWORG_DEFN_INFO D ON Q.DEPT_ID = D.BRANCH_AREA_ID ");
		sb.append("ORDER BY Q.DEPT_ID ");
		
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
		sb.append("SET SRM1_CNT = :SRM1_CNT, "); 
		sb.append("    SRM2_CNT = :SRM2_CNT, "); 
		sb.append("    SRM3_CNT = :SRM3_CNT, "); 
		sb.append("    OPH_CNT = :OPH_CNT, ");
		sb.append("    OP1_CNT = :OP1_CNT, ");
		sb.append("    OP2_CNT = :OP2_CNT,  ");
		sb.append("    VERSION = VERSION + 1, "); 
		sb.append("    MODIFIER = :empId, "); 
		sb.append("    LASTUPDATE = sysdate ");
		sb.append("WHERE DEPT_ID = :DEPT_ID ");
		
		queryCondition.setObject("SRM1_CNT", StringUtils.isNotEmpty(inputVO.getSRM1_CNT()) ? new BigDecimal(inputVO.getSRM1_CNT()) : BigDecimal.ZERO);
		queryCondition.setObject("SRM2_CNT", StringUtils.isNotEmpty(inputVO.getSRM2_CNT()) ? new BigDecimal(inputVO.getSRM2_CNT()) : BigDecimal.ZERO);
		queryCondition.setObject("SRM3_CNT", StringUtils.isNotEmpty(inputVO.getSRM3_CNT()) ? new BigDecimal(inputVO.getSRM3_CNT()) : BigDecimal.ZERO);
		queryCondition.setObject("OPH_CNT", StringUtils.isNotEmpty(inputVO.getOPH_CNT()) ? new BigDecimal(inputVO.getOPH_CNT()) : BigDecimal.ZERO);
		queryCondition.setObject("OP1_CNT", StringUtils.isNotEmpty(inputVO.getOP1_CNT()) ? new BigDecimal(inputVO.getOP1_CNT()) : BigDecimal.ZERO);
		queryCondition.setObject("OP2_CNT", StringUtils.isNotEmpty(inputVO.getOP2_CNT()) ? new BigDecimal(inputVO.getOP2_CNT()) : BigDecimal.ZERO);
		queryCondition.setObject("empId", getUserVariable(FubonSystemVariableConsts.LOGINID));
		queryCondition.setObject("DEPT_ID", inputVO.getDEPT_ID());
		
		queryCondition.setQueryString(sb.toString());
		
		dam.exeUpdate(queryCondition);

		sendRtnObject(null);
	}
}