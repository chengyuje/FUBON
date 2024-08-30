package com.systex.jbranch.fubon.commons.esb.vo.fp052650;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * Created by SebastianWu on 2016/10/18.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType
public class FP052650OutputVO {
    @XmlElement
	private String CUST_ID;     //客戶ID 
    @XmlElement
	private String FLAG1;       //金控法44條 
    @XmlElement
	private String FLAG2;       //金控法45條 
    @XmlElement
	private String FLAG3;       //銀行法 
    @XmlElement
	private String CUST_NAME;   //戶名
    @XmlElement(name = "TxRepeat")
	private List<FP052650OutputVODetails> details;

    public String getCUST_ID() {
        return CUST_ID;
    }

    public void setCUST_ID(String CUST_ID) {
        this.CUST_ID = CUST_ID;
    }

    public String getFLAG1() {
        return FLAG1;
    }

    public void setFLAG1(String FLAG1) {
        this.FLAG1 = FLAG1;
    }

    public String getFLAG2() {
        return FLAG2;
    }

    public void setFLAG2(String FLAG2) {
        this.FLAG2 = FLAG2;
    }

    public String getFLAG3() {
        return FLAG3;
    }

    public void setFLAG3(String FLAG3) {
        this.FLAG3 = FLAG3;
    }

    public String getCUST_NAME() {
        return CUST_NAME;
    }

    public void setCUST_NAME(String CUST_NAME) {
        this.CUST_NAME = CUST_NAME;
    }

    public List<FP052650OutputVODetails> getDetails() {
        return details;
    }

    public void setDetails(List<FP052650OutputVODetails> details) {
        this.details = details;
    }
}
