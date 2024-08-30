package com.systex.jbranch.app.server.fps.oth001;

// For 平測模式的 InputVO
public class OTH001InputVO {
    private String txn;
    private String method;
    private String id;
    private String aocode;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getAocode() {
        return aocode;
    }

    public void setAocode(String aocode) {
        this.aocode = aocode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTxn() {
        return txn;
    }

    public void setTxn(String txn) {
        this.txn = txn;
    }

    @Override
    public String toString() {
        return "OTH001InputVO{" +
                "txn='" + txn + '\'' +
                ", method='" + method + '\'' +
                ", id='" + id + '\'' +
                ", aocode='" + aocode + '\'' +
                '}';
    }
}
