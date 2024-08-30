package com.systex.jbranch.fubon.commons.cbs.vo._067164_067165;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class CBS067164InputVO {
    private String IDNo;
    private String IDType;
    private String Function = "E";
    private String AgreementType = "01";
    private String AgreementCode = "00001";

    public String getIDNo() {
        return IDNo;
    }

    public void setIDNo(String IDNo) {
        this.IDNo = IDNo;
    }

    public String getIDType() {
        return IDType;
    }

    public void setIDType(String IDType) {
        this.IDType = IDType;
    }

    public String getFunction() {
        return Function;
    }

    public void setFunction(String function) {
        Function = function;
    }

    public String getAgreementType() {
        return AgreementType;
    }

    public void setAgreementType(String agreementType) {
        AgreementType = agreementType;
    }

    public String getAgreementCode() {
        return AgreementCode;
    }

    public void setAgreementCode(String agreementCode) {
        AgreementCode = agreementCode;
    }
}
