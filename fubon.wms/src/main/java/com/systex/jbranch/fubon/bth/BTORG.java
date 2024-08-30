package com.systex.jbranch.fubon.bth;

import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.bth.job.context.AccessContext;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.scheduler.SchedulerHelper;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.util.IPrimitiveMap;
@Component("btorg")
@Scope("prototype")
@SuppressWarnings({"rawtypes", "unchecked"})
public class BTORG extends BizLogic {
	private DataAccessManager dam = null;
	
	//寫入target(TBORG_PS_SA_INS_EMPFUND)
	public void insertTargetFromTmp(Object body, IPrimitiveMap<?> header) throws Exception {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		Object arg = ((Map)((Map)body).get(SchedulerHelper.JOB_PARAMETER_KEY)).get("arg");
		
		String sql = new StringBuffer()
			.append("insert into TBORG_PS_SA_INS_EMPFUND ")
			.append("select C.EMPID, F.FUNDID, TO_DATE(C.TNDDATE) TNDDATE, M.CUST_ID, SYSDATE, :class CLASS ")
			.append("from TBORG_PS_SA_INS_CLASS_SG C ")
			.append("join TBORG_PS_SA_INS_FUND_SG F ")
			.append("on :class = F.CLASS ")
			.append("join TBORG_MEMBER M ")
			.append("on C.EMPID = M.EMP_ID ")
			.append("where M.CHANGE_FLAG in ('A','M','P') ")
			.append("and not exists (  ")
			.append("  select person_id from TBORG_PS_SA_INS_EMPFUND X where C.EMPID = X.EMPID AND F.FUNDID = X.FUND_CODE ")
			.append(") ")
			.toString();
		
		queryCondition.setQueryString(sql);
		queryCondition.setObject("class", arg);
		dam.exeUpdate(queryCondition);
	}
	
	//target產檔
	public void genFileFromTarget(Object body, IPrimitiveMap<?> header) throws Exception {
		//撈取要寫入檔案的資料
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		String sql = new StringBuffer()
			.append("select PERSON_ID || ',' || FUND_CODE || ',' || to_char(END_DATE,'yyyy/mm/dd') LINE ")
			.append("from TBORG_PS_SA_INS_EMPFUND  ")
			.append("where DATA_DATE >= (SYSDATE - 15)  ")
			.append("and EMPID in ( ")
			.append("  select distinct emp_id from TBORG_MEMBER_CERT where length(CERTIFICATE_CODE) = 2 ")
			.append(") ")
			.append("and PERSON_ID in (  ")
			.append("  select cust_id from TBORG_MEMBER where CHANGE_FLAG in ('A','M','P') AND SERVICE_FLAG = 'A'")
			.append(") ")
			.append("order by PERSON_ID, FUND_CODE ")
			.toString();
			
			try (
					BufferedWriter bw = Files.newBufferedWriter(
							Paths.get(AccessContext.tempReportPath + "FD_8A.TXT"),
							StandardCharsets.UTF_8);
			) {
				queryCondition.setQueryString(sql);
				List<Map> result = dam.exeQuery(queryCondition);
				for(Map m: result) 
					bw.append((String)m.get("LINE") + "\r\n");
				
				bw.flush();
			}
	}
}