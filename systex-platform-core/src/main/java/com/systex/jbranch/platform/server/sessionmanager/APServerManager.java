package com.systex.jbranch.platform.server.sessionmanager;

import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class APServerManager {
// ------------------------------ FIELDS ------------------------------

    public static final String SESSIONMANAGER_LOG = "SESSIONMANAGER_LOG";
    private static final String AP_SERVER_STATUS_OFF = "OFF";
    private long apServerTimeAlive;
    private String apSvrName;
    private String datasourceName;
	private Logger logger = LoggerFactory.getLogger(APServerManager.class);

// -------------------------- OTHER METHODS --------------------------

    public void setSystem(com.systex.jbranch.platform.common.dataManager.System system) {
        this.apSvrName = (String) system.getInfo().get("apServerName");
    }

    /**
     * @param
     */
    @Deprecated
    public void unregister(String apSvrName) throws JBranchException {
        updateAPServerAndWorkStationStatus(apSvrName, AP_SERVER_STATUS_OFF);
    }

    /**
     * 已移至排程JOB CMBTH107
     *
     * @param
     */
    @Deprecated
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateAPServerAndWorkStationStatus(String apServerName, String status) throws JBranchException {
        String logMsg;

        //update status of apserver
        updateApServerStatus(apServerName, status);

        if (logger.isDebugEnabled()) {
            logger.debug("APServer[{}]已超過回報時間 {} 分鐘/關機，將其狀態改為OFF", apServerName, apServerTimeAlive);
        }

        //delete ws status by apserver name
        int wsResult = deleteWSOnlineStatusByApServerName(apServerName);

        if (logger.isDebugEnabled()) {
            logger.debug("並刪除APServer[{}]上 共{}台工作站!", apServerName, wsResult);
        }
    }

    /**
     * @param
     */
    @Deprecated
    private int updateApServerStatus(String apServerName, String status) throws JBranchException {
        //DataAccessManager dam = new DataAccessManager();
        DataAccessManager dam = getDataAccessManager();
        String apSql = "UPDATE TBSYSAPSERVERSTATUS SET STATUS = ? WHERE APSvrName = ?";
        QueryConditionIF condition = dam.getQueryCondition();

        condition.setQueryString(apSql);
        condition.setString(0, status);
        condition.setString(1, apServerName);

        return dam.executeUpdate(condition);
    }

    protected DataAccessManager getDataAccessManager() throws JBranchException {
        return new DataAccessManager(datasourceName);
    }

    /**
     * @param
     */
    @Deprecated
    private int deleteWSOnlineStatusByApServerName(String apServerName) throws JBranchException {
        //DataAccessManager dam = new DataAccessManager();
        DataAccessManager dam = getDataAccessManager();
        QueryConditionIF condition = dam.getQueryCondition();
        String wsSql = "DELETE FROM TBSYSWSONLINESTATUS WHERE APSvrName = ?";
        condition.setQueryString(wsSql);
        condition.setString(0, apServerName);

        return dam.executeUpdate(condition);
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public long getApServerTimeAlive() {
        return this.apServerTimeAlive;
    }

    public void setApServerTimeAlive(long apServerTimeAlive) {
        this.apServerTimeAlive = apServerTimeAlive;
    }

    public String getApSvrName() {
        return apSvrName;
    }

    public String getDatasourceName() {
        return this.datasourceName;
    }

    public void setDatasourceName(String datasourceName) {
        this.datasourceName = datasourceName;
    }
}
