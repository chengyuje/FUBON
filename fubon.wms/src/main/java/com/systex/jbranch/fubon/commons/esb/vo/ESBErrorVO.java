package com.systex.jbranch.fubon.commons.esb.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by SebastianWu on 2016/10/5.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class ESBErrorVO {
    @XmlElement
    private String EMSGID; //錯誤代碼
    @XmlElement
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
