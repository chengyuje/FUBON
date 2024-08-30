package com.systex.jbranch.fubon.commons.esb.vo.njbrva1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by SebastianWu on 2016/9/29.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NJBRVA1InputVO {
    @XmlElement
    private String CustId; //身份証ID
    @XmlElement
    private String BondNo; //債券代號

    public String getCustId() {
        return CustId;
    }

    public void setCustId(String custId) {
        CustId = custId;
    }

    public String getBondNo() {
        return BondNo;
    }

    public void setBondNo(String bondNo) {
        BondNo = bondNo;
    }

    @Override
    public String toString() {
        return "NJBRVA1InputVO{" +
                "CustId='" + CustId + '\'' +
                ", BondNo='" + BondNo + '\'' +
                '}';
    }
}
