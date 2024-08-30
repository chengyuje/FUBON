package com.systex.jbranch.fubon.commons.esb.vo.ajbrvb1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by James on 2017/7/13.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class AJBRVB1InputVO {
    @XmlElement
    private String CustId;  //身份証ID

    public String getCustId() {
        return CustId;
    }

    public void setCustId(String custId) {
        CustId = custId;
    }

    @Override
    public String toString() {
        return "AJBRVB1InputVO{" +
                "CustId='" + CustId + '\'' +
                '}';
    }
}
