package com.systex.jbranch.platform.common.platformdao.table;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

/**
 * @author Alex Lin
 * @version 2011/03/03 1:24 PM
 */
public class TbsysdmworkstationVO implements Serializable {
// ------------------------------ FIELDS ------------------------------

    private String wsID;
    private long version;
    private String signOnBranchID;
    private String wsIP;
    private String developMode;
    private String localMode;
    private String signOnWsID;
    private long touchedTime;
    private String applicationID;
    private Set<TbsysdmsectionVO> sections;
    private TbsysdmbranchVO branch;
    private Set<TbsysdmuserVO> user;

    public Set<TbsysdmuserVO> getUser() {
        return user;
    }

    public void setUser(Set<TbsysdmuserVO> user) {
        this.user = user;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public String getApplicationID() {
        return applicationID;
    }

    public void setApplicationID(String applicationID) {
        this.applicationID = applicationID;
    }

    public TbsysdmbranchVO getBranch() {
        return branch;
    }

    public void setBranch(TbsysdmbranchVO branch) {
        this.branch = branch;
    }

    public String getDevelopMode() {
        return developMode;
    }

    public void setDevelopMode(String developMode) {
        this.developMode = developMode;
    }

    public String getLocalMode() {
        return localMode;
    }

    public void setLocalMode(String localMode) {
        this.localMode = localMode;
    }

    public Set<TbsysdmsectionVO> getSections() {
        return sections;
    }

    public void setSections(Set<TbsysdmsectionVO> sections) {
        this.sections = sections;
    }

    public String getSignOnBranchID() {
        return signOnBranchID;
    }

    public void setSignOnBranchID(String signOnBranchID) {
        this.signOnBranchID = signOnBranchID;
    }

    public String getSignOnWsID() {
        return signOnWsID;
    }

    public void setSignOnWsID(String signOnWsID) {
        this.signOnWsID = signOnWsID;
    }

    public long getTouchedTime() {
        return touchedTime;
    }

    public void setTouchedTime(long touchedTime) {
        this.touchedTime = touchedTime;
    }


    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getWsID() {
        return wsID;
    }

    public void setWsID(String wsID) {
        this.wsID = wsID;
    }

    public String getWsIP() {
        return wsIP;
    }

    public void setWsIP(String wsIP) {
        this.wsIP = wsIP;
    }

// ------------------------ CANONICAL METHODS ------------------------

    @Override
    public String toString() {
        return "com.systex.jbranch.platform.common.platformdao.table.TbsysdmworkstationVO{" +
                "wsID='" + wsID + '\'' +
                ", version=" + version +
                ", signOnBranchID='" + signOnBranchID + '\'' +
                ", wsIP='" + wsIP + '\'' +
                ", developMode='" + developMode + '\'' +
                ", localMode='" + localMode + '\'' +
                ", signOnWsID='" + signOnWsID + '\'' +
                ", touchedTime=" + touchedTime +
                ", applicationID='" + applicationID + '\'' +
                ", sections=" + sections +
                ", branch=" + branch +
                '}';
    }
}
