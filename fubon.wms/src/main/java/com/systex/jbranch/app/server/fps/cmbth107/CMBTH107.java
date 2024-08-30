package com.systex.jbranch.app.server.fps.cmbth107;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.systex.jbranch.platform.common.dataManager.Branch;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSAPSERVERSTATUSVO;
import com.systex.jbranch.platform.common.scheduler.AuditLogUtil;
import com.systex.jbranch.platform.common.scheduler.SchedulerHelper;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.sessionmanager.APServerManager;
import com.systex.jbranch.platform.server.sessionmanager.WSManagerIF;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Repository("cmbth107")
@Scope("prototype")
public class CMBTH107 extends FubonWmsBizLogic {
	private String apSvrName;
    private long apServerDefaultTimeAlive;
    private String datasourceName;
    public static final String CMBTH107_LOG = "SESSIONMANAGER_LOG";

    /** job key for ap server timeout */
    private static final String SERVER_TIMEOUT = "serverTimeout";

    /** job key for workstation timeout */
    private static final String WORKSTATION_TIMEOUT = "workStationTimeout";
    
    /** default workstation timeout - 40 * 60 seconds */
    private static final int DEFAULT_WORKSTATION_TIMEOUT = 40 * 60;
    
    /**寫LOG*/
    private Logger logger = LoggerFactory.getLogger(CMBTH107.class);

    public CMBTH107() throws JBranchException {
        APServerManager apServerManager = (APServerManager) PlatformContext.getBean("apServerManager");
        apSvrName = apServerManager.getApSvrName();
        apServerDefaultTimeAlive = apServerManager.getApServerTimeAlive();       
        datasourceName = apServerManager.getDatasourceName();
    }

