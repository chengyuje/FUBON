package com.systex.jbranch.platform.common.platformdao.table;

import java.io.Serializable;

/**
 * @author Alex Lin
 * @version 2011/03/03 12:03 PM
 */
public class TbsysdmsectionVO implements Serializable {
// ------------------------------ FIELDS ------------------------------

    private long version;
    private String sectionID;
    private String luNo;
    private String txnCode;
    private String txnName;
    private TbsysdmworkstationVO workstation;
    private TbsysdmservertransactionVO serverTransaction;
    private TbsysdmclienttransactionVO clientTransaction;

// --------------------- GETTER / SETTER METHODS ---------------------

    public TbsysdmclienttransactionVO getClientTransaction() {
        return clientTransaction;
    }

    public void setClientTransaction(TbsysdmclienttransactionVO clientTransaction) {
        this.clientTransaction = clientTransaction;
    }

    public String getLuNo() {
        return luNo;
    }

    public void setLuNo(String luNo) {
        this.luNo = luNo;
    }

    public String getSectionID() {
        return sectionID;
    }

    public void setSectionID(String sectionID) {
        this.sectionID = sectionID;
    }

    public TbsysdmservertransactionVO getServerTransaction() {
        return serverTransaction;
    }

    public void setServerTransaction(TbsysdmservertransactionVO serverTransaction) {
        this.serverTransaction = serverTransaction;
    }

    public String getTxnCode() {
        return txnCode;
    }

    public void setTxnCode(String txnCode) {
        this.txnCode = txnCode;
    }

    public String getTxnName() {
        return txnName;
    }

    public void setTxnName(String txnName) {
        this.txnName = txnName;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
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
        return "com.systex.jbranch.platform.common.platformdao.table.TbsysdmsectionVO{" +
                "version=" + version +
                ", sectionID='" + sectionID + '\'' +
                ", luNo='" + luNo + '\'' +
                ", txnCode='" + txnCode + '\'' +
                ", txnName='" + txnName + '\'' +
                ", workstation=" + workstation +
                ", serverTransaction=" + serverTransaction +
                ", clientTransaction=" + clientTransaction +
                '}';
    }
}
