package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPRD_INS_MAINVO extends VOBase {

    /** identifier field */
    private BigDecimal INSPRD_KEYNO;

    /** persistent field */
    private String INSPRD_TYPE;

    /** persistent field */
    private String INSPRD_CLASS;

    /** nullable persistent field */
    private String SPECIAL_CONDITION;

    /** persistent field */
    private String INSPRD_ID;

    /** persistent field */
    private String INSPRD_NAME;

    /** nullable persistent field */
    private String INSPRD_ANNUAL;

    /** persistent field */
    private String MAIN_RIDER;

    /** persistent field */
    private String PAY_TYPE;

    /** persistent field */
    private String CURR_CD;

    /** nullable persistent field */
    private String FEE_STATE;

    /** nullable persistent field */
    private BigDecimal PRD_RATE;

    /** nullable persistent field */
    private BigDecimal CNR_RATE;

    /** nullable persistent field */
    private BigDecimal COEFFICIENT;

    /** persistent field */
    private String NEED_MATCH;

    /** nullable persistent field */
    private String PRD_RISK;

    /** nullable persistent field */
    private String CERT_TYPE;

    /** nullable persistent field */
    private String TRAINING_TYPE;

    /** nullable persistent field */
    private BigDecimal EXCH_RATE;

    /** nullable persistent field */
    private BigDecimal AB_EXCH_RATE;

    /** persistent field */
    private Timestamp EFFECT_DATE;

    /** nullable persistent field */
    private Timestamp EXPIRY_DATE;

    /** nullable persistent field */
    private String APPROVER;

    /** nullable persistent field */
    private Timestamp APP_DATE;

    /** nullable persistent field */
    private BigDecimal COMPANY_NUM;
    
    /** nullable persistent field */
    private String DIVIDEND_YN;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPRD_INS_MAIN";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPRD_INS_MAINVO(BigDecimal INSPRD_KEYNO, String INSPRD_TYPE, String INSPRD_CLASS, String SPECIAL_CONDITION, String INSPRD_ID, String INSPRD_NAME, String INSPRD_ANNUAL, String MAIN_RIDER, String PAY_TYPE, String CURR_CD, String FEE_STATE, BigDecimal PRD_RATE, BigDecimal CNR_RATE, BigDecimal COEFFICIENT, String NEED_MATCH, String PRD_RISK, String CERT_TYPE, String TRAINING_TYPE, BigDecimal EXCH_RATE, BigDecimal AB_EXCH_RATE, Timestamp EFFECT_DATE, Timestamp EXPIRY_DATE, String APPROVER, Timestamp APP_DATE, Timestamp createtime, String creator, Timestamp lastupdate, String modifier, BigDecimal COMPANY_NUM, Long version, String DIVIDEND_YN) {
        this.INSPRD_KEYNO = INSPRD_KEYNO;
        this.INSPRD_TYPE = INSPRD_TYPE;
        this.INSPRD_CLASS = INSPRD_CLASS;
        this.SPECIAL_CONDITION = SPECIAL_CONDITION;
        this.INSPRD_ID = INSPRD_ID;
        this.INSPRD_NAME = INSPRD_NAME;
        this.INSPRD_ANNUAL = INSPRD_ANNUAL;
        this.MAIN_RIDER = MAIN_RIDER;
        this.PAY_TYPE = PAY_TYPE;
        this.CURR_CD = CURR_CD;
        this.FEE_STATE = FEE_STATE;
        this.PRD_RATE = PRD_RATE;
        this.CNR_RATE = CNR_RATE;
        this.COEFFICIENT = COEFFICIENT;
        this.NEED_MATCH = NEED_MATCH;
        this.PRD_RISK = PRD_RISK;
        this.CERT_TYPE = CERT_TYPE;
        this.TRAINING_TYPE = TRAINING_TYPE;
        this.EXCH_RATE = EXCH_RATE;
        this.AB_EXCH_RATE = AB_EXCH_RATE;
        this.EFFECT_DATE = EFFECT_DATE;
        this.EXPIRY_DATE = EXPIRY_DATE;
        this.APPROVER = APPROVER;
        this.APP_DATE = APP_DATE;
        this.createtime = createtime;
        this.creator = creator;
        this.lastupdate = lastupdate;
        this.modifier = modifier;
        this.COMPANY_NUM = COMPANY_NUM;
        this.DIVIDEND_YN = DIVIDEND_YN;
        this.version = version;
    }

    /** default constructor */
    public TBPRD_INS_MAINVO() {
    }

    /** minimal constructor */
    public TBPRD_INS_MAINVO(BigDecimal INSPRD_KEYNO, String INSPRD_TYPE, String INSPRD_CLASS, String INSPRD_ID, String INSPRD_NAME, String MAIN_RIDER, String PAY_TYPE, String CURR_CD, String NEED_MATCH, String PRD_RISK, Timestamp EFFECT_DATE, Timestamp createtime, String creator) {
        this.INSPRD_KEYNO = INSPRD_KEYNO;
        this.INSPRD_TYPE = INSPRD_TYPE;
        this.INSPRD_CLASS = INSPRD_CLASS;
        this.INSPRD_ID = INSPRD_ID;
        this.INSPRD_NAME = INSPRD_NAME;
        this.MAIN_RIDER = MAIN_RIDER;
        this.PAY_TYPE = PAY_TYPE;
        this.CURR_CD = CURR_CD;
        this.NEED_MATCH = NEED_MATCH;
        this.PRD_RISK = PRD_RISK;
        this.EFFECT_DATE = EFFECT_DATE;
        this.createtime = createtime;
        this.creator = creator;
    }

    public BigDecimal getINSPRD_KEYNO() {
        return this.INSPRD_KEYNO;
    }

    public void setINSPRD_KEYNO(BigDecimal INSPRD_KEYNO) {
        this.INSPRD_KEYNO = INSPRD_KEYNO;
    }

    public String getINSPRD_TYPE() {
        return this.INSPRD_TYPE;
    }

    public void setINSPRD_TYPE(String INSPRD_TYPE) {
        this.INSPRD_TYPE = INSPRD_TYPE;
    }

    public String getINSPRD_CLASS() {
        return this.INSPRD_CLASS;
    }

    public void setINSPRD_CLASS(String INSPRD_CLASS) {
        this.INSPRD_CLASS = INSPRD_CLASS;
    }

    public String getSPECIAL_CONDITION() {
        return this.SPECIAL_CONDITION;
    }

    public void setSPECIAL_CONDITION(String SPECIAL_CONDITION) {
        this.SPECIAL_CONDITION = SPECIAL_CONDITION;
    }

    public String getINSPRD_ID() {
        return this.INSPRD_ID;
    }

    public void setINSPRD_ID(String INSPRD_ID) {
        this.INSPRD_ID = INSPRD_ID;
    }

    public String getINSPRD_NAME() {
        return this.INSPRD_NAME;
    }

    public void setINSPRD_NAME(String INSPRD_NAME) {
        this.INSPRD_NAME = INSPRD_NAME;
    }

    public String getINSPRD_ANNUAL() {
        return this.INSPRD_ANNUAL;
    }

    public void setINSPRD_ANNUAL(String INSPRD_ANNUAL) {
        this.INSPRD_ANNUAL = INSPRD_ANNUAL;
    }

    public String getMAIN_RIDER() {
        return this.MAIN_RIDER;
    }

    public void setMAIN_RIDER(String MAIN_RIDER) {
        this.MAIN_RIDER = MAIN_RIDER;
    }

    public String getPAY_TYPE() {
        return this.PAY_TYPE;
    }

    public void setPAY_TYPE(String PAY_TYPE) {
        this.PAY_TYPE = PAY_TYPE;
    }

    public String getCURR_CD() {
        return this.CURR_CD;
    }

    public void setCURR_CD(String CURR_CD) {
        this.CURR_CD = CURR_CD;
    }

    public String getFEE_STATE() {
        return this.FEE_STATE;
    }

    public void setFEE_STATE(String FEE_STATE) {
        this.FEE_STATE = FEE_STATE;
    }

    public BigDecimal getPRD_RATE() {
        return this.PRD_RATE;
    }

    public void setPRD_RATE(BigDecimal PRD_RATE) {
        this.PRD_RATE = PRD_RATE;
    }

    public BigDecimal getCNR_RATE() {
        return this.CNR_RATE;
    }

    public void setCNR_RATE(BigDecimal CNR_RATE) {
        this.CNR_RATE = CNR_RATE;
    }

    public BigDecimal getCOEFFICIENT() {
        return this.COEFFICIENT;
    }

    public void setCOEFFICIENT(BigDecimal COEFFICIENT) {
        this.COEFFICIENT = COEFFICIENT;
    }

    public String getNEED_MATCH() {
        return this.NEED_MATCH;
    }

    public void setNEED_MATCH(String NEED_MATCH) {
        this.NEED_MATCH = NEED_MATCH;
    }

    public String getPRD_RISK() {
		return PRD_RISK;
	}

	public void setPRD_RISK(String pRD_RISK) {
		PRD_RISK = pRD_RISK;
	}

	public String getCERT_TYPE() {
        return this.CERT_TYPE;
    }

    public void setCERT_TYPE(String CERT_TYPE) {
        this.CERT_TYPE = CERT_TYPE;
    }

    public String getTRAINING_TYPE() {
        return this.TRAINING_TYPE;
    }

    public void setTRAINING_TYPE(String TRAINING_TYPE) {
        this.TRAINING_TYPE = TRAINING_TYPE;
    }

    public BigDecimal getEXCH_RATE() {
        return this.EXCH_RATE;
    }

    public void setEXCH_RATE(BigDecimal EXCH_RATE) {
        this.EXCH_RATE = EXCH_RATE;
    }

    public BigDecimal getAB_EXCH_RATE() {
        return this.AB_EXCH_RATE;
    }

    public void setAB_EXCH_RATE(BigDecimal AB_EXCH_RATE) {
        this.AB_EXCH_RATE = AB_EXCH_RATE;
    }

    public Timestamp getEFFECT_DATE() {
        return this.EFFECT_DATE;
    }

    public void setEFFECT_DATE(Timestamp EFFECT_DATE) {
        this.EFFECT_DATE = EFFECT_DATE;
    }

    public Timestamp getEXPIRY_DATE() {
        return this.EXPIRY_DATE;
    }

    public void setEXPIRY_DATE(Timestamp EXPIRY_DATE) {
        this.EXPIRY_DATE = EXPIRY_DATE;
    }

    public String getAPPROVER() {
        return this.APPROVER;
    }

    public void setAPPROVER(String APPROVER) {
        this.APPROVER = APPROVER;
    }

    public Timestamp getAPP_DATE() {
        return this.APP_DATE;
    }

    public void setAPP_DATE(Timestamp APP_DATE) {
        this.APP_DATE = APP_DATE;
    }

    public BigDecimal getCOMPANY_NUM() {
        return this.COMPANY_NUM;
    }

    public void setCOMPANY_NUM(BigDecimal COMPANY_NUM) {
        this.COMPANY_NUM = COMPANY_NUM;
    }

    public String getDIVIDEND_YN() {
		return DIVIDEND_YN;
	}

	public void setDIVIDEND_YN(String dIVIDEND_YN) {
		DIVIDEND_YN = dIVIDEND_YN;
	}

	public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("INSPRD_KEYNO", getINSPRD_KEYNO())
            .toString();
    }

}
