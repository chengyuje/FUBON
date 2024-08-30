package com.systex.jbranch.fubon.commons.esb.vo.fc81;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by SebastianWu on 2016/10/18.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType
public class FC81InputVO {
    @XmlElement
	private String TRAN_ID;
    @XmlElement
	private String TX_TYPE;
    @XmlElement
	private String TX_DATE;
    @XmlElement
	private String TX_TIME;
    @XmlElement
	private String CUST_NO;
    @XmlElement
	private String TX_DESC;
    @XmlElement
	private String TX_RSN;
    @XmlElement
	private String MTN_BRH;
    @XmlElement
	private String MTN_EMP;
    @XmlElement
	private String RETURN_COD;
    @XmlElement
	private String RETURN_MSG;
  
    @XmlElement
	private String ID_TYPE;
    
    

    public String getID_TYPE() {
		return ID_TYPE;
	}

	public void setID_TYPE(String iD_TYPE) {
		ID_TYPE = iD_TYPE;
	}

	public String getTRAN_ID() {
        return TRAN_ID;
    }

    public void setTRAN_ID(String TRAN_ID) {
        this.TRAN_ID = TRAN_ID;
    }

    public String getTX_TYPE() {
        return TX_TYPE;
    }

    public void setTX_TYPE(String TX_TYPE) {
        this.TX_TYPE = TX_TYPE;
    }

    public String getTX_DATE() {
        return TX_DATE;
    }

    public void setTX_DATE(String TX_DATE) {
        this.TX_DATE = TX_DATE;
    }

    public String getTX_TIME() {
        return TX_TIME;
    }

    public void setTX_TIME(String TX_TIME) {
        this.TX_TIME = TX_TIME;
    }

    public String getCUST_NO() {
        return CUST_NO;
    }

    public void setCUST_NO(String CUST_NO) {
        this.CUST_NO = CUST_NO;
    }

    public String getTX_DESC() {
        return TX_DESC;
    }

    public void setTX_DESC(String TX_DESC) {
        this.TX_DESC = TX_DESC;
    }

    public String getTX_RSN() {
        return TX_RSN;
    }

    public void setTX_RSN(String TX_RSN) {
        this.TX_RSN = TX_RSN;
    }

    public String getMTN_BRH() {
        return MTN_BRH;
    }

    public void setMTN_BRH(String MTN_BRH) {
        this.MTN_BRH = MTN_BRH;
    }

    public String getMTN_EMP() {
        return MTN_EMP;
    }

    public void setMTN_EMP(String MTN_EMP) {
        this.MTN_EMP = MTN_EMP;
    }

    public String getRETURN_COD() {
        return RETURN_COD;
    }

    public void setRETURN_COD(String RETURN_COD) {
        this.RETURN_COD = RETURN_COD;
    }

    public String getRETURN_MSG() {
        return RETURN_MSG;
    }

    public void setRETURN_MSG(String RETURN_MSG) {
        this.RETURN_MSG = RETURN_MSG;
    }
}
