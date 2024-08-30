package com.systex.jbranch.fubon.commons.esb.vo.sdactq3;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.math.BigDecimal;

/**
 * Created by SebastianWu on 2016/10/5.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class SDACTQ3InputVO {
    @XmlElement
	private String IVID;        //客戶證號
    @XmlElement
	private String IVBRH;       //推薦分行別
    @XmlElement
	private String RECBRH;      //錄音分行別
    @XmlElement
	private String SDPRD;       //產品編號
    @XmlElement
	private String IVCUAC;      //活存帳號
    @XmlElement
	private String IVTDAC;      //定存帳號
    @XmlElement
	private BigDecimal IVAMT2;  //簽約金額
    @XmlElement
	private String RECNO;       //錄音序號
    @XmlElement
	private String AGENT;       //解說專員
    @XmlElement
	private String TXBOSS;      //覆核主管
    @XmlElement
	private String TRADERID;    //授權交易人員
    
    public String getIVID() {
        return IVID;
    }

    public void setIVID(String IVID) {
        this.IVID = IVID;
    }

    public String getIVBRH() {
        return IVBRH;
    }

    public void setIVBRH(String IVBRH) {
        this.IVBRH = IVBRH;
    }

    public String getRECBRH() {
        return RECBRH;
    }

    public void setRECBRH(String RECBRH) {
        this.RECBRH = RECBRH;
    }

    public String getSDPRD() {
        return SDPRD;
    }

    public void setSDPRD(String SDPRD) {
        this.SDPRD = SDPRD;
    }

    public String getIVCUAC() {
        return IVCUAC;
    }

    public void setIVCUAC(String IVCUAC) {
        this.IVCUAC = IVCUAC;
    }

    public String getIVTDAC() {
        return IVTDAC;
    }

    public void setIVTDAC(String IVTDAC) {
        this.IVTDAC = IVTDAC;
    }

    public BigDecimal getIVAMT2() {
        return IVAMT2;
    }

    public void setIVAMT2(BigDecimal IVAMT2) {
        this.IVAMT2 = IVAMT2;
    }

    public String getRECNO() {
        return RECNO;
    }

    public void setRECNO(String RECNO) {
        this.RECNO = RECNO;
    }

    public String getAGENT() {
        return AGENT;
    }

    public void setAGENT(String AGENT) {
        this.AGENT = AGENT;
    }

    public String getTXBOSS() {
        return TXBOSS;
    }

    public void setTXBOSS(String TXBOSS) {
        this.TXBOSS = TXBOSS;
    }
    
    
    public String getTRADERID() {
		return TRADERID;
	}

	public void setTRADERID(String tRADERID) {
		TRADERID = tRADERID;
	}

	@Override
    public String toString() {
        return "SDACTQ3InputVO{" +
                "IVID='" + IVID + '\'' +
                ", IVBRH='" + IVBRH + '\'' +
                ", RECBRH='" + RECBRH + '\'' +
                ", SDPRD='" + SDPRD + '\'' +
                ", IVCUAC='" + IVCUAC + '\'' +
                ", IVTDAC='" + IVTDAC + '\'' +
                ", IVAMT2='" + IVAMT2 + '\'' +
                ", RECNO='" + RECNO + '\'' +
                ", AGENT='" + AGENT + '\'' +
                ", TXBOSS='" + TXBOSS + '\'' +
                '}';
    }
}
