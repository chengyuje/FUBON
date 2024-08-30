package com.systex.jbranch.fubon.commons.esb.vo.ajbrva3;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by Carley on 2024/07/26
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class AJBRVA3InputVO {
    @XmlElement
	private String CustId; // 身份証ID
    @XmlElement
	private String BondNo; // 債券代號

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
        return "AJBRVA3InputVO{" +
                "CustId='" + CustId + '\'' +
                ", BondNo='" + BondNo + '\'' +
                '}';
    }
}
