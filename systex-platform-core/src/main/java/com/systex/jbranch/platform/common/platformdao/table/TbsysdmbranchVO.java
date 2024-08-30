package com.systex.jbranch.platform.common.platformdao.table;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * @author Alex Lin
 * @version 2011/03/03 12:03 PM
 */
public class TbsysdmbranchVO implements Serializable {
// ------------------------------ FIELDS ------------------------------

    private long version;
    private String brchID;
    private String name;
    private String opDate;
    private String txnFlag;
    private Set<TbsysdmworkstationVO> workstations;
    private Map<String, Serializable> platformVars;
    private Map<String, Serializable> bizlogicVars;

// --------------------- GETTER / SETTER METHODS ---------------------

    public Map<String, Serializable> getBizlogicVars() {
        return bizlogicVars;
    }

    public void setBizlogicVars(Map<String, Serializable> bizlogicVars) {
        this.bizlogicVars = bizlogicVars;
    }

    public String getBrchID() {
        return brchID;
    }

    public void setBrchID(String brchID) {
        this.brchID = brchID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOpDate() {
        return opDate;
    }

    public void setOpDate(String opDate) {
        this.opDate = opDate;
    }

    public Map<String, Serializable> getPlatformVars() {
        return platformVars;
    }

    public void setPlatformVars(Map<String, Serializable> platformVars) {
        this.platformVars = platformVars;
    }

    public String getTxnFlag() {
        return txnFlag;
    }

    public void setTxnFlag(String txnFlag) {
        this.txnFlag = txnFlag;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public Set<TbsysdmworkstationVO> getWorkstations() {
        return workstations;
    }

    public void setWorkstations(Set<TbsysdmworkstationVO> workstations) {
        this.workstations = workstations;
    }

// ------------------------ CANONICAL METHODS ------------------------

    @Override
    public String toString() {
        return "com.systex.jbranch.platform.common.platformdao.table.TbsysdmbranchVO{" +
                "version=" + version +
                ", brchID='" + brchID + '\'' +
                ", name='" + name + '\'' +
                ", opDate='" + opDate + '\'' +
                ", txnFlag='" + txnFlag + '\'' +
                '}';
    }
}
