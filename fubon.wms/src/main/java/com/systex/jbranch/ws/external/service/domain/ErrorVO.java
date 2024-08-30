package com.systex.jbranch.ws.external.service.domain;

public class ErrorVO {
    private String EMSGID; //錯誤代碼
    private String EMSGTXT; //錯誤訊息

    public String getEMSGID() {
        return EMSGID;
    }

    public void setEMSGID(String EMSGID) {
        this.EMSGID = EMSGID;
    }

    public String getEMSGTXT() {
        return EMSGTXT;
    }

    public void setEMSGTXT(String EMSGTXT) {
        this.EMSGTXT = EMSGTXT;
    }

    @Override
    public String toString() {
        return "ESBErrorVO{" +
                "EMSGID='" + EMSGID + '\'' +
                ", EMSGTXT='" + EMSGTXT + '\'' +
                '}';
    }
}
