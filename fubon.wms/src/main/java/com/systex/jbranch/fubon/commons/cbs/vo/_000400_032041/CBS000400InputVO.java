package com.systex.jbranch.fubon.commons.cbs.vo._000400_032041;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class CBS000400InputVO {
    private String accntNumber1; // 帳號
    private String enquiryoptn1 = "7";
    private String SubAccountInfo; // 幣別 or 存單編號

    public String getAccntNumber1() {
        return accntNumber1;
    }

    public void setAccntNumber1(String accntNumber1) {
        this.accntNumber1 = accntNumber1;
    }

    public String getEnquiryoptn1() {
        return enquiryoptn1;
    }

    public void setEnquiryoptn1(String enquiryoptn1) {
        this.enquiryoptn1 = enquiryoptn1;
    }

    public String getSubAccountInfo() {
        return SubAccountInfo;
    }

    public void setSubAccountInfo(String subAccountInfo) {
        SubAccountInfo = subAccountInfo;
    }
}
