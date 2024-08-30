package com.systex.jbranch.fubon.commons.cbs.vo._060425_060433;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class CBS060425InputVO {
    private String ReasonCode = "98";
    private String EnquiryType = "0002";
    private String idno;
    private String idtype;
    private String opt = "A";

    public String getReasonCode() {
        return ReasonCode;
    }

    public void setReasonCode(String reasonCode) {
        ReasonCode = reasonCode;
    }

    public String getEnquiryType() {
        return EnquiryType;
    }

    public void setEnquiryType(String enquiryType) {
        EnquiryType = enquiryType;
    }

    public String getIdno() {
        return idno;
    }

    public void setIdno(String idno) {
        this.idno = idno;
    }

    public String getIdtype() {
        return idtype;
    }

    public void setIdtype(String idtype) {
        this.idtype = idtype;
    }

    public String getOpt() {
        return opt;
    }

    public void setOpt(String opt) {
        this.opt = opt;
    }
}
