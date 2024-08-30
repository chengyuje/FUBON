package com.systex.jbranch.fubon.commons.esb.vo.sdactq5;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.math.BigDecimal;

/**
 * Created by CathyTang on 2016/11/7.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class SDACTQ5InputVO {
	@XmlElement
	private String TYPE;		//贖回類型
    @XmlElement
	private String IVID;        //客戶證號
    @XmlElement
	private String SDPRD;       //產品編號
    @XmlElement
	private String IVBRH;       //推薦分行別
    @XmlElement
    private String IVRNO;		//收件編號
    @XmlElement
	private String IVCUAC;      //活存帳號
    @XmlElement
	private String IVTDAC;      //定存帳號
    @XmlElement
    private String OPTION;		//限價方式
    @XmlElement
    private String SDDTE;		//參考贖回報價日期
    @XmlElement
	private BigDecimal SDAMT3;  //參考贖回報價
    @XmlElement
	private BigDecimal SDSELL;  //委託贖回價格

    
    public String getTYPE() {
		return TYPE;
	}

	public void setTYPE(String tYPE) {
		TYPE = tYPE;
	}

	public String getIVID() {
		return IVID;
	}

	public void setIVID(String iVID) {
		IVID = iVID;
	}

	public String getSDPRD() {
		return SDPRD;
	}

	public void setSDPRD(String sDPRD) {
		SDPRD = sDPRD;
	}

	public String getIVBRH() {
		return IVBRH;
	}

	public void setIVBRH(String iVBRH) {
		IVBRH = iVBRH;
	}

	public String getIVRNO() {
		return IVRNO;
	}

	public void setIVRNO(String iVRNO) {
		IVRNO = iVRNO;
	}

	public String getIVCUAC() {
		return IVCUAC;
	}

	public void setIVCUAC(String iVCUAC) {
		IVCUAC = iVCUAC;
	}

	public String getIVTDAC() {
		return IVTDAC;
	}

	public void setIVTDAC(String iVTDAC) {
		IVTDAC = iVTDAC;
	}

	public String getOPTION() {
		return OPTION;
	}

	public void setOPTION(String oPTION) {
		OPTION = oPTION;
	}

	public String getSDDTE() {
		return SDDTE;
	}

	public void setSDDTE(String sDDTE) {
		SDDTE = sDDTE;
	}

	public BigDecimal getSDAMT3() {
		return SDAMT3;
	}

	public void setSDAMT3(BigDecimal sDAMT3) {
		SDAMT3 = sDAMT3;
	}

	public BigDecimal getSDSELL() {
		return SDSELL;
	}

	public void setSDSELL(BigDecimal sDSELL) {
		SDSELL = sDSELL;
	}

	@Override
    public String toString() {
        return "SDACTQ5InputVO{" +
        		" TYPE='" + TYPE + '\'' +
                ", IVID='" + IVID + '\'' +
                ", SDPRD='" + SDPRD + '\'' +
                ", IVBRH='" + IVBRH + '\'' +
                ", IVRNO='" + IVRNO + '\'' +
                ", IVCUAC='" + IVCUAC + '\'' +
                ", IVTDAC='" + IVTDAC + '\'' +
                ", OPTION='" + OPTION + '\'' +
                ", SDDTE='" + SDDTE + '\'' +
                ", SDAMT3='" + SDAMT3 + '\'' +
                ", SDSELL='" + SDSELL + '\'' +
                '}';
    }
}
