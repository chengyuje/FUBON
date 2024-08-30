package com.systex.jbranch.app.server.fps.cmmgr023;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 排程監控_確認未來排程預計要Get的檔案是否存在 for前端
 *
 * @author SamTu
 * @date 2019/09/04
 */
@Component("cmmgr023")
@Scope("request")
public class CMMGR023 extends FubonWmsBizLogic {
	private DataAccessManager dam;
	private QueryConditionIF condition;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyyMMdd");

	public void getLatestMoniterResult(Object body, IPrimitiveMap<?> header) throws Exception {
		CMMGR023OutputVO returnVO = new CMMGR023OutputVO();
		getDam();
		getCondition();
		condition.setQueryString(getQuerySql());
		List qryLst = dam.exeQuery(condition);
		returnVO.setRecentSCHDMonitorList(qryLst);

		condition.setQueryString(getMonitorTimeSql());
		List qryLst2 = dam.exeQuery(condition);
		HashMap map = (HashMap) qryLst2.get(0);
		returnVO.setMonitorTime(dateFormat.parse(map.get("MONITORTIME").toString()));

		this.sendRtnObject(returnVO);
	}
	
	public void getHistoryMoniterResult(Object body, IPrimitiveMap<?> header) throws DAOException, JBranchException{
		CMMGR023InputVO inputVO = (CMMGR023InputVO)body;
		CMMGR023OutputVO returnVO = new CMMGR023OutputVO();
		System.out.println(dateFormat2.format(inputVO.getSearchDate()));
		List qryLst2 = exeQueryForMap(getHistorySql(inputVO), getHistoryMap(inputVO));
		returnVO.setHisSCHDMonitorList(qryLst2);
		this.sendRtnObject(returnVO);
	}

	public String getQuerySql() {
		return new StringBuffer().append("select MONITORTIME, SCHEDULEID, JOBID, Substr(SCHDDEALTIME,1,4) || '-' || Substr(SCHDDEALTIME,5,2) || '-' || Substr(SCHDDEALTIME,7,2)|| ' ' || Substr(SCHDDEALTIME,9,2) || ':' || Substr(SCHDDEALTIME,11,2) || ':' || Substr(SCHDDEALTIME,13,2) SCHDMONITORTIME, FILECHECK, FTPGETCODE, FTPCONNECTRESULT, HOSTID, SRCDIRECTORY, SRCFILENAME ").append("from TBSYSSCHDMONITOR where MONITORTIME = (select max(MONITORTIME) from TBSYSSCHDMONITOR) and SCHDDEALTIME != '無'").toString();

	}

	public String getMonitorTimeSql() {
		return new StringBuffer().append("select MONITORTIME from TBSYSSCHDMONITOR where MONITORTIME = (select max(MONITORTIME) from TBSYSSCHDMONITOR)").toString();
	}
	
	public String getHistorySql(CMMGR023InputVO inputVO) {
		StringBuffer sql = new StringBuffer().append("select MONITORTIME, SCHEDULEID, JOBID, Substr(SCHDDEALTIME,1,4) || '-' || Substr(SCHDDEALTIME,5,2) || '-' || Substr(SCHDDEALTIME,7,2)|| ' ' || Substr(SCHDDEALTIME,9,2) || ':' || Substr(SCHDDEALTIME,11,2) || ':' || Substr(SCHDDEALTIME,13,2) SCHDMONITORTIME")
				.append(", FILECHECK, FTPGETCODE, FTPCONNECTRESULT, HOSTID, SRCDIRECTORY, SRCFILENAME  from TBSYSSCHDMONITOR where to_char(MONITORTIME,'yyyymmdd') = :searchDate AND SCHDDEALTIME !='無' AND FILECHECK !='Y' "); 
		
		if(StringUtils.isNotBlank(inputVO.getSCHDID())){
			sql.append("AND SCHEDULEID = :SCHEDULEID ");
			
		}
		if(StringUtils.isNotBlank(inputVO.getJOBID())){
			sql.append("AND JOBID = :JOBID ");
		}
		sql.append("ORDER BY SCHDMONITORTIME");
		
		
		return sql.toString();
	}
	
	public HashMap getHistoryMap(CMMGR023InputVO inputVO){
		HashMap map = new HashMap();
		
		map.put("searchDate", dateFormat2.format(inputVO.getSearchDate()));
		if(StringUtils.isNotBlank(inputVO.getSCHDID())){
			map.put("SCHEDULEID", inputVO.getSCHDID());
		}
		if(StringUtils.isNotBlank(inputVO.getJOBID())){
			map.put("JOBID", inputVO.getJOBID());
		}
		return map;
		
	}

	/** 取得 {@link DataAccessManager} 物件 */
	private void getDam() {
		dam = this.getDataAccessManager();
	}

	/** 取得 {@link QueryConditionIF} 物件 */
	private void getCondition() throws DAOException, JBranchException {
		condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
	}

}
