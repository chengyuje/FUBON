package com.systex.jbranch.platform.common.platformdao.table;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Alex Lin
 * @version 2011/03/03 12:03 PM
 */
public class TbsysdmuserVO implements Serializable {
// ------------------------------ FIELDS ------------------------------

    private String userID;
    private String userName;
    private String userAuth;
    private String status;
    private String errLevel;
    private String level;
    private String proxiedUserID;
    private String proxiedUserAuth;
    private Map<String, Serializable> platformVars;
    private Map<String, Serializable> bizlogicVars;
    private long uid;
    private TbsysdmworkstationVO workstation;
    private String wsID;

    public String getWsID() {
        return wsID;
    }

    public void setWsID(String wsID) {
        this.wsID = wsID;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public Map<String, Serializable> getBizlogicVars() {
        return bizlogicVars;
    }

    public void setBizlogicVars(Map<String, Serializable> bizlogicVars) {
        this.bizlogicVars = bizlogicVars;
    }

    public String getErrLevel() {
        return errLevel;
    }

    public void setErrLevel(String errLevel) {
        this.errLevel = errLevel;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Map<String, Serializable> getPlatformVars() {
        return platformVars;
    }

    public void setPlatformVars(Map<String, Serializable> platformVars) {
        this.platformVars = platformVars;
    }

    public String getProxiedUserAuth() {
        return proxiedUserAuth;
    }

    public void setProxiedUserAuth(String proxiedUserAuth) {
        this.proxiedUserAuth = proxiedUserAuth;
    }

    public String getProxiedUserID() {
        return proxiedUserID;
    }

    public void setProxiedUserID(String proxiedUserID) {
        this.proxiedUserID = proxiedUserID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getUserAuth() {
        return userAuth;
    }

    public void setUserAuth(String userAuth) {
        this.userAuth = userAuth;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public TbsysdmworkstationVO getWorkstation() {
        return workstation;
    }

    public void setWorkstation(TbsysdmworkstationVO workstation) {
        this.workstation = workstation;
    }

// ------------------------ CANONICAL METHODS ------------------------

    @Override
    public String toString() {
        return "com.systex.jbranch.platform.common.platformdao.table.TbsysdmuserVO{" +
                "userID='" + userID + '\'' +
                ", userName='" + userName + '\'' +
                ", userAuth='" + userAuth + '\'' +
                ", status='" + status + '\'' +
                ", errLevel='" + errLevel + '\'' +
                ", level='" + level + '\'' +
                ", proxiedUserID='" + proxiedUserID + '\'' +
                ", proxiedUserAuth='" + proxiedUserAuth + '\'' +
                ", platformVars=" + platformVars +
                ", bizlogicVars=" + bizlogicVars +
                ", uid=" + uid +
                ", workstation=" + workstation +
                '}';
    }
}
