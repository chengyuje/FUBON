package com.systex.jbranch.fubon.commons.esb.vo.ajbrva3;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by Carley on 2024/07/26
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class AJBRVA3OutputVO {
    @XmlElement
	private String SPRefId;	// 傳送序號
    @XmlElement
	private String CustId;	// 身份証ID
    @XmlElement
	private String BondNo;	// 債券代號
    @XmlElement
	private String Occur;	// 筆數
    @XmlElement(name = "TxRepeat")
    private List<AJBRVA3OutputVODetails> details;

    public String getSPRefId() {
        return SPRefId;
    }

    public void setSPRefId(String SPRefId) {
        this.SPRefId = SPRefId;
    }

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

    public String getOccur() {
        return Occur;
    }

    public void setOccur(String occur) {
        Occur = occur;
    }

    public List<AJBRVA3OutputVODetails> getDetails() {
        return details;
    }

    public void setDetails(List<AJBRVA3OutputVODetails> details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "AJBRVA3OutputVO{" +
                "SPRefId='" + SPRefId + '\'' +
                ", CustId='" + CustId + '\'' +
                ", BondNo='" + BondNo + '\'' +
                ", Occur='" + Occur + '\'' +
                '}';
    }
}
