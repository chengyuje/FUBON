package com.systex.jbranch.app.server.fps.sot708;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by SebastianWu on 2016/9/30.
 */
public class CustAssetSIVO {
	private String ID;              //客戶證號
    private String IVRNO;           //收件編號　
    private String SDPRD;           //商品代號
    private String PRDCNM;          //商品名稱
    private String IVCUCY;          //商品幣別
    private BigDecimal IVAMT2;      //庫存金額
    private Date INVEND;            //交易日
    private Date DEPSTR;            //發行日 起息日
    private Date DEPEND;            //到期日
    private Date PLADTE;            //最新配息日
    private BigDecimal INTRATE;     //最新配息率
    private BigDecimal ACMINTRATE;  //累積配息率
    private BigDecimal SDAMT3;      //最新報價
    private BigDecimal INTAMT;      //含息報價
    private BigDecimal INTROR;      //含息報酬率
    private String INTROR_S;      //含息報酬率正負
    private String NMVLU;           //產品風險等級
    private String F01NPD;          //到期保本率
    private BigDecimal MTYEAR;      //到期年限
    private BigDecimal LRDAMT;      //最低贖回金額
    private BigDecimal RDUTAMT;     //贖回單位金額
    private BigDecimal INTFR;       //配息頻率
    private Date SDAMT3DATE;		//最新報價日
    private String IVBRH;           //分行別(收件行)
    private String IVCUAC;          //活存帳號
    private String IVTDAC;          //定存帳號
    private String TYPE;          	//狀態
    

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

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

    public BigDecimal getIVAMT2() {
        return IVAMT2;
    }

    public void setIVAMT2(BigDecimal IVAMT2) {
        this.IVAMT2 = IVAMT2;
    }

    public Date getINVEND() {
        return INVEND;
    }

    public void setINVEND(Date INVEND) {
        this.INVEND = INVEND;
    }

    public Date getDEPSTR() {
        return DEPSTR;
    }

    public void setDEPSTR(Date DEPSTR) {
        this.DEPSTR = DEPSTR;
    }

    public Date getDEPEND() {
        return DEPEND;
    }

    public void setDEPEND(Date DEPEND) {
        this.DEPEND = DEPEND;
    }

    public Date getPLADTE() {
        return PLADTE;
    }

    public void setPLADTE(Date PLADTE) {
        this.PLADTE = PLADTE;
    }

    public BigDecimal getINTRATE() {
        return INTRATE;
    }

    public void setINTRATE(BigDecimal INTRATE) {
        this.INTRATE = INTRATE;
    }

    public BigDecimal getACMINTRATE() {
        return ACMINTRATE;
    }

    public void setACMINTRATE(BigDecimal ACMINTRATE) {
        this.ACMINTRATE = ACMINTRATE;
    }

    public BigDecimal getSDAMT3() {
        return SDAMT3;
    }

    public void setSDAMT3(BigDecimal SDAMT3) {
        this.SDAMT3 = SDAMT3;
    }

    public BigDecimal getINTAMT() {
        return INTAMT;
    }

    public void setINTAMT(BigDecimal INTAMT) {
        this.INTAMT = INTAMT;
    }

    public BigDecimal getINTROR() {
        return INTROR;
    }

    public void setINTROR(BigDecimal INTROR) {
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

    public BigDecimal getMTYEAR() {
        return MTYEAR;
    }

    public void setMTYEAR(BigDecimal MTYEAR) {
        this.MTYEAR = MTYEAR;
    }

    public BigDecimal getLRDAMT() {
        return LRDAMT;
    }

    public void setLRDAMT(BigDecimal LRDAMT) {
        this.LRDAMT = LRDAMT;
    }

    public BigDecimal getRDUTAMT() {
        return RDUTAMT;
    }

    public void setRDUTAMT(BigDecimal RDUTAMT) {
        this.RDUTAMT = RDUTAMT;
    }

    public BigDecimal getINTFR() {
        return INTFR;
    }

    public void setINTFR(BigDecimal INTFR) {
        this.INTFR = INTFR;
    }

    public Date getSDAMT3DATE() {
		return SDAMT3DATE;
	}

	public void setSDAMT3DATE(Date sDAMT3DATE) {
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
        return "CustAssetSIVO{" +
                "ID='" + ID + '\'' +
                ", IVRNO='" + IVRNO + '\'' +
                ", SDPRD='" + SDPRD + '\'' +
                ", PRDCNM='" + PRDCNM + '\'' +
                ", IVCUCY='" + IVCUCY + '\'' +
                ", IVAMT2=" + IVAMT2 +
                ", INVEND=" + INVEND +
                ", DEPSTR=" + DEPSTR +
                ", DEPEND=" + DEPEND +
                ", PLADTE=" + PLADTE +
                ", INTRATE=" + INTRATE +
                ", ACMINTRATE=" + ACMINTRATE +
                ", SDAMT3=" + SDAMT3 +
                ", INTAMT=" + INTAMT +
                ", INTROR=" + INTROR +
                ", NMVLU='" + NMVLU + '\'' +
                ", F01NPD='" + F01NPD + '\'' +
                ", MTYEAR=" + MTYEAR +
                ", LRDAMT=" + LRDAMT +
                ", RDUTAMT=" + RDUTAMT +
                ", INTFR=" + INTFR +
                ", SDAMT3DATE=" + SDAMT3DATE +
                ", INVBRH='" + IVBRH + '\'' +
                ", IVCUAC='" + IVCUAC + '\'' +
                ", IVTDAC='" + IVTDAC + '\'' +
                ", TYPE='" + TYPE + '\'' + 
                '}';
    }

	public String getINTROR_S() {
		return INTROR_S;
	}

	public void setINTROR_S(String iNTROR_S) {
		INTROR_S = iNTROR_S;
	}
}
