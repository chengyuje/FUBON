package com.systex.jbranch.platform.server.sessionmanager;

import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.EnumErrInputType;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.errHandle.SessionException;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSWSONLINESTATUSPK;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSWSONLINESTATUSVO;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@SuppressWarnings("unchecked")
public class DBWSManager implements WSManagerIF {
// ------------------------------ FIELDS ------------------------------

    public static final String SESSIONMANAGER_LOG = "SESSIONMANAGER_LOG";
    private static final String STATUS_OFF = "OFF";
    private static final String STATUS_ON = "ON";
    private static final String STATUS_ON_NOCHK = "ON_NOCHK";
    private static final String STATUS_OUT = "OUT";
    private static final String STATUS_SERVEROUT = "SERVEROUT";
    private static final String STATUS_TIMEOUT = "TIMEOUT";
    private String datasourceName;
    private boolean allowDupLogin;
	private Logger logger = LoggerFactory.getLogger(DBWSManager.class);

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface WSManagerIF ---------------------

    public void whenLogin(String wsId, String tellerId, String apSvrName, String brchId) throws JBranchException {
        whenLogin(wsId, tellerId, apSvrName, brchId, this.allowDupLogin);
    }

    public void whenLogin(String wsId, String tellerId, String apSvrName, String brchId, Boolean allowDupLogin_bln) throws JBranchException {
        if (logger.isDebugEnabled()) {
            logger.debug("wsid[" + wsId + "] tellerid[" + tellerId + "] apSvrName[" + apSvrName + "] brchId[" + brchId + "] allowDupLogin[" + allowDupLogin_bln.toString() + "]");
        }

        DataAccessManager dam = getDataAccessManager();
        try {
            TBSYSWSONLINESTATUSPK pk = new TBSYSWSONLINESTATUSPK(tellerId, wsId, apSvrName);
            TBSYSWSONLINESTATUSVO wsStatus = (TBSYSWSONLINESTATUSVO) dam.findByPKey(TBSYSWSONLINESTATUSVO.TABLE_UID, pk);
            if (wsStatus != null) {
                wsStatus.setBRCHID(brchId);
                if (allowDupLogin_bln) {
                    wsStatus.setSTATUS(STATUS_ON_NOCHK);
                }
                else {
                    wsStatus.setSTATUS(STATUS_ON);
                }
                dam.update(wsStatus);
            }
            else {
                wsStatus = new TBSYSWSONLINESTATUSVO();
                wsStatus.setcomp_id(pk);
                wsStatus.setBRCHID(brchId);
                if (allowDupLogin_bln) {
                    wsStatus.setSTATUS(STATUS_ON_NOCHK);
                }
                else {
                    wsStatus.setSTATUS(STATUS_ON);
                }
                dam.create(wsStatus);
            }
        }
        catch (DAOException e) {
            throw e;
        }
        catch (JBranchException e) {
            throw e;
        }

        if (!allowDupLogin_bln) {
            //取出相同tellerId及不同wsId的資料,且狀態為上線
            List<TBSYSWSONLINESTATUSVO> wsList = dam.getHibernateCriteria(TBSYSWSONLINESTATUSVO.TABLE_UID)
                    .add(Restrictions.eq("comp_id.TELLERID", tellerId))
                    .add(Restrictions.eq("STATUS", STATUS_ON))
                    .add(Restrictions.ne("comp_id.WSID", wsId))
                    .list();

            //更新狀態為強迫登出
            for (TBSYSWSONLINESTATUSVO vo : wsList) {
                if (logger.isDebugEnabled()) {
                    logger.debug("更新狀態為OUT, tellerId[" + vo.getcomp_id().getTELLERID() +
                            "] wsid[" + vo.getcomp_id().getWSID() +
                            "] apSvrName[" + vo.getcomp_id().getAPSVRNAME() + "]");
                }

                vo.setSTATUS(STATUS_OUT);
                dam.update(vo);
            }
        }
    }

