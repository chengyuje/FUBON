package com.systex.jbranch.fubon.commons.esb.vo.sdactq4;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by SebastianWu on 2016/10/5.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class SDACTQ4OutputDetailsVO {
    @XmlElement
    private String IVRNO;           //收件編號　
    @XmlElement
    private String SDPRD;           //商品代號
    @XmlElement
    private String PRDCNM;          //商品名稱
    @XmlElement
    private String IVCUCY;          //商品幣別
    @XmlElement
    private String IVAMT2;      	//庫存金額
    @XmlElement
    private String INVEND;          //交易日
    @XmlElement
    private String DEPSTR;          //發行日/起息日
    @XmlElement
    private String DEPEND;          //到期日
    @XmlElement
    private String PLADTE;          //最新配息日
    @XmlElement
    private String INTRATE;     	//最新配息率
    @XmlElement
    private String ACMINTRATE;  	//累積配息率
    @XmlElement
    private String SDAMT3;      	//最新報價
    @XmlElement
    private String INTAMT;      	//含息報價
    @XmlElement
    private String INTROR;      	//含息報酬率
    @XmlElement
    private String INTROR_S;      	//含息報酬率正負
    @XmlElement
    private String NMVLU;           //產品風險等級
    @XmlElement
    private String F01NPD;          //到期保本率
    @XmlElement
    private String MTYEAR;      	//到期年限
    @XmlElement
    private String LRDAMT;      	//最低贖回金額
    @XmlElement
    private String RDUTAMT;    	//贖回單位金額
    @XmlElement
    private String INTFR;      	//配息頻率
    @XmlElement
    private String SDAMT3DATE;		//最新報價日
    @XmlElement
    private String IVBRH;           //分行別(收件行)
    @XmlElement
    private String IVCUAC;          //扣款帳號
    @XmlElement
    private String IVTDAC;          //組合式商品帳號
    @XmlElement
    private String TYPE;          	//狀態
    
    public String getIVRNO() {
        return IVRNO;
    }

    public void setIVRNO(String IVRNO) {
        this.IVRNO = IVRNO;
    }

    public String getSDPRD() {
        return SDPRD;
    }

    public void setSDPRD(String SDPRD) {
        this.SDPRD = SDPRD;
    }

    public String getPRDCNM() {
        return PRDCNM;
    }

    public void setPRDCNM(String PRDCNM) {
        this.PRDCNM = PRDCNM;
    }

    public String getIVCUCY() {
        return IVCUCY;
    }

    public void setIVCUCY(String IVCUCY) {
        this.IVCUCY = IVCUCY;
    }

    public String getIVAMT2() {
        return IVAMT2;
    }

    public void setIVAMT2(String IVAMT2) {
        this.IVAMT2 = IVAMT2;
    }

    public String getINVEND() {
        return INVEND;
    }

    public void setINVEND(String INVEND) {
        this.INVEND = INVEND;
    }

    public String getDEPSTR() {
        return DEPSTR;
    }

    public void setDEPSTR(String DEPSTR) {
        this.DEPSTR = DEPSTR;
    }

    public String getDEPEND() {
        return DEPEND;
    }

    public void setDEPEND(String DEPEND) {
        this.DEPEND = DEPEND;
    }

    public String getPLADTE() {
        return PLADTE;
    }

    public void setPLADTE(String PLADTE) {
        this.PLADTE = PLADTE;
    }

    public String getINTRATE() {
        return INTRATE;
    }

    public void setINTRATE(String INTRATE) {
        this.INTRATE = INTRATE;
    }

    public String getACMINTRATE() {
        return ACMINTRATE;
    }

    public void setACMINTRATE(String ACMINTRATE) {
        this.ACMINTRATE = ACMINTRATE;
    }

    public String getSDAMT3() {
        return SDAMT3;
    }

    public void setSDAMT3(String SDAMT3) {
        this.SDAMT3 = SDAMT3;
    }

    public String getINTAMT() {
        return INTAMT;
    }

    public void setINTAMT(String INTAMT) {
        this.INTAMT = INTAMT;
    }

    public String getINTROR() {
        return INTROR;
    }

    public void setINTROR_S(String INTROR_S) {
        this.INTROR_S = INTROR_S;
    }

    public String getINTROR_S() {
        return INTROR_S;
    }

    public void setINTROR(String INTROR) {
        this.INTROR = INTROR;
    }
    
    public String getNMVLU() {
        return NMVLU;
    }

    public void setNMVLU(String NMVLU) {
        this.NMVLU = NMVLU;
    }

    public String getF01NPD() {
        return F01NPD;
    }

    public void setF01NPD(String f01NPD) {
        F01NPD = f01NPD;
    }

    public String getMTYEAR() {
        return MTYEAR;
    }

    public void setMTYEAR(String MTYEAR) {
        this.MTYEAR = MTYEAR;
    }

    public String getLRDAMT() {
        return LRDAMT;
    }

    public void setLRDAMT(String LRDAMT) {
        this.LRDAMT = LRDAMT;
    }

    public String getRDUTAMT() {
        return RDUTAMT;
    }

    public void setRDUTAMT(String RDUTAMT) {
        this.RDUTAMT = RDUTAMT;
    }

    public String getINTFR() {
        return INTFR;
    }

    public void setINTFR(String INTFR) {
        this.INTFR = INTFR;
    }

    public String getSDAMT3DATE() {
		return SDAMT3DATE;
	}

	public void setSDAMT3DATE(String sDAMT3DATE) {
		SDAMT3DATE = sDAMT3DATE;
	}
	
	public String getIVBRH(){
	    return IVBRH = IVBRH;
	}
	
	public void setIVBRH(String iVBRH) {
		this.IVBRH = iVBRH;
	}
	
	public String getIVCUAC(){
		return IVCUAC = IVCUAC;
	}
	
	public void setIVCUAC(String iVCUAC){
		this.IVCUAC = iVCUAC;
	}
	
	public String getIVTDAC(){
		return IVTDAC = IVTDAC;
	}
	
	public void setIVTDAC(String iVTDAC){
		this.IVTDAC = iVTDAC;
	}	

	public String getTYPE() {
		return TYPE;
	}

	public void setTYPE(String tYPE) {
		TYPE = tYPE;
	}

	@Override
    public String toString() {
        return "SDACTQ4OutputVODetails{" +
                "IVRNO='" + IVRNO + '\'' +
                ", SDPRD='" + SDPRD + '\'' +
                ", PRDCNM='" + PRDCNM + '\'' +
                ", IVCUCY='" + IVCUCY + '\'' +
                ", IVAMT2='" + IVAMT2 + '\'' +
                ", INVEND='" + INVEND + '\'' +
                ", DEPSTR='" + DEPSTR + '\'' +
                ", DEPEND='" + DEPEND + '\'' +
                ", PLADTE='" + PLADTE + '\'' +
                ", INTRATE='" + INTRATE + '\'' +
                ", ACMINTRATE='" + ACMINTRATE + '\'' +
                ", SDAMT3='" + SDAMT3 + '\'' +
                ", INTAMT='" + INTAMT + '\'' +
                ", INTROR='" + INTROR + '\'' +
                ", INTROR_S='" + INTROR_S + '\'' +
                ", NMVLU='" + NMVLU + '\'' +
                ", F01NPD='" + F01NPD + '\'' +
                ", MTYEAR='" + MTYEAR + '\'' +
                ", LRDAMT='" + LRDAMT + '\'' +
                ", RDUTAMT='" + RDUTAMT + '\'' +
                ", INTFR='" + INTFR + '\'' +
                ", SDAMT3DATE='" + SDAMT3DATE + '\'' +
                ", INVBRH='" + IVBRH + '\'' +
                ", IVCUAC='" + IVCUAC + '\'' +
                ", IVTDAC='" + IVTDAC + '\'' +
                ", TYPE='" + TYPE + '\'' +
                '}';
    }
}
