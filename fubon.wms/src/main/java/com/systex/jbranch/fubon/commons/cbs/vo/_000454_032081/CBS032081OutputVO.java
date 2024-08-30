package com.systex.jbranch.fubon.commons.cbs.vo._000454_032081;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class CBS032081OutputVO {
    @XmlElement(name = "TxRepeat")
    private List<CBS032081OutputDetailsVO> details;

    private String ACNO;
    private String SUBACCT_INFO;
    private String CURRENCY;
    private String SERIAL_NO;
    private String FROM_DATE;
    private String START_TIME;
    private String ND_DATE;
    private String END_TIME;
    private String DATE_INDICATOR;
    private String SORTING_METHOD;
    private String TRNAS_TYPE;
    private String OUTPUT_TYPE;
    private String REC_CNT;
    private String INCT_SOC;
    private String INCT_ACC;
    private String INCT_RECNO;
    private String INCT_DATE;
    private String INCT_TIME;
    private String SUBCNT;
    private String REASON;
    private String HOLD_FLAG;
    private String REV;
    private String TOT_CNT;
    private String FLD_LENGTH;

    public CBS032081OutputVO() {
        this.ACNO = "";
        this.SUBACCT_INFO = "";
        this.CURRENCY = "";
        this.SERIAL_NO = "";
        this.FROM_DATE = "";
        this.START_TIME = "";
        this.ND_DATE = "";
        this.END_TIME = "";
        this.DATE_INDICATOR = "";
        this.SORTING_METHOD = "";
        this.TRNAS_TYPE = "";
        this.OUTPUT_TYPE = "";
        this.REC_CNT = "";
        this.INCT_SOC = "";
        this.INCT_ACC = "";
        this.INCT_RECNO = "";
        this.INCT_DATE = "";
        this.INCT_TIME = "";
        this.SUBCNT = "";
        this.REASON = "";
        this.HOLD_FLAG = "";
        this.REV = "";
        this.TOT_CNT = "";
        this.FLD_LENGTH = "";

    }

    public List<CBS032081OutputDetailsVO> getDetails() {
        return details;
    }

    public void setDetails(List<CBS032081OutputDetailsVO> details) {
        this.details = details;
    }

    public String getACNO() {
        return ACNO;
    }

    public void setACNO(String aCNO) {
        ACNO = aCNO;
    }

    public String getSUBACCT_INFO() {
        return SUBACCT_INFO;
    }

    public void setSUBACCT_INFO(String sUBACCT_INFO) {
        SUBACCT_INFO = sUBACCT_INFO;
    }

    public String getCURRENCY() {
        return CURRENCY;
    }

    public void setCURRENCY(String cURRENCY) {
        CURRENCY = cURRENCY;
    }

    public String getSERIAL_NO() {
        return SERIAL_NO;
    }

    public void setSERIAL_NO(String sERIAL_NO) {
        SERIAL_NO = sERIAL_NO;
    }

    public String getFROM_DATE() {
        return FROM_DATE;
    }

    public void setFROM_DATE(String fROM_DATE) {
        FROM_DATE = fROM_DATE;
    }

    public String getSTART_TIME() {
        return START_TIME;
    }

    public void setSTART_TIME(String sTART_TIME) {
        START_TIME = sTART_TIME;
    }

    public String getND_DATE() {
        return ND_DATE;
    }

    public void setND_DATE(String nD_DATE) {
        ND_DATE = nD_DATE;
    }

    public String getEND_TIME() {
        return END_TIME;
    }

    public void setEND_TIME(String eND_TIME) {
        END_TIME = eND_TIME;
    }

    public String getDATE_INDICATOR() {
        return DATE_INDICATOR;
    }

    public void setDATE_INDICATOR(String dATE_INDICATOR) {
        DATE_INDICATOR = dATE_INDICATOR;
    }

    public String getSORTING_METHOD() {
        return SORTING_METHOD;
    }

    public void setSORTING_METHOD(String sORTING_METHOD) {
        SORTING_METHOD = sORTING_METHOD;
    }

    public String getTRNAS_TYPE() {
        return TRNAS_TYPE;
    }

    public void setTRNAS_TYPE(String tRNAS_TYPE) {
        TRNAS_TYPE = tRNAS_TYPE;
    }

    public String getOUTPUT_TYPE() {
        return OUTPUT_TYPE;
    }

    public void setOUTPUT_TYPE(String oUTPUT_TYPE) {
        OUTPUT_TYPE = oUTPUT_TYPE;
    }

    public String getREC_CNT() {
        return REC_CNT;
    }

    public void setREC_CNT(String rEC_CNT) {
        REC_CNT = rEC_CNT;
    }

    public String getINCT_SOC() {
        return INCT_SOC;
    }

    public void setINCT_SOC(String iNCT_SOC) {
        INCT_SOC = iNCT_SOC;
    }

    public String getINCT_ACC() {
        return INCT_ACC;
    }

    public void setINCT_ACC(String iNCT_ACC) {
        INCT_ACC = iNCT_ACC;
    }

    public String getINCT_RECNO() {
        return INCT_RECNO;
    }

    public void setINCT_RECNO(String iNCT_RECNO) {
        INCT_RECNO = iNCT_RECNO;
    }

    public String getINCT_DATE() {
        return INCT_DATE;
    }

    public void setINCT_DATE(String iNCT_DATE) {
        INCT_DATE = iNCT_DATE;
    }

    public String getINCT_TIME() {
        return INCT_TIME;
    }

    public void setINCT_TIME(String iNCT_TIME) {
        INCT_TIME = iNCT_TIME;
    }

    public String getSUBCNT() {
        return SUBCNT;
    }

    public void setSUBCNT(String sUBCNT) {
        SUBCNT = sUBCNT;
    }

    public String getREASON() {
        return REASON;
    }

    public void setREASON(String rEASON) {
        REASON = rEASON;
    }

    public String getHOLD_FLAG() {
        return HOLD_FLAG;
    }

    public void setHOLD_FLAG(String hOLD_FLAG) {
        HOLD_FLAG = hOLD_FLAG;
    }

    public String getREV() {
        return REV;
    }

    public void setREV(String rEV) {
        REV = rEV;
    }

    public String getTOT_CNT() {
        return TOT_CNT;
    }

    public void setTOT_CNT(String tOT_CNT) {
        TOT_CNT = tOT_CNT;
    }

    public String getFLD_LENGTH() {
        return FLD_LENGTH;
    }

    public void setFLD_LENGTH(String fLD_LENGTH) {
        FLD_LENGTH = fLD_LENGTH;
    }

}