    public void whenTransaction(String wsId, String tellerId, String apSvrName, String brchId) throws JBranchException {
        if (logger.isDebugEnabled()) {
            logger.debug("wsid[" + wsId + "] tellerid[" + tellerId + "] apSvrName[" + apSvrName + "] brchId[" + brchId + "] in");
        }

        //取得本session狀態
        DataAccessManager dam = getDataAccessManager();
        TBSYSWSONLINESTATUSPK pk = new TBSYSWSONLINESTATUSPK(tellerId, wsId, apSvrName);
        TBSYSWSONLINESTATUSVO wsStatus = (TBSYSWSONLINESTATUSVO) dam.findByPKey(TBSYSWSONLINESTATUSVO.TABLE_UID, pk);
        if (wsStatus == null) {
            //2010-06-08 tlron前執行的動作,如getXMLList..等,此時並無資料
            //throw new SessionException(EnumErrInputType.MSG, "pf_sessionmanager_error_003"); //系統異常

            //取得本wsid狀態,若無資料(tlron前執行的動作,如getXMLList..等),skip
            //              若有資料,check最近一筆異動的狀態(ex.SSO tlrid missing)
            List<TBSYSWSONLINESTATUSVO> wsList = dam.getHibernateCriteria(TBSYSWSONLINESTATUSVO.TABLE_UID)
                    .add(Restrictions.eq("comp_id.WSID", wsId))
                    .addOrder(Order.desc("lastupdate"))
                    .list();
            if (wsList.size() > 0) {
                verifyStatus(wsList.get(0).getSTATUS());
            }
        }
        else {
            verifyStatus(wsStatus.getSTATUS());
        }
        if (logger.isDebugEnabled()) {
            logger.debug("wsid[" + wsId + "] tellerid[" + tellerId + "] apSvrName[" + apSvrName + "] brchId[" + brchId + "] out");
        }
    }

    public void whenLogout(String wsId, String tellerId, String apSvrName, String brchId) throws JBranchException {
        if (logger.isDebugEnabled()) {
            logger.debug("wsid[" + wsId + "] tellerid[" + tellerId + "] apSvrName[" + apSvrName + "] brchId[" + brchId + "]");
        }

        DataAccessManager dam = getDataAccessManager();
        if (tellerId != null) {
            TBSYSWSONLINESTATUSPK pk = new TBSYSWSONLINESTATUSPK(tellerId, wsId, apSvrName);
            TBSYSWSONLINESTATUSVO wsStatus = (TBSYSWSONLINESTATUSVO) dam.findByPKey(TBSYSWSONLINESTATUSVO.TABLE_UID, pk);
            if ((wsStatus != null)) {
                wsStatus.setSTATUS(STATUS_OFF);
                dam.update(wsStatus);
            }
            else {
                if (logger.isDebugEnabled()) {
                    logger.debug("登出時找不到在TBSYSWSONLINESTATUS的記錄");
                }
            }
        }
    }

    public void whenTimeout(String wsId, String tellerId, String apSvrName, String brchId) throws JBranchException {
        if (logger.isDebugEnabled()) {
            logger.debug("wsid[" + wsId + "] tellerid[" + tellerId + "] apSvrName[" + apSvrName + "] brchId[" + brchId + "]");
        }

        DataAccessManager dam = getDataAccessManager();
        TBSYSWSONLINESTATUSVO wsStatus = new TBSYSWSONLINESTATUSVO();
        TBSYSWSONLINESTATUSPK pk = new TBSYSWSONLINESTATUSPK(tellerId, wsId, apSvrName);
        try {
            wsStatus = (TBSYSWSONLINESTATUSVO) dam.findByPKey(TBSYSWSONLINESTATUSVO.TABLE_UID, pk, true);
        }
        catch (Exception e1) {
        }
        if ((wsStatus != null) && (tellerId != null)) {
            wsStatus.setSTATUS(STATUS_TIMEOUT);
            dam.update(wsStatus);
        }
        else {
            if (logger.isDebugEnabled()) {
                logger.debug("登出時找不到在TBSYSWSONLINESTATUS的記錄");
            }
        }
    }

    protected DataAccessManager getDataAccessManager() throws JBranchException {
        return new DataAccessManager(datasourceName);
    }

    private void verifyStatus(String status) throws JBranchException {
        if (StringUtils.equals(status, STATUS_OUT)) {
            throw new SessionException(EnumErrInputType.MSG, "pf_sessionmanager_error_002"); //重複登入
        }
        else if (StringUtils.equals(status, STATUS_TIMEOUT)) {
            throw new SessionException(EnumErrInputType.MSG, "pf_sessionmanager_error_001"); //工作站逾時
        }
        else if (StringUtils.equals(status, STATUS_SERVEROUT)) {
            throw new SessionException(EnumErrInputType.MSG, "pf_sessionmanager_error_004"); //Server關機登出
            //如tlroff後會getXMLList
            //}else if (StringUtils.equals(status, STATUS_OFF)){
            //    throw new SessionException(EnumErrInputType.MSG, "pf_sessionmanager_error_005"); //工作站已登出
        }
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public boolean getAllowDupLogin() {
        return this.allowDupLogin;
    }

    public void setAllowDupLogin(boolean allowDupLogin) {
        this.allowDupLogin = allowDupLogin;
    }

    public void setDatasourceName(String datasourceName) {
        this.datasourceName = datasourceName;
    }
}
