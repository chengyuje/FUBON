package com.systex.jbranch.fubon.commons.esb.vo.fp052650;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by SebastianWu on 2016/10/18.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType
public class FP052650InputVO {
    @XmlElement
	private String FUNC;      //功能 
    @XmlElement
	private String SEP01;     //FKey
    @XmlElement
	private String CUST_ID;   //客戶ID
    @XmlElement
	private String SEP02;     //FKey
    @XmlElement
	private String DATE_S;    //查詢起日
    @XmlElement
	private String SEP03;     //FKey
    @XmlElement
	private String DATE_E;    //查詢迄日
    @XmlElement
	private String SEP04;     //FKey
    @XmlElement
	private String ENDKEY;    //FKey

    public String getFUNC() {
        return FUNC;
    }

    public void setFUNC(String FUNC) {
        this.FUNC = FUNC;
    }

    public String getSEP01() {
        return SEP01;
    }

    public void setSEP01(String SEP01) {
        this.SEP01 = SEP01;
    }

    public String getCUST_ID() {
        return CUST_ID;
    }

    public void setCUST_ID(String CUST_ID) {
        this.CUST_ID = CUST_ID;
    }

    public String getSEP02() {
        return SEP02;
    }

    public void setSEP02(String SEP02) {
        this.SEP02 = SEP02;
    }

    public String getDATE_S() {
        return DATE_S;
    }

    public void setDATE_S(String DATE_S) {
        this.DATE_S = DATE_S;
    }

    public String getSEP03() {
        return SEP03;
    }

    public void setSEP03(String SEP03) {
        this.SEP03 = SEP03;
    }

    public String getDATE_E() {
        return DATE_E;
    }

    public void setDATE_E(String DATE_E) {
        this.DATE_E = DATE_E;
    }

    public String getSEP04() {
        return SEP04;
    }

    public void setSEP04(String SEP04) {
        this.SEP04 = SEP04;
    }

    public String getENDKEY() {
        return ENDKEY;
    }

    public void setENDKEY(String ENDKEY) {
        this.ENDKEY = ENDKEY;
    }
}
