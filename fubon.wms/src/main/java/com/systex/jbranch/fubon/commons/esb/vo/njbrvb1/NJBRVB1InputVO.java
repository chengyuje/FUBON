package com.systex.jbranch.fubon.commons.esb.vo.njbrvb1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by SebastianWu on 2016/9/29.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NJBRVB1InputVO {
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
        return "NJBRVB1InputVO{" +
                "CustId='" + CustId + '\'' +
                '}';
    }
}