	public void updateAPServerTimestamp() throws JBranchException{
		this.updateAPServerTimestamp(null, null);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateAPServerTimestamp(Object body, IPrimitiveMap<?> header) throws JBranchException {
        String status = "ON";
        //DataAccessManager dam = this.getDataAccessManager();
        DataAccessManager dam = new DataAccessManager(datasourceName);
        
        TBSYSAPSERVERSTATUSVO apserverStatus = (TBSYSAPSERVERSTATUSVO) dam.findByPKey(TBSYSAPSERVERSTATUSVO.TABLE_UID, apSvrName);
        
        if (apserverStatus != null) {
            apserverStatus.setAPSVRNAME(apSvrName);
            apserverStatus.setSTATUS(status);
            dam.update(apserverStatus);
            logger.debug("APServer[ " + apserverStatus.getAPSVRNAME() + "] 更新存活時間 ");
            //LogUtil.writeLog(CMBTH107_LOG, LogUtil.LEVEL_DEBUG, "APServer[ " + apserverStatus.getAPSVRNAME() + "] 更新存活時間 ");

        }
        else {
            apserverStatus = new TBSYSAPSERVERSTATUSVO();
            apserverStatus.setAPSVRNAME(apSvrName);
            apserverStatus.setSTATUS(status);
            dam.create(apserverStatus);
            logger.debug("APServer [" + apserverStatus.getAPSVRNAME() + "] 新增存活時間 ");
            //LogUtil.writeLog(CMBTH107_LOG, LogUtil.LEVEL_DEBUG, "APServer [" + apserverStatus.getAPSVRNAME() + "] 新增存活時間 ");
        }
    }

    public void checkAPServerAlive(Object body, IPrimitiveMap<?> header) throws Exception {
        //取得傳入參數
        Map inputMap = (Map) body;
        
        //取得timeout時間，如果不存在，則使用預設值(sessionmanager.bean.xml的apServerTimeAlive)
        Map jobParameter = (Map) inputMap.get(SchedulerHelper.JOB_PARAMETER_KEY);
        long ServerTimeout = NumberUtils.toLong((String) jobParameter.get(SERVER_TIMEOUT), apServerDefaultTimeAlive);
    	
        List<TBSYSAPSERVERSTATUSVO> apserversStatus = getAPServerStatusList();
     
        if (apserversStatus != null) {
            for (TBSYSAPSERVERSTATUSVO apserverVo : apserversStatus) {
                long lastTime = apserverVo.getLastupdate().getTime();
                long now = System.currentTimeMillis();
                long diff = (now - lastTime) / 1000;
                if ((ServerTimeout) < diff) {
                    updateAPServerAndWorkStationStatus(apserverVo, "TIMEOUT",ServerTimeout);
                }
                else {
                	logger.debug("APServer[" + apserverVo.getAPSVRNAME() + "]未超過回報存活時間");
                	//LogUtil.writeLog(CMBTH107_LOG, LogUtil.LEVEL_DEBUG, "APServer[" + apserverVo.getAPSVRNAME() + "]未超過回報存活時間");
                }
            }
        }
 
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<TBSYSAPSERVERSTATUSVO> getAPServerStatusList() throws JBranchException {
        //DataAccessManager dam = new DataAccessManager();
        DataAccessManager dam = new DataAccessManager(datasourceName);
        return (List<TBSYSAPSERVERSTATUSVO>) dam.findAll(TBSYSAPSERVERSTATUSVO.TABLE_UID);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateAPServerAndWorkStationStatus(TBSYSAPSERVERSTATUSVO apvo, String status,Long ServerTimeout) throws Exception {
        //DataAccessManager dam = new DataAccessManager();
        DataAccessManager dam = new DataAccessManager(datasourceName);
       
        String apSql = " UPDATE TBSYSAPSERVERSTATUS SET STATUS = ?,LASTUPDATE=? WHERE APSvrName = ?";
        QueryConditionIF condition = dam.getQueryCondition();

        condition.setQueryString(apSql);
        condition.setString(0, status);
        condition.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
        condition.setString(2, apvo.getAPSVRNAME());
        int apResult = dam.exeUpdate(condition);
        logger.debug("APServer[" + apvo.getAPSVRNAME() + "]已超過回報時間" + ServerTimeout + " 秒，將其狀態改為" + status);
        //LogUtil.writeLog(CMBTH107_LOG, LogUtil.LEVEL_INFO, "APServer[" + apvo.getAPSVRNAME() + "]已超過回報時間" + ServerTimeout + " 秒，將其狀態改為" + status);
        
        updateWorkStationStatus(apvo.getAPSVRNAME(),"SERVEROUT");	 //Server關機登出  
    }

    public void updateWorkStationStatus(String apSvrName,String status) throws Exception {
	    DataAccessManager dam = new DataAccessManager(datasourceName);
	    QueryConditionIF condition = dam.getQueryCondition();
	  
	    condition = dam.getQueryCondition();
        //String wsSql = " DELETE FROM TBSYSWSONLINESTATUS WHERE APSvrName = ?";
        String wsSql = " UPDATE TBSYSWSONLINESTATUS SET STATUS = ?,LASTUPDATE=? WHERE APSvrName = ? AND STATUS='ON'";        
        condition.setQueryString(wsSql);
        condition.setString(0, status);    
        condition.setTimestamp(1, new Timestamp(System.currentTimeMillis()));        
        condition.setString(2, apSvrName);
        int wsResult = dam.exeUpdate(condition);
        logger.debug("修改APServer[" + apSvrName + "]上 共" + wsResult + "台工作站,將其狀態改為" + status);
        //LogUtil.writeLog(CMBTH107_LOG, LogUtil.LEVEL_INFO, "修改APServer[" + apSvrName + "]上 共" + wsResult + "台工作站,將其狀態改為" + status);
    }
    /*
     * 修改連線到本台ap server的工作站狀態
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateWorkStationStatus(String status) throws Exception {
    	updateWorkStationStatus(apSvrName,status);
    }
    public void maintainAPServerStatus(Object body, IPrimitiveMap<?> header) throws Exception {
        checkAPServerAlive(body, header);
        updateAPServerTimestamp(body, header);

    }

    public void housekeep(Object body, IPrimitiveMap<?> header) throws Exception {
        //取得傳入參數
        Map inputMap = (Map) body;
        AuditLogUtil audit = (AuditLogUtil) inputMap.get(SchedulerHelper.AUDIT_PARAMETER_KEY);	
        
        //取得timeout時間，如果不存在，則使用預設值
        Map jobParameter = (Map) inputMap.get(SchedulerHelper.JOB_PARAMETER_KEY);
        int workStationTimeout = NumberUtils.toInt((String) jobParameter.get(WORKSTATION_TIMEOUT), DEFAULT_WORKSTATION_TIMEOUT);

        List<String> timeOutList = new LinkedList<String>();
        List<String> unTimeOutList = new LinkedList<String>();
        List<String> timeOutList_audit = new LinkedList<String>();

        long startTime = System.currentTimeMillis();
        logger.debug(" start  workstationkeeper......  ");
        //LogUtil.writeLog(CMBTH107_LOG, LogUtil.LEVEL_DEBUG, " start  workstationkeeper......  ");

        Map<String, Branch> branchs = DataManager.getBranch();
        WSManagerIF wsManager = (WSManagerIF) PlatformContext.getBean("wsManager");

        Set<Entry<String,Branch>> branchEntries = branchs.entrySet();
        for (Entry<String,Branch> entry : branchEntries) {
            Branch branch = entry.getValue();
            Map<String, WorkStation> workstations = branch.getWorkStations();
            Set<Entry<String, WorkStation>> workstationEntries = workstations.entrySet();
            for (Entry<String, WorkStation> workStationEntry : workstationEntries) {
                WorkStation workstation = workStationEntry.getValue();
                long now = System.currentTimeMillis();
                long wsTouchedTime = workstation.getTouchedTime();
                                
                long diffSeconds = (now - wsTouchedTime) / 1000;
                if (diffSeconds > workStationTimeout) {
                    //逾時
                    //刪除workstation
                    branch.deleteWorkStation(workStationEntry.getKey());
                    try {
                        //將該workstation設定為timeout
                        wsManager.whenTimeout(workstation.getWsID(), workstation.getUser().getUserID(), apSvrName, workstation.getBrchID());
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    timeOutList.add("於" + DateFormatUtils.format(now, "yyyy/MM/dd HH:mm:ss") + "刪除 " + branch.getName() + "[" + branch.getBrchID() + "]的工作站 IP:" + workstation.getWsIP() + "[" + workstation.getWsID() + "], 逾時:" + (diffSeconds - workStationTimeout) + "秒");
                    if(!"0000".equals(branch.getBrchID())){
                        timeOutList_audit.add("於" + DateFormatUtils.format(now, "yyyy/MM/dd HH:mm:ss") + "刪除 " + branch.getName() + "[" + branch.getBrchID() + "]的工作站 IP:" + workstation.getWsIP() + "[" + workstation.getWsID() + "], 逾時:" + (diffSeconds - workStationTimeout) + "秒");                   	
                    }
                }
                else {
                    unTimeOutList.add("於" + DateFormatUtils.format(now, "yyyy/MM/dd HH:mm:ss") + "檢查 " + branch.getName() + "[" + branch.getBrchID() + "]的工作站 IP:" + workstation.getWsIP() + "[" + workstation.getWsID() + "]未 timeout");
                }
            }
        }
        long endTime = System.currentTimeMillis();
        if (!timeOutList.isEmpty()) {
            for (String aTimeOutList : timeOutList) {
            	logger.debug(aTimeOutList);
            	//LogUtil.writeLog(CMBTH107_LOG, LogUtil.LEVEL_INFO, aTimeOutList);
             }
        }
        if (!unTimeOutList.isEmpty()) {
            for (String anUnTimeOutList : unTimeOutList) {
            	logger.debug(anUnTimeOutList);
            	//LogUtil.writeLog(CMBTH107_LOG, LogUtil.LEVEL_DEBUG, anUnTimeOutList);
            }
        }
        if (!timeOutList_audit.isEmpty()) {
            for (String aTimeOutList_audit : timeOutList_audit) {
              audit.audit(aTimeOutList_audit);
            }
        }
        logger.debug("housekeeping spent " + (endTime - startTime) + " milliseconds ");
}
}
