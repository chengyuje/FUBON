package com.systex.jbranch.app.server.fps.cmmgr003;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.scheduler.ScheduleInitiator;
import com.systex.jbranch.platform.common.scheduler.ScheduleManagement;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.workday.WorkDayHelper;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("cmmgr003")
@Scope("request")
public class CMMGR003 extends BizLogic {

	private DataAccessManager dam=null;
	
	
	/**
	 * 發動查詢
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void inquire(Object body, IPrimitiveMap header)
			throws JBranchException {
		CMMGR003InputVO inputvo=(CMMGR003InputVO) body;
		
		ScheduleManagement smgr=new ScheduleManagement();
		
//		smgr.addOneTime("com.systex.jbranch.app.server.fps.CMBTH109", null, "method=execute", inputvo.getProcessor());	
		
		smgr.addOneTime("CMBTH109", null, "method=execute", inputvo.getProcessor());	

		sendRtnMsg("ehl_01_common_025");
		
		dam=this.getDataAccessManager();
		
		ScheduleManagement sm = new ScheduleManagement();
		sm.getProcessList(dam);
				
	}
	
	public void getProcessList(Object body, IPrimitiveMap header)
			throws JBranchException {
		ScheduleManagement sm = new ScheduleManagement();
		dam = this.getDataAccessManager();
		String[] processList = sm.getProcessList(dam);
		CMMGR003ProcessorOutputVO output = new CMMGR003ProcessorOutputVO();
		output.setProcessList(processList);
		sendRtnObject(output);
	}
	
	//更新查詢
	public void refresh(Object body, IPrimitiveMap header)
			throws JBranchException {
		CMMGR003InputVO inputvo=(CMMGR003InputVO) body;
		dam=this.getDataAccessManager();
		
		CMMGR003OutputVO output=new CMMGR003OutputVO();
		
		
		QueryConditionIF queryCondition = dam.getQueryCondition();
		
		String sqlQuery ="SELECT A.CREATETIME,B.FIRETIME,B.NEXT_FIRETIME,C.AUDITID,C.JOBID,C.SCHEDULEID,D.SCHEDULENAME,E.JOBNAME"
				+ " FROM TBSYSSCHDQUERYMASTER A"
				+ " LEFT OUTER JOIN TBSYSSCHDQUERYDETAIL B ON A.QUERYID=B.QUERYID" 
				+ " LEFT OUTER JOIN TBSYSSCHDADMASTER C ON B.AUDITID=C.AUDITID"
				+ " LEFT OUTER JOIN TBSYSSCHD D ON C.SCHEDULEID=D.SCHEDULEID"
				+ " LEFT OUTER JOIN TBSYSSCHDJOB E ON C.JOBID=E.JOBID WHERE A.PROCESSOR=? ";
		

		/**"WITH TMP AS (SELECT QUERYID,CREATETIME,MAX(CREATETIME) OVER (PARTITION BY PROCESSOR) AS MAX_CREATETIME" 
				+ " FROM TBSYSSCHDQUERYMASTER WHERE PROCESSOR=?)" 
				+ "SELECT A.CREATETIME,B.FIRETIME,B.NEXT_FIRETIME,C.AUDITID,C.JOBID,C.SCHEDULEID,D.SCHEDULENAME,E.JOBNAME"
				+ " FROM TMP A" 
				+ " LEFT OUTER JOIN TBSYSSCHDQUERYDETAIL B ON A.QUERYID=B.QUERYID" 
				+ " LEFT OUTER JOIN TBSYSSCHDADMASTER C ON B.AUDITID=C.AUDITID"
				+ " LEFT OUTER JOIN TBSYSSCHD D ON C.SCHEDULEID=D.SCHEDULEID"
				+ " LEFT OUTER JOIN TBSYSSCHDJOB E ON C.JOBID=E.JOBID WHERE A.CREATETIME=A.MAX_CREATETIME"; **/
		
		queryCondition.setQueryString(sqlQuery);
		queryCondition.setString(1, inputvo.getProcessor());
		
		List<Map<String, Object>> query_List = dam.executeQuery(queryCondition);
		
		if (query_List.size()==0)
		{
			throw new APException("ehl_01_common_001");
		}else{
			output.setDataList(query_List);
			sendRtnObject(output);
		}	
	}
	
	//重起查詢
	public void restart(Object body, IPrimitiveMap header)
			throws JBranchException,Exception {
		ScheduleInitiator scheini= PlatformContext.getBean(ScheduleInitiator.class); // new ScheduleInitiator();

		scheini.execute();
		
		sendRtnMsg("ehl_01_common_025");
			
		
	}
	
	public void refreshCandlander(Object body, IPrimitiveMap header) throws Exception{
		CMMGR003InputVO inputvo=(CMMGR003InputVO) body;
		ScheduleManagement sm = new ScheduleManagement();
		sm.addOneTimeAllProcess("com.systex.jbranch.app.server.fps.CMBTH113", null, WorkDayHelper.CALENDAR_DATA_PROVIDER + "=" + inputvo.getCalendar());
	}

}
