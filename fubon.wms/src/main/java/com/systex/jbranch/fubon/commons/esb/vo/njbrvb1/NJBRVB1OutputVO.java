package com.systex.jbranch.fubon.commons.esb.vo.njbrvb1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * Created by SebastianWu on 2016/9/29.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NJBRVB1OutputVO {
    @XmlElement
	private String SPRefId; //傳送序號
    @XmlElement
	private String CustId; //身份証ID
    @XmlElement
	private String Occur; //筆數
    @XmlElement(name = "TxRepeat")
    private List<NJBRVB1OutputVODetials> details;

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

    public String getOccur() {
        return Occur;
    }

    public void setOccur(String occur) {
        Occur = occur;
    }

    public List<NJBRVB1OutputVODetials> getDetails() {
        return details;
    }

    public void setDetails(List<NJBRVB1OutputVODetials> details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "NJBRVB1OutputVO{" +
                "SPRefId='" + SPRefId + '\'' +
                ", CustId='" + CustId + '\'' +
                ", Occur='" + Occur + '\'' +
                ", details=" + details +
                '}';
    }
}
